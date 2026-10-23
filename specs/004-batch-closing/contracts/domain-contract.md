# Domain Contract: Consulta de Cierre de Lote

**Date**: 2026-10-23 | **Scope**: API interna de dominio, sin HTTP ni integración externa.

## Repositorio de solo lectura

`CierreConsultaRepository.readCompleted(periodo: CierrePeriodo): ConsultaSnapshotsResult` es suspendido. Devuelve Success(List<PedidoConsultaSnapshot>), InvalidData(código) o ReadFailure. CancellationException no se transforma en fallo. No ofrece métodos de escritura. La implementación garantiza una instantánea de pedidos COMPLETADO por fecha de creación y sus líneas, sin PII.

Filtro canónico:

```sql
WHERE p.estado = 'COMPLETADO'
  AND p.fecha_creacion >= :inicio
  AND p.fecha_creacion < :finExclusivo
```

La lectura parte de pedidos LEFT JOIN pedido_detalle; nunca filtra por fecha_actualizacion o fecha_hora de transiciones. No excluye operaciones por cierres porque no existe consolidación en esta entrega. No se hace join al catálogo. El SQL proyecta aliases inequívocos para IDs y omite cliente/notas.

## Casos de uso

- `ValidateCierrePeriodoUseCase`: valida extremos, zona e inicio<fin<=nowMillis. Códigos MissingBoundary, InvalidZone, StartNotBeforeEnd, FutureEnd; no acceso a almacenamiento.
- `CalculateConsultaCierreUseCase`: función pura que recibe período y snapshots, valida integridad y devuelve Success(ConsultaCierre) o InvalidData. Money exacto y cantidades Long comprobadas; devuelve resumen/Top5 juntos incluso en vacío.
- `GetConsultaCierreUseCase`: valida con reloj inyectado, invoca repositorio una vez y calcula fuera de main mediante dispatcher inyectado. Devuelve ConsultaCierreResult, propaga cancelación y no lanza excepciones de datos esperables al consumidor. Valida fin contra reloj cada vez, no solo al editar.

Errores de datos: MissingLines, InvalidIdentity, DuplicateLine, DuplicatePosition, InvalidQuantity, InvalidAmount, LineSubtotalMismatch, HeaderTotalMismatch, Overflow. No devuelve éxito parcial ni omite registros incorrectos para obtener un total aparente.

## Adaptador temporal

Interfaz `CierreDateTimeResolver` de dominio:

- `zoneId(): String` devuelve zona vigente del dispositivo, que actúa como zona del negocio.
- `toLocal(epochMillis, zoneId)` devuelve componentes civiles/offset sin modificar instante.
- `resolve(localParts, zoneId)` devuelve Unique(candidate), Ambiguous(candidates), InvalidDate o NonexistentTime.

Implementación en data/time con Calendar/TimeZone, strict validation y comprobación de ida/vuelta por candidato; probar sin Android cuando sea posible. La UI pide elegir offset para Ambiguous; no aplica un offset silencioso. BusinessDayProvider mantiene reloj y medianoche inicial. Fecha elegida con Material DatePicker se extrae como componentes de fecha, no como instante de medianoche local.

## Coherencia y actualización

Cada invocación produce una lectura única, sin caché persistida. VM la solicita al entrar/aplicar edición válida/reintentar/reanudar. No se exige streaming de cambios mientras se mantiene la pantalla sin interacción. Publicación de ambos indicadores es indivisible. Generación de VM descarta cualquier respuesta obsoleta; cancelación cooperativa es necesaria pero no suficiente.

## Ejemplo contractual de aceptación

Período [1000,2000), now=3000:

| Pedido | Creado | Estado actual | Bruto | Líneas | Resultado |
| --- | --- | --- | --- | --- | --- |
| 1 | 1000 | COMPLETADO | 300 | Producto7: cantidad2 ×100; Producto8: cantidad1 ×100 | Incluido |
| 2 | 1500 | COMPLETADO | 200 | Producto7: cantidad2 ×100 | Incluido |
| 3 | 2000 | COMPLETADO | 100 | Producto8: cantidad1 ×100 | Excluido, fin |
| 4 | 1200 | PENDIENTE | 100 | Producto8: cantidad1 ×100 | Excluido, estado |
| 5 | 900 | COMPLETADO | 100 | Producto8: cantidad1 ×100 | Excluido aunque complete dentro |

Importes en centavos: resumen2 pedidos, bruto500; ranking Producto7 cantidad4 bruto400, Producto8 cantidad1 bruto100. Ningún valor acredita cobro. Completar pedido4 y recalcular incorpora ese pedido al mismo período. Verificar integridad y ausencia de escrituras antes/después.

## Privacidad y mantenimiento

KDoc de contratos públicos describe precondiciones/resultados/fallos. Logs con códigos técnicos y solicitud local, sin objetos de pedido completos, clientes, nombres, notas ni valores bancarios. No exponer SQL/Throwable al usuario. No añadir permisos ni procesos de red.

# Research: Consulta preliminar de Cierre de Lote

**Date**: 2026-10-23 | **Spec**: [spec.md](spec.md)

Investigación basada en el código del repositorio y en las dos respuestas de aclaración. No requiere seleccionar ni actualizar servicios o bibliotecas externas.

## 1. Alcance e integración

- **Decision**: Crear `:feature:batch-closing` para consulta, usando el paquete `feature.batchclosing`. Estado actual COMPLETADO y fecha de creación son la única base de selección. No implementar Pagos, notas, cierre ni historial.
- **Rationale**: Las respuestas C/C sustituyen el alcance financiero del SRS. Los datos existentes bastan para consultas históricas brutas.
- **Alternatives considered**: Implementar el cierre completo contradice la aclaración; mostrar cobros cero inventa información; depender de feature/orders rompe límites de módulos.
- **Evidence**: `domain/.../model/Pedido.kt`, `data/.../Entities/Pedido.kt`, `spec.md` Clarifications y FR-004/005.

## 2. Una lectura consistente y mínima

- **Decision**: Añadir a PedidoDao una consulta suspendida de proyección plana con LEFT JOIN pedidos/detalles, filtrada por `p.estado='COMPLETADO' AND p.fecha_creacion >= :inicio AND p.fecha_creacion < :fin`. Seleccionar solo identidad, fecha, total y líneas históricas, sin cliente/observaciones. Adaptar a snapshots mínimos de dominio mediante un repositorio de solo lectura específico de consulta. Una sola sentencia entrega el conjunto de cabeceras/líneas del que se derivan resumen y Top 5.
- **Rationale**: Evita combinar lecturas en diferentes versiones y cargar clientes o catálogo. LEFT JOIN permite detectar pedidos corruptos sin líneas en lugar de omitirlos silenciosamente. Índices existentes `(estado, fecha_creacion)` y detalle `pedido_id` cubren el acceso. No cambiar entidades, versión 4, índices ni migraciones.
- **Alternatives considered**: Una consulta por pedido produce N+1; combinar flows independientes de resumen/productos puede mezclar versiones; JOIN interno oculta cabeceras sin líneas; SUM SQL dificulta tratar overflow y reglas de consistencia en dominio. No reutilizar observeSummary, que incluye otros estados y solo cuenta pedidos.
- **Evidence**: `data/.../DAO/PedidoDao.kt`, `data/.../DB/AppDatabase.kt`, `data/.../Entities/PedidoDetalle.kt`.

## 3. Agregación exacta y nombres históricos

- **Decision**: Contar cabeceras únicas, sumar Money de cada pedido una vez y validar contra suma de líneas. Agrupar líneas por productoId; acumular cantidades en Long con suma comprobada y dinero mediante Money. Ordenar cantidad DESC, importe DESC, productoId ASC y tomar cinco. Nombre del registro con mayor tupla `(fechaCreacion, pedidoId, posicion)`; ante datos duplicados/posiciones repetidas devolver inconsistencia.
- **Rationale**: La cabecera repetida por el JOIN no multiplica el bruto. Money existente rechaza negativos y overflow. No hay descuentos, por lo que header.total es el bruto histórico y debe coincidir con líneas. Producto borrado no afecta detalles, que no tienen FK al catálogo.
- **Alternatives considered**: Double pierde exactitud; cantidad Int agregada puede desbordarse; nombre actual del catálogo rompe historia; sumar solo Top 5 no representa el total.
- **Evidence**: `domain/.../model/Money.kt`, `data/.../Entities/PedidoDetalle.kt`, FR-008/011.

## 4. Fechas, horas y API 25

- **Decision**: Mantener epoch millis, Calendar/TimeZone y BusinessDayProvider existente como reloj/jornada. Añadir interfaz pura de resolución de fecha-hora local con segundos y zona, implementada en data/time. Inicio inclusivo, fin exclusivo y fin <= reloj al validar. DatePicker/TimePicker más campo de segundos 00–59; cambios conservan segundos no editados. Rechazar fechas u horas inexistentes; en hora repetida ofrecer los dos offsets para elegir un instante inequívoco, mostrando zona/offset efectivo.
- **Rationale**: BusinessDayProvider.range convierte fechas completas inclusivas y no sirve directamente para extremos con hora. minSdk25 no tiene java.time nativo sin desugaring, que no se añade. No sumar 24 horas para obtener medianoche siguiente. DatePicker entrega fecha civil en su convención UTC: extraer sus componentes y resolverlos en la zona del negocio, no tratar su valor como medianoche local.
- **Alternatives considered**: truncar a minutos pierde precisión; usar fecha de finalización contradice respuesta C; reutilizar range cambia el fin; normalización lenient de Calendar oculta horas inexistentes; elegir silenciosamente una hora ambigua dificulta revisar fronteras.
- **Evidence**: `domain/.../time/BusinessDayProvider.kt`, `data/.../time/LocalBusinessDayProvider.kt`, FR-003/004.

## 5. Actualización, cancelación y coherencia

- **Decision**: Consulta suspendida al entrar, aplicar extremo válido, reintentar y reanudar. SavedStateHandle conserva extremos/offsets/zona resueltos, sin resultados sensibles. Cada solicitud tiene generación; cancelar la anterior y descartar resultados tardíos, incluso si el datasource demora en cancelar. Publicar resumen y productos juntos. Al editar un extremo invalidar el resultado anterior. Si cambia zona, mantener instantes seleccionados y volver a mostrar fecha/hora en nueva zona; al retroceder reloj validar otra vez fin no futuro.
- **Rationale**: FR-014 exige recalcular/reabrir, no observar continuamente todos los cambios. La lectura única es una fotografía temporal de consulta, sin persistencia. Evita listeners adicionales a tablas o escrituras auxiliares.
- **Alternatives considered**: streaming continuo añade coordinación sin requisito; conservar datos obsoletos como vigentes confunde períodos; cache persistida implica mantenimiento/escrituras innecesarias.

## 6. Navegación y presentación

- **Decision**: Añadir MainDestination.BatchClosing y codificación `close`, habilitar nav_close ya existente y tarjeta home_close_button. Home recibe onBatchClosingClick y cambia subtítulo «Registra el cierre del día» a «Consulta preliminar de pedidos». Mostrar banner, dos indicadores disponibles y descuentos/neto/métodos No disponible con causa. Sin botón Cerrar lote, notas o acceso a historial.
- **Rationale**: La navegación actual ya reserva `close` pero lo deshabilita y su decodificación cae en Home. Hay que actualizar codificador/selección/host y pruebas. Mantener navegación tipada guardable, sin nueva biblioteca.
- **Alternatives considered**: habilitar solo tarjeta deja acceso inferior inconsistente; reutilizar selección Home para close marca sección incorrecta; migrar navegación completa amplía el alcance.
- **Evidence**: `app/.../navigation/MainDestination.kt`, `MainNavigation.kt`, `feature/home/.../HomeView.kt`.

## 7. Calidad y contexto del agente

- **Decision**: Pruebas primero por historia, integración real de lectura v4 sin escrituras y UI accesible; ≥70% cobertura de dominio/ViewModels tocados mediante JaCoCo ya catalogado. Actualizar referencia de AGENTS.md directamente entre marcadores.
- **Rationale**: El script update-agent-context.ps1 no está instalado; la instrucción específica de fase 1 permite actualizar la referencia. No crear un script sustituto. El nuevo informe de cobertura no reutiliza órdenes de Pedidos que excluyen clases nuevas.
- **Alternatives considered**: afirmar validación conectada sin dispositivo es incorrecto; regenerar toda la configuración del agente no es necesario.

Todas las decisiones necesarias para diseño están resueltas; los contratos financieros se difieren expresamente fuera de esta entrega.

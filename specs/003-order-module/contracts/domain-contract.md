# Domain Contract: Pedidos

**Date**: 2026-10-21 | **Related**: [modelo](../data-model.md)

Contrato interno de la aplicación; no hay API remota ni endpoints HTTP. Los nombres son la guía de implementación, los tipos deben residir en domain sin dependencias de Android/Room/Hilt.

## Repositorio

```kotlin
interface PedidoRepository {
    fun observePedidos(consulta: PedidoConsulta): Flow<PedidoReadResult<List<PedidoResumen>>>
    fun observePedido(id: Long): Flow<PedidoReadResult<Pedido>>
    fun observeResumen(inicio: Long, finExclusivo: Long): Flow<PedidoReadResult<ResumenConteos>>
    suspend fun crear(command: CrearPedidoCommand): PedidoWriteResult
    suspend fun actualizar(command: ActualizarPedidoCommand): PedidoWriteResult
    suspend fun cambiarEstado(command: CambiarEstadoPedidoCommand): PedidoWriteResult
}
```

- `PedidoResumen` contiene solo datos de tarjeta, sin imágenes ni líneas; consulta mantiene orden estable.
- Lecturas: Success(valor), NotFound (detalle), PersistenceError o InvalidLocalData. Loading es un estado de presentación, no una lista vacía de sustitución. Lista sin registros es Success(emptyList()).
- `CrearPedidoCommand`: token estable, cliente, observaciones y líneas nuevas con snapshot capturado por domain al incorporar producto. No recibe número, estado ni importes calculados por UI.
- `ActualizarPedidoCommand`: id, expectedRevision, cliente, observaciones, líneas existentes (id/cantidad) y nuevas con snapshot. No permite cambiar estado, número ni fecha de creación.
- `CambiarEstadoPedidoCommand`: id, expectedRevision y destino. Valida origen vigente y transición; confirmar cancelación es una precondición del flujo de UI.
- Escrituras: Success(pedido), ValidationError(campo/motivo), ProductUnavailable(productoId), NotFound, Conflict, InvalidTransition, InvalidLocalData o PersistenceError. No exponer Throwable ni mensajes SQL a UI; diagnósticos sanitizados en data. CancellationException se propaga.
- Crear con token confirmado devuelve Success del mismo pedido, incluso si otro escritor ya cambió su estado. UI no reutiliza ese token para otro pedido; la primera escritura confirmada determina su contenido.
- Edición/estado con revisión obsoleta devuelve Conflict; ningún cambio parcial. Reintentar tras éxito de respuesta perdida puede requerir recarga, sin duplicar historial.
- No se expone operación de eliminación, reapertura ni edición de terminales.

## Casos de uso

| Caso | Entrada | Resultado / responsabilidad |
|---|---|---|
| ObservePedidosUseCase | PedidoConsulta | Valida/normaliza criterios y observa proyección de tarjetas. |
| ObservePedidoUseCase | id | Detalle completo o fallo explícito. |
| CapturePedidoProductUseCase | productoId | Consulta ProductoRepository, exige activo, convierte dinero y devuelve snapshot, sin depender de feature:product. |
| ValidatePedidoUseCase | draft | Errores por campo, incluyendo vacío, cantidad inválida y overflow. |
| CalculatePedidoUseCase | líneas | Subtotales/total exactos; también usado dentro de escritura. |
| CreatePedidoUseCase | token + draft nuevo | Validar y delegar escritura atómica. |
| UpdatePedidoUseCase | id/revisión + draft | Validar y delegar; repositorio vuelve a validar contra estado vigente. |
| ChangePedidoStateUseCase | id/revisión/destino | Política pura de transiciones + escritura atómica. |
| ObserveDailyOrdersSummaryUseCase | sin filtros | flatMapLatest sobre jornada vigente y resumen; devuelve fecha y conteos. |

Precios del catálogo se convierten al incorporar una línea, no al renderizar. Snapshot nuevo contiene id, nombre y Money; cambios en cantidad no vuelven a consultar precio. En persistencia solo se revalida disponibilidad de nuevas referencias; no se sustituyen silenciosamente los valores capturados.

## Reloj y jornada

```kotlin
interface BusinessDayProvider {
    fun nowMillis(): Long
    fun observeDay(): Flow<BusinessDay>
    fun range(from: LocalDateParts?, through: LocalDateParts?): EpochRange
}
```

`LocalDateParts` representa año/mes/día sin framework; `BusinessDay` incluye fecha local e inicio/finExclusivo. Infraestructura observa medianoche, reanudación y cambios de fecha/hora/zona. El reloj usado para alta/transición y los límites se inyecta, no se llama System.currentTimeMillis directamente en reglas. Estados se ordenan por revisión/id, sin asumir reloj monotónico.

## Cobertura contractual mínima

Comprobar las 16 parejas origen/destino del enum; exactamente cuatro son válidas. Probar cero líneas, cliente con espacios, cantidad cero/negativa/fraccionaria, centavos, HALF_UP, NaN/infinito y overflow. Probar MARÍA/María y Unicode compuesto/descompuesto, búsqueda literal con %/_, intersección vacía, límites inclusivos de importes/fechas. Para escritura: token repetido, dos altas concurrentes, transición contra edición, rollback intermedio, producto borrado y snapshot inmutable.

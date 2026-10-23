# Data Model: Consulta preliminar de Cierre de Lote

**Date**: 2026-10-23 | **Spec**: [spec.md](spec.md)

## Persistencia existente

No hay entidades persistidas nuevas, cambios de esquema ni migración. Room permanece v4. Se leen exclusivamente:

| Fuente | Campos usados | Regla |
| --- | --- | --- |
| pedidos | id, fecha_creacion, estado, total_centavos | COMPLETADO actual y creación en [inicio, fin) |
| pedido_detalle | id, pedido_id, producto_id, nombre_producto, cantidad, precio_unitario_centavos, subtotal_centavos, posicion | Solo líneas pertenecientes a esas cabeceras |

No se consulta catálogo ni pedido_transiciones. Finalización no participa en el período. Índices existentes: pedidos(estado,fecha_creacion), pedido_detalle(pedido_id). Ninguna consulta modifica secuencia o registros. Cliente/observaciones/create_token no se proyectan.

## Modelos de dominio propuestos

| Modelo | Atributos | Validación / relaciones |
| --- | --- | --- |
| CierrePeriodo | inicioMillis:Long, finExclusivoMillis:Long, zonaId:String | Ambos extremos presentes; inicio < fin; fin <= reloj; zona válida; epoch millis |
| CierreLocalDateTime | year, month, day, hour, minute, second | Fecha civil real; mes1–12, hora0–23, minuto/segundo0–59 |
| CierreTimeCandidate | epochMillis:Long, offsetSeconds:Int | Uno o dos candidatos de resolución local; cero implica hora inexistente |
| PedidoConsultaSnapshot | pedidoId:Long, fechaCreacion:Long, totalBruto:Money, lineas:List<LineaConsultaSnapshot> | Identidad única positiva, al menos una línea, total coincide con suma de líneas |
| LineaConsultaSnapshot | lineaId:Long, productoId:Long, nombreHistorico:String, cantidad:Int, precioUnitario:Money, subtotal:Money, posicion:Int | Identidades positivas, nombre no vacío, cantidad>0, posición>=0 única por pedido, subtotal=precio×cantidad |
| ResumenConsultaCierre | totalPedidos:Int, bruto:Money | Conteo de cabeceras distintas y suma exacta una vez por cabecera |
| ProductoConsultaCierre | productoId:Long, nombreHistorico:String, cantidad:Long, bruto:Money | Agregado de todas las líneas incluidas de esa identidad |
| ConsultaCierre | periodo:CierrePeriodo, resumen:ResumenConsultaCierre, topProductos:List<ProductoConsultaCierre> | Máximo5; orden cantidad DESC, bruto DESC, productoId ASC; todos proceden de la misma lectura |
| ConsultaCierreResult | Success(ConsultaCierre), InvalidPeriod(código), InvalidData(código), ReadFailure | Resultados explícitos, sin texto de excepción/PII a UI; cancelación se propaga |

No hay modelos de pagos/cierres ficticios. La indisponibilidad financiera es capacidad fija de esta versión en presentación, no Money(0) ni null ambiguo dentro del resultado de consulta.

## Proyección de lectura y transformación

Una sola sentencia SELECT con LEFT JOIN devuelve columnas de cabecera y columnas de línea anulables. La columna lineaId determina si hay línea: si falta, el repositorio devuelve InvalidData(MissingLines); no inventa una lista válida vacía. Campos obligatorios ausentes o cabeceras repetidas con valores discordantes son InvalidData.

El mapper agrupa por pedidoId, conserva únicamente datos mínimos y valida estructura; el caso de uso valida integridad monetaria y genera ambas agregaciones. No usa SUM SQL ni precios actuales. Una cabecera repetida por cada línea nunca se suma más de una vez. Una lista de cero filas es éxito vacío.

## Agregación y representación histórica

1. Validar Money y cantidad de cada línea; multiplicación comprobada y correspondencia con subtotal.
2. Validar cabecera respecto de sus líneas y unicidad de IDs/posiciones.
3. Sumar bruto por cabecera con comprobación de overflow. Cantidad de cabeceras debe caber en Int.
4. Agrupar todas las líneas por productoId, sumar cantidad en Long comprobado e importe Money comprobado.
5. Elegir nombre histórico de la línea con mayor `(fechaCreacion, pedidoId, posicion)`. Posición única por pedido evita empate residual; lineaId permite trazabilidad.
6. Ordenar y tomar cinco; su suma monetaria puede ser inferior al bruto general porque no contiene productos restantes. «Otros» no es agregado.

Contexto monetario vigente: Money es MXN no negativo y los formatos existentes usan es-MX. Mostrar centavos exactos con BigDecimal/NumberFormat, sin conversión a Double ni moneda configurable nueva.

## Estado de presentación y ciclo de vida

`BatchClosingUiState` contiene selección persistible (instantes/zona), edición civil temporal, generación de solicitud, estado Loading/InvalidPeriod/Empty/Ready/Error y resultado inmutable cuando corresponde. Empty conserva resumen de ceros. Métodos/descuentos/neto siempre se representan como No disponible, incluso en Empty/Error; error de consulta es independiente de indisponibilidad financiera.

Transiciones: entrada→selección por defecto→validación→Loading→Ready/Empty/Error. Editar o reintentar invalida resultado y crea generación nueva; validación fallida no invoca repositorio. Resultados tardíos no alteran estado vigente. Reanudación refresca reloj/zona, revalida y consulta mismo intervalo. Restauración de proceso conserva selección y vuelve a leer, no restaura resultado como evidencia vigente.

No hay transiciones del estado del pedido, creación de lotes ni asociación de operaciones. Un pedido pendiente que se completa aparecerá al recalcular su período de creación, aunque su finalización sea posterior al fin consultado.

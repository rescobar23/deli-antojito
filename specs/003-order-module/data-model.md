# Data Model: Módulo de Pedidos

**Date**: 2026-10-21 | **Related**: [research.md](research.md), [contrato](contracts/domain-contract.md)

## Modelo de dominio

| Entidad | Campos / tipos conceptuales | Invariantes |
|---|---|---|
| Money | centavos: Long, moneda MXN implícita | No negativo; precios de línea >0; aritmética exacta sin overflow. |
| Pedido | id: Long, numero: Long, cliente: String, creadoEn/actualizadoEn: Long epoch millis, estado: EstadoPedido, total: Money, observaciones: String?, revision: Long, lineas: List | id/numero positivos, cliente trim no vacío, ≥1 línea, total exacto, revisión incremental. |
| PedidoLinea | id: Long, productoId: Long, nombreProducto: String, cantidad: Int, precioUnitario/subtotal: Money, posicion: Int | cantidad >0; snapshot inmutable; subtotal exacto; posición conserva orden visible. |
| PedidoTransicion | id/pedidoId: Long, anterior/nuevo: EstadoPedido, ocurridoEn: Long | Cambio autorizado; orden estable por id; no inventar historial anterior al alta. |
| PedidoDraft | cliente, observaciones, líneas nuevas o referencias a líneas existentes | Sin número/fecha/estado editables; distinguir línea existente por id. |
| PedidoConsulta | texto, estadoPestana?, estadosAvanzados: Set, desde/hasta: fecha local?, min/max: Money? | Rangos válidos, extremos opcionales; conjunto avanzado vacío no restringe; intersección vacía sí devuelve cero. |
| ResumenPedidos | fecha local de referencia, totalPedidos, pendientes, enPreparacion, completados | Cuenta solo creados en intervalo del día, total incluye cancelados; independiente de consulta. |

Normalizar observaciones vacías a null; no imponer límites arbitrarios de negocio a textos en esta entrega. Validar capacidad numérica y señalar errores de cantidad/importe demasiado grande antes de escribir. No truncar datos silenciosamente.

## Máquina de estados

```text
alta → PENDIENTE → EN_PREPARACION → COMPLETADO
           ↓             ↓
       CANCELADO ←───────┘
```

Solo PENDIENTE es editable. COMPLETADO/CANCELADO son terminales. Alta establece fecha de creación/actualización y revisión 1; no es una transición desde un estado previo ficticio. Cada cambio posterior agrega una transición y aumenta revisión. La edición aumenta revisión/actualización sin agregar transición de estado.

## Persistencia v4

### pedidos

| Columna | Tipo SQL | Restricciones |
|---|---|---|
| id | INTEGER | PK AUTOINCREMENT NOT NULL |
| numero | INTEGER | NOT NULL, índice UNIQUE |
| create_token | TEXT | NOT NULL, índice UNIQUE |
| cliente / cliente_busqueda | TEXT | NOT NULL; original y clave normalizada |
| numero_busqueda | TEXT | NOT NULL, mínimo cinco dígitos |
| fecha_creacion / fecha_actualizacion | INTEGER | NOT NULL, epoch millis |
| estado | TEXT | NOT NULL, nombre estable del enum |
| total_centavos | INTEGER | NOT NULL |
| observaciones | TEXT | NULL |
| revision | INTEGER | NOT NULL |

Índices adicionales: fecha_creacion e (estado, fecha_creacion). Orden estable fecha_creacion DESC, id DESC. Consultas parametrizadas, sin concatenar texto del usuario. Los tokens no se muestran ni registran en logs.

### pedido_detalle

| Columna | Tipo SQL | Restricciones |
|---|---|---|
| id | INTEGER | PK AUTOINCREMENT NOT NULL |
| pedido_id | INTEGER | NOT NULL, FK pedidos(id), ON DELETE RESTRICT; índice |
| producto_id | INTEGER | NOT NULL, referencia histórica sin FK a productos |
| nombre_producto | TEXT | NOT NULL |
| cantidad | INTEGER | NOT NULL |
| precio_unitario_centavos / subtotal_centavos | INTEGER | NOT NULL |
| posicion | INTEGER | NOT NULL |

Edición reemplaza o actualiza solo líneas del pedido validado, sin alterar sus snapshots existentes. Retirar una línea pendiente está permitido; eliminar el pedido no lo está. Una selección repetida del mismo producto/precio se agrupa, conservando snapshot de la línea ya presente; distinto precio conserva línea separada.

### pedido_transiciones

id INTEGER PK AUTOINCREMENT; pedido_id INTEGER NOT NULL FK pedidos(id) ON DELETE RESTRICT con índice; estado_anterior TEXT NOT NULL; estado_nuevo TEXT NOT NULL; fecha_hora INTEGER NOT NULL. Historial solo append. No actualización/borrado expuestos por repositorio.

### pedido_secuencia

id INTEGER PK NOT NULL, fila singleton id=1; siguiente_numero INTEGER NOT NULL inicial 1. Insertar singleton en migración y en callback de creación fresca. Un número solo se consume dentro de la transacción de alta. Verificar agotamiento Long y devolver error, nunca reiniciar o reutilizar secuencia.

## Fronteras y consistencia

- Mappers traducen entidades a modelos; nunca filtran entidades Room a feature. Estado desconocido o datos inválidos producen InvalidLocalData, no conversión silenciosa a pendiente.
- Domain posee validación/cálculo y políticas de transición; data aplica esas políticas sobre la versión vigente dentro de la transacción usando funciones puras de domain.
- Crear: buscar token → si existe devolver pedido confirmado → comprobar referencias nuevas activas → validar/cálculo → reservar número → insertar cabecera/líneas → confirmar. Cualquier fallo revierte todo.
- Editar: leer agregado → comparar revisión/estado → conservar snapshots de ids existentes → comprobar referencias nuevas → validar/recalcular → escribir líneas/cabecera con revisión+1. Cero filas en actualización condicional implica conflicto y rollback.
- Transición: leer vigente → validar revisión y transición → actualización condicional → insertar historial. Errores no alteran cabecera ni historial.
- No aceptar total ni subtotal de UI como fuente de verdad. Línea existente debe pertenecer al pedido; precio/nombre se obtienen de su registro persistido.
- Cambios de catálogo no recalculan pedidos. Para líneas nuevas, precio/nombre capturados por el caso de uso al incorporarlas se conservan aunque el catálogo cambie; la disponibilidad se revalida al guardar.
- Rango diario/fechas: convertir fechas locales a [inicio, inicio del día siguiente al final); usar fecha_creacion, no fecha de transición. Resumen agrega conteos directamente, sin cargar detalles ni aplicar filtros.

## Migración y pruebas

MIGRATION_3_4 crea tablas/índices y secuencia sin tocar productos/configuracion. La creación fresca debe generar exactamente el mismo esquema. Comparar ambos con la validación de Room, abrir fixture v3 con datos y ejecutar también las cadenas 1→4/2→4. Exportar el esquema v4 versionado mediante KSP/schemaLocation en data. No hay migración de pedidos históricos ni rollback destructivo.

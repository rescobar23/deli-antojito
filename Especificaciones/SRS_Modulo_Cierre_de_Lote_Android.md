# SRS – Módulo de Cierre de Lote

**Aplicación Android | Jetpack Compose**  
**Versión:** 1.0  
**Estado:** Propuesta inicial  
**Fecha:** 22 de octubre de 2026  
**Arquitectura:** Clean Architecture + MVVM + Multi-módulo  
**Persistencia:** Room  
**Referencia:** Mockup proporcionado

## 1. Introducción

### 1.1 Propósito

Definir los requisitos funcionales y no funcionales del módulo **Cierre de Lote**, que permitirá consultar, consolidar y registrar las operaciones de venta de un período, manteniendo integridad financiera y un historial inmutable.

### 1.2 Alcance

- Selección del período de cierre.
- Resumen de ventas, descuentos y total neto.
- Desglose de ventas por método de pago.
- Consulta de productos más vendidos.
- Notas opcionales.
- Validación y confirmación del cierre.
- Persistencia de cierres e historial.
- Prevención de cierres duplicados.

### 1.3 Objetivos

| ID | Objetivo |
|---|---|
| OBJ-001 | Consolidar ventas por período. |
| OBJ-002 | Presentar indicadores financieros claros. |
| OBJ-003 | Identificar métodos de pago. |
| OBJ-004 | Mostrar productos más vendidos. |
| OBJ-005 | Evitar duplicidad de operaciones cerradas. |
| OBJ-006 | Mantener historial. |
| OBJ-007 | Garantizar integridad de importes. |
| OBJ-008 | Facilitar conciliación. |

## 2. Descripción general

### 2.1 Usuarios

Personal responsable de supervisar y realizar cierres. No se definen permisos diferenciados en la primera versión.

### 2.2 Concepto y estados

Un lote contiene identificador, número consecutivo, período, fecha de ejecución, cantidad de pedidos, ventas brutas, descuentos, total neto, métodos de pago, productos vendidos, notas y estado.

| Estado | Significado |
|---|---|
| ABIERTO | Período disponible para consolidar operaciones. |
| CERRADO | Consolidación definitiva, de solo lectura. |

**Reglas RN-001 a RN-007:** Solo se cierra un lote abierto tras validación; un cierre no se reabre ni modifica; no puede ejecutarse dos veces; ninguna operación puede pertenecer a dos cierres; se conserva una fotografía financiera; el cierre es transaccional.

## 3. Requisitos funcionales

### RF-001 – Visualizar pantalla de cierre (Alta)

La pantalla muestra encabezado «Cierre de Lote», período, cuatro tarjetas de resumen, ventas por método de pago, productos más vendidos, notas y botón «Cerrar lote».

**Criterios CA-001 a CA-003:** Se muestra el período al entrar; aparecen los indicadores; se presentan métodos de pago y productos vendidos.

### RF-002 – Seleccionar período (Alta)

Dos selectores de fecha y hora: **Fecha de inicio** y **Fecha de fin**. Al cambiar el período, se recalculan los indicadores.

- **RN-008:** Inicio anterior a fin.
- **RN-009:** No se ejecutan cierres con fecha final futura.
- **RN-010:** Usar zona horaria del negocio.
- **RN-011:** Excluir operaciones ya cerradas.
- **RN-012:** Mantener precisión temporal de segundos o superior.

Se utilizarán intervalos semiabiertos: `fechaInicio <= fechaOperacion < fechaFinExclusiva`. Para el 15 de mayo, inicio `2024-05-15 00:00:00` y fin exclusivo `2024-05-16 00:00:00`.

**CA-004 a CA-006:** Cambiar inicio o fin actualiza los datos; períodos inválidos impiden cerrar.

### RF-003 – Resumen financiero (Alta)

| Indicador | Ejemplo del mockup | Fórmula |
|---|---:|---|
| Pedidos | 28 | `COUNT(PedidosElegibles)` |
| Ventas totales | $6,850.00 | `SUM(SubtotalBrutoPedido)` |
| Descuentos | $320.00 | `SUM(DescuentoPedido)` |
| Total neto | $6,530.00 | `VentasTotales - DescuentosTotales` |

- **RN-013:** Excluir pedidos cancelados de ventas.
- **RN-014:** Excluir pagos no confirmados de importes cobrados.
- **RN-015:** Calcular importes con precisión monetaria exacta.
- **RN-016:** Considerar exclusivamente operaciones del período.
- **RN-017:** Obtener descuentos de registros reales.

**Dependencia:** El módulo Pedidos deberá incorporar descuentos y pagos para soportar estos cálculos.

### RF-004 – Ventas por método de pago (Alta)

Mostrar icono, nombre, importe y porcentaje de **Efectivo, Tarjeta, Transferencia y Otros**.

| Método | Importe de ejemplo | Porcentaje |
|---|---:|---:|
| Efectivo | $3,120.00 | 47.8% |
| Tarjeta | $2,650.00 | 40.6% |
| Transferencia | $480.00 | 7.4% |
| Otros | $280.00 | 4.2% |
| **Total** | **$6,530.00** | **100%** |

`PorcentajeMetodo = (TotalMetodoPago / TotalNeto) * 100`, o `0.0%` cuando el neto sea cero.

- **RN-018:** Agrupar pagos por método.
- **RN-019:** Incluir solo pagos confirmados.
- **RN-020:** Excluir pagos ya cerrados.
- **RN-021:** La suma por métodos debe coincidir con el neto.
- **RN-022:** Impedir el cierre ante diferencias de conciliación.

**CA-007 a CA-010:** Importes y porcentajes correctos; suma conciliada; ceros cuando no hay ventas.

### RF-005 – Productos más vendidos (Alta)

Tabla de los cinco productos con mayor cantidad vendida, con columnas **#**, **Producto**, **Cantidad** e **Importe**.

| # | Producto | Cantidad | Importe de ejemplo |
|---|---|---:|---:|
| 1 | Alitas | 42 | $2,520.00 |
| 2 | Crepas | 28 | $1,680.00 |
| 3 | Papas | 25 | $1,250.00 |
| 4 | Plátanos fritos | 18 | $720.00 |
| 5 | Otros | 15 | $400.00 |

- **RN-023:** Ordenar por cantidad descendente.
- **RN-024:** Usar precios históricos del pedido.
- **RN-025:** Excluir pedidos cancelados.
- **RN-026:** Mostrar Top 5.
- **RN-027:** Desempatar por importe descendente.
- **RN-028:** Conservar nombres históricos aunque el producto esté desactivado.

«Otros» solo será un producto real del catálogo; no agrupará automáticamente productos restantes.

**CA-011 a CA-013:** Top 5 ordenado correctamente y con importes reales.

### RF-006 – Notas del cierre (Media)

Campo multilínea opcional con texto guía «Agregar notas sobre el cierre del lote...», máximo **250 caracteres** y contador visible.

- **RN-029:** Máximo 250 caracteres.
- **RN-030:** Mostrar contador.
- **RN-031:** Guardar notas con el cierre.
- **RN-032:** No permitir edición tras cerrar.

**CA-014 a CA-016:** Admite notas; impide exceder el límite; conserva notas.

### RF-007 – Ejecutar cierre (Crítica)

**Flujo:** Seleccionar período → consultar y revisar operaciones → escribir notas opcionales → pulsar «Cerrar lote» → validar → mostrar confirmación → confirmar → guardar lote y asociar operaciones → mostrar éxito.

**Confirmación:** «¿Deseas cerrar el lote correspondiente al período seleccionado? Esta operación consolidará las ventas y no podrá deshacerse». Botones: **Cancelar** y **Confirmar cierre**.

**Validaciones:**

| ID | Validación |
|---|---|
| VAL-001 | Período válido. |
| VAL-002 | Sin operaciones duplicadas. |
| VAL-003 | Operaciones elegibles. |
| VAL-004 | Importes consistentes. |
| VAL-005 | Pagos coinciden con neto. |
| VAL-006 | Sin operaciones incluidas en otro cierre. |
| VAL-007 | Base de datos disponible. |
| VAL-008 | Sin cierre concurrente sobre las mismas operaciones. |

- **RN-033:** Ejecutar dentro de transacción.
- **RN-034:** Revertir íntegramente ante errores.
- **RN-035:** Evitar duplicados.
- **RN-036:** Asociar operaciones al identificador de cierre.
- **RN-037:** Guardar fotografía histórica.
- **RN-038:** Lote cerrado inmutable.
- **RN-039:** Si cambian datos antes de confirmar, recalcular y solicitar confirmación de nuevo.

**Política propuesta de pendientes:** Pedidos pendientes o en preparación no bloquean automáticamente; solo se incluyen operaciones con pago confirmado y elegibles, sin consolidación previa. Esta regla requiere validación de negocio.

### RF-008 – Historial de cierres (Media)

Consulta de lotes anteriores con número, fecha de ejecución, período, cantidad de pedidos, neto y estado. Orden descendente por fecha; solo lectura; datos históricos independientes de cambios posteriores en productos o precios. No está representado directamente en el mockup, pero es necesario para consultar cierres registrados.

## 4. Interfaz y diseño

### 4.1 Estructura visual

1. Barra superior con menú y título.
2. Tarjeta **Período del cierre**, con fecha y hora inicial y final.
3. Tarjeta **Resumen de ventas**, con cuatro indicadores.
4. Tarjeta **Ventas por método de pago**, con importes y porcentajes.
5. Tarjeta **Productos más vendidos**, con tabla Top 5.
6. Tarjeta **Notas del cierre (opcional)**, con contador `0/250`.
7. Botón principal **Cerrar lote**, ancho disponible y estilo Aqua.

### 4.2 Paleta Aqua

| Token | Hex | Uso |
|---|---|---|
| AquaLv1 | `#A9E0D4` | Fondos suaves. |
| AquaLv2 | `#5FC8B0` | Elementos secundarios. |
| AquaLv3 | `#3CAFA9` | Interacciones. |
| AquaLv4 | `#2E8E8E` | Títulos y selección. |
| AquaLv5 | `#1A6B69` | Encabezados y acciones principales. |

Colores semánticos: pedidos verde, ventas Aqua, descuentos naranja y neto azul.

### 4.3 Componentes Compose

| Elemento | Componente |
|---|---|
| Pantalla | `Scaffold` |
| Barra superior | `TopAppBar` |
| Secciones e indicadores | `Card` |
| Fecha y hora | `DatePicker`, `TimePicker` |
| Métodos de pago | `Column` |
| Tabla Top 5 | `Column` o `LazyColumn` |
| Notas | `OutlinedTextField` |
| Acción principal | `Button` |
| Confirmación | `AlertDialog` |
| Carga | `CircularProgressIndicator` |

En pantallas compactas las tarjetas de indicadores se dispondrán en dos columnas; en pantallas amplias podrán ir en una fila. Evitar desbordamiento horizontal.

## 5. Modelo de datos

```kotlin
data class CierreLote(
    val id: Long,
    val numero: String,
    val fechaInicio: Instant,
    val fechaFin: Instant,
    val fechaCierre: Instant,
    val estado: EstadoLote,
    val totalPedidos: Int,
    val ventasBrutasCentavos: Long,
    val descuentosCentavos: Long,
    val totalNetoCentavos: Long,
    val notas: String?
)

enum class EstadoLote { ABIERTO, CERRADO }

data class ResumenVentas(
    val totalPedidos: Int,
    val ventasBrutasCentavos: Long,
    val descuentosCentavos: Long,
    val totalNetoCentavos: Long
)

data class VentaMetodoPago(
    val metodo: MetodoPago,
    val importeCentavos: Long,
    val porcentaje: Double
)

enum class MetodoPago { EFECTIVO, TARJETA, TRANSFERENCIA, OTROS }

data class ProductoVendido(
    val productoId: Long,
    val nombre: String,
    val cantidad: Int,
    val importeCentavos: Long
)

data class CierreLoteUiState(
    val isLoading: Boolean = false,
    val isClosing: Boolean = false,
    val fechaInicio: Instant? = null,
    val fechaFin: Instant? = null,
    val resumen: ResumenVentas? = null,
    val metodosPago: List<VentaMetodoPago> = emptyList(),
    val productosVendidos: List<ProductoVendido> = emptyList(),
    val notas: String = "",
    val error: String? = null
)
```

Los importes se almacenarán en centavos (`Long`) para evitar errores de precisión.

## 6. Arquitectura multi-módulo

```text
NombreDelProyecto/
├── app/navigation/AppNavigation.kt
├── core/
│   ├── ui/{theme,components}/
│   └── common/
├── domain/
│   ├── model/{CierreLote,ResumenVentas,VentaMetodoPago,ProductoVendido}.kt
│   ├── repository/CierreLoteRepository.kt
│   └── usecase/cierre/
│       ├── ObtenerResumenVentasUseCase.kt
│       ├── ObtenerVentasPorMetodoUseCase.kt
│       ├── ObtenerProductosVendidosUseCase.kt
│       ├── ValidarCierreUseCase.kt
│       ├── EjecutarCierreUseCase.kt
│       └── ObtenerHistorialCierresUseCase.kt
├── data/
│   ├── database/
│   │   ├── entity/{CierreLoteEntity,CierreLotePedidoEntity,CierreLoteMetodoPagoEntity}.kt
│   │   └── dao/CierreLoteDao.kt
│   └── repository/CierreLoteRepositoryImpl.kt
└── feature/cierre/
    ├── navigation/CierreLoteNavigation.kt
    ├── presentation/{CierreLoteScreen,CierreLoteViewModel,CierreLoteUiState}.kt
    ├── history/{HistorialCierresScreen,HistorialCierresViewModel}.kt
    └── components/
        ├── PeriodoCierreCard.kt
        ├── ResumenVentasCard.kt
        ├── MetodoPagoRow.kt
        ├── ProductosVendidosTable.kt
        ├── NotasCierreCard.kt
        └── ConfirmarCierreDialog.kt
```

**Responsabilidades:** Presentation renderiza; ViewModel administra estado; Domain aplica reglas y cálculos; Repository abstrae datos; Data implementa Room; Core UI provee componentes.

## 7. Persistencia

### 7.1 Tabla `cierre_lote`

| Columna | Tipo | Restricción |
|---|---|---|
| id | INTEGER | PRIMARY KEY |
| numero | TEXT | UNIQUE NOT NULL |
| fecha_inicio | INTEGER | NOT NULL |
| fecha_fin | INTEGER | NOT NULL |
| fecha_cierre | INTEGER | NOT NULL |
| estado | TEXT | NOT NULL |
| total_pedidos | INTEGER | NOT NULL |
| ventas_brutas_centavos | INTEGER | NOT NULL |
| descuentos_centavos | INTEGER | NOT NULL |
| total_neto_centavos | INTEGER | NOT NULL |
| notas | TEXT | NULL |

### 7.2 Tabla `cierre_lote_pedido`

| Columna | Tipo | Restricción |
|---|---|---|
| id | INTEGER | PRIMARY KEY |
| cierre_lote_id | INTEGER | FOREIGN KEY |
| pedido_id | INTEGER | UNIQUE, FOREIGN KEY |
| importe_centavos | INTEGER | NOT NULL |

La unicidad de `pedido_id` impide asociar un pedido a dos cierres. Si se admiten pagos parciales, deberá asociarse cada transacción de pago a un cierre, en lugar del pedido completo.

### 7.3 Tabla `cierre_lote_metodo_pago`

| Columna | Tipo | Restricción |
|---|---|---|
| id | INTEGER | PRIMARY KEY |
| cierre_lote_id | INTEGER | FOREIGN KEY |
| metodo_pago | TEXT | NOT NULL |
| importe_centavos | INTEGER | NOT NULL |

Restricción única compuesta: `(cierre_lote_id, metodo_pago)`.

### 7.4 Transacción de cierre

Validar operaciones → comprobar no duplicidad → recalcular importes → insertar cierre → asociar pedidos → guardar métodos de pago → persistir fotografía histórica → confirmar. Ante cualquier error, revertir todo.

## 8. Requisitos no funcionales

| ID | Requisito |
|---|---|
| RNF-001 | Resumen en menos de 2 segundos para 1,000 operaciones locales en dispositivo de pruebas. |
| RNF-002 | Consultas fuera del hilo principal. |
| RNF-003 | Cálculos mediante Coroutines. |
| RNF-004 | Indicadores de carga. |
| RNF-005 | Consultas optimizadas con índices. |
| RNF-006 | Cierres atómicos. |
| RNF-007 | Sin duplicidad de operaciones. |
| RNF-008 | Precisión monetaria. |
| RNF-009 | Cierres inmutables. |
| RNF-010 | Persistencia tras reiniciar. |
| RNF-011 | Material Design 3. |
| RNF-012 | Importes legibles. |
| RNF-013 | Confirmación de acciones irreversibles. |
| RNF-014 | Diseño adaptable. |
| RNF-015 | Errores comprensibles. |
| RNF-016 | Lógica financiera separada de UI. |
| RNF-017 | Cálculos en casos de uso. |
| RNF-018 | Repositorios definidos por interfaces. |
| RNF-019 | Componentes reutilizables. |
| RNF-020 | Pruebas unitarias de reglas financieras. |

## 9. Casos de uso

| ID | Caso de uso | Prioridad |
|---|---|---|
| CU-001 | Consultar pantalla de cierre | Alta |
| CU-002 | Seleccionar período | Alta |
| CU-003 | Consultar resumen de ventas | Alta |
| CU-004 | Consultar métodos de pago | Alta |
| CU-005 | Consultar productos vendidos | Alta |
| CU-006 | Registrar notas | Media |
| CU-007 | Validar cierre | Crítica |
| CU-008 | Confirmar cierre | Crítica |
| CU-009 | Registrar lote cerrado | Crítica |
| CU-010 | Consultar historial | Media |

## 10. Pruebas

| ID | Escenario | Resultado esperado |
|---|---|---|
| TEST-001 | Abrir módulo | Pantalla correcta. |
| TEST-002 | Modificar período | Indicadores actualizados. |
| TEST-003 | Fechas inválidas | Validación visible. |
| TEST-004 | Consultar ventas | Importes correctos. |
| TEST-005 | Consultar métodos de pago | Importes y porcentajes correctos. |
| TEST-006 | Consultar productos | Top 5 correcto. |
| TEST-007 | Escribir notas | Se permite texto. |
| TEST-008 | Superar 250 caracteres | Se bloquea exceso. |
| TEST-009 | Pulsar Cerrar lote | Solicita confirmación. |
| TEST-010 | Cancelar confirmación | No guarda cierre. |
| TEST-011 | Confirmar cierre válido | Guarda lote. |
| TEST-012 | Cerrar operaciones ya cerradas | Impide duplicidad. |
| TEST-013 | Error de transacción | Reversión total. |
| TEST-014 | Consultar historial | Muestra fotografía almacenada. |
| TEST-015 | Reiniciar app | Persisten cierres. |
| TEST-016 | Diferencia entre pagos y neto | Bloquea cierre. |
| TEST-017 | Modificación concurrente | Detecta cambio y evita datos obsoletos. |

## 11. Dependencias e integración

| Módulo | Propósito |
|---|---|
| Pedidos | Operaciones e importes. |
| Productos | Información histórica de artículos vendidos. |
| Pagos | Métodos e importes cobrados. |
| Core UI | Tema y componentes. |
| Domain | Reglas financieras. |
| Data Database | Persistencia. |
| Navigation | Navegación. |
| Hilt | Inyección de dependencias. |

**Bloqueo de integración:** El SRS inicial de Pedidos no contempla completamente descuentos, pagos, pagos parciales, devoluciones ni reembolsos. Estos contratos deben definirse antes de implementar la consolidación financiera definitiva.

## 12. Etapas de desarrollo

| Etapa | Funcionalidad | Prioridad | Estado |
|---|---|---|---|
| 1. Consulta del cierre | Pantalla, período, indicadores y productos. | Alta | Pendiente |
| 2. Consolidación financiera | Pagos, descuentos y conciliación. | Alta | Pendiente |
| 3. Ejecución del cierre | Confirmación, transacción y persistencia. | Crítica | Pendiente |
| 4. Historial y optimización | Consulta histórica, pruebas y rendimiento. | Media | Pendiente |

**Entregables etapa 1:** `CierreLoteScreen`, `CierreLoteViewModel`, `PeriodoCierreCard`, `ResumenVentasCard`, `ProductosVendidosTable` y consultas por período.

**Entregables etapa 2:** Cálculos de ventas brutas, descuentos, neto, desglose por pago, conciliación y pruebas unitarias.

**Entregables etapa 3:** Validación previa, confirmación, transacción, fotografía histórica, prevención de duplicados y manejo de errores.

**Entregables etapa 4:** Historial, detalle histórico, optimización de consultas, pruebas de integración y rendimiento.

## 13. Criterios generales de aceptación

El módulo estará completo cuando permita: seleccionar período; mostrar indicadores correctos; calcular bruto, descuentos y neto; desglosar pagos; mostrar Top 5; guardar notas; validar y confirmar cierre; ejecutar transacción atómica; evitar operaciones duplicadas; conservar historial; consultar cierres anteriores; respetar el mockup; e integrarse a la arquitectura multi-módulo.

## 14. Decisiones pendientes de validación

| ID | Decisión | Propuesta inicial |
|---|---|---|
| DP-001 | Cerrar períodos anteriores | Sí, operaciones no cerradas. |
| DP-002 | Cerrar sin ventas | No. |
| DP-003 | Incluir pedidos pendientes | Solo operaciones pagadas y elegibles. |
| DP-004 | Incluir cancelados | No, salvo conciliación financiera separada. |
| DP-005 | Cierres duplicados | No. |
| DP-006 | Reabrir lote | No. |
| DP-007 | Historial | Sí. |
| DP-008 | Exportar PDF | Fuera del alcance inicial. |
| DP-009 | Imprimir cierre | Fuera del alcance inicial. |
| DP-010 | Sincronizar con servidor | Fuera del alcance inicial. |
| DP-011 | Pagos parciales | Por definir. |
| DP-012 | Devoluciones | Por definir. |
| DP-013 | Asignación temporal | Fecha de confirmación de pago. |
| DP-014 | Períodos superpuestos | Solo operaciones todavía no cerradas. |

## 15. Resumen

| Categoría | Cantidad |
|---|---:|
| Objetivos | 8 |
| Requisitos funcionales | 8 |
| Reglas de negocio | 39 |
| Requisitos no funcionales | 20 |
| Casos de uso | 10 |
| Escenarios de prueba | 17 |
| Decisiones pendientes | 14 |
| Etapas | 4 |

**Conclusión:** SRS versión 1.0 del módulo Cierre de Lote, preparado como base para implementación con Kotlin, Jetpack Compose, Room, MVVM y Clean Architecture, y para planificación posterior con GitHub Spec Kit y Codex.

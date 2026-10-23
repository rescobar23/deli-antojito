# Especificación de Requisitos de Software (SRS)
## Módulo de Pedidos — Aplicación Android

| Campo | Valor |
|---|---|
| Versión | 1.0 |
| Fecha | 2026-10-19 |
| Estado | Propuesta inicial |
| Plataforma | Android |
| Lenguaje | Kotlin |
| UI | Jetpack Compose / Material Design 3 |
| Arquitectura | Clean Architecture + MVVM + multimódulo |
| Persistencia | Room |
| Referencia | Mockup de listado de pedidos proporcionado |

## 1. Introducción

### 1.1 Propósito
Definir los requisitos funcionales y no funcionales del módulo **Pedidos**, que permitirá registrar, consultar, buscar, filtrar y administrar pedidos, además de presentar indicadores diarios.

### 1.2 Alcance
- Listado de pedidos y acceso a detalle.
- Alta y edición de pedidos.
- Búsqueda por número y cliente.
- Filtrado por estado y filtros avanzados.
- Gestión de estados y cancelaciones.
- Resumen del día.
- Navegación con los demás módulos.

### 1.3 Objetivos
| ID | Objetivo |
|---|---|
| OBJ-001 | Centralizar la administración de pedidos. |
| OBJ-002 | Identificar rápidamente el estado de cada pedido. |
| OBJ-003 | Permitir búsquedas y filtros eficientes. |
| OBJ-004 | Reducir el tiempo de consulta. |
| OBJ-005 | Mostrar indicadores diarios. |
| OBJ-006 | Mantener consistencia visual con la aplicación. |

## 2. Descripción general

### 2.1 Usuarios
Personal del negocio encargado de registrar y administrar pedidos. La primera versión no diferencia permisos por usuario.

### 2.2 Estados
| Estado | Significado | Identificación visual |
|---|---|---|
| `PENDIENTE` | Registrado, sin iniciar preparación. | Amarillo y reloj. |
| `EN_PREPARACION` | Se está preparando. | Azul y gorro de chef. |
| `COMPLETADO` | Preparación finalizada. | Verde y marca de verificación. |
| `CANCELADO` | Pedido cancelado. | Rojo y cruz. |

Transiciones permitidas:

```text
PENDIENTE ───────> EN_PREPARACION ───────> COMPLETADO
    │                    │
    └────> CANCELADO <───┘
```

**Reglas de negocio**
- **RN-001:** Todo pedido nuevo inicia en `PENDIENTE`.
- **RN-002:** Desde `PENDIENTE` se permite `EN_PREPARACION` o `CANCELADO`.
- **RN-003:** Desde `EN_PREPARACION` se permite `COMPLETADO` o `CANCELADO`.
- **RN-004:** `COMPLETADO` es terminal.
- **RN-005:** `CANCELADO` es terminal.
- **RN-006:** No se permite eliminar físicamente pedidos desde este módulo.
- **RN-007:** Cada cambio de estado registra fecha y hora.

## 3. Requisitos funcionales

### RF-001 — Listado de pedidos
**Prioridad:** Alta.

La pantalla principal muestra tarjetas con número consecutivo (`#00025`), cliente, fecha, hora, estado, total, icono de estado y acceso al detalle.

**Reglas:** orden descendente por creación; desplazamiento vertical; total en formato monetario; selección de tarjeta abre detalle.

**Aceptación:**
- **CA-001:** Al abrir el módulo con datos, aparecen los pedidos del más reciente al más antiguo.
- **CA-002:** Al tocar una tarjeta, se abre su detalle.
- **CA-003:** Sin pedidos, aparece un estado vacío explicativo.

### RF-002 — Búsqueda
**Prioridad:** Alta.

El campo `Buscar pedidos...` filtra por número o nombre del cliente.

**Reglas:** búsqueda insensible a mayúsculas, sin espacios extremos; actualización al escribir; debounce recomendado de 300 ms; combinación con filtros activos; limpiar búsqueda restablece el conjunto filtrado.

**Aceptación:**
- **CA-004:** Buscar `María` encuentra coincidencias por cliente.
- **CA-005:** Buscar `00025` encuentra el pedido correspondiente si satisface los demás filtros.
- **CA-006:** Sin coincidencias se muestra `No se encontraron pedidos`.

### RF-003 — Pestañas de estado
**Prioridad:** Alta.

Pestañas: **Todos**, **Pendientes**, **En preparación**, **Completados**, **Cancelados**.

**Reglas:** `Todos` seleccionada inicialmente; selección exclusiva; actualización inmediata; pestaña activa con subrayado Aqua; navegación horizontal cuando no quepan todas.

**Aceptación:**
- **CA-007:** `Pendientes` muestra solo pedidos pendientes.
- **CA-008:** `Todos` muestra pedidos de cualquier estado.
- **CA-009:** La pestaña activa se destaca con una línea inferior Aqua.

### RF-004 — Filtros avanzados
**Prioridad:** Media.

El botón de filtro abre un panel con fecha inicial/final, importe mínimo/máximo y selección múltiple de estados; acciones **Aplicar**, **Limpiar** y **Cancelar**.

**Reglas:** fecha inicial ≤ final; importe mínimo ≤ máximo; los criterios se combinan con búsqueda y pestaña activa (intersección de estados).

### RF-005 — Alta de pedido
**Prioridad:** Alta.

El botón `+` abre el formulario de nuevo pedido.

| Campo | Tipo | Obligatorio |
|---|---|---|
| Número | Generado automáticamente | Sí |
| Cliente | Texto | Sí |
| Fecha/hora | Automático | Sí |
| Productos | Lista | Sí |
| Cantidad | Entero positivo | Sí |
| Precio unitario | Importe | Sí |
| Subtotal | Calculado | Sí |
| Total | Calculado | Sí |
| Estado | Automático (`PENDIENTE`) | Sí |
| Observaciones | Texto | No |

**Reglas:** al menos un producto; cantidad > 0; número único; conservar precio histórico de cada producto; `subtotal = cantidad × precio unitario`; `total = suma de subtotales`. No se incluyen descuentos, impuestos adicionales ni cargos de servicio en esta versión.

**Aceptación:**
- **CA-010:** `+` abre el alta.
- **CA-011:** Guardar un pedido válido lo incorpora al listado.
- **CA-012:** El pedido creado inicia pendiente.
- **CA-013:** Se rechaza guardar sin productos.

### RF-006 — Detalle de pedido
**Prioridad:** Alta.

Presenta número, cliente, fecha/hora, estado, productos, cantidades, precios, subtotales, total y observaciones.

| Estado | Acciones |
|---|---|
| Pendiente | Editar, iniciar preparación, cancelar |
| En preparación | Completar, cancelar |
| Completado | Consultar |
| Cancelado | Consultar |

### RF-007 — Edición
**Prioridad:** Alta.

Solo se editan pedidos pendientes: cliente, productos, cantidades y observaciones. El número es inmutable, los importes se recalculan y se solicita confirmación al salir con cambios no guardados.

### RF-008 — Cambiar estado
**Prioridad:** Alta.

El detalle permite ejecutar únicamente las transiciones autorizadas en §2.2. Los cambios se reflejan en tarjetas e indicadores; transiciones inválidas se rechazan.

**Aceptación:**
- **CA-014:** Iniciar preparación cambia `PENDIENTE` a `EN_PREPARACION`.
- **CA-015:** Completar cambia `EN_PREPARACION` a `COMPLETADO`.
- **CA-016:** Un cancelado no vuelve a pendiente.

### RF-009 — Cancelación
**Prioridad:** Alta.

Desde pedidos pendientes o en preparación se solicita confirmación:

> ¿Estás seguro de que deseas cancelar este pedido? Esta acción no se puede deshacer.

Confirmar cambia a `CANCELADO` sin borrar el registro. No se cancelan pedidos completados.

### RF-010 — Resumen del día
**Prioridad:** Alta.

La sección **Resumen del día** muestra:

| Indicador | Definición |
|---|---|
| Pedidos | Todos los creados durante el día, incluidos cancelados. |
| Pendientes | Creados hoy cuyo estado actual es pendiente. |
| En preparación | Creados hoy cuyo estado actual es en preparación. |
| Completados | Creados hoy cuyo estado actual es completado. |

**Reglas:** usar fecha local del negocio; actualizar automáticamente después de altas y cambios de estado; no depender de filtros de listado. Los valores del mockup son ilustrativos.

## 4. Interfaz de usuario

### 4.1 Estructura
```text
┌─────────────────────────────────────┐
│ ☰            Pedidos             + │
├─────────────────────────────────────┤
│ [ Buscar pedidos...         ] [⚲]  │
│ Todos | Pendientes | Preparación... │
│ ┌─────────────────────────────────┐ │
│ │ #00025               Pendiente │ │
│ │ María López          $1,250.00 │ │
│ └─────────────────────────────────┘ │
│ ┌─────────────────────────────────┐ │
│ │ #00024          En preparación │ │
│ │ Carlos Ramírez          $850.00 │ │
│ └─────────────────────────────────┘ │
│ ┌─────────────────────────────────┐ │
│ │ Resumen del día                 │ │
│ │ Pedidos / Pend. / Prep. / Comp. │ │
│ └─────────────────────────────────┘ │
├─────────────────────────────────────┤
│ Inicio | Productos | Pedidos | ...  │
└─────────────────────────────────────┘
```

### 4.2 Paleta Aqua
| Token | Hex | Uso |
|---|---|---|
| AquaLv1 | `#A9E0D4` | Fondos suaves |
| AquaLv2 | `#5FC8B0` | Secundarios |
| AquaLv3 | `#3CAFA9` | Interactivos |
| AquaLv4 | `#2E8E8E` | Seleccionados |
| AquaLv5 | `#1A6B69` | Encabezados y principales |

Los colores de estado se implementarán como tokens semánticos adicionales.

### 4.3 Componentes Compose
| Elemento | Componente sugerido |
|---|---|
| Contenedor | `Scaffold` |
| Encabezado | `TopAppBar` |
| Búsqueda | `OutlinedTextField` |
| Filtros | `IconButton` |
| Pestañas | `ScrollableTabRow` |
| Listado | `LazyColumn` |
| Pedido | `Card` |
| Resumen | `Row` y `Card` |
| Navegación inferior | `NavigationBar` |
| Confirmación | `AlertDialog` |
| Fechas | `DatePicker` |

El resumen debe ser accesible mediante desplazamiento y no superponerse a la barra inferior. Se contemplarán estados de carga, vacío, sin resultados, error de lectura, error de guardado y éxito.

## 5. Modelo de dominio

```kotlin
data class Pedido(
    val id: Long,
    val numero: String,
    val cliente: String,
    val fechaCreacion: Instant,
    val fechaActualizacion: Instant,
    val estado: EstadoPedido,
    val total: BigDecimal,
    val observaciones: String?
)

enum class EstadoPedido {
    PENDIENTE, EN_PREPARACION, COMPLETADO, CANCELADO
}

data class PedidoDetalle(
    val id: Long,
    val pedidoId: Long,
    val productoId: Long,
    val nombreProducto: String,
    val cantidad: Int,
    val precioUnitario: BigDecimal,
    val subtotal: BigDecimal
)

data class ResumenPedidos(
    val totalPedidos: Int,
    val pendientes: Int,
    val enPreparacion: Int,
    val completados: Int,
    val cancelados: Int
)
```

Los importes se modelan con precisión decimal en dominio; en Room se recomienda persistir centavos como `Long`.

## 6. Arquitectura

```text
NombreDelProyecto/
├── app/
│   └── navigation/AppNavigation.kt
├── core/
│   ├── ui/{theme,components}/
│   └── common/
├── domain/
│   ├── model/{Pedido,PedidoDetalle,EstadoPedido}.kt
│   ├── repository/PedidoRepository.kt
│   └── usecase/pedidos/
│       ├── ObtenerPedidosUseCase.kt
│       ├── BuscarPedidosUseCase.kt
│       ├── CrearPedidoUseCase.kt
│       ├── ActualizarPedidoUseCase.kt
│       ├── CambiarEstadoPedidoUseCase.kt
│       └── ObtenerResumenDiarioUseCase.kt
├── data/
│   ├── database/
│   │   ├── entity/{PedidoEntity,PedidoDetalleEntity}.kt
│   │   └── dao/PedidoDao.kt
│   └── repository/PedidoRepositoryImpl.kt
└── feature/pedidos/
    ├── navigation/PedidosNavigation.kt
    ├── list/{PedidosScreen,PedidosViewModel,PedidosUiState}.kt
    ├── detail/{PedidoDetailScreen,PedidoDetailViewModel}.kt
    ├── form/{PedidoFormScreen,PedidoFormViewModel}.kt
    └── components/
        ├── PedidoCard.kt
        ├── PedidoStatusBadge.kt
        ├── PedidoSearchBar.kt
        ├── PedidoFilters.kt
        └── PedidosDailySummary.kt
```

**Responsabilidades:** Presentation renderiza UI; ViewModel mantiene estado; Domain aplica reglas; Repository abstrae acceso; Data implementa consultas y persistencia; Core UI ofrece componentes compartidos.

**Tecnologías:** `StateFlow`, coroutines, `collectAsStateWithLifecycle()`, Hilt.

```kotlin
data class PedidosUiState(
    val isLoading: Boolean = false,
    val pedidos: List<Pedido> = emptyList(),
    val searchQuery: String = "",
    val selectedStatus: EstadoPedido? = null, // null = Todos
    val resumen: ResumenPedidos? = null,
    val error: String? = null
)
```

Los filtros avanzados requieren un estado adicional.

## 7. Persistencia

### 7.1 Tabla `pedidos`
| Columna | Tipo | Restricción |
|---|---|---|
| id | INTEGER | PRIMARY KEY |
| numero | TEXT | UNIQUE NOT NULL |
| cliente | TEXT | NOT NULL |
| fecha_creacion | INTEGER | NOT NULL |
| fecha_actualizacion | INTEGER | NOT NULL |
| estado | TEXT | NOT NULL |
| total_centavos | INTEGER | NOT NULL |
| observaciones | TEXT | NULL |

### 7.2 Tabla `pedido_detalle`
| Columna | Tipo | Restricción |
|---|---|---|
| id | INTEGER | PRIMARY KEY |
| pedido_id | INTEGER | FOREIGN KEY |
| producto_id | INTEGER | NOT NULL |
| nombre_producto | TEXT | NOT NULL |
| cantidad | INTEGER | NOT NULL |
| precio_unitario_centavos | INTEGER | NOT NULL |
| subtotal_centavos | INTEGER | NOT NULL |

Las altas y modificaciones del pedido con sus detalles serán transaccionales. La asignación del consecutivo deberá ser única incluso con operaciones concurrentes.

## 8. Navegación

| Ruta | Destino |
|---|---|
| `pedidos` | Listado |
| `pedidos/nuevo` | Alta |
| `pedidos/{id}` | Detalle |
| `pedidos/{id}/editar` | Edición |

**Alta:** Listado → `+` → Alta → Guardar → Listado actualizado.

**Consulta:** Listado → Pedido → Detalle → Editar / Cambiar estado / Cancelar (según estado).

## 9. Requisitos no funcionales

| ID | Requisito |
|---|---|
| RNF-001 | Cargar el listado en menos de 2 segundos con hasta 1,000 pedidos locales en dispositivo de prueba definido. |
| RNF-002 | Buscar sin bloquear la interfaz. |
| RNF-003 | Renderizar el listado con `LazyColumn`. |
| RNF-004 | No ejecutar consultas a BD en el hilo principal. |
| RNF-005 | Evitar recomposiciones innecesarias. |
| RNF-006 | Seguir Material Design 3. |
| RNF-007 | Mantener tamaños táctiles adecuados. |
| RNF-008 | Identificar estados por texto e iconos además de color. |
| RNF-009 | Adaptarse a distintos tamaños de pantalla. |
| RNF-010 | Respetar tamaños de fuente del sistema. |
| RNF-011 | Impedir pedidos sin productos. |
| RNF-012 | Evitar errores de precisión monetaria. |
| RNF-013 | Validar transiciones de estado. |
| RNF-014 | Realizar altas y modificaciones transaccionales. |
| RNF-015 | Persistir datos entre reinicios. |
| RNF-016 | Mantener lógica de negocio fuera de Composables. |
| RNF-017 | Definir interfaces de repositorio. |
| RNF-018 | Reutilizar componentes visuales. |
| RNF-019 | Incluir pruebas unitarias de reglas de negocio. |
| RNF-020 | Evitar acoplamiento del dominio a Room. |

## 10. Casos de uso

| ID | Caso de uso | Prioridad |
|---|---|---|
| CU-001 | Consultar listado | Alta |
| CU-002 | Buscar pedido | Alta |
| CU-003 | Filtrar por estado | Alta |
| CU-004 | Aplicar filtros avanzados | Media |
| CU-005 | Registrar pedido | Alta |
| CU-006 | Consultar detalle | Alta |
| CU-007 | Editar pedido | Alta |
| CU-008 | Iniciar preparación | Alta |
| CU-009 | Completar pedido | Alta |
| CU-010 | Cancelar pedido | Alta |
| CU-011 | Consultar resumen diario | Alta |

## 11. Plan de pruebas

| ID | Escenario | Resultado esperado |
|---|---|---|
| TEST-001 | Abrir módulo | Listado visible. |
| TEST-002 | Buscar número | Coincidencias correctas. |
| TEST-003 | Buscar cliente | Coincidencias correctas. |
| TEST-004 | Filtrar pendientes | Solo pendientes. |
| TEST-005 | Crear pedido válido | Se guarda. |
| TEST-006 | Crear sin productos | Se impide guardar. |
| TEST-007 | Editar pendiente | Cambios persistidos. |
| TEST-008 | Editar completado | Edición no disponible. |
| TEST-009 | Iniciar preparación | Estado actualizado. |
| TEST-010 | Completar | Estado actualizado. |
| TEST-011 | Cancelar | Se solicita confirmación. |
| TEST-012 | Consultar resumen | Indicadores correctos. |
| TEST-013 | Reiniciar aplicación | Datos persistentes. |
| TEST-014 | Filtros sin coincidencias | Estado vacío. |
| TEST-015 | Altas concurrentes | Consecutivos únicos. |
| TEST-016 | Transición inválida | Operación rechazada. |

## 12. Dependencias e integración

| Módulo | Integración |
|---|---|
| Productos | Catálogo de productos para seleccionar. |
| Core UI | Tema y componentes. |
| Domain | Modelos y reglas. |
| Data Database | Persistencia Room. |
| Navigation | Rutas y navegación. |
| Hilt | Inyección de dependencias. |
| Cierre de Lote | Contrato futuro de consulta de operaciones; el cierre se implementa en su módulo. |

## 13. Etapas de implementación

| Etapa | Alcance | Prioridad | Estado |
|---|---|---|---|
| 1. Consulta | Listado, búsqueda, pestañas, tarjetas, resumen diario. | Alta | Pendiente |
| 2. Gestión | Alta, detalle, edición, productos, importes. | Alta | Pendiente |
| 3. Estados | Transiciones, cancelación, validaciones, indicadores reactivos. | Alta | Pendiente |
| 4. Optimización | Filtros avanzados, rendimiento, pruebas de integración. | Media | Pendiente |

**Entregables:**
- **Etapa 1:** Pantalla principal funcional, repositorio de lectura y resumen.
- **Etapa 2:** Formularios, detalle, selección de productos y cálculo de importes.
- **Etapa 3:** Máquina de estados, confirmaciones y actualización reactiva.
- **Etapa 4:** Filtros avanzados, pruebas UI y validación de rendimiento.

## 14. Criterios generales de aceptación

El módulo estará funcionalmente completo cuando:
1. Muestre pedidos persistidos ordenados correctamente.
2. Permita buscar por número y cliente.
3. Filtre correctamente por estado.
4. Registre pedidos con uno o varios productos.
5. Edite únicamente pedidos pendientes.
6. Muestre el detalle de cualquier pedido.
7. Respete las transiciones permitidas.
8. Solicite confirmación para cancelar.
9. Calcule correctamente el resumen diario.
10. Conserve datos tras reiniciar.
11. Respete el mockup y la paleta Aqua.
12. Se integre en la arquitectura multimódulo.

## 15. Decisiones pendientes de validación

| ID | Pregunta | Propuesta |
|---|---|---|
| DP-001 | ¿Cliente obligatorio? | Sí. |
| DP-002 | ¿Descuentos? | No en versión inicial. |
| DP-003 | ¿Impuestos adicionales? | No en versión inicial. |
| DP-004 | ¿Editar durante preparación? | No. |
| DP-005 | ¿Cancelar durante preparación? | Sí. |
| DP-006 | ¿Eliminar pedidos? | No. |
| DP-007 | ¿Funcionamiento offline? | Sí, Room. |
| DP-008 | ¿Sincronización remota? | Fuera de alcance inicial. |
| DP-009 | ¿Historial de estados? | Registrar fecha y hora de cada transición. |
| DP-010 | ¿Cancelados dentro del total diario? | Sí. |

---

**Nota:** Los comportamientos no visibles en el mockup (alta, edición, filtros avanzados y transiciones) son propuestas de alcance inicial sujetas a validación. Este SRS puede servir como base para elaborar `spec.md`, `plan.md` y `tasks.md` con GitHub Spec Kit.

# UI Contract: Pedidos

**Date**: 2026-10-21 | **Related**: [spec.md](../spec.md)

## Host y navegación

- app conserva el gate de configuración inicial. Destinos Home, Products, ProductEdit, BusinessConfig, Orders, OrderNew, OrderDetail(id) y OrderEdit(id) se representan con tipos guardables; no combinaciones de booleanos.
- Entrada desde tarjeta Pedidos de Inicio y navegación inferior Inicio/Productos/Pedidos. Los controles hacia funcionalidades fuera de alcance no fingen acciones disponibles. El menú de encabezado expone Inicio, Productos y Configuración usando los mismos callbacks del host.
- Orders → + → OrderNew → éxito → Orders actualizado. Orders → tarjeta → OrderDetail → Editar → OrderEdit → éxito → OrderDetail actualizado.
- Back desde detalle regresa a listado; desde formulario solicita descarte si está modificado, si no vuelve al origen. Cancelar descarte mantiene texto y líneas. Cambiar sección desde formulario aplica la misma regla.
- Guardar destino/back stack e ids; ViewModel por destino/identidad. SavedStateHandle conserva campos compactos, token y referencias/snapshots de líneas del borrador para restauración, sin imágenes ni catálogo completo. No almacenar PII en logs o herramientas de diagnóstico. Al completar alta, consumir navegación de éxito una vez y retirar su borrador.
- Restaurar formulario no vuelve a crear un token ni sobrescribe entradas con una nueva carga. Si el pedido dejó de ser editable mostrar conflicto y ofrecer recargar/volver; no forzar guardado.

## Listado

Estado inmutable `OrdersUiState`: carga, tarjetas, consulta aplicada, borrador del panel, resumen independiente, errores por sección y estado de reintento. La carga fallida no se transforma en vacío. Si se retienen resultados previos, marcarlos como desactualizados hasta reintento exitoso. Permitir que el resumen falle sin ocultar un listado correcto y viceversa.

- Encabezado Pedidos, menú y +; búsqueda «Buscar pedidos...» y botón de filtros.
- Todos/Pendientes/En preparación/Completados/Cancelados: exclusiva, scroll horizontal, subrayado Aqua; selección inmediata. Búsqueda con debounce 300 ms y cancelación de consulta anterior; pestaña/filtros sin debounce.
- Tarjeta: # + número con al menos cinco dígitos; cliente, fecha/hora local, estado con texto/icono/color y total MXN con dos decimales. La tarjeta entera abre detalle.
- Distinguir base vacía de consulta sin coincidencias. Consulta vacía muestra «No se encontraron pedidos»; sin registros muestra invitación a crear. Determinar existencia global mediante conteo independiente, no inferirla solo del resultado filtrado.
- Resumen accesible al final de la lista, sin superposición con barra inferior: cuatro indicadores, incluido total con cancelados; no tomar conteos del listado filtrado.
- En error, mensaje y Reintentar reinicia la colección fallida; no requiere reiniciar app. Mantener consulta al regresar del detalle y actualizar resultados al cambiar datos.

## Panel de filtros

Fecha inicial/final de creación, importe mínimo/máximo, estados múltiples. Campos opcionales. Al abrir copiar criterios aplicados a borrador; Cancelar desecha borrador; Limpiar vacía campos del panel; Aplicar valida y sustituye filtros avanzados. Búsqueda y pestaña no cambian al limpiar el panel. Rangos invertidos muestran error junto al campo; no se cierra ni se altera consulta aplicada. Estados avanzados vacíos no restringen; intersección sin estados muestra cero resultados.

DatePicker devuelve fecha civil mediante adaptador; fechas final e inicial incluyen todo su día local. Importes admiten separador decimal local, máximo dos decimales, sin separadores de miles ni negativos; un valor inválido se señala, no se redondea silenciosamente. Mínimo cero permitido.

## Formulario

Estado `OrderFormState`: carga inicial, cliente, líneas identificadas, observaciones, errores por campo, dirty, saving, error recuperable y diálogo de descarte. Subtotales/total provienen de domain; el formulario no permite editar precio, número, estado ni fechas automáticas.

- Selector muestra productos activos; incorpora snapshot calculado por caso de uso. Sin catálogo activo, explicar y ofrecer abrir Productos con protección de cambios sin guardar.
- Cantidad se captura como texto para poder representar entradas vacías/inválidas; se valida como Int positivo. Cliente trim no vacío; observaciones opcionales.
- Edición conserva líneas históricas incluso de productos eliminados/inactivos; permite cambiar cantidad o retirar. Agrupación por producto/precio; distinto precio queda separado.
- Guardar deshabilitado durante envío; un segundo evento no inicia otra solicitud. Error conserva entradas/token. ProductUnavailable identifica la línea que se debe retirar o sustituir; no la elimina automáticamente.
- Conflicto conserva borrador y explica que el pedido cambió; Recargar requiere confirmar descarte si dirty. Datos guardados son autoridad.

## Detalle y estados

Número, cliente, creación/actualización, estado, productos/cantidades/precios/subtotales, total y observaciones. Acciones según FR-012. No hay botón de eliminar.

Cancelar abre: «¿Estás seguro de que deseas cancelar este pedido? Esta acción no se puede deshacer.» Confirmar ejecuta una vez; rechazar no cambia nada. Completar solo en preparación; iniciar solo pendiente. Historial queda persistido para consulta futura; no se añade una pantalla de historial como requisito nuevo.

## Accesibilidad y estilo

- Aqua niveles 1–3 existentes; tokens acotados a Pedidos para niveles 4 #2E8E8E y 5 #1A6B69. Estados amarillo/reloj, azul/gorro, verde/verificación, rojo/cruz; texto siempre presente, iconos con semántica no redundante.
- Controles ≥48 dp, nombres accesibles, orden de foco coherente, fuente 200%, pantallas pequeñas, orientación y scroll de formularios/pestañas/resumen. Insets de teclado y barra inferior sin ocultar Guardar o último indicador.
- Tags estables: orders_screen, orders_search, orders_filters, orders_tab_<estado>, order_card_<id>, orders_empty, orders_no_results, orders_retry, orders_summary, order_form, order_save, order_detail, order_cancel_confirm. Usar datos sintéticos en pruebas.

## Aceptación

Cada escenario US1–US6 de spec debe tener prueba de presentación o recorrido de integración. SC-003 requiere además observación con usuarios; SC-001/002 requieren mediciones, no solo assertions funcionales. Pruebas de UI no sustituyen los tests transaccionales del repositorio.

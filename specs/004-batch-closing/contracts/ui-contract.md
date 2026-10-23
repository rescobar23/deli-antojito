# UI Contract: Consulta preliminar de Cierre de Lote

**Date**: 2026-10-23

## Entrada y navegación

- Destino principal guardable `BatchClosing`, clave `close`. Habilitar nav_close, corregir decodeDestination y selección de barra; botón Atrás vuelve a Inicio.
- HomeView/HomeContent reciben onBatchClosingClick; conectar home_close_button y entrada de menú. Cambiar subtítulo a «Consulta preliminar de pedidos».
- Conservar configuración inicial obligatoria y destinos existentes. No migrar a otro sistema de navegación.
- Route obtiene VM por DI; Screen recibe estado inmutable y callbacks. Feature no importa clases de data ni de orders.

## Contenido visible

1. Título «Cierre de Lote» y banner «Consulta preliminar. El cierre definitivo estará disponible cuando se registren pagos».
2. «Pedidos completados por fecha de creación» y zona horaria utilizada.
3. Un campo «Fecha de consulta» con calendario y formato dd/MM/yyyy, sin hora ni campo de fecha de fin.
4. Indicadores «Pedidos completados» y «Importe bruto de pedidos»; aclaración «No representa cobros confirmados».
5. Descuentos y total neto: «No disponible — Aún no se registran pagos y descuentos».
6. Efectivo, Tarjeta, Transferencia y Otros: «No disponible», sin importes ni porcentajes, con causa compartida visible/accesible.
7. Top 5: posición, nombre histórico, unidades y bruto de líneas. En compacto puede renderizarse como filas/tarjetas sin desplazamiento horizontal.
8. Reintentar/Actualizar consulta como acciones de lectura. Sin botón Cerrar lote, confirmación de cierre, notas ni historial.

## Selección temporal

Por defecto se selecciona hoy. Un calendario permite seleccionar otro día; no se editan horas, minutos, segundos ni el fin. Para días pasados la consulta abarca medianoche local hasta la medianoche siguiente (exclusiva), respetando días de duración variable. Para hoy el fin es el instante de consulta. Cancelar conserva selección; aplicar una fecha válida inicia recálculo. Fechas futuras o inválidas muestran error y no consultan.

SavedStateHandle conserva el día civil. Al restaurar, reanudar o actualizar se recalculan sus límites en la zona local y se vuelve a consultar; actualizar hoy incluye los pedidos nuevos hasta ese momento. La interfaz no solicita offsets ni resolución manual de horas.

## Estados y acciones

| Estado | Renderizado | Acciones |
| --- | --- | --- |
| Loading | Indicador de carga, sin cifras anteriores como vigentes | Editar período; solicitudes previas se cancelan/descartan |
| InvalidPeriod | Error asociado al extremo y explicación; sin resultado vigente | Corregir/Cancelar edición |
| Empty | Cero pedidos, bruto $0.00, Top5 vacío | Editar/Actualizar |
| Ready | Resumen y Top5 de la misma respuesta | Editar/Actualizar |
| Error | Mensaje comprensible, sin stacktrace; período conservado | Reintentar/Editar |

No disponible financiero se mantiene en todos los estados y no se usa como sinónimo de Empty/Error. Error de integridad: «No se puede calcular la consulta porque hay datos de pedidos inconsistentes»; falla de lectura: «No se pudo consultar. Intenta nuevamente».

## Accesibilidad y pruebas de interacción

- Etiquetas accesibles para fecha y botones; estados anunciados semánticamente, no solo por color. Targets interactivos mínimos48dp, foco coherente y contenido legible al200% de fuente.
- Dos indicadores disponibles pueden ir en una fila amplia o columna compacta; no forzar cuatro tarjetas financieras como si fueran valores válidos.
- Test tags propuestos: batch_closing_screen, batch_preliminary_notice, batch_period_start (única fecha), batch_date_picker, batch_timezone, batch_period_error, batch_loading, batch_empty, batch_retry, batch_order_count, batch_gross_amount, batch_financial_unavailable, batch_top_product_{id}.
- Mantener home_close_button/nav_close. Probar entrada desde ambos, selección guardable, Atrás, rotación, reanudación, fallos, reintento, cambios rápidos, accesibilidad y ausencia de acciones financieras.

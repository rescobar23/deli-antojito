# Pedidos

Módulo de presentación local. Depende de domain y core; no accede directamente a Room ni a feature:product.

## Estado de implementación

Implementadas las pantallas de listado, detalle y formulario, filtros avanzados, estados y resumen diario; acceso desde Inicio y barra inferior. El seguimiento de verificaciones pendientes está en specs/003-order-module/tasks.md y validation/. No se atribuye validación en dispositivo sin evidencia.

## Contratos

Consultar specs/003-order-module/contracts/. Listado → alta → listado; listado → detalle → edición solo pendiente. PENDIENTE → EN_PREPARACION → COMPLETADO, o cancelación desde los dos primeros.

StateFlow inmutable, callbacks y colección ligada al lifecycle. Errores tipados; conservar entradas ante fallos. Nunca registrar cliente, observaciones, token o imágenes. Tests con datos sintéticos y tags orders_screen, order_form, order_detail. La identidad de entrada restaura el borrador y evita reutilizar el token al comenzar otro pedido; lista/detalle/formulario tienen estados separados.

Los importes son Money en centavos. El snapshot se captura al incorporar un producto activo y se conserva aunque el catálogo cambie o se borre. Room v4 confirma cabecera/líneas/secuencia o cabecera/historial dentro de una transacción. Revisión obsoleta produce conflicto y requiere recargar; cancelar un pedido nunca lo elimina.

## Verificación

Desde la raíz: .\gradlew.bat :feature:orders:testDebugUnitTest :feature:orders:lintDebug. Las pruebas instrumentadas requieren dispositivo: :feature:orders:connectedDebugAndroidTest.

Cobertura: :feature:orders:createDebugUnitTestCoverageReport y :domain:ordersDomainCoverage. Ver specs/003-order-module/quickstart.md para el harness release de rendimiento y la matriz de aceptación.


# Revisión estática de privacidad y arquitectura — 2026-10-21

- domain contiene modelos, políticas e interfaces sin imports Android/Room/Hilt en los archivos nuevos.
- data implementa las interfaces con Room y el adaptador Android de jornada; feature:orders depende de domain/core, sin dependencia de data ni feature:product.
- app conserva el gate inicial y compone navegación/DI. No se añadieron servicios remotos, credenciales ni permisos de red.
- SQL de producción parametrizado. Las claves de búsqueda son derivadas locales; nunca se incluyen textos de usuario en SQL concatenado.
- Logs nuevos: solo categoría de operación y nombre de clase de excepción. No contienen cliente, notas, token, imágenes ni mensajes SQL.
- Los errores públicos no exponen Throwable. CancellationException se propaga y las escrituras fallidas revierten la transacción.
- Los datos de formulario se guardan compactos para restauración dentro del estado local autorizado; no se registra catálogo ni imágenes en SavedStateHandle.
- Los fixtures y pruebas usan datos sintéticos y bases con nombres aislados; no borran la base operativa.

Esta revisión es estática. La inspección de logs en dispositivo y las pruebas de persistencia quedan en los gates pendientes, sin atribuir ejecución.


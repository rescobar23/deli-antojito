# Consulta preliminar de Cierre de Lote

Solo lectura de pedidos actualmente COMPLETADO por fecha de creación [inicio, fin). Importes MXN históricos, no cobros confirmados. Sin pagos, notas, lotes, historial ni migraciones. Depende solo de domain/core; app compone navegación y DI.

Validación: specs/004-batch-closing/quickstart.md. Las pruebas conectadas y sesiones reales no se sustituyen por compilar sus APK.

### Estado de verificación

Consulta selecciona estado actual COMPLETADO por fecha de creación, usa una única lectura y muestra resumen/ranking juntos. El calendario selecciona un solo día, sin hora ni fecha de fin. Los días pasados abarcan desde su medianoche local hasta la siguiente (fin exclusivo); hoy se consulta hasta el instante de actualización. SavedStateHandle conserva el día civil; reanudar o actualizar recalcula sus límites en la zona local, respetando cambios de horario. No se permiten fechas futuras.

Pruebas: unit/build/lint y cobertura en validation de la feature. Ejecutar las suites conectadas de app y feature de forma secuencial (`--no-parallel`), porque comparten el dispositivo y crean actividades Compose. La medición release usa solo fixtures sintéticos en base separada. API25, revisión TalkBack y sesiones de cinco responsables requieren evidencia adicional.

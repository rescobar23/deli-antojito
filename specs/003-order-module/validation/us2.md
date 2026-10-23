# US2 — alta

Implementados captura activa, snapshots, dinero exacto, agrupación, token estable restaurable y alta transaccional. Los números no reinician por jornada ni se truncan; existe control de agotamiento. El formulario protege el doble envío y conserva borrador ante fallo.

Los tests incluyen $46.30, validación y restauración en JVM, más transacciones reales preparadas para duplicados/concurrencia, agotamiento, rollback, producto no disponible y reapertura en base física aislada.

T035 pendiente: ejecutar esos escenarios Room y el recorrido offline en Android. Compilar un APK de tests no acredita atomicidad observada. Ver [test-results.md](test-results.md).


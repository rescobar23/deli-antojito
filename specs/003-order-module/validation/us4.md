# US4 — estados

Implementadas exactamente cuatro transiciones autorizadas. La política de dominio se prueba sobre las 16 parejas; la escritura condicional y el historial comparten transacción. Completado/cancelado son terminales y no hay eliminación en el contrato de repositorio ni en UI.

Cancelación usa el texto de confirmación del SRS. Pruebas de UI y de historial/rollback/concurrencia preparadas; resultados JVM/compilación en [test-results.md](test-results.md).

T051 pendiente: ejecutar historial persistido, reapertura y escenarios simultáneos sobre Android.


# US1: aceptación del incremento — 2026-10-23

PASS funcional en API35 para los seis escenarios de US1, apoyado en pruebas JVM/Room/UI y navegación app. Ver us1-unit.md, us1-connected.md y build-and-tests.md.

| Requisito | Evidencia |
| --- | --- |
| FR003/004, SC005 | ValidateCierrePeriodoUseCaseTest; CierreConsultaRepositoryTest; VM invalidación/reanudación |
| FR008/010, SC002 | CalculateConsultaCierreUseCaseTest: fixture contractual2 pedidos/500 centavos; cabeceras únicas; vacío/integridad/overflow |
| FR014 | VM lateCancelledResponseCannotOverwriteNewPeriod, readyPublishesSummaryAndRankingTogether y editor conservado |
| FR021 | VM error/reintento y Compose error/vacío/período |
| Acceso | BatchClosingNavigationStateTest y pruebas app de tarjeta/barra |

La aceptación del incremento API35 no elimina el gate adicional API25 de T026 ni la validación completa con usuarios. Sin pagos/cierres ni modificaciones de operaciones.

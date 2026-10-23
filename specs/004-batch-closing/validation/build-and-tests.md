# JVM y compilación — 2026-10-23

Comprobación general en gradle-final-checks.log y revisión final de DI por constructor en gradle-final-di-checks.log; regresiones de módulos existentes en gradle-performance-release.log. BUILD SUCCESSFUL; APK debug y APK instrumentados app/data/batch-closing preparados. No cambios a AppDatabase v4, entidades, migraciones ni índices.

| Módulo | Pruebas JVM | Fallos/errores |
| --- | ---: | ---: |
| domain | 63 | 0 |
| data | 32 | 0 |
| feature:batch-closing | 11 | 0 |
| app | 4 | 0 |
| feature:orders | 14 | 0 |
| feature:product | 14 | 0 |
| feature:business-config | 9 | 0 |

Total147. Las pruebas incluyen snapshots, integridad monetaria/overflow, selección/errores, mapper, resolver de gap/overlap, SavedStateHandle, cancelación y respuesta tardía, editor preservado y destino guardable. Las pruebas de ranking fallaron antes de implementar su agregación; las iniciales de dominio/VM fallaron por clases aún ausentes.

Lint domain/data/batch-closing/home: cero incidencias. App: cero errores y dos warnings preexistentes LockedOrientationActivity/DiscouragedApi por MainActivity en portrait (AndroidManifest.xml ya contenía esa restricción en HEAD). Propietario: implementador del proyecto; revisión2026-10-30; riesgo: orientación fija/letterboxing en otros formatos. Sin supresiones nuevas. La deuda de iconos core/ui conserva revisión2026-10-28 del plan de Pedidos.

Se corrigió StateFlowValueCalledInComposition usando estado inicial recordado y observación repeatOnLifecycle. Un intento simultáneo de Gradle produjo un bloqueo de classes.jar de Windows; se resolvió repitiendo las comprobaciones secuencialmente. No se atribuye ese fallo a comportamiento de la aplicación.


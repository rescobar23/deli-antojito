# Resultados de verificación — 2026-10-21

## Pruebas JVM

Los XML JUnit revisados registran 96 tests, cero fallos, cero errores y cero omitidos:

| Módulo | Tests | Fallos |
|---|---:|---:|
| domain | 37 | 0 |
| data | 19 | 0 |
| feature:orders | 14 | 0 |
| app | 3 | 0 |
| feature:product | 14 | 0 |
| feature:business-config | 9 | 0 |

Incluyen validación monetaria y overflow, Unicode/filtros, política completa de estados, calendario con DST/medianoche omitida, restauración y token de formulario, conflictos y estados de presentación. Las transacciones Room requieren Android y no están incluidas en este total JVM.

## Cobertura

- Dominio tocado, consolidando ejecuciones de domain y feature:orders: 133 líneas cubiertas /148, **89.86%**.
- ViewModels de Pedidos, incluyendo sus clases de coroutines generadas: 228 /255, **89.41%**.
- Ambos superan la meta de 70%. Se incluyen Money/Pedido/Estado/Resumen, contratos temporales y usecase/pedido; no se usa cobertura de código ajeno para elevar el porcentaje.
- Informes reproducibles: `:domain:ordersDomainCoverage` y `:feature:orders:createDebugUnitTestCoverageReport`. XML bajo domain/build/reports/jacoco/ordersDomainCoverage/ y feature/orders/build/reports/coverage/test/debug/.

## Compilación y análisis

La verificación conjunta terminó con BUILD SUCCESSFUL. Comandos ejecutados: unit tests de la tabla, informes de cobertura, :app:assembleDebug, :data:assembleDebugAndroidTest, :feature:orders:assembleDebugAndroidTest, :app:assembleDebugAndroidTest y lintDebug de app/data/domain/orders/home/core:ui/core:common. Después se validó el ajuste de carreras asíncronas y sus dos tests nuevos, también con BUILD SUCCESSFUL. Logs locales: build/orders-validation.log y build/orders-final-validation.log (excluidos de Git).

El empaquetado final debug y lint de la última interfaz terminaron con BUILD SUCCESSFUL (build/orders-packaging.log). También pasaron `-PordersPerformance=true :app:assembleRelease :app:assembleReleaseAndroidTest`, incluido lintVitalRelease (build/orders-performance-build.log). Esa variante release está firmada para pruebas locales, no para distribución. El fixture v1 de configuración conserva AUTOINCREMENT conforme a su entidad histórica en Git.

App/data/domain/orders/home/core:common: lint sin errores ni warnings. core:ui: cero errores y 32 warnings preexistentes de iconos (21 IconXmlAndPng, 5 IconLauncherShape y 6 IconDuplicates), documentados con responsable/vencimiento en plan.md y maintainability.md. No hay supresiones nuevas.

Correcciones comprobadas durante el proceso:

- Money: cinco fallos iniciales contra esqueleto y posterior aprobación.
- Calendario: fallo reproducido para medianoche omitida y corrección con primera hora válida del día civil.
- Lint detectó una lectura de StateFlow.value en composición; se movió a inicialización recordada y colección observable ligada al lifecycle.
- El harness de rendimiento necesitó Room en androidTestImplementation de app; usa las mismas versiones catalogadas, sin dependencia de producción nueva.
- Dos regresiones adicionales protegen el cambio de entrada de formulario ante cargas/selecciones tardías. Guardar queda bloqueado mientras se captura un producto; respuestas de una entrada anterior no modifican la nueva.

## No ejecutado

ADB no tiene dispositivos y no hay AVD configurado. Los APK instrumentados se preparan/compilan, pero no se han ejecutado pruebas Room, UI, navegación real, restauración/accesibilidad en API 25/35 ni rendimiento. El harness de rendimiento se omite intencionalmente en debug y requiere el comando release del quickstart. No se realizaron sesiones con usuarios.

Ver [release-readiness.md](release-readiness.md) y [acceptance.md](acceptance.md) para gates pendientes, responsable y seguimiento. El usuario autorizó continuar sin dispositivo; no equivale a aprobar esos gates.

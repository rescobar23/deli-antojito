# Quickstart: Implementación y validación de consulta Cierre de Lote

**Date**: 2026-10-23 | **Plan**: [plan.md](plan.md)

Este documento define verificaciones futuras. La fase de planificación no ejecutó builds, pruebas, mediciones ni sesiones de usuarios.

## Entorno

PowerShell en raíz del repositorio, JDK17, Android SDK/API35 y wrapper existente. minSdk25. No instalar nuevas bibliotecas. Consultar plan de Pedidos para infraestructura de pruebas vigente; no confundir sus APK preparados con evidencia de esta nueva feature.

Antes de implementar, leer spec, research, data-model y contratos. Ejecutar /speckit-tasks para crear tareas con pruebas primero por historia. La planificación no crea tasks.md ni código Kotlin.

## Comandos de implementación futura

Estos comandos presuponen que el módulo y sus pruebas se hayan creado:

```powershell
.\gradlew.bat :domain:testDebugUnitTest :data:testDebugUnitTest :feature:batch-closing:testDebugUnitTest :app:testDebugUnitTest
.\gradlew.bat :app:assembleDebug :data:assembleDebugAndroidTest :feature:batch-closing:assembleDebugAndroidTest :app:assembleDebugAndroidTest
.\gradlew.bat :domain:lintDebug :data:lintDebug :feature:batch-closing:lintDebug :feature:home:lintDebug :app:lintDebug
.\gradlew.bat :data:connectedDebugAndroidTest --no-parallel
.\gradlew.bat :feature:batch-closing:connectedDebugAndroidTest --no-parallel
.\gradlew.bat :app:connectedDebugAndroidTest --no-parallel
```

Ejecutar también pruebas de regresión existentes de Pedidos, Productos y configuración si sus recorridos/contratos se tocan. En nueva feature habilitar enableUnitTestCoverage; implementar informe JaCoCo específico `:domain:batchClosingDomainCoverage` que incluya datos de tests del nuevo módulo y filtre clases nuevas/modificadas; informe VM de feature separado. Ese task aún no existe y no basta ejecutar ordersDomainCoverage, cuyos filtros excluyen cierre.

Ejecutar cada comando conectado solo cuando haya terminado el anterior: las suites app/feature abren actividades en el mismo dispositivo. La ejecución conjunta mostró un fallo de host Compose; la ejecución aislada aprobó. No cambiar applicationId, test runner global, migraciones ni configuración de distribución para ejecutar pruebas. Para medición release puede reutilizarse explícitamente `-PordersPerformance=true` del harness actual de app, si las pruebas de Cierre se alojan en app/src/androidTest; mantener firma debug solo en esa variante de medición y documentar su uso.

## Matriz de aceptación y datos

| Grupo | Casos mínimos | Evidencia |
| --- | --- | --- |
| US1 / FR003–004 / SC005 | Inicio incluido, fin excluido, inicio>=fin, futuro; completado creado fuera y completado dentro; creado dentro y completado después; otros3 estados excluidos | Unit+Room+UI |
| US1 / FR008/010 | Cero filas, varias líneas sin multiplicar total de header, subtotal/header inconsistentes, cantidades inválidas y overflow | Unit+Room |
| US1 / FR014/021 | Cambios rápidos con respuesta antigua tardía, error/recuperación, período conservado, reanudación, proceso recreado | VM+UI |
| US2 / FR011 | Más/menos de5, empate cantidad/importe, ID ascendente, nombres históricos distintos, producto renombrado/desactivado/borrado, producto real Otros | Unit+Room+UI |
| US3 / FR005/009/013 | No disponible en Ready/Empty/Error, sin valores/porcentajes simulados, sin cierre/notas/historial | UI |
| US3 / FR015/019 / SC003 | Comparar todas las tablas antes/después de consultar/reintentar/navegar; schema v4 idéntico; sin cambios en secuencia | Room integración |
| Navegación / FR001 | Tarjeta y barra→close seleccionado, Atrás→Inicio, restore destino, gate configuración inicial, Productos/Pedidos/Config accesibles | App unit+UI |
| Tiempo / FR003 | Zona del dispositivo, API25, medianoche, días23/25h, hora inexistente y repetida con elección de offset, cambio de zona/reloj | Adaptador unit+UI |
| Accesibilidad / FR020 | Ancho320dp y amplio600dp, fuente100%/200%, TalkBack/foco/etiquetas, sin desbordamiento horizontal | UI+manual |

Fixture base contractual en contracts/domain-contract.md: [1000,2000), now3000, resultado2 pedidos y500 centavos. Añadir una cabecera sin líneas para verificar InvalidData y un pedido cuya transición a COMPLETADO ocurra después del fin. Para consistencia durante concurrencia, alternar transacción de cambio de estado y consulta y verificar que resumen/Top5 corresponden siempre al mismo conjunto completo.

## Rendimiento SC-001

Baseline de ejecución disponible (2026-10-23): dispositivo físico TB330FU, Android15/API35, arm64-v8a, memoria reportada8020276kB y pantalla1200×1920; fuente100%, build release de medición sin depurador. El emulador Pixel2/2GB/2vCPU inicialmente propuesto no está configurado; sus resultados no se presuponen equivalentes. Registrar host, CPU, SDK/emulador, zona, commit, variante y estado térmico cuando aplique. No extrapolar medición JVM al dispositivo.

Dataset reproducible: 1,000 pedidos con10 líneas cada uno y100 productos, los cuatro estados y fechas repartidas antes/dentro/después. Preparar un período que incluya los1,000 como COMPLETADO para medir el peor conjunto de lectura; otro mixto verifica selección. 5 calentamientos y30 cambios de período medidos desde selección válida hasta resumen y Top5 completamente visibles. Reportar todas las mediciones, máximo y p95; SC001 exige <2s en las30 mediciones válidas, no solo promedio. Probar funcionalidad adicional en API25; no declarar ese dispositivo equivalente para latencia.

## Usabilidad SC-006

Cinco responsables representativos reciben la tarea de consultar un período y explicar si el importe representa dinero cobrado. Registrar éxito sin ayuda y tiempo de cada sesión; al menos4/5 completan ambas tareas en<2min. Pedir consentimiento para registrar resultados sin nombres/clientes/notas reales; fixtures sintéticos. No sustituir sesiones por tests automatizados.

## Evidencia de salida y gates

Durante implementación crear `validation/` dentro de esta feature con resultados unit/Room/UI, cobertura, rendimiento y aceptación; registrar comandos, fechas, dispositivo, conteos y fallos. No crear informes que afirmen ejecución a partir de este plan.

Gates: build, unit, cobertura≥70% dominio/VM tocados, integración de lectura coherente/no escrituras, UI y navegación, lint sin avisos nuevos no gestionados, SC001 y SC006 verificables. Revisar diff de esquema: ningún cambio v4, ningún lote/pago/nota persistido. API públicas documentadas y README de feature.

Si no hay dispositivo, completar unit/build/lint e instrumentados compilables; mantener Room/UI/rendimiento/usabilidad pendientes con causa, riesgo, responsable implementador de Cierre y revisión2026-10-30, sin marcarlos aprobados. La autorización previa de continuar sin dispositivo permite preparar lo verificable, no sustituye los gates. Mantener deuda de iconos preexistente con revisión2026-10-28 según plan de Pedidos.



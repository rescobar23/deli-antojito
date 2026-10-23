# Rendimiento — pendiente de dispositivo

Preparado app/src/androidTest/java/com/famessa/deli_antojito/OrdersPerformanceTest.kt: 1,000 pedidos sintéticos, ViewModel y consulta reales, lectura/render y búsquedas con debounce, 5 calentamientos +30 muestras. Se omite intencionalmente en debug.

Ejecutar el comando release de quickstart en el baseline API 35/Pixel 2/2 GB/2 vCPU. Registrar host, emulador, hash y tiempos. El harness usa base en memoria; completar además medición de apertura desde navegación, filtros avanzados y base en disco.

No se registran tiempos inventados ni se acredita SC-001/SC-002. Responsable: implementador; ejecutar al disponer de dispositivo, revisión propuesta 2026-10-28. T072 permanece pendiente hasta la ejecución completa.

Verificado sin dispositivo: `-PordersPerformance=true :app:assembleRelease :app:assembleReleaseAndroidTest` terminó con BUILD SUCCESSFUL. El harness y su host release compilan; todavía no se ejecutaron las 30 muestras.


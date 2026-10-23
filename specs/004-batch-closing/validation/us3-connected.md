# US3: integración — 2026-10-23

CierreReadOnlyTest pasó en API35 y compara todas las tablas de negocio y la secuencia antes/después de consultas repetidas; base v4 intacta. La suite Compose aislada pasó8/8, incluyendo disponibilidad sin valores financieros o acciones ejecutables. La suite app pasó7 casos y omitió2 benchmarks por ejecutarse en debug; MainActivityModuleNavigationTest incluyó Productos, Pedidos y Cierre de Lote desde tarjeta/barra y restauración de actividad, sin omisión por falta de negocio configurado.

XML iniciales conservados en connected-results/. Una ejecución conjunta de suites app/feature falló al no encontrar el host Compose; se adoptó ejecución secuencial, aprobada en gradle-ui-isolated.log. Los benchmarks debug omitidos no son validación de rendimiento. API25 y TalkBack/usuarios se siguen por separado.

Actualización final: gradle-ui-final.log aprobó9/9 pruebas con dispatcher inyectado por constructor. BatchClosingAvailabilityTest recorre Loading/InvalidPeriod/Empty/Ready/Error y verifica seis campos No disponible; el caso añadido de segundos verifica que sigue siendo posible elegir explícitamente el offset ambiguo.

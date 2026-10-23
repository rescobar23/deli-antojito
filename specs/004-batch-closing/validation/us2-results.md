# US2: resultados — 2026-10-23

PASS API35 y JVM. CalculateConsultaCierreTopProductsTest: seis casos de agrupación, Top5, orden estable, nombre histórico más reciente, cantidad mayor que Int y producto real Otros. Tests de dominio pasaron en la suite63.

CierreHistoricalProductsTest: tres casos pasaron tras fortalecer el fixture con producto real(id7) del catálogo sintético. Se cambian nombre/precio/activo, se elimina, se verifica identidad/nombre/importes históricos conservados; se alternan estados durante lecturas y se verifica coherencia count×cantidad/bruto; se cierra y reabre una base de pruebas con nombre UUID y se obtiene el mismo resultado. La base sintética se elimina al finalizar.

Room completo inicial:34/34. Reejecución focalizada histórica:3/3 con --no-parallel (gradle-historical-connected.log y connected-results/data-historical-final.xml). UI de Top5 pasó dentro de la suite aislada8/8. No se suman las tres reejecuciones como tres pruebas únicas nuevas.

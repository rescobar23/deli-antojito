# US2: aceptación — 2026-10-23

PASS FR011/SC002/SC004 en API35, con evidencia en us2-results.md. Agrupación por productoId, orden cantidad/importe descendentes e identidad ascendente; nombre histórico de mayor fechaCreacion/pedidoId/posición. Resumen cuenta cabeceras una vez y Top5 suma solo sus productos incluidos; no se exige que Top5 equivalga al bruto general si existen más productos.

La prueba de catálogo usa registros sintéticos reales, cambia sus valores y los elimina, sin afectar snapshots. La prueba de reapertura mantiene el mismo período/estado/importe; la concurrencia verifica que ranking y resumen reflejan una misma lectura. Los históricos no dependen del precio o nombre actuales del catálogo.

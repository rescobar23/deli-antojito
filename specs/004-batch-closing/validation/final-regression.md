# Regresión final — 2026-10-23

PASS en API35/TB330FU y JVM. La navegación final (gradle-navigation-final.log) ejecutó2/2 casos sin fallos ni omisiones: tarjeta Inicio→Cierre, destinos Productos/Pedidos/Cierre desde tarjeta/barra, selección correcta y recreación de actividad con la DI final de cinco parámetros. Los otros recorridos app de la suite anterior fueron7 aprobados y2 benchmarks omitidos en debug; esos benchmarks no se contabilizan como pases.

La suite aislada final de feature pasó9/9 en gradle-ui-final.log. Datos:34/34 en suite completa;3/3 históricos tras mejorar el fixture con catálogo real sintético. Regresiones JVM de Pedidos14, Productos14 y configuración9 pasaron, además de dominio63/data32/VM11/app4 (total147).

No cambió AppDatabase v4, entidades, índices ni migraciones; CierreReadOnlyTest conservó tablas de negocio y secuencia. La prueba de reapertura conserva líneas/importes y la prueba de concurrencia no mezcla datos del resumen y ranking. Snapshots no dependen del catálogo.

Las suites de actividades se ejecutan una por vez con --no-parallel, después del fallo de host en ejecución conjunta; no se ignoran resultados fallidos. Las correcciones de DI/lint y el dispatcher constructor se compilaron con éxito, sin nuevas supresiones.

La regresión de esta tarea se refiere al dispositivo API35 disponible. Compatibilidad ejecutada API25 y revisión TalkBack siguen abiertas en T026/T051; sesiones de usuarios en T052. No se afirma que esos gates estén validados.

SC001 final pasó en release,30/30 muestras<2s; detalle y valores completos en performance.md. Las dos pruebas de navegación finales no tuvieron omisiones.

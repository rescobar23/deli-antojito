# Accesibilidad — 2026-10-23

Pruebas Compose API35 aprobadas: ancho320dp/fuente200% y600dp/fuente100%, etiquetas accesibles, controles de período con acción, causa de disponibilidad y conservación de contenido. El contenido vertical no fuerza tabla horizontal; Top5 usa filas con nombre/cantidad/importe y los controles Material mantienen superficie interactiva mínima48dp.

Pendiente: recorrido manual con TalkBack y comprobación en AndroidAPI25. Los asserts automáticos prueban semántica/render, no acreditan experiencia de lector de pantalla ni examen visual exhaustivo. La orientación portrait existente del host tiene warnings preexistentes documentados en build-and-tests.md.

Responsable: implementador de Cierre; seguimiento2026-10-30. Riesgo: foco/orden de lectura o adaptación específica de plataforma no detectados. T051 sigue abierta.

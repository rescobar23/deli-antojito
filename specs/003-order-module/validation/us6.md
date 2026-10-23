# US6 — resumen diario

Implementados conteos por creación, cancelados incluidos en total, agregado sin filtros y errores/reintento independientes del listado. Proveedor de jornada responde a medianoche, fecha/hora/zona y reanudación, liberando listeners al cancelar su colección.

Regresión detectada mediante test JVM: una medianoche omitida por cambio horario provocaba excepción. Se corrigió validando la fecha civil independientemente y normalizando su primer instante válido; el siguiente límite se calcula por fecha civil, sin sumar 24 horas.

Tests preparados para días de 23/25 horas, límites abiertos, cambio de zona, timer/resume y el fixture 5/2/1/1. Resultados finales en [test-results.md](test-results.md). T068 pendiente de ejecución del resumen reactivo en Android.


# US1: pruebas JVM — 2026-10-23

PASS. Ver build-and-tests.md y coverage.md. Dominio: validación de rango/zonas, lectura no invocada ante error, propagación de cancelación/fallo, conteo por cabecera, importes exactos e integridad. Data: mapper con campos ausentes y resolver estricto de segundos, fechas inválidas y cambios estacionales, incluidos offsets de30min. VM:11 pruebas con reloj/dispatcher controlados, restauración, reanudación, cambio de zona, error/reintento, publicación conjunta y resultados tardíos.

El caso adicional completedReadDoesNotDismissAnOpenEditor protege la edición durante una consulta en curso. Los tests de interfaces Room/Compose se registran separadamente, no se consideran ejecutados por compilar sus APK.

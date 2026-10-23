# Fundamentos — 2026-10-21

T006–T013 implementados: Money, modelos/contratos, entidades, migración 3→4, exportación v4, fakes y fixture de Room real con fallos por triggers.

Evidencia TDD: MoneyTest ejecutado inicialmente con 5 fallos contra un esqueleto; después 5/5 aprobados. La migración y las primeras pruebas de datos compilaron en :data:assembleDebugAndroidTest. data/schemas/com.famessa.deli_antojito.data.db.AppDatabase/4.json fue generado por Room.

T014 permanece pendiente en su parte instrumentada: no se ejecutó la migración en Android. Los fixtures v1/v2/v3, la preservación de datos, índices, FK y creación fresca tienen pruebas preparadas, no resultados de ejecución.

El usuario indicó «Continúa preparando lo verificable sin dispositivo». Se continuó con pruebas JVM y compilación de las historias; no se interpreta esa autorización como validación de la migración. Riesgo: discrepancia SQLite/Room no detectada por compilación. Responsable: implementador; ejecutar al conectar API 25/35, revisión propuesta 2026-10-28.


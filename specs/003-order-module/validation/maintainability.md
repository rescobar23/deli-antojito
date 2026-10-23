# Mantenibilidad y dependencias — 2026-10-21

Contratos y modelos de domain/data y tokens compartidos cuentan con KDoc. feature/orders/README.md describe recorridos, reglas, estado, errores y comandos. Se mantienen carpetas físicas históricas DB/DAO/Entities/Repository, sin renombrados ajenos.

Se agrega :feature:orders con Kotlin/Compose/Room/Hilt/Coroutines/Lifecycle y dependencias de prueba ya existentes. No hay upgrade de producción ni elevación de minSdk.

Se cataloga JaCoCo 0.8.12, la versión ya utilizada por AGP y presente en la caché, para generar el informe consolidado de dominio desde unit tests de dominio y presentación. Es una herramienta de verificación, sin dependencia en el APK. Responsable: implementador del módulo; no se cambia de motor de cobertura.

El build release de rendimiento se activa solo con -PordersPerformance=true: usa firma debug y host Compose de prueba para medición, no distribución. La configuración normal conserva su comportamiento.

Los comentarios explican independencia del snapshot, eliminación de productos, límites civiles e idempotencia. Pruebas y resultados pendientes se describen en test-results/release-readiness; no hay supresiones nuevas para ocultar fallos de lint.

Lint conserva deuda de iconos preexistente en core:ui: IconXmlAndPng, IconLauncherShape e IconDuplicates. No hay cambios en core/ui/src/main/res. Responsable: implementador del proyecto; revisión/vencimiento 2026-10-28. Se limita a selección/aspecto de recursos existentes; no se altera fuera del alcance de Pedidos.


# Quickstart: Implementación y validación de Pedidos

**Date**: 2026-10-21 | **Plan**: [plan.md](plan.md)

Este documento describe pasos de implementación y comprobación futuros. El comando de planificación no ha compilado ni ejecutado estas pruebas.

## Preparación

1. Usar la rama `codex/003-order-module`. Leer spec, research, data-model y ambos contratos.
2. Tener JDK 17 para ejecutar AGP (el código mantiene target JVM 11), SDK 35 y emuladores/dispositivo API 25 y 35. Conservar la configuración local del SDK fuera del control de versiones.
3. Generar `tasks.md` mediante `/speckit-tasks`. Incluir tests primero por historia. No ejecutar tareas `:feature:orders:*` antes de crear el módulo e incluirlo en settings.
4. Tomar fixture de base v3 con productos y configuración sintéticos antes de editar AppDatabase. No usar datos reales de clientes.

## Orden de trabajo

Domain y pruebas → data/migración y pruebas reales → UI por US1–US6 y ViewModels → host/Home/DI → regresiones y mediciones. Mantener la aplicación utilizable con configuración/productos durante el cableado. Versionar esquemas v4 exportados; no registrar archivos de build ni bases de dispositivos.

## Comandos desde la raíz del repositorio

Después de implementar los módulos y pruebas:

```powershell
.\gradlew.bat :domain:testDebugUnitTest :data:testDebugUnitTest :feature:orders:testDebugUnitTest
.\gradlew.bat :app:assembleDebug :app:lintDebug :data:lintDebug :domain:lintDebug :feature:orders:lintDebug :feature:home:lintDebug :core:common:lintDebug :core:ui:lintDebug
.\gradlew.bat :data:connectedDebugAndroidTest :feature:orders:connectedDebugAndroidTest :app:connectedDebugAndroidTest
.\gradlew.bat :feature:product:testDebugUnitTest :feature:business-config:testDebugUnitTest
.\gradlew.bat :feature:product:connectedDebugAndroidTest :feature:business-config:connectedDebugAndroidTest :feature:home:connectedDebugAndroidTest
git diff --check
```

Los informes se generan con `:domain:createDebugUnitTestCoverageReport` y `:feature:orders:createDebugUnitTestCoverageReport`. El informe `:domain:ordersDomainCoverage` consolida la ejecución de dominio desde sus tests y los de presentación usando el mismo JaCoCo 0.8.12 de AGP. Adjuntar informe con ≥70% en código tocado de domain/ViewModels; no suponer que ejecutar tests implica alcanzar cobertura. Capturar warnings/lint; no añadir supresiones amplias para aprobar.

## Matriz mínima de verificación

| Grupo | Casos | Requisitos |
|---|---|---|
| US1 | orden estable, detalle, sin datos, no encontrado, carga/error/reintento | FR-001/002/012/019 |
| US2 | cliente vacío, ≥1 línea, cantidad inválida, centavos, agrupación, no activos, guardado/reinicio | FR-007/008/009/017/018 |
| US3 | campos editables, snapshot preservado, descarte, terminal bloqueado, conflicto revisión | FR-010/011/012 |
| US4 | matriz completa 16 pares, cuatro válidos, confirmación cancelar, historial y fecha por cambio | FR-013/014/015 |
| US5 | parcial número/cliente, MARÍA/María, NFC, comodines literales, pestañas y rangos abiertos/inclusivos/invertidos, intersección vacía, limpiar/cancelar | FR-003/004/005/006 |
| US6 | cinco pedidos de ejemplo, cancelados en total, pedidos anteriores excluidos, independencia filtros, medianoche/reanudación/cambio zona | FR-016 |
| Integridad | token repetido con respuesta perdida, dos altas concurrentes, dos ediciones, edición contra transición, fallo entre cabecera/líneas/historial y rollback | FR-008/017/018 |
| Migración | creación fresca v4, fixture v3→4 y cadenas v1/v2→4, datos previos intactos, índices y secuencia, sin fallback destructivo | FR-017/018 |
| Dinero | precio no finito, >2 decimales HALF_UP, precio redondeado cero, máximo y overflow de multiplicación/suma, sin precio editable | FR-007/009/010 |
| Accesibilidad | colores/iconos/textos, semántica, ≥48 dp, fuente 200%, teclado, pantalla pequeña, resumen accesible | FR-020/021 |
| Privacidad | inspección de logs y errores: sin cliente, observaciones, tokens, secretos ni SQL con datos | FR-022 |
| Regresión | configuración inicial obligatoria, productos CRUD, rutas/Back/rotación/restauración, cambio sección con borrador | FR-001/011 |

Pruebas de migración deben abrir base física usando el código de producción v4 y comprobar esquema/datos; un test Room en memoria de la versión nueva no prueba migraciones. Tests de fallo intermedio y concurrencia usan Room real, no solo repositorios fake.

## Recorrido manual

1. Iniciar offline con configuración existente y catálogo activo. Entrar a Pedidos desde Inicio; verificar vacío y resumen cero.
2. Crear pedido cliente de prueba con 2 × $12.50 y 3 × $7.10; total $46.30, estado pendiente. Salir/reabrir y reiniciar app: mismo número y datos.
3. Cambiar precio/nombre del catálogo y borrar uno de los productos. Consultar pedido: historia intacta. Editar cantidad de línea existente y conservar precio; añadir una línea nueva toma precio vigente.
4. Buscar por número y nombre con cambios de mayúsculas. Aplicar fechas, importes y estados que se intersecten y contradigan pestaña; limpiar/cancelar panel sin alterar búsqueda.
5. Iniciar preparación y completar un pedido. Cancelar otro primero rechazando y luego confirmando. Verificar acciones terminales, historial persistido y resumen correcto.
6. Mantener pantalla abierta al cambiar jornada y reanudar tras suspensión. El resumen se actualiza sin reiniciar y sin incorporar pedidos creados ayer.
7. Cambiar fuente al 200%, abrir teclado y recorrer pantallas con navegación accesible. Verificar que resumen y Guardar no quedan ocultos.

## Rendimiento y usabilidad

Baseline: emulador API 35 perfil Pixel 2, 2 GB RAM/2 vCPU, fuente 100%, animaciones normales; build release sin depurador. Registrar host, versión del emulador y hash de build para reproducir.

Sembrar 1,000 pedidos sintéticos con distribución de estados/clientes/fechas y líneas. Hacer 5 calentamientos y 30 muestras de apertura; medir desde acción hasta tarjetas listas. Ejecutar 30 búsquedas/cambios de filtro; medir desde última entrada hasta resultados correspondientes (incluye debounce). Listado <2 s, p95 búsqueda/filtro <1 s; comprobar interacción responsiva durante operaciones. No cargar imágenes del catálogo en proyecciones de tarjeta.

Observar a cinco usuarios representativos registrando dos productos y localizando un pedido conocido, sin asistencia; al menos cuatro cumplen <2 min y <30 s respectivamente. Registrar tiempos y problemas sin datos personales. Estas sesiones no se sustituyen por UI tests.

El harness `OrdersPerformanceTest` siembra 1,000 pedidos en una base aislada y mide lectura/render y búsquedas con el ViewModel real (incluido debounce), con 5 calentamientos y 30 muestras. Se omite en debug deliberadamente. Ejecutar en el dispositivo baseline con:

```powershell
.\gradlew.bat -PordersPerformance=true :app:connectedReleaseAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.famessa.deli_antojito.OrdersPerformanceTest
```

La propiedad habilita firma debug solo para ese build release de medición e incluye el host Compose de pruebas. No es una configuración de distribución. El harness no sustituye la medición manual de apertura desde navegación, filtros avanzados, pruebas en disco ni sesiones con usuarios; registrar esas limitaciones junto a los tiempos.

## Evidencia antes de merge

Adjuntar resultados de build/lint/tests, cobertura, migración, rendimiento, usabilidad y recorrido offline/accesibilidad. Propietario: implementador del módulo. Si una puerta no puede ejecutarse, documentar motivo, riesgo, responsable y fecha concreta de seguimiento en plan/PR; no marcarla aprobada. Cambios documentales de esta fase se validan por coherencia y enlaces, sin ejecutar pruebas de código que aún no existe.

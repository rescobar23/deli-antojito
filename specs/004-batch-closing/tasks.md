# Tasks: Cierre de Lote — consulta preliminar

**Input**: `specs/004-batch-closing/` — [spec.md](spec.md), [plan.md](plan.md), [research.md](research.md), [data-model.md](data-model.md), [contratos](contracts/domain-contract.md), [UI](contracts/ui-contract.md), [quickstart.md](quickstart.md).
**Branch**: `codex/004-batch-closing` | **Date**: 2026-10-23

**Tests**: Obligatorios por especificación y constitución. Escribir las pruebas de comportamiento antes de implementar, verificar que fallan por el comportamiento ausente y ejecutarlas tras implementarlo. No afirmar gates conectados aprobados sin ejecución real.
**Organization**: Setup → contratos compartidos → US1 → US2 → US3 → gates. Todas las historias son P1; el orden sigue dependencias reales. Solo consulta de COMPLETADO actual por fecha de creación [inicio, fin), sin pagos, notas, lotes, historial o migración.

## Format and Path Conventions

Formato: `- [ ] Tnnn [P] [USn] Descripción con ruta exacta`. [P] permite trabajo simultáneo solo cuando se han completado sus prerrequisitos; no autoriza ejecutar una implementación antes de sus tests. Rutas relativas a raíz del repositorio; carpetas físicas DAO/Repository conservadas. Nombres nuevos son los acordados por el plan. Los checkpoints no sustituyen tareas de validación.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Preparar el módulo con el stack existente, sin cambiar versiones ni esquema.

- [X] T001 Crear módulo Android library Compose/Hilt `:feature:batch-closing` con minSdk25, compileSdk35, JVM11, dependencias domain/core y pruebas existentes en `feature/batch-closing/build.gradle.kts` y `feature/batch-closing/src/main/AndroidManifest.xml`, e incluirlo en `settings.gradle.kts`; sin dependencia a data/orders.
- [X] T002 Conectar dependencia de la nueva feature en `app/build.gradle.kts`, sin cambiar applicationId, test runner global ni variantes de distribución.
- [X] T003 [P] Definir recursos iniciales es-MX para consulta preliminar, regla COMPLETADO/creación, fechas, errores y disponibilidad en `feature/batch-closing/src/main/res/values/strings.xml`, con valores monetarios formateados en MXN sin Double.
- [X] T004 [P] Crear guía breve del módulo en `feature/batch-closing/README.md` y matriz de evidencia vacía en `specs/004-batch-closing/validation/release-readiness.md`, indicando alcance, comandos, propietario y gates no ejecutados; registrar baseline v4 sin cambios de entidades.

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Contratos compartidos mínimos para las tres historias; no adelantar implementación financiera o del ranking.

- [X] T005 Definir modelos inmutables CierrePeriodo, PedidoConsultaSnapshot/LineaConsultaSnapshot, ResumenConsultaCierre, ProductoConsultaCierre y ConsultaCierre en `domain/src/main/java/com/famessa/deli_antojito/domain/model/cierre/CierrePeriodo.kt`, `PedidoConsultaSnapshot.kt` y `ConsultaCierre.kt` de ese mismo directorio; usar Money existente y cantidad agrupada Long.
- [X] T006 Definir resultados tipados de snapshots/consulta, códigos de período e integridad en `domain/src/main/java/com/famessa/deli_antojito/domain/model/cierre/ConsultaCierreResult.kt`, y contrato suspendido solo lectura en `domain/src/main/java/com/famessa/deli_antojito/domain/repository/CierreConsultaRepository.kt`; documentar cancelación y cero escrituras con KDoc.
- [X] T007 [P] Definir componentes civiles con segundos, candidatos con offset y resultados Unique/Ambiguous/InvalidDate/NonexistentTime en `domain/src/main/java/com/famessa/deli_antojito/domain/time/CierreDateTimeResolver.kt`, sin Android/java.time y sin reemplazar BusinessDayProvider.
- [X] T008 [P] Preparar snapshots/fakes deterministas y dispatcher/reloj controlables en `feature/batch-closing/src/test/java/com/famessa/deli_antojito/feature/batchclosing/BatchClosingFakes.kt`, y fixture contractual [1000,2000) con dos pedidos/500 centavos en `domain/src/test/java/com/famessa/deli_antojito/domain/usecase/cierre/CierreFixtures.kt`.
- [X] T009 Preparar fixture Room v4 de pedidos y líneas en `data/src/androidTest/java/com/famessa/deli_antojito/data/cierre/CierreRoomFixtures.kt`, incluyendo estados, límites, finalización posterior y cabecera sin líneas; reutilizar apertura de pruebas existente sin crear migración.

**Checkpoint**: Contratos disponibles; US1/US2 comparten los mismos snapshots. La implementación de cada comportamiento empieza después de sus pruebas correspondientes.

## Phase 3: User Story 1 — Consultar pedidos de un período (Priority: P1) MVP

**Goal**: Consultar cantidad y bruto exactos de pedidos COMPLETADO creados en el período desde la app, con selección temporal precisa y estados recuperables.
**Independent Test**: Fixture contractual produce2 pedidos/500 centavos; cambia estados/fechas, prueba límites y errores, reabre consulta y verifica selección conservada y datos actualizados. Banner mínimo identifica consulta preliminar desde este incremento.

### Tests for User Story 1 — FIRST

- [X] T010 [P] [US1] Escribir pruebas de inicio<fin<=now, zona inválida, límites y no invocar repositorio ante validación fallida en `domain/src/test/java/com/famessa/deli_antojito/domain/usecase/cierre/ValidateCierrePeriodoUseCaseTest.kt` y `GetConsultaCierreUseCaseTest.kt` del mismo directorio; incluir propagación de CancellationException y ReadFailure tipado.
- [X] T011 [P] [US1] Escribir pruebas de conteo único, suma de cabecera una vez, cero filas, líneas duplicadas/sin líneas, cantidades/posiciones inválidas, inconsistencia subtotal/total y overflow monetario en `domain/src/test/java/com/famessa/deli_antojito/domain/usecase/cierre/CalculateConsultaCierreUseCaseTest.kt`; todavía sin exigir ranking, que corresponde a US2.
- [X] T012 [P] [US1] Escribir pruebas de fecha-hora estricta, segundos, conversión DatePicker, API25 sin java.time, medianoche, días23/25h, gap y overlap con candidatos explícitos en `data/src/test/java/com/famessa/deli_antojito/data/time/LocalCierreDateTimeResolverTest.kt`.
- [X] T013 [P] [US1] Escribir tests del mapper de LEFT JOIN (cabecera repetida, campos nulos de línea, IDs/posiciones duplicados) en `data/src/test/java/com/famessa/deli_antojito/data/mappers/CierreConsultaMappersTest.kt`, y tests Room de filtro COMPLETADO/creación inclusiva-exclusiva, sin N+1 ni precio de catálogo en `data/src/androidTest/java/com/famessa/deli_antojito/data/cierre/CierreConsultaRepositoryTest.kt`.
- [X] T014 [P] [US1] Escribir pruebas VM de defecto medianoche→apertura, Loading/Empty/Ready/Error, edición, reintento, respuesta tardía tras cancelación, reanudación/cambio de zona-reloj y restauración SavedStateHandle en `feature/batch-closing/src/test/java/com/famessa/deli_antojito/feature/batchclosing/BatchClosingViewModelTest.kt`.
- [X] T015 [P] [US1] Escribir pruebas Compose de período/segundos, validación, offsets ambiguos, cancelar selector, vacío/error/reintento y sin datos anteriores vigentes en `feature/batch-closing/src/androidTest/java/com/famessa/deli_antojito/feature/batchclosing/BatchClosingPeriodTest.kt`; añadir casos decode/restore `close` en `app/src/test/java/com/famessa/deli_antojito/navigation/BatchClosingNavigationStateTest.kt`.

### Implementation for User Story 1

- [X] T016 [US1] Implementar validación del período y cálculo de resumen exacto con integridad de líneas/cabeceras en `domain/src/main/java/com/famessa/deli_antojito/domain/usecase/cierre/ValidateCierrePeriodoUseCase.kt` y `CalculateConsultaCierreUseCase.kt` del mismo directorio; devolver lista Top5 vacía hasta US2, no calcular cobros/neto.
- [X] T017 [US1] Añadir proyección plana sin cliente/notas y SELECT suspendido LEFT JOIN en `data/src/main/java/com/famessa/deli_antojito/data/DAO/PedidoDao.kt`, filtrando `estado='COMPLETADO' AND fecha_creacion>=inicio AND fecha_creacion<fin`; no cambiar AppDatabase, entidades, índices ni esquema v4.
- [X] T018 [US1] Implementar agrupación de proyección a snapshots y repositorio de lectura única con errores tipados/cancelación en `data/src/main/java/com/famessa/deli_antojito/data/mappers/CierreConsultaMappers.kt` y `data/src/main/java/com/famessa/deli_antojito/data/Repository/CierreConsultaRepositoryImpl.kt`, sin writes ni caché persistida.
- [X] T019 [P] [US1] Implementar resolución estricta Calendar/TimeZone, conversión local/epoch con segundos y detección explícita de horas inexistentes/ambiguas en `data/src/main/java/com/famessa/deli_antojito/data/time/LocalCierreDateTimeResolver.kt`, conservando instantes al cambiar zona y sin reutilizar range diario para extremos con hora.
- [X] T020 [US1] Implementar orquestación validación→lectura única→cálculo fuera de main y wiring de repositorio/resolver/use cases en `domain/src/main/java/com/famessa/deli_antojito/domain/usecase/cierre/GetConsultaCierreUseCase.kt`, `data/src/main/java/com/famessa/deli_antojito/data/di/RepositoryModule.kt` y `app/src/main/java/com/famessa/deli_antojito/di/BatchClosingUseCaseModule.kt`.
- [X] T021 [US1] Implementar estado inmutable y VM con generación de solicitud, cancelación/descartar respuestas tardías, SavedStateHandle y refresco de reloj/zona en `feature/batch-closing/src/main/java/com/famessa/deli_antojito/feature/batchclosing/BatchClosingUiState.kt` y `BatchClosingViewModel.kt` del mismo directorio; publicar resultado conjunto, sin cachearlo como evidencia vigente.
- [X] T022 [US1] Implementar selector fecha/hora/segundos y elección de offset, fechas DatePicker interpretadas como componentes civiles, en `feature/batch-closing/src/main/java/com/famessa/deli_antojito/feature/batchclosing/components/PeriodDateTimeDialog.kt` y `PeriodCard.kt` del mismo directorio; cancelar conserva selección, aceptar recalcula e invalida resultado anterior.
- [X] T023 [US1] Implementar ruta lifecycle-aware y pantalla básica Aqua con banner preliminar, regla de selección, zona/offset, conteo/bruto MXN, estados y reintento en `feature/batch-closing/src/main/java/com/famessa/deli_antojito/feature/batchclosing/BatchClosingRoute.kt`, `BatchClosingScreen.kt` y `components/SummaryCard.kt`; sin acción de cierre.
- [X] T024 [US1] Conectar MainDestination.BatchClosing/clave close, selección correcta de barra/nav_close y host en `app/src/main/java/com/famessa/deli_antojito/navigation/MainDestination.kt` y `MainNavigation.kt`; conectar callback/tarjeta/menú y subtítulo preliminar en `feature/home/src/main/java/com/famessa/deli_antojito/feature/home/HomeView.kt`.
- [X] T025 [US1] Ejecutar unit tests de T010–T014 y app state de T015, corregir fallos y registrar resultados/comandos en `specs/004-batch-closing/validation/us1-unit.md`; no marcar Room/Compose como ejecutados por compilar APK.
- [ ] T026 [US1] Ejecutar integración Room y Compose de T013/T015 en dispositivo/emulador API35 y casos horarios/funcionales API25, registrar resultados en `specs/004-batch-closing/validation/us1-connected.md`; si falta dispositivo dejar esta tarea pendiente con causa/riesgo/propietario/seguimiento2026-10-30.
- [X] T027 [US1] Verificar el checkpoint de US1 con fixture contractual y escenarios de fecha creación/finalización y período vacío en `specs/004-batch-closing/validation/us1-acceptance.md`, enlazando evidencia real para FR003/004/008/010/014/021 y SC002/005.

**Checkpoint**: MVP navegable de consulta básica. US1 se valida sin depender del ranking ni de capacidad financiera. La aceptación completa de la entrega requiere US2, US3 y gates finales.

## Phase 4: User Story 2 — Consultar productos más vendidos (Priority: P1)

**Goal**: Top5 exacto y coherente con el resumen, usando las mismas líneas históricas.
**Independent Test**: Inyectar snapshots conocidos y verificar cantidades/importes/orden/nombres sin pagos, lotes ni acceso al catálogo.

### Tests for User Story 2 — FIRST

- [X] T028 [P] [US2] Escribir pruebas de agrupación por productoId, cantidad Long, suma comprobada, Top5, empates e histórico más reciente por fechaCreacion/pedidoId/posición en `domain/src/test/java/com/famessa/deli_antojito/domain/usecase/cierre/CalculateConsultaCierreTopProductsTest.kt`; incluir producto real Otros y nombres distintos de una identidad.
- [X] T029 [P] [US2] Escribir integración de renombrar/desactivar/borrar catálogo sin alterar datos y lectura concurrente con transición de estado en `data/src/androidTest/java/com/famessa/deli_antojito/data/cierre/CierreHistoricalProductsTest.kt`, comprobando resumen y Top5 del mismo conjunto.
- [X] T030 [P] [US2] Escribir pruebas Compose de 0/menos/más de5 productos, cantidades/bruto y ranking accesible sin desbordar ancho compacto en `feature/batch-closing/src/androidTest/java/com/famessa/deli_antojito/feature/batchclosing/BatchClosingTopProductsTest.kt`.

### Implementation for User Story 2

- [X] T031 [US2] Extender el cálculo existente para acumular productos, elegir nombre histórico y ordenar/tomar5 en `domain/src/main/java/com/famessa/deli_antojito/domain/usecase/cierre/CalculateConsultaCierreUseCase.kt`, usando Long comprobado para cantidades y Money para importes, sin nueva lectura independiente.
- [X] T032 [US2] Implementar tabla/filas Top5 con posición, nombre, unidades y bruto MXN en `feature/batch-closing/src/main/java/com/famessa/deli_antojito/feature/batchclosing/components/TopProductsTable.kt`, incluyendo estado vacío, semántica y adaptación320/600dp.
- [X] T033 [US2] Integrar Top5 en la misma publicación de estado y render de resumen en `feature/batch-closing/src/main/java/com/famessa/deli_antojito/feature/batchclosing/BatchClosingScreen.kt`; añadir caso de no mezclar resumen/período/ranking tardío en `feature/batch-closing/src/test/java/com/famessa/deli_antojito/feature/batchclosing/BatchClosingViewModelTest.kt` antes de modificar coordinación si hiciera falta.
- [X] T034 [US2] Ejecutar unit de ranking y Room/UI históricos T028–T030 y registrar resultados por tipo en `specs/004-batch-closing/validation/us2-results.md`; dejar subevidencia conectada y esta tarea pendientes si no pudo ejecutarse.
- [X] T035 [US2] Verificar FR011 y SC002/004 con cambios reales del catálogo, reinicio y consulta del mismo período en `specs/004-batch-closing/validation/us2-acceptance.md`; no exigir que suma Top5 equivalga al bruto total cuando existen más productos.

**Checkpoint**: Resumen y ranking proceden de una sola instantánea. Cambios de catálogo no alteran las líneas históricas.

## Phase 5: User Story 3 — Reconocer límites de la consulta (Priority: P1)

**Goal**: Identificar disponibilidad real y demostrar solo lectura, sin confundir importes con cobros.
**Independent Test**: Con snapshots sintéticos, comprobar avisos/No disponible en todos los estados y ausencia de acciones financieras; desde app comparar base antes/después de consultar y navegar.

### Tests for User Story 3 — FIRST

- [X] T036 [P] [US3] Escribir pruebas de No disponible para descuentos/neto/cuatro métodos en Loading/Empty/Ready/Error, sin importes/porcentajes ficticios, notas/historial/Cerrar lote, en `feature/batch-closing/src/androidTest/java/com/famessa/deli_antojito/feature/batchclosing/BatchClosingAvailabilityTest.kt`.
- [X] T037 [P] [US3] Escribir integración que compare tablas, secuencia, versión y schema v4 antes/después de consultar/reintentar en `data/src/androidTest/java/com/famessa/deli_antojito/data/cierre/CierreReadOnlyTest.kt`; corroborar sin entidades/tablas de lote nuevas.
- [X] T038 [P] [US3] Escribir pruebas app de tarjeta y nav_close, selección correcta, Atrás/restore, gate inicial y regresiones Productos/Pedidos/Config en `app/src/androidTest/java/com/famessa/deli_antojito/BatchClosingNavigationTest.kt`, incluyendo navegar/volver sin mutar pedidos.

### Implementation for User Story 3

- [X] T039 [US3] Implementar panel financiero No disponible con causa visible/accesible y cuatro métodos sin Money(0)/porcentajes en `feature/batch-closing/src/main/java/com/famessa/deli_antojito/feature/batchclosing/components/UnavailablePaymentsCard.kt`; ampliar textos en `feature/batch-closing/src/main/res/values/strings.xml`.
- [X] T040 [US3] Integrar panel en todos los estados y afinar banner/etiqueta «No representa cobros confirmados» en `feature/batch-closing/src/main/java/com/famessa/deli_antojito/feature/batchclosing/BatchClosingScreen.kt`, manteniendo ausencia de acción Cerrar lote, notas e historial.
- [X] T041 [US3] Ejecutar integración de no escrituras y UI/app de T036–T038, registrar comandos/resultados reales y pendientes en `specs/004-batch-closing/validation/us3-connected.md`; no cerrar tarea sin ejecución completa.
- [X] T042 [US3] Revisar grafo de dependencias, SQL/proyección y rutas públicas para confirmar cero PII innecesaria, cero API de consolidación y cero writes en `specs/004-batch-closing/validation/us3-readonly-review.md`, citando `CierreConsultaRepository.kt`, `PedidoDao.kt` y `CierreConsultaRepositoryImpl.kt` por sus rutas del plan.
- [X] T043 [US3] Verificar escenarios US3 y FR005/009/012/013/015/016/017/018 con SC003/007 en `specs/004-batch-closing/validation/us3-acceptance.md`, enlazando evidencia de estados no disponibles y ausencia de modificaciones.

**Checkpoint**: Alcance preliminar explícito y sin acciones financieras. Los requisitos diferidos solo exigen ausencia de esas capacidades, no su implementación.

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Completar evidencia y calidad transversal sin ampliar el alcance.

- [X] T044 Configurar informe específico `batchClosingDomainCoverage` con JaCoCo catalogado en `domain/build.gradle.kts`, incluyendo ejecución desde nueva feature y clases domain/cierre/time tocadas; configurar cobertura VM en `feature/batch-closing/build.gradle.kts`, sin usar filtros de ordersDomainCoverage como sustituto.
- [X] T045 Ejecutar unit/build/lint y compilación de APK instrumentados según `specs/004-batch-closing/quickstart.md`, registrar conteos/fallos/avisos en `specs/004-batch-closing/validation/build-and-tests.md`; corregir fallos y no añadir supresiones para ocultar warnings nuevos.
- [X] T046 Generar informes y verificar ≥70% de cobertura en dominio/VM tocados, ampliar pruebas por riesgo si hace falta y registrar clases/denominadores/resultados en `specs/004-batch-closing/validation/coverage.md`.
- [X] T047 [P] Revisar KDoc público, constructor DI, cancelación, errores explícitos y logs sin PII en `specs/004-batch-closing/validation/maintainability-privacy.md`, citando archivos domain/data/feature modificados; actualizar `feature/batch-closing/README.md` con comportamiento final.
- [X] T048 Preparar fixture reproducible1000 pedidos×10 líneas/100 productos, versiones todos-completados/mixta, en `app/src/androidTest/java/com/famessa/deli_antojito/BatchClosingPerformanceTest.kt` para medir hasta resumen+Top5 visible con harness release existente, sin introducir variantes de distribución.
- [X] T049 Ejecutar5 calentamientos+30 mediciones en el dispositivo API35 documentado en quickstart (TB330FU físico, sustitución del Pixel2 propuesto), exigir cada medición<2s y registrar valores/max/p95/entorno en `specs/004-batch-closing/validation/performance.md`; dejar pendiente si no se mide en dispositivo/emulador.
- [X] T050 [P] Preparar prueba de accesibilidad320/600dp, fuente100/200%, targets48dp, semántica/foco y offsets en `feature/batch-closing/src/androidTest/java/com/famessa/deli_antojito/feature/batchclosing/BatchClosingAccessibilityTest.kt`, sin duplicar pruebas de ranking.
- [ ] T051 Ejecutar prueba de accesibilidad y revisión TalkBack/API25/API35 en `specs/004-batch-closing/validation/accessibility.md`; adjuntar evidencia de legibilidad y ausencia de desbordamiento, dejando tarea pendiente si falta validación real.
- [ ] T052 Ejecutar sesiones SC006 con cinco responsables y registrar éxito/tiempo anónimos en `specs/004-batch-closing/validation/usability.md`; exigir≥4/5 sin ayuda en<2min distinguiendo bruto/cobrado, sin sustituirlas por test automatizado.
- [X] T053 Ejecutar suite conectada final y regresiones de recorridos tocados según `specs/004-batch-closing/quickstart.md`, registrar resultados y comprobar esquema v4/datos intactos en `specs/004-batch-closing/validation/final-regression.md`; no repetir suites ya válidas sin cambios o incertidumbre que lo justifique.
- [X] T054 Consolidar matriz FR/SC, evidencias, gates reales y pendientes en `specs/004-batch-closing/validation/release-readiness.md`, contrastar alcance con `specs/004-batch-closing/spec.md` y mantener cualquier gate no ejecutado con causa/riesgo/implementador responsable/revisión2026-10-30; conservar deuda de iconos previa con revisión2026-10-28 sin atribuirla a esta feature.

## Dependencies & Execution Order

### Phase Dependencies

```text
Setup T001–T004
  → Foundational T005–T009
    → US1 tests T010–T015 → implementación T016–T024 → validación T025–T027
      → US2 tests T028–T030 → implementación T031–T033 → validación T034–T035
      → US3 tests T036–T038 → implementación T039–T040 → validación T041–T043
        → Polish T044–T054 (gates de salida)
```

US2 y US3 son verificables con fixtures por separado después de US1; su implementación puede alternarse, pero T033 y T040 tocan BatchClosingScreen y se integran secuencialmente. No se declara que todas las historias puedan desarrollarse en paralelo sin coordinación.

### Explicit Task Dependencies

| Grupo | Prerrequisitos |
| --- | --- |
| T002–T004 | T001; T003 y T004 entre sí independientes |
| T006–T009 | T005; T007/T008 en archivos distintos tras T006; T009 reutiliza modelos/fixtures acordados |
| T010–T015 | Fundación completa; pruebas en archivos separados, no dependen de implementación ausente |
| T016 | T010/T011 fallando por comportamiento ausente |
| T017–T018 | T013; T018 después de T017 y contratos |
| T019 | T012; puede coincidir con T017/T018 |
| T020 | T016/T018/T019; no ejecutar wiring antes de clases dependientes |
| T021 | T014 y T020 |
| T022 | T015 y T019/T021 |
| T023–T024 | T021/T022; T024 después de ruta T023 y test app T015 |
| T025–T027 | Implementación US1 completa; T027 requiere evidencia T025/T026 |
| T028–T030 | US1 funcional; archivos separados con fixtures compartidos ya preparados |
| T031–T033 | Tests correspondientes primero; T032 puede coincidir con T031; T033 después de ambos |
| T034–T035 | US2 integrado; T035 requiere evidencia T034 |
| T036–T038 | US1; pruebas separadas independientes de ranking |
| T039–T040 | T036 primero; T040 después de T039 y sin editar Screen simultáneamente con T033 |
| T041–T043 | US3 integrado; T042 puede hacerse tras implementación sin hardware; T043 requiere T041/T042 |
| T044–T046 | Historias implementadas; configuración T044 antes de informes T046; T045 antes de T046 |
| T047 | Historias implementadas; independiente de cobertura/medición |
| T048–T049 | US1+US2+US3 funcionales y build release válido; T049 después de harness T048 |
| T050–T051 | Screen final; T051 después de prueba T050 |
| T052 | UI final y guion SC006; requiere participantes reales |
| T053–T054 | T053 tras cambios finales que requieran regresión; T054 consolida toda evidencia y pendientes sin simular gates |

Los tests-first pueden registrarse como pruebas inicialmente fallidas sin dejar producción intencionalmente rota como entregable. Si falta dispositivo, avanzar en implementación/unit/build/documentación independiente; mantener tareas de ejecución conectada y aceptación dependiente abiertas.

## Parallel Execution Examples

### User Story 1

Tras fundación, T010/T011/T012/T013/T014/T015 escriben pruebas en archivos distintos y pueden ejecutarse en paralelo. Tras los tests correspondientes, T019 (resolver horario) puede coincidir con T017/T018 (DAO/mapper/repositorio). No paralelizar T020 con las clases que inyecta ni T021 con el contrato aún cambiante.

### User Story 2

Tras US1, T028/T029/T030 son pruebas independientes; luego T031 (cálculo de dominio) y T032 (tabla Compose) pueden trabajarse a la vez. T033 integra ambos y espera su finalización. T034/T035 verifican el conjunto; no modificar el cálculo simultáneamente desde dos tareas.

### User Story 3

Tras US1, T036/T037/T038 escriben pruebas en feature/data/app respectivamente. Pueden coincidir con las pruebas US2. T039 implementa el panel y T040 integra cuando T033 deje Screen estable. La revisión de solo lectura T042 no necesita esperar disponibilidad de dispositivo de T041.

## Traceability and Independent Validation

| Historia / grupo | Requisitos | Resultado verificable |
| --- | --- | --- |
| US1 T010–T027 | FR001–004/008/010/014/019/021 | Selección exacta y resumen válido, actualizado y recuperable; SC002/005 |
| US2 T028–T035 | FR011, coherencia FR014 | Top5 histórico, sin dependencia del catálogo; SC002/004 |
| US3 T036–T043 | FR005–007/009/012/013/015–018 | No disponible sin cifras ficticias, sin consolidación ni historial; SC003/007 |
| Transversal T044–T054 | FR019/020 y restricciones de calidad | Cobertura/build/lint/Room/UI; SC001/006 y regresiones |

FR006/007/012/016/017/018 se cubren como límites de alcance, nunca mediante implementación de pagos, devoluciones, notas o cierres.

## Implementation Strategy

1. MVP técnico: Setup+Fundación+US1, navegable con banner preliminar, período y resumen. No presentarlo como módulo financiero completo.
2. Agregar US2 sin otra lectura; probar historia con snapshots conocidos y luego integrar Top5 al mismo estado.
3. Completar US3 y validar límites/solo lectura. El banner mínimo de US1 no sustituye paneles ni evidencia de US3.
4. Completar gates transversales antes de declarar entrega validada. Sin hardware o participantes, preparar lo verificable y conservar tareas pendientes con seguimiento.

## Notes

54 tareas, IDs únicos T001–T054; etiquetas US solo en fases de historia. [P] identifica archivos sin conflicto una vez satisfechas dependencias; los ejemplos describen oportunidades adicionales por subetapa. No implica creación de subagentes ni commits automáticos. Marcar una tarea solo al completar su resultado y evidencia, no al compilar un test que aún no se ejecutó. Ejecutar pruebas adecuadas al cambio; repetirlas únicamente tras cambios/fallos/incertidumbres relevantes.










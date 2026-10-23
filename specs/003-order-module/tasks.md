# Tasks: Módulo de Pedidos

**Input**: Design documents from `specs/003-order-module/`

**Branch**: `codex/003-order-module` | **Created**: 2026-10-21

**Prerequisites**: [plan.md](plan.md), [spec.md](spec.md), [research.md](research.md), [data-model.md](data-model.md), [domain-contract.md](contracts/domain-contract.md), [ui-contract.md](contracts/ui-contract.md), [quickstart.md](quickstart.md).

**Tests**: Obligatorios por especificación y constitución. En cada bloque escribir pruebas, observar fallo por el comportamiento pendiente y después implementar. La generación actual es exclusivamente documental: no ejecuta estas tareas ni acredita pruebas futuras.

**Organization**: Setup → fundamentos → US1–US6 en prioridad → validación transversal. Las casillas reflejan el progreso; las validaciones instrumentadas y de aceptación permanecen pendientes hasta ejecutarse. Los paths son relativos a la raíz del repositorio; las carpetas físicas DB/DAO/Entities/Repository se conservan.

**Format**: `- [ ] Tnnn [P?] [USn?] Descripción con paths`. [P] permite ejecutar tareas del mismo bloque sobre archivos diferentes cuando sus prerrequisitos estén completos; no elimina la barrera de pruebas ni autoriza trabajo entre fases dependientes.

## Phase 1: Setup — módulo y entorno

**Goal**: Preparar módulo, herramientas y evidencia de la base anterior sin cambiar su esquema.

**Independent Test / Checkpoint**: El módulo vacío está incluido, las herramientas están identificadas y los fixtures preceden cualquier modificación de AppDatabase.

- [X] T001 Verificar JDK 17, SDK 35, wrapper, rama y dispositivos API 25/35; registrar baseline y comandos disponibles sin secretos en specs/003-order-module/validation/environment.md.
- [X] T002 Crear módulo Android library :feature:orders con minSdk 25, JVM 11, Compose/Hilt y dependencias de pruebas existentes en feature/orders/build.gradle.kts; incluirlo en settings.gradle.kts y app/build.gradle.kts.
- [X] T003 Configurar cobertura de unit tests para domain/ViewModels, descubrir y registrar tareas de informes de AGP en domain/build.gradle.kts, feature/orders/build.gradle.kts y specs/003-order-module/validation/coverage.md, sin upgrades de dependencias.
- [X] T004 [P] Capturar DDL y datos sintéticos de versiones 1, 2 y 3 a partir de entidades/migraciones vigentes en data/src/androidTest/assets/pedidos/legacy-v1.sql, data/src/androidTest/assets/pedidos/legacy-v2.sql y data/src/androidTest/assets/pedidos/legacy-v3.sql; no generar falsos esquemas Room históricos.
- [X] T005 [P] Documentar comandos, límites de capas, manejo de errores y convenciones de tags/estado en feature/orders/README.md.

## Phase 2: Foundational — contratos y base compartida

**Goal**: Establecer tipos, persistencia v4 y herramientas deterministas que bloquean las historias.

**Independent Test / Checkpoint**: La migración preserva configuración/productos, creación fresca y actualización producen esquema equivalente, y domain no depende de Android/Room/Hilt.

- [X] T006 [P] Escribir pruebas de Money, conversión HALF_UP, no finitos, cero tras redondeo y overflow en domain/src/test/java/com/famessa/deli_antojito/domain/model/MoneyTest.kt.
- [X] T007 [P] Escribir pruebas de migración física v3→4, cadenas v1/v2→4, índices únicos, FK, secuencia y equivalencia con base fresca en data/src/androidTest/java/com/famessa/deli_antojito/data/DB/PedidoMigrationTest.kt usando fixtures T004.
- [X] T008 Definir Money con centavos Long, conversión exacta desde catálogo y aritmética comprobada con KDoc en domain/src/main/java/com/famessa/deli_antojito/domain/model/Money.kt; hacer pasar T006.
- [X] T009 Definir Pedido, PedidoLinea, PedidoResumen, EstadoPedido, PedidoDraft, comandos, consultas, resultados tipados y resumen según data-model en domain/src/main/java/com/famessa/deli_antojito/domain/model/Pedido.kt, domain/src/main/java/com/famessa/deli_antojito/domain/model/PedidoDraft.kt, domain/src/main/java/com/famessa/deli_antojito/domain/model/PedidoConsulta.kt y domain/src/main/java/com/famessa/deli_antojito/domain/model/PedidoResult.kt; no exponer Throwable a UI.
- [X] T010 Declarar repositorio completo y contrato de reloj/jornada puro en domain/src/main/java/com/famessa/deli_antojito/domain/repository/PedidoRepository.kt y domain/src/main/java/com/famessa/deli_antojito/domain/time/BusinessDayProvider.kt; incluir conteo global observable para distinguir base vacía de consulta sin coincidencias.
- [X] T011 Crear entidades e índices v4, relaciones hacia pedido con RESTRICT y referencia histórica sin FK a productos en data/src/main/java/com/famessa/deli_antojito/data/Entities/Pedido.kt, data/src/main/java/com/famessa/deli_antojito/data/Entities/PedidoDetalle.kt, data/src/main/java/com/famessa/deli_antojito/data/Entities/PedidoTransicion.kt y data/src/main/java/com/famessa/deli_antojito/data/Entities/PedidoSecuencia.kt.
- [X] T012 Implementar MIGRATION_3_4 y secuencia singleton tanto en migración como creación fresca en data/src/main/java/com/famessa/deli_antojito/data/DB/AppDatabase.kt y data/src/main/java/com/famessa/deli_antojito/data/di/DatabaseModule.kt; registrar cadena completa sin fallback destructivo; habilitar schemaLocation en data/build.gradle.kts y versionar data/schemas/.
- [X] T013 Crear fakes de repositorio/reloj, fixture de Room real e inyección controlada de fallos para rollback, sin hooks de prueba en UI, en domain/src/test/java/com/famessa/deli_antojito/domain/usecase/pedido/FakePedidoRepository.kt, feature/orders/src/test/java/com/famessa/deli_antojito/feature/orders/FakeBusinessDayProvider.kt y data/src/androidTest/java/com/famessa/deli_antojito/data/fixtures/PedidoDatabaseFixture.kt.
- [ ] T014 Ejecutar pruebas T006/T007 y registrar esquema preservado, campos previos intactos y resultado de compilación de fundamentos en specs/003-order-module/validation/foundation.md; no iniciar historias con fallos de migración.

## Phase 3: US1 — Consultar pedidos y detalle (P1, MVP)

**Goal**: Mostrar pedidos existentes en orden, abrir detalle y distinguir carga, vacío, error y no encontrado.

**Independent Test / Checkpoint**: Sembrar cuatro estados con fixture: abrir desde Inicio, comprobar tarjetas/orden/detalle, recuperar error con Reintentar y conservar configuración inicial obligatoria.

### Tests — escribir primero y comprobar fallo antes de implementar

- [X] T015 [P] [US1] Escribir pruebas de lecturas y mappers para orden creación/id, detalle con líneas, estado inválido, NotFound y errores explícitos en data/src/androidTest/java/com/famessa/deli_antojito/data/Repository/PedidoReadContractTest.kt y data/src/test/java/com/famessa/deli_antojito/data/mappers/PedidoMappersTest.kt.
- [X] T016 [P] [US1] Escribir pruebas de carga/éxito/vacío/error/reintento y detalle no encontrado con fakes en feature/orders/src/test/java/com/famessa/deli_antojito/feature/orders/list/OrdersViewModelTest.kt y feature/orders/src/test/java/com/famessa/deli_antojito/feature/orders/detail/OrderDetailViewModelTest.kt.
- [X] T017 [P] [US1] Escribir pruebas UI de tarjeta completa, número >99999, total/fecha/estado, vacío, error y detalle en feature/orders/src/androidTest/java/com/famessa/deli_antojito/feature/orders/OrdersReadScreenTest.kt.
- [X] T018 [P] [US1] Escribir pruebas de entrada desde Inicio, Back, restauración del destino y regresión del gate inicial en app/src/androidTest/java/com/famessa/deli_antojito/OrdersNavigationTest.kt.

### Implementation

- [X] T019 [US1] Implementar proyecciones ligeras, lectura de agregado, conteo global y mapeo validado en data/src/main/java/com/famessa/deli_antojito/data/DAO/PedidoDao.kt, data/src/main/java/com/famessa/deli_antojito/data/mappers/PedidoMappers.kt y data/src/main/java/com/famessa/deli_antojito/data/Repository/PedidoRepositoryImpl.kt; registrar binding en data/src/main/java/com/famessa/deli_antojito/data/di/RepositoryModule.kt; escrituras se completan en sus historias antes de publicación final.
- [X] T020 [US1] Implementar casos de lectura y sus providers en domain/src/main/java/com/famessa/deli_antojito/domain/usecase/pedido/ObservePedidosUseCase.kt, domain/src/main/java/com/famessa/deli_antojito/domain/usecase/pedido/ObservePedidoUseCase.kt y app/src/main/java/com/famessa/deli_antojito/di/PedidoUseCaseModule.kt.
- [X] T021 [US1] Crear estado inmutable, ViewModels de lista/detalle y Routes con colección ligada al lifecycle y reintento real en feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/list/OrdersUiState.kt, feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/list/OrdersViewModel.kt, feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/list/OrdersRoute.kt, feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/detail/OrderDetailViewModel.kt y feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/detail/OrderDetailRoute.kt.
- [X] T022 [US1] Crear pantallas consultivas, tarjetas y badges con texto/icono/color, tokens Aqua específicos y tags en feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/list/OrdersScreen.kt, feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/detail/OrderDetailScreen.kt, feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/components/OrderCard.kt, feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/components/OrderStatusBadge.kt y core/ui/src/main/java/com/famessa/deli_antojito/core/ui/theme/OrderColors.kt.
- [X] T023 [US1] Sustituir booleanos por destinos tipados/back stack guardables, añadir entrada Pedidos, menú y navegación inferior Inicio/Productos/Pedidos en app/src/main/java/com/famessa/deli_antojito/navigation/MainDestination.kt, app/src/main/java/com/famessa/deli_antojito/navigation/MainNavigation.kt, app/src/main/java/com/famessa/deli_antojito/MainActivity.kt y feature/home/src/main/java/com/famessa/deli_antojito/feature/home/HomeView.kt; conservar rutas previas y ViewModels por identidad.
- [ ] T024 [US1] Ejecutar pruebas US1 y recorrido offline de consulta usando semilla de prueba, registrar aceptación y limitación de MVP consultivo en specs/003-order-module/validation/us1.md; las acciones de historias futuras no deben simular éxito.

## Phase 4: US2 — Registrar un pedido (P1)

**Goal**: Crear un pedido pendiente exacto, único e indivisible desde productos activos.

**Independent Test / Checkpoint**: Registrar 2 × $12.50 y 3 × $7.10: total $46.30, único número, persiste tras reiniciar; reintento con mismo token devuelve el mismo registro.

### Tests — escribir primero y comprobar fallo antes de implementar

- [X] T025 [P] [US2] Escribir pruebas de cliente vacío, líneas, cantidades, agrupación por producto/precio, captura activa y $46.30 en domain/src/test/java/com/famessa/deli_antojito/domain/usecase/pedido/CreatePedidoUseCaseTest.kt y domain/src/test/java/com/famessa/deli_antojito/domain/usecase/pedido/CapturePedidoProductUseCaseTest.kt.
- [X] T026 [P] [US2] Escribir pruebas Room de token repetido, respuesta perdida, dos altas concurrentes, agotamiento de secuencia, producto no disponible, fallo entre cabecera/líneas y persistencia tras reapertura en data/src/androidTest/java/com/famessa/deli_antojito/data/Repository/PedidoCreateContractTest.kt.
- [X] T027 [P] [US2] Escribir pruebas de validación por campo, restauración de token/borrador, doble pulsación, error sin pérdida de entradas y éxito consumido una vez en feature/orders/src/test/java/com/famessa/deli_antojito/feature/orders/form/OrderCreateViewModelTest.kt.
- [X] T028 [P] [US2] Escribir pruebas de +, selección activa, catálogo vacío, cantidades inválidas, precio no editable, Guardar, errores y descarte de alta en feature/orders/src/androidTest/java/com/famessa/deli_antojito/feature/orders/OrderCreateScreenTest.kt.

### Implementation

- [X] T029 [US2] Implementar validación, cálculo y snapshot al incorporar producto en domain/src/main/java/com/famessa/deli_antojito/domain/usecase/pedido/ValidatePedidoUseCase.kt, domain/src/main/java/com/famessa/deli_antojito/domain/usecase/pedido/CalculatePedidoUseCase.kt y domain/src/main/java/com/famessa/deli_antojito/domain/usecase/pedido/CapturePedidoProductUseCase.kt; tomar catálogo por ProductoRepository sin depender de feature:product.
- [X] T030 [US2] Implementar creación con token estable y validación en domain/src/main/java/com/famessa/deli_antojito/domain/usecase/pedido/CreatePedidoUseCase.kt; ampliar providers en app/src/main/java/com/famessa/deli_antojito/di/PedidoUseCaseModule.kt.
- [X] T031 [US2] Implementar reserva de secuencia, búsqueda de token, comprobación de referencias activas e inserción atómica en data/src/main/java/com/famessa/deli_antojito/data/DAO/PedidoDao.kt y data/src/main/java/com/famessa/deli_antojito/data/Repository/PedidoRepositoryImpl.kt; persistir claves Unicode y numero_busqueda; propagar cancelación y sanitizar fallos.
- [X] T032 [US2] Implementar reloj epoch millis inyectable compatible API 25 para altas en data/src/main/java/com/famessa/deli_antojito/data/time/LocalBusinessDayProvider.kt y app/src/main/java/com/famessa/deli_antojito/di/PedidoUseCaseModule.kt; no añadir java.time ni elevar minSdk.
- [X] T033 [US2] Crear OrderFormState/ViewModel con cantidades como texto, selector por catálogo, Money de domain, token y snapshots compactos restaurables en feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/form/OrderFormState.kt y feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/form/OrderFormViewModel.kt; no guardar imágenes ni catálogo en SavedStateHandle.
- [X] T034 [US2] Implementar formulario, selector, cantidades, total, guardado protegido y diálogo de descarte en feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/form/OrderFormScreen.kt, feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/form/OrderFormRoute.kt y feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/components/OrderProductSelector.kt; conectar OrderNew y retorno a listado en app/src/main/java/com/famessa/deli_antojito/navigation/MainNavigation.kt.
- [ ] T035 [US2] Ejecutar aceptación US2, reabrir base y simular reintento de guardado; registrar evidencia de unicidad/rollback y funcionamiento offline en specs/003-order-module/validation/us2.md.

## Phase 5: US3 — Editar pedidos pendientes (P1)

**Goal**: Modificar contenido pendiente preservando identidad, snapshots y cambios ajenos.

**Independent Test / Checkpoint**: Con pendiente sembrado, cambiar cliente/líneas/cantidad/observaciones; conservar número/creación/precios históricos y rechazar edición tras cambio de revisión o estado.

### Tests — escribir primero y comprobar fallo antes de implementar

- [X] T036 [P] [US3] Escribir pruebas de campos permitidos, snapshot conservado y cantidades recalculadas en domain/src/test/java/com/famessa/deli_antojito/domain/usecase/pedido/UpdatePedidoUseCaseTest.kt.
- [X] T037 [P] [US3] Escribir pruebas Room de dos editores, líneas ajenas, producto histórico borrado/inactivo, nueva referencia inválida y fallo intermedio con rollback en data/src/androidTest/java/com/famessa/deli_antojito/data/Repository/PedidoUpdateContractTest.kt.
- [X] T038 [P] [US3] Escribir pruebas de edición/restauración, estado no editable, recarga con confirmación y Back/cambio de sección con dirty en feature/orders/src/test/java/com/famessa/deli_antojito/feature/orders/form/OrderEditViewModelTest.kt y feature/orders/src/androidTest/java/com/famessa/deli_antojito/feature/orders/OrderEditScreenTest.kt.

### Implementation

- [X] T039 [US3] Implementar caso UpdatePedidoUseCase y provider usando revisión esperada y draft explícito de líneas existentes/nuevas en domain/src/main/java/com/famessa/deli_antojito/domain/usecase/pedido/UpdatePedidoUseCase.kt y app/src/main/java/com/famessa/deli_antojito/di/PedidoUseCaseModule.kt.
- [X] T040 [US3] Implementar lectura vigente, política pendiente, validación de pertenencia, actualización condicional revisión/estado, preservación de snapshots y escritura indivisible en data/src/main/java/com/famessa/deli_antojito/data/Repository/PedidoRepositoryImpl.kt y data/src/main/java/com/famessa/deli_antojito/data/DAO/PedidoDao.kt; cero filas actualizadas produce Conflict y rollback.
- [X] T041 [US3] Extender formulario para carga una vez, edición sin reemplazar entradas restauradas, conflicto conservando borrador y recarga protegida en feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/form/OrderFormViewModel.kt y feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/form/OrderFormScreen.kt.
- [X] T042 [US3] Conectar Editar solo pendiente y OrderEdit(id) con retorno a detalle, BackHandler y guardia de descarte para menú/barra inferior en feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/detail/OrderDetailScreen.kt, feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/form/OrderFormRoute.kt y app/src/main/java/com/famessa/deli_antojito/navigation/MainNavigation.kt.
- [ ] T043 [US3] Ejecutar pruebas US3 con pendiente sembrado y terminales, registrar inmutabilidad de identidad/precios y ausencia de sobrescrituras en specs/003-order-module/validation/us3.md.

## Phase 6: US4 — Preparación y cancelación (P1)

**Goal**: Aplicar únicamente cuatro transiciones y guardar cada cambio con fecha/hora.

**Independent Test / Checkpoint**: Con un fixture por estado comprobar las 16 parejas, confirmar/rechazar cancelación y verificar terminales e historial sin depender del alta.

### Tests — escribir primero y comprobar fallo antes de implementar

- [X] T044 [P] [US4] Escribir matriz de 16 parejas con cuatro válidas y reloj fake en domain/src/test/java/com/famessa/deli_antojito/domain/usecase/pedido/ChangePedidoStateUseCaseTest.kt.
- [X] T045 [P] [US4] Escribir pruebas Room de historial append, revisión/fecha, terminales, transición simultánea con edición y fallo entre cabecera/historial con rollback en data/src/androidTest/java/com/famessa/deli_antojito/data/Repository/PedidoStateContractTest.kt.
- [X] T046 [P] [US4] Escribir pruebas de acciones por estado, doble pulsación, confirmación exacta de cancelar, rechazo del diálogo, error/conflicto y retorno actualizado en feature/orders/src/test/java/com/famessa/deli_antojito/feature/orders/detail/OrderStateViewModelTest.kt y feature/orders/src/androidTest/java/com/famessa/deli_antojito/feature/orders/OrderStateScreenTest.kt.

### Implementation

- [X] T047 [US4] Implementar política pura de transición, caso de uso y providers en domain/src/main/java/com/famessa/deli_antojito/domain/usecase/pedido/ChangePedidoStateUseCase.kt y app/src/main/java/com/famessa/deli_antojito/di/PedidoUseCaseModule.kt; no habilitar reapertura o borrado.
- [X] T048 [US4] Implementar actualización condicional por revisión/estado e inserción de transición dentro de la misma transacción en data/src/main/java/com/famessa/deli_antojito/data/DAO/PedidoDao.kt y data/src/main/java/com/famessa/deli_antojito/data/Repository/PedidoRepositoryImpl.kt; edición incrementa revisión sin crear historial de estado.
- [X] T049 [US4] Implementar acciones tipadas, exclusión de envíos simultáneos, errores/conflicto y actualización reactiva en feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/detail/OrderDetailViewModel.kt.
- [X] T050 [US4] Añadir botones permitidos y diálogo «¿Estás seguro de que deseas cancelar este pedido? Esta acción no se puede deshacer.» en feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/detail/OrderDetailScreen.kt y feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/detail/OrderDetailRoute.kt; nunca mostrar eliminación.
- [ ] T051 [US4] Ejecutar US4 y comprobar pedido/historial tras reapertura, conflictos contra edición y lista reactiva; registrar evidencia en specs/003-order-module/validation/us4.md.

## Phase 7: US5 — Buscar y filtrar pedidos (P2)

**Goal**: Aplicar búsqueda Unicode y filtros combinados sin bloquear interacción.

**Independent Test / Checkpoint**: Usar pedidos sembrados con fechas/importes/estados distintos; probar coincidencia parcial por número/cliente, intersección vacía y Aplicar/Limpiar/Cancelar sin crear pedidos nuevos.

### Tests — escribir primero y comprobar fallo antes de implementar

- [X] T052 [P] [US5] Escribir pruebas NFC/Locale.ROOT, prefijo #, espacios, límites monetarios/fechas y distinción sin restricción versus intersección vacía en domain/src/test/java/com/famessa/deli_antojito/domain/usecase/pedido/ValidatePedidoConsultaUseCaseTest.kt.
- [X] T053 [P] [US5] Escribir pruebas de consultas parametrizadas con MARÍA/María, Unicode descompuesto, %/_ literales, orden estable y rangos abiertos/inclusivos en data/src/androidTest/java/com/famessa/deli_antojito/data/Repository/PedidoSearchContractTest.kt.
- [X] T054 [P] [US5] Escribir pruebas de debounce 300 ms, cancelación de resultados obsoletos, pestaña inmediata, borrador de filtros y estado restaurado en feature/orders/src/test/java/com/famessa/deli_antojito/feature/orders/list/OrdersFiltersViewModelTest.kt; pruebas UI en feature/orders/src/androidTest/java/com/famessa/deli_antojito/feature/orders/OrderFiltersScreenTest.kt.

### Implementation

- [X] T055 [US5] Implementar normalización compartida, validación/intersección y parsing de importes sin Double ni redondeo silencioso en domain/src/main/java/com/famessa/deli_antojito/domain/usecase/pedido/ValidatePedidoConsultaUseCase.kt y domain/src/main/java/com/famessa/deli_antojito/domain/model/PedidoSearchNormalizer.kt; reutilizar normalización al guardar y editar.
- [X] T056 [US5] Extender consulta Room y adaptación de rangos/estados/normalización en data/src/main/java/com/famessa/deli_antojito/data/DAO/PedidoDao.kt y data/src/main/java/com/famessa/deli_antojito/data/Repository/PedidoRepositoryImpl.kt; exponer cero resultados para intersección vacía y conteo global independiente.
- [X] T057 [US5] Implementar límites civiles mediante Calendar/TimeZone y adaptador DatePicker sin desfase UTC en data/src/main/java/com/famessa/deli_antojito/data/time/LocalBusinessDayProvider.kt y feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/components/OrderDateAdapter.kt; fechas finales incluyen su día completo sin sumar 24 h fijas.
- [X] T058 [US5] Extender OrdersUiState/ViewModel con texto, pestaña, criterios aplicados y borrador de panel, flatMapLatest/debounce y restauración de consulta en feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/list/OrdersUiState.kt y feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/list/OrdersViewModel.kt.
- [X] T059 [US5] Implementar búsqueda, cinco pestañas desplazables, panel y mensajes por campo, Limpiar/Cancelar/Aplicar y sin resultados en feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/components/OrderFilters.kt, feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/components/OrderSearchBar.kt y feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/list/OrdersScreen.kt; limpiar panel no borra búsqueda/pestaña.
- [ ] T060 [US5] Ejecutar US5 incluyendo vuelta desde detalle y actualización de pedido que deja de satisfacer filtros; registrar evidencia funcional y responsividad en specs/003-order-module/validation/us5.md.

## Phase 8: US6 — Resumen del día (P2)

**Goal**: Mostrar cuatro indicadores independientes del listado y de la jornada anterior.

**Independent Test / Checkpoint**: Sembrar hoy 2 pendientes, 1 preparación, 1 completado, 1 cancelado y datos de ayer; esperar 5/2/1/1, sin cambios por filtros y con actualización a medianoche.

### Tests — escribir primero y comprobar fallo antes de implementar

- [X] T061 [P] [US6] Escribir pruebas del día observable, medianoche, reanudación, cambio de reloj/zona y días con duración distinta de 24 h en data/src/test/java/com/famessa/deli_antojito/data/time/LocalBusinessDayProviderTest.kt; emplear adaptadores fake para eventos Android.
- [X] T062 [P] [US6] Escribir pruebas de agregado por fecha de creación y total incluyendo cancelados en data/src/androidTest/java/com/famessa/deli_antojito/data/Repository/PedidoDailySummaryContractTest.kt; probar caso de uso con reloj fake en domain/src/test/java/com/famessa/deli_antojito/domain/usecase/pedido/ObserveDailyOrdersSummaryUseCaseTest.kt.
- [X] T063 [P] [US6] Escribir pruebas de errores independientes entre resumen/lista, cambios de filtros sin recalcular universo y presentación desplazable de cuatro indicadores en feature/orders/src/test/java/com/famessa/deli_antojito/feature/orders/list/OrdersSummaryViewModelTest.kt y feature/orders/src/androidTest/java/com/famessa/deli_antojito/feature/orders/OrderDailySummaryScreenTest.kt.

### Implementation

- [X] T064 [US6] Completar proveedor observable de jornada en data/src/main/java/com/famessa/deli_antojito/data/time/LocalBusinessDayProvider.kt: temporizador a siguiente medianoche, invalidación por fecha/hora/zona y señal de reanudación; liberar receptores y timers al cancelar colección.
- [X] T065 [US6] Implementar consulta agregada y observación de resumen sin filtros ni detalles en data/src/main/java/com/famessa/deli_antojito/data/DAO/PedidoDao.kt y data/src/main/java/com/famessa/deli_antojito/data/Repository/PedidoRepositoryImpl.kt; invalidación tras altas/transiciones sin incluir pedidos creados ayer.
- [X] T066 [US6] Implementar flatMapLatest por jornada y composición de fecha/conteos en domain/src/main/java/com/famessa/deli_antojito/domain/usecase/pedido/ObserveDailyOrdersSummaryUseCase.kt y app/src/main/java/com/famessa/deli_antojito/di/PedidoUseCaseModule.kt; conectar señal de reanudación en app/src/main/java/com/famessa/deli_antojito/MainActivity.kt.
- [X] T067 [US6] Integrar flujo/errores/reintento independiente del resumen en feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/list/OrdersViewModel.kt y componente desplazable en feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/components/OrdersDailySummary.kt y feature/orders/src/main/java/com/famessa/deli_antojito/feature/orders/list/OrdersScreen.kt; no ocultar lista válida por fallo del resumen.
- [ ] T068 [US6] Ejecutar US6 con medianoche/reanudación/cambio de zona y transición de pedido antiguo; registrar conteos y aislamiento de filtros en specs/003-order-module/validation/us6.md.

## Phase 9: Polish — validación transversal

**Goal**: Cerrar requisitos de calidad y documentar evidencia real sin atribuir resultados aún no ejecutados.

**Independent Test / Checkpoint**: Todas las historias aceptadas; build/lint/pruebas/cobertura y mediciones registradas, o gates pendientes explícitos con responsable/riesgo/fecha.

- [ ] T069 Añadir y ejecutar escenarios de accesibilidad API 25/35, fuente 200%, 48 dp, semántica, teclado, scroll/resumen, orientación y restauración en feature/orders/src/androidTest/java/com/famessa/deli_antojito/feature/orders/OrdersAccessibilityTest.kt y app/src/androidTest/java/com/famessa/deli_antojito/OrdersRestorationTest.kt; corregir pantallas afectadas.
- [X] T070 [P] Revisar fronteras, propagación de CancellationException y sanitización de errores/logs, sin PII/tokens/SQL personal; registrar inspección de domain/src/main/, data/src/main/ y feature/orders/src/main/ en specs/003-order-module/validation/privacy-architecture.md.
- [X] T071 [P] Completar KDoc público y README con rutas, reglas, fallos y comandos, comprobar dependencias solo catalogadas y sin upgrades en feature/orders/README.md, gradle/libs.versions.toml y specs/003-order-module/validation/maintainability.md.
- [ ] T072 Crear datos sintéticos e instrumentación reproducible de 1,000 pedidos sin nueva dependencia en app/src/androidTest/java/com/famessa/deli_antojito/OrdersPerformanceTest.kt; ejecutar baseline release 5 calentamientos/30 muestras del quickstart y registrar listado <2 s y p95 consultas <1 s en specs/003-order-module/validation/performance.md.
- [ ] T073 [P] Realizar sesiones con cinco usuarios representativos para SC-003 y registrar tiempos anónimos de alta <2 min/localización <30 s y tasa ≥4/5 en specs/003-order-module/validation/usability.md; si no hay participantes disponibles marcar gate pendiente con responsable y seguimiento, sin inventar resultados.
- [ ] T074 Ejecutar comandos de quickstart para build, lint, unitarias, Room, UI y regresiones de productos/configuración/Home; generar cobertura ≥70% domain/ViewModels tocados y registrar comandos/resultados/limitaciones en specs/003-order-module/validation/test-results.md y specs/003-order-module/validation/coverage.md.
- [ ] T075 Ejecutar recorrido offline completo y validar preservación histórica/reinicio, navegación, errores y criterios SC-001–SC-008; completar trazabilidad FR-001–FR-022 con evidencia en specs/003-order-module/validation/acceptance.md.
- [X] T076 Revisar diff, enlaces, artefactos y esquema versionado; excluir outputs de build/secretos/bases personales, actualizar comandos reales y gates pendientes con responsable/riesgo/fecha en specs/003-order-module/quickstart.md y specs/003-order-module/validation/release-readiness.md.

## Dependencies & Execution Order

### Phase dependencies

Setup T001–T005 → fundamentos T006–T014 → historias → cierre T069–T076. T004 debe completarse antes de T012: conservar evidencia de la base previa antes de cambiar el esquema. T006/T007 se escriben antes de T008/T011/T012 y deben pasar en T014.

Dentro de cada historia: escribir sus pruebas primero; implementar contratos/casos de uso antes de data, data antes de integración UI y cerrar su checkpoint antes de marcarla completa. Los tests que mencionan APIs todavía inexistentes pueden comenzar con esqueletos mínimos; comprobar fallo de comportamiento, no considerar un simple error de compilación evidencia suficiente.

| Historia | Tareas | Prerrequisito de implementación | Prueba independiente |
|---|---|---|---|
| US1 (P1) | T015–T024 | T014 | Datos sembrados en cuatro estados; listado/detalle/error sin alta. |
| US2 (P1) | T025–T035 | US1, por host/Routes/lecturas | Catálogo sintético; guardar y reabrir un pedido exacto. |
| US3 (P1) | T036–T043 | US1 + US2, por formulario/cálculo/reloj | Pendiente sembrado; editar y rechazar conflictos sin ejecutar alta. |
| US4 (P1) | T044–T051 | US1 + US2; T048/T051 después de T040 para conflicto con edición | Pedidos sembrados; matriz de estados, historial y cancelación. |
| US5 (P2) | T052–T060 | US1 + US2, por consulta/reloj; integrar después de US4 en la secuencia recomendada | Historial sembrado; filtros/búsqueda sin altas nuevas. |
| US6 (P2) | T061–T068 | US5, por rango civil y consulta; US2/US4 para invalidaciones de escritura | Reloj fake + pedidos hoy/ayer; 5/2/1/1 y medianoche. |

```text
Setup → Fundamentos → US1 → US2 → US3
                              ├──→ US4 (cierre después de escritura US3)
                              └──→ US5 → US6
US1 + US2 + US3 + US4 + US5 + US6 → Polish
```

Las historias se verifican con fixtures independientes; reutilizar pantalla, repositorio y host crea dependencias reales de implementación. No se promete implementación simultánea de historias que editan los mismos archivos. Orden recomendado: US1 → US2 → US3 → US4 → US5 → US6.

### Parallel opportunities and examples

Hay 27 tareas [P]. Son oportunidades dentro de una fase o bloque, después de sus prerrequisitos. T001/T002/T003 preceden los tests que requieren módulos/cobertura; T004 y T005 pueden ejecutarse simultáneamente después de preparar el entorno. No ejecutar Gradle concurrentemente contra el mismo árbol de outputs.

| Historia | Ejemplo de tareas simultáneas | Condición |
|---|---|---|
| US1 | T015 (Room/mappers) + T016 (ViewModels) + T017 (UI) + T018 (navegación) | Fundamentos listos; cada tarea escribe tests distintos antes de implementación. |
| US2 | T025 (domain) + T026 (Room) + T027 (ViewModel) + T028 (UI) | US1 y contratos listos; no modificar fakes compartidos simultáneamente. |
| US3 | T036 (domain) + T037 (Room) + T038 (presentación) | Formulario/cálculo de US2 disponible. |
| US4 | T044 (matriz domain) + T045 (Room) + T046 (presentación) | Prerrequisitos de la tabla completos; antes de implementar estados. |
| US5 | T052 (reglas) + T053 (consultas) + T054 (presentación) | Lecturas y reloj disponibles; mantener archivos de tests separados. |
| US6 | T061 (reloj) + T062 (agregado/caso de uso) + T063 (presentación) | US5 terminado; usar fakes ya definidos. |

T070, T071 y T073 trabajan en evidencia diferente y pueden prepararse sin competir por los archivos de producción, una vez terminadas las historias. Si una revisión exige modificar archivos compartidos, serializar esa corrección. [P] describe posibilidades de organización; no solicita por sí mismo agentes adicionales.

## Implementation Strategy

### MVP first

Completar Setup + Foundational + US1 (T001–T024) y demostrar consulta con datos sintéticos persistidos. Es un MVP de consulta; todavía no permite operar pedidos nuevos. Mantener acciones futuras no disponibles y documentar ese límite, sin publicar stubs como módulo final.

Para un primer incremento operativo, añadir US2 (T025–T035). Después añadir edición, estados, búsqueda/filtros y resumen. No marcar el alcance completo ni distribuir como entrega final mientras falten escrituras, accesibilidad o gates.

### Incremental delivery

Cada checkpoint requiere pruebas de su historia pasando y regresión de lo ya entregado. Mantener KDoc y manejo de errores al implementar cada contrato; las tareas de cierre verifican su cobertura, no posponen esos requisitos. Marcar una casilla solo al completar implementación y verificación; las mediciones y sesiones con usuarios requieren evidencia real.

No se implementa un backend ni endpoints HTTP. Los contratos de dominio y UI se cubren en las fases correspondientes: lectura US1, creación US2, edición US3, transiciones US4, consultas US5 y jornada/resumen US6. Las cuatro tablas se crean juntas para una única migración v4; sus comportamientos se implementan por historia.

## Requirement Traceability

| Requisito | Tareas principales |
|---|---|
| FR-001 navegación | T018, T023, T034, T042, T069 |
| FR-002 listado | T015–T017, T019–T022 |
| FR-003 búsqueda | T052–T055, T057–T060 |
| FR-004 pestañas | T054, T058–T060 |
| FR-005 filtros avanzados | T052–T060 |
| FR-006 rangos | T052, T053, T055–T057, T059 |
| FR-007 validaciones alta | T006, T008, T025–T029, T033, T034 |
| FR-008 número único | T007, T011, T012, T026, T031 |
| FR-009 cálculo e histórico | T006, T008, T025, T029, T031, T037, T040 |
| FR-010 edición | T036–T043 |
| FR-011 descarte | T028, T034, T038, T041, T042, T069 |
| FR-012 detalle/acciones | T017, T021, T022, T038, T042, T046, T050 |
| FR-013 transiciones | T044–T051 |
| FR-014 historial/fechas | T011, T032, T045, T047, T048, T051 |
| FR-015 cancelación/no eliminación | T010, T044–T051, T070 |
| FR-016 resumen | T061–T068 |
| FR-017 persistencia/offline | T004, T007, T012, T014, T026, T035, T037, T051, T075 |
| FR-018 atomicidad/idempotencia | T026, T031, T037, T040, T045, T048 |
| FR-019 estados/error/reintento | T016, T017, T021, T027, T033, T038, T046, T054, T063, T067 |
| FR-020 identidad visual | T017, T022, T050, T059, T069 |
| FR-021 accesibilidad | T022, T034, T059, T063, T067, T069, T075 |
| FR-022 privacidad | T019, T031, T070, T075 |

SC-001/002 se verifican en T072; SC-003 en T073; SC-004 en T006/T025/T036 y T075; SC-005 en T044/T045/T051; SC-006 en T026/T037/T045 y T075; SC-007 en T061/T062/T068; SC-008 en T069/T075. T074 concentra los gates automatizados y cobertura. T076 comprueba cierre documental y exclusión de artefactos no entregables.

## Generation Validation

76 tareas consecutivas T001–T076: Setup 5, Foundational 9, US1 10, US2 11, US3 8, US4 8, US5 9, US6 8, Polish 8. Todas tienen checkbox, ID y paths; las 54 tareas de historias llevan etiqueta US correspondiente. Hay 27 oportunidades [P] limitadas a archivos/bloques independientes.

Esta fase modifica únicamente este documento; por ser generación documental no ejecuta pruebas de implementación. No se omiten los tests futuros exigidos por la especificación.

Hooks before_tasks/after_tasks: la extensión Git ofrece `/speckit.git.commit` como acción opcional; auto_commit.before_tasks y auto_commit.after_tasks están desactivados en la configuración, por lo que no se crea commit.

## Implementation Progress — 2026-10-21

64 tareas de preparación, contratos, implementación, escritura de pruebas y revisión de artefactos completadas. Las 12 casillas pendientes incluyen ejecución en Android, mediciones y cierre de validación; no se marcan como aprobadas por haber compilado sus APK.

El usuario autorizó continuar preparando lo verificable sin dispositivo. T014 conserva pendiente su gate de migración ejecutada; se avanzó con pruebas JVM y compilación. Los resultados y límites están en validation/foundation.md, validation/test-results.md y validation/release-readiness.md. No se atribuye el ciclo rojo/verde a todas las pruebas: está registrado explícitamente para Money y la regresión de medianoche; las demás se validan con sus resultados reales.

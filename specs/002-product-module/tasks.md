# Tasks: Product Module

**Input**: Design documents from `/specs/002-product-module/`

**Prerequisites**: `plan.md`, `spec.md`, `research.md`, `data-model.md`, `contracts/ui-contract.md`, `quickstart.md`

**Tests**: Mandatory for this feature. Write failing tests before implementation for validation, persistence, ViewModel and UI behavior.

**Organization**: Tasks are grouped by user story so each story can be implemented and verified independently.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel because it touches different files and does not depend on incomplete tasks
- **[Story]**: User story label, used only in user story phases
- Every task includes an exact file path

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Create the product feature module and wire project structure.

- [X] T001 Add `:feature:product` to `settings.gradle.kts`
- [X] T002 Create product feature Gradle configuration in `feature/product/build.gradle.kts`
- [X] T003 [P] Create product feature source directory in `feature/product/src/main/java/com/famessa/deli_antojito/feature/product/`
- [X] T004 [P] Create product feature unit test directory in `feature/product/src/test/java/com/famessa/deli_antojito/feature/product/`
- [X] T005 [P] Create product feature android test directory in `feature/product/src/androidTest/java/com/famessa/deli_antojito/feature/product/`
- [X] T006 Add `implementation(project(":feature:product"))` to `app/build.gradle.kts`
- [X] T007 [P] Add product module README with scope, routes and image-source notes in `feature/product/README.md`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Evolve the shared product domain/data contract needed by every story.

**CRITICAL**: No user story work can begin until this phase is complete.

- [X] T008 [P] Add product validation result model in `domain/src/main/java/com/famessa/deli_antojito/domain/model/ProductoValidationResult.kt`
- [X] T009 Update product domain model fields and KDoc in `domain/src/main/java/com/famessa/deli_antojito/domain/model/Producto.kt`
- [X] T010 Update product repository contract with observe, get, save, name availability and physical delete APIs in `domain/src/main/java/com/famessa/deli_antojito/domain/repository/ProductoRepository.kt`
- [X] T011 [P] Add validate-product use case in `domain/src/main/java/com/famessa/deli_antojito/domain/usecase/producto/ValidateProductoUseCase.kt`
- [X] T012 [P] Add product save result model in `domain/src/main/java/com/famessa/deli_antojito/domain/model/ProductoSaveResult.kt`
- [X] T013 [P] Add product delete result model in `domain/src/main/java/com/famessa/deli_antojito/domain/model/ProductoDeleteResult.kt`
- [X] T014 [P] Add product list summary model in `domain/src/main/java/com/famessa/deli_antojito/domain/model/ProductListSummary.kt`
- [X] T015 Update Room product entity fields, indices and KDoc in `data/src/main/java/com/famessa/deli_antojito/data/Entities/Producto.kt`
- [X] T016 Update product mappers for `img` and `activo` in `data/src/main/java/com/famessa/deli_antojito/data/mappers/Mappers.kt`
- [X] T017 Update product DAO queries for list ordering, id lookup, normalized-name lookup, insert/update and physical delete in `data/src/main/java/com/famessa/deli_antojito/data/DAO/ProductoDao.kt`
- [X] T018 Add Room migration for the existing `productos` schema in `data/src/main/java/com/famessa/deli_antojito/data/DB/AppDatabase.kt`
- [X] T019 Update product repository implementation for explicit result handling and duplicate-name guard in `data/src/main/java/com/famessa/deli_antojito/data/Repository/ProductoRepositoryImpl.kt`
- [X] T020 Add product use-case Hilt bindings in `app/src/main/java/com/famessa/deli_antojito/di/ProductoUseCaseModule.kt`
- [X] T021 Update product module navigation entry point in `app/src/main/java/com/famessa/deli_antojito/MainActivity.kt`

**Checkpoint**: Foundation ready. Stories can now be implemented and tested.

---

## Phase 3: User Story 1 - Consultar productos registrados (Priority: P1) MVP

**Goal**: User can open the product module, see loading/empty/loaded/error states, product rows, and total/active counts.

**Independent Test**: Open the product module with empty and populated local data; verify rows, placeholders, formatted prices, active status and summary counts render correctly.

### Tests for User Story 1

- [X] T022 [P] [US1] Add validation unit tests for listing-safe defaults in `domain/src/test/java/com/famessa/deli_antojito/domain/usecase/producto/ValidateProductoUseCaseTest.kt`
- [X] T023 [P] [US1] Add DAO list and summary fixture tests in `data/src/androidTest/java/com/famessa/deli_antojito/data/DAO/ProductoDaoTest.kt`
- [X] T024 [P] [US1] Add repository observe-products tests in `data/src/test/java/com/famessa/deli_antojito/data/Repository/ProductoRepositoryImplTest.kt`
- [X] T025 [P] [US1] Add ProductViewModel loading, empty, loaded and error state tests in `feature/product/src/test/java/com/famessa/deli_antojito/feature/product/ProductViewModelTest.kt`
- [X] T026 [P] [US1] Add product list Compose UI tests for empty and loaded states in `feature/product/src/androidTest/java/com/famessa/deli_antojito/feature/product/ProductListScreenTest.kt`

### Implementation for User Story 1

- [X] T027 [P] [US1] Add observe products use case in `domain/src/main/java/com/famessa/deli_antojito/domain/usecase/producto/ObserveProductosUseCase.kt`
- [X] T028 [US1] Implement product list UI state in `feature/product/src/main/java/com/famessa/deli_antojito/feature/product/ProductUiState.kt`
- [X] T029 [US1] Implement ProductViewModel list observation and summary calculation in `feature/product/src/main/java/com/famessa/deli_antojito/feature/product/ProductViewModel.kt`
- [X] T030 [US1] Implement ProductListScreen with summary, empty state, rows, placeholder image and retry UI in `feature/product/src/main/java/com/famessa/deli_antojito/feature/product/ProductListScreen.kt`
- [X] T031 [US1] Implement ProductListRoute collecting state and callbacks in `feature/product/src/main/java/com/famessa/deli_antojito/feature/product/ProductListRoute.kt`
- [X] T032 [US1] Connect product module entry navigation from home to product list in `feature/home/src/main/java/com/famessa/deli_antojito/feature/home/HomeView.kt`

**Checkpoint**: MVP complete. Product list is independently functional and testable.

---

## Phase 4: User Story 2 - Registrar producto (Priority: P2)

**Goal**: User can navigate from the list to add a product, choose an image from the device or camera, validate fields and image, save, and return to the list with the new product visible.

**Independent Test**: From the product list, tap add, enter valid product data, choose image from device or camera, save, and verify the product appears in the list; invalid inputs show messages and preserve form data.

### Tests for User Story 2

- [X] T033 [P] [US2] Add product validation tests for required name, max length, positive price, duplicate name, image type, image size and source cancel behavior in `domain/src/test/java/com/famessa/deli_antojito/domain/usecase/producto/ValidateProductoUseCaseTest.kt`
- [X] T034 [P] [US2] Add DAO insert and duplicate-name lookup tests in `data/src/androidTest/java/com/famessa/deli_antojito/data/DAO/ProductoDaoTest.kt`
- [X] T035 [P] [US2] Add repository create-product success and failure tests in `data/src/test/java/com/famessa/deli_antojito/data/Repository/ProductoRepositoryImplTest.kt`
- [X] T036 [P] [US2] Add ProductEditViewModel create, validation, image-source and save-error tests in `feature/product/src/test/java/com/famessa/deli_antojito/feature/product/ProductEditViewModelTest.kt`
- [X] T037 [P] [US2] Add add-product Compose UI tests for form validation, gallery/camera selection and successful save in `feature/product/src/androidTest/java/com/famessa/deli_antojito/feature/product/ProductEditScreenTest.kt`

### Implementation for User Story 2

- [X] T038 [P] [US2] Add save product use case in `domain/src/main/java/com/famessa/deli_antojito/domain/usecase/producto/SaveProductoUseCase.kt`
- [X] T039 [P] [US2] Add normalized product-name availability use case in `domain/src/main/java/com/famessa/deli_antojito/domain/usecase/producto/ValidateProductoNameAvailabilityUseCase.kt`
- [X] T040 [US2] Implement create-product path in repository in `data/src/main/java/com/famessa/deli_antojito/data/Repository/ProductoRepositoryImpl.kt`
- [X] T041 [US2] Implement product edit UI form state, image-source actions and validation messages in `feature/product/src/main/java/com/famessa/deli_antojito/feature/product/ProductUiState.kt`
- [X] T042 [US2] Implement ProductEditViewModel create mode, device/camera image handling, size validation, save flow and dirty-state tracking in `feature/product/src/main/java/com/famessa/deli_antojito/feature/product/ProductEditViewModel.kt`
- [X] T043 [US2] Implement ProductEditScreen add mode with image picker entry point, camera entry point, name, price, active selector, validation messages, discard confirmation and save button in `feature/product/src/main/java/com/famessa/deli_antojito/feature/product/ProductEditScreen.kt`
- [X] T044 [US2] Implement ProductEditRoute create mode navigation callbacks in `feature/product/src/main/java/com/famessa/deli_antojito/feature/product/ProductEditRoute.kt`
- [X] T045 [US2] Connect ProductListScreen add action to ProductEditRoute create mode in `feature/product/src/main/java/com/famessa/deli_antojito/feature/product/ProductListScreen.kt`

**Checkpoint**: Add-product flow works independently and updates the product list.

---

## Phase 5: User Story 3 - Editar producto (Priority: P3)

**Goal**: User can open an existing product, see current values, modify valid fields, save, and return to the list without creating a duplicate.

**Independent Test**: Select an existing product, edit name/price/image/status, save, and verify the same product id shows updated values in the list.

### Tests for User Story 3

- [X] T046 [P] [US3] Add get-product and edit validation use case tests in `domain/src/test/java/com/famessa/deli_antojito/domain/usecase/producto/GetProductoByIdUseCaseTest.kt`
- [X] T047 [P] [US3] Add DAO update and same-id name reuse tests in `data/src/androidTest/java/com/famessa/deli_antojito/data/DAO/ProductoDaoTest.kt`
- [X] T048 [P] [US3] Add repository update-product success, missing-product and duplicate-name tests in `data/src/test/java/com/famessa/deli_antojito/data/Repository/ProductoRepositoryImplTest.kt`
- [X] T049 [P] [US3] Add ProductEditViewModel edit-load, update, not-found and discard tests in `feature/product/src/test/java/com/famessa/deli_antojito/feature/product/ProductEditViewModelTest.kt`
- [X] T050 [P] [US3] Add edit-product Compose UI tests in `feature/product/src/androidTest/java/com/famessa/deli_antojito/feature/product/ProductEditScreenTest.kt`

### Implementation for User Story 3

- [X] T051 [P] [US3] Add get product by id use case in `domain/src/main/java/com/famessa/deli_antojito/domain/usecase/producto/GetProductoByIdUseCase.kt`
- [X] T052 [US3] Implement update-product path and missing-product handling in `data/src/main/java/com/famessa/deli_antojito/data/Repository/ProductoRepositoryImpl.kt`
- [X] T053 [US3] Extend ProductEditViewModel edit mode to load existing values and save updates in `feature/product/src/main/java/com/famessa/deli_antojito/feature/product/ProductEditViewModel.kt`
- [X] T054 [US3] Extend ProductEditScreen for edit title, existing image display and unchanged-name handling in `feature/product/src/main/java/com/famessa/deli_antojito/feature/product/ProductEditScreen.kt`
- [X] T055 [US3] Connect product row edit action to ProductEditRoute edit mode in `feature/product/src/main/java/com/famessa/deli_antojito/feature/product/ProductListScreen.kt`

**Checkpoint**: Edit flow works independently and preserves product identity.

---

## Phase 6: User Story 4 - Eliminar producto fisicamente (Priority: P4)

**Goal**: User can request delete, cancel safely, or confirm physical deletion and see list/totals update.

**Independent Test**: Select delete for a product, cancel once and verify it remains, then confirm delete and verify the product is removed from list and totals.

### Tests for User Story 4

- [X] T056 [P] [US4] Add delete product use case tests in `domain/src/test/java/com/famessa/deli_antojito/domain/usecase/producto/DeleteProductoUseCaseTest.kt`
- [X] T057 [P] [US4] Add DAO physical delete tests in `data/src/androidTest/java/com/famessa/deli_antojito/data/DAO/ProductoDaoTest.kt`
- [X] T058 [P] [US4] Add repository delete success and missing-product tests in `data/src/test/java/com/famessa/deli_antojito/data/Repository/ProductoRepositoryImplTest.kt`
- [X] T059 [P] [US4] Add ProductViewModel delete confirmation, cancel, success and failure tests in `feature/product/src/test/java/com/famessa/deli_antojito/feature/product/ProductViewModelTest.kt`
- [X] T060 [P] [US4] Add product delete confirmation Compose UI tests in `feature/product/src/androidTest/java/com/famessa/deli_antojito/feature/product/ProductListScreenTest.kt`

### Implementation for User Story 4

- [X] T061 [P] [US4] Add delete product use case in `domain/src/main/java/com/famessa/deli_antojito/domain/usecase/producto/DeleteProductoUseCase.kt`
- [X] T062 [US4] Implement physical delete result handling in `data/src/main/java/com/famessa/deli_antojito/data/Repository/ProductoRepositoryImpl.kt`
- [X] T063 [US4] Extend ProductViewModel with delete dialog state, cancel, confirm, refresh and error handling in `feature/product/src/main/java/com/famessa/deli_antojito/feature/product/ProductViewModel.kt`
- [X] T064 [US4] Add row action menu and delete confirmation dialog to ProductListScreen in `feature/product/src/main/java/com/famessa/deli_antojito/feature/product/ProductListScreen.kt`

**Checkpoint**: Physical delete works independently and updates persisted catalog state.

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Final validation, documentation and quality gates across all stories.

- [X] T065 [P] Add product feature KDoc review updates in `domain/src/main/java/com/famessa/deli_antojito/domain/` and `data/src/main/java/com/famessa/deli_antojito/data/`
- [X] T066 [P] Add logging guard tests or assertions for no base64 image logging in `feature/product/src/test/java/com/famessa/deli_antojito/feature/product/ProductLoggingTest.kt`
- [X] T067 Update quickstart validation results in `specs/002-product-module/quickstart.md`
- [X] T068 Run `./gradlew.bat :domain:test :data:test :feature:product:testDebugUnitTest` and fix failures in touched files
- [X] T069 Run `./gradlew.bat :data:connectedDebugAndroidTest :feature:product:connectedDebugAndroidTest :app:connectedDebugAndroidTest` and fix failures in touched files
- [X] T070 Run `./gradlew.bat :app:assembleDebug` and fix build/lint failures in touched files

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies; can start immediately
- **Foundational (Phase 2)**: Depends on Setup; blocks all user stories
- **User Story 1 (Phase 3)**: Depends on Foundational; MVP scope
- **User Story 2 (Phase 4)**: Depends on Foundational and uses US1 list route for end-to-end flow
- **User Story 3 (Phase 5)**: Depends on Foundational and reuses US2 edit form
- **User Story 4 (Phase 6)**: Depends on US1 list state and Foundational delete contract
- **Polish (Phase 7)**: Depends on selected story phases being complete

### User Story Dependencies

- **US1 Consultar productos registrados**: First deliverable after foundation; no dependency on other stories
- **US2 Registrar producto**: Can be developed after foundation, but final navigation validation depends on US1 list route
- **US3 Editar producto**: Reuses US2 edit screen and save flow
- **US4 Eliminar producto fisicamente**: Reuses US1 list screen and list ViewModel

### Within Each User Story

- Tests first, confirm they fail before implementation
- Domain use cases before repository implementation
- Repository/DAO behavior before ViewModel integration
- ViewModel state before Compose screen wiring
- Route/navigation wiring after screens and callbacks exist

## Parallel Opportunities

- T003, T004, T005 and T007 can run in parallel after T001-T002
- T008, T011, T012, T013 and T014 can run in parallel during foundational work
- US1 tests T022-T026 can run in parallel
- US2 tests T033-T037 can run in parallel
- US3 tests T046-T050 can run in parallel
- US4 tests T056-T060 can run in parallel

## Parallel Example: User Story 1

```text
Task: T022 [US1] Add validation unit tests for listing-safe defaults
Task: T023 [US1] Add DAO list and summary fixture tests
Task: T024 [US1] Add repository observe-products tests
Task: T025 [US1] Add ProductViewModel state tests
Task: T026 [US1] Add product list Compose UI tests
```

## Parallel Example: User Story 2

```text
Task: T033 [US2] Add product validation tests
Task: T034 [US2] Add DAO insert and duplicate-name lookup tests
Task: T035 [US2] Add repository create-product tests
Task: T036 [US2] Add ProductEditViewModel create tests
Task: T037 [US2] Add add-product Compose UI tests
```

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 setup.
2. Complete Phase 2 foundational domain/data evolution.
3. Complete Phase 3 US1 list and summary.
4. Validate US1 independently with product list tests and manual empty/populated states.

### Incremental Delivery

1. Deliver US1 product list and totals.
2. Add US2 product creation with device/camera image selection and validation.
3. Add US3 product edition.
4. Add US4 physical deletion.
5. Finish polish and full quickstart validation.

### Notes

- `[P]` means the task can run in parallel only when earlier dependencies are complete.
- Keep physical delete irreversible only after explicit confirmation.
- Do not implement search/filter, category, description or remote sync in this task set.
- Do not log base64 image content.


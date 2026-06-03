# Tasks: Configuracion del negocio

**Input**: Design documents from `/specs/001-business-config/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/ui-contract.md, quickstart.md

**Tests**: Required by the specification and implementation plan. Add domain/use case unit tests, DAO/repository persistence tests, ViewModel unit tests, Compose UI tests, and app startup UI tests for the behavior changed by this feature.

**Organization**: Tasks are grouped by user story so each story can be implemented and tested as an independent increment.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel because it touches different files and has no dependency on incomplete tasks
- **[Story]**: User story label from spec.md
- Every task includes an exact file or directory path

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Add the dedicated feature module and test scaffolding needed by all stories.

- [X] T001 Include the new `:feature:business-config` module in `settings.gradle.kts`
- [X] T002 Create the business configuration module Gradle file in `feature/business-config/build.gradle.kts`
- [X] T003 [P] Create the main package directory in `feature/business-config/src/main/java/com/famessa/deli_antojito/feature/businessconfig/`
- [X] T004 [P] Create the unit test package directory in `feature/business-config/src/test/java/com/famessa/deli_antojito/feature/businessconfig/`
- [X] T005 [P] Create the androidTest package directory in `feature/business-config/src/androidTest/java/com/famessa/deli_antojito/feature/businessconfig/`
- [X] T006 Add `project(":feature:business-config")` dependency to `app/build.gradle.kts`
- [X] T007 Add missing test dependencies for coroutine, Room, Hilt, and Compose coverage in `domain/build.gradle.kts`, `data/build.gradle.kts`, `feature/business-config/build.gradle.kts`, and `app/build.gradle.kts`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Define shared domain and persistence contracts that all user stories depend on.

**CRITICAL**: No user story work can begin until this phase is complete.

- [X] T008 Define explicit configuration state and error models in `domain/src/main/java/com/famessa/deli_antojito/domain/model/ConfiguracionState.kt`
- [X] T009 Update `Configuracion` with timestamps and KDoc in `domain/src/main/java/com/famessa/deli_antojito/domain/model/Configuracion.kt`
- [X] T010 Update repository contract for nullable/stateful reads, save, update, and duplicate handling in `domain/src/main/java/com/famessa/deli_antojito/domain/repository/ConfiguracionRepository.kt`
- [X] T011 [P] Create business name validation use case contract in `domain/src/main/java/com/famessa/deli_antojito/domain/usecase/configuracion/ValidateBusinessNameUseCase.kt`
- [X] T012 [P] Create startup decision use case contract in `domain/src/main/java/com/famessa/deli_antojito/domain/usecase/configuracion/GetStartupConfigurationStateUseCase.kt`
- [X] T013 [P] Create save/update use case contracts in `domain/src/main/java/com/famessa/deli_antojito/domain/usecase/configuracion/SaveConfiguracionUseCase.kt`
- [X] T014 Decide Room migration approach for singleton row and timestamps in `data/src/main/java/com/famessa/deli_antojito/data/DB/AppDatabase.kt`
- [X] T015 Update Room entity with singleton identity and timestamps in `data/src/main/java/com/famessa/deli_antojito/data/Entities/Configuracion.kt`
- [X] T016 Update mapper functions for new domain/entity fields in `data/src/main/java/com/famessa/deli_antojito/data/mappers/Mappers.kt`
- [X] T017 Update Hilt repository binding compatibility in `data/src/main/java/com/famessa/deli_antojito/data/di/RepositoryModule.kt`

**Checkpoint**: Foundation ready - user story implementation can now begin.

---

## Phase 3: User Story 1 - Registrar configuracion inicial del negocio (Priority: P1) MVP

**Goal**: On first launch without saved configuration, block normal app use, collect a valid business name, persist exactly one configuration, and continue to the main flow.

**Independent Test**: Clear app data, launch the app, verify the registration screen appears before `HomeView`, reject invalid names, save a valid name, and verify the app proceeds to the main flow.

### Tests for User Story 1

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation.**

- [X] T018 [P] [US1] Add validation tests for blank, whitespace, over-100-character, trimmed, and valid names in `domain/src/test/java/com/famessa/deli_antojito/domain/usecase/configuracion/ValidateBusinessNameUseCaseTest.kt`
- [X] T019 [P] [US1] Add startup state tests for missing, configured, duplicate, and read failure cases in `domain/src/test/java/com/famessa/deli_antojito/domain/usecase/configuracion/GetStartupConfigurationStateUseCaseTest.kt`
- [X] T020 [P] [US1] Add DAO tests for zero rows, one row, duplicate rows, and upsert behavior in `data/src/androidTest/java/com/famessa/deli_antojito/data/DAO/ConfiguracionDaoTest.kt`
- [X] T021 [P] [US1] Add repository tests for missing state, save success, duplicate invalid state, and save failure mapping in `data/src/test/java/com/famessa/deli_antojito/data/Repository/ConfiguracionRepositoryImplTest.kt`
- [X] T022 [P] [US1] Add ViewModel tests for initial input, validation errors, successful save, save failure preserving input, and retry state in `feature/business-config/src/test/java/com/famessa/deli_antojito/feature/businessconfig/BusinessConfigViewModelTest.kt`
- [X] T023 [P] [US1] Add Compose UI tests for first-run registration form, invalid-name feedback, no-bypass behavior, and save action in `feature/business-config/src/androidTest/java/com/famessa/deli_antojito/feature/businessconfig/BusinessConfigScreenTest.kt`
- [X] T024 [P] [US1] Add app startup UI test for no-configuration launch gating in `app/src/androidTest/java/com/famessa/deli_antojito/MainActivityStartupTest.kt`

### Implementation for User Story 1

- [X] T025 [US1] Implement `ValidateBusinessNameUseCase` in `domain/src/main/java/com/famessa/deli_antojito/domain/usecase/configuracion/ValidateBusinessNameUseCase.kt`
- [X] T026 [US1] Implement `GetStartupConfigurationStateUseCase` in `domain/src/main/java/com/famessa/deli_antojito/domain/usecase/configuracion/GetStartupConfigurationStateUseCase.kt`
- [X] T027 [US1] Update `ConfiguracionDao` with nullable read, row count, duplicate read, singleton upsert, and update queries in `data/src/main/java/com/famessa/deli_antojito/data/DAO/ConfiguracionDao.kt`
- [X] T028 [US1] Implement repository state mapping, singleton save, duplicate detection, and non-PII Timber failure logging in `data/src/main/java/com/famessa/deli_antojito/data/Repository/ConfiguracionRepositoryImpl.kt`
- [X] T029 [US1] Create `BusinessConfigUiState` for loading, editing, validation, saving, saved, save error, read error, and invalid local data states in `feature/business-config/src/main/java/com/famessa/deli_antojito/feature/businessconfig/BusinessConfigUiState.kt`
- [X] T030 [US1] Implement `BusinessConfigViewModel` save and retry behavior in `feature/business-config/src/main/java/com/famessa/deli_antojito/feature/businessconfig/BusinessConfigViewModel.kt`
- [X] T031 [US1] Implement registration mode UI in `feature/business-config/src/main/java/com/famessa/deli_antojito/feature/businessconfig/BusinessConfigScreen.kt`
- [X] T032 [US1] Implement route callbacks for save success and blocking error retry in `feature/business-config/src/main/java/com/famessa/deli_antojito/feature/businessconfig/BusinessConfigRoute.kt`
- [X] T033 [US1] Gate startup between loading, business registration, blocking error, and `HomeView` in `app/src/main/java/com/famessa/deli_antojito/MainActivity.kt`

**Checkpoint**: User Story 1 is fully functional and testable as the MVP.

---

## Phase 4: User Story 2 - Usar configuracion existente al iniciar (Priority: P2)

**Goal**: Relaunch with a saved configuration skips first-run registration and makes the saved business name available to the normal app flow.

**Independent Test**: Seed or save one configuration, relaunch the app, verify registration is skipped, and verify the saved business name can be read by app/home behavior.

### Tests for User Story 2

- [X] T034 [P] [US2] Add repository observe tests for existing configuration and persisted business name in `data/src/test/java/com/famessa/deli_antojito/data/Repository/ConfiguracionRepositoryImplExistingTest.kt`
- [X] T035 [P] [US2] Add startup use case tests for existing configuration startup decisions in `domain/src/test/java/com/famessa/deli_antojito/domain/usecase/configuracion/GetStartupConfigurationStateUseCaseExistingTest.kt`
- [X] T036 [P] [US2] Add app startup UI test for existing-configuration launch skipping registration in `app/src/androidTest/java/com/famessa/deli_antojito/MainActivityExistingConfigTest.kt`
- [X] T037 [P] [US2] Add home integration test for displaying or consuming the saved business name in `feature/home/src/androidTest/java/com/famessa/deli_antojito/feature/home/HomeBusinessConfigTest.kt`

### Implementation for User Story 2

- [X] T038 [US2] Add a domain use case for observing the saved configuration in `domain/src/main/java/com/famessa/deli_antojito/domain/usecase/configuracion/ObserveConfiguracionUseCase.kt`
- [X] T039 [US2] Expose configured startup state to the app without showing registration in `app/src/main/java/com/famessa/deli_antojito/MainActivity.kt`
- [X] T040 [US2] Wire saved business name consumption into the normal app flow in `feature/home/src/main/java/com/famessa/deli_antojito/feature/home/HomeViewModel.kt`
- [X] T041 [US2] Render the saved business name where the normal flow needs it in `feature/home/src/main/java/com/famessa/deli_antojito/feature/home/HomeView.kt`

**Checkpoint**: User Stories 1 and 2 both work independently.

---

## Phase 5: User Story 3 - Consultar y actualizar datos del negocio (Priority: P3)

**Goal**: Provide a configuration module where an existing business name is prefilled, can be changed with validation, and persists across relaunch.

**Independent Test**: Start with a saved configuration, open the configuration module, verify the current name is shown, update it, relaunch, and verify the updated name is retained.

### Tests for User Story 3

- [X] T042 [P] [US3] Add update use case tests for valid update, invalid update, and update failure in `domain/src/test/java/com/famessa/deli_antojito/domain/usecase/configuracion/SaveConfiguracionUseCaseUpdateTest.kt`
- [X] T043 [P] [US3] Add repository update tests for preserving `createdAt`, changing `updatedAt`, and preventing duplicates in `data/src/test/java/com/famessa/deli_antojito/data/Repository/ConfiguracionRepositoryImplUpdateTest.kt`
- [X] T044 [P] [US3] Add ViewModel edit-mode tests for prefilled name, validation feedback, successful update, and save failure preserving input in `feature/business-config/src/test/java/com/famessa/deli_antojito/feature/businessconfig/BusinessConfigEditViewModelTest.kt`
- [X] T045 [P] [US3] Add Compose UI edit-flow tests for prefilled name, invalid update, successful update, and persistence feedback in `feature/business-config/src/androidTest/java/com/famessa/deli_antojito/feature/businessconfig/BusinessConfigEditScreenTest.kt`

### Implementation for User Story 3

- [X] T046 [US3] Extend `SaveConfiguracionUseCase` to support updating an existing configuration in `domain/src/main/java/com/famessa/deli_antojito/domain/usecase/configuracion/SaveConfiguracionUseCase.kt`
- [X] T047 [US3] Implement repository update behavior that preserves singleton identity and timestamps in `data/src/main/java/com/famessa/deli_antojito/data/Repository/ConfiguracionRepositoryImpl.kt`
- [X] T048 [US3] Extend `BusinessConfigViewModel` with edit-mode loading, update, validation, and error preservation in `feature/business-config/src/main/java/com/famessa/deli_antojito/feature/businessconfig/BusinessConfigViewModel.kt`
- [X] T049 [US3] Extend `BusinessConfigScreen` with edit mode, current name prefill, and update feedback in `feature/business-config/src/main/java/com/famessa/deli_antojito/feature/businessconfig/BusinessConfigScreen.kt`
- [X] T050 [US3] Add navigation or entry point from the normal app flow to the configuration module in `app/src/main/java/com/famessa/deli_antojito/MainActivity.kt`

**Checkpoint**: All user stories are independently functional.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final validation, maintainability, and quality checks across all stories.

- [X] T051 [P] Add or update KDoc for public configuration models, repository methods, and use cases in `domain/src/main/java/com/famessa/deli_antojito/domain/`
- [X] T052 [P] Review non-PII logging and remove business-name logging from `data/src/main/java/com/famessa/deli_antojito/data/Repository/ConfiguracionRepositoryImpl.kt` and `feature/business-config/src/main/java/com/famessa/deli_antojito/feature/businessconfig/`
- [X] T053 [P] Add accessibility semantics and stable test tags for configuration UI in `feature/business-config/src/main/java/com/famessa/deli_antojito/feature/businessconfig/BusinessConfigScreen.kt`
- [X] T054 Run focused unit tests with `.\gradlew.bat :domain:testDebugUnitTest :data:testDebugUnitTest :feature:business-config:testDebugUnitTest`
- [X] T055 Run instrumented persistence and UI tests with `.\gradlew.bat :data:connectedDebugAndroidTest :feature:business-config:connectedDebugAndroidTest :app:connectedDebugAndroidTest`
- [X] T056 Run debug build with `.\gradlew.bat :app:assembleDebug`
- [ ] T057 Execute manual validation steps from `specs/001-business-config/quickstart.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion and blocks all user stories
- **User Story 1 (Phase 3)**: Depends on Foundational and is the MVP
- **User Story 2 (Phase 4)**: Depends on Foundational; practically validates best after US1 save flow exists
- **User Story 3 (Phase 5)**: Depends on Foundational; uses configuration module created in US1
- **Polish (Phase 6)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Start after Phase 2; no dependency on other stories
- **User Story 2 (P2)**: Start after Phase 2; can use seeded data for independent testing, and integrates naturally after US1
- **User Story 3 (P3)**: Start after Phase 2; can use seeded data for independent testing, and reuses the US1 screen/ViewModel structure

### Within Each User Story

- Tests must be written before implementation for behavior changes
- Domain use cases and repository contracts come before data implementation
- DAO/entity/mappers come before repository implementation
- ViewModel and UI state come before Compose screen integration
- App startup/navigation wiring comes after feature route behavior exists

---

## Parallel Opportunities

- Setup directories T003-T005 can run in parallel
- Foundational domain use cases T011-T013 can run in parallel after T008-T010 are agreed
- US1 tests T018-T024 can run in parallel because they target different modules/files
- US2 tests T034-T037 can run in parallel because they target different modules/files
- US3 tests T042-T045 can run in parallel because they target different modules/files
- Polish tasks T051-T053 can run in parallel after implementation is complete

---

## Parallel Example: User Story 1

```text
Task: "T018 [P] [US1] Add validation tests in domain/src/test/java/com/famessa/deli_antojito/domain/usecase/configuracion/ValidateBusinessNameUseCaseTest.kt"
Task: "T020 [P] [US1] Add DAO tests in data/src/androidTest/java/com/famessa/deli_antojito/data/DAO/ConfiguracionDaoTest.kt"
Task: "T023 [P] [US1] Add Compose UI tests in feature/business-config/src/androidTest/java/com/famessa/deli_antojito/feature/businessconfig/BusinessConfigScreenTest.kt"
Task: "T024 [P] [US1] Add app startup UI test in app/src/androidTest/java/com/famessa/deli_antojito/MainActivityStartupTest.kt"
```

## Parallel Example: User Story 2

```text
Task: "T034 [P] [US2] Add repository observe tests in data/src/test/java/com/famessa/deli_antojito/data/Repository/ConfiguracionRepositoryImplExistingTest.kt"
Task: "T035 [P] [US2] Add startup use case tests in domain/src/test/java/com/famessa/deli_antojito/domain/usecase/configuracion/GetStartupConfigurationStateUseCaseExistingTest.kt"
Task: "T036 [P] [US2] Add app startup UI test in app/src/androidTest/java/com/famessa/deli_antojito/MainActivityExistingConfigTest.kt"
```

## Parallel Example: User Story 3

```text
Task: "T042 [P] [US3] Add update use case tests in domain/src/test/java/com/famessa/deli_antojito/domain/usecase/configuracion/SaveConfiguracionUseCaseUpdateTest.kt"
Task: "T043 [P] [US3] Add repository update tests in data/src/test/java/com/famessa/deli_antojito/data/Repository/ConfiguracionRepositoryImplUpdateTest.kt"
Task: "T045 [P] [US3] Add Compose UI edit-flow tests in feature/business-config/src/androidTest/java/com/famessa/deli_antojito/feature/businessconfig/BusinessConfigEditScreenTest.kt"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 setup.
2. Complete Phase 2 foundational domain and persistence contracts.
3. Complete Phase 3 User Story 1.
4. Validate first-launch gating, invalid input rejection, save success, save failure, duplicate-row handling, and transition to `HomeView`.

### Incremental Delivery

1. Add User Story 1 for mandatory first-run registration.
2. Add User Story 2 for subsequent startup reuse and saved-name availability.
3. Add User Story 3 for edit/view behavior after initial setup.
4. Finish polish, KDoc, logging review, quickstart validation, and build/test runs.

### Parallel Team Strategy

1. Complete Setup and Foundational phases together.
2. Split tests by module while implementation contracts settle.
3. Assign US1, US2, and US3 to different developers only after shared domain/data contracts are stable.
4. Integrate through `app/src/main/java/com/famessa/deli_antojito/MainActivity.kt` after feature routes and use cases compile.

---

## Notes

- [P] tasks are parallelizable by file ownership and dependency order.
- [US1], [US2], and [US3] labels map directly to the prioritized user stories in `spec.md`.
- The existing `Configuracion` domain/data files should be evolved instead of creating a duplicate persistence model.
- Business names are business-identifying information; do not log the value in production code or fixtures.
- Stop at each checkpoint to validate the story independently before continuing.

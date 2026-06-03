# Implementation Plan: Configuración del negocio

**Branch**: `001-business-config` | **Date**: 2026-06-03 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/001-business-config/spec.md`

## Summary

Add a business configuration module that persists exactly one local business configuration and blocks normal app use on first launch until a valid business name is saved. The implementation will evolve the existing `Configuracion` domain/data model, repository, DAO, Room table, and app startup flow, then add a dedicated Compose feature module for first-run registration and later editing.

## Technical Context

**Language/Version**: Kotlin 2.0.21, Android Gradle Plugin 8.9.1, Java/Kotlin JVM target 11

**Primary Dependencies**: Jetpack Compose, Material 3, Hilt, Kotlin Coroutines/Flow, Room 2.6.1, Timber. Retrofit/OkHttp and Coil remain available but are not needed for this feature.

**Storage**: Existing Room database `deli_antojito_db`, existing `configuracion` table evolved to enforce one active row, nullable read when missing, and update/upsert behavior for the saved business name.

**Testing**: Domain/use case unit tests, repository/DAO persistence tests, ViewModel unit tests, and Compose UI tests for first-run registration, validation, save failure, existing-configuration startup, and edit flow.

**Target Platform**: Android mobile app using the current project Gradle configuration (`minSdk = 25`, `targetSdk = 35`). The feature introduces no API requirement above the current minimum and remains offline-capable.

**Project Type**: Android mobile app

**Performance Goals**: Startup configuration check completes quickly enough that the user sees either the registration screen or normal flow without perceptible delay; save/update actions complete within the spec target of under 1 minute for the full user task.

**Constraints**: No normal application use without a valid saved business name. Configuration data stays local to the device. Business name length is limited to 100 characters. UI state must preserve user input after save failures.

**Scale/Scope**: One active business configuration per device. Initial data scope is business name plus creation and last-updated timestamps. No multi-device sync, authentication, roles, or remote APIs in this feature.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Architecture Boundaries**: PASS. Touched modules are `domain`, `data`, new `feature:business-config`, `app`, and likely `settings.gradle.kts`. Dependency direction remains `domain <- data/feature <- app`; `domain` defines business rules and repository contracts, `data` implements Room persistence, `feature` owns Compose UI and ViewModels, and `app` wires startup navigation.
- **Security & Kotlin Quality**: PASS. Business name is business-identifying information and must not be logged unnecessarily. All missing-data paths use nullable or sealed state instead of `!!`. Room I/O runs off the main thread through suspend/Flow APIs. ViewModels expose immutable UI state and callbacks. Hilt constructor injection is used at boundaries.
- **Testing Strategy**: PASS. Required coverage includes validation use case tests, startup decision tests, DAO/repository tests for missing/create/update/corrupt duplicate behavior, ViewModel tests for all UI states, and Compose UI tests for first-run gating and edit flow. Touched domain and ViewModel code targets at least 70% coverage.
- **Maintainability & Observability**: PASS. New public domain models, use cases, and repository methods need KDoc. Error states must be explicit in domain/UI state. Timber logging may record persistence failures with non-PII context only.
- **Dependencies & Change Control**: PASS. No new third-party dependency is required; Room, Hilt, Compose, Coroutines, and Timber already exist in the version catalog. Adding `:feature:business-config` is a module graph change with low migration risk.

## Project Structure

### Documentation (this feature)

```text
specs/001-business-config/
|-- plan.md
|-- research.md
|-- data-model.md
|-- quickstart.md
|-- contracts/
|   `-- ui-contract.md
|-- checklists/
|   `-- requirements.md
`-- tasks.md              # Phase 2 output from /speckit-tasks, not created here
```

### Source Code (repository root)

```text
settings.gradle.kts                         # include :feature:business-config

app/
`-- src/main/java/com/famessa/deli_antojito/
    |-- MainActivity.kt                     # app startup gating/navigation
    `-- DeliAntojitoApp.kt                  # existing Hilt application

domain/
`-- src/main/java/com/famessa/deli_antojito/domain/
    |-- model/Configuracion.kt              # business configuration domain model
    |-- repository/ConfiguracionRepository.kt
    `-- usecase/configuracion/              # validation, get, save/update, startup decision

data/
`-- src/main/java/com/famessa/deli_antojito/data/
    |-- DAO/ConfiguracionDao.kt             # nullable read, upsert/update, duplicate detection
    |-- DB/AppDatabase.kt                   # Room schema/migration if needed
    |-- Entities/Configuracion.kt           # local entity with one-row identity
    |-- Repository/ConfiguracionRepositoryImpl.kt
    |-- di/RepositoryModule.kt
    `-- mappers/Mappers.kt

feature/
|-- home/
`-- business-config/
    |-- build.gradle.kts
    `-- src/main/java/com/famessa/deli_antojito/feature/businessconfig/
        |-- BusinessConfigRoute.kt
        |-- BusinessConfigScreen.kt
        |-- BusinessConfigViewModel.kt
        `-- BusinessConfigUiState.kt

domain/src/test/
data/src/test/ or data/src/androidTest/
feature/business-config/src/test/
feature/business-config/src/androidTest/
app/src/androidTest/
```

**Structure Decision**: Create a focused `:feature:business-config` module instead of placing the screen in `:feature:home`, because the configuration flow is independently testable, participates in startup gating, and remains reusable for both first-run registration and later editing. Reuse and evolve the existing `Configuracion` domain/data classes instead of creating a second concept.

## Phase 0: Research Summary

Research completed in [research.md](./research.md). Decisions resolve all planning unknowns: use existing Room stack, enforce one local configuration row, expose nullable/explicit missing state from the repository, add domain use cases for validation and startup decision, create a dedicated feature module, and keep startup gating in `app`.

## Phase 1: Design Summary

Design artifacts completed:

- [data-model.md](./data-model.md): BusinessConfiguration model, validation rules, repository behavior, and state transitions.
- [contracts/ui-contract.md](./contracts/ui-contract.md): UI state, actions, navigation/startup contract, and acceptance coverage.
- [quickstart.md](./quickstart.md): Build, test, and manual validation workflow for this feature.

## Constitution Check - Post Design

- **Architecture Boundaries**: PASS. Design keeps Room entities in `data`, business rules in `domain`, UI state in `feature:business-config`, and startup composition in `app`.
- **Security & Kotlin Quality**: PASS. The design avoids force unwraps, hidden globals, and PII logging; all I/O remains coroutine/Flow based.
- **Testing Strategy**: PASS. Design artifacts define domain, DAO/repository, ViewModel, Compose UI, and app startup tests.
- **Maintainability & Observability**: PASS. KDoc and explicit error states are required for new public APIs and states.
- **Dependencies & Change Control**: PASS. No new external dependency is introduced; the only dependency graph change is a new feature module.

## Complexity Tracking

No constitution violations or complexity exceptions.

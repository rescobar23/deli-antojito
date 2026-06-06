# Implementation Plan: Product Module

**Branch**: `002-product-module` | **Date**: 2026-06-06 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/002-product-module/spec.md`

## Summary

Add a product catalog module that lists local products, shows product totals, supports product creation and edition, and physically deletes products after confirmation. The implementation will evolve the existing `Producto` domain/data model, repository, DAO, Room table and mappers, then add a dedicated Compose feature module for the list and add/edit flows. Product data stays local and offline-capable through the existing Room-backed app database.

## Technical Context

**Language/Version**: Kotlin 2.0.21, Android Gradle Plugin 8.9.1, Java/Kotlin JVM target 11

**Primary Dependencies**: Jetpack Compose, Material 3, Navigation Compose through app wiring, Hilt, Kotlin Coroutines/Flow, Room 2.6.1, Coil 2.7.0, Timber

**Storage**: Existing Room database `deli_antojito_db`, existing `productos` table evolved from current fields (`id`, `nombre`, `precioBase`, `categoria`) to the requested product shape (`id`, `nombre`, `precio_base`, `img`, `activo`). A schema migration is required because current persisted product rows do not match the new spec.

**Testing**: Domain/use case unit tests, DAO/repository persistence tests, ViewModel unit tests, Compose UI tests for list/add/edit/delete/validation states, and app navigation tests for route wiring where practical.

**Target Platform**: Android mobile app using the current project Gradle configuration (`minSdk = 25`, `targetSdk = 35`). The module is offline-capable and stores all product data locally.

**Project Type**: Android mobile app

**Performance Goals**: Product list opens and shows catalog status in under 5 seconds; valid product registration completes in under 2 minutes; edit/delete flow completes in under 1 minute after locating a product.

**Constraints**: Local-only product data; physical deletion is irreversible after confirmation; product names are unique after trimming and case-folding; product name length is 120 characters max; base price must be greater than zero; product image is optional and limited to JPG/PNG up to 2 MB.

**Scale/Scope**: One local product catalog per device. Scope includes list, totals, add, edit, active/inactive status, optional image, validation, and physical delete. Search/filter controls shown in the mockup remain out of scope for this feature.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Architecture Boundaries**: PASS. Touched modules are `domain`, `data`, new `feature:product`, `core:ui` only if shared UI primitives are reused, `app`, and `settings.gradle.kts`. Dependency direction remains `domain <- data/feature <- app`; domain owns product rules and repository contracts, data owns Room persistence, feature owns Compose UI and ViewModels, and app wires navigation/DI.
- **Security & Kotlin Quality**: PASS. Product names, prices and images are local business data and must not be logged with full image payloads. Room I/O runs through suspend/Flow APIs off the main thread. ViewModels expose immutable state and callbacks. Hilt constructor injection is used at boundaries. No force unwraps are needed.
- **Testing Strategy**: PASS. Required coverage includes validation use case tests, repository/DAO tests for uniqueness, migration, CRUD and physical delete, ViewModel tests for list/add/edit/delete states, Compose UI tests for form validation and delete confirmation, and app navigation coverage for opening the module.
- **Maintainability & Observability**: PASS. New public domain models, repository methods, use cases and UI state contracts need KDoc. Error states must be explicit. Timber may log persistence failures with non-sensitive context only and must never log base64 image content.
- **Dependencies & Change Control**: PASS. No new third-party dependency is required. Existing Room, Hilt, Compose, Coroutines, Coil and Timber cover the feature. Adding `:feature:product` is a module graph change with low migration risk.

## Project Structure

### Documentation (this feature)

```text
specs/002-product-module/
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
settings.gradle.kts                         # include :feature:product

app/
`-- src/main/java/com/famessa/deli_antojito/
    |-- MainActivity.kt                     # product route entry/navigation wiring
    `-- di/
        `-- ProductoUseCaseModule.kt        # use case bindings if needed

domain/
`-- src/main/java/com/famessa/deli_antojito/domain/
    |-- model/Producto.kt                   # requested product model
    |-- repository/ProductoRepository.kt    # product catalog contract
    `-- usecase/producto/                   # observe, get, save, delete, validate

data/
`-- src/main/java/com/famessa/deli_antojito/data/
    |-- DAO/ProductoDao.kt                  # list, find, insert/update/delete, uniqueness reads
    |-- DB/AppDatabase.kt                   # Room schema version and migration
    |-- Entities/Producto.kt                # local entity for productos table
    |-- Repository/ProductoRepositoryImpl.kt
    `-- mappers/Mappers.kt

feature/
|-- home/                                   # entry point can link to products
|-- business-config/
`-- product/
    |-- build.gradle.kts
    `-- src/main/java/com/famessa/deli_antojito/feature/product/
        |-- ProductListRoute.kt
        |-- ProductListScreen.kt
        |-- ProductEditRoute.kt
        |-- ProductEditScreen.kt
        |-- ProductViewModel.kt
        |-- ProductEditViewModel.kt
        `-- ProductUiState.kt

domain/src/test/
data/src/test/ or data/src/androidTest/
feature/product/src/test/
feature/product/src/androidTest/
app/src/androidTest/
```

**Structure Decision**: Create a dedicated `:feature:product` module rather than placing this in `:feature:home`, because the product module has independent navigation, list state, form state, validation, image handling and delete confirmation. Reuse and evolve the existing `Producto` domain/data files instead of adding a parallel concept.

## Phase 0: Research Summary

Research completed in [research.md](./research.md). Decisions resolve planning unknowns: evolve the existing product model, use Room migration for schema changes, enforce name uniqueness in domain validation plus persistence guard, keep images as optional base64 text with JPG/PNG and 2 MB validation, use a dedicated feature module, and treat new products as active by default in implementation planning based on the mockup unless the product owner revises it before task generation.

## Phase 1: Design Summary

Design artifacts completed:

- [data-model.md](./data-model.md): Product entity, validation rules, repository behavior, migration notes and state transitions.
- [contracts/ui-contract.md](./contracts/ui-contract.md): UI routes, states, actions and acceptance coverage for list, add/edit and delete flows.
- [quickstart.md](./quickstart.md): Build, test and manual validation workflow for this feature.

## Constitution Check - Post Design

- **Architecture Boundaries**: PASS. Design keeps product validation in `domain`, Room implementation in `data`, UI state and Compose screens in `feature:product`, and navigation composition in `app`.
- **Security & Kotlin Quality**: PASS. The design avoids force unwraps, main-thread persistence work and base64 image logging; form and list states are explicit immutable models.
- **Testing Strategy**: PASS. Design artifacts define domain, DAO/repository, ViewModel, Compose UI and navigation tests, including physical delete and migration coverage.
- **Maintainability & Observability**: PASS. Public APIs requiring KDoc are identified, and error states/logging constraints are explicit.
- **Dependencies & Change Control**: PASS. No new external dependency is introduced; only a feature module and Room schema migration are planned.

## Complexity Tracking

No constitution violations or complexity exceptions.

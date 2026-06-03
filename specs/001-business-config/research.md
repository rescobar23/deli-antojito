# Research: Configuración del negocio

## Decision: Reuse Room for local business configuration

**Rationale**: The feature explicitly requires local SQLite storage, and the project already uses Room with version catalog entries, KSP, DAOs, entities, and an `AppDatabase`. Reusing Room keeps persistence inside the approved stack and avoids adding dependencies.

**Alternatives considered**: DataStore was rejected because the requirement names SQLite/local database storage and the project already has a Room database. Raw SQLite was rejected because Room is already approved and testable.

## Decision: Evolve the existing `Configuracion` model instead of creating a duplicate model

**Rationale**: `domain.model.Configuracion`, `ConfiguracionRepository`, `ConfiguracionDao`, and a Room entity already exist. The feature should close behavior gaps in those artifacts: nullable missing state, single-row enforcement, validation, and update/upsert.

**Alternatives considered**: Creating `BusinessConfiguration` as a separate model was rejected because it would duplicate an existing domain concept. Renaming everything immediately was rejected as unnecessary churn for planning; implementation may add aliases or naming cleanup only if tasks justify it.

## Decision: Repository exposes explicit missing and failure states

**Rationale**: The app must decide at startup whether configuration exists. The current repository returns `Flow<Configuracion>`, which cannot safely represent absence. The domain boundary should expose nullable data or a sealed result/state so startup can distinguish loading, missing, configured, and error.

**Alternatives considered**: Returning a default empty configuration was rejected because it would hide missing setup and weaken the mandatory first-run flow. Letting Room throw on no rows was rejected because it produces fragile UI behavior.

## Decision: Enforce exactly one active configuration row

**Rationale**: The spec requires one active configuration per device. Room persistence should use a stable singleton identity, upsert/update semantics, and duplicate detection/cleanup handling for corrupt data or prior migrations.

**Alternatives considered**: Auto-generating IDs indefinitely was rejected because it permits duplicates. Deleting and reinserting on every save was rejected because it loses lifecycle metadata and creates unnecessary churn.

## Decision: Add a dedicated `:feature:business-config` module

**Rationale**: The configuration screen is a distinct user flow used both at first launch and later editing. A dedicated module keeps UI state and ViewModel tests focused, while `app` composes it into startup navigation.

**Alternatives considered**: Adding the screen to `:feature:home` was rejected because first-run gating is not home behavior. Putting UI in `app` was rejected because it would violate module boundaries.

## Decision: App startup gating is composed in `app`, not in the feature module

**Rationale**: `app` owns composition, entry points, and navigation wiring. The business config feature can expose routes/screens and state callbacks, while the app decides whether to show setup or normal flow based on the domain startup decision.

**Alternatives considered**: Making the home screen check configuration was rejected because it would allow other entry points to bypass setup and mix startup orchestration with home UI.

## Decision: No new external dependency is needed

**Rationale**: Existing Compose, Hilt, Coroutines/Flow, Room, and Timber cover UI, dependency injection, async state, persistence, and logging.

**Alternatives considered**: Adding a navigation library beyond current app wiring was rejected for this plan because the current app has a simple single-activity Compose setup and tasks can add only the minimal navigation needed.

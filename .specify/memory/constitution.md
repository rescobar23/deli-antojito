<!--
SYNC IMPACT REPORT
==================
Version Change: 1.0.0 -> 1.1.0
Modified Principles:
  - I. Clean Architecture & Separacion de Responsabilidades -> I. Arquitectura Limpia y Limites de Modulo
  - II. Seguridad & Calidad del Codigo en Kotlin -> II. Seguridad y Calidad del Codigo Kotlin
  - III. Testing Obligatorio & Testabilidad -> III. Testing Obligatorio y Testabilidad
  - IV. Mantenibilidad & Documentacion -> IV. Mantenibilidad, Documentacion y Observabilidad
  - V. Control de Dependencias & Modularizacion -> V. Control de Dependencias y Cambios

Added Sections:
  - Quality Gates

Removed Sections:
  - None

Templates Requiring Updates:
  - Updated: .specify/templates/plan-template.md
  - Updated: .specify/templates/spec-template.md
  - Updated: .specify/templates/tasks-template.md
  - Reviewed: .specify/templates/checklist-template.md
  - Reviewed: .specify/extensions/git/commands/*.md
  - Reviewed: AGENTS.md

Follow-up TODOs:
  - None
-->

# Deli-Antojito Project Constitution

## Core Principles

### I. Arquitectura Limpia y Limites de Modulo

El proyecto MUST conservar Clean Architecture con modulos separados por
responsabilidad: `domain`, `data`, `feature`, `core` y `app`.

- `domain` MUST contener reglas de negocio puras, use cases, modelos de dominio
  e interfaces; MUST NOT depender de Android, Room, Retrofit, Hilt, Compose ni
  cualquier modulo de implementacion.
- `data` MUST implementar interfaces de `domain`; MAY depender de `domain` y
  `core`; MUST NOT exponer entidades Room, DTOs remotos ni detalles de cache a
  `feature`.
- `feature` MUST contener UI Compose, state holders y ViewModels; MAY depender
  de `domain` y `core`; MUST NOT implementar reglas de negocio ni acceder
  directamente a Room, Retrofit u otros datasources.
- `core` MUST contener capacidades transversales sin reglas de negocio:
  design system, utilidades, logging, networking base y configuracion comun.
- `app` MUST ser el punto de composicion para navegacion, DI y wiring global.
  Su rol es orquestar, no alojar logica de negocio.

Toda feature nueva MUST declarar en su plan que modulos toca y como respeta el
flujo de dependencias. Una dependencia circular o una referencia entre capas no
permitida bloquea merge.

Rationale: limites claros reducen acoplamiento, hacen testeable la logica de
negocio y permiten evolucionar UI, persistencia o red sin reescribir casos de uso.

### II. Seguridad y Calidad del Codigo Kotlin

Todo codigo Kotlin de produccion MUST ser seguro por defecto y legible en code
review.

- Null safety: `!!` is PROHIBITED except when a local comment explains the
  invariant and the code has a test or guard that proves it. Prefer explicit
  nullable handling, sealed results and early returns.
- Concurrency: I/O, base de datos, red y trabajo costoso MUST run fuera del main
  thread usando coroutines y Flow cuando aplique. Blocking calls in UI code are
  PROHIBITED.
- State management: Compose screens MUST receive immutable state and callbacks.
  Side effects MUST be isolated in ViewModels, effects scoped to lifecycle, or
  dedicated infrastructure.
- Dependency injection: clases con dependencias externas MUST use constructor
  injection or Hilt providers. Service locator, global mutable singletons and
  hidden dependencies are PROHIBITED.
- Secrets: API keys, tokens and credentials MUST NOT be committed. Use
  `local.properties`, environment-specific config, encrypted storage or secure
  CI secrets.
- Static checks: Kotlin compiler warnings, lint warnings and formatting failures
  MUST be resolved or explicitly documented in the plan with owner and expiry.

Rationale: Kotlin and Android provide compile-time and lifecycle tools that avoid
crashes, leaked state and credential exposure when used consistently.

### III. Testing Obligatorio y Testabilidad

Testing is NON-NEGOTIABLE for every behavior change.

- Domain use cases, pure business rules and ViewModels MUST have unit tests.
- Repositories, mappers, Room DAOs and datasource coordination MUST have
  integration tests or contract tests when their behavior changes.
- Composables with conditional rendering, user interaction, validation or loading
  states MUST have Compose UI tests or focused screenshot/state tests.
- Bug fixes MUST include a regression test that fails without the fix unless the
  plan documents why automated coverage is technically infeasible.
- New code MUST be designed for testability through interfaces at boundaries,
  deterministic inputs and injectable clocks, dispatchers or external services.
- Generated tasks MUST include tests first for each independently testable user
  story. The only exception is documentation-only work, and that exception MUST
  be stated in `tasks.md`.

Target coverage for touched `domain` and ViewModel code is at least 70%. Lower
coverage requires an explicit plan entry with the uncovered risk and mitigation.

Rationale: executable tests are the cheapest way to protect business behavior as
the app adds screens, data sources and offline flows.

### IV. Mantenibilidad, Documentacion y Observabilidad

Code MUST be understandable to a new maintainer without private context.

- Public APIs in `domain`, `data` and shared `core` modules MUST include KDoc
  describing purpose, parameters, returns and relevant failure modes.
- Names MUST be descriptive and domain-oriented. Ambiguous abbreviations and
  one-letter names outside local lambdas are PROHIBITED.
- Comments MUST explain non-obvious rationale, business rules or tradeoffs.
  Comments that restate the code are discouraged.
- Feature modules SHOULD include a short README when they introduce non-trivial
  navigation, state machines, external contracts or multi-screen flows.
- Logging MUST use the approved logging facility, include actionable context and
  avoid PII, secrets and high-volume loops.
- Error states MUST be explicit in UI state and domain results; swallowing
  exceptions silently is PROHIBITED.

Rationale: maintainability depends on clear names, documented contracts and
observable failures, not on tribal knowledge.

### V. Control de Dependencias y Cambios

Dependencies and architectural changes MUST be intentional, reviewable and
versioned.

- Dependency versions MUST live in `gradle/libs.versions.toml` or an approved
  Gradle convention plugin. Hardcoded module-level versions are PROHIBITED.
- New libraries MUST include rationale in the plan: purpose, owner, license
  acceptability, Android compatibility and why existing stack is insufficient.
- Major dependency upgrades and stack replacements MUST include migration notes,
  risk assessment and rollback strategy.
- Module public APIs MUST expose domain models or stable abstractions, not
  framework implementation details.
- Cyclic module dependencies are PROHIBITED.

Rationale: unmanaged dependencies create security, build and migration risk.
Explicit ownership keeps the app maintainable as the module graph grows.

## Requisitos Tecnicos y Stack

The approved project context is an Android application using Kotlin and a
modular Clean Architecture.

| Aspect | Standard |
|--------|----------|
| Language | Kotlin 1.9+ |
| Platform | Android 8.0+ (API 26+) unless product requirements say otherwise |
| UI | Jetpack Compose, Material 3, Navigation Compose |
| Architecture | Clean Architecture, feature modules, UDF/MVI-style state flow |
| Dependency Injection | Hilt |
| Async | Kotlin Coroutines and Flow |
| Networking | Retrofit and OkHttp |
| Serialization | Kotlinx Serialization or Moshi, chosen per feature plan |
| Persistence | Room |
| Image Loading | Coil |
| Logging | Timber or the approved project logging wrapper |

Stack changes MUST be proposed in the feature plan and reviewed before
implementation. The proposal MUST state the problem, alternatives considered,
cost, migration impact and testing impact.

## Flujo de Desarrollo

### Feature Planning

Each feature MUST start from a spec under `.specify/specs/[###-feature]/spec.md`
with prioritized user stories, acceptance scenarios, functional requirements,
edge cases, success criteria and assumptions.

The implementation plan MUST include:

- touched modules and dependency direction;
- data model and mapper boundaries;
- security considerations, including secrets and PII handling;
- testing strategy for unit, integration and UI coverage;
- dependency additions or upgrades;
- complexity exceptions, if any.

### Implementation

Implementation order SHOULD follow dependency direction:

1. `domain` contracts, models and use cases;
2. `data` implementations, datasources, persistence and mappers;
3. `feature` UI state, ViewModels and Compose screens;
4. `app` navigation and dependency wiring.

Code review MUST verify architecture boundaries, security rules, test coverage,
documentation for public APIs, lint status and dependency rationale.

### Quality Gates

Before merge, the change MUST pass:

- build for affected modules;
- unit tests for touched domain/ViewModel behavior;
- integration or contract tests for touched persistence, repository or network
  behavior;
- UI tests for changed interactive Compose behavior;
- lint/static analysis with no unowned warnings;
- manual or automated validation of the primary user story.

Any skipped gate MUST be documented in the plan or PR with owner, reason, risk
and follow-up date.

## Governance

This constitution supersedes local preferences, ad hoc patterns and generated
template defaults. Reviewers MUST block changes that violate the Core Principles
unless an explicit, time-bounded exception is documented.

Amendments require:

1. a written proposal describing the change and affected templates;
2. review by the project owner or architecture reviewer;
3. updated constitution version, date and Sync Impact Report;
4. updates to dependent templates or explicit follow-up TODOs.

Versioning policy:

- MAJOR: removes or redefines a principle, changes governance, or requires
  broad migration of existing compliant code.
- MINOR: adds a principle, section, gate or materially expands requirements.
- PATCH: clarifies wording, fixes typos or corrects non-semantic guidance.

Compliance is checked during feature planning, task generation, code review and
merge. A quarterly review SHOULD sample recent changes for architecture,
security, testing and dependency compliance.

**Version**: 1.1.0 | **Ratified**: 2025-01-14 | **Last Amended**: 2026-05-24

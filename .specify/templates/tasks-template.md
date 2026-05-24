---

description: "Task list template for Android feature implementation"
---

# Tasks: [FEATURE NAME]

**Input**: Design documents from `/specs/[###-feature-name]/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Tests are mandatory for behavior changes. Include unit tests for
domain/ViewModel behavior, integration or contract tests for repository,
persistence, mapper, or network behavior, and Compose UI tests for interactive
UI changes. Documentation-only work may omit tests only when stated explicitly.

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- **Android app shell**: `app/src/main/`, `app/src/test/`, `app/src/androidTest/`
- **Domain**: `domain/src/main/`, `domain/src/test/`
- **Data**: `data/src/main/`, `data/src/test/`, `data/src/androidTest/`
- **Feature modules**: `feature/[name]/src/main/`, `feature/[name]/src/test/`, `feature/[name]/src/androidTest/`
- **Core modules**: `core/[name]/src/main/`, `core/[name]/src/test/`
- Paths shown below are examples - adjust based on plan.md structure

<!--
  ============================================================================
  IMPORTANT: The tasks below are SAMPLE TASKS for illustration purposes only.

  The /speckit.tasks command MUST replace these with actual tasks based on:
  - User stories from spec.md (with their priorities P1, P2, P3...)
  - Feature requirements from plan.md
  - Entities from data-model.md
  - Endpoints or contracts from contracts/
  - Constitution gates for architecture, security, testing, maintainability,
    observability, and dependency control

  Tasks MUST be organized by user story so each story can be:
  - Implemented independently
  - Tested independently
  - Delivered as an MVP increment

  DO NOT keep these sample tasks in the generated tasks.md file.
  ============================================================================
-->

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Create or verify project/module structure per implementation plan
- [ ] T002 Configure Gradle modules and version catalog entries for [feature]
- [ ] T003 [P] Configure linting, formatting, and static analysis for touched modules

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**CRITICAL**: No user story work can begin until this phase is complete

Examples of foundational tasks (adjust based on your project):

- [ ] T004 Define domain contracts and shared result/error model in domain/src/main/
- [ ] T005 [P] Configure Hilt bindings and module wiring in app/src/main/ or data/src/main/
- [ ] T006 [P] Setup Room schema/migrations or remote datasource contracts in data/src/main/
- [ ] T007 Create base domain models and mappers that all stories depend on
- [ ] T008 Configure error handling, UI state conventions, and logging infrastructure
- [ ] T009 Setup environment configuration and secrets handling

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - [Title] (Priority: P1) MVP

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 1

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation**

- [ ] T010 [P] [US1] Unit test for [use case/ViewModel] in [module]/src/test/
- [ ] T011 [P] [US1] Integration or contract test for [repository/datasource] in [module]/src/test/ or [module]/src/androidTest/
- [ ] T012 [P] [US1] Compose UI test for [screen state/interaction] in feature/[name]/src/androidTest/

### Implementation for User Story 1

- [ ] T013 [P] [US1] Create domain model/use case in domain/src/main/
- [ ] T014 [P] [US1] Implement repository/datasource/mapper in data/src/main/
- [ ] T015 [US1] Implement ViewModel and UI state in feature/[name]/src/main/
- [ ] T016 [US1] Implement Compose screen and navigation entry point
- [ ] T017 [US1] Add validation, error handling, and retry behavior
- [ ] T018 [US1] Add logging without PII or secrets

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - [Title] (Priority: P2)

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 2

- [ ] T019 [P] [US2] Unit test for [use case/ViewModel] in [module]/src/test/
- [ ] T020 [P] [US2] Integration or contract test for [repository/datasource] in [module]/src/test/ or [module]/src/androidTest/
- [ ] T021 [P] [US2] Compose UI test for [screen state/interaction] in feature/[name]/src/androidTest/

### Implementation for User Story 2

- [ ] T022 [P] [US2] Create domain model/use case in domain/src/main/
- [ ] T023 [US2] Implement repository/datasource/mapper in data/src/main/
- [ ] T024 [US2] Implement ViewModel, UI state, and Compose screen in feature/[name]/src/main/
- [ ] T025 [US2] Integrate with User Story 1 components if needed

**Checkpoint**: At this point, User Stories 1 and 2 should both work independently

---

## Phase 5: User Story 3 - [Title] (Priority: P3)

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 3

- [ ] T026 [P] [US3] Unit test for [use case/ViewModel] in [module]/src/test/
- [ ] T027 [P] [US3] Integration or contract test for [repository/datasource] in [module]/src/test/ or [module]/src/androidTest/
- [ ] T028 [P] [US3] Compose UI test for [screen state/interaction] in feature/[name]/src/androidTest/

### Implementation for User Story 3

- [ ] T029 [P] [US3] Create domain model/use case in domain/src/main/
- [ ] T030 [US3] Implement repository/datasource/mapper in data/src/main/
- [ ] T031 [US3] Implement ViewModel, UI state, and Compose screen in feature/[name]/src/main/

**Checkpoint**: All user stories should now be independently functional

---

[Add more user story phases as needed, following the same pattern]

---

## Phase N: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] TXXX [P] Documentation updates in docs/ or feature/[name]/README.md
- [ ] TXXX Code cleanup and refactoring
- [ ] TXXX Performance optimization across all stories
- [ ] TXXX [P] Additional unit, integration, or Compose UI tests for uncovered risk
- [ ] TXXX Security hardening
- [ ] TXXX Verify KDoc for public APIs in domain/data/core
- [ ] TXXX Verify dependencies are declared in gradle/libs.versions.toml
- [ ] TXXX Run quickstart.md validation

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel if staffed
  - Or sequentially in priority order (P1 -> P2 -> P3)
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - May integrate with US1 but should be independently testable
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - May integrate with US1/US2 but should be independently testable

### Within Each User Story

- Tests MUST be written and fail before implementation for behavior changes
- Domain models/contracts before data implementations
- Data implementations before feature UI integration
- ViewModels and UI state before Compose screen integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel within Phase 2
- Once Foundational phase completes, all user stories can start in parallel if team capacity allows
- All tests for a user story marked [P] can run in parallel
- Domain models within a story marked [P] can run in parallel
- Different user stories can be worked on in parallel by different team members

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together:
Task: "Unit test for [use case/ViewModel] in [module]/src/test/"
Task: "Integration or contract test for [repository/datasource] in [module]/src/test/ or [module]/src/androidTest/"
Task: "Compose UI test for [screen state/interaction] in feature/[name]/src/androidTest/"

# Launch domain/data files that do not touch the same paths:
Task: "Create domain model/use case in domain/src/main/"
Task: "Implement mapper in data/src/main/"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. STOP and VALIDATE: Test User Story 1 independently
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational -> Foundation ready
2. Add User Story 1 -> Test independently -> Deploy/Demo (MVP)
3. Add User Story 2 -> Test independently -> Deploy/Demo
4. Add User Story 3 -> Test independently -> Deploy/Demo
5. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1
   - Developer B: User Story 2
   - Developer C: User Story 3
3. Stories complete and integrate independently

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify behavior tests fail before implementing
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid vague tasks, same-file conflicts, hidden cross-layer dependencies, and cross-story coupling that breaks independence

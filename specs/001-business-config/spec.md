# Feature Specification: Configuración del negocio

**Feature Branch**: `001-business-config`

**Created**: 2026-06-02

**Status**: Draft

**Input**: User description: "Crear un modulo donde se realizara la configuracion del negocio, como Nombre del negocio. Esta configuracion se almacenara en una bd sqlite local en el dispositivo. Si la aplicacion no tiene un registro de configuracion, al iniciarla, se presentara la pantalla para registrar dicha configuracion"

## Clarifications

### Session 2026-06-03

- Q: ¿La configuración inicial debe ser obligatoria antes de usar la aplicación? → A: La configuración inicial es obligatoria; no se puede continuar sin guardar un nombre válido.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Registrar configuración inicial del negocio (Priority: P1)

Como responsable del negocio, quiero que la aplicación me solicite la configuración básica del negocio cuando todavía no existe, para poder comenzar a usar la aplicación con la identidad correcta del negocio.

**Why this priority**: Sin una configuración inicial, la aplicación no puede mostrar ni operar con datos del negocio de forma confiable.

**Independent Test**: Puede probarse iniciando la aplicación sin configuración registrada y verificando que la pantalla de registro se presenta antes de permitir el uso normal.

**Acceptance Scenarios**:

1. **Given** que no existe configuración del negocio registrada, **When** el usuario inicia la aplicación, **Then** se muestra la pantalla para registrar la configuración del negocio.
2. **Given** que el usuario está en la pantalla de registro inicial, **When** captura un nombre de negocio válido y confirma, **Then** la configuración queda guardada y la aplicación continúa al flujo principal.
3. **Given** que el usuario intenta guardar sin nombre de negocio, **When** confirma el formulario, **Then** se muestra una validación clara y la configuración no se guarda.
4. **Given** que no existe configuración del negocio registrada, **When** el usuario intenta continuar sin guardar un nombre válido, **Then** la aplicación permanece en la pantalla de registro inicial.

---

### User Story 2 - Usar configuración existente al iniciar (Priority: P2)

Como usuario de la aplicación, quiero que la aplicación use la configuración ya registrada al iniciar, para evitar capturar la misma información cada vez.

**Why this priority**: Evita fricción diaria y confirma que la configuración local persiste entre sesiones.

**Independent Test**: Puede probarse registrando una configuración, cerrando la aplicación e iniciándola nuevamente para verificar que no se solicita el registro inicial.

**Acceptance Scenarios**:

1. **Given** que ya existe una configuración del negocio registrada, **When** el usuario inicia la aplicación, **Then** la aplicación omite la pantalla de registro inicial y continúa al flujo principal.
2. **Given** que ya existe una configuración registrada, **When** la aplicación necesita mostrar el nombre del negocio, **Then** usa el nombre guardado.

---

### User Story 3 - Consultar y actualizar datos del negocio (Priority: P3)

Como responsable del negocio, quiero acceder a un módulo de configuración para revisar o corregir el nombre del negocio, para mantener actualizada la información visible de la aplicación.

**Why this priority**: El registro inicial cubre el arranque, pero el negocio puede necesitar corregir errores o cambiar su nombre más adelante.

**Independent Test**: Puede probarse entrando al módulo de configuración con una configuración existente, cambiando el nombre y verificando que el nuevo valor se conserva.

**Acceptance Scenarios**:

1. **Given** que existe configuración registrada, **When** el usuario abre el módulo de configuración, **Then** se muestra el nombre actual del negocio.
2. **Given** que el usuario modifica el nombre por un valor válido, **When** guarda los cambios, **Then** el nuevo nombre queda registrado y se usa en el resto de la aplicación.

### Edge Cases

- Si el almacenamiento local no está disponible o falla durante la lectura inicial, la aplicación debe mostrar un estado de error comprensible y permitir reintentar.
- Si el almacenamiento local falla al guardar, la aplicación debe informar el problema sin perder lo que el usuario capturó en pantalla.
- Si existe más de un registro de configuración por datos corruptos o migraciones previas, la aplicación debe tratarlo como un estado inválido y evitar crear configuraciones duplicadas automáticamente.
- Si el nombre del negocio contiene solo espacios en blanco, debe rechazarse como inválido.
- Si el nombre del negocio excede el límite permitido, debe mostrarse una validación antes de guardar.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST determine during application startup whether a business configuration record exists.
- **FR-002**: System MUST show the business configuration registration screen at startup when no business configuration record exists.
- **FR-003**: System MUST allow the user to register the business name as part of the business configuration.
- **FR-004**: System MUST require the business name before saving the business configuration.
- **FR-005**: System MUST prevent normal application use until the initial business configuration is saved with a valid business name.
- **FR-006**: System MUST reject business names that are empty, whitespace-only, or longer than 100 characters.
- **FR-007**: System MUST persist exactly one active business configuration for the device.
- **FR-008**: System MUST reuse the existing business configuration on subsequent startups without asking the user to register it again.
- **FR-009**: System MUST provide a business configuration module where the current business name can be viewed and updated after initial registration.
- **FR-010**: System MUST preserve the user's entered data on screen when a save attempt fails.
- **FR-011**: System MUST provide clear user-facing feedback for validation errors, save failures, and read failures.
- **FR-012**: System MUST preserve Clean Architecture boundaries by keeping business rules out of UI and implementation details out of domain models.
- **FR-013**: System MUST handle null, empty, error, and loading states without crashes or silent failures.
- **FR-014**: System MUST avoid exposing secrets, credentials, tokens, or PII in source code, logs, UI state, or test fixtures.
- **FR-015**: System MUST define testable acceptance coverage for changed domain, data, ViewModel, and UI behavior.

### Architecture & Quality Constraints *(mandatory)*

- **Affected Modules**: domain, data, feature configuration, core/app startup flow.
- **Dependency Direction**: Dependencies must continue to follow domain <- data/feature <- app. Domain models must not depend on local storage details.
- **Security/Privacy**: The business name is business-identifying information and must not be logged unnecessarily or exposed outside the application UI.
- **State & Error Handling**: Startup and configuration screens must represent loading, missing configuration, valid configuration, validation error, persistence error, and retry states.
- **Testing Expectations**: Unit coverage for configuration validation and startup decision logic; data persistence coverage for create/read/update behavior; UI coverage for initial registration and existing-configuration startup paths.
- **Dependency Changes**: Local on-device SQLite storage is an explicit constraint from the feature request; no other new dependency is assumed by this specification.

### Key Entities

- **BusinessConfiguration**: Represents the local configuration for the business using the device. Key attributes include business name, creation timestamp, and last updated timestamp.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of first launches without an existing configuration present the registration screen before normal application use.
- **SC-002**: Users can complete initial business configuration with a valid name in under 1 minute.
- **SC-003**: 100% of subsequent launches with a saved configuration skip the registration screen and continue to the normal application flow.
- **SC-004**: 95% of validation or persistence failures display a clear, actionable message without losing the user's current input.
- **SC-005**: A saved business name remains available after closing and reopening the application across at least 10 consecutive launch cycles.

## Assumptions

- The initial configuration scope for this feature is limited to the business name.
- Only one active business configuration is needed per device.
- The configuration is local to the device and does not need synchronization with other devices in this feature.
- Editing the business name after initial setup is in scope because the requested module is for business configuration, not only first-run setup.
- User authentication and role-based permissions are outside the scope of this feature.

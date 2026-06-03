# UI Contract: Configuración del negocio

## Startup Contract

At app startup, the app checks configuration state before showing the normal application flow.

| State | Required UI Result |
|-------|--------------------|
| Loading | Show non-interactive loading state. |
| MissingConfiguration | Show mandatory business configuration registration screen. |
| Configured | Show normal application flow. |
| InvalidLocalData | Show blocking error state with retry guidance. |
| PersistenceError | Show blocking error state with retry action. |

## Registration Screen Contract

### Inputs

- Business name text field.
- Save action.

### Validation

- Empty or whitespace-only name displays validation feedback and does not save.
- More than 100 characters displays validation feedback and does not save.
- Valid input enables save behavior.

### Save Behavior

- On successful save, app proceeds to normal application flow.
- On save failure, current input remains visible and an actionable error is shown.
- User cannot bypass registration when no valid configuration exists.

## Edit Screen Contract

### Inputs

- Current business name prefilled.
- Save/update action.

### Behavior

- Valid updates persist and become the business name used across the app.
- Invalid updates display validation feedback.
- Save failures preserve current input and show an actionable error.

## Acceptance Coverage

- First launch with no configuration shows registration.
- Attempting to continue without a valid name remains on registration.
- Saving a valid name proceeds to normal flow.
- Relaunch with saved configuration skips registration.
- Opening the module with saved configuration displays the current name.
- Updating the name persists across relaunch.
- Read and save failures show explicit error states.

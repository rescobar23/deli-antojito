# Data Model: Configuración del negocio

## Entity: BusinessConfiguration (`Configuracion`)

Represents the one local business configuration for the current device.

### Fields

| Field | Type | Required | Rules |
|-------|------|----------|-------|
| `id` | Integer | Yes | Stable singleton identity. Exactly one active row is valid. |
| `nombreNegocio` | String | Yes | Trimmed value must not be blank and must be 100 characters or fewer. |
| `createdAt` | Timestamp | Yes | Set when the configuration is first saved. |
| `updatedAt` | Timestamp | Yes | Set when the configuration is created or updated. |

### Identity and Uniqueness

- The device may have zero or one valid active configuration.
- Startup with zero rows means the app must show the mandatory registration screen.
- More than one row is invalid data and must surface an explicit error state instead of silently creating another row.
- Updates must modify the existing active configuration rather than inserting duplicates.

### Validation Rules

- Business name is required.
- Business name is invalid when it is empty, whitespace-only, or longer than 100 characters.
- Persisted business name should be trimmed before save.
- Save failure must preserve the user's current input in UI state.

### State Transitions

```text
Unknown/Loading
  -> MissingConfiguration       # no valid row exists
  -> Configured                 # exactly one valid row exists
  -> InvalidLocalData           # duplicate/corrupt configuration rows
  -> PersistenceError           # database read failure

MissingConfiguration
  -> Saving                     # user submits valid business name
  -> ValidationError            # user submits invalid name

Saving
  -> Configured                 # save succeeds
  -> SaveError                  # save fails, input preserved

Configured
  -> Editing                    # user opens configuration module
  -> Saving                     # user saves a valid update
  -> ValidationError            # user submits invalid update
```

## Repository Contract

The domain repository should support:

- Observing the current configuration state, including missing and error states.
- Saving initial configuration with a valid business name.
- Updating the existing configuration.
- Detecting duplicate local configuration rows.

The data implementation maps Room entities to domain models and must not expose Room-specific types outside `data`.

## Migration Notes

The existing Room table `configuracion` already stores `id` and `nombre_negocio`. Implementation should decide whether a schema migration is required for timestamps and singleton identity. If the current schema remains version `1`, tasks must document why no migration is needed; otherwise bump the Room version and add a migration or approved destructive migration only for non-production data.

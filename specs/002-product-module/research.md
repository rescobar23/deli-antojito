# Research: Product Module

## Decision: Evolve existing Producto model and repository

**Rationale**: The repository already contains `domain.model.Producto`, `ProductoRepository`, `ProductoDao`, `ProductoRepositoryImpl`, a Room entity and mappers. Evolving these files keeps a single canonical product concept and avoids duplicate domain models.

**Alternatives considered**:

- Create a separate product model for this feature: rejected because it would conflict with existing repository contracts and table names.
- Keep current `categoria` field: rejected for this feature because the requested model and spec explicitly scope product data to id, name, base price, image and active status.

## Decision: Use Room migration for productos schema changes

**Rationale**: The current `productos` table has `categoria` and lacks `img` and `activo`. The feature requires local SQLite-compatible persistence through the existing Room database, so a versioned migration is needed to preserve schema validity and avoid runtime crashes.

**Alternatives considered**:

- Drop and recreate the database: rejected because it would lose local business configuration and any existing product data without an explicit requirement.
- Add a second table: rejected because the app already has `productos` and the module should preserve one product catalog.

## Decision: Enforce unique product names in domain validation and persistence

**Rationale**: The clarification states names are unique after trimming spaces and ignoring case. Domain validation gives immediate user feedback, while a DAO/repository guard protects against stale list state or concurrent save attempts.

**Alternatives considered**:

- UI-only validation: rejected because it does not protect repository or persistence calls.
- Database-only validation: rejected because it produces weaker user feedback and complicates test intent.

## Decision: Store optional image as base64 text with JPG/PNG and 2 MB validation

**Rationale**: The requested product model uses `img` as base64 and the accepted clarification limits images to JPG/PNG up to 2 MB. Validation before encoding/storage protects local database size and keeps invalid media out of the model.

**Alternatives considered**:

- Store image file paths: rejected because the requested model explicitly uses base64.
- Allow any image type or size: rejected because it conflicts with the clarification and increases storage risk.

## Decision: Create a dedicated `:feature:product` module

**Rationale**: Product management has multiple screens, independent UI state, ViewModels, validation flows and UI tests. A dedicated module matches the existing `:feature:business-config` pattern and preserves `:feature:home` as a navigation/landing area.

**Alternatives considered**:

- Implement inside `:feature:home`: rejected because it would mix product catalog logic with the home feature and reduce test isolation.
- Put screens in `app`: rejected by the constitution because app should compose navigation and DI, not own feature UI.

## Decision: New products default to active in planning

**Rationale**: The mockup shows `Activo` selected in the add-product form, and the primary user goal is to add products available for sale. This was not confirmed in clarification because planning was requested before the question was answered, so it remains a planning assumption that can be revised before task generation.

**Alternatives considered**:

- Default inactive: safer operationally but adds a step for the primary flow and does not match the mockup.
- Require explicit selection: increases friction and is unnecessary because active/inactive is visible in the form.

# Data Model: Product Module

## Producto

Represents an item sold or offered by the business.

### Fields

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| `id` | Long | Yes | Generated locally; stable after creation |
| `nombre` | String | Yes | Trimmed value must be 1-120 characters and unique ignoring case |
| `precioBase` | Decimal | Yes | Must be a valid monetary value greater than zero |
| `img` | String? | No | Base64 content from JPG/PNG image up to 2 MB before storage, sourced from device selection or camera capture |
| `activo` | Boolean | Yes | Defaults to active for new products unless user changes it |

### Identity and Uniqueness

- `id` is the technical identity and is generated on insert.
- `nombre` is the business uniqueness key.
- Name comparisons trim leading/trailing spaces and ignore letter case.
- Editing a product may keep the same name for the same `id`.
- Editing a product may not take a normalized name already used by another product.

### Lifecycle

```text
Draft form -> Validated -> Persisted active/inactive
Persisted -> Edited -> Persisted active/inactive
Persisted -> Delete confirmation -> Physically deleted
```

### State Rules

- Active products remain stored and available for sale.
- Inactive products remain stored and visible in the module but are not available for sale.
- Physically deleted products are removed from future reads and aggregate totals.
- Products without images remain valid and use a visual placeholder in the UI.
- Canceling a gallery or camera flow must preserve the previously selected image, if any.
- Denying gallery or camera access must not block the rest of the form.

## Product List Summary

Represents aggregate values shown in the initial product screen.

### Fields

| Field | Type | Source |
|-------|------|--------|
| `totalProductos` | Int | Count of all persisted products |
| `totalActivos` | Int | Count of persisted products where `activo` is true |

### Rules

- Summary updates whenever the product list changes.
- Physical deletion immediately decreases `totalProductos` and may decrease `totalActivos`.
- Empty catalogs show both counts as zero.

## Repository Contract

The product repository should expose behavior in domain terms:

- Observe all products ordered for stable list presentation.
- Get one product by `id` for edition.
- Save a new or existing product after validation.
- Check normalized name availability.
- Physically delete a product by `id`.

Repository methods should return explicit success/error results for validation, persistence and missing-product failures instead of throwing into UI state.

## Persistence Notes

- The existing Room `productos` table must be migrated because current rows include `categoria` and do not include `img` or `activo`.
- Migration should preserve existing `id`, `nombre` and `precioBase` values where possible.
- Existing rows should receive `img = null`.
- Existing rows should receive `activo = true` unless a later product decision overrides this before implementation.
- `categoria` should be removed from the domain model for this feature scope. If Room migration cannot drop a column directly on the target SQLite version, use a create-copy-drop-rename migration.

## Validation Results

Product validation should distinguish at least:

- Missing name
- Name too long
- Duplicate name
- Missing or invalid base price
- Invalid image format
- Image too large
- Persistence failure
- Product not found for edit/delete

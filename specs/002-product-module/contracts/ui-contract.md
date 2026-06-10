# UI Contract: Product Module

## Routes

### Product List

**Purpose**: Initial screen for the product module.

**Inputs**:

- None required.

**State**:

- `Loading`
- `Empty(totalProductos = 0, totalActivos = 0)`
- `Loaded(products, totalProductos, totalActivos)`
- `Error(message, canRetry)`

**Displayed Product Fields**:

- Image or placeholder
- Name
- Base price formatted for display
- Active/inactive status
- Row actions menu with edit and delete actions

**Actions**:

- `AddProduct`: navigate to product edit route without an id.
- `EditProduct(id)`: navigate to product edit route with an id.
- `RequestDelete(id)`: show delete confirmation.
- `ConfirmDelete(id)`: physically delete product, refresh list and totals.
- `CancelDelete`: dismiss confirmation with no data change.
- `Retry`: reload product list after error.

**Acceptance Coverage**:

- Products render with required fields.
- Empty catalog shows an empty state and add action.
- Active total matches active products in loaded data.
- Delete confirmation prevents accidental physical deletion.

## Product Add/Edit

**Purpose**: Create a product or edit an existing product.

**Inputs**:

- Optional `productId` for edition.

**State**:

- `LoadingExistingProduct`
- `Editing(form, validationErrors, isSaving)`
- `SaveSuccess(productId)`
- `Error(message, canRetry)`

**Form Fields**:

- Product image picker/drop area
- Product name
- Base price
- Active/inactive selection

**Actions**:

- `ChangeName(value)`
- `ChangeBasePrice(value)`
- `SelectImage(image)`
- `SelectImageFromDevice`
- `CaptureImageFromCamera`
- `RemoveImage`
- `SetActive(value)`
- `Save`
- `NavigateBack`
- `ConfirmDiscardChanges`
- `CancelDiscardChanges`

**Validation Contract**:

- Name is required.
- Name must be 120 characters or fewer.
- Name must be unique ignoring case and outer spaces.
- Base price must be greater than zero.
- Image is optional.
- Image must be JPG/PNG and no larger than 2 MB when provided.
- Canceling either image source keeps the current form image unchanged.
- Permission denial for gallery or camera shows a recoverable message and leaves the form usable.

**Navigation Contract**:

- Successful create returns to the list and shows the created product.
- Successful edit returns to the list and shows updated values.
- Back navigation with dirty form prompts for discard confirmation.
- Back navigation with clean form returns immediately.

## Error and Logging Contract

- UI shows user-facing validation messages without exposing internal exception details.
- Persistence failures show retry-capable messages where retry is meaningful.
- Logs may include product id and operation name.
- Logs must not include base64 image content.

## Out of Scope

- Search implementation
- Filter implementation
- Category or description data capture
- Remote synchronization

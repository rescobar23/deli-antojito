# Feature Specification: Product Module

**Feature Branch**: `[002-product-module]`

**Created**: 2026-06-06

**Status**: Draft

**Input**: User description: "Genera el modulo de producto considerando listado de productos registrados, boton para alta de producto, eliminacion fisica, almacenamiento local SQLite, y modelo Producto con id autoincrement, nombre, precio_base, img base64 y activo."

## Clarifications

### Session 2026-06-06

- Q: Debe el catalogo permitir productos duplicados por nombre? -> A: El nombre del producto debe ser unico, ignorando mayusculas/minusculas y espacios al inicio/fin.
- Q: Que restricciones deben aplicarse a la imagen del producto? -> A: Aceptar solo JPG/PNG de hasta 2 MB.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Consultar productos registrados (Priority: P1)

Como usuario administrador del negocio, quiero ver la lista de productos registrados para revisar rapidamente que productos existen, su precio base, su imagen y si estan activos para venta.

**Why this priority**: Es la pantalla inicial del modulo y permite validar el inventario de productos antes de crear, editar o eliminar registros.

**Independent Test**: Puede probarse entrando al modulo de productos con productos previamente registrados y verificando que la lista muestre cada producto con nombre, precio base, imagen cuando exista y estado activo/inactivo.

**Acceptance Scenarios**:

1. **Given** existen productos registrados, **When** el usuario abre el modulo de productos, **Then** ve el listado de productos con nombre, precio base, imagen y estado.
2. **Given** no existen productos registrados, **When** el usuario abre el modulo de productos, **Then** ve un estado vacio que permite agregar el primer producto.
3. **Given** existen productos activos e inactivos, **When** el usuario abre el modulo, **Then** ve un resumen con el total de productos y el total de productos activos.

---

### User Story 2 - Registrar producto (Priority: P2)

Como usuario administrador del negocio, quiero agregar un producto desde una pantalla dedicada para capturar nombre, precio base, imagen y estado de disponibilidad.

**Why this priority**: Permite construir el catalogo de venta y es la accion principal derivada del listado inicial.

**Independent Test**: Puede probarse desde el listado usando el boton de agregar producto, capturando datos validos, guardando y confirmando que el producto aparece en la lista.

**Acceptance Scenarios**:

1. **Given** el usuario esta en el listado de productos, **When** selecciona agregar producto, **Then** navega a la pantalla de alta de producto.
2. **Given** el usuario captura un nombre valido, precio base valido, imagen opcional y estado, **When** guarda el producto, **Then** el producto queda registrado y el usuario vuelve al listado con el nuevo producto visible.
3. **Given** el usuario intenta guardar sin nombre o sin precio base valido, **When** solicita guardar, **Then** el sistema muestra errores claros y conserva la informacion capturada.

---

### User Story 3 - Editar producto (Priority: P3)

Como usuario administrador del negocio, quiero modificar un producto existente para corregir su nombre, precio base, imagen o estado activo sin crear duplicados.

**Why this priority**: El mockup de alta-edicion indica que la misma experiencia debe soportar cambios sobre productos existentes, lo cual mantiene el catalogo actualizado.

**Independent Test**: Puede probarse seleccionando un producto existente, modificando un campo, guardando y verificando que el listado refleje el cambio en el mismo registro.

**Acceptance Scenarios**:

1. **Given** existe un producto registrado, **When** el usuario abre su edicion, **Then** la pantalla muestra los valores actuales del producto.
2. **Given** el usuario cambia datos validos de un producto, **When** guarda los cambios, **Then** el registro se actualiza y el listado muestra la informacion modificada.

---

### User Story 4 - Eliminar producto fisicamente (Priority: P4)

Como usuario administrador del negocio, quiero eliminar un producto para quitar definitivamente registros que ya no forman parte del catalogo.

**Why this priority**: La funcionalidad fue solicitada explicitamente como eliminacion fisica, pero debe ocurrir despues de poder consultar y gestionar productos.

**Independent Test**: Puede probarse seleccionando eliminar sobre un producto, confirmando la accion y verificando que el producto desaparece del listado y del conteo total.

**Acceptance Scenarios**:

1. **Given** existe un producto registrado, **When** el usuario elige eliminar y confirma, **Then** el producto se elimina definitivamente del catalogo.
2. **Given** el usuario inicia la eliminacion de un producto, **When** cancela la confirmacion, **Then** el producto permanece sin cambios.

### Edge Cases

- Si la lista no tiene productos, el modulo debe mostrar un estado vacio comprensible y mantener disponible la accion de agregar producto.
- Si una imagen no fue capturada o no puede mostrarse, el producto debe presentarse con un marcador visual alterno sin impedir consultar la lista.
- Si el usuario selecciona una imagen que no es JPG/PNG o excede 2 MB, el sistema debe impedir adjuntarla y mostrar el motivo.
- Si el nombre excede 120 caracteres, el sistema debe impedir guardar y explicar el limite.
- Si el nombre coincide con otro producto al ignorar mayusculas/minusculas y espacios al inicio/fin, el sistema debe impedir guardar y explicar que el nombre ya existe.
- Si el precio base es cero, negativo, vacio o no numerico, el sistema debe impedir guardar.
- Si ocurre un error al guardar, editar, listar o eliminar, el usuario debe ver un mensaje de error y poder reintentar sin perder datos capturados en formularios.
- Si el usuario intenta salir de alta o edicion con cambios sin guardar, el sistema debe advertir que los cambios se perderan.
- Si se elimina el ultimo producto, el resumen y la lista deben actualizarse al estado vacio.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST provide a product module initial screen that lists all registered products.
- **FR-002**: System MUST show each listed product with product name, base price, active status and product image when available.
- **FR-003**: System MUST show a product summary with total registered products and total active products.
- **FR-004**: Users MUST be able to navigate from the product list to a dedicated add-product screen.
- **FR-005**: Users MUST be able to create a product with name, base price, image and active status.
- **FR-006**: System MUST assign a unique generated identifier to each product.
- **FR-007**: System MUST require product name and base price before a product can be saved.
- **FR-008**: System MUST limit product name to a maximum of 120 characters.
- **FR-009**: System MUST accept only valid monetary values greater than zero for base price.
- **FR-010**: System MUST store the product image as captured image content when the user provides one.
- **FR-011**: System MUST accept only JPG or PNG product images up to 2 MB.
- **FR-012**: System MUST prevent duplicate product names by comparing names after trimming leading/trailing spaces and ignoring letter case.
- **FR-013**: Users MUST be able to mark a product as active or inactive during creation and edition.
- **FR-014**: Users MUST be able to edit an existing product without changing its identifier.
- **FR-015**: Users MUST be able to delete a product physically after confirming the action.
- **FR-016**: System MUST remove physically deleted products from the product list, totals and future reads.
- **FR-017**: System MUST preserve Clean Architecture boundaries by keeping business rules out of UI and implementation details out of domain models.
- **FR-018**: System MUST handle null, empty, error and loading states without crashes or silent failures.
- **FR-019**: System MUST avoid exposing secrets, credentials, tokens or unnecessary business data in source code, logs, UI state or test fixtures.
- **FR-020**: System MUST define testable acceptance coverage for changed domain, data, ViewModel and UI behavior.

### Architecture & Quality Constraints *(mandatory)*

- **Affected Modules**: domain, data, a product feature module or product feature area, app navigation, and test sources for affected behavior.
- **Dependency Direction**: Dependencies must continue to follow domain <- data/feature <- app. Domain owns product rules and contracts; data owns local persistence; feature owns product UI state and user interactions; app wires navigation.
- **Security/Privacy**: Product names, prices and images are local business data. Logs and test fixtures must avoid unnecessary real business images or sensitive operational data.
- **State & Error Handling**: The module must expose loading, empty, populated, validation-error, save-error, delete-error and retry-capable states. Form input must be preserved after validation or save failures.
- **Testing Expectations**: Unit tests for validation and use cases, persistence tests for create/read/update/delete behavior, ViewModel tests for UI states, and UI tests for listing, add, edit, validation and delete confirmation flows.
- **Dependency Changes**: No new external dependency is expected. The user-requested storage target is local SQLite-compatible persistence.

### Key Entities *(include if feature involves data)*

- **Producto**: Represents an item sold or offered by the business. Key attributes are generated identifier, unique name, base price, optional JPG/PNG image content up to 2 MB and active status. Product name uniqueness is evaluated after trimming leading/trailing spaces and ignoring letter case.
- **Product List Summary**: Represents aggregate counts shown in the product module: total products and active products.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can open the product module and identify the current product catalog status in under 5 seconds.
- **SC-002**: Users can register a valid product from the list screen in under 2 minutes.
- **SC-003**: At least 95% of valid product saves are reflected in the list immediately after completion during acceptance testing.
- **SC-004**: Users can edit or delete an existing product in under 1 minute after locating it in the list.
- **SC-005**: 100% of invalid required-field submissions display a clear validation message and preserve entered form data.
- **SC-006**: Physical deletion removes the selected product from the list and product totals in 100% of delete-confirmation tests.

## Assumptions

- The product module is used by an administrator or operator who already has access to the application.
- Search and filter controls shown in the mockup are visual context for the initial screen; detailed search/filter behavior is outside this specification unless planned later.
- Fields visible in the mockups but absent from the requested Product model, such as description or category, are outside the data scope for this feature.
- Product images are optional; products without an image remain valid.
- Active products are available for sale, while inactive products remain stored but are not available for sale.
- Product deletion is intentionally physical and irreversible after user confirmation.
- Product data remains local to the device and does not require remote synchronization for this feature.

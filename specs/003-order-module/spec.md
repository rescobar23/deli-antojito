# Feature Specification: Módulo de Pedidos

**Feature Branch**: `codex/003-order-module`

**Created**: 2026-10-19

**Status**: Draft

**Input**: User description: "Continuando con el desarrollo de la aplicación, el siguiente módulo a desarrollar es el de Pedidos --files Especificaciones/SRS_Modulo_Pedidos_Android.md"

**Fuente**: [SRS Módulo de Pedidos Android](../../Especificaciones/SRS_Modulo_Pedidos_Android.md), versión 1.0. Esta especificación adopta el alcance funcional propuesto del SRS; las sugerencias de implementación se resolverán en el plan.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Consultar pedidos y su detalle (Priority: P1)

Como personal del negocio, quiero consultar los pedidos y abrir su detalle para conocer lo solicitado y su estado actual.

**Why this priority**: Proporciona la vista central de operación del negocio.

**Independent Test**: Con pedidos de prueba en los cuatro estados, abrir el módulo, comprobar el orden y consultar cada detalle sin registrar nuevos pedidos.

**Acceptance Scenarios**:

1. **Given** pedidos registrados, **When** se abre Pedidos, **Then** aparece el listado del más reciente al más antiguo, con número, cliente, fecha, hora, estado, icono y total monetario; Todos está seleccionada.
2. **Given** una tarjeta, **When** se selecciona, **Then** se abre el detalle con número, cliente, fechas, estado, productos, cantidades, precios históricos, subtotales, total y observaciones si existen.
3. **Given** ningún pedido, **When** se abre el módulo, **Then** aparece un estado vacío explicativo y el acceso al alta.
4. **Given** un error al consultar, **When** se muestra la pantalla, **Then** aparece un mensaje y una acción de reintento; un fallo no se presenta como ausencia de pedidos.

### User Story 2 - Registrar un pedido (Priority: P1)

Como personal del negocio, quiero registrar cliente, productos y cantidades para guardar un pedido con importes correctos.

**Why this priority**: Permite incorporar trabajo nuevo a la operación.

**Independent Test**: Con catálogo disponible, registrar un pedido de dos productos, comprobar importes y volver a consultarlo tras reiniciar la aplicación.

**Acceptance Scenarios**:

1. **Given** el listado, **When** se pulsa +, **Then** se abre el alta con selección de productos, cliente, cantidades y observaciones opcionales.
2. **Given** cliente no vacío y al menos un producto con cantidad entera positiva, **When** se guarda, **Then** se crea un único pedido pendiente, con número único automático y fecha/hora de creación; se regresa al listado actualizado.
3. **Given** dos unidades a $12.50 y tres a $7.10, **When** se calcula el pedido, **Then** los subtotales son $25.00 y $21.30 y el total es $46.30.
4. **Given** cliente vacío, ausencia de productos, cantidad inválida o importe no válido, **When** se intenta guardar, **Then** se señala el problema y no se guarda ningún pedido parcial.
5. **Given** un error al guardar, **When** falla la operación, **Then** se conserva el formulario para corregir o reintentar y no se informa éxito.

### User Story 3 - Editar pedidos pendientes (Priority: P1)

Como personal del negocio, quiero corregir un pedido pendiente antes de iniciar su preparación.

**Why this priority**: Permite corregir solicitudes sin alterar pedidos que ya están en ejecución o finalizados.

**Independent Test**: Con un pedido pendiente existente, cambiar cliente, productos, cantidades y observaciones y verificar la persistencia y los totales.

**Acceptance Scenarios**:

1. **Given** un pedido pendiente, **When** se edita y guarda, **Then** se actualizan los campos permitidos y los importes; se conservan número, fecha de creación y estado.
2. **Given** cambios sin guardar en alta o edición, **When** se intenta salir, **Then** se solicita confirmar su descarte y se permite continuar editando.
3. **Given** un pedido en preparación, completado o cancelado, **When** se consulta o se intenta editar, **Then** no se permite modificar su contenido.
4. **Given** un precio histórico y un precio de catálogo distinto, **When** se modifica la cantidad de una línea existente, **Then** se conserva su precio histórico y se recalcula el subtotal.

### User Story 4 - Administrar preparación y cancelación (Priority: P1)

Como personal del negocio, quiero iniciar, completar o cancelar un pedido según su estado para reflejar su avance real.

**Why this priority**: Evita estados contradictorios y permite coordinar la preparación.

**Independent Test**: Con pedidos en cada estado, ejecutar las transiciones permitidas y rechazar las restantes, comprobando fecha/hora y conservación del registro.

**Acceptance Scenarios**:

1. **Given** un pendiente, **When** se inicia preparación, **Then** pasa a EN_PREPARACION y registra la transición con fecha/hora.
2. **Given** un pedido en preparación, **When** se completa, **Then** pasa a COMPLETADO y queda disponible solo para consulta.
3. **Given** un pendiente o en preparación, **When** se solicita cancelar, **Then** se muestra «¿Estás seguro de que deseas cancelar este pedido? Esta acción no se puede deshacer.»; confirmar lo marca CANCELADO y rechazar conserva el estado.
4. **Given** un completado o cancelado, **When** se intenta cambiar su estado, **Then** se rechaza la operación y se conserva el registro.
5. **Given** un cambio exitoso, **When** se regresa al listado, **Then** tarjetas, resultados filtrados y resumen reflejan el estado actual.

### User Story 5 - Buscar y filtrar pedidos (Priority: P2)

Como personal del negocio, quiero localizar pedidos por cliente, número, estado, fecha o importe para reducir el tiempo de consulta.

**Why this priority**: Hace manejable el historial conforme crece el volumen de pedidos.

**Independent Test**: Con pedidos de distintos clientes, fechas, importes y estados, comprobar cada criterio y sus combinaciones.

**Acceptance Scenarios**:

1. **Given** un pedido #00025 de María López, **When** se busca «  maría  » o «00025», **Then** aparece si satisface los demás filtros; los resultados cambian al escribir.
2. **Given** las pestañas Todos, Pendientes, En preparación, Completados y Cancelados, **When** se selecciona una, **Then** queda como única activa con subrayado Aqua y limita los resultados al estado elegido; Todos elimina esa restricción.
3. **Given** filtros avanzados de fecha, importe y múltiples estados, **When** se aplican, **Then** se intersectan con búsqueda y pestaña; una intersección vacía muestra «No se encontraron pedidos».
4. **Given** fechas inicial y final o importes mínimo y máximo invertidos, **When** se intenta aplicar, **Then** se indica el error y se conservan los filtros previamente aplicados.
5. **Given** cambios en el panel, **When** se cancela, **Then** no se aplican; Limpiar restablece sus campos y Aplicar confirma el conjunto vacío de filtros avanzados.
6. **Given** búsqueda y otros filtros activos, **When** se limpia la búsqueda, **Then** permanecen los demás criterios.

### User Story 6 - Consultar el resumen del día (Priority: P2)

Como personal del negocio, quiero conocer el volumen y estado de los pedidos creados hoy para organizar la jornada.

**Why this priority**: Ofrece una lectura rápida de la carga diaria sin revisar cada pedido.

**Independent Test**: Con pedidos creados hoy y ayer en distintos estados, verificar los cuatro indicadores y cambiar filtros sin alterar sus valores.

**Acceptance Scenarios**:

1. **Given** cinco pedidos creados hoy: dos pendientes, uno en preparación, uno completado y uno cancelado, **When** se muestra el resumen, **Then** indica Pedidos 5, Pendientes 2, En preparación 1 y Completados 1.
2. **Given** pedidos de días previos, **When** cambian de estado hoy, **Then** no se incluyen en los indicadores de pedidos creados hoy.
3. **Given** búsqueda, pestañas o filtros activos, **When** cambian, **Then** no cambian los indicadores diarios; un alta o transición de un pedido de hoy sí los actualiza.
4. **Given** el cambio de día local, **When** se consulta el resumen, **Then** refleja la nueva jornada sin exigir reiniciar la aplicación.

### Edge Cases

- Sin productos activos: informar por qué no puede completarse el alta y permitir volver al catálogo.
- Producto renombrado, desactivado o eliminado después del alta: mantener nombre, precio e importes históricos del pedido y su consulta.
- Pedido #99999: mostrar el consecutivo completo siguiente, sin truncar ni reutilizar números.
- Altas simultáneas o pulsaciones repetidas de guardar: números únicos y un solo pedido por envío del formulario.
- Estado modificado mientras se edita o confirma una acción: validar el estado actual antes de guardar; rechazar acciones que dejaron de ser válidas y ofrecer recargar.
- Fallo durante alta o modificación: conservar íntegra la versión previa; no dejar cabecera, líneas, total o historial parcialmente actualizados.
- Límites de fechas e importes: incluir ambos extremos; permitir límites abiertos y mínimo cero; rechazar importes negativos o cantidades fraccionarias.
- Pedido solicitado que ya no está disponible: mensaje explicativo y retorno al listado sin cierre inesperado.
- Pantalla pequeña o fuente ampliada: permitir desplazarse por pestañas, listado y resumen sin ocultarlos tras la navegación inferior.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El módulo DEBE estar disponible desde la navegación principal y permitir los recorridos listado → alta → listado, listado → detalle y detalle → edición según estado.
- **FR-002**: El listado DEBE presentar tarjetas desplazables en orden descendente de creación con número consecutivo, cliente, fecha/hora, texto e icono del estado y total monetario; seleccionar una tarjeta abre su detalle.
- **FR-003**: La búsqueda «Buscar pedidos...» DEBE encontrar coincidencias parciales por número visible o nombre del cliente, ignorando mayúsculas y espacios extremos, y actualizarse al escribir sin bloquear la interacción.
- **FR-004**: Las cinco pestañas de estado DEBEN tener selección exclusiva, iniciar en Todos, destacar la activa con subrayado Aqua y permitir desplazamiento horizontal si no caben.
- **FR-005**: Los filtros avanzados DEBEN ofrecer fechas inicial/final de creación, importes mínimo/máximo del total y selección múltiple de estados, con Aplicar, Limpiar y Cancelar. No seleccionar estados significa no restringirlos. Búsqueda, pestaña y filtros se combinan por intersección.
- **FR-006**: Las fechas y los importes DEBEN aceptar extremos opcionales e inclusivos, impedir rangos invertidos y mostrar validación sin reemplazar filtros válidos por criterios inválidos.
- **FR-007**: El alta DEBE exigir cliente no vacío tras quitar espacios extremos y al menos una línea de producto, con cantidad entera mayor que cero y precio unitario positivo. Las observaciones son opcionales.
- **FR-008**: El número único, consecutivo e inmutable DEBE asignarse automáticamente al guardar; se muestra con al menos cinco dígitos y prefijo #, y puede crecer sin truncarse. La fecha/hora de creación y el estado PENDIENTE también se asignan automáticamente.
- **FR-009**: Cada línea DEBE conservar la referencia al producto, nombre y precio unitario histórico; el subtotal es cantidad × precio y el total es la suma exacta de subtotales, sin descuentos, impuestos adicionales ni cargos de servicio.
- **FR-010**: La edición DEBE permitir cambiar solo cliente, productos, cantidades y observaciones de pedidos pendientes; recalcular importes y conservar número y fecha de creación. Una línea conservada mantiene su precio histórico; una nueva captura el precio vigente del catálogo.
- **FR-011**: Salir de alta o edición con cambios sin guardar DEBE solicitar confirmación de descarte y permitir continuar editando.
- **FR-012**: El detalle DEBE mostrar todos los datos del pedido y sus líneas. Pendiente ofrece Editar, Iniciar preparación y Cancelar; En preparación ofrece Completar y Cancelar; Completado y Cancelado ofrecen solo consulta.
- **FR-013**: Solo se DEBEN permitir PENDIENTE → EN_PREPARACION, PENDIENTE → CANCELADO, EN_PREPARACION → COMPLETADO y EN_PREPARACION → CANCELADO. El estado vigente se valida al ejecutar cualquier modificación.
- **FR-014**: Cada transición exitosa DEBE conservar estado anterior, nuevo estado y fecha/hora; el pedido mantiene su fecha/hora de actualización. Los estados terminales no permiten reapertura, edición ni cancelación adicional.
- **FR-015**: Cancelar DEBE solicitar el texto de confirmación definido en la historia 4; solo confirmar cambia el estado, conserva el pedido y actualiza el historial. El módulo no permite eliminar físicamente pedidos.
- **FR-016**: El resumen DEBE mostrar Pedidos, Pendientes, En preparación y Completados de los creados en el día local del negocio; Pedidos incluye cancelados. Es independiente de filtros y se actualiza tras altas, cambios de estado y cambio de jornada.
- **FR-017**: Pedidos, líneas, importes e historial DEBEN persistir localmente entre reinicios y permitir la operación completa sin conexión. Cambiar o eliminar un producto del catálogo no altera el historial del pedido.
- **FR-018**: Guardar o modificar DEBE ser una operación indivisible; los fallos preservan la versión anterior y no generan pedidos parciales. Los envíos repetidos no duplican pedidos y las altas simultáneas no repiten consecutivos.
- **FR-019**: La interfaz DEBE diferenciar carga, vacío, sin coincidencias, error de lectura, error de guardado y éxito; ofrecer reintento cuando corresponda y conservar las entradas ante errores recuperables.
- **FR-020**: Los estados DEBEN identificarse por texto, color e icono: pendiente amarillo/reloj, preparación azul/gorro de chef, completado verde/verificación, cancelado rojo/cruz. La interfaz conserva la paleta Aqua del SRS y la consistencia visual existente.
- **FR-021**: El módulo DEBE respetar el tamaño de fuente del sistema, ofrecer controles táctiles de al menos 48 × 48 unidades independientes de densidad y permitir acceder al resumen mediante desplazamiento sin superponerlo a la barra inferior.
- **FR-022**: Los nombres de clientes y observaciones DEBEN mostrarse únicamente en los flujos operativos que los requieren y excluirse de registros de diagnóstico, junto con secretos y credenciales.

### Architecture & Quality Constraints *(mandatory)*

Estas restricciones expresan los límites del proyecto exigidos por la plantilla y constitución; el diseño concreto se define durante la planificación.

- **Affected Modules**: Reglas y contratos en domain; persistencia en data; pantallas y estado de Pedidos en feature; componentes compartidos en core; navegación y composición en app. Productos aporta el catálogo existente.
- **Dependency Direction**: Conservar domain ← data/feature ← app, con core transversal; las reglas no residen en la interfaz y el dominio no conoce detalles de almacenamiento ni de presentación.
- **Security/Privacy**: Datos locales de clientes; sin permisos diferenciados en esta versión, sin información personal en diagnósticos ni secretos en artefactos o pruebas.
- **State & Error Handling**: Resultados de negocio y estados de pantalla explícitos; sin fallos silenciosos, pérdida de entradas ni bloqueos de interacción por lectura o guardado.
- **Testing Expectations**: Cobertura unitaria de reglas y estado de presentación; integración de persistencia, conservación histórica, operaciones indivisibles y concurrencia; pruebas de interacción para navegación, validaciones, filtros, confirmaciones y accesibilidad. Aplicar las metas de cobertura de la constitución en el plan.
- **Dependency Changes**: No se requieren cambios de bibliotecas para especificar el comportamiento. Cualquier propuesta posterior debe justificarse en el plan.

### Key Entities *(include if feature involves data)*

- **Pedido**: Solicitud identificada por número único; cliente, creación, actualización, estado actual, total y observaciones; contiene una o más líneas.
- **Línea de pedido**: Producto referenciado, nombre histórico, cantidad entera, precio histórico y subtotal. Su información se conserva aunque el catálogo cambie.
- **Transición de estado**: Cambio asociado a un pedido con estado previo, estado nuevo y fecha/hora; permite conservar cada transición.
- **Criterios de consulta**: Búsqueda, pestaña, fechas, importes y estados avanzados que delimitan el listado.
- **Resumen diario**: Fecha local de referencia y cuatro conteos calculados sobre los pedidos creados durante ella.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Con hasta 1,000 pedidos locales, el listado aparece en menos de 2 segundos en el dispositivo de prueba acordado durante la planificación.
- **SC-002**: Con ese volumen, el 95% de las búsquedas y cambios de filtro muestran resultados en menos de 1 segundo tras la última entrada y permiten seguir interactuando durante la consulta.
- **SC-003**: En una validación con cinco usuarios representativos, al menos cuatro registran sin asistencia un pedido de dos productos en menos de 2 minutos y localizan y consultan un pedido conocido en menos de 30 segundos.
- **SC-004**: El 100% de los casos de aceptación de cálculo conserva importes exactos, incluidos precios con centavos y cambios posteriores del catálogo.
- **SC-005**: El 100% de las transiciones permitidas funciona y de las transiciones inválidas se rechaza, conservando el historial y sin modificar pedidos terminales.
- **SC-006**: En todos los escenarios de alta/edición con fallo, envío repetido, altas simultáneas y reinicio, no existen números repetidos, registros parciales ni pérdida de pedidos confirmados.
- **SC-007**: Los cuatro indicadores diarios coinciden en el 100% de los casos de prueba con los pedidos creados en el día local, incluidos cancelados, límites de medianoche y cambios de filtros.
- **SC-008**: Todos los recorridos principales pueden completarse sin conexión y con la fuente del sistema ampliada al 200%, manteniendo accesibles controles, estados y resumen.

## Assumptions

- Se adoptan las propuestas DP-001 a DP-010 del SRS: cliente obligatorio, sin descuentos ni impuestos adicionales, edición solo pendiente, cancelación también en preparación, sin eliminación, operación local sin sincronización e historial de cada transición.
- La zona horaria local del dispositivo representa la del negocio en esta versión; el día abarca de medianoche local a la siguiente medianoche. No se incorpora una configuración nueva de zona horaria.
- Se reutiliza la moneda y formato monetario del negocio; si no existe configuración monetaria, se usa MXN con dos decimales. No hay conversión de monedas.
- El catálogo de Productos existente es dependencia del alta. Solo productos activos pueden incorporarse como líneas nuevas; las líneas históricas siguen disponibles para consultar, cambiar cantidad o retirar durante la edición de un pendiente.
- El precio unitario se toma del catálogo al incorporar la línea y no se edita manualmente, coherente con los campos editables del SRS. Repetir un producto con el mismo precio se agrupa sumando cantidades; precios históricos distintos permanecen en líneas separadas.
- La numeración corresponde a una secuencia local por dispositivo, sin reinicio diario. Se garantiza unicidad y crecimiento, sin exigir ausencia de huecos tras operaciones fallidas.
- No se incluyen cobros, pagos, reparto, inventario, cuentas de usuario, sincronización, exportaciones ni cierre de lote. El futuro módulo Cierre de Lote podrá consultar operaciones conservadas; su desarrollo no forma parte de esta funcionalidad.
- La referencia visual disponible es el esquema y la paleta del SRS; no se asume otro archivo de mockup. Las propuestas técnicas de sus secciones 4 a 9 deberán contrastarse con el proyecto durante `/speckit-plan`.

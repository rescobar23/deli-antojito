# Feature Specification: Módulo de Cierre de Lote — consulta preliminar

**Feature Branch**: `codex/004-batch-closing`

**Created**: 2026-10-23

**Status**: Draft — aclarada y lista para planificación de consulta preliminar

**Input**: Continuar el desarrollo desde `Especificaciones/SRS_Modulo_Cierre_de_Lote_Android.md` (v1.0, 2026-10-22), limitado por aclaración del usuario a consulta hasta disponer de Pagos.

## Clarifications

### Session 2026-10-23

- Q: ¿Qué alcance tendrán los pagos y descuentos en esta entrega? → A: C. Desarrollar únicamente la consulta; posponer el cierre definitivo hasta disponer de Pagos.

- Q: ¿Qué pedidos y fecha deben usarse para esta consulta? → A: C. Solo pedidos COMPLETADO, por fecha de creación.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Consultar pedidos de un período (Priority: P1)

Como responsable del negocio, quiero seleccionar un período y revisar los pedidos y sus importes históricos disponibles para conocer la actividad registrada.

**Why this priority**: Ofrece una consulta útil con los datos existentes mientras se desarrolla la capacidad de Pagos.

**Independent Test**: Con pedidos conocidos, consultar un período y comparar cantidad e importes con sus registros originales, sin modificar operaciones.

**Acceptance Scenarios**:

1. **Given** pedidos en distintos estados y fechas, **When** se consulta un período, **Then** resumen y Top 5 incluyen solo pedidos actualmente COMPLETADO creados dentro del intervalo, aunque se hayan completado después de su fin.
2. **Given** inicio igual o posterior al fin, o fin futuro, **When** se consulta, **Then** se explica el error sin presentar resultados como válidos.
3. **Given** un período sin pedidos incluidos, **When** termina la consulta, **Then** cantidad e importe bruto son cero y se muestra un estado vacío; los datos financieros no disponibles siguen identificados como tales.
4. **Given** una consulta en curso o fallida, **When** se muestra la pantalla, **Then** se distingue carga de error y se permite reintentar sin presentar datos anteriores como vigentes.

5. **Given** un pedido creado fuera del período pero completado dentro de él, **When** se consulta, **Then** se excluye del resumen y Top 5.
6. **Given** un pedido pendiente creado dentro del período, **When** pasa a COMPLETADO y se recalcula o reabre la consulta, **Then** se incorpora a ese período de creación.

### User Story 2 - Consultar los productos más vendidos (Priority: P1)

Como responsable, quiero revisar los cinco productos con mayor cantidad registrada en los pedidos incluidos para conocer su distribución.

**Why this priority**: Aprovecha líneas históricas reales sin depender de métodos de pago aún inexistentes.

**Independent Test**: Con líneas conocidas, verificar cantidades, importes y orden del Top 5 sin pagos ni cierres.

**Acceptance Scenarios**:

1. **Given** más de cinco productos incluidos, **When** se consulta la tabla, **Then** aparecen cinco por cantidad descendente, desempate por importe descendente y luego identificador ascendente; no se crea un agregado ficticio «Otros».
2. **Given** un producto renombrado, desactivado o eliminado del catálogo, **When** se consulta su actividad, **Then** se utilizan nombres e importes históricos del pedido.
3. **Given** menos de cinco productos o ninguno, **When** se consulta, **Then** aparecen solo los existentes o un estado vacío.

### User Story 3 - Reconocer los límites de la consulta (Priority: P1)

Como responsable, quiero distinguir los importes de pedidos de los cobros confirmados para no interpretar la consulta preliminar como un cierre financiero.

**Why this priority**: Evita presentar datos inexistentes como ventas cobradas o permitir una acción irreversible sin Pagos.

**Independent Test**: Abrir el módulo sin registros de pagos y verificar los avisos, estados no disponibles y ausencia de acciones de consolidación.

**Acceptance Scenarios**:

1. **Given** que Pedidos no registra pagos ni descuentos, **When** se abre el módulo, **Then** se identifica como consulta preliminar y descuentos, neto y métodos de pago se muestran como «No disponible» con su causa, sin importes o porcentajes inventados.
2. **Given** cualquier resultado de consulta, **When** el usuario lo revisa o sale del módulo, **Then** ningún pedido queda asociado a un lote ni cambia su estado y no se genera un cierre o historial de cierres.
3. **Given** una consulta preliminar, **When** se muestran los importes históricos, **Then** sus etiquetas aclaran que corresponden a pedidos registrados y no acreditan cobros confirmados.

### Edge Cases

- El inicio es inclusivo y el fin exclusivo sobre la fecha de creación; la precisión temporal es de segundos o superior. Un pedido creado exactamente en el fin se excluye aunque se complete en ese período.
- Cambios de día y horario estacional se interpretan en la zona del negocio; los extremos representan instantes inequívocos.
- Períodos anteriores y superpuestos pueden consultar nuevamente los mismos pedidos; esta entrega no los marca como cerrados.
- Ausencia de pagos o descuentos no equivale a un valor financiero cero; no se calcula un neto cobrado a partir del bruto de pedidos.
- Sin conexión la consulta local sigue disponible; una falla de almacenamiento muestra error recuperable sin borrar ni modificar datos.
- Al cambiar rápidamente el período solo se presenta como vigente el resultado de la última selección.
- Importes o cantidades inválidos y desbordamientos generan un error explícito; no producen totales silenciosamente incorrectos.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: El sistema DEBE ofrecer acceso a «Cierre de Lote» desde la navegación existente, conservando acceso a Inicio, Productos, Pedidos y configuración, e identificar esta entrega como consulta preliminar.
- **FR-002**: La pantalla DEBE mostrar período con inicio/fin de fecha y hora, cantidad de pedidos incluidos, importe bruto histórico, Top 5 y estado de disponibilidad de descuentos, total neto y métodos de pago.
- **FR-003**: El período DEBE cumplir inicio < fin y fin no futuro; se interpretará en la zona del negocio y como inicio inclusivo/fin exclusivo. Cambiar cualquier extremo recalcula todos los datos disponibles.
- **FR-004**: La consulta DEBE incluir exclusivamente pedidos cuyo estado actual sea COMPLETADO y cuya fecha de creación cumpla inicio <= fechaCreacion < fin. La fecha de finalización no determina el período. PENDIENTE, EN_PREPARACION y CANCELADO se excluyen del resumen y del Top 5. La interfaz DEBE indicar «Pedidos completados por fecha de creación».
- **FR-005**: Esta entrega DEBE limitarse a consulta con los datos reales existentes. Captura de pagos y descuentos, conciliación financiera y cierre definitivo quedan pospuestos hasta disponer de Pagos; no se simularán pagos confirmados ni descuentos.
- **FR-006**: La elegibilidad de pedidos pagados pendientes/en preparación para consolidación financiera queda diferida a la entrega de cierre definitivo; no condiciona esta consulta de pedidos sin consolidación.
- **FR-007**: Devoluciones, reembolsos y ajustes financieros quedan fuera de esta entrega de consulta; no se presentará ningún importe como cobro conciliado ni saldo reembolsable.
- **FR-008**: El resumen DEBE contar pedidos incluidos distintos y sumar sus importes brutos históricos con precisión monetaria exacta, sin reutilizar precios actuales del catálogo ni descontar valores no registrados. Su etiqueta DEBE indicar que es un importe de pedidos, no de cobros confirmados.
- **FR-009**: Descuentos, total neto, Efectivo, Tarjeta, Transferencia y Otros DEBEN indicarse como «No disponible» porque faltan sus registros de origen; no mostrar importes o porcentajes ficticios, tampoco cero como sustituto de información ausente.
- **FR-010**: La consulta DEBE admitir períodos vacíos mostrando cero pedidos y bruto cero sin cambiar el estado no disponible de los indicadores financieros.
- **FR-011**: El Top 5 DEBE agrupar por identidad de producto las líneas de los mismos pedidos incluidos en el resumen y sumar cantidad e importe bruto histórico; ordenar según US2. Ante nombres históricos distintos, mostrar el más reciente de las líneas incluidas, con desempate estable por identificador de pedido y posición de línea. «Otros» solo representa un producto real.
- **FR-012**: Las notas asociadas a cierres quedan diferidas; esta entrega no captura ni persiste notas de lote o borradores.
- **FR-013**: Esta entrega DEBE omitir la acción ejecutable «Cerrar lote» y su confirmación, explicando que el cierre definitivo estará disponible cuando exista la capacidad de Pagos.
- **FR-014**: La consulta DEBE reflejar cambios de los pedidos incluidos al recalcular o reabrir el módulo y evitar combinar indicadores correspondientes a distintos períodos o versiones de los datos.
- **FR-015**: Consultar DEBE ser una operación de solo lectura respecto de Pedidos y lotes: no asociar operaciones, reservar consecutivos ni cambiar estados.
- **FR-016**: La creación de lotes ABIERTO/CERRADO, consecutivos, transacción de consolidación y asociaciones exclusivas queda diferida al cierre definitivo.
- **FR-017**: La fotografía financiera inmutable, prevención de consolidaciones duplicadas y prohibición de reapertura son objetivos de la futura entrega de cierre, fuera de los entregables actuales.
- **FR-018**: Historial y detalle de cierres quedan diferidos junto con el registro definitivo; esta entrega no genera historial ficticio ni presenta consultas como lotes guardados.
- **FR-019**: El módulo DEBE operar localmente sin conexión y preservar los datos existentes de Pedidos, Productos y configuración.
- **FR-020**: Las pantallas DEBEN usar el estilo Aqua existente, importes legibles con moneda del negocio, etiquetas además de color, adaptación a tamaños compactos/amplios sin desbordamiento horizontal y descripciones accesibles de controles.
- **FR-021**: Los errores DEBEN explicar qué impide consultar y ofrecer reintento cuando sea posible, sin perder el período seleccionado por un fallo recuperable.

### Architecture & Quality Constraints *(mandatory)*

- **Affected Modules**: domain y data para consultas de pedidos existentes; nueva feature de consulta de cierre; core UI para componentes compartidos; app para navegación e integración. No se añade captura financiera a Pedidos ni persistencia de lotes en esta entrega.
- **Dependency Direction**: Conservar domain ← data/feature ← app; reglas de selección y cálculo independientes de presentación y persistencia, sin dependencias entre features ni ciclos.
- **Security/Privacy**: Reutilizar el contexto local sin nuevos permisos ni roles; no registrar clientes, notas, credenciales o datos sensibles en logs. No capturar datos bancarios.
- **State & Error Handling**: Carga, vacío, listo, datos no disponibles, validación y fallo explícitos; estado inmutable y trabajo costoso fuera del hilo de interfaz. Un resultado no disponible no es cero ni un fallo de consulta.
- **Testing Expectations**: Pruebas de reglas monetarias/temporales y ViewModels; integración de consultas y preservación de datos; interacción de período, Top 5, estados no disponibles y navegación. Cobertura mínima del 70% en dominio/ViewModels modificados, conforme a la constitución.
- **Dependency Changes**: No se requieren nuevas bibliotecas desde esta especificación. El plan justificará cualquier incorporación y mantendrá compatibilidad con la plataforma existente.

### Key Entities

- **Período de consulta**: Inicio inclusivo, fin exclusivo y zona del negocio; no representa un lote persistido.
- **Pedido incluido**: Pedido actualmente COMPLETADO creado dentro del intervalo seleccionado, con sus líneas históricas, sin asociación a cierre ni inferencia de pago.
- **Resumen de consulta**: Cantidad distinta de pedidos, importe bruto y disponibilidad de indicadores financieros; no constituye una conciliación.
- **Producto vendido**: Identidad histórica del producto, nombre conservado, cantidad e importe bruto de líneas incluidas; no acredita cobro.
- **Capacidades financieras diferidas**: Pagos, descuentos, lotes y sus fotografías históricas se definirán en una futura especificación de cierre definitivo; no se crean registros sustitutos en esta entrega.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Con 1,000 pedidos locales, el usuario ve resumen y Top 5 actualizados en menos de 2 segundos después de cambiar el período, en el dispositivo de pruebas documentado durante planificación.
- **SC-002**: El 100% de los conjuntos de aceptación reproduce exactamente cantidades e importes brutos esperados de los pedidos incluidos, con los precios históricos.
- **SC-003**: En todos los recorridos de aceptación, consultar, reintentar, cambiar período y salir produce cero modificaciones en pedidos y cero lotes creados.
- **SC-004**: El 100% de consultas con los mismos pedidos y período conserva sus importes al reiniciar o modificar posteriormente el catálogo.
- **SC-005**: El 100% de casos de selección incluye solo pedidos actualmente COMPLETADO por fecha de creación, incluyendo exactamente el inicio y excluyendo el fin; completar dentro del período un pedido creado fuera no lo incorpora.
- **SC-006**: Al menos cuatro de cinco responsables representativos consultan un período y distinguen el importe de pedidos de los cobros confirmados sin ayuda, en menos de dos minutos.
- **SC-007**: En el 100% de escenarios sin registros financieros, los indicadores dependientes se muestran como no disponibles, sin importes simulados ni acción de cierre ejecutable.

## Assumptions

- Fuente: SRS de Cierre de Lote v1.0 e integración en `specs/003-order-module/spec.md` y `plan.md`. La respuesta C limita expresamente esta entrega a consulta; las etapas de consolidación, ejecución e historial del SRS no forman parte de su aceptación.
- Los pedidos existentes contienen importes y líneas históricas, fechas de creación y transiciones de estado, pero no pagos ni descuentos. La consulta usa estado actual COMPLETADO y fecha de creación, conforme a FR-004; no se usa una fecha de pago inexistente.
- Un negocio y moneda por dispositivo; se reutiliza la zona efectiva del negocio actualmente basada en la del dispositivo. Al entrar se propone desde la medianoche local hasta el instante actual; el usuario puede cambiarlo.
- El desglose de productos representa importes brutos de líneas; no se inventa un reparto de descuentos o cobros por producto.
- No se agregan PDF, impresión, servidor, sincronización, inventario, permisos diferenciados, captura de pagos/descuentos, devoluciones, notas persistidas ni historial de cierres.
- Las políticas financieras sobre pagos parciales, pedidos pagados aún pendientes, devoluciones y asignación por confirmación del pago deberán definirse antes de una futura consolidación definitiva; no bloquean la planificación de esta consulta preliminar.
- Los identificadores FR-001 a FR-021 se conservan para trazabilidad; los requisitos expresamente diferidos no generan tareas de implementación de esta entrega.
- La sección Architecture & Quality Constraints responde a la plantilla y constitución; las historias y resultados no prescriben mecanismos de implementación.


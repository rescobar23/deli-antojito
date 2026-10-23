# Trazabilidad de aceptación — 2026-10-21

Las seis historias están implementadas en código. La aceptación de operación en Android no se acredita sin ejecutar las pruebas en dispositivo. La autorización de continuar sin dispositivo permite preparar y compilar, no declarar esos resultados aprobados.

| Requisitos | Evidencia preparada / automatizada | Validación pendiente |
|---|---|---|
| FR-001 | MainNavigationStateTest; OrdersNavigationTest y OrdersRestorationTest | Recorrido completo con gate de configuración y navegación real. |
| FR-002, FR-012, FR-019 | Mappers y ViewModels; OrdersReadScreenTest y PedidoReadContractTest | Render y lecturas/error/reintento en Android. |
| FR-003–FR-006 | Normalización Unicode, importes/rangos, debounce/restauración; filtros SQL/UI preparados | Consultas reales y panel interactivo. |
| FR-007–FR-009 | MoneyTest, Create/Capture tests, formulario y pruebas de alta transaccional | Alta persistida, unicidad, respuesta perdida y concurrencia en SQLite Android. |
| FR-010–FR-011 | Update y OrderEditViewModel tests; snapshots/revisión/descarte preparados | Edición y descarte/reapertura reales. |
| FR-013–FR-015 | Matriz de 16 transiciones; tests de estado/UI/historial preparados | Terminales, cancelación confirmada e historial persistido. |
| FR-016 | Proveedor civil, caso de uso diario y ViewModel; fixture 5/2/1/1 preparado | Invalidación de agregado, medianoche, reanudación y cambio de zona en Android. |
| FR-017–FR-018 | MIGRATION_3_4 + esquema exportado; tests de rollback/reapertura/idempotencia/concurrencia | Ejecutar pruebas Room; compilar sus APK no las sustituye. |
| FR-020–FR-021 | Tokens Aqua/estado, gorro vectorial y pruebas de accesibilidad preparadas | Revisión visual, fuente 200%, targets y desplazamiento en API 25/35. |
| FR-022 | Revisión estática de logs, errores y fronteras | Confirmar diagnósticos sanitizados durante recorridos reales. |

| Criterio | Estado |
|---|---|
| SC-001/SC-002 | Pendiente: harness preparado, sin tiempos medidos. |
| SC-003 | Pendiente: cinco participantes no disponibles en esta sesión. |
| SC-004 | Cálculos JVM aprobados; persistencia histórica por ejecutar. |
| SC-005 | Política de 16 parejas aprobada en JVM; transacciones e historial por ejecutar. |
| SC-006 | Tests de integridad compilables; escenarios reales por ejecutar. |
| SC-007 | Calendario y presentación JVM aprobados; agregado reactivo Android por ejecutar. |
| SC-008 | Diseño local sin servicios remotos; recorrido offline/accesible por ejecutar. |

Responsable de pendientes: implementador de Pedidos, con coordinación del negocio para participantes. Revisión propuesta: 2026-10-28, o al disponer de dispositivos. No hay aprobación de merge/distribución implícita.

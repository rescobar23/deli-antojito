# Implementation Plan: Módulo de Pedidos

**Branch**: `codex/003-order-module` | **Date**: 2026-10-21 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `specs/003-order-module/spec.md`

## Summary

Crear `:feature:orders` con listado, detalle, alta, edición de pendientes, estados, búsqueda, filtros avanzados y resumen diario. Extender domain/data y conectar la tarjeta Pedidos en Inicio. Migrar la base local de versión 3 a 4 agregando pedidos, líneas, transiciones y secuencia sin modificar productos/configuración. Usar precios históricos en centavos, transacciones, revisión optimista y token persistido de idempotencia.

## Technical Context

**Language/Version**: Kotlin 2.0.21, AGP 8.9.1, JVM target 11; wrapper Gradle existente.

**Primary Dependencies**: Compose BOM 2024.09.00, Material 3 (catálogo también declara 1.4.0), Hilt 2.51.1, Room 2.6.1, Coroutines 1.9.0, Lifecycle 2.8.7 y Timber 5.0.1. Sin upgrades ni bibliotecas nuevas.

**Storage**: Room `deli_antojito_db`, actual v3; migración aditiva 3→4, tablas pedidos, pedido_detalle, pedido_transiciones y pedido_secuencia. Sin sincronización ni red.

**Testing**: JUnit4, coroutines-test, integración Room y Compose UI con las dependencias actuales. Cobertura ≥70% del código tocado de domain/ViewModels; configurar informe con herramientas de cobertura de AGP, sin biblioteca de producción nueva.

**Target Platform**: Android móvil, minSdk 25, compile/targetSdk 35 actuales. Conservar compatibilidad API 25 del contexto de producto previo. Usar epoch millis y Calendar/TimeZone en infraestructura; no introducir java.time sin soporte configurado.

**Project Type**: Android mobile app offline.

**Performance Goals**: Con 1,000 pedidos: listado <2 s, p95 búsquedas/filtros <1 s después de última entrada; debounce de búsqueda 300 ms. Alta de dos productos <2 min y localización <30 s por al menos 4/5 usuarios representativos.

**Constraints**: Centavos Long, cantidades Int positivas, operaciones sin overflow, snapshots históricos independientes del catálogo, terminales inmutables, sin eliminación. Datos personales excluidos de logs. Resumen diario independiente de filtros, actualizado en medianoche y reanudación.

**Scale/Scope**: Un negocio/catálogo por dispositivo; seis historias, 22 FR, ocho SC. Tres pantallas: lista, detalle y formulario alta/edición; panel de filtros y confirmaciones. Sin pagos, inventario, reparto, cuentas, sincronización, exportación ni cierre de lote.

## Constitution Check

Evaluación previa a investigación y repetida después del diseño:

| Gate | Pre-design | Post-design | Evidencia / compromiso |
|---|---|---|---|
| Architecture Boundaries | PASS | PASS | domain: reglas/interfaces; data: Room y adaptadores de reloj; orders: presentación; core: UI compartida; app: navegación/DI. domain ← data/feature ← app. Sin orders→product ni feature→data. |
| Security & Kotlin Quality | PASS | PASS | Validación tipada, estado inmutable, sin `!!`, I/O suspendido, reloj/dispatchers inyectables. Propagar CancellationException. No PII ni secretos en logs. |
| Testing Strategy | PASS | PASS | Pruebas primero por historia; unitarias, transacciones reales, migraciones y UI. Cobertura ≥70% en domain/ViewModels tocados. Matriz en quickstart. |
| Maintainability & Observability | PASS | PASS | KDoc de modelos/contratos/repositorios públicos; README de orders; fallos explícitos y logs sanitizados. |
| Dependencies & Change Control | PASS | PASS | Nuevo módulo con dependencias ya catalogadas. Migración aditiva y pruebas de preservación 1→4, 2→4 y 3→4. |

API 25 conserva el contexto existente de `specs/002-product-module/plan.md`, conforme a la excepción por requisitos de producto de la constitución. La ubicación specs/ sigue el flujo instalado y `.specify/feature.json`.

## Project Structure

### Documentation (this feature)

```text
specs/003-order-module/
  spec.md
  plan.md
  research.md
  data-model.md
  quickstart.md
  contracts/{domain-contract,ui-contract}.md
  checklists/requirements.md
  tasks.md                         # siguiente comando; no generado aquí
```

### Source Code (repository root)

```text
settings.gradle.kts                           # include :feature:orders
app/build.gradle.kts                          # dependencia orders
app/src/main/java/com/famessa/deli_antojito/
  MainActivity.kt                             # startup y host
  navigation/{MainDestination,MainNavigation}.kt
  di/PedidoUseCaseModule.kt
feature/home/src/main/java/com/famessa/deli_antojito/feature/home/
  HomeView.kt                                 # onOrdersClick
feature/orders/
  build.gradle.kts
  README.md
  src/main/java/com/famessa/deli_antojito/feature/orders/
    list/{OrdersRoute,OrdersScreen,OrdersViewModel,OrdersUiState}.kt
    detail/{OrderDetailRoute,OrderDetailScreen,OrderDetailViewModel}.kt
    form/{OrderFormRoute,OrderFormScreen,OrderFormViewModel,OrderFormState}.kt
    components/                               # tarjetas, filtros, badge, resumen
domain/src/main/java/com/famessa/deli_antojito/domain/
  model/                                      # Pedido, PedidoLinea, EstadoPedido,
                                              # PedidoConsulta, PedidoDraft, resultados
  repository/PedidoRepository.kt
  usecase/pedido/                             # validar, calcular, observar, escribir
  time/BusinessDayProvider.kt
data/src/main/java/com/famessa/deli_antojito/data/
  Entities/{Pedido,PedidoDetalle,PedidoTransicion,PedidoSecuencia}.kt
  DAO/PedidoDao.kt
  DB/AppDatabase.kt                           # v4 + MIGRATION_3_4
  Repository/PedidoRepositoryImpl.kt
  mappers/PedidoMappers.kt
  di/{DatabaseModule,RepositoryModule}.kt
data/src/main/java/com/famessa/deli_antojito/data/time/ # adaptador reloj/jornada
core/ui/src/main/java/                        # tokens de estado/Aqua para Pedidos
domain/src/test/                             # reglas, cálculos, filtros
data/src/test/                               # mappers/normalización
data/src/androidTest/                        # Room/migración/concurrencia
feature/orders/src/test/                      # ViewModels
feature/orders/src/androidTest/               # UI
app/src/androidTest/                          # navegación y regresiones startup
```

**Structure Decision**: orders usa el catálogo mediante domain, sin depender de las pantallas de product. Conservar carpetas físicas DB/DAO/Entities/Repository existentes aunque los packages sean minúsculos. Sustituir booleanos de MainActivity por destinos tipados guardables y un pequeño back stack en app, sin añadir Navigation Compose. Preservar la configuración inicial obligatoria y los recorridos existentes.

## Implementation Sequence & Validation

1. Pruebas y contratos de domain: dinero, snapshots, validación, consultas y transiciones; después implementación.
2. Pruebas de migración/persistencia; implementar v4 y repositorio transaccional con secuencia/token/revisión.
3. US1 listado/detalle/error/reintento; US2 alta con catálogo; US3 edición/descarte; US4 estados e historial. Pruebas primero por historia.
4. US5 búsqueda normalizada y filtros con borrador; US6 resumen independiente y proveedor de jornada observable.
5. Home, navegación inferior Inicio/Productos/Pedidos, host y DI; accesibilidad, rendimiento y regresiones. Ejecutar gates de quickstart.

La fase de tareas desglosará este orden. Este comando es documental: no implementa ni ejecuta builds del módulo futuro.

## Migration & Operational Risk

- AppDatabase v3 tiene exportSchema=false. Capturar fixture SQL v3 desde entidades/migraciones actuales antes de modificar la base; exportar/versionar esquemas desde v4. Abrir el fixture con Room v4 y verificar datos de productos/configuración. No inventar un esquema histórico exportado.
- Registrar MIGRATION_3_4 junto a 1→2 y 2→3. No fallback destructivo. Un fallo de migración aborta apertura, sin borrar registros para recuperarla.
- No se soporta ejecutar un binario v3 sobre una base v4. Ensayar sobre copia previa y disponer de respaldo del dispositivo de validación antes de distribuir; corregir producción hacia delante con una migración posterior. No se añade funcionalidad de backup/exportación.
- Producto.precioBase es Double: convertir en la frontera de incorporación de línea, pedidos usan solo centavos. No migrar el catálogo monetario en esta entrega.
- Baseline reproducible propuesto para SC-001/002: emulador API 35 perfil Pixel 2, 2 GB RAM/2 vCPU, fuente 100%, animaciones normales, build release sin depurador; 5 calentamientos +30 mediciones con 1,000 pedidos. Registrar host y versión del emulador. Validar funcionalidad adicional en API 25 y fuente 200%. SC-003 requiere además sesiones con cinco usuarios.
- Propietario de gates: implementador de Pedidos. Checks no ejecutables requieren causa, riesgo, responsable y seguimiento fechado antes de merge; no implican aprobación.

## Complexity Tracking

Sin violaciones nuevas. Secuencia, idempotencia y revisión resuelven FR-008/FR-018 y conflictos de escritura; un bloqueo solo en UI no satisface esos requisitos.

## Implementation Verification Notes — 2026-10-21

Se cataloga JaCoCo 0.8.12, ya utilizado por AGP, para consolidar cobertura de dominio desde sus unit tests y los de presentación. No cambia el stack de producción. El harness release de rendimiento se activa explícitamente con `-PordersPerformance=true`, usa firma debug para medición y no es una configuración de distribución.

Lint de Pedidos no tiene errores ni warnings nuevos. `core:ui` conserva avisos de recursos existentes: IconXmlAndPng, IconLauncherShape e IconDuplicates. No se modificaron esos recursos ni se añadieron supresiones. Responsable de revisar la deuda: implementador del proyecto; vencimiento de revisión 2026-10-28. Riesgo acotado al recurso de icono seleccionado/aspecto del launcher; no afecta reglas, almacenamiento ni importes de Pedidos.

El usuario autorizó continuar con lo verificable sin dispositivo. Las migraciones, transacciones y pantallas tienen APK de prueba preparados, pero los gates conectados y la aceptación real permanecen pendientes en validation/release-readiness.md.

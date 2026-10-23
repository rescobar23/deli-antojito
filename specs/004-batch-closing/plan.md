# Implementation Plan: Cierre de Lote — consulta preliminar

**Branch**: `codex/004-batch-closing` | **Date**: 2026-10-23 | **Spec**: [spec.md](spec.md)

**Input**: `specs/004-batch-closing/spec.md`, aclaraciones C/C del 2026-10-23.

## Summary

**Ajuste solicitado 2026-10-23**: la interfaz selecciona un solo día mediante calendario, sin hora, minutos, segundos ni fecha de fin. Este ajuste sustituye la selección manual de extremos descrita más abajo. Reutilizar `BusinessDayProvider.range(fecha, fecha)` para consultar desde medianoche local hasta la siguiente; para hoy limitar el fin al momento de consulta. Conservar el día civil al restaurar/reanudar y recalcular límites en la zona local; actualizar hoy también actualiza el fin. Sin cambios al contrato de lectura `[inicio, fin)` ni al esquema.

Crear `:feature:batch-closing` con selección de fecha/hora, resumen bruto y Top 5 de pedidos actualmente COMPLETADO por fecha de creación. Usar una lectura consistente de las tablas de Pedidos y cálculos exactos en dominio. Mostrar descuentos, neto y métodos como No disponible. Habilitar tarjeta y navegación `close`, preservando configuración inicial y módulos existentes. Sin pagos, notas, consolidación ni historial; sin migración de base.

## Technical Context

**Language/Version**: Kotlin 2.0.21, AGP 8.9.1, Gradle 8.11.1 existente; JDK17 para build, JVM target11.

**Primary Dependencies**: Compose BOM2024.09.00, Material3 del catálogo existente, Hilt2.51.1, Room2.6.1, Coroutines1.9.0, Lifecycle2.8.7, Timber5.0.1; JaCoCo0.8.12 para pruebas. Sin upgrades ni nuevas bibliotecas.

**Storage**: Room deli_antojito_db v4; lectura de pedidos/pedido_detalle vía PedidoDao. Se conservan entidades, esquema exportado, versión e índices existentes; sin tablas de lote ni escrituras de consulta.

**Testing**: JUnit4/coroutines-test para dominio y VM; integración Room y Compose UI existentes. Informe específico de cobertura de dominio/VM tocados ≥70%, incluyendo ejecución desde el nuevo módulo.

**Target Platform**: Android móvil minSdk25, compile/target35; offline. Epoch millis y adaptadores Calendar/TimeZone, sin introducir java.time ni desugaring. Mantener excepción API25 ya documentada en plan de Pedidos por contexto de producto.

**Project Type**: Android mobile app modular.

**Performance Goals**: Consulta completa <2 s con 1,000 pedidos; protocolo en quickstart. Cuatro de cinco responsables consultan y distinguen bruto/cobrado sin ayuda en <2 min.

**Constraints**: COMPLETADO actual; fechaCreacion en [inicio, fin), fin <= now; dinero Money/Long exacto; cantidades agrupadas Long; snapshots históricos; resultado coherente y solo lectura. Loading/Empty/Error/Ready y No disponible separados, sin PII en logs.

**Scale/Scope**: Un negocio/moneda MXN por dispositivo, formato es-MX existente; tres historias, una pantalla y selectores/dialogos auxiliares. FR-006/007/012/016/017/018 delimitan capacidades diferidas; no generan implementación financiera. Consultas pasadas pueden variar al completarse un pedido creado en ese período.

## Constitution Check

Evaluado antes de investigación y repetido después del diseño. PASS aquí expresa conformidad del diseño, no pruebas ejecutadas.

| Gate | Pre-design | Post-design | Evidencia / compromiso |
| --- | --- | --- | --- |
| Architecture Boundaries | PASS | PASS | Domain puro define snapshots, cálculo y contrato; data implementa lectura/time; batch-closing usa domain/core; app integra; sin feature→data ni feature→feature. |
| Security & Kotlin Quality | PASS | PASS | Proyección sin clientes/notas, logs sanitizados, errores tipados, sin !!, I/O y cálculo fuera de main, DI constructor, CancellationException propagada. |
| Testing Strategy | PASS | PASS | Tests primero por historia; integración de filtros/snapshot/no escrituras; UI período/error/No disponible/navegación; cobertura ≥70%; quickstart define evidencia pendiente. |
| Maintainability & Observability | PASS | PASS | KDoc de APIs nuevas; README de feature, códigos de fallo sin PII, correlación local de solicitud solo en diagnóstico. |
| Dependencies & Change Control | PASS | PASS | Sin dependencias nuevas/upgrades ni migración; nuevo módulo usa catálogo y esquema v4 sin cambios. |

Se conserva domain como módulo Android por estructura existente, pero su código nuevo no importa Android/Room/Hilt. `specs/` sigue el workflow instalado y feature.json. No se propone cambiar stack ni reestructurar navegación.

## Project Structure

### Documentation (this feature)

```text
specs/004-batch-closing/
  spec.md
  plan.md
  research.md
  data-model.md
  contracts/domain-contract.md
  contracts/ui-contract.md
  quickstart.md
  checklists/requirements.md
  tasks.md                    # futuro /speckit-tasks; no creado aquí
```

### Source Code (repository root)

Rutas propuestas bajo `src/main/java/com/famessa/deli_antojito/` salvo indicación:

```text
settings.gradle.kts                             # include :feature:batch-closing
app/build.gradle.kts                            # dependencia nueva feature
app/.../navigation/{MainDestination,MainNavigation}.kt
app/.../di/BatchClosingUseCaseModule.kt
feature/home/.../HomeView.kt                     # callback, tarjeta y menú
feature/batch-closing/
  build.gradle.kts
  README.md
  src/main/java/com/famessa/deli_antojito/feature/batchclosing/
    BatchClosingRoute.kt
    BatchClosingScreen.kt
    BatchClosingViewModel.kt
    BatchClosingWorker.kt                       # qualifier de dispatcher inyectado
    BatchClosingUiState.kt
    components/{PeriodCard,SummaryCard,TopProductsTable,UnavailablePaymentsCard}.kt
    components/PeriodDateTimeDialog.kt
  src/main/res/values/strings.xml
domain/.../model/cierre/{CierrePeriodo,PedidoConsultaSnapshot,ConsultaCierre,ConsultaCierreResult}.kt
domain/.../repository/CierreConsultaRepository.kt
domain/.../time/CierreDateTimeResolver.kt
domain/.../usecase/cierre/{ValidateCierrePeriodo,CalculateConsultaCierre,GetConsultaCierre}UseCase.kt
data/.../DAO/PedidoDao.kt                         # proyección/consulta única
data/.../Repository/CierreConsultaRepositoryImpl.kt
data/.../mappers/CierreConsultaMappers.kt
data/.../time/LocalCierreDateTimeResolver.kt
data/.../di/RepositoryModule.kt                    # binding repositorio y resolver
domain/src/test/                                  # cálculo y validación
feature/batch-closing/src/test/                   # VM y carreras
data/src/test/                                   # mapper y adaptador horario
data/src/androidTest/                            # Room lectura consistente/no escrituras
feature/batch-closing/src/androidTest/            # Compose
app/src/test/ y app/src/androidTest/               # destino y recorridos
```

**Structure Decision**: Repositorio específico de consulta para no ampliar contratos de escritura ni transportar cliente/observaciones. Añadir proyección en PedidoDao existente; sin nuevo Dao ni cambios a AppDatabase. Reutilizar componentes Aqua en core/ui sin trasladar reglas de negocio allí. Mantener carpetas físicas DAO/Repository existentes. Resolver fecha-hora mediante interfaz pura y adaptador data/time; no usar BusinessDayProvider.range para rangos con hora.

## Implementation Sequence & Validation

1. Contratos y pruebas de validación/agregación exacta; tests y lectura Room consistente, sin escrituras. Este cimiento habilita US1 y US2.
2. US1: primero pruebas VM/UI de período y carga/vacío/error, después consulta y pantalla básica. Incluir segundos y resolución explícita de ambigüedad horaria.
3. US2: primero pruebas ranking/nombres/totales, después Top 5 integrado al mismo resultado de consulta.
4. US3: pruebas de No disponible/ausencia de acciones financieras; banner, textos, navegación Home y barra inferior; regresiones startup/config/product/orders.
5. Ejecutar gates de quickstart, generar cobertura específica y documentar evidencia. /speckit-tasks desglosará dependencias y tests-first por historia, sin tareas financieras diferidas.

## Operational Risks & Validation Ownership

- No se modifica el esquema v4; comparar schema exportado y datos antes/después de consultas. No añadir índices o tablas por anticipación. Si medición evidencia necesidad de modificar esquema, revisar el plan y definir migración/pruebas antes de cambiarlo.
- Lectura de un conjunto de 1,000 pedidos y sus líneas debe medirse con datos realistas (10 líneas por pedido, 100 productos). No asumir que 1,000 pedidos equivale a 1,000 líneas.
- Reusar BusinessDayProvider para reloj/día inicial y refresh; adaptador específico para componentes civiles y offsets. Tests de gap/overlap y API25 evitan errores de fecha/hora.
- Fin inicial = instante de apertura, no sigue avanzando automáticamente. Reanudar conserva selección y consulta otra vez; ajustar zona mantiene instantes. Cambio manual de reloj puede invalidar un fin previamente válido y debe mostrarse.
- Responsable de gates: implementador de consulta Cierre de Lote. Si falta dispositivo, preparar APK/unit tests, registrar causa/riesgo/evidencia faltante y seguimiento 2026-10-30; no marcar integración/UI/rendimiento/usabilidad como aprobados sin ejecutarlos.
- Deuda preexistente de iconos core/ui documentada en plan de Pedidos (revisión 2026-10-28); no añadir supresiones. Registrar cualquier warning nuevo con propietario y vencimiento o corregirlo.

## Complexity Tracking

Sin violaciones de arquitectura nuevas. La proyección dedicada, resolver horario y cancelación por generación responden a coherencia, API25 y precisión temporal; no requieren persistencia financiera. Detalles del contrato en [domain-contract.md](contracts/domain-contract.md) y [ui-contract.md](contracts/ui-contract.md).


## Implementation Validation Context — 2026-10-23

Está disponible un dispositivo físico TB330FU Android15/API35 (arm64-v8a, memoria reportada8020276kB, pantalla1200×1920). Se adopta como dispositivo de pruebas de SC001, con variante release de medición y parámetros del quickstart. El Pixel2 emulado fue una propuesta inicial sin AVD instalado; no se atribuyen a él resultados físicos. La ejecución adicional en API25 y la revisión TalkBack siguen siendo gates separados. Un proveedor de PedidoDao se añade a DatabaseModule para el repositorio de lectura, sin cambiar AppDatabase ni esquema.

Lint de app conserva dos warnings preexistentes de orientación portrait en AndroidManifest.xml (LockedOrientationActivity y DiscouragedApi). Responsable de revisión: implementador del proyecto; fecha2026-10-30. Riesgo: orientación fija/letterboxing en otros formatos. No se modifica esa política en esta entrega ni se añaden supresiones. Ejecutar suites conectadas de actividad secuencialmente y con --no-parallel; una repetición aislada aprobó tras un fallo de host en ejecución conjunta.


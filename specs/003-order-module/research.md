# Research: Módulo de Pedidos

**Date**: 2026-10-21 | **Spec**: [spec.md](spec.md)

Investigación del repositorio, contrastada con el SRS y una revisión independiente de persistencia, dinero y fechas. No se propone actualizar versiones ni añadir servicios. Todas las decisiones técnicas necesarias para planificar están resueltas.

## 1. Persistencia

**Decision**: Migración aditiva 3→4, cuatro tablas nuevas y exportación de esquemas desde v4. Fixture v3 construido desde entidades y migraciones actuales para validar la actualización real.

**Rationale**: `data/.../DB/AppDatabase.kt` registra solo Producto y Configuracion, v3, exportSchema=false. DatabaseModule conserva migraciones 1→2 y 2→3. No hay pedidos antiguos que transformar.

**Alternatives considered**: Base separada (complica consistencia con catálogo), recreación destructiva (pérdida de datos), automigración sin esquema histórico (no disponible).

## 2. Dinero histórico

**Decision**: `Money` con centavos Long en domain/persistencia. Convertir Producto.precioBase usando BigDecimal.valueOf, escala 2 HALF_UP y conversión exacta; rechazar no finitos, precio redondeado ≤0 y overflow. Multiplicar/sumar con operaciones verificadas. Parsear filtros directamente de texto decimal a centavos.

**Rationale**: Producto usa Double y precio_base es REAL. La validación existente no protege explícitamente todos los casos no finitos. Pedidos debe asegurar su propia frontera sin cambiar el catálogo. Guardar snapshots de nombre/precio.

**Alternatives considered**: Double para total (imprecisión), BigDecimal persistido como texto (complica rangos), migrar Productos (amplía alcance).

## 3. Transacciones e idempotencia

**Decision**: Transacción Room para secuencia, cabecera y líneas; numero y create_token tienen índices únicos. UUID estable por formulario, conservado en SavedStateHandle y reutilizado ante reintentos. Token ya guardado devuelve el pedido existente. Edición/transición usa revisión esperada y estado vigente; actualizar historial en la misma transacción.

**Rationale**: Desactivar Guardar no evita duplicados entre escritores ni tras perder la respuesta de un éxito. La revisión impide sobrescrituras de dos editores del mismo pendiente. No usar MAX+1 fuera de transacción.

**Alternatives considered**: Mutex de pantalla (no persistente), contador en preferencias (no atómico con pedido), timestamp como revisión (colisiones y cambios de reloj).

## 4. Catálogo e historia

**Decision**: Nuevas líneas capturan snapshot al incorporarse desde un producto activo. Dentro de la transacción comprobar que las referencias nuevas siguen existiendo/activas; si no, ProductUnavailable y conservar borrador. No sustituir silenciosamente el snapshot si cambia el precio/nombre del catálogo. Las líneas existentes conservan snapshot por su id; la pantalla solo cambia cantidades o las retira. No FK desde líneas hacia productos; sí FK hacia pedido.

**Rationale**: Productos permite eliminación física. Una cascada eliminaría historia y una restricción alteraría su comportamiento. Referencia histórica y snapshot bastan para pedidos guardados.

**Alternatives considered**: Restringir borrado de productos (cambia módulo anterior), cascada (pierde historia), recalcular al consultar (cambia pedidos).

## 5. Búsqueda Unicode

**Decision**: Persistir cliente_busqueda normalizado NFC + lowercase(Locale.ROOT) y numero_busqueda con mínimo cinco dígitos sin #. Normalizar igual la consulta, quitar # inicial opcional y espacios extremos; coincidencia parcial literal con instr y parámetros. Fechas/importes/estados se filtran en consulta; creación DESC, id DESC. Intersección vacía de estados produce cero resultados, distinta de ausencia de restricción.

**Rationale**: Garantiza MARÍA/María y formas Unicode compuestas/descompuestas sin depender del case folding de SQLite. No se exige búsqueda sin acentos. El límite objetivo de 1,000 pedidos no requiere FTS ni paginación nueva.

**Alternatives considered**: LIKE/NOCASE como única garantía Unicode, filtrado completo en memoria (viable pero menos selectivo), FTS (complejidad innecesaria).

## 6. Jornada y API 25

**Decision**: Contrato puro de reloj/jornada con epoch millis e intervalos [inicio, siguienteInicio). Adaptador Calendar/TimeZone en data/time con zona dinámica e instancias locales por cálculo. Emitir nueva jornada al llegar a medianoche, reanudar o cambiar fecha/hora/zona. No sumar 24 horas fijas. Adaptar DatePicker como fecha de calendario; no interpretar su medianoche UTC como instante local.

**Rationale**: Los Gradle actuales fijan API 25 y no configuran desugaring. Evitar java.time sin soporte adicional. Probar medianoche, cambio horario y cambio de zona con reloj fake.

**Alternatives considered**: Elevar mínimo (rompe compatibilidad), agregar biblioteca temporal/desugaring (innecesario), refrescar solo al abrir (resumen obsoleto).

## 7. Navegación y presentación

**Decision**: Destinos tipados guardables y back stack en app; callbacks públicos de orders y navegación inferior Inicio/Productos/Pedidos en pantallas principales. ViewModels separados por identidad y StateFlow, recolección con repeatOnLifecycle/produceState usando dependencias existentes. BackHandler respeta descarte.

**Rationale**: MainActivity usa booleanos con remember, no un grafo de navegación; Home muestra Pedidos sin onClick. No añadir Navigation Compose únicamente porque el SRS lo sugiere. Guardar destinos y borradores compactos, nunca catálogo ni imágenes en SavedStateHandle.

**Alternatives considered**: Más booleanos (combinaciones inválidas), biblioteca de navegación nueva (innecesaria en este alcance), feature→feature (acoplamiento).

## 8. Diferencias de paleta

**Decision**: Tokens de Pedidos para AquaLv4 #2E8E8E y AquaLv5 #1A6B69 del SRS, reutilizar niveles 1–3 y agregar tokens semánticos de estado. No modificar globalmente los niveles actuales usados por otras pantallas.

**Rationale**: core/ui/.../theme/Color.kt contiene #0B7779 y #04595D para niveles 4/5. Una adaptación acotada respeta la referencia de Pedidos y evita regresiones visuales.

**Alternatives considered**: Cambiar toda la aplicación (fuera de alcance), ignorar diferencia (incumple referencia).

## 9. Calidad y evidencia

**Decision**: Mantener las dependencias existentes de pruebas; pruebas primero por historia, Room real para rollback/concurrencia y fixtures para migración. Cobertura mínima y baseline de rendimiento en plan/quickstart.

**Rationale**: Los tests de producto actuales ya usan Room en memoria; no cubren por sí solos migraciones ni atomicidad de pedidos. La investigación es estática y no constituye evidencia de ejecución.

**Alternatives considered**: Solo fakes (ocultan errores de transacción), solo pruebas manuales (no protegen reglas).

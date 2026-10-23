# US5 — búsqueda y filtros

Implementados NFC/Locale.ROOT, búsqueda parcial literal (incluidos %/_), número con # opcional, intersecciones y rangos inclusivos. La búsqueda espera 300 ms; pestañas/filtros se aplican inmediatamente. El panel conserva su borrador durante restauración y separa Aplicar/Limpiar/Cancelar.

Fechas civiles del DatePicker se interpretan en UTC como fecha, no como instante local; la infraestructura convierte sus límites a zona local. Se valida formato, calendario, importes con hasta dos decimales y rangos invertidos.

Tests JVM y UI preparados; resultados en [test-results.md](test-results.md). T060 pendiente de recorrido interactivo/consulta SQL en Android. No se han medido tiempos.


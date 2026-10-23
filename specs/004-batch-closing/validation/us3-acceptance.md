# US3: aceptación — 2026-10-23

PASS funcional API35 de los tres escenarios de US3. No se habilitan Cerrar lote, notas, historial, descuentos/pagos capturables ni consolidación. Descuentos/neto/métodos aparecen como No disponible con causa; cantidad y bruto se identifican como pedidos, sin afirmar cobro.

FR005/006/007/009/012/013/016/017/018: límites explícitos de UI/contratos, sin modelos financieros ficticios. FR015/SC003: CierreReadOnlyTest + revisión de SQL/API, ver us3-readonly-review.md. SC007: disponibilidad visible en todos los estados y ausencia de acciones financieras; evidencia Compose y revisión del render incondicional del panel.

Consultar y navegar no cambia estados ni asocia operaciones; los casos de dominio/datos y el acceso desde Home no invocan una API de escritura. Validación de comprensión por usuarios: pendiente en usability.md, no sustituida por esta aceptación funcional.

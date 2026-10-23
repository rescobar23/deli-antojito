# US1: integración y UI — 2026-10-23

API35/TB330FU: suite Room de34 pruebas, cero fallos/errores; pruebas de filtro de Cierre incluidas. Suite Compose aislada inicial de8 pruebas, cero fallos/errores. La ejecución posterior conjunta con app provocó fallos de host Compose; repetir únicamente feature con --no-parallel volvió a aprobar8/8 (gradle-ui-isolated.log). No se ocultó ni ignoró el fallo de entorno. El dispositivo estaba despierto y desbloqueado durante el diagnóstico.

El contrato usa estado actual COMPLETADO y fechaCreacion en intervalo semiabierto. Fechas de actualización/finalización fuera del período no lo excluyen. Se verifica cabecera sin líneas como error, no como cero; UI valida período, estado vacío, errores, precisión de segundos y cancelación de edición.

Pendiente: ejecución funcional adicional en API25. No hay AVD configurado ni dispositivo de esa versión. Las pruebas JVM del adaptador usan Calendar/TimeZone sin java.time, pero no acreditan ejecución Android25. Responsable: implementador Cierre; seguimiento2026-10-30; riesgo: diferencias de plataforma/selección horaria. T026 permanece abierta por ese alcance.

La suite final ampliada aprobó9/9 en gradle-ui-final.log; no hubo fallos ni omisiones.

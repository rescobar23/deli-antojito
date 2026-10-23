# Mantenibilidad y privacidad — 2026-10-23

PASS de revisión. APIs públicas de dominio/data nuevas documentadas con KDoc. Repositorio/resolver por constructor y bindings Hilt; casos de uso compuestos en app. No hay Android/Room/Hilt en código nuevo de dominio, ni dependencias de una feature a otra.

Modelos de estado inmutables y snapshots mínimos sin cliente/notas. Trabajo de lectura Room suspendido y agregación en dispatcher de trabajo; resolver civil en dispatcher de trabajo para edición. UI observa el estado con repeatOnLifecycle y callbacks. No se añadieron !! ni secretos, permisos o red. Cancelación no se transforma en fallo de almacenamiento. Fallos esperables son tipados y UI no muestra SQL, stacktrace o excepciones.

Los logs de producción nuevos solo contienen un código/mensaje constante de lectura fallida; fixtures de pruebas sintéticos. README describe contrato de selección, capacidades no disponibles, pruebas y limitaciones.

Ignore verificado: build/, .gradle, .kotlin/, .idea/, out/, classes/jars/logs/.env y temporales; excepción existente para gradle-wrapper.jar preservada. No hay Docker/Node/Terraform/Helm que requieran nuevas exclusiones.

La revisión final eliminó el setter de dispatcher de pruebas del ViewModel. BatchClosingWorker califica el dispatcher inyectado por constructor y app provee Dispatchers.Default. Las pruebas pasan su dispatcher determinista por ese mismo constructor, sin dependencias mutables ocultas.

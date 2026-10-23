# Rendimiento final — 2026-10-23

**SC001: PASS**. Una prueba release, cero fallos/omisiones (gradle-performance-final.log). Dispositivo físico TB330FU, Android15/API35, arm64-v8a, memoria reportada8020276kB, pantalla física1200×1920. Tema de aplicación Deli_antojitoTheme claro. Fuente del sistema115%; el harness fija solo la composición de prueba al100%, sin cambiar preferencias del usuario.

Fixture aislado:1,000 pedidos COMPLETADO,10 líneas por pedido y100 productos (10,000 líneas), con precios históricos. Cinco calentamientos y30 cambios de período medidos. Se mide desde antes de selectPeriod hasta que el conteo/resumen y el último producto del Top5 han sido visitados y comprobados visibles; incluye renderizado y desplazamientos verticales de la pantalla. No se exige que todos estén en el mismo viewport. No es medición de arranque frío.

- Máximo: 333.540077 ms.
- p95: 322.246154 ms.
- Las30 muestras son<2,000ms.
- Tras las muestras se alternan estados para confirmar selección de250 pedidos, fuera de la medición.

```text
samples_ms=[333.540077, 322.246154, 299.441461, 288.179923, 266.244692, 299.776307, 288.383077, 279.148, 287.692231, 312.124307, 289.092923, 289.007, 275.914154, 302.052692, 287.416, 256.26677, 278.802461, 299.229769, 254.296, 268.042077, 278.770462, 242.026692, 279.201, 266.936923, 265.81577, 254.824923, 268.096923, 266.212231, 268.313615, 287.145615]
```

Base Git: 4b697e8543dadc7df901500e1dae7d01e71c0626, con cambios locales sin commit. Release de medición firmado debug exclusivamente mediante -PordersPerformance=true; no es un APK de distribución. SHA256 del release medido: BCDAF1BB8C6E2EF5E648C6FBAB9B188788433EAAA41C919D6FA94B227F06F1B6.

El Pixel2 emulado inicialmente propuesto no está configurado; se adoptó y documentó este dispositivo de pruebas en plan/quickstart. No se extrapolan cifras a API25 ni a ese emulador. La primera serie exploratoria sin desplazamientos usó fuente del sistema115%; no se utiliza como resultado final. Un intento que exigía que resumen y Top5 completo estuvieran en el mismo viewport falló; se corrigió el recorrido del test para respetar la interfaz vertical, sin cambiar los límites ni la implementación de consulta.

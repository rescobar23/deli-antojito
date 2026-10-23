# Cobertura

Configurado enableUnitTestCoverage en debug de domain y feature:orders, sin upgrade de producción. Gradle verificó createDebugUnitTestCoverageReport. :domain:ordersDomainCoverage consolida dominio desde pruebas de dominio y presentación con JaCoCo 0.8.12.

Medición final: dominio tocado 133/148 líneas (89.86%); ViewModels de Pedidos 228/255 líneas (89.41%). Ambas superan la meta ≥70%. Los XML están bajo domain/build/reports/jacoco/ordersDomainCoverage/ y feature/orders/build/reports/coverage/test/debug/. Ver test-results.md para pruebas y límites de ejecución; instrumentación aún requiere dispositivo.


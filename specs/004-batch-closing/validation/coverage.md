# Cobertura — 2026-10-23

JaCoCo0.8.12 ya catalogado. Informes separados del informe de Pedidos:

| Informe | Líneas cubiertas / total | Porcentaje |
| --- | ---: | ---: |
| domain:batchClosingDomainCoverage | 81/81 | 100% |
| feature:batch-closing:batchClosingViewModelCoverage | 63/63 | 100% |

Se incluyen model/cierre, usecase/cierre y contratos temporales de Cierre, además de VM/estado nuevos. El informe de dominio combina ejecución directa y desde VM. Se excluyen fábricas Hilt generadas del informe VM. No se utiliza ordersDomainCoverage como sustituto.

Ambos superan el mínimo70% de líneas del código tocado. Esto no implica cobertura exhaustiva de combinaciones ni sustituye integración/UI/API25 o validación de usuarios.

Fuentes XML: domain/build/reports/jacoco/batchClosingDomainCoverage/batchClosingDomainCoverage.xml y feature/batch-closing/build/reports/jacoco/batchClosingViewModelCoverage/batchClosingViewModelCoverage.xml. Generación incluida en gradle-final-checks.log.

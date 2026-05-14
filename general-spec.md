Archivo que describe en lo general la arquitectura del proyecto android
## 1. Visión general de arquitectura

# Se sigue una combinación de:

- Clean Architecture
- Modularización por features + core compartido
- Unidirectional Data Flow (UDF / MVI simplificado)
- Flujo de dependencias (importante)
  feature → domain → data → core
  app → (feature + domain + data + core)

# Estructura de la aplicación
	
	deli-antojito/
	├── build-logic/                  # (Opcional) Plugins de convención para Gradle
	├── gradle/
	│   └── libs.versions.toml        # Catálogo central de dependencias
	├── app/                          # Orquestador: Inyección de dependencias global y navegación
	│   └── src/main/java/com/app/
	│       └── MainActivity.kt       # Punto de entrada único
	├── core/                         # Módulos transversales (reutilizables)
	│   ├── ui/                       # Design System (Colores, botones, temas)
	│   ├── common/                   # Extensiones de Kotlin, utilidades de fechas
	│   └── network/                  # Cliente Retrofit/OkHttp configurado
	├── data/                         # Capa de datos (Implementación)
	│   ├── database/                 # Room y entidades locales
	│   └── repository/               # Repositorios que deciden si ir a Red o BD
	├── domain/                       # Reglas de negocio (Puro Kotlin)
	│   └── usecases/                 # LoginUseCase.kt, GetUserProducts.kt
	└── feature/                      # Funcionalidades (UI + ViewModels)
	├── login/                    # Pantalla de acceso
	│   └── src/main/java/com/app/login/
	│       ├── LoginScreen.kt
	│       └── LoginViewModel.kt
	├── home/                     # Pantalla principal
	└── profile/                  # Perfil de usuario

# Reglas:

	domain NO depende de nadie
	data depende de domain y core
	feature depende de domain y core
	app depende de todo (solo orquesta)

## 2. Stack tecnológico
	UI
		Jetpack Compose
		Material 3
		Navigation Compose
	DI
		Hilt
	Async
		Kotlin Coroutines + Flow
	Network
		Retrofit + OkHttp + Moshi/Kotlinx Serialization
	DB
		Room
	Otros
		Timber (logs)
		Coil (imágenes)
		Accompanist (opcional)
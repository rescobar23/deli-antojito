Archivo que describe en lo general la arquitectura del proyecto android
## 1. Visión general de arquitectura

# Se sigue una combinación de:

- Clean Architecture
- Modularización por features + core compartido
- Unidirectional Data Flow (UDF / MVI simplificado)
- Flujo de dependencias (importante)
	feature → domain → data → core
	app → (feature + domain + data + core)

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
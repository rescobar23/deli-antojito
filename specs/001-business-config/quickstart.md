# Quickstart: Configuración del negocio

## Prerequisites

- Android Studio or Gradle available for the project.
- Device or emulator using the current project-supported Android configuration.
- Local database state can be cleared between manual validation runs.

## Build

```powershell
.\gradlew.bat :app:assembleDebug
```

## Focused Tests

Run the test set added by this feature once tasks are implemented:

```powershell
.\gradlew.bat :domain:testDebugUnitTest :data:testDebugUnitTest :feature:business-config:testDebugUnitTest
```

Run instrumented persistence/UI coverage on a connected device or emulator:

```powershell
.\gradlew.bat :data:connectedDebugAndroidTest :feature:business-config:connectedDebugAndroidTest :app:connectedDebugAndroidTest
```

## Manual Validation

1. Clear app data.
2. Launch the app.
3. Verify the business configuration registration screen appears before normal app content.
4. Try saving an empty business name and verify validation appears.
5. Enter a valid business name and save.
6. Verify the app proceeds to normal flow.
7. Close and relaunch the app.
8. Verify registration is skipped and the saved name is available.
9. Open the business configuration module, update the name, and save.
10. Relaunch again and verify the updated name persists.

## Failure Validation

- Simulate or force a persistence read failure and verify a blocking retry/error state.
- Simulate or force a save failure and verify the typed business name remains visible.
- Seed duplicate configuration rows and verify the app reports invalid local data instead of creating another row.

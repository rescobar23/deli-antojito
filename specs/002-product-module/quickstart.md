# Quickstart: Product Module

## Prerequisites

- Android SDK configured for the current project.
- Local Gradle wrapper available.
- Device or emulator available for instrumented and Compose UI tests.

## Build

```powershell
./gradlew.bat :app:assembleDebug
```

## Focused Test Commands

Run domain tests:

```powershell
./gradlew.bat :domain:test
```

Run data tests:

```powershell
./gradlew.bat :data:test :data:connectedDebugAndroidTest
```

Run product feature tests after the module is created:

```powershell
./gradlew.bat :feature:product:testDebugUnitTest :feature:product:connectedDebugAndroidTest
```

Run app navigation/integration checks:

```powershell
./gradlew.bat :app:connectedDebugAndroidTest
```

## Manual Validation

1. Launch the app and open the product module.
2. Verify the empty state appears when no products exist and the add action is available.
3. Add a product with a unique name, valid base price, optional JPG/PNG image up to 2 MB and active status, choosing the image from the device or by taking a photo.
4. Confirm the list shows the new product, image or placeholder, formatted price, active status, total products and active products.
5. Try saving another product with the same name using different case or surrounding spaces; confirm the duplicate-name error appears.
6. Try invalid price values: empty, non-numeric, zero and negative; confirm save is blocked and form data remains.
7. Try an unsupported image type or image over 2 MB; confirm the image is rejected with a clear reason.
8. Cancel the gallery or camera flow and confirm the previously selected image remains intact.
9. Deny gallery or camera permission and confirm the form remains usable with a clear message.
10. Edit the product and confirm the existing record updates without creating a duplicate.
11. Delete the product, cancel once, then confirm deletion; verify the product disappears and totals update.
12. Restart the app and confirm persisted products remain, while physically deleted products do not return.

## Notes

- Search/filter controls from the mockup are not part of this feature unless added in a later spec.
- The add form defaults to active in this plan based on the mockup. Revise before `/speckit-tasks` if product ownership decides otherwise.

## Validation Results

Validated on 2026-06-08:

- `./gradlew.bat :domain:test :data:test :feature:product:testDebugUnitTest` passed.
- `./gradlew.bat :data:connectedDebugAndroidTest :feature:product:connectedDebugAndroidTest :app:connectedDebugAndroidTest :app:assembleDebug` passed on connected device `TB330FU - 15`.

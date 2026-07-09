# Луна — календарь месячных и овуляции

Kotlin Multiplatform (Compose Multiplatform) приложение для Android и iOS.
Дизайн — из HTML-прототипа «Луна» (тёплая пастельная тема, Nunito-подобная типографика).

## Стек
Kotlin Multiplatform · Compose Multiplatform (Material 3) · kotlinx-datetime ·
kotlinx-serialization · Ktor Client (ИИ-чат) · Koin · multiplatform-settings.

## Сборка

### Android
```bash
./gradlew :composeApp:assembleDebug
# APK: composeApp/build/outputs/apk/debug/
```
Или откройте проект в Android Studio (Ladybug+) и запустите конфигурацию `composeApp`.

### iOS (нужен macOS + Xcode)
```bash
brew install xcodegen
cd iosApp && xcodegen && open iosApp.xcodeproj
```
Затем запустите таргет `iosApp` на симуляторе. Kotlin-фреймворк собирается
автоматически шагом `embedAndSignAppleFrameworkForXcode`.

## Структура
```
composeApp/src/commonMain/kotlin/com/kozyrevda/menstrualcalendar/
  app/         — точка входа App()
  navigation/  — навигация
  theme/       — дизайн-токены «Луны»
  core/        — модели, логика цикла, хранилище, сеть
  feature/     — экраны (календарь, лог, чат, таблетки, paywall, настройки)
```

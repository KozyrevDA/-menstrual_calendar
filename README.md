# Луна — календарь месячных и овуляции

Kotlin Multiplatform (Compose Multiplatform) приложение для Android и iOS.
Дизайн — из HTML-прототипа «Луна»: тёплая пастельная гамма, карточки 22–24 dp, pill-кнопки.

## MVP-функционал

- ✅ Онбординг (4 шага: интро → дата последних месячных → длина цикла → длина месячных)
- ✅ Главный экран: кольцо прогресса цикла, день, статус фазы, прогноз, карточки-переходы
- ✅ Календарь 7×6: месячные / овуляция / фертильные дни / сегодня, листание месяцев, карточка дня
- ✅ Логирование симптомов и настроения + заметка (точки в календаре)
- ✅ Таблетки: курс 21+7, блистер 28 дней, статусы принято/пропущено/пауза, серия, время напоминания
- ✅ Чат «Луна»: mock-ответы офлайн, готовый Ktor-клиент для реального API (`KtorLunaChatRepository`)
- ✅ Paywall «Луна Premium»: 299 ₽/мес · 2 490 ₽/год · 3 дня за 1 ₽ (UI-заглушка без billing)
- ✅ Настройки: параметры цикла, напоминания, приватный режим, экспорт JSON, удаление данных
- ✅ Локальное хранение (multiplatform-settings): всё переживает перезапуск

## Сборка

### Android
```bash
./gradlew :composeApp:assembleDebug
```
APK: `composeApp/build/outputs/apk/debug/composeApp-debug.apk`

Тесты: `./gradlew :composeApp:testDebugUnitTest`

Либо откройте проект в Android Studio (Ladybug или новее, JDK 17) и запустите конфигурацию `composeApp`.

CI: GitHub Actions (`.github/workflows/android.yml`) на каждый push в `main`
прогоняет тесты, собирает debug-APK и прикладывает его артефактом `luna-debug-apk`.

### iOS (нужен macOS + Xcode)
```bash
brew install xcodegen
cd iosApp && xcodegen && open iosApp.xcodeproj
```
Запустите таргет `iosApp` на симуляторе — Kotlin-фреймворк соберётся автоматически.

## Структура

```
composeApp/src/commonMain/kotlin/com/kozyrevda/menstrualcalendar/
  app/         App.kt — корень Compose-дерева
  navigation/  Screen (sealed), Navigator, AppNavigation — state-based, без библиотек
  theme/       AppColors, AppTypography, AppTheme — дизайн-токены «Луны»
  core/
    model/     CycleSettings, CycleDayInfo, DayLog, PillCourse, ChatMessage…
    logic/     CyclePredictor, PillLogic, русские даты и тексты (+ unit-тесты)
    data/      AppStateHolder (состояние), PersistentStore, LunaChatRepository (mock/Ktor)
  feature/     onboarding, home, calendar, log, pills, chat, stats, paywall, settings
```

## Подключение реального ИИ

В `core/data/KtorLunaChatRepository.kt` заполните `PROXY_URL` (рекомендуется свой
прокси-бэкенд; API-ключ в клиенте не хранить) — фабрика `createLunaChatRepository()`
сама переключит приложение с mock на реальный API.

## Дисклеймер

Прогнозы носят ориентировочный характер и не являются медицинской рекомендацией
или методом контрацепции. Данные хранятся только на устройстве пользовательницы.

# Pre-release аудит — «Календарь месячных» v0.1.0

Ветка: `release/rustore-v0.1.0` · Дата: 10 июля 2026

## 1. Команды сборки

Все команды выполняются в CI (GitHub Actions, чистый раннер = эквивалент `clean`)
на каждый push; статус текущей ветки — зелёный.

| Команда | Результат |
|---|---|
| `./gradlew clean` | ✅ (чистое окружение раннера) |
| `./gradlew :composeApp:test` | ✅ 35 unit-тестов (CyclePredictor, PillLogic, схемы 21+7/24+4/28, LunaSafety, Sanitize) |
| `./gradlew :composeApp:assembleDebug` | ✅ |
| `./gradlew :composeApp:assembleRelease -Pci.unsigned=true` | ✅ R8 + shrinkResources |

## 2. Размеры APK

| Сборка | Размер (артефакт CI) |
|---|---|
| Debug | ≈ 10.3 МБ |
| Release (unsigned, R8 + shrink) | ≈ 1.3 МБ |

## 3. Чек-лист аудита

- ✅ Debug CI зелёный (тесты + debug APK + проверка R8-release на каждый push)
- ✅ Секретов в Git нет: `*.jks`, `*.keystore`, `keystore.properties` — в `.gitignore`, в истории отсутствуют
- ✅ API-ключей нет: `API_KEY`/`PROXY_URL` — пустые константы, приложение работает на офлайн-mock
- ✅ Логирования данных цикла и сообщений нет (`Log.*`/`println` отсутствуют; в release все `android.util.Log` дополнительно вырезаются R8-правилом)
- ✅ Mock-активации Premium в UI нет: кнопка покупки показывает уведомление, помечено `TODO(billing)`
- ✅ Ссылки Privacy/Terms/Support ведут на опубликованные страницы GitHub Pages
- ✅ `applicationId = com.kozyrevda.menstrualcalendar`
- ✅ `versionCode = 1`
- ✅ `versionName = 0.1.0`

## 4. Известные ограничения v0.1.0

- Чат «Луна» работает офлайн (mock); реальный ИИ подключается через `PROXY_URL` + свой прокси-бэкенд
- Push-уведомления не реализованы (настройки времени сохраняются)
- Оплата Premium отключена до интеграции RuStore Billing
- Статистика — базовая (без графиков истории циклов)
- Приватный режим — переключатель без PIN-экрана
- E-mail поддержки в `docs/support.html` и `docs/privacy.html` — заглушка `TODO`
- Запуск release-APK на устройстве после R8 ещё не проверялся вручную

## 5. Обязательные ручные тесты перед подачей в RuStore

1. Установить **release**-APK (подписанный) на реальное устройство
2. Онбординг: 4 шага, «Не помню дату», возврат назад по шагам
3. Главный: кольцо, статус, прогноз, все карточки-переходы
4. Календарь: листание месяцев (через границу года), тап по дню, дисклеймер
5. Симптомы: сохранить/изменить/очистить запись, точка в календаре
6. Таблетки: курс каждой схемы (21+7, 24+4, 28), название, время, отметка/ретро-тап, пропуски
7. Чат: дисклеймер при первом входе; safe-ответы (проверить фразами про лекарства, диагноз, кризис); обычный диалог
8. Paywall: выбор тарифа, уведомление вместо оплаты, ссылки Terms/Privacy открываются
9. Настройки: степперы (прогноз меняется), тумблеры, экспорт в буфер, политика, удаление данных → онбординг
10. Системный Back с каждого экрана; сворачивание/убийство процесса → состояние на месте; поворот экрана
11. Повреждение данных: вручную не проверяется — покрыто unit-тестами санитизации

## 6. Что требуется от владельца для подписанного APK

1. Сгенерировать keystore (см. README, «Release-подпись»): `keytool -genkeypair … -keystore menstrual-calendar-release.jks -alias release …`; хранить вне репозитория, сделать резервную копию
2. Добавить 4 секрета в GitHub → Settings → Secrets and variables → Actions:
   `ANDROID_KEYSTORE_BASE64` (=`base64 -w0 menstrual-calendar-release.jks`), `ANDROID_STORE_PASSWORD`, `ANDROID_KEY_ALIAS`, `ANDROID_KEY_PASSWORD`
3. Запустить workflow **Release build** (Actions → Run workflow) → артефакт `luna-release-apk-v0.1.0-…` с APK и `SHA256SUMS.txt`
4. Заменить e-mail поддержки в `docs/support.html` и `docs/privacy.html`
5. Смержить `release/rustore-v0.1.0` в `main` и переключить GitHub Pages на `main /docs`
6. Пройти ручные тесты из раздела 5 на release-сборке

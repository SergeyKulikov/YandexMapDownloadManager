This is a Kotlin Multiplatform project targeting Android, Desktop (JVM).

* [/shared](./shared/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./shared/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./shared/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./shared/src/jvmMain/kotlin)
    folder is the appropriate location.

### Running the apps

Use the run configurations provided by the run widget in your IDE's toolbar. You can also use these commands and options:

- Android app: `./gradlew :androidApp:assembleDebug`
- Desktop app:
  - Hot reload: `./gradlew :desktopApp:hotRun --auto`
  - Standard run: `./gradlew :desktopApp:run`

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…


# AtomYandexMapManager

## Назначение

**AtomYandexMapManager** — внутренний инструмент компании **ATOM**, предназначенный для автоматизации получения офлайн-карт Яндекс.

Приложение позволяет удаленно управлять процессом загрузки карт на Android-устройстве, получать информацию о ходе загрузки и автоматически передавать скачанные карты на Desktop для последующей доставки в редактор.

Основная цель проекта — исключить ручные операции при подготовке карт и обеспечить полностью автоматизированный процесс их получения.

---

## Основные задачи

Проект решает следующие задачи:

- автоматическое подключение к Android-устройству;
- управление загрузкой офлайн-карт Яндекс;
- контроль состояния загрузки;
- получение информации о доступных и скачанных картах;
- передача файлов карт с Android на Desktop;
- доставка полученных карт в редактор;
- журналирование выполняемых операций.

---

## Архитектура

Проект состоит из двух приложений.

### Desktop

Desktop-приложение является управляющим центром системы.

Оно:

- принимает подключение Android-устройства;
- отправляет команды;
- получает ответы и прогресс выполнения;
- принимает файлы карт;
- передает полученные карты в редактор.

### Android

Android-приложение выполняет команды, поступающие от Desktop.

Оно отвечает за:

- взаимодействие с Яндекс Картами;
- управление загрузкой карт;
- контроль состояния загрузки;
- поиск файлов карт;
- передачу файлов на Desktop.

---

## Схема работы

```
                Android

        Загрузка карт Яндекс
                 │
                 ▼
        Получение файлов карт
                 │
                 ▼
        WebSocket (Ktor)
                 │
                 ▼
               Desktop
                 │
                 ▼
         Передача файлов
            в редактор
```

---

## Взаимодействие приложений

Desktop и Android поддерживают одно постоянное WebSocket-соединение.

Desktop является сервером.

Android автоматически подключается после запуска приложения.

Все взаимодействие осуществляется посредством собственного протокола обмена сообщениями.

---

## Протокол

Каждое сообщение передается внутри объекта `Packet`.

Поддерживаются следующие типы сообщений:

- `Request` — выполнение команды;
- `Response` — результат выполнения;
- `Progress` — уведомление о ходе длительной операции.

Передача файлов выполняется потоково через бинарные WebSocket-фреймы без загрузки файла целиком в память.

---

## Используемые технологии

- Kotlin Multiplatform
- Compose Multiplatform
- Kotlin Coroutines
- Ktor WebSocket
- kotlinx.serialization

---

## Текущее состояние

### Реализовано

- структура проекта;
- сетевой протокол;
- описание команд;
- архитектура взаимодействия.

### В разработке

- WebSocket-транспорт;
- выполнение команд;
- передача файлов;
- автоматическое получение карт;
- интеграция с редактором.

---

## Перспективы развития

Архитектура проекта позволяет в дальнейшем добавить:

- поддержку нескольких устройств;
- очередь заданий;
- автоматическое обновление карт;
- контроль версий карт;
- централизованное журналирование;
- интеграцию с другими внутренними сервисами ATOM.
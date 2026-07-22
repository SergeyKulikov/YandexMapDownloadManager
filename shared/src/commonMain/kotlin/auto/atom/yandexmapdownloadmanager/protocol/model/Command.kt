package auto.atom.yandexmapdownloadmanager.protocol.model

import kotlinx.serialization.Serializable

/**
 * Команды, поддерживаемые протоколом обмена данными между Desktop и Android.
 *
 * Команда передается внутри [Request] и определяет действие, которое должен
 * выполнить получатель.
 *
 * Каждая команда должна завершиться отправкой [Response].
 * Для длительных операций допускается отправка одного или нескольких
 * сообщений [Progress] перед окончательным ответом.
 *
 * Последовательность выполнения:
 *
 * ```
 * Desktop
 *     |
 *     | Request(Command)
 *     |
 * Android
 *     |
 *     | Progress (необязательно)
 *     |
 *     | Response
 *     |
 * Desktop
 * ```
 */
@Serializable
enum class Command {
    NONE,

    /** Проверка доступности удаленного приложения. */
    // PING,

    /** Получение информации об Android-устройстве. */
    GET_DEVICE_INFO,

    GET_REGIONS,

    DOWNLOAD_REGION,

    PAUSE_REGION_DOWNLOAD,

    RESUME_REGION_DOWNLOAD,

    CANCEL_REGION_DOWNLOAD,

    DELETE_REGION,
    GET_PATH,
    GET_REGION_STATE,
    GET_DOWNLOADED_RELEASE_TIME,

    GET_REGION_FILE,

    UPLOAD_FILE,

    DELETE_FILE
}
package auto.atom.yandexmapdownloadmanager.protocol

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

    /** Проверка доступности удаленного приложения. */
    PING,

    /** Получение информации об Android-устройстве. */
    GET_DEVICE_INFO,

    /** Передача файла с Android на Desktop. */
    DOWNLOAD_FILE,

    /** Передача файла с Desktop на Android. */
    UPLOAD_FILE,

    /** Удаление файла на Android. */
    DELETE_FILE,

    /** Начало загрузки офлайн-карты. */
    START_MAP_DOWNLOAD,

    /** Отмена текущей загрузки карты. */
    STOP_MAP_DOWNLOAD
}
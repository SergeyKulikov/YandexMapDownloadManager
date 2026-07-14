package auto.atom.yandexmapdownloadmanager.protocol

import kotlinx.serialization.Serializable

/**
 * Результат выполнения команды.
 *
 * Используется в сообщении [Response].
 */
@Serializable
enum class Status {

    /** Команда успешно выполнена. */
    OK,

    /** Во время выполнения произошла ошибка. */
    ERROR,

    UNSUPPORTED_PROTOCOL,

    NOT_IMPLEMENTED
}
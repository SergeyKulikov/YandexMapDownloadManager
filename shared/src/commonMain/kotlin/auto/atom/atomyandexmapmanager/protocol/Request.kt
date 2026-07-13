package auto.atom.yandexmapdownloadmanager.protocol

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Запрос на выполнение команды.
 *
 * Создается одной стороной соединения и отправляется другой.
 *
 * Каждый запрос имеет уникальный идентификатор [id].
 * Получатель обязан вернуть [Response] с тем же идентификатором.
 *
 * Например:
 *
 * ```
 * Request(
 *     id = "42",
 *     command = Command.GET_DEVICE_INFO
 * )
 * ```
 *
 * @property id Уникальный идентификатор запроса.
 * @property command Выполняемая команда.
 * @property payload Дополнительные параметры команды в формате JSON.
 */
@Serializable
data class Request(
    val id: String,
    val command: Command,
    val payload: JsonElement? = null
) : Message()

package auto.atom.yandexmapdownloadmanager.protocol

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Ответ на ранее полученный [Request].
 *
 * Ответ всегда содержит тот же идентификатор [id], что и исходный запрос.
 * Это позволяет отправителю сопоставить ответ с запросом даже при
 * одновременном выполнении нескольких операций.
 *
 * Если выполнение завершилось успешно, поле [status] содержит
 * [Status.OK].
 *
 * При ошибке используется [Status.ERROR], а описание причины
 * помещается в [error].
 *
 * @property id Идентификатор исходного запроса.
 * @property status Результат выполнения команды.
 * @property payload Возвращаемые данные.
 * @property error Текст ошибки при неудачном выполнении.
 */
@Serializable
data class Response(
    val id: String,
    val command: Command,
    val status: Status,
    val payload: JsonElement? = null,
    val error: String? = null
) : Message()
package auto.atom.yandexmapdownloadmanager.protocol.model

import kotlinx.serialization.Serializable

/**
 * Сообщение о прогрессе выполнения длительной операции.
 *
 * Используется для информирования отправителя о ходе выполнения команды,
 * не дожидаясь ее завершения.
 *
 * После последнего сообщения [Progress] обязательно отправляется
 * итоговый [Response].
 *
 * Типичный сценарий:
 *
 * ```
 * Request
 *      ↓
 * Progress(10)
 *      ↓
 * Progress(40)
 *      ↓
 * Progress(75)
 *      ↓
 * Progress(100)
 *      ↓
 * Response
 * ```
 *
 * @property id Идентификатор запроса.
 * @property progress Прогресс выполнения операции в диапазоне от 0 до 100.
 *
 * @throws IllegalArgumentException если значение [progress] находится
 * вне диапазона от 0 до 100.
 */
@Serializable
data class Progress(
    val id: String,
    val progress: Float
) : Message() {

}
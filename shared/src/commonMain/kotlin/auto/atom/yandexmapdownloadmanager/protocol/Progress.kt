package auto.atom.yandexmapdownloadmanager.protocol

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
 * @property percent Прогресс выполнения операции в диапазоне от 0 до 100.
 *
 * @throws IllegalArgumentException если значение [percent] находится
 * вне диапазона от 0 до 100.
 */
@Serializable
data class Progress(
    val id: String,
    val percent: Int
) : Message() {

    init {
        require(percent in 0..100)
    }
}
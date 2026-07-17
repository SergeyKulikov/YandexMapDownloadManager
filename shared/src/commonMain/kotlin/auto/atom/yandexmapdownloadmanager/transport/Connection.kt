package auto.atom.yandexmapdownloadmanager.transport

import auto.atom.yandexmapdownloadmanager.protocol.model.Packet
import kotlinx.coroutines.flow.StateFlow

/**
 * Соединение между Desktop и Android.
 *
 * Интерфейс скрывает реализацию транспорта от остального приложения.
 * В текущей версии проекта транспортом является TCP на базе Ktor Network,
 * однако бизнес-логика не зависит от конкретной реализации.
 *
 * Типичный сценарий работы:
 *
 * ```
 * val connection: Connection = ...
 *
 * connection.send(Request(...))
 *
 * when (val message = connection.receive()) {
 *     is Response -> ...
 *     is Progress -> ...
 * }
 *
 * connection.close()
 * ```
 */
interface Connection {

    /**
     * Текущее состояние соединения.
     *
     * true — соединение активно.
     * false — соединение разорвано.
     */
    val isConnected: StateFlow<Boolean>

    /**
     * Отправляет сообщение удаленной стороне.
     *
     * Метод завершается после помещения сообщения в транспорт.
     *
     * @param message сообщение для отправки.
     */
    suspend fun send(message: Packet): Boolean

    fun sendAsync(packet: Packet)

    /**
     * Ожидает получение следующего сообщения.
     *
     * Метод блокируется до получения сообщения
     * либо закрытия соединения.
     *
     * @return полученное сообщение.
     */
    suspend fun receive(): Packet?

    /**
     * Закрывает соединение и освобождает связанные ресурсы.
     */
    suspend fun close()
}
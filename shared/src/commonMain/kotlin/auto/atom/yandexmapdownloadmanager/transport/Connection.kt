
package auto.atom.yandexmapdownloadmanager.transport

import auto.atom.yandexmapdownloadmanager.protocol.Message

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
     * Отправляет сообщение удаленной стороне.
     *
     * Метод завершается после помещения сообщения в транспорт.
     *
     * @param message сообщение для отправки.
     */
    suspend fun send(message: Message)

    /**
     * Ожидает получение следующего сообщения.
     *
     * Метод блокируется до получения сообщения
     * либо закрытия соединения.
     *
     * @return полученное сообщение.
     */
    suspend fun receive(): Message

    /**
     * Закрывает соединение и освобождает связанные ресурсы.
     */
    suspend fun close()
}
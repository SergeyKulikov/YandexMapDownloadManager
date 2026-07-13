package auto.atom.yandexmapdownloadmanager.command.handlers

import auto.atom.yandexmapdownloadmanager.command.CommandHandler
import auto.atom.yandexmapdownloadmanager.protocol.Request
import auto.atom.yandexmapdownloadmanager.protocol.Response
import auto.atom.yandexmapdownloadmanager.protocol.Status
import auto.atom.yandexmapdownloadmanager.transport.Connection

/**
 * Обрабатывает команду проверки соединения.
 *
 * Используется для проверки доступности Android-устройства
 * и работоспособности транспортного уровня.
 *
 * Схема работы:
 *
 * Desktop
 *    │
 *    ▼
 * Request(PING)
 *    │
 *    ▼
 * PingHandler
 *    │
 *    ▼
 * Response(Status.OK)
 */
class PingHandler : CommandHandler {

    override suspend fun execute(
        request: Request,
        connection: Connection
    ) {
        connection.send(
            Response(
                id = request.id,
                status = Status.OK
            )
        )
    }
}
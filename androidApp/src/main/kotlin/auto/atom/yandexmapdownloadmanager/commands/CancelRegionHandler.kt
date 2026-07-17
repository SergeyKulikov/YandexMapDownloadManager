package auto.atom.yandexmapdownloadmanager.commands

import auto.atom.yandexmapdownloadmanager.command.CommandHandler
import auto.atom.yandexmapdownloadmanager.map.OfflineYandexMapsManager
import auto.atom.yandexmapdownloadmanager.protocol.Packet
import auto.atom.yandexmapdownloadmanager.protocol.Request
import auto.atom.yandexmapdownloadmanager.protocol.Response
import auto.atom.yandexmapdownloadmanager.protocol.Status
import auto.atom.yandexmapdownloadmanager.protocol.map.RegionPayload
import auto.atom.yandexmapdownloadmanager.transport.Connection
import auto.atom.yandexmapdownloadmanager.transport.ProtocolJson
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * Отменяет выполняющуюся операцию.
 *
 * Используется для прерывания длительных операций,
 * например загрузки или экспорта карты.
 *
 * После получения команды обработчик уведомляет
 * выполняющуюся задачу о необходимости завершения.
 *
 * Схема работы:
 *
 * Desktop
 *    │
 *    ▼
 * Request(CANCEL)
 *    │
 *    ▼
 * CancelHandler
 *    │
 *    ▼
 * Response(Status.CANCELLED)
 */
class CancelRegionHandler (
    private val offlineYandexMapsManager: OfflineYandexMapsManager
) : CommandHandler {

    override suspend fun execute(
        request: Request,
        connection: Connection
    ) {

        val payload =
            ProtocolJson.decodeFromJsonElement<RegionPayload>(
                request.payload!!
            )

        try {
            offlineYandexMapsManager.cancel(payload.regionId)
            connection.sendAsync(
                Packet(
                    message = Response(
                        id = request.id,
                        command = request.command,
                        status = Status.OK
                    )
                )
            )

        } catch (e: Exception) {
            connection.sendAsync(
                Packet(
                    message = Response(
                        id = request.id,
                        command = request.command,
                        status = Status.ERROR,
                        error = e.message ?: "Unknown error"
                    )
                )
            )
        }
    }
}
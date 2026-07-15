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
 * Выполняет загрузку карты Яндекса.
 *
 * Команда может выполняться продолжительное время.
 * Во время работы обработчик отправляет сообщения Progress,
 * позволяя Desktop отображать текущий прогресс загрузки.
 *
 * Схема работы:
 *
 * Desktop
 *    │
 *    ▼
 * Request(DOWNLOAD_MAP)
 *    │
 *    ▼
 * DownloadMapHandler
 *    │
 *    ├────────► Progress(3%)
 *    ├────────► Progress(14%)
 *    ├────────► Progress(42%)
 *    ├────────► Progress(76%)
 *    ├────────► Progress(100%)
 *    ▼
 * Response(Status.OK)
 */
class DownloadRegionHandler(
    private val offlineManager: OfflineYandexMapsManager
) : CommandHandler {
    override suspend fun execute(
        request: Request,
        connection: Connection
    ) {

        val payload =
            ProtocolJson.decodeFromJsonElement<RegionPayload>(
                request.payload!!
            )

        offlineManager.download(payload.regionId)

        connection.send(
            Packet(
                message = Response(
                    id = request.id,
                    command = request.command,
                    status = Status.OK
                )
            )
        )
    }
}
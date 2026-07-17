package auto.atom.yandexmapdownloadmanager.commands

import android.util.Log
import auto.atom.yandexmapdownloadmanager.dispatcher.CommandHandler
import auto.atom.yandexmapdownloadmanager.map.OfflineYandexMapsManager
import auto.atom.yandexmapdownloadmanager.protocol.model.Packet
import auto.atom.yandexmapdownloadmanager.protocol.model.Progress
import auto.atom.yandexmapdownloadmanager.protocol.model.Request
import auto.atom.yandexmapdownloadmanager.protocol.model.Response
import auto.atom.yandexmapdownloadmanager.protocol.model.Status
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionPayload
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

            offlineYandexMapsManager.download(
                regionId = payload.regionId
            ) { progress ->

                Log.d("PROTO", "Send Progress = $progress")

                connection.sendAsync(
                    Packet(
                        message = Progress(
                            id = request.id,
                            progress = progress
                        )
                    )
                )
            }

            Log.d("PROTO", "Download completed")

            Log.d("PROTO", "Send Response OK")

            connection.sendAsync(
                Packet(
                    message = Response(
                        id = request.id,
                        command = request.command,
                        status = Status.OK
                    )
                )
            )

            Log.d("PROTO", "Response queued")

        } catch (e: Exception) {

            Log.e("PROTO", "Download failed", e)

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
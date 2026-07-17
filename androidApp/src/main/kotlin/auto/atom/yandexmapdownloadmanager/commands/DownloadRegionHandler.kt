package auto.atom.yandexmapdownloadmanager.commands

import android.util.Log
import auto.atom.yandexmapdownloadmanager.dispatcher.CommandHandler
import auto.atom.yandexmapdownloadmanager.map.OfflineYandexMapsManager
import auto.atom.yandexmapdownloadmanager.protocol.model.Packet
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionPayload
import auto.atom.yandexmapdownloadmanager.protocol.model.Request
import auto.atom.yandexmapdownloadmanager.protocol.model.Response
import auto.atom.yandexmapdownloadmanager.protocol.model.Status
import auto.atom.yandexmapdownloadmanager.transport.Connection
import auto.atom.yandexmapdownloadmanager.transport.ProtocolJson
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * Выполняет загрузку региона.
 *
 * Прогресс больше не отправляется отсюда.
 * Он приходит через RegionProgressNotification,
 * который рассылает AndroidProtocolHandler.
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

            Log.d(
                "PROTO",
                "Start download region=${payload.regionId}"
            )

            offlineYandexMapsManager.download(
                payload.regionId
            )

            Log.d(
                "PROTO",
                "Download completed"
            )

            Log.d(
                "PROTO",
                "Send Response OK"
            )

            connection.sendAsync(
                Packet(
                    message = Response(
                        id = request.id,
                        command = request.command,
                        status = Status.OK
                    )
                )
            )

            Log.d(
                "PROTO",
                "Response queued"
            )

        } catch (e: Exception) {

            Log.e(
                "PROTO",
                "Download failed",
                e
            )

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
package auto.atom.yandexmapdownloadmanager.commands

import android.util.Log
import auto.atom.yandexmapdownloadmanager.dispatcher.CommandHandler
import auto.atom.yandexmapdownloadmanager.map.OfflineYandexMapsManager
import auto.atom.yandexmapdownloadmanager.protocol.model.Packet
import auto.atom.yandexmapdownloadmanager.protocol.model.Request
import auto.atom.yandexmapdownloadmanager.protocol.model.Response
import auto.atom.yandexmapdownloadmanager.protocol.model.Status
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionPayload
import auto.atom.yandexmapdownloadmanager.transport.Connection
import auto.atom.yandexmapdownloadmanager.transport.ProtocolJson
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * Приостанавливает загрузку региона.
 */
class PauseRegionHandler(
    private val offlineYandexMapsManager: OfflineYandexMapsManager
) : CommandHandler {

    override suspend fun execute(
        request: Request,
        connection: Connection
    ) {
        Log.d("Protocol", "PAUSE request: ${request.payload}")

        val payload =
            ProtocolJson.decodeFromJsonElement<RegionPayload>(
                request.payload!!
            )

        try {

            offlineYandexMapsManager.pause(payload.regionId)

            Log.d("Protocol", "PAUSE request sent to yandex")

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
            Log.e("Protocol", e.message.toString())

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
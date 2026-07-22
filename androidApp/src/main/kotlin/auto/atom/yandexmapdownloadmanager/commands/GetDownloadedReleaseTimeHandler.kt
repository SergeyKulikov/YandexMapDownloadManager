package auto.atom.yandexmapdownloadmanager.commands

import auto.atom.yandexmapdownloadmanager.dispatcher.CommandHandler
import auto.atom.yandexmapdownloadmanager.map.OfflineYandexMapsManager
import auto.atom.yandexmapdownloadmanager.protocol.model.DownloadedReleaseTimePayload
import auto.atom.yandexmapdownloadmanager.protocol.model.Packet
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionPayload
import auto.atom.yandexmapdownloadmanager.protocol.model.Request
import auto.atom.yandexmapdownloadmanager.protocol.model.Response
import auto.atom.yandexmapdownloadmanager.protocol.model.Status
import auto.atom.yandexmapdownloadmanager.transport.Connection
import auto.atom.yandexmapdownloadmanager.transport.ProtocolJson
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

/**
 * Возвращает время выпуска загруженной версии региона.
 */
class GetDownloadedReleaseTimeHandler(
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

            val releaseTime =
                offlineYandexMapsManager.getDownloadedReleaseTime(
                    payload.regionId
                )

            connection.sendAsync(
                Packet(
                    message = Response(
                        id = request.id,
                        command = request.command,
                        status = Status.OK,
                        payload = ProtocolJson.encodeToJsonElement(
                            DownloadedReleaseTimePayload(
                                regionId = payload.regionId,
                                releaseTime = releaseTime
                            )
                        )
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
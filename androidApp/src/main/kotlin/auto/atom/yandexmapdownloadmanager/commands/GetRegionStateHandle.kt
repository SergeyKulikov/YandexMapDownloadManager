package auto.atom.yandexmapdownloadmanager.commands

import auto.atom.yandexmapdownloadmanager.map.OfflineYandexMapsManager
import auto.atom.yandexmapdownloadmanager.command.CommandHandler
import auto.atom.yandexmapdownloadmanager.map.toOfflineRegionState
import auto.atom.yandexmapdownloadmanager.protocol.Packet
import auto.atom.yandexmapdownloadmanager.protocol.Request
import auto.atom.yandexmapdownloadmanager.protocol.Response
import auto.atom.yandexmapdownloadmanager.protocol.Status
import auto.atom.yandexmapdownloadmanager.protocol.map.RegionPayload
import auto.atom.yandexmapdownloadmanager.protocol.map.RegionStatePayload
import auto.atom.yandexmapdownloadmanager.transport.Connection
import auto.atom.yandexmapdownloadmanager.transport.ProtocolJson
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

/**
 * Возвращает текущее состояние региона.
 */
class GetRegionStateHandler(
    private val offlineYandexMapsManager: OfflineYandexMapsManager
) : CommandHandler {

    override suspend fun execute(
        request: Request,
        connection: Connection
    ) {
        try {

            val payload = ProtocolJson.decodeFromJsonElement<RegionPayload>(
                request.payload!!
            )

            val state = offlineYandexMapsManager
                .getState(payload.regionId)
                .toOfflineRegionState()

            connection.sendAsync(
                Packet(
                    message = Response(
                        id = request.id,
                        command = request.command,
                        status = Status.OK,
                        payload = ProtocolJson.encodeToJsonElement(
                            RegionStatePayload(
                                regionId = payload.regionId,
                                state = state
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
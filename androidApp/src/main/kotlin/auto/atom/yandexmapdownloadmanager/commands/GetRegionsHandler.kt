package auto.atom.yandexmapdownloadmanager.commands

import auto.atom.yandexmapdownloadmanager.dispatcher.CommandHandler
import auto.atom.yandexmapdownloadmanager.map.OfflineYandexMapsManager
import auto.atom.yandexmapdownloadmanager.map.filterRussiaRegions
import auto.atom.yandexmapdownloadmanager.map.toOfflineRegionTree
import auto.atom.yandexmapdownloadmanager.protocol.model.Packet
import auto.atom.yandexmapdownloadmanager.protocol.model.Request
import auto.atom.yandexmapdownloadmanager.protocol.model.Response
import auto.atom.yandexmapdownloadmanager.protocol.model.Status
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionsPayload
import auto.atom.yandexmapdownloadmanager.transport.Connection
import auto.atom.yandexmapdownloadmanager.transport.ProtocolJson
import kotlinx.serialization.json.encodeToJsonElement

/**
 * Возвращает список доступных офлайн-регионов.
 */
class GetRegionsHandler(
    private val offlineYandexMapsManager: OfflineYandexMapsManager
) : CommandHandler {

    override suspend fun execute(
        request: Request,
        connection: Connection
    ) {
        try {
            offlineYandexMapsManager.loadRegions { regions ->

                val states = regions.associate { region ->
                    region.id to offlineYandexMapsManager.getState(region.id)
                }

                connection.sendAsync(
                    Packet(
                        message = Response(
                            id = request.id,
                            command = request.command,
                            status = Status.OK,
                            payload = ProtocolJson.encodeToJsonElement(
                                RegionsPayload(
                                    regions.toOfflineRegionTree(states).filterRussiaRegions()
                                )
                            )
                        )
                    )
                )
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}
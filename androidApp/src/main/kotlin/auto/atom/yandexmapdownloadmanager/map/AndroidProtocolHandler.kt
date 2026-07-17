package auto.atom.yandexmapdownloadmanager.map

import auto.atom.yandexmapdownloadmanager.command.CommandDispatcher
import auto.atom.yandexmapdownloadmanager.commands.DownloadRegionHandler
import auto.atom.yandexmapdownloadmanager.commands.GetRegionsHandler
import auto.atom.yandexmapdownloadmanager.protocol.Command
import auto.atom.yandexmapdownloadmanager.protocol.HelloRequest
import auto.atom.yandexmapdownloadmanager.protocol.HelloResponse
import auto.atom.yandexmapdownloadmanager.protocol.Packet
import auto.atom.yandexmapdownloadmanager.protocol.Progress
import auto.atom.yandexmapdownloadmanager.protocol.RegionStateNotification
import auto.atom.yandexmapdownloadmanager.protocol.Request
import auto.atom.yandexmapdownloadmanager.protocol.Response
import auto.atom.yandexmapdownloadmanager.protocol.map.RegionStatePayload
import auto.atom.yandexmapdownloadmanager.transport.Connection
import java.util.UUID

/**
 * Обрабатывает входящие команды Desktop.
 */

class AndroidProtocolHandler(
    private val connection: Connection,
    private val offlineYandexMapsManager: OfflineYandexMapsManager
) {

    init {
        offlineYandexMapsManager.setOnRegionStateChangedListener { regionId, state ->
            connection.sendAsync(
                Packet(
                    message = RegionStateNotification(
                        id = UUID.randomUUID().toString(),
                        payload = RegionStatePayload(
                            regionId = regionId,
                            state = state.toOfflineRegionState()
                        )
                    )
                )
            )
        }
    }
    private val dispatcher = CommandDispatcher(
        mapOf(
            Command.GET_REGIONS to GetRegionsHandler(offlineYandexMapsManager),
            Command.DOWNLOAD_REGION to DownloadRegionHandler(offlineYandexMapsManager),
//            Command.PAUSE_REGION_DOWNLOAD to PauseRegionDownloadHandler(offlineYandexMapsManager),
//            Command.RESUME_REGION_DOWNLOAD to ResumeRegionDownloadHandler(offlineYandexMapsManager),
//            Command.CANCEL_REGION_DOWNLOAD to CancelRegionDownloadHandler(offlineYandexMapsManager),
//            Command.DELETE_REGION to DeleteRegionHandler(offlineYandexMapsManager)
        )
    )

    suspend fun run() {

        while (connection.isConnected.value) {

            val packet = connection.receive() ?: break

            when (val message = packet.message) {
                is Request ->
                    dispatcher.dispatch(message, connection)

                is Response, is Progress, is RegionStateNotification  -> {
                    // Android не ожидает Response
                    // Android не ожидает Progress
                }

                is HelloRequest, is HelloResponse -> {
                    // HelloRequest/HelloResponse уже обработаны во время handshake()
                }

            }
        }
    }
}


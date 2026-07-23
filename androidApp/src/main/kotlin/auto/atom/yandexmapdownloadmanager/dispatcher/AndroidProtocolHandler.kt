package auto.atom.yandexmapdownloadmanager.dispatcher

import auto.atom.yandexmapdownloadmanager.commands.CancelRegionHandler
import auto.atom.yandexmapdownloadmanager.commands.DeleteRegionHandler
import auto.atom.yandexmapdownloadmanager.commands.DownloadRegionHandler
import auto.atom.yandexmapdownloadmanager.commands.GetCachePathHandler
import auto.atom.yandexmapdownloadmanager.commands.GetDownloadedReleaseTimeHandler
import auto.atom.yandexmapdownloadmanager.commands.GetRegionFileHandler
import auto.atom.yandexmapdownloadmanager.commands.GetRegionStateHandler
import auto.atom.yandexmapdownloadmanager.commands.GetRegionsHandler
import auto.atom.yandexmapdownloadmanager.commands.PauseRegionHandler
import auto.atom.yandexmapdownloadmanager.commands.ResumeRegionHandler
import auto.atom.yandexmapdownloadmanager.map.OfflineYandexMapsManager
import auto.atom.yandexmapdownloadmanager.map.toOfflineRegionState
import auto.atom.yandexmapdownloadmanager.protocol.model.Command
import auto.atom.yandexmapdownloadmanager.protocol.model.FileCopyProgressNotification
import auto.atom.yandexmapdownloadmanager.protocol.model.HelloRequest
import auto.atom.yandexmapdownloadmanager.protocol.model.HelloResponse
import auto.atom.yandexmapdownloadmanager.protocol.model.Packet
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionProgressNotification
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionProgressPayload
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionStateNotification
import auto.atom.yandexmapdownloadmanager.protocol.model.Request
import auto.atom.yandexmapdownloadmanager.protocol.model.Response
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionStatePayload
import auto.atom.yandexmapdownloadmanager.transport.BinaryFrame
import auto.atom.yandexmapdownloadmanager.transport.Connection
import auto.atom.yandexmapdownloadmanager.transport.JsonFrame
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Обрабатывает входящие команды Desktop.
 */

class AndroidProtocolHandler(
    private val connection: Connection,
    private val offlineYandexMapsManager: OfflineYandexMapsManager
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

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

        offlineYandexMapsManager.setOnRegionProgressChangedListener { regionId, progress ->
            connection.sendAsync(
                Packet(
                    message = RegionProgressNotification(
                        payload = RegionProgressPayload(
                            regionId = regionId,
                            progress = progress
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
            Command.PAUSE_REGION_DOWNLOAD to PauseRegionHandler(offlineYandexMapsManager),
            Command.RESUME_REGION_DOWNLOAD to ResumeRegionHandler(offlineYandexMapsManager),
            Command.CANCEL_REGION_DOWNLOAD to CancelRegionHandler(offlineYandexMapsManager),
            Command.DELETE_REGION to DeleteRegionHandler(offlineYandexMapsManager),
            Command.GET_PATH to GetCachePathHandler(offlineYandexMapsManager),
            Command.GET_REGION_STATE to GetRegionStateHandler(offlineYandexMapsManager),
            Command.GET_DOWNLOADED_RELEASE_TIME to GetDownloadedReleaseTimeHandler(offlineYandexMapsManager),
            Command.GET_REGION_FILE to GetRegionFileHandler(offlineYandexMapsManager),
        )
    )

    suspend fun run() {

        while (connection.isConnected.value) {

            when (val incoming = connection.receive()) {
                null -> break

                is JsonFrame -> {
                    when (val message = incoming.packet.message) {

                        is Request -> {
                            scope.launch {
                                dispatcher.dispatch(
                                    message,
                                    connection
                                )
                            }
                        }

                        else -> { }
                    }
                }

                is BinaryFrame -> {
                    // Android не принимает бинарные кадры
                }
            }
        }
    }
}
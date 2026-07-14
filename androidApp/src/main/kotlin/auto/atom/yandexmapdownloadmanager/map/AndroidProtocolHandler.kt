package auto.atom.yandexmapdownloadmanager.map

import auto.atom.yandexmapdownloadmanager.protocol.Command
import auto.atom.yandexmapdownloadmanager.protocol.HelloRequest
import auto.atom.yandexmapdownloadmanager.protocol.HelloResponse
import auto.atom.yandexmapdownloadmanager.protocol.Packet
import auto.atom.yandexmapdownloadmanager.protocol.Progress
import auto.atom.yandexmapdownloadmanager.protocol.Protocol
import auto.atom.yandexmapdownloadmanager.protocol.Request
import auto.atom.yandexmapdownloadmanager.protocol.Response
import auto.atom.yandexmapdownloadmanager.protocol.Status
import auto.atom.yandexmapdownloadmanager.protocol.map.RegionPayload
import auto.atom.yandexmapdownloadmanager.protocol.map.RegionsPayload
import auto.atom.yandexmapdownloadmanager.transport.Connection
import auto.atom.yandexmapdownloadmanager.transport.ProtocolJson
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement

/**
 * Обрабатывает входящие команды Desktop.
 */

class AndroidProtocolHandler(
    private val connection: Connection,
    private val offlineYandexMapsManager: OfflineYandexMapsManager
) {

    suspend fun run() {

        while (connection.isConnected.value) {

            val packet = connection.receive() ?: break

            if (packet.version != Protocol.PROTOCOL_VERSION) {

                connection.send(
                    Packet(
                        message = Response(
                            id = "",
                            command = Command.PING,
                            status = Status.UNSUPPORTED_PROTOCOL
                        )
                    )
                )

                continue
            }

            when (val message = packet.message) {
                is Request -> processRequest(message)

                is Response -> {
                    // Android не ожидает Response
                }

                is Progress -> {
                    // Android не ожидает Progress
                }

                is HelloRequest -> {
                    // Android не ожидает запроса рукопожатия
                }

                is HelloResponse -> {
                    // TODO
                }
            }
        }
    }

    private suspend fun processRequest(
        request: Request
    ) {

        when (request.command) {

            Command.GET_REGIONS -> {

                offlineYandexMapsManager.loadRegions().let { regions ->
                    connection.send(
                        Packet(
                            message = Response(
                                id = request.id,
                                command = request.command,
                                status = Status.OK,
                                payload = ProtocolJson.encodeToJsonElement(
                                    RegionsPayload(regions.toOfflineRegion())
                                )
                            )
                        )
                    )
                }
            }

            Command.DOWNLOAD_REGION -> {

                val payload =
                    ProtocolJson.decodeFromJsonElement<RegionPayload>(
                        request.payload!!
                    )

                offlineYandexMapsManager.download(payload.regionId)

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

            Command.PAUSE_REGION_DOWNLOAD -> {
                val payload =
                    ProtocolJson.decodeFromJsonElement<RegionPayload>(
                        request.payload!!
                    )

                offlineYandexMapsManager.pause(payload.regionId)

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

            Command.RESUME_REGION_DOWNLOAD -> {
                val payload =
                    ProtocolJson.decodeFromJsonElement<RegionPayload>(
                        request.payload!!
                    )

                offlineYandexMapsManager.resume(payload.regionId)

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

            Command.CANCEL_REGION_DOWNLOAD -> {
                val payload =
                    ProtocolJson.decodeFromJsonElement<RegionPayload>(
                        request.payload!!
                    )

                offlineYandexMapsManager.cancel(payload.regionId)

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

            Command.DELETE_REGION -> {

                val payload =
                    ProtocolJson.decodeFromJsonElement<RegionPayload>(
                        request.payload!!
                    )

                offlineYandexMapsManager.remove(payload.regionId)

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

            else -> {

                connection.send(
                    Packet(
                        message = Response(
                            id = request.id,
                            command = request.command,
                            status = Status.NOT_IMPLEMENTED
                        )
                    )
                )
            }
        }
    }
}


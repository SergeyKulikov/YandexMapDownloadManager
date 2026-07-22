package auto.atom.yandexmapdownloadmanager.commands

import auto.atom.yandexmapdownloadmanager.dispatcher.CommandHandler
import auto.atom.yandexmapdownloadmanager.map.OfflineYandexMapsManager
import auto.atom.yandexmapdownloadmanager.protocol.model.Packet
import auto.atom.yandexmapdownloadmanager.protocol.model.Request
import auto.atom.yandexmapdownloadmanager.protocol.model.Response
import auto.atom.yandexmapdownloadmanager.protocol.model.Status
import auto.atom.yandexmapdownloadmanager.protocol.model.CachePathPayload
import auto.atom.yandexmapdownloadmanager.transport.Connection
import auto.atom.yandexmapdownloadmanager.transport.ProtocolJson
import kotlinx.serialization.json.encodeToJsonElement

/**
 * Возвращает путь к каталогу офлайн-карт.
 */
class GetCachePathHandler(
    private val offlineYandexMapsManager: OfflineYandexMapsManager
) : CommandHandler {

    override suspend fun execute(
        request: Request,
        connection: Connection
    ) {

        try {

            offlineYandexMapsManager.requestCachePath { path ->

                connection.sendAsync(
                    Packet(
                        message = Response(
                            id = request.id,
                            command = request.command,
                            status = Status.OK,
                            payload = ProtocolJson.encodeToJsonElement(
                                CachePathPayload(
                                    path = path
                                )
                            )
                        )
                    )
                )
            }

        } catch (ex: Exception) {

            ex.printStackTrace()

            connection.sendAsync(
                Packet(
                    message = Response(
                        id = request.id,
                        command = request.command,
                        status = Status.ERROR
                    )
                )
            )
        }
    }
}
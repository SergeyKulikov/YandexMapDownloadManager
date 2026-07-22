package auto.atom.yandexmapdownloadmanager.commands

import android.util.Log
import auto.atom.yandexmapdownloadmanager.dispatcher.CommandHandler
import auto.atom.yandexmapdownloadmanager.map.OfflineYandexMapsManager
import auto.atom.yandexmapdownloadmanager.model.FileTransferState
import auto.atom.yandexmapdownloadmanager.protocol.model.Packet
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionFileChunkPayload
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionPayload
import auto.atom.yandexmapdownloadmanager.protocol.model.Request
import auto.atom.yandexmapdownloadmanager.protocol.model.Response
import auto.atom.yandexmapdownloadmanager.protocol.model.Status
import auto.atom.yandexmapdownloadmanager.transport.Connection
import auto.atom.yandexmapdownloadmanager.transport.ProtocolJson
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import java.io.File
import java.io.FileInputStream

/**
 * Передает файл региона чанками.
 */
class GetRegionFileHandler(
    private val offlineYandexMapsManager: OfflineYandexMapsManager
) : CommandHandler {

    companion object {
        private const val CHUNK_SIZE = 256 * 1024
    }

    override suspend fun execute(
        request: Request,
        connection: Connection
    ) {
        val payload = ProtocolJson.decodeFromJsonElement<RegionPayload>(
            request.payload!!
        )

        try {
            val cachePath = offlineYandexMapsManager.getCachePath()

            val regionDir = File(
                cachePath,
                payload.regionId.toString()
            )

            require(regionDir.exists() && regionDir.isDirectory) {
                "Region directory not found: ${regionDir.absolutePath}"
            }

            regionDir.listFiles()
                ?.filter { it.isFile }
                ?.sortedBy { it.name }
                ?.forEach { file ->
                    sendFile(
                        request = request,
                        connection = connection,
                        regionId = payload.regionId,
                        file = file
                    )
                }

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
                "Region ${payload.regionId} sent"
            )

        } catch (e: Exception) {

            Log.e(
                "PROTO",
                "Send region files failed",
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

    private suspend fun sendFile(
        request: Request,
        connection: Connection,
        regionId: Int,
        file: File
    ) {
        Log.d(
            "PROTO",
            "Send file: ${file.absolutePath}"
        )

        val totalSize = file.length()

        connection.sendAsync(
            Packet(
                message = Response(
                    id = request.id,
                    command = request.command,
                    status = Status.OK,
                    payload = ProtocolJson.encodeToJsonElement(
                        RegionFileChunkPayload(
                            regionId = regionId,
                            fileName = file.name,
                            state = FileTransferState.START,
                            totalSize = totalSize
                        )
                    )
                )
            )
        )

        val buffer = ByteArray(CHUNK_SIZE)
        var offset = 0L

        FileInputStream(file).use { input ->
            while (true) {
                val read = input.read(buffer)
                if (read == -1) {
                    break
                }

                connection.sendAsync(
                    Packet(
                        message = Response(
                            id = request.id,
                            command = request.command,
                            status = Status.OK,
                            payload = ProtocolJson.encodeToJsonElement(
                                RegionFileChunkPayload(
                                    regionId = regionId,
                                    fileName = file.name,
                                    state = FileTransferState.CHUNK,
                                    offset = offset,
                                    totalSize = totalSize,
                                    bytes = buffer.copyOf(read)
                                )
                            )
                        )
                    )
                )

                offset += read
            }
        }

        connection.sendAsync(
            Packet(
                message = Response(
                    id = request.id,
                    command = request.command,
                    status = Status.OK,
                    payload = ProtocolJson.encodeToJsonElement(
                        RegionFileChunkPayload(
                            regionId = regionId,
                            fileName = file.name,
                            state = FileTransferState.END
                        )
                    )
                )
            )
        )
    }
}
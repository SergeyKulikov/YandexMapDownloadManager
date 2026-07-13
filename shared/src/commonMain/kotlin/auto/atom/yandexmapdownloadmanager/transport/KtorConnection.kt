package auto.atom.yandexmapdownloadmanager.transport

import auto.atom.yandexmapdownloadmanager.protocol.Message
import io.ktor.network.sockets.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Реализация [Connection] поверх Ktor TCP-соединения.
 *
 * Использует [FrameIO] и [ProtocolJson].
 * Не содержит бизнес-логики.
 */
internal class KtorConnection(
    private val socket: Socket,
    private val frameIO: FrameIO
) : Connection {

    override suspend fun send(message: Message) {
        val jsonString = ProtocolJson.encodeToString(Message.serializer(), message)
        val payload = jsonString.encodeToByteArray()
        frameIO.writeFrame(payload)
    }

    override suspend fun receive(): Message {
        val payload = frameIO.readFrame()
        val jsonString = payload.decodeToString()
        return ProtocolJson.decodeFromString(Message.serializer(), jsonString)
    }

    /**
     * Закрывает соединение и все связанные ресурсы.
     */
    override suspend fun close() {
        withContext(Dispatchers.IO) {

            runCatching {
                frameIO.close()
            }

            runCatching {
                socket.close()
            }.onFailure {
                // Log.w("TRANSPORT", "Socket close failed", it)
            }
        }
    }
}
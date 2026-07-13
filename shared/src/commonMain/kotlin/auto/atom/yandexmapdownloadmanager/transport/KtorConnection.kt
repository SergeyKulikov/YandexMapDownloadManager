package auto.atom.yandexmapdownloadmanager.transport

import auto.atom.yandexmapdownloadmanager.protocol.Message
import io.ktor.network.sockets.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    private val _isConnected = MutableStateFlow(true)
    override val isConnected: StateFlow<Boolean> = _isConnected

    override suspend fun send(message: Message): Boolean {
        if (!_isConnected.value) {
            return false
        }

        return try {
            val json = ProtocolJson.encodeToString(
                Message.serializer(),
                message
            )

            frameIO.writeFrame(json.encodeToByteArray())

            true
        } catch (_: Exception) {
            _isConnected.value = false

            runCatching { frameIO.close() }
            runCatching { socket.close() }
            false
        }
    }

    override suspend fun receive(): Message? {

        if (!_isConnected.value) {
            return null
        }

        return try {

            val payload = frameIO.readFrame()

            ProtocolJson.decodeFromString(
                Message.serializer(),
                payload.decodeToString()
            )

        } catch (_: Exception) {
            _isConnected.value = false

            runCatching { frameIO.close() }
            runCatching { socket.close() }
            null
        }
    }

    /**
     * Закрывает соединение и все связанные ресурсы.
     */
    override suspend fun close() {
        withContext(Dispatchers.IO) {
            _isConnected.value = false

            runCatching { frameIO.close() }
            runCatching { socket.close() }
        }
    }

}
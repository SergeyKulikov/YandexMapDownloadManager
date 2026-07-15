package auto.atom.yandexmapdownloadmanager.transport

import auto.atom.yandexmapdownloadmanager.protocol.Packet
import io.ktor.network.sockets.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Реализация [Connection] поверх TCP-соединения Ktor.
 *
 * Отвечает исключительно за транспортный уровень:
 * - сериализацию и десериализацию [Packet];
 * - запись и чтение кадров через [FrameIO];
 * - отслеживание состояния соединения;
 * - освобождение сетевых ресурсов.
 *
 * Класс не содержит логики протокола и не анализирует содержимое
 * передаваемых сообщений.
 */
internal class KtorConnection(
    private val socket: Socket,
    private val frameIO: FrameIO
) : Connection {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun sendAsync(packet: Packet) {
        scope.launch {
            send(packet)
        }
    }

    private val _isConnected = MutableStateFlow(true)

    /**
     * Текущее состояние соединения.
     */
    override val isConnected: StateFlow<Boolean> = _isConnected

    /**
     * Отправляет пакет удаленной стороне.
     *
     * @param packet пакет протокола для передачи.
     * @return true, если пакет успешно отправлен.
     */
    override suspend fun send(packet: Packet): Boolean {

        if (!_isConnected.value) {
            return false
        }

        return try {

            val json = ProtocolJson.encodeToString(
                Packet.serializer(),
                packet
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

    /**
     * Ожидает получение следующего пакета.
     *
     * @return полученный пакет или null,
     * если соединение было закрыто.
     */
    override suspend fun receive(): Packet? {

        if (!_isConnected.value) {
            return null
        }

        return try {

            val payload = frameIO.readFrame()

            ProtocolJson.decodeFromString(
                Packet.serializer(),
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
     * Закрывает соединение и освобождает связанные ресурсы.
     */
    override suspend fun close() {

        withContext(Dispatchers.IO) {

            _isConnected.value = false

            runCatching { frameIO.close() }
            runCatching { socket.close() }
        }
    }
}
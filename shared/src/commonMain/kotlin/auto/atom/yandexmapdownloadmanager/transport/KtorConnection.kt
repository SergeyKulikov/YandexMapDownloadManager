package auto.atom.yandexmapdownloadmanager.transport

import auto.atom.yandexmapdownloadmanager.protocol.model.BinaryFileFrame
import auto.atom.yandexmapdownloadmanager.protocol.model.Packet
import io.ktor.network.sockets.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CancellationException

/**
 * Реализация [Connection] поверх TCP-соединения Ktor.
 *
 * Отвечает исключительно за транспортный уровень:
 * - сериализацию и десериализацию [Packet];
 * - запись и чтение кадров через [FrameIO];
 * - отслеживание состояния соединения;
 * - освобождение сетевых ресурсов.
 *
 * Для асинхронной отправки используется очередь исходящих
 * сообщений и единственная корутина-писатель. Такой подход
 * исключает одновременную запись в сокет, уменьшает количество
 * создаваемых корутин при частой отправке сообщений (например,
 * прогресса загрузки) и обеспечивает последовательную передачу
 * всех пакетов.
 *
 * Класс не содержит логики протокола и не анализирует
 * содержимое передаваемых сообщений.
 */
internal class KtorConnection(
    private val socket: Socket,
    private val frameIO: FrameIO
) : Connection {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Очередь исходящих сообщений.
     */
    // private val sendChannel = Channel<TransportFrame>(Channel.UNLIMITED)
    private val sendChannel = Channel<TransportFrame>(16)

    /**
     * Текущее состояние соединения.
     */
    private val _isConnected = MutableStateFlow(true)

    override val isConnected: StateFlow<Boolean> = _isConnected

    init {
        scope.launch {

            println(">>> Writer started")

            for (frame in sendChannel) {

                when (frame) {

                    is JsonTransportFrame ->
                        frameIO.writeFrame(frame)

                    is BinaryTransportFrame ->
                        frameIO.writeFrame(frame)
                }
            }

            println(">>> Writer stopped")
        }
    }

    /**
     * Асинхронно помещает пакет в очередь на отправку.
     */
    override fun sendAsync(packet: Packet) {
        scope.launch {
            sendChannel.send(
                JsonTransportFrame(
                    ProtocolJson.encodeToString(
                        Packet.serializer(),
                        packet
                    ).encodeToByteArray()
                )
            )
        }
    }

    override suspend fun sendBinary(
        frame: BinaryFileFrame
    ) {
        if (!_isConnected.value) {
            return
        }

        sendChannel.send(
            BinaryTransportFrame(frame)
        )
    }

    /**
     * Отправляет пакет удаленной стороне.
     *
     * Метод вызывается только одной корутиной,
     * поэтому запись в сокет всегда последовательная.
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

            frameIO.writeFrame(
                JsonTransportFrame(
                    json.encodeToByteArray()
                )
            )

            true

        } catch (e: java.io.EOFException) {

            println(">>> Connection closed")

            _isConnected.value = false
            sendChannel.close()

            runCatching { frameIO.close() }
            runCatching { socket.close() }

            false

        } catch (e: Exception) {

            println(">>> SEND FAILED")
            e.printStackTrace()

            _isConnected.value = false
            sendChannel.close()

            runCatching { frameIO.close() }
            runCatching { socket.close() }

            false
        }
    }
    /**
     * Ожидает получение следующего кадра.
     *
     * @return полученный кадр или null,
     * если соединение закрыто.
     */
    override suspend fun receive(): IncomingFrame? {

        if (!_isConnected.value) return null

        return try {

            when (val frame = frameIO.readFrame()) {

                is JsonTransportFrame ->
                    JsonFrame(
                        ProtocolJson.decodeFromString(
                            Packet.serializer(),
                            frame.payload.decodeToString()
                        )
                    )

                is BinaryTransportFrame ->
                    BinaryFrame(frame.frame)
            }

        } catch (e: CancellationException) {
            throw e

        } catch (e: java.io.EOFException) {

            _isConnected.value = false
            sendChannel.close()

            runCatching { frameIO.close() }
            runCatching { socket.close() }

            null

        } catch (e: Exception) {

            e.printStackTrace()

            _isConnected.value = false
            sendChannel.close()

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

            sendChannel.close()

            runCatching { frameIO.close() }
            runCatching { socket.close() }
        }
    }


}
package auto.atom.yandexmapdownloadmanager.transport

import auto.atom.yandexmapdownloadmanager.protocol.Packet
import io.ktor.network.sockets.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
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
    private val sendChannel = Channel<Packet>(Channel.UNLIMITED)

    /**
     * Текущее состояние соединения.
     */
    private val _isConnected = MutableStateFlow(true)

    override val isConnected: StateFlow<Boolean> = _isConnected

    init {
        scope.launch {

            println(">>> Writer started")

            for (packet in sendChannel) {

                println(">>> Writer got ${packet.message::class.simpleName}")

                if (!send(packet)) break
            }

            println(">>> Writer stopped")
        }
    }

    /**
     * Асинхронно помещает пакет в очередь на отправку.
     */
    override fun sendAsync(packet: Packet) {

        println(">>> QUEUE ${packet.message::class.simpleName}")

        val result = sendChannel.trySend(packet)

        if (result.isFailure) {
            println(">>> QUEUE FAILED ${result.exceptionOrNull()}")
        }
    }

    /**
     * Отправляет пакет удаленной стороне.
     *
     * Метод вызывается только одной корутиной,
     * поэтому запись в сокет всегда последовательная.
     */
    override suspend fun send(message: Packet): Boolean {

        if (!_isConnected.value) return false

        return try {

            val json = ProtocolJson.encodeToString(
                Packet.serializer(),
                message
            )

            println(">>> SEND ${message::class.simpleName}")
            println(json)

            frameIO.writeFrame(json.encodeToByteArray())

            println(">>> WRITE OK")

            true

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
     * Ожидает получение следующего пакета.
     *
     * @return полученный пакет или null,
     * если соединение закрыто.
     */
    override suspend fun receive(): Packet? {

        if (!_isConnected.value) return null

        return try {

            val payload = frameIO.readFrame()

            println("<<< RECV")
            println(payload.decodeToString())

            val packet = ProtocolJson.decodeFromString(
                Packet.serializer(),
                payload.decodeToString()
            )

            println("<<< RECEIVE ${packet.message::class.simpleName}")

            return packet

        } catch (e: Exception) {

            println("<<< RECEIVE FAILED")
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
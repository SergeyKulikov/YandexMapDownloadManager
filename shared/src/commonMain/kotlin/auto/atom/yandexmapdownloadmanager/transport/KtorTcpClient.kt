package auto.atom.yandexmapdownloadmanager.transport

import auto.atom.yandexmapdownloadmanager.protocol.model.HelloRequest
import auto.atom.yandexmapdownloadmanager.protocol.model.HelloResponse
import auto.atom.yandexmapdownloadmanager.protocol.model.Packet
import auto.atom.yandexmapdownloadmanager.protocol.model.Protocol.APPLICATION_NAME
import auto.atom.yandexmapdownloadmanager.protocol.model.Protocol.PROTOCOL_VERSION
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * TCP-клиент Android.
 *
 * Отвечает за:
 * - установку TCP-соединения;
 * - выполнение рукопожатия (handshake);
 * - создание транспортного [Connection];
 * - освобождение сетевых ресурсов.
 *
 * Класс не содержит логики протокола обмена командами.
 */
class KtorTcpClient {

    private var selectorManager: SelectorManager? = null
    private var socket: Socket? = null
    private var connection: Connection? = null

    /**
     * Текущее активное соединение.
     */
    val currentConnection: Connection?
        get() = connection

    /**
     * Подключается к Desktop-серверу.
     *
     * После установки TCP-соединения выполняется рукопожатие
     * с проверкой совместимости протокола.
     *
     * @param host адрес Desktop.
     * @param port TCP-порт.
     *
     * @return установленное соединение.
     */
    suspend fun connect(
        host: String,
        port: Int
    ): Connection = withContext(Dispatchers.IO) {

        close()

        try {

            selectorManager = SelectorManager(Dispatchers.IO)

            socket = aSocket(selectorManager!!)
                .tcp()
                .connect(host, port)

            val frameIO = FrameIO(
                socket!!.openReadChannel(),
                socket!!.openWriteChannel(autoFlush = true)
            )

            val newConnection = KtorConnection(
                socket = socket!!,
                frameIO = frameIO
            )

            handshake(newConnection)

            connection = newConnection

            newConnection

        } catch (e: Exception) {

            close()

            throw e
        }
    }

    /**
     * Выполняет рукопожатие с Desktop.
     */
    private suspend fun handshake(
        connection: Connection
    ) {

        connection.send(
            Packet(
                message = HelloRequest(
                    application = APPLICATION_NAME
                )
            )
        )

        val response = connection.receive()

        require(response?.message is HelloResponse) {
            "Invalid handshake response"
        }

        require(response.protocolVersion == PROTOCOL_VERSION) {
            "Unsupported protocol version: ${response.protocolVersion}"
        }

        require(response.message.application == APPLICATION_NAME) {
            "Unknown server: ${response.message.application}"
        }
    }

    /**
     * Закрывает соединение и освобождает все сетевые ресурсы.
     *
     * Метод безопасно вызывать многократно.
     */
    suspend fun close() = withContext(Dispatchers.IO) {

        runCatching {
            connection?.close()
        }

        runCatching {
            selectorManager?.close()
        }

        runCatching {
            socket?.close()
        }

        connection = null
        socket = null
        selectorManager = null
    }

    /**
     * Возвращает состояние соединения.
     */
    fun isConnected(): Boolean =
        connection?.isConnected?.value == true
}
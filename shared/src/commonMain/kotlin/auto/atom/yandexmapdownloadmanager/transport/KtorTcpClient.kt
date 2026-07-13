package auto.atom.yandexmapdownloadmanager.transport

import auto.atom.yandexmapdownloadmanager.protocol.HelloRequest
import auto.atom.yandexmapdownloadmanager.protocol.HelloResponse
import auto.atom.yandexmapdownloadmanager.protocol.Protocol.APPLICATION_NAME
import auto.atom.yandexmapdownloadmanager.protocol.Protocol.PROTOCOL_VERSION
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * TCP-клиент для Android.
 *
 * Подключается к Desktop-серверу по протоколу TCP.
 */
class KtorTcpClient {

    private var selectorManager: SelectorManager? = null
    private var socket: Socket? = null
    private var connection: Connection? = null

    /**
     * Текущее соединение.
     */
    val currentConnection: Connection?
        get() = connection

    /**
     * Устанавливает соединение с сервером.
     *
     * @param host хост или IP-адрес сервера
     * @param port порт сервера
     * @return установленное соединение
     */
    suspend fun connect(
        host: String,
        port: Int
    ): Connection = withContext(Dispatchers.IO) {

        // если старое соединение осталось — корректно закрываем
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


    private suspend fun handshake(connection: Connection) {

        connection.send(
            HelloRequest(
                protocolVersion = PROTOCOL_VERSION,
                application = APPLICATION_NAME
            )
        )

        val response = connection.receive()

        require(response is HelloResponse) {
            "Invalid handshake response"
        }

        require(response.protocolVersion == PROTOCOL_VERSION) {
            "Unsupported protocol version: ${response.protocolVersion}"
        }

        require(response.application == APPLICATION_NAME) {
            "Unknown server: ${response.application}"
        }
    }

    /**
     * Закрывает соединение и освобождает все ресурсы.
     *
     * Безопасно вызывать перед уничтожением объекта.
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
     * Проверяет, подключен ли клиент.
     */
    fun isConnected(): Boolean =
        connection?.isConnected?.value == true

}
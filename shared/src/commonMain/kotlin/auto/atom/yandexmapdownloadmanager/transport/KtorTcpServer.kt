package auto.atom.yandexmapdownloadmanager.transport

import auto.atom.yandexmapdownloadmanager.protocol.HelloRequest
import auto.atom.yandexmapdownloadmanager.protocol.HelloResponse
import auto.atom.yandexmapdownloadmanager.protocol.Packet
import auto.atom.yandexmapdownloadmanager.protocol.Protocol
import auto.atom.yandexmapdownloadmanager.protocol.Protocol.APPLICATION_NAME
import auto.atom.yandexmapdownloadmanager.protocol.Protocol.PROTOCOL_VERSION
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.ServerSocket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * TCP-сервер (Desktop JVM).
 */
class KtorTcpServer(
    private val host: String = "0.0.0.0",
    private val port: Int = Protocol.PORT
) {
    private var selectorManager: SelectorManager? = null
    private var serverSocket: ServerSocket? = null
    private var currentConnection: Connection? = null
    suspend fun start() = withContext(Dispatchers.IO) {
        check(serverSocket == null) { "Server already started" }

        selectorManager = SelectorManager(Dispatchers.IO)

        serverSocket = aSocket(selectorManager!!)
            .tcp()
            .bind(host, port)
    }

    suspend fun waitForConnection(): Connection {

        while (true) {

            val connection = withContext(Dispatchers.IO) {

                val socket = serverSocket?.accept()
                    ?: error("Server is not started")

                val frameIO = FrameIO(
                    socket.openReadChannel(),
                    socket.openWriteChannel(autoFlush = true)
                )

                KtorConnection(socket, frameIO)
            }

            if (handshake(connection)) {
                currentConnection = connection
                return connection
            }

            connection.close()
        }
    }

    private suspend fun handshake(connection: Connection): Boolean {

        val request = connection.receive()

        if (request?.message !is HelloRequest) {
            return false
        }

        if (request.message.protocolVersion != PROTOCOL_VERSION) {
            return false
        }

        if (request.message.application != APPLICATION_NAME) {
            return false
        }

        return connection.send(
            Packet(
                message =
                    HelloResponse(
                        protocolVersion = PROTOCOL_VERSION,
                        application = APPLICATION_NAME
                    )
            )
        )
    }

    /**
     * Останавливает сервер и закрывает все ресурсы.
     */
    suspend fun stop() {
        withContext(Dispatchers.IO) {

            runCatching {
                currentConnection?.close()
            }

            runCatching {
                serverSocket?.close()
            }

            runCatching {
                selectorManager?.close()
            }

            currentConnection = null
            serverSocket = null
            selectorManager = null
        }
    }
}
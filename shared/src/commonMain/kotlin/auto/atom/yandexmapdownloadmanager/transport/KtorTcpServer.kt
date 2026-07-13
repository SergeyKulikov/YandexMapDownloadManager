package auto.atom.yandexmapdownloadmanager.transport

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.ServerSocket
import io.ktor.network.sockets.Socket
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
    private val port: Int = 9000
) {
    private var selectorManager: SelectorManager? = null
    private var serverSocket: ServerSocket? = null
    private var currentConnection: KtorConnection? = null

    suspend fun start() {
        check(serverSocket == null) { "Server already started" }

        selectorManager = SelectorManager(Dispatchers.IO)
        serverSocket = aSocket(selectorManager!!)
            .tcp()
            .bind(host, port)
    }

    suspend fun waitForConnection(): Connection {
        val socket = serverSocket?.accept()
            ?: throw IllegalStateException("Server is not started. Call start() first.")

        val readChannel = socket.openReadChannel()
        val writeChannel = socket.openWriteChannel(autoFlush = true)

        val frameIO = FrameIO(readChannel, writeChannel)
        val connection = KtorConnection(socket, frameIO)
        currentConnection = connection
        return connection
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
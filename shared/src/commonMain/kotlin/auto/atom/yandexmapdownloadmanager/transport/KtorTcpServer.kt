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
    private val port: Int = 5555
) {
    private var selectorManager: SelectorManager? = null
    private var serverSocket: ServerSocket? = null
    private var currentConnection: KtorConnection? = null

    suspend fun start() = withContext(Dispatchers.IO) {
        check(serverSocket == null) { "Server already started" }

        selectorManager = SelectorManager(Dispatchers.IO)

        serverSocket = aSocket(selectorManager!!)
            .tcp()
            .bind(host, port)
    }

    suspend fun waitForConnection(): Connection =
        withContext(Dispatchers.IO) {

            val socket = serverSocket?.accept()
                ?: error("Server is not started")

            val frameIO = FrameIO(
                socket.openReadChannel(),
                socket.openWriteChannel(autoFlush = true)
            )

            KtorConnection(socket, frameIO).also {
                currentConnection = it
            }
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
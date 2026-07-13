package auto.atom.yandexmapdownloadmanager.transport

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.isClosed
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
    private var connection: KtorConnection? = null

    /**
     * Устанавливает соединение с сервером.
     *
     * @param host хост или IP-адрес сервера
     * @param port порт сервера
     * @return установленное соединение
     */
    suspend fun connect(host: String, port: Int): Connection =
        withContext(Dispatchers.IO) {

            check(socket == null) { "Client is already connected" }

            try {
                selectorManager = SelectorManager(Dispatchers.IO)

                socket = aSocket(selectorManager!!)
                    .tcp()
                    .connect(host, port)

                val frameIO = FrameIO(
                    socket!!.openReadChannel(),
                    socket!!.openWriteChannel(autoFlush = true)
                )

                connection = KtorConnection(socket!!, frameIO)
                connection!!

            } catch (e: Exception) {

                runCatching { connection?.close() }
                runCatching { socket?.close() }
                runCatching { selectorManager?.close() }

                connection = null
                socket = null
                selectorManager = null

                throw e
            }
        }

    /**
     * Закрывает соединение и освобождает все ресурсы.
     *
     * Безопасно вызывать перед уничтожением объекта.
     */
    suspend fun close() {
        withContext(Dispatchers.IO) {
            runCatching {
                connection?.close()
            }

            runCatching {
                selectorManager?.close()
            }

            connection = null
            socket = null
            selectorManager = null
        }
    }
    /**
     * Проверяет, активно ли соединение.
     */
    fun isConnected(): Boolean = socket?.isClosed == false
}
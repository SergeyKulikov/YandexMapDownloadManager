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
    suspend fun connect(host: String, port: Int): Connection {
        check(socket == null) { "Client is already connected" }

        selectorManager = SelectorManager(Dispatchers.IO)
        socket = aSocket(selectorManager!!)
            .tcp()
            .connect(host, port)

        val readChannel = socket!!.openReadChannel()
        val writeChannel = socket!!.openWriteChannel(autoFlush = true)

        val frameIO = FrameIO(readChannel, writeChannel)
        connection = KtorConnection(socket!!, frameIO)

        return connection!!
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
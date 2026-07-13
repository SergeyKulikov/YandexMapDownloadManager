package auto.atom.yandexmapdownloadmanager.transport

import auto.atom.yandexmapdownloadmanager.protocol.Message
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.DefaultWebSocketSession

/**
 * Реализация [Connection] поверх WebSocket-сессии Ktor.
 *
 * Класс инкапсулирует работу с WebSocket и предоставляет
 * простой интерфейс обмена сообщениями.
 *
 * Используется как Desktop, так и Android.
 *
 * Получение самой WebSocket-сессии выполняется
 * классами [KtorDesktopServer] и [KtorAndroidClient].
 */
internal class KtorConnection(
    private val session: DefaultWebSocketSession
) : Connection {

    override suspend fun send(message: Message) {
        val text = ProtocolJson.encodeToString(
            Message.serializer(),
            message
        )

        session.send(Frame.Text(text))
    }

    override suspend fun receive(): Message {

        while (true) {

            when (val frame = session.incoming.receive()) {

                is Frame.Text -> {
                    return ProtocolJson.decodeFromString(
                        Message.serializer(),
                        frame.readText()
                    )
                }

                is Frame.Close -> {
                    throw IllegalStateException("Соединение закрыто.")
                }

                else -> Unit
            }
        }
    }

    override suspend fun close() {
        session.close()
    }
}
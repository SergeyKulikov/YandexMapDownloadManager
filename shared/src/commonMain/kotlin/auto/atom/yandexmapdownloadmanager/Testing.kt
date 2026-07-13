package auto.atom.yandexmapdownloadmanager

import auto.atom.yandexmapdownloadmanager.command.CommandDispatcher
import auto.atom.yandexmapdownloadmanager.command.handlers.PingHandler
import auto.atom.yandexmapdownloadmanager.protocol.Command
import auto.atom.yandexmapdownloadmanager.protocol.Request
import auto.atom.yandexmapdownloadmanager.transport.KtorTcpServer

/**
 * Простейший тест транспортного уровня.
 *
 * Запускает TCP-сервер, ожидает подключения клиента
 * и передает входящие запросы в CommandDispatcher.
 */
object Testing {

    suspend fun execute() {

        val server = KtorTcpServer(port = 5555)

        val dispatcher = CommandDispatcher(
            mapOf(
                Command.PING to PingHandler()
            )
        )

        println("Starting server...")

        server.start()

        println("Server started on port 5555.")
        println("Waiting for client...")

        val connection = server.waitForConnection()

        println("Client connected.")

        try {

            while (true) {

                when (val message = connection.receive()) {

                    is Request -> {
                        println("Received request: ${message.command} (${message.id})")
                        dispatcher.dispatch(message, connection)
                    }

                    else -> {
                        println("Ignoring unexpected message: ${message::class.simpleName}")
                    }
                }
            }

        } finally {

            println("Stopping server...")

            connection.close()
            server.stop()

            println("Server stopped.")
        }
    }
}
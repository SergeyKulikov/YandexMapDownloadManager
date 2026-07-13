package auto.atom.yandexmapdownloadmanager.command

import auto.atom.yandexmapdownloadmanager.protocol.Command
import auto.atom.yandexmapdownloadmanager.protocol.Request
import auto.atom.yandexmapdownloadmanager.protocol.Response
import auto.atom.yandexmapdownloadmanager.protocol.Status
import auto.atom.yandexmapdownloadmanager.transport.Connection

/**
 * Подсистема обработки команд протокола обмена между Desktop и Android.
 *
 * После установки TCP-соединения все входящие запросы проходят
 * через диспетчер команд.
 *
 * Общая схема обработки:
 *
 *                         Request
 *                            │
 *                            ▼
 *                   CommandDispatcher
 *                            │
 *      ┌─────────────────────┼──────────────────────┐
 *      ▼                     ▼                      ▼
 *  PingHandler       DeviceInfoHandler     DownloadMapHandler
 *                                                 │
 *                                                 ▼
 *                                         ExportMapHandler
 *                                                 │
 *                                                 ▼
 *                                         DeleteMapHandler
 *                                                 │
 *                                                 ▼
 *                                          CancelHandler
 *
 * Каждый обработчик отвечает только за одну команду.
 *
 * Dispatcher не содержит бизнес-логики.
 * Его единственная задача — определить обработчик по типу команды
 * и передать ему управление.
 *
 * Во время выполнения обработчик самостоятельно отправляет сообщения
 * удаленной стороне через Connection.
 *
 * Пример жизненного цикла команды:
 *
 * Desktop
 *    │
 *    ▼
 * Request(DOWNLOAD_MAP)
 *    │
 *    ▼
 * CommandDispatcher
 *    │
 *    ▼
 * DownloadMapHandler
 *    │
 *    ├────────► Progress(5%)
 *    ├────────► Progress(17%)
 *    ├────────► Progress(48%)
 *    ├────────► Progress(91%)
 *    └────────► Response(Status.OK)
 *
 * Такой подход позволяет:
 *
 * • выполнять длительные операции;
 * • отправлять произвольное количество Progress;
 * • уведомлять о промежуточных состояниях;
 * • возвращать итоговый Response;
 * • легко добавлять новые команды без изменения Dispatcher.
 *
 * Dispatcher не содержит бизнес-логики.
 * После получения [Request] он определяет обработчик,
 * соответствующий команде, и передает ему управление.
 *
 * Общая схема работы:
 *
 *                         Request
 *                            │
 *                            ▼
 *                   CommandDispatcher
 *                            │
 *      ┌─────────────────────┼──────────────────────┐
 *      ▼                     ▼                      ▼
 *  PingHandler       DeviceInfoHandler     DownloadMapHandler
 */
class CommandDispatcher(
    handlers: Map<Command, CommandHandler>
) {

    private val handlers = handlers.toMap()

    /**
     * Передает запрос соответствующему обработчику.
     *
     * @throws IllegalArgumentException если обработчик команды не зарегистрирован.
     */
    suspend fun dispatch(
        request: Request,
        connection: Connection
    ) {
        val handler = handlers[request.command]

        if (handler == null) {
            connection.send(
                Response(
                    id = request.id,
                    status = Status.ERROR,
                    error = "Unsupported command: ${request.command}"
                )
            )
            return
        }

        try {
            handler.execute(request, connection)
        } catch (e: Exception) {
            connection.send(
                Response(
                    id = request.id,
                    status = Status.ERROR,
                    error = e.message ?: "Internal error"
                )
            )
        }
    }
}
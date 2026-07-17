package auto.atom.yandexmapdownloadmanager.dispatcher

import auto.atom.yandexmapdownloadmanager.protocol.model.Request
import auto.atom.yandexmapdownloadmanager.transport.Connection

/**
 * Базовый интерфейс обработчика команды.
 *
 * Каждый обработчик отвечает только за одну команду протокола.
 *
 * Примеры:
 *
 * PING          → PingHandler
 *
 * DEVICE_INFO   → DeviceInfoHandler
 *
 * DOWNLOAD_MAP  → DownloadMapHandler
 *
 * EXPORT_MAP    → ExportMapHandler
 *
 * DELETE_MAP    → DeleteMapHandler
 *
 * CANCEL        → CancelHandler
 *
 * Во время выполнения обработчик может отправить удаленной стороне
 * любое количество сообщений через Connection.
 *
 * Обычно последовательность выглядит следующим образом:
 *
 * Request
 *    │
 *    ▼
 * Progress
 *    │
 *    ▼
 * Progress
 *    │
 *    ▼
 * Response
 *
 * Dispatcher отвечает только за выбор обработчика.
 * Вся бизнес-логика располагается внутри реализации CommandHandler.
 */
interface CommandHandler {

    /**
     * Выполняет обработку полученной команды.
     *
     * @param request полученный запрос.
     * @param connection соединение с удаленной стороной.
     */
    suspend fun execute(
        request: Request,
        connection: Connection
    )
}
package auto.atom.yandexmapdownloadmanager.command.handlers

/**
 * Выполняет загрузку карты Яндекса.
 *
 * Команда может выполняться продолжительное время.
 * Во время работы обработчик отправляет сообщения Progress,
 * позволяя Desktop отображать текущий прогресс загрузки.
 *
 * Схема работы:
 *
 * Desktop
 *    │
 *    ▼
 * Request(DOWNLOAD_MAP)
 *    │
 *    ▼
 * DownloadMapHandler
 *    │
 *    ├────────► Progress(3%)
 *    ├────────► Progress(14%)
 *    ├────────► Progress(42%)
 *    ├────────► Progress(76%)
 *    ├────────► Progress(100%)
 *    ▼
 * Response(Status.OK)
 */
class DownloadMapHandler {
}
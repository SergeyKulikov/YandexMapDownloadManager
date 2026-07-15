package auto.atom.yandexmapdownloadmanager.commands

/**
 * Выполняет экспорт ранее загруженной карты.
 *
 * После подготовки данных инициирует передачу карты
 * удаленной стороне.
 *
 * Во время выполнения может отправлять Progress.
 *
 * Схема работы:
 *
 * Desktop
 *    │
 *    ▼
 * Request(EXPORT_MAP)
 *    │
 *    ▼
 * ExportMapHandler
 *    │
 *    ├────────► Progress(...)
 *    ├────────► Progress(...)
 *    └────────► Response(Status.OK)
 */
class ExportMapHandler {
}
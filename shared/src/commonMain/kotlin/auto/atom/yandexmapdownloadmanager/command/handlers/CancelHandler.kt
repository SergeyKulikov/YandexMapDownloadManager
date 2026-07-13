package auto.atom.yandexmapdownloadmanager.command.handlers

/**
 * Отменяет выполняющуюся операцию.
 *
 * Используется для прерывания длительных операций,
 * например загрузки или экспорта карты.
 *
 * После получения команды обработчик уведомляет
 * выполняющуюся задачу о необходимости завершения.
 *
 * Схема работы:
 *
 * Desktop
 *    │
 *    ▼
 * Request(CANCEL)
 *    │
 *    ▼
 * CancelHandler
 *    │
 *    ▼
 * Response(Status.CANCELLED)
 */
class CancelHandler {
}
package auto.atom.yandexmapdownloadmanager.command.handlers

/**
 * Выполняет удаление карты.
 *
 * После завершения операции отправляет итоговый Response.
 *
 * Схема работы:
 *
 * Desktop
 *    │
 *    ▼
 * Request(DELETE_MAP)
 *    │
 *    ▼
 * DeleteMapHandler
 *    │
 *    ▼
 * Response(Status.OK)
 */
class RemoveRegionRequest {
    val regionId: Int = 0
}
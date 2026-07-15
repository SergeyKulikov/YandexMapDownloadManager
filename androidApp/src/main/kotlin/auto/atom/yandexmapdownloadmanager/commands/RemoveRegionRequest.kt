package auto.atom.yandexmapdownloadmanager.commands

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
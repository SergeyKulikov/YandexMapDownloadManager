package auto.atom.yandexmapdownloadmanager.model

import auto.atom.yandexmapdownloadmanager.ui.OfflineRegionState

/**
 * Формирует список регионов, требующих загрузки или обновления.
 */
fun List<OfflineRegion>.buildUpdateQueue(): List<RegionUpdateTask> {

    val result = mutableListOf<RegionUpdateTask>()

    forEach {
        it.collectUpdateTasks(result)
    }

    return result
}

private fun OfflineRegion.collectUpdateTasks(
    result: MutableList<RegionUpdateTask>
) {

    when (state) {

        OfflineRegionState.AVAILABLE -> {
            result += RegionUpdateTask(
                region = this,
                reason = UpdateReason.NOT_DOWNLOADED
            )
        }

        OfflineRegionState.OUTDATED,
        OfflineRegionState.NEED_UPDATE -> {
            result += RegionUpdateTask(
                region = this,
                reason = UpdateReason.NEW_VERSION_AVAILABLE
            )
        }

        else -> {
            // Ничего делать не нужно.
        }
    }

    children.forEach {
        it.collectUpdateTasks(result)
    }
}
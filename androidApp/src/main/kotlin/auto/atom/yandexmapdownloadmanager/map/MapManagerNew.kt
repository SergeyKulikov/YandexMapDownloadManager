package auto.atom.yandexmapdownloadmanager.map

/**
class MapManagerNew {
    companion object {
        private const val RUSSIA_GEO_ID = 225
    }

    // Слушатель по новой документации Яндекса
    private val regionListener = object : OfflineCacheManager.RegionListener {
        override fun onRegionStateChanged(regionId: Int) {
            val state = offlineCacheManager.getState(regionId)
            // Здесь обрабатываем статусы (например, RegionState.COMPLETED, RegionState.DOWNLOADING)
            when (state) {
                RegionState.COMPLETED -> { /* Регион успешно скачан */ }
                RegionState.DOWNLOADING -> { /* Началось скачивание */ }
                else -> {}
            }
        }

        override fun onRegionProgress(regionId: Int) {
            val progress = offlineCacheManager.getProgress(regionId)
            // Здесь обновляем UI ProgressBar для конкретного региона (progress от 0.0 до 1.0)
        }
    }

    init {
        // Обязательно подписываемся на изменения состояний
        offlineCacheManager.addRegionListener(regionListener)
    }

    /**
     * Возвращает отфильтрованный список регионов России
     */
    fun getRussianRegions(): List<Region> {
        val allRegions = offlineCacheManager.regions()

        // Строим карту "Регион -> его Родитель", чтобы быстро находить корни вглубь дерева
        val regionMap = allRegions.associateBy { it.id }

        return allRegions.filter { region ->
            isRegionInRussia(region, regionMap)
        }
    }

    /**
     * Рекурсивная проверка: принадлежит ли регион России (проверяет всю цепочку parentId)
     */
    private fun isRegionInRussia(region: Region, regionMap: Map<Int, Region>): Boolean {
        var currentParentId = region.parentId

        // Поднимаемся вверх по дереву parentId, пока не упремся в корень (0) или в GeoID России
        while (currentParentId != 0) {
            if (currentParentId == RUSSIA_GEO_ID) {
                return true
            }
            // Переходим к следующему родителю в цепочке (например: Город -> Область -> Округ -> Страна)
            val parentRegion = regionMap[currentParentId]
            currentParentId = parentRegion?.parentId ?: 0
        }

        return false
    }

    // Не забывайте освобождать ресурсы в жизненном цикле Activity/ViewModel
    fun destroy() {
        offlineCacheManager.removeRegionListener(regionListener)
    }
}
*/
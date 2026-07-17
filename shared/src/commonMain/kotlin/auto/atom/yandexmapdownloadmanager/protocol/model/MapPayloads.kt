package auto.atom.yandexmapdownloadmanager.protocol.model

import auto.atom.yandexmapdownloadmanager.ui.OfflineRegionState
import auto.atom.yandexmapdownloadmanager.model.OfflineRegion
import kotlinx.serialization.Serializable


/**
 * Ответ со списком регионов.
 */
@Serializable
data class RegionsPayload(
    val regions: List<OfflineRegion>
)

/**
 * Команда управления регионом. Запрос по коду региона.
 */
@Serializable
data class RegionPayload(
    val regionId: Int
)

/**
 * Ответ для получения статуса загрузки карты по региону.
 */
@Serializable
data class RegionStatePayload(
    val regionId: Int,
    val state: OfflineRegionState
)

@Serializable
data class RegionProgressPayload(
    val regionId: Int,
    val progress: Float
)
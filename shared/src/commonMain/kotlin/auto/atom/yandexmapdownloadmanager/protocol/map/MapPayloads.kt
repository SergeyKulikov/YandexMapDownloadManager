package auto.atom.yandexmapdownloadmanager.protocol.map

import auto.atom.yandexmapdownloadmanager.map.OfflineRegion
import kotlinx.serialization.Serializable

/**
 * Запрос списка доступных регионов.
 *
 * Payload отсутствует.
 */
@Serializable
data object GetRegionsPayload

/**
 * Ответ со списком регионов.
 */
@Serializable
data class RegionsPayload(
    val regions: List<OfflineRegion>
)

/**
 * Команда управления регионом.
 */
@Serializable
data class RegionPayload(
    val regionId: Int
)

/**
 * Прогресс загрузки региона.
 */
@Serializable
data class DownloadProgressPayload(
    val regionId: Int,
    val downloadedBytes: Long,
    val totalBytes: Long,
    val percent: Int
) {
    init {
        require(percent in 0..100)
    }
}
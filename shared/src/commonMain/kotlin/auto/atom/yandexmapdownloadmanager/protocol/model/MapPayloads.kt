package auto.atom.yandexmapdownloadmanager.protocol.model

import auto.atom.yandexmapdownloadmanager.model.OfflineRegion
import auto.atom.yandexmapdownloadmanager.model.OfflineRegionState
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

@Serializable
data class FileCopyProgressPayload(
    val regionId: Int,
    val fileName: String,
    val progress: Float
)

@Serializable
data class CachePathPayload(
    val path: String
)

@Serializable
data class DownloadedReleaseTimePayload(
    val regionId: Int,
    val releaseTime: Long?
)


/**
 * Чанк файла региона.
 */
@Serializable
data class FileDataPayload(
    val regionId: Int,
    val fileName: String,
    val offset: Long = 0L,
    val bytes: ByteArray = ByteArray(0)
)

package auto.atom.yandexmapdownloadmanager.protocol.model

import auto.atom.yandexmapdownloadmanager.model.FileTransferState
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
data class CachePathPayload(
    val path: String
)

@Serializable
data class DownloadedReleaseTimePayload(
    val regionId: Int,
    val releaseTime: Long?
)

@Serializable
data class RegionFilePayload(
    val regionId: Int,
    val fileName: String,
    val size: Long,
    val bytes: ByteArray
)

/**
 * Чанк файла региона.
 */
@Serializable
data class RegionFileChunkPayload(
    val regionId: Int,
    val fileName: String,
    val state: FileTransferState,
    val offset: Long = 0L,
    val totalSize: Long = 0L,
    val bytes: ByteArray = ByteArray(0)
)

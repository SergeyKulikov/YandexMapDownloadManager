package auto.atom.yandexmapdownloadmanager.timer.model

import kotlinx.serialization.Serializable

@Serializable
data class RegionDownloadState(
    val regionId: Int,
    val lastSuccessfulDownloadMillis: Long = 0L,
    val lastSuccessfulCopyMillis: Long = 0L,
    val copiedPath: String? = null
)
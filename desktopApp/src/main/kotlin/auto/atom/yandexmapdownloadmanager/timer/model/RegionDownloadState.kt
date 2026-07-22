package auto.atom.yandexmapdownloadmanager.timer.model

import kotlinx.serialization.Serializable

@Serializable
data class RegionDownloadState(
    val regionId: Int,

    // Последняя успешная загрузка
    val lastSuccessfulDownloadMillis: Long = 0L,

    // Следующая запланированная попытка
    val nextPlannedDownloadMillis: Long = 0L,

    val lastSuccessfulCopyMillis: Long = 0L,
    val copiedPath: String? = null
)
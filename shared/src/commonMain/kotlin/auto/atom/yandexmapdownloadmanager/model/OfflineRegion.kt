package auto.atom.yandexmapdownloadmanager.model

import auto.atom.yandexmapdownloadmanager.protocol.model.FileCopyProgressPayload
import kotlinx.serialization.Serializable

@Serializable
data class OfflineRegion(
    val id: Int,
    val parentId: Int?,
    val name: String,
    val country: String,
    val releaseTime: Long,
    val size: Long,
    val children: MutableList<OfflineRegion> = mutableListOf(),
    val state: OfflineRegionState = OfflineRegionState.AVAILABLE,
    val downloadProgress: Float? = null,
    val fileCopyProgress: FileCopyProgressPayload? = null,
)
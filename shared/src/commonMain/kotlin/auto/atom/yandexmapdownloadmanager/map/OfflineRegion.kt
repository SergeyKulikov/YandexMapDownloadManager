package auto.atom.yandexmapdownloadmanager.map

import auto.atom.yandexmapdownloadmanager.RegionState
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
    val state: RegionState = RegionState.NOT_DOWNLOADED,
    val downloadProgress: Int? = null,
)
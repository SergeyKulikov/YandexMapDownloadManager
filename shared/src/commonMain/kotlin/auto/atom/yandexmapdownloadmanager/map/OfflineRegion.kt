package auto.atom.yandexmapdownloadmanager.map

import kotlinx.serialization.Serializable

@Serializable
data class OfflineRegion(
    val id: Int,
    val parentId: Int?,
    val name: String,
    val country: String,
    val size: Long,
    val children: MutableList<OfflineRegion> = mutableListOf()
)
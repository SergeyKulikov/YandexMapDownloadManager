package auto.atom.yandexmapdownloadmanager.map

import kotlinx.serialization.Serializable

@Serializable
data class OfflineRegionNode(
    val region: OfflineRegion,
    val children: MutableList<OfflineRegionNode> = mutableListOf()
)
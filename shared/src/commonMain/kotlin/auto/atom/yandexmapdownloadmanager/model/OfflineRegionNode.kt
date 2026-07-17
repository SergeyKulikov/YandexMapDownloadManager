package auto.atom.yandexmapdownloadmanager.model

import kotlinx.serialization.Serializable

@Serializable
data class OfflineRegionNode(
    val region: OfflineRegion,
    val children: MutableList<OfflineRegionNode> = mutableListOf()
)
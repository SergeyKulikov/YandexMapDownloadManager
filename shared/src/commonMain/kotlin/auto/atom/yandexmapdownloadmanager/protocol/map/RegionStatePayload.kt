package auto.atom.yandexmapdownloadmanager.protocol.map

import auto.atom.yandexmapdownloadmanager.OfflineRegionState
import kotlinx.serialization.Serializable

@Serializable
data class RegionStatePayload(
    val regionId: Int,
    val state: OfflineRegionState
)
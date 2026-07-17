package auto.atom.yandexmapdownloadmanager.protocol

import auto.atom.yandexmapdownloadmanager.protocol.map.RegionStatePayload
import kotlinx.serialization.Serializable

@Serializable
data class RegionStateNotification(
    val id: String,
    val payload: RegionStatePayload
) : Message()
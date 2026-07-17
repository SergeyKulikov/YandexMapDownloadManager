package auto.atom.yandexmapdownloadmanager.protocol.model

import auto.atom.yandexmapdownloadmanager.protocol.model.RegionStatePayload
import kotlinx.serialization.Serializable

@Serializable
data class RegionStateNotification(
    val id: String,
    val payload: RegionStatePayload
) : Message()
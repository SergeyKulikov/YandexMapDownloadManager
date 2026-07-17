package auto.atom.yandexmapdownloadmanager.protocol.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class RegionProgressNotification(
    val id: String = UUID.randomUUID().toString(),
    val payload: RegionProgressPayload
) : Message()
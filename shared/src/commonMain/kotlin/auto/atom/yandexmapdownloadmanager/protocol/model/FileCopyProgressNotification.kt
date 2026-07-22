package auto.atom.yandexmapdownloadmanager.protocol.model

import kotlinx.serialization.Serializable

@Serializable
data class FileCopyProgressNotification(
    val payload: FileCopyProgressPayload
) : Message()
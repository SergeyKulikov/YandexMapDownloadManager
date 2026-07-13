package auto.atom.yandexmapdownloadmanager.protocol

import kotlinx.serialization.Serializable

@Serializable
data class HelloResponse(
    val protocolVersion: Int = 1,
    val application: String = "YandexMapDownloadManager"
) : Message()
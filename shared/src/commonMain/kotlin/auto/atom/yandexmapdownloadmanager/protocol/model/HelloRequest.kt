package auto.atom.yandexmapdownloadmanager.protocol.model

import kotlinx.serialization.Serializable

@Serializable
data class HelloRequest(
    val application: String = "YandexMapDownloadManager"
) : Message()
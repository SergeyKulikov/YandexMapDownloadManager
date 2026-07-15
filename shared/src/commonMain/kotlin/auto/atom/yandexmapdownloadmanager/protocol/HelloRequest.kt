package auto.atom.yandexmapdownloadmanager.protocol

import kotlinx.serialization.Serializable

@Serializable
data class HelloRequest(
    val application: String = "YandexMapDownloadManager"
) : Message()
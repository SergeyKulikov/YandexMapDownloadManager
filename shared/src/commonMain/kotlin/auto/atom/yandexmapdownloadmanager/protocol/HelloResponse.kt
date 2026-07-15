package auto.atom.yandexmapdownloadmanager.protocol

import kotlinx.serialization.Serializable

@Serializable
data class HelloResponse(
    val application: String = "YandexMapDownloadManager"
) : Message()
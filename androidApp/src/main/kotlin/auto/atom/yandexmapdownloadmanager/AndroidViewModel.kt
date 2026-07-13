package auto.atom.yandexmapdownloadmanager

import auto.atom.yandexmapdownloadmanager.transport.KtorTcpClient

class AndroidViewModel {

    private val client = KtorTcpClient()

    suspend fun connect() {
        client.connect(
            host = "10.0.2.2",
            port = 5555
        )
    }

    suspend fun disconnect() {
        client.close()
    }
}
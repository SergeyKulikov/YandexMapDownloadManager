package auto.atom.yandexmapdownloadmanager.commands

interface DownloadListener {
    fun onProgress(progress: Int)
    fun onCompleted()
    fun onError(error: Throwable)
}
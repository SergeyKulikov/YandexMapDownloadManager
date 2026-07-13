package auto.atom.yandexmapdownloadmanager

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
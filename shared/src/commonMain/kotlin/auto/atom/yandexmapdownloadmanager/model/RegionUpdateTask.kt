package auto.atom.yandexmapdownloadmanager.model

data class RegionUpdateTask(
    val region: OfflineRegion,
    val reason: UpdateReason,
)
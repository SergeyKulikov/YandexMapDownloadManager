package auto.atom.yandexmapdownloadmanager.protocol

import auto.atom.yandexmapdownloadmanager.map.OfflineRegion

interface DesktopProtocolApi {
    suspend fun getRegions(): List<OfflineRegion>
    suspend fun downloadRegion(regionId: Int, onProgress: (Float) -> Unit)
    suspend fun pauseRegion(regionId: String)
    suspend fun resumeRegion(regionId: String)
    suspend fun cancelRegion(regionId: String)
    suspend fun deleteRegion(regionId: String)
}
package auto.atom.yandexmapdownloadmanager.protocol

import auto.atom.yandexmapdownloadmanager.model.OfflineRegion
import auto.atom.yandexmapdownloadmanager.model.OfflineRegionState

interface DesktopProtocolApi {
    suspend fun getRegions(): List<OfflineRegion>
    suspend fun downloadRegion(regionId: Int)
    suspend fun pauseRegion(regionId: Int)
    suspend fun resumeRegion(regionId: Int)
    suspend fun cancelRegion(regionId: Int)
    suspend fun deleteRegion(regionId: Int)
    suspend fun getPath(): String
    suspend fun getRegionState(regionId: Int): OfflineRegionState
    suspend fun getDownloadedReleaseTime(regionId: Int): Long?
}
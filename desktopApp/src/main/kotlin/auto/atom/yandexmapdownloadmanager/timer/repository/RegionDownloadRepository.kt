package auto.atom.yandexmapdownloadmanager.timer.repository

import auto.atom.yandexmapdownloadmanager.timer.model.RegionDownloadState

interface RegionDownloadRepository {

    suspend fun getStates(): List<RegionDownloadState>

    suspend fun getState(regionId: Int): RegionDownloadState?

    suspend fun updateState(state: RegionDownloadState)

    suspend fun deleteState(regionId: Int)
}
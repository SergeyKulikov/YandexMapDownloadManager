package auto.atom.yandexmapdownloadmanager.timer

import auto.atom.yandexmapdownloadmanager.flatten
import auto.atom.yandexmapdownloadmanager.model.OfflineRegion
import auto.atom.yandexmapdownloadmanager.model.OfflineRegionState
import auto.atom.yandexmapdownloadmanager.timer.model.RegionAssignments
import auto.atom.yandexmapdownloadmanager.timer.model.RegionDownloadState
import auto.atom.yandexmapdownloadmanager.timer.model.UpdatePolicy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AutoUpdateManager(
    private val scope: CoroutineScope,
    private val onDownloadRegion: suspend (Int) -> Unit,
    private val regions: StateFlow<List<OfflineRegion>>,
    private val policies: StateFlow<List<UpdatePolicy>>,
    private val assignments: StateFlow<RegionAssignments>,
    private val downloadStates: StateFlow<List<RegionDownloadState>>,
    private val onDownloadStarted: suspend (Int) -> Unit,
) {

    private var job: Job? = null

    /**
     * Регионы, для которых уже отправлена команда на загрузку,
     * но состояние еще не успело измениться.
     */
    private val startedDownloads = mutableSetOf<Int>()

    fun start() {

        if (job != null) {
            return
        }

        job = scope.launch {

            checkUpdates()

            while (isActive) {
                delay(60_000)
                checkUpdates()
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
        startedDownloads.clear()
    }

    private suspend fun checkUpdates() {

        val now = System.currentTimeMillis()

        val regionsById =
            regions.value
                .flatten()
                .associateBy { it.id }

        // Убираем регионы, для которых загрузка уже началась
        // или завершилась, чтобы их можно было проверять повторно.
        startedDownloads.removeAll { regionId ->

            when (regionsById[regionId]?.state) {

                OfflineRegionState.DOWNLOADING,
                OfflineRegionState.COMPLETED,
                OfflineRegionState.OUTDATED,
                OfflineRegionState.NEED_UPDATE -> true

                else -> false
            }
        }

        for ((regionId, policyId) in assignments.value.assignments) {

            if (regionId in startedDownloads) {
                continue
            }

            val policy =
                policies.value.firstOrNull {
                    it.id == policyId
                } ?: continue

            val region =
                regionsById[regionId]
                    ?: continue

            when (region.state) {

                OfflineRegionState.DOWNLOADING,
                OfflineRegionState.PAUSED,
                OfflineRegionState.UNSUPPORTED -> continue

                else -> Unit
            }

            val lastDownload =
                downloadStates.value
                    .firstOrNull {
                        it.regionId == regionId
                    }
                    ?.lastSuccessfulDownloadMillis
                    ?: 0L

            val nextDownload =
                lastDownload +
                        policy.periodDays.inWholeMilliseconds

            if (lastDownload == 0L || nextDownload <= now) {

                startedDownloads += regionId

                onDownloadRegion(regionId)

                onDownloadStarted(regionId)

                return
            }
        }
    }
}
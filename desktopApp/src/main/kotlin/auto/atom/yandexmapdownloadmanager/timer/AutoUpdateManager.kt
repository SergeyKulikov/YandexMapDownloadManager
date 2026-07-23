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
    private val onDownloadCompleted: suspend (Int) -> Unit,
    private val onCopyRegion: suspend (Int) -> Unit,
) {

    private var schedulerJob: Job? = null
    private var monitorJob: Job? = null

    /**
     * Регионы, загрузка которых сейчас выполняется.
     */
    private val startedDownloads = mutableSetOf<Int>()

    fun start() {

        if (schedulerJob != null) {
            return
        }

        //
        // Проверка расписания раз в минуту.
        //
        schedulerJob = scope.launch {

            while (isActive) {

                checkSchedule()

                delay(60_000)
            }
        }

        //
        // Контроль завершения загрузок.
        //
        monitorJob = scope.launch {

            while (isActive) {

                monitorDownloads()

                delay(1000)
            }
        }
    }

    fun stop() {

        schedulerJob?.cancel()
        schedulerJob = null

        monitorJob?.cancel()
        monitorJob = null

        startedDownloads.clear()
    }

    private suspend fun monitorDownloads() {

        if (startedDownloads.isEmpty()) {
            return
        }

        val regionsById =
            regions.value
                .flatten()
                .associateBy { it.id }

        for (regionId in startedDownloads.toList()) {

            val region = regionsById[regionId] ?: continue

            if (region.state == OfflineRegionState.COMPLETED) {

                onDownloadCompleted(regionId)

                startedDownloads.remove(regionId)

                onCopyRegion(regionId)

                checkSchedule()

                return
            }
        }
    }

    private suspend fun checkSchedule() {

        if (startedDownloads.isNotEmpty()) {
            return
        }

        val now = System.currentTimeMillis()

        val regionsById =
            regions.value
                .flatten()
                .associateBy { it.id }

        for ((regionId, policyId) in assignments.value.assignments) {

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
                OfflineRegionState.UNSUPPORTED ->
                    continue

                //
                // Если карта отсутствует на устройстве,
                // скачиваем её сразу, не обращая внимания на расписание.
                //
                OfflineRegionState.AVAILABLE -> {

                    startedDownloads += regionId

                    onDownloadRegion(regionId)

                    onDownloadStarted(regionId)

                    return
                }

                else -> Unit
            }

            val state =
                downloadStates.value.firstOrNull {
                    it.regionId == regionId
                }

            val nextDownload =
                state?.nextPlannedDownloadMillis ?: 0L

            if (nextDownload == 0L || nextDownload <= now) {

                startedDownloads += regionId

                onDownloadRegion(regionId)

                onDownloadStarted(regionId)

                return
            }
        }
    }
}
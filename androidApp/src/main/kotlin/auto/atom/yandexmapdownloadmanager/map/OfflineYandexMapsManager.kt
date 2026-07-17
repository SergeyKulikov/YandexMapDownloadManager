package auto.atom.yandexmapdownloadmanager.map

import android.util.Log
import com.google.gson.Gson
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.offline_cache.OfflineCacheManager
import com.yandex.mapkit.offline_cache.Region
import com.yandex.mapkit.offline_cache.RegionListUpdatesListener
import com.yandex.mapkit.offline_cache.RegionListener
import com.yandex.mapkit.offline_cache.RegionState
import com.yandex.runtime.Error
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference

/**
 * Работа с офлайн-картами MapKit.
 *
 * Инкапсулирует OfflineCacheManager и предоставляет
 * простой API для приложения.
 */
class OfflineYandexMapsManager {

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.Main
    )

    private val offlineCacheManager: OfflineCacheManager =
        MapKitFactory.getInstance().offlineCacheManager

    private var onRegionsLoaded: ((List<Region>) -> Unit)? = null

    /**
     * Изменился список регионов.
     */
    private val regionListUpdatesListener = RegionListUpdatesListener {

        onRegionsLoaded?.invoke(
            offlineCacheManager.regions()
        )
    }

    private var onRegionStateChanged: ((Int, RegionState) -> Unit)? = null

    fun setOnRegionStateChangedListener(
        listener: (Int, RegionState) -> Unit
    ) {
        onRegionStateChanged = listener
    }

    private var onRegionProgressChanged: ((Int, Float) -> Unit)? = null

    fun setOnRegionProgressChangedListener(
        listener: (Int, Float) -> Unit
    ) {
        onRegionProgressChanged = listener
    }

    /**
     * Активные загрузки.
     */
    private val downloads = mutableMapOf<Int, CompletableDeferred<Unit>>()

    /**
     * Колбэки прогресса.
     */
    private val progressCallbacks =
        mutableMapOf<Int, (Float) -> Unit>()

    /**
     * Слушатель изменений состояния региона.
     */
    private val regionListener = object : RegionListener {
        override fun onRegionStateChanged(regionId: Int) {
            val state = offlineCacheManager.getState(regionId)

            Log.d(
                "OfflineMaps",
                "Region $regionId state = $state"
            )

            onRegionStateChanged?.invoke(
                regionId,
                state
            )

            when (state) {
                RegionState.COMPLETED -> {
                    downloads[regionId]?.complete(Unit)
                }

                RegionState.AVAILABLE,
                RegionState.DOWNLOADING,
                RegionState.PAUSED,
                RegionState.OUTDATED,
                RegionState.UNSUPPORTED,
                RegionState.NEED_UPDATE -> {
                    // Ждем дальнейших событий.
                }
            }
        }

//        override fun onRegionProgress(regionId: Int) {
//            val progress = offlineCacheManager.getProgress(regionId)
//
//            progressCallbacks[regionId]?.invoke(progress)
//
//            Log.d(
//                "OfflineMaps",
//                "Region $regionId progress = $progress"
//            )
//        }

        override fun onRegionProgress(regionId: Int) {
            val progress = offlineCacheManager.getProgress(regionId)

            Log.d(
                "OfflineMaps",
                "Region $regionId progress = $progress"
            )

            onRegionProgressChanged?.invoke(
                regionId,
                progress
            )
        }
    }

    /**
     * Слушатель ошибок загрузки.
     */
    private val errorListener = object : OfflineCacheManager.ErrorListener {

        override fun onError(error: Error) {

            Log.e(
                "OfflineMaps",
                error.toString()
            )
        }

        override fun onRegionError(
            error: Error,
            regionId: Int
        ) {

            Log.e(
                "OfflineMaps",
                "Region $regionId error: $error"
            )

            downloads[regionId]?.completeExceptionally(
                RuntimeException(error.toString())
            )
        }
    }

    init {

        offlineCacheManager.addRegionListUpdatesListener(
            WeakReference(regionListUpdatesListener)
        )

        offlineCacheManager.addRegionListener(
            WeakReference(regionListener)
        )

        offlineCacheManager.addErrorListener(
            WeakReference(errorListener)
        )
    }

    /**
     * Загружает список доступных регионов.
     */
    fun loadRegions(
        onLoaded: (List<Region>) -> Unit
    ) {

        onRegionsLoaded = onLoaded

        scope.launch {

            val regions = offlineCacheManager.regions()

            Log.d(
                "REG",
                Gson().toJson(regions)
            )

            onLoaded(regions)
        }
    }

    /**
     * Начать загрузку региона.
     */
    suspend fun download(regionId: Int) {

        val deferred = CompletableDeferred<Unit>()
        downloads[regionId] = deferred

        try {

            withContext(Dispatchers.Main.immediate) {
                offlineCacheManager.startDownload(regionId)
            }

            deferred.await()

        } finally {
            downloads.remove(regionId)
        }
    }
    /*
    suspend fun download(
        regionId: Int,
        onProgress: (Float) -> Unit
    ) {
        val deferred = CompletableDeferred<Unit>()

        downloads[regionId] = deferred
        progressCallbacks[regionId] = onProgress

        try {

            withContext(Dispatchers.Main.immediate) {
                offlineCacheManager.startDownload(regionId)
            }

            deferred.await()

        } finally {

            downloads.remove(regionId)
            progressCallbacks.remove(regionId)
        }
    }
    */

    /**
     * Приостановить загрузку.
     */
    suspend fun pause(regionId: Int) {
        withContext(Dispatchers.Main.immediate) {
            offlineCacheManager.pauseDownload(regionId)
        }
    }

    /**
     * Продолжить загрузку.
     */
    suspend fun resume(regionId: Int) {
        withContext(Dispatchers.Main.immediate) {
            offlineCacheManager.startDownload(regionId)
        }
    }

    /**
     * Отменить загрузку.
     */
    suspend fun cancel(regionId: Int) {
        withContext(Dispatchers.Main.immediate) {
            offlineCacheManager.stopDownload(regionId)
        }
    }

    /**
     * Удалить загруженный регион.
     */
    suspend fun remove(regionId: Int) {
        withContext(Dispatchers.Main.immediate) {
            offlineCacheManager.drop(regionId)
        }
    }

    /**
     * Текущее состояние региона.
     */
    fun getState(regionId: Int): RegionState {
        return offlineCacheManager.getState(regionId)
    }

    /**
     * Прогресс загрузки региона (0..100).
     */
    fun getProgress(regionId: Int): Float =
        offlineCacheManager.getProgress(regionId)

    /**
     * Дата скачанной версии региона.
     */
    fun getDownloadedReleaseTime(regionId: Int): Long? =
        offlineCacheManager.getDownloadedReleaseTime(regionId)

    /**
     * Возможно недостаточно свободного места.
     */
    fun mayBeOutOfAvailableSpace(regionId: Int): Boolean =
        offlineCacheManager.mayBeOutOfAvailableSpace(regionId)
}
package auto.atom.yandexmapdownloadmanager.map

import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.offline_cache.OfflineCacheManager
import com.yandex.mapkit.offline_cache.Region
import com.yandex.mapkit.offline_cache.RegionListUpdatesListener
import com.yandex.mapkit.offline_cache.RegionListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

/**
 * Работа с офлайн-картами MapKit.
 *
 * Инкапсулирует OfflineCacheManager и предоставляет
 * простой API для приложения.
 */
class OfflineYandexMapsManager {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val offlineCacheManager: OfflineCacheManager = MapKitFactory.getInstance().offlineCacheManager
    private val regionListUpdatesListener = RegionListUpdatesListener {
        onRegionsLoaded?.invoke(offlineCacheManager.regions())
    }

    private var onRegionsLoaded: ((List<Region>) -> Unit)? = null

    init {
        /**
         * Менеджер офлайн-карт MapKit.
         */
        offlineCacheManager.addRegionListUpdatesListener(
            WeakReference(regionListUpdatesListener)
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

            if (regions.isNotEmpty()) {
                onLoaded(regions)
            }
        }
    }

    /**
     * Начинает загрузку региона.
     */
    fun download(regionId: Int) {
        TODO("Будет реализовано следующим шагом")
    }

    /**
     * Приостанавливает загрузку.
     */
    fun pause(regionId: Int) {
        TODO("Будет реализовано следующим шагом")
    }

    /**
     * Продолжает загрузку.
     */
    fun resume(regionId: Int) {
        TODO("Будет реализовано следующим шагом")
    }

    /**
     * Отменяет загрузку.
     */
    fun cancel(regionId: Int) {
        TODO("Будет реализовано следующим шагом")
    }

    /**
     * Удаляет регион.
     */
    fun remove(regionId: Int) {
        TODO("Будет реализовано следующим шагом")
    }




}



package auto.atom.yandexmapdownloadmanager.ya

import android.content.Context
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.offline_cache.OfflineCacheManager
import com.yandex.mapkit.offline_cache.Region
import com.yandex.mapkit.offline_cache.RegionListUpdatesListener
import java.lang.ref.WeakReference

/**
 * Работа с офлайн-картами MapKit.
 *
 * Инкапсулирует OfflineCacheManager и предоставляет
 * простой API для приложения.
 */
class OfflineMapsManager {

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

        val regions = offlineCacheManager.regions()

        if (regions.isNotEmpty()) {
            onLoaded(regions)
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
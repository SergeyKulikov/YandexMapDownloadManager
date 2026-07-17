package auto.atom.yandexmapdownloadmanager.map

import auto.atom.yandexmapdownloadmanager.model.OfflineRegion
import auto.atom.yandexmapdownloadmanager.model.OfflineRegionNode
import auto.atom.yandexmapdownloadmanager.ui.OfflineRegionState
import com.yandex.mapkit.offline_cache.Region
import com.yandex.mapkit.offline_cache.RegionState

fun buildTree(
    regions: List<Region>
): List<OfflineRegionNode> {

    val nodes = regions.associate { region ->

        region.id to OfflineRegionNode(
            region = OfflineRegion(
                id = region.id,
                parentId = region.parentId,
                name = region.name,
                country = region.country,
                releaseTime = region.releaseTime,
                size = region.size.value.toLong()
            )
        )
    }

    val roots = mutableListOf<OfflineRegionNode>()

    nodes.values.forEach { node ->

        val parentId = node.region.parentId

        if (parentId == null) {
            roots += node
        } else {
            nodes[parentId]?.children?.add(node)
        }
    }

    return roots
}


/**
 * Строит дерево регионов из списка MapKit по всем странам.
 */
fun List<Region>.toOfflineRegionTree(status: Map<Int, RegionState>, progress: Map<Int, Float>): List<OfflineRegion> {

    val nodes = this.associate { region ->

        region.id to OfflineRegion(
            id = region.id,
            parentId = region.parentId,
            name = region.name,
            country = region.country,
            releaseTime = region.releaseTime,
            state = (status[region.id] ?: RegionState.UNSUPPORTED).toOfflineRegionState(),
            downloadProgress = progress[region.id],
            size = region.size.value.toLong()
        )
    }

    val roots = mutableListOf<OfflineRegion>()

    nodes.values.forEach { region ->

        val parentId = region.parentId

        if (parentId == null) {
            roots += region
        } else {
            nodes[parentId]
                ?.children
                ?.add(region)
        }
    }

    return roots
}

/**
 * Оставляет только корневые офлайн-регионы России.
 */
fun List<OfflineRegion>.filterRussiaRegions(): List<OfflineRegion> {

    val russianIds = auto.atom.yandexmapdownloadmanager.model.OfflineRussiaRegion.entries
        .map(auto.atom.yandexmapdownloadmanager.model.OfflineRussiaRegion::id)
        .toSet()

    return mapNotNull { region ->
        if (region.id in russianIds) {
            region.copy(parentId = auto.atom.yandexmapdownloadmanager.model.CountryGeoID.RUSSIA.id)
        } else {
            null
        }
    }
}


fun RegionState.toOfflineRegionState() =
    when (this) {
        RegionState.AVAILABLE -> OfflineRegionState.AVAILABLE
        RegionState.DOWNLOADING -> OfflineRegionState.DOWNLOADING
        RegionState.PAUSED -> OfflineRegionState.PAUSED
        RegionState.COMPLETED -> OfflineRegionState.COMPLETED
        RegionState.OUTDATED -> OfflineRegionState.OUTDATED
        RegionState.UNSUPPORTED -> OfflineRegionState.UNSUPPORTED
        RegionState.NEED_UPDATE -> OfflineRegionState.NEED_UPDATE
    }
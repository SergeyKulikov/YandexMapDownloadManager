package auto.atom.yandexmapdownloadmanager.map

import auto.atom.yandexmapdownloadmanager.protocol.CountryGeoID
import auto.atom.yandexmapdownloadmanager.protocol.OfflineRussiaRegion
import com.yandex.mapkit.offline_cache.Region

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
fun List<Region>.toOfflineRegionTree(): List<OfflineRegion> {

    val nodes = this.associate { region ->

        region.id to OfflineRegion(
            id = region.id,
            parentId = region.parentId,
            name = region.name,
            country = region.country,
            releaseTime = region.releaseTime,
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

    val russianIds = OfflineRussiaRegion.entries
        .map(OfflineRussiaRegion::id)
        .toSet()

    return mapNotNull { region ->
        if (region.id in russianIds) {
            region.copy(parentId = CountryGeoID.RUSSIA.id)
        } else {
            null
        }
    }
}
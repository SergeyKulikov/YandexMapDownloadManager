package auto.atom.yandexmapdownloadmanager.timer.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import auto.atom.yandexmapdownloadmanager.model.OfflineRegion
import auto.atom.yandexmapdownloadmanager.ui.OfflineRegionItem

private sealed interface RegionListItem {

    data class Header(
        val letter: Char
    ) : RegionListItem

    data class Region(
        val region: OfflineRegion
    ) : RegionListItem
}

private fun List<OfflineRegion>.flatten(): List<OfflineRegion> =
    buildList {

        fun addRegion(region: OfflineRegion) {
            add(region)
            region.children.forEach(::addRegion)
        }

        forEach(::addRegion)
    }

@Composable
fun AssignedRegionsList(
    regions: List<OfflineRegion>,
    onDownloadClick: (OfflineRegion) -> Unit,
    formatSize: (Long) -> String,
    formatDate: (Long) -> String,
    modifier: Modifier = Modifier
) {

    val listItems = remember(regions) {

        buildList {

            regions
                .flatten()
                .sortedBy { it.name.lowercase() }
                .groupBy { it.name.first().uppercaseChar() }
                .toSortedMap()
                .forEach { (letter, list) ->

                    add(RegionListItem.Header(letter))

                    list.forEach {
                        add(RegionListItem.Region(it))
                    }
                }
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {

        items(
            items = listItems,
            key = {
                when (it) {
                    is RegionListItem.Header -> "header_${it.letter}"
                    is RegionListItem.Region -> it.region.id
                }
            },
            span = {
                when (it) {
                    is RegionListItem.Header -> GridItemSpan(maxLineSpan)
                    is RegionListItem.Region -> GridItemSpan(1)
                }
            }
        ) { item ->

            when (item) {

                is RegionListItem.Header -> {

                    Text(
                        text = item.letter.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 12.dp
                        )
                    )
                }

                is RegionListItem.Region -> {

                    OfflineRegionItem(
                        region = item.region,
                        level = 0,
                        onDownloadClick = onDownloadClick,
                        formatSize = formatSize,
                        formatDate = formatDate
                    )
                }
            }
        }
    }
}
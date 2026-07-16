package auto.atom.yandexmapdownloadmanager

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import auto.atom.yandexmapdownloadmanager.map.OfflineRegion

@Composable
fun OfflineRegionTree(
    regions: List<OfflineRegion>,
    onDownloadClick: (OfflineRegion) -> Unit
) {
    LazyColumn {
        items(
            items = regions,
            key = { it.id }
        ) { region ->
            OfflineRegionItem(
                region = region,
                level = 0,
                onDownloadClick = onDownloadClick
            )
        }
    }
}
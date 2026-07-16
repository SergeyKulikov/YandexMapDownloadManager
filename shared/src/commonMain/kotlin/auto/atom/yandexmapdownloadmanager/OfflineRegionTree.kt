package auto.atom.yandexmapdownloadmanager

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import auto.atom.yandexmapdownloadmanager.map.OfflineRegion

@Composable
fun OfflineRegionTree(
    regions: List<OfflineRegion>,
    onDownloadClick: (OfflineRegion) -> Unit,
    formatSize: (Long) -> String,
    formatDate: (Long) -> String,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(
            items = regions,
            key = { it.id }
        ) { region ->
            OfflineRegionItem(
                region = region,
                level = 0,
                onDownloadClick = onDownloadClick,
                formatSize = formatSize,
                formatDate = formatDate
            )
        }
    }
}
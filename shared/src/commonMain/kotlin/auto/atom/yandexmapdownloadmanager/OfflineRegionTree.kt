package auto.atom.yandexmapdownloadmanager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import auto.atom.yandexmapdownloadmanager.map.OfflineRegion

@Composable
fun OfflineRegionTree(
    regions: List<OfflineRegion>,
    onDownloadClick: (OfflineRegion) -> Unit,
    formatSize: (Long) -> String,
    formatDate: (Long) -> String,
    modifier: Modifier = Modifier
) {

    val groups = remember(regions) {
        regions
            .sortedBy { it.name.lowercase() }
            .groupBy { it.name.first().uppercaseChar() }
            .toSortedMap()
    }

    LazyColumn(
        modifier = modifier
    ) {

        groups.forEach { (letter, list) ->

            item(key = "header_$letter") {

                Text(
                    text = letter.toString(),
                    modifier = Modifier.padding(
                        start = 20.dp,
                        top = 20.dp,
                        bottom = 12.dp
                    ),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            val rows = list.chunked(2)

            itemsIndexed(
                items = rows,
                key = { _, row -> row.first().id }
            ) { _, row ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    OfflineRegionItem(
                        modifier = Modifier.weight(1f),
                        region = row[0],
                        level = 0,
                        onDownloadClick = onDownloadClick,
                        formatSize = formatSize,
                        formatDate = formatDate
                    )

                    if (row.size > 1) {

                        OfflineRegionItem(
                            modifier = Modifier.weight(1f),
                            region = row[1],
                            level = 0,
                            onDownloadClick = onDownloadClick,
                            formatSize = formatSize,
                            formatDate = formatDate
                        )

                    } else {

                        Spacer(
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
package auto.atom.yandexmapdownloadmanager.timer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import auto.atom.yandexmapdownloadmanager.timer.model.RegionTransferData
import auto.atom.yandexmapdownloadmanager.ui.MainViewModel
import kotlin.collections.toSortedMap

@Composable
fun RegionAssignmentPanel(
    viewModel: MainViewModel
) {
    val regions by viewModel.filteredRegions.collectAsState()

    val groupedRegions = remember(regions) {
        regions
            .map { region ->
                RegionTransferData(
                    id = region.id,
                    name = region.name
                )
            }
            .sortedBy { it.name }
            .groupBy {
                it.name
                    .firstOrNull()
                    ?.uppercaseChar()
                    ?.toString()
                    ?: "#"
            }
            .toSortedMap()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {

        groupedRegions.forEach { (letter, regionList) ->

            stickyHeader {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = letter,
                        modifier = Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(regionList.chunked(3)) { row ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    row.forEach { region ->

                        RegionTransferItem(
                            region = region,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    repeat(3 - row.size) {
                        androidx.compose.foundation.layout.Spacer(
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
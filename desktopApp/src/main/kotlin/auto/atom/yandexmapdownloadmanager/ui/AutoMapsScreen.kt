package auto.atom.yandexmapdownloadmanager.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import auto.atom.yandexmapdownloadmanager.flatten
import auto.atom.yandexmapdownloadmanager.model.OfflineRegionState
import auto.atom.yandexmapdownloadmanager.model.RegionUpdateTask
import auto.atom.yandexmapdownloadmanager.model.UpdateReason

private sealed interface TimerListItem {
    data class Header(
        val letter: Char
    ) : TimerListItem

    data class Region(
        val task: RegionUpdateTask,
        val policy: auto.atom.yandexmapdownloadmanager.timer.model.UpdatePolicy,
        val downloadState: auto.atom.yandexmapdownloadmanager.timer.model.RegionDownloadState?
    ) : TimerListItem
}

@Composable
fun AutoMapsScreen(
    viewModel: MainViewModel
) {

    val regions by viewModel.regions.collectAsState()
    val policies by viewModel.policies.collectAsState()
    val assignments by viewModel.regionAssignments.collectAsState()
    val downloadStates by viewModel.downloadStates.collectAsState()

    val listItems = remember(
        regions,
        policies,
        assignments,
        downloadStates
    ) {

        val flatRegions = regions.flatten()

        val timerItems =
            assignments.assignments.mapNotNull { (regionId, policyId) ->

                val region =
                    flatRegions.firstOrNull {
                        it.id == regionId
                    } ?: return@mapNotNull null

                val policy =
                    policies.firstOrNull {
                        it.id == policyId
                    } ?: return@mapNotNull null

                val downloadState =
                    downloadStates.firstOrNull {
                        it.regionId == regionId
                    }

                val reason =
                    if (region.state == OfflineRegionState.AVAILABLE)
                        UpdateReason.NOT_DOWNLOADED
                    else
                        UpdateReason.NEW_VERSION_AVAILABLE

                TimerListItem.Region(
                    task = RegionUpdateTask(
                        region = region,
                        reason = reason
                    ),
                    policy = policy,
                    downloadState = downloadState
                )
            }

        buildList {

            timerItems
                .sortedBy {
                    it.task.region.name.lowercase()
                }
                .groupBy {
                    it.task.region.name.first().uppercaseChar()
                }
                .toSortedMap()
                .forEach { (letter, items) ->

                    add(
                        TimerListItem.Header(letter)
                    )

                    addAll(items)
                }
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(8.dp)
    ) {

        items(
            items = listItems,
            key = {
                when (it) {
                    is TimerListItem.Header ->
                        "header_${it.letter}"

                    is TimerListItem.Region ->
                        it.task.region.id
                }
            },
            span = {
                when (it) {
                    is TimerListItem.Header ->
                        GridItemSpan(maxLineSpan)

                    is TimerListItem.Region ->
                        GridItemSpan(1)
                }
            }
        ) { item ->

            when (item) {

                is TimerListItem.Header -> {

                    Text(
                        text = item.letter.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 12.dp
                        )
                    )
                }

                is TimerListItem.Region -> {

                    RegionUpdateItem(
                        region = item.task.region,
                        task = item.task,
                        policy = item.policy,
                        downloadState = item.downloadState
                    )
                }
            }
        }
    }
}
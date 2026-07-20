package auto.atom.yandexmapdownloadmanager.ui

import androidx.compose.foundation.layout.PaddingValues
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
import auto.atom.yandexmapdownloadmanager.model.UpdateListItem
import auto.atom.yandexmapdownloadmanager.model.buildUpdateQueue
import androidx.compose.foundation.layout.*

@Composable
fun AutoMapsScreen(
    viewModel: MainViewModel
) {

    val regions by viewModel.regions.collectAsState()


    println(regions.toString())

    val listItems = remember(regions) {
        buildList {

            regions
                .buildUpdateQueue()
                .sortedBy { it.region.name.lowercase() }
                .groupBy { it.region.name.first().uppercaseChar() }
                .toSortedMap()
                .forEach { (letter, tasks) ->

                    add(UpdateListItem.Header(letter))

                    tasks.forEach {
                        add(UpdateListItem.Region(it))
                    }
                }
        }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth(),
        contentPadding = PaddingValues(8.dp)
    ) {

        items(
            items = listItems,
            key = {
                when (it) {
                    is UpdateListItem.Header -> "header_${it.letter}"
                    is UpdateListItem.Region -> it.task.region.id
                }
            },
            span = {
                when (it) {
                    is UpdateListItem.Header ->
                        GridItemSpan(maxLineSpan)

                    is UpdateListItem.Region ->
                        GridItemSpan(1)
                }
            }
        ) { item ->

            when (item) {

                is UpdateListItem.Header -> {

                    Text(
                        text = item.letter.toString(),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(
                            horizontal = 8.dp,
                            vertical = 12.dp
                        )
                    )
                }

                is UpdateListItem.Region -> {

                    RegionUpdateItem(item.task)
                }
            }
        }
    }
}


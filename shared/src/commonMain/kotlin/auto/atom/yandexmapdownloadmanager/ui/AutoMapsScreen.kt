package auto.atom.yandexmapdownloadmanager.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import auto.atom.yandexmapdownloadmanager.model.buildUpdateQueue

@Composable
fun AutoMapsScreen(
    viewModel: MainViewModel
) {

    val regions by viewModel.regions.collectAsState()

    val updateQueue = remember(regions) {
        regions.buildUpdateQueue()
    }

    if (updateQueue.isEmpty()) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Все регионы обновлены.")
        }

        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {

        items(
            items = updateQueue,
            key = { it.region.id }
        ) { task ->

            RegionUpdateItem(task)
        }
    }
}
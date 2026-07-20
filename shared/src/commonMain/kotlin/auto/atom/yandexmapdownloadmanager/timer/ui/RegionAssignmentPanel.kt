package auto.atom.yandexmapdownloadmanager.timer.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import auto.atom.yandexmapdownloadmanager.ui.MainViewModel
import auto.atom.yandexmapdownloadmanager.ui.OfflineRegionTree

@Composable
fun RegionAssignmentPanel(
    viewModel: MainViewModel
) {

    val regions by viewModel.filteredRegions.collectAsState()

    OfflineRegionTree(
        modifier = Modifier.fillMaxSize(),
        regions = regions,
        onDownloadClick = viewModel::regionAction,
        formatSize = { size ->
            "${size / 1024 / 1024} MB"
        },
        formatDate = { time ->
            java.text.SimpleDateFormat("dd.MM.yyyy")
                .format(java.util.Date(time))
        }
    )
}
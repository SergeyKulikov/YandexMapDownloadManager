package auto.atom.yandexmapdownloadmanager.timer.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import auto.atom.yandexmapdownloadmanager.formatDate
import auto.atom.yandexmapdownloadmanager.formatSize
import auto.atom.yandexmapdownloadmanager.ui.MainViewModel

@Composable
fun TimerScreen(
    viewModel: MainViewModel
) {

    val policies by viewModel.policies.collectAsState()
    val selectedPolicyId by viewModel.selectedPolicyId.collectAsState()
    val regions by viewModel.filteredRegions.collectAsState()

    Row(
        modifier = Modifier.fillMaxSize()
    ) {

        TimerList(
            modifier = Modifier
                .width(260.dp)
                .fillMaxHeight(),
            policies = policies,
            selectedPolicyId = selectedPolicyId,
            onSelect = viewModel::selectPolicy,
            onAdd = viewModel::addPolicy,
            onDelete = viewModel::deletePolicy
        )

        VerticalDivider()

        /*
        AssignedRegionsList(
            regions = regions,
            onDownloadClick = viewModel::regionAction,
            formatSize = ::formatSize,
            formatDate = ::formatDate,
            modifier = Modifier.weight(1f)
        )
        */

        RegionAssignmentPanel(viewModel)
    }
}
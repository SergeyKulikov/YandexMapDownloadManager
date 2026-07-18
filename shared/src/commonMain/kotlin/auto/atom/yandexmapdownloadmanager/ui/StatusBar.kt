package auto.atom.yandexmapdownloadmanager.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import atomyandexmapmanager.shared.generated.resources.Res
import atomyandexmapmanager.shared.generated.resources.folder
import atomyandexmapmanager.shared.generated.resources.system_update
import auto.atom.yandexmapdownloadmanager.model.OfflineRegion
import auto.atom.yandexmapdownloadmanager.model.buildUpdateQueue
import org.jetbrains.compose.resources.painterResource

@Composable
fun StatusBar(
    viewModel: MainViewModel
) {
    val regions by viewModel.regions.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            painter = painterResource(Res.drawable.folder),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.width(6.dp))

        Text(
            text = "${regions.countRegions()} регионов"
        )

        Spacer(Modifier.width(6.dp)) // Spacer(Modifier.weight(1f))

        Icon(
            painter = painterResource(Res.drawable.system_update),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.width(6.dp))

        Text(
            text = "${regions.buildUpdateQueue().size} требуют обновления"
        )
    }
}

private fun List<OfflineRegion>.countRegions(): Int =
    sumOf { it.countRegions() }

private fun OfflineRegion.countRegions(): Int =
    1 + children.sumOf { it.countRegions() }
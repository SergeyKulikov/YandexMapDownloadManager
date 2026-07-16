package auto.atom.yandexmapdownloadmanager

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.toString
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import auto.atom.yandexmapdownloadmanager.map.OfflineRegion
import com.sun.org.apache.xalan.internal.lib.ExsltDatetime.formatDate

@Composable
fun OfflineRegionItem(
    region: OfflineRegion,
    level: Int,
    onDownloadClick: (OfflineRegion) -> Unit
) {

    Column {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = (level * 24).dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(region.name)

                Text(
                    text = "dddd",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = region.releaseTime.toString(), //formatDate(region.releaseTime),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            RegionDownloadButton(
                region = region,
                onClick = {
                    onDownloadClick(region)
                }
            )
        }

        region.downloadProgress?.let {

            LinearProgressIndicator(
                progress = { it / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = (level * 24 + 16).dp,
                        end = 16.dp
                    )
            )
        }

        region.children.forEach {

            OfflineRegionItem(
                region = it,
                level = level + 1,
                onDownloadClick = onDownloadClick,
            )
        }
    }
}

@Composable
private fun RegionDownloadButton(
    region: OfflineRegion,
    onClick: () -> Unit
) {

    val title = when (region.state) {

        RegionState.NOT_DOWNLOADED ->
            "Скачать"

        RegionState.DOWNLOADING ->
            "Отмена"

        RegionState.DOWNLOADED ->
            "Удалить"

        RegionState.PAUSED ->
            "Продолжить"

        RegionState.ERROR ->
            "Повторить"
    }

    Button(
        onClick = onClick
    ) {
        Text(title)
    }
}
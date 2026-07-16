package auto.atom.yandexmapdownloadmanager

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import atomyandexmapmanager.shared.generated.resources.Res
import atomyandexmapmanager.shared.generated.resources.calendar
import atomyandexmapmanager.shared.generated.resources.folder
import atomyandexmapmanager.shared.generated.resources.storage
import auto.atom.yandexmapdownloadmanager.map.OfflineRegion
import org.jetbrains.compose.resources.painterResource

@Composable
fun OfflineRegionItem(
    region: OfflineRegion,
    level: Int,
    onDownloadClick: (OfflineRegion) -> Unit,
    formatSize: (Long) -> String,
    formatDate: (Long) -> String
) {

    Column {

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = (level * 24).dp,
                    top = 6.dp,
                    bottom = 6.dp,
                    end = 12.dp
                ),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {

            Column {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    RegionTreeIcon(region)

                    Spacer(Modifier.width(12.dp))

                    Image(
                        painter = painterResource(Res.drawable.folder),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {

                        Text(
                            text = region.name,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Image(
                                painter = painterResource(Res.drawable.storage),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(Modifier.width(4.dp))

                            Text(
                                text = formatSize(region.size),
                                style = MaterialTheme.typography.bodySmall
                            )

                            Spacer(Modifier.width(12.dp))

                            Image(
                                painter = painterResource(Res.drawable.calendar),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(Modifier.width(4.dp))

                            Text(
                                text = formatDate(region.releaseTime),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    RegionDownloadButton(
                        region = region,
                        onClick = {
                            onDownloadClick(region)
                        }
                    )
                }

                region.downloadProgress?.let { progress ->

                    Column(
                        modifier = Modifier.padding(
                            start = 56.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        )
                    ) {

                        Text(
                            text = "Загрузка...",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { progress / 100f },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = "$progress%",
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }

        region.children.forEach { child ->

            OfflineRegionItem(
                region = child,
                level = level + 1,
                onDownloadClick = onDownloadClick,
                formatSize = formatSize,
                formatDate = formatDate
            )
        }
    }
}
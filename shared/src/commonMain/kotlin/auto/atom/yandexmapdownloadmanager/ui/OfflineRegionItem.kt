package auto.atom.yandexmapdownloadmanager.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import atomyandexmapmanager.shared.generated.resources.Res
import atomyandexmapmanager.shared.generated.resources.calendar
import atomyandexmapmanager.shared.generated.resources.folder
import atomyandexmapmanager.shared.generated.resources.map
import atomyandexmapmanager.shared.generated.resources.storage
import auto.atom.yandexmapdownloadmanager.model.OfflineRegion
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt

@Composable
fun OfflineRegionItem(
    modifier: Modifier = Modifier,
    region: OfflineRegion,
    level: Int,
    onDownloadClick: (OfflineRegion) -> Unit,
    formatSize: (Long) -> String,
    formatDate: (Long) -> String
) {

    var expanded by rememberSaveable(region.id) {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
    ) {

        Row {

            Spacer(
                modifier = Modifier.width((12 + level * 20).dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        end = 12.dp,
                        top = 6.dp,
                        bottom = 6.dp
                    ),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {

                Column {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 76.dp)
                            .padding(
                                horizontal = 16.dp,
                                vertical = 12.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        RegionTreeIcon(
                            region = region,
                            expanded = expanded,
                            onExpandedChange = {
                                expanded = it
                            }
                        )

                        Spacer(Modifier.width(8.dp))

                        val isLeaf = region.children.isEmpty()

                        Image(
                            painter = painterResource(
                                if (isLeaf) Res.drawable.map
                                else Res.drawable.folder
                            ),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            colorFilter = ColorFilter.tint(
                                if (isLeaf)
                                    MaterialTheme.colorScheme.secondary
                                else
                                    MaterialTheme.colorScheme.primary
                            )
                        )

                        Spacer(Modifier.width(14.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = region.name,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1
                            )

                            Spacer(Modifier.height(6.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Image(
                                    painter = painterResource(Res.drawable.storage),
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )

                                Spacer(Modifier.width(4.dp))

                                Text(
                                    text = formatSize(region.size),
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Spacer(Modifier.width(12.dp))

                                Text("•")

                                Spacer(Modifier.width(12.dp))

                                Image(
                                    painter = painterResource(Res.drawable.calendar),
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
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
                            level = level,
                            onClick = {
                                onDownloadClick(region)
                            }
                        )
                    }
                    region.downloadProgress?.let { progress ->
                        val percent = (progress * 10000).roundToInt() / 100f

                        Column(
                            modifier = Modifier.padding(
                                start = 64.dp,
                                end = 24.dp,
                                bottom = 18.dp
                            )
                        ) {

                            Text(
                                text = "Загрузка...",
                                style = MaterialTheme.typography.bodySmall
                            )

                            Spacer(Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = "$percent%",
                                modifier = Modifier.align(Alignment.End),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        if (region.children.isNotEmpty()) {
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {

                Column {

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
        }
    }
}

@Composable
private fun TreeIndent(
    level: Int,
    expanded: Boolean,
    hasChildren: Boolean
) {

    val width = (level * 32 + 36).dp

    Box(
        modifier = Modifier
            .width(width)
            .fillMaxHeight()
            .drawBehind {

                val lineColor = Color(0xFFD8DEE9)

                val step = 32.dp.toPx()
                val start = 18.dp.toPx()

                repeat(level) { index ->

                    val x = start + index * step

                    drawLine(
                        color = lineColor,
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                if (level > 0) {

                    val x = start + level * step

                    drawLine(
                        color = lineColor,
                        start = Offset(
                            x,
                            size.height / 2
                        ),
                        end = Offset(
                            x + 18.dp.toPx(),
                            size.height / 2
                        ),
                        strokeWidth = 1.dp.toPx()
                    )

                    if (hasChildren && expanded) {

                        drawLine(
                            color = lineColor,
                            start = Offset(
                                x,
                                size.height / 2
                            ),
                            end = Offset(
                                x,
                                size.height
                            ),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                }
            }
    )
}
package auto.atom.yandexmapdownloadmanager.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import atomyandexmapmanager.shared.generated.resources.Res
import atomyandexmapmanager.shared.generated.resources.chevron_right
import atomyandexmapmanager.shared.generated.resources.edit
import atomyandexmapmanager.shared.generated.resources.file_copy
import auto.atom.yandexmapdownloadmanager.formatDate
import auto.atom.yandexmapdownloadmanager.formatShortDate
import auto.atom.yandexmapdownloadmanager.localizedName
import auto.atom.yandexmapdownloadmanager.model.OfflineRegion
import auto.atom.yandexmapdownloadmanager.model.OfflineRegionState
import auto.atom.yandexmapdownloadmanager.model.RegionUpdateTask
import auto.atom.yandexmapdownloadmanager.timer.model.RegionDownloadState
import auto.atom.yandexmapdownloadmanager.timer.model.ScheduledUpdateStatus
import auto.atom.yandexmapdownloadmanager.timer.model.UpdatePolicy
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RegionUpdateItem(
    region: OfflineRegion,
    task: RegionUpdateTask,
    policy: UpdatePolicy,
    downloadState: RegionDownloadState?,
    onEditPlannedDate: (OfflineRegion) -> Unit,
    onCopyRegionPath: (OfflineRegion) -> Unit
) {

    val lastDownloadMillis =
        downloadState?.lastSuccessfulDownloadMillis ?: 0L

    val lastDownloadText =
        if (lastDownloadMillis == 0L) {
            "Никогда"
        } else {
            formatDate(lastDownloadMillis)
        }

    val nextDownloadMillis =
        downloadState?.nextPlannedDownloadMillis ?: 0L

    val status =
        when {
            nextDownloadMillis == 0L ->
                ScheduledUpdateStatus.NOW

            nextDownloadMillis <= System.currentTimeMillis() ->
                ScheduledUpdateStatus.OVERDUE

            else ->
                ScheduledUpdateStatus.PLANNED
        }

    val scheduleTitle =
        when (status) {
            ScheduledUpdateStatus.NOW ->
                "Первое обновление"

            ScheduledUpdateStatus.OVERDUE ->
                "Требуется обновление"

            ScheduledUpdateStatus.PLANNED ->
                "Запланировано"
        }

    val scheduleValue =
        when (status) {
            ScheduledUpdateStatus.NOW ->
                "не запланировано"

            ScheduledUpdateStatus.OVERDUE ->
                "с ${formatShortDate(nextDownloadMillis)}"

            ScheduledUpdateStatus.PLANNED ->
                formatShortDate(nextDownloadMillis)
        }

    val tooltipText =
        when (status) {
            ScheduledUpdateStatus.NOW ->
                "Регион еще ни разу не обновлялся автоматически. Первое обновление будет выполнено при ближайшей автоматической проверке."

            ScheduledUpdateStatus.OVERDUE ->
                "Дата планового обновления уже прошла. Регион будет обновлен при следующей автоматической проверке."

            ScheduledUpdateStatus.PLANNED ->
                "Автоматическое обновление региона запланировано на указанную дату."
        }

    var showProgress by remember(region.id) {
        mutableStateOf(false)
    }

    var progressWasVisible by remember(region.id) {
        mutableStateOf(false)
    }

    LaunchedEffect(region.state, region.fileCopyProgress) {

        val copyProgress = region.fileCopyProgress
        val isCopying = (copyProgress?.progress ?: 1f) < 1f

        when {
            isCopying -> {
                showProgress = true
                progressWasVisible = true
            }

            region.state == OfflineRegionState.DOWNLOADING ||
                    region.state == OfflineRegionState.PAUSED -> {
                showProgress = true
                progressWasVisible = true
            }

            progressWasVisible -> {
                delay(700)
                showProgress = false
                progressWasVisible = false
            }

            else -> {
                showProgress = false
                progressWasVisible = false
            }
        }
    }

    val containerColor =
        when (region.state) {
            OfflineRegionState.DOWNLOADING ->
                MaterialTheme.colorScheme.primaryContainer

            OfflineRegionState.PAUSED ->
                MaterialTheme.colorScheme.secondaryContainer

            else ->
                MaterialTheme.colorScheme.surface
        }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        )
    ) {

        Column {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val visible =
                    region.state == OfflineRegionState.COMPLETED ||
                            region.state == OfflineRegionState.OUTDATED

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    IconButton(
                        onClick = {
                            onCopyRegionPath(region)
                        },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.file_copy),
                            contentDescription = "Скопировать регион",
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.size(20.dp)
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = task.region.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2
                    )

                    Text(
                        text = policy.name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    InfoRow(
                        title = "Статус карты",
                        value = task.region.state.localizedName()
                    )

                    InfoRow(
                        title = "Версия карты",
                        value = formatShortDate(task.region.releaseTime)
                    )

                    InfoRow(
                        title = "Последнее обновление",
                        value = lastDownloadText
                    )

                    TooltipArea(
                        tooltip = {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                tonalElevation = 4.dp
                            ) {
                                Text(
                                    text = tooltipText,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                text = scheduleTitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                Text(
                                    text = scheduleValue,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )

                                if (status == ScheduledUpdateStatus.PLANNED) {

                                    IconButton(
                                        onClick = {
                                            onEditPlannedDate(region)
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            painter = painterResource(Res.drawable.edit),
                                            contentDescription = "Изменить дату",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Icon(
                    painter = painterResource(Res.drawable.chevron_right),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
            }

            AnimatedVisibility(
                visible = showProgress,
                enter = fadeIn(),
                exit = fadeOut() + shrinkVertically()
            ) {

                val progress =
                    region.fileCopyProgress?.progress
                        ?: region.downloadProgress
                        ?: 0f

                val percent = (progress * 10000).roundToInt() / 100f
                val copyProgress = region.fileCopyProgress
                val isCopying = (copyProgress?.progress ?: 1f) < 1f

                Column(
                    modifier = Modifier.padding(
                        start = 76.dp,
                        end = 24.dp,
                        bottom = 18.dp
                    )
                ) {

                    Text(
                        text = if (isCopying)
                            "Копирование файлов карты..."
                        else
                            "Загрузка...",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(
                        modifier = Modifier.size(6.dp)
                    )

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(
                        modifier = Modifier.size(4.dp)
                    )

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

@Composable
private fun InfoRow(
    title: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
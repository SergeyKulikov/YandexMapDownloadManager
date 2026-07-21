package auto.atom.yandexmapdownloadmanager.timer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragData
import androidx.compose.ui.draganddrop.dragData
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import atomyandexmapmanager.shared.generated.resources.Res
import atomyandexmapmanager.shared.generated.resources.delete
import atomyandexmapmanager.shared.generated.resources.edit
import org.jetbrains.compose.resources.painterResource


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimerListItem(
    title: String,
    periodDays: Int,
    regionCount: Int,
    selected: Boolean,
    onClick: () -> Unit,
    onEdit: (() -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onRegionDropped: ((regionId: Int, regionName: String) -> Unit)? = null
) {

    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()

    Box {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .hoverable(interactionSource)
                .dragAndDropTarget(
                    shouldStartDragAndDrop = { true },
                    target = object : DragAndDropTarget {

                        override fun onDrop(event: DragAndDropEvent): Boolean {
                            val dragData = event.dragData()
                            if (dragData !is DragData.Text) {
                                return false
                            }

                            val text = dragData.readText()
                            val parts = text.split('|', limit = 2)

                            if (parts.size != 2) {
                                return false
                            }

                            val regionId = parts[0].toIntOrNull() ?: return false
                            onRegionDropped?.invoke(regionId, parts[1])

                            return true
                        }
                    }
                )
                .background(
                    color =
                        if (selected)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable(onClick = onClick)
                .padding(
                    horizontal = 14.dp,
                    vertical = 10.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    maxLines = 1,
                    fontWeight =
                        if (selected)
                            FontWeight.SemiBold
                        else
                            FontWeight.Normal
                )

                Text(
                    text = "$periodDays дней",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                if (onEdit != null && hovered) {
                    IconButton(
                        onClick = onEdit
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.edit),
                            contentDescription = "Редактировать период",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (onDelete != null && hovered) {
                    IconButton(
                        onClick = onDelete
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.delete),
                            contentDescription = "Удалить период",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }

        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-12).dp, y = (-8).dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            tonalElevation = 6.dp,
            shadowElevation = 6.dp
        ) {
            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = regionCount.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
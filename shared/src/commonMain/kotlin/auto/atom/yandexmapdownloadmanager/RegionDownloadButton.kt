package auto.atom.yandexmapdownloadmanager

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import atomyandexmapmanager.shared.generated.resources.Res
import atomyandexmapmanager.shared.generated.resources.delete
import atomyandexmapmanager.shared.generated.resources.download
import atomyandexmapmanager.shared.generated.resources.pause
import auto.atom.yandexmapdownloadmanager.map.OfflineRegion
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun RegionDownloadButton(
    region: OfflineRegion,
    onClick: () -> Unit
) {

    when (region.state) {

        RegionState.NOT_DOWNLOADED -> {

            RegionActionButton(
                text = "Скачать",
                icon = Res.drawable.download,
                color = MaterialTheme.colorScheme.primary,
                onClick = onClick
            )
        }

        RegionState.DOWNLOADING -> {

            RegionActionButton(
                text = "Отмена",
                icon = Res.drawable.pause,
                color = MaterialTheme.colorScheme.tertiary,
                onClick = onClick
            )
        }

        RegionState.DOWNLOADED -> {

            RegionActionButton(
                text = "Удалить",
                icon = Res.drawable.delete,
                color = MaterialTheme.colorScheme.error,
                onClick = onClick
            )
        }

        RegionState.PAUSED -> {

            Button(
                onClick = onClick,
                modifier = Modifier
                    .height(36.dp)
                    .defaultMinSize(minWidth = 96.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {

                Text(
                    text = "Продолжить",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        RegionState.ERROR -> {

            Button(
                onClick = onClick,
                modifier = Modifier
                    .height(36.dp)
                    .defaultMinSize(minWidth = 96.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {

                Text(
                    text = "Повторить",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun RegionActionButton(
    text: String,
    icon: DrawableResource,
    color: Color,
    onClick: () -> Unit
) {

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = color
        ),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        modifier = Modifier
            .height(44.dp)
            .width(170.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(color)
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Text(
                text = text,
                color = color,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}
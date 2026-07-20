package auto.atom.yandexmapdownloadmanager.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import atomyandexmapmanager.shared.generated.resources.cloud_download
import atomyandexmapmanager.shared.generated.resources.delete
import atomyandexmapmanager.shared.generated.resources.pause
import auto.atom.yandexmapdownloadmanager.model.OfflineRegion
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun RegionDownloadButton(
    region: OfflineRegion,
    level: Int,
    onClick: () -> Unit
) {

    val updateColor = Color(0xFF7C3AED)

    val normalColor =
        if (level == 0)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.secondary

    when (region.state) {

        OfflineRegionState.AVAILABLE -> {

            RegionActionButton(
                text = "Скачать",
                icon = Res.drawable.cloud_download,
                color = normalColor,
                enabled = true,
                onClick = onClick
            )
        }

        OfflineRegionState.DOWNLOADING -> {

            RegionActionButton(
                text = "Остановить",
                icon = Res.drawable.pause,
                color = normalColor,
                enabled = true,
                onClick = onClick
            )
        }

        OfflineRegionState.PAUSED -> {

            RegionActionButton(
                text = "Продолжить",
                icon = Res.drawable.cloud_download,
                color = normalColor,
                enabled = true,
                onClick = onClick
            )
        }

        OfflineRegionState.COMPLETED -> {

            RegionActionButton(
                text = "Скачано",
                icon = Res.drawable.cloud_download,
                color = MaterialTheme.colorScheme.outline,
                enabled = false,
                onClick = {}
            )
        }

        OfflineRegionState.OUTDATED -> {

            RegionActionButton(
                text = "Обновить",
                icon = Res.drawable.cloud_download,
                color = updateColor,
                enabled = true,
                isUpdateButton = true,
                onClick = onClick
            )
        }

        OfflineRegionState.NEED_UPDATE -> {

            RegionActionButton(
                text = "Обновить",
                icon = Res.drawable.cloud_download,
                color = updateColor,
                enabled = true,
                isUpdateButton = true,
                onClick = onClick
            )
        }

        OfflineRegionState.UNSUPPORTED -> {

            RegionActionButton(
                text = "Не поддерживается",
                icon = Res.drawable.cloud_download,
                color = MaterialTheme.colorScheme.outline,
                enabled = false,
                onClick = {}
            )
        }
    }
}

@Composable
private fun RegionActionButton(
    text: String,
    icon: DrawableResource,
    color: Color,
    enabled: Boolean,
    isUpdateButton: Boolean = false,
    onClick: () -> Unit
) {

    val backgroundColor =
        if (enabled && isUpdateButton)
            color
        else
            MaterialTheme.colorScheme.surface

    val contentColor =
        when {
            !enabled ->
                MaterialTheme.colorScheme.outline

            isUpdateButton ->
                Color.White

            else ->
                color
        }

    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.dp,
            color = color
        ),
        color = backgroundColor,
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
                colorFilter = ColorFilter.tint(contentColor)
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Text(
                text = text,
                color = contentColor,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}
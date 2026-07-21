package auto.atom.yandexmapdownloadmanager.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import atomyandexmapmanager.shared.generated.resources.Res
import atomyandexmapmanager.shared.generated.resources.chevron_right
import atomyandexmapmanager.shared.generated.resources.cloud_download
import atomyandexmapmanager.shared.generated.resources.system_update
import auto.atom.yandexmapdownloadmanager.model.RegionUpdateTask
import auto.atom.yandexmapdownloadmanager.model.UpdateReason
import org.jetbrains.compose.resources.painterResource

@Composable
fun RegionUpdateItem(
    task: RegionUpdateTask
) {

    val icon: Painter
    val textColor: Color
    val description: String

    when (task.reason) {

        UpdateReason.NOT_DOWNLOADED -> {
            icon = painterResource(Res.drawable.cloud_download)
            textColor = Color(0xFFFF8C00)
            description = "Не загружен"
        }

        UpdateReason.NEW_VERSION_AVAILABLE -> {
            icon = painterResource(Res.drawable.system_update)
            textColor = MaterialTheme.colorScheme.primary
            description = "Доступно обновление"
        }
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
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = textColor
            )

            Spacer(
                modifier = Modifier.size(20.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Text(
                    text = task.region.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
            }

            Icon(
                painter = painterResource(Res.drawable.chevron_right),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}
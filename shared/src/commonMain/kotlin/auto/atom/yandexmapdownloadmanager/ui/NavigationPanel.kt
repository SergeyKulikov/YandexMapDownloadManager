package auto.atom.yandexmapdownloadmanager.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import atomyandexmapmanager.shared.generated.resources.Res
import atomyandexmapmanager.shared.generated.resources.connected
import atomyandexmapmanager.shared.generated.resources.disconnected
import atomyandexmapmanager.shared.generated.resources.error
import atomyandexmapmanager.shared.generated.resources.folder
import atomyandexmapmanager.shared.generated.resources.handmade
import atomyandexmapmanager.shared.generated.resources.server
import atomyandexmapmanager.shared.generated.resources.system_update
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun NavigationPanel(
    uiState: MainUiState,
    viewModel: MainViewModel
) {

    Surface(
        modifier = Modifier
            .width(250.dp)
            .fillMaxHeight(),
        tonalElevation = 3.dp
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    painter = painterResource(Res.drawable.error),
                    contentDescription = null,
                    modifier = Modifier.size(42.dp)
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    "Yandex Map\nDownload Manager",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                "РАЗДЕЛЫ",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(Modifier.height(8.dp))

            NavigationItem(
                icon = Res.drawable.server,
                title = "Сервер",
                selected = uiState.screen == Screen.SERVER
            ) {
                viewModel.showScreen(Screen.SERVER)
            }

            NavigationItem(
                icon = Res.drawable.handmade,
                title = "Ручное обновление",
                selected = uiState.screen == Screen.MAPS_HANDMADE
            ) {
                viewModel.showScreen(Screen.MAPS_HANDMADE)
            }

            NavigationItem(
                icon = Res.drawable.system_update,
                title = "Автоматическое обновление",
                selected = uiState.screen == Screen.MAPS_AUTO
            ) {
                viewModel.showScreen(Screen.MAPS_AUTO)
            }

            Spacer(Modifier.weight(1f))

            HorizontalDivider()

            Spacer(Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    painter = painterResource(
                        if (uiState.isClientConnected)
                            Res.drawable.connected
                        else
                            Res.drawable.disconnected
                    ),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(Modifier.width(8.dp))

                Column {

                    Text(
                        if (uiState.isClientConnected)
                            "Android подключен"
                        else
                            "Нет подключения"
                    )

                    Text(
                        "Порт ${uiState.serverPort}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationItem(
    icon: DrawableResource,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor =
                if (selected)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    Color.Transparent
        )
    ) {

        Row(
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 12.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(28.dp)
                    .background(
                        if (selected)
                            MaterialTheme.colorScheme.primary
                        else
                            Color.Transparent,
                        RoundedCornerShape(100)
                    )
            )

            Spacer(Modifier.width(12.dp))

            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint =
                    if (selected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.width(14.dp))

            Text(
                text = title,
                fontWeight =
                    if (selected)
                        FontWeight.SemiBold
                    else
                        FontWeight.Normal
            )
        }
    }
}
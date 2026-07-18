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
import androidx.compose.foundation.shape.CircleShape
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
            .width(350.dp)
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
                    text = "Yandex Map\nDownload Manager",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text = "РАЗДЕЛЫ",
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

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
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
                    modifier = Modifier.size(48.dp)
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Android",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (uiState.isClientConnected)
                        "Клиент подключен"
                    else
                        "Ожидание подключения",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (uiState.isClientConnected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                )

                Spacer(Modifier.height(18.dp))

                HorizontalDivider()

                Spacer(Modifier.height(18.dp))

                ConnectionRow(
                    title = "Статус",
                    value = if (uiState.isClientConnected)
                        "Online"
                    else
                        "Offline",
                    color = if (uiState.isClientConnected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error
                )

                Spacer(Modifier.height(10.dp))

                ConnectionRow(
                    title = "Порт",
                    value = uiState.serverPort.toString()
                )
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


@Composable
private fun ConnectionRow(
    title: String,
    value: String,
    color: Color = MaterialTheme.colorScheme.onSurface
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(10.dp)
                .background(
                    color = color,
                    shape = CircleShape
                )
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = value,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}
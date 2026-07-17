package auto.atom.yandexmapdownloadmanager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun AndroidApp() {

    val viewModel = remember { AndroidViewModel() }
    val uiState by viewModel.uiState.collectAsState()

    val scope = rememberCoroutineScope()

    MaterialTheme {

        Surface(
            modifier = Modifier.fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .safeContentPadding()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "🗺",
                            fontSize = 52.sp
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = "Yandex Map",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Downloader",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(Modifier.height(24.dp))

                        Divider()

                        Spacer(Modifier.height(20.dp))

                        StatusRow(
                            title = "Статус",
                            value = uiState.status,
                            color = when {
                                uiState.isConnected ->
                                    MaterialTheme.colorScheme.primary

                                uiState.status.contains("Ошибка", true) ||
                                        uiState.status.contains("unreachable", true) ->
                                    MaterialTheme.colorScheme.error

                                else ->
                                    MaterialTheme.colorScheme.onSurface
                            }
                        )

                        Spacer(Modifier.height(12.dp))

                        StatusRow(
                            title = "Сервер",
                            value = if (uiState.isConnected)
                                "Подключен"
                            else
                                "Не подключен"
                        )

                        Spacer(Modifier.height(12.dp))

                        StatusRow(
                            title = "Операция",
                            value = uiState.operation
                        )

                        uiState.progress?.let { progress ->

                            Spacer(Modifier.height(20.dp))

                            LinearProgressIndicator(
                                progress = { progress / 100f },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(Modifier.height(6.dp))

                            Text("$progress%")
                        }

                        Spacer(Modifier.height(28.dp))

                        if (uiState.isBusy) {

                            CircularProgressIndicator()

                            Spacer(Modifier.height(20.dp))
                        }

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !uiState.isBusy,
                            onClick = {

                                scope.launch {

                                    if (uiState.isConnected) {

                                        viewModel.setStatus("Отключение...")

                                        viewModel.disconnect()

                                    } else {

                                        viewModel.setStatus("Подключение...")

                                        viewModel.connect()
                                    }
                                }
                            }
                        ) {

                            Text(
                                when {

                                    uiState.isBusy && uiState.isConnected ->
                                        "Отключение..."

                                    uiState.isBusy ->
                                        "Подключение..."

                                    uiState.isConnected ->
                                        "Отключиться"

                                    else ->
                                        "Подключиться"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusRow(
    title: String,
    value: String,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )

            Spacer(Modifier.size(10.dp))

            Text(
                text = title,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = value,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}
package auto.atom.yandexmapdownloadmanager

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeContentPadding()
                    .padding(24.dp),

                verticalArrangement = Arrangement.spacedBy(16.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Yandex Map Downloader",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Text("Статус: ${uiState.status}")

                Text(
                    if (uiState.isConnected)
                        "Сервер: подключен"
                    else
                        "Сервер: не подключен"
                )

                Text("Операция: ${uiState.operation}")

                if (uiState.isConnected) {
                    uiState.progress?.let { progress ->

                        LinearProgressIndicator(
                            progress = { progress / 100f },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("$progress%")
                    }
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isBusy,
                    onClick = {

                        scope.launch {

                            if (uiState.isConnected) {

                                // Пользователь сразу увидит действие
                                viewModel.setStatus("Отключение...")

                                viewModel.disconnect()

                            } else {

                                // Пользователь сразу увидит действие
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
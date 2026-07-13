package auto.atom.yandexmapdownloadmanager

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun App(viewModel: MainViewModel) {

    // val viewModel = androidx.compose.runtime.remember { MainViewModel() }
    val uiState by viewModel.uiState.collectAsState()

    MaterialTheme {

        Surface(
            modifier = Modifier.fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .safeContentPadding()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Yandex Map Download Manager",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Статус: ${uiState.status}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = "Порт: ${uiState.serverPort}"
                )

                Text(
                    text = if (uiState.isClientConnected)
                        "Клиент: подключен"
                    else
                        "Клиент: отсутствует"
                )

                Text(
                    text = "Операция: ${uiState.operation}"
                )

                uiState.progress?.let { progress ->
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("$progress%")
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isBusy,
                    onClick = {
                        if (uiState.isServerRunning) {
                            viewModel.stopServer()
                        } else {
                            viewModel.startServer()
                        }
                    }
                ) {
                    Text(
                        when {
                            uiState.isBusy && uiState.isServerRunning ->
                                "Остановка..."

                            uiState.isBusy ->
                                "Запуск..."

                            uiState.isServerRunning ->
                                "Остановить сервер"

                            else ->
                                "Запустить сервер"
                        }
                    )
                }
            }
        }
    }
}
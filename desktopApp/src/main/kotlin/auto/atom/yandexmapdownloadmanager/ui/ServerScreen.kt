package auto.atom.yandexmapdownloadmanager.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import atomyandexmapmanager.shared.generated.resources.Res
import atomyandexmapmanager.shared.generated.resources.folder
import org.jetbrains.compose.resources.painterResource

/**
 * Экран запуска сервера и ожидания подключения Android-устройства.
 */
@Composable
fun ServerScreen(
    viewModel: MainViewModel
) {

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .safeContentPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier.widthIn(max = 500.dp),
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
                text = if (uiState.isClientConnected)
                    "Клиент: подключен"
                else
                    "Клиент: отсутствует"
            )

            val defaultCopyDirectory by viewModel.defaultCopyDirectory.collectAsState()

            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = defaultCopyDirectory,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                label = {
                    Text("Каталог копирования карт")
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            viewModel.selectCopyDirectory()
                        }
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.folder),
                            contentDescription = "Выбрать папку"
                        )
                    }
                }
            )


            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isBusy,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.isServerRunning) {
                        Color(0xFF4CAF50)
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                ),
                onClick = {
                    if (uiState.isServerRunning)
                        viewModel.stopServer()
                    else
                        viewModel.startServer()
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
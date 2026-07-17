package auto.atom.yandexmapdownloadmanager.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import auto.atom.yandexmapdownloadmanager.model.buildUpdateQueue

@Composable
fun StatusBar(
    viewModel: MainViewModel
) {

    val uiState by viewModel.uiState.collectAsState()
    val regions by viewModel.regions.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            if (uiState.isServerRunning)
                "Сервер запущен"
            else
                "Сервер остановлен"
        )

        Spacer(Modifier.width(16.dp))

        Text(
            if (uiState.isClientConnected)
                "Android подключен"
            else
                "Нет подключения"
        )

        Spacer(Modifier.weight(1f))

        Text("Порт ${uiState.serverPort}")

        Spacer(Modifier.width(16.dp))

        Text("Регионов: ${regions.size}")

        Spacer(Modifier.width(16.dp))

        Text("К обновлению: ${regions.buildUpdateQueue().size}")
    }
}
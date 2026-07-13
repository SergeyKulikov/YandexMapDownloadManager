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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun AndroidApp() {

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

                Text("Статус: Ожидание подключения")

                Text("Сервер: не подключен")

                Text("Операция: отсутствует")

                LinearProgressIndicator(
                    progress = { 0f },
                    modifier = Modifier.fillMaxWidth()
                )

                val viewModel = remember { AndroidViewModel() }
                val scope = rememberCoroutineScope()

                Button(
                    onClick = {
                        scope.launch {
                            viewModel.connect()
                        }
                    }
                ) {
                    Text("Подключиться")
                }
            }
        }
    }
}
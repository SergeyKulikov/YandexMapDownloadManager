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
import auto.atom.yandexmapdownloadmanager.ui.theme.YandexMapManagerTheme

@Composable
fun App(viewModel: MainViewModel) {

    val uiState by viewModel.uiState.collectAsState()

    YandexMapManagerTheme {

        Surface(
            modifier = Modifier.fillMaxSize()
        ) {

            when (uiState.screen) {

                Screen.SERVER ->
                    ServerScreen(viewModel)

                Screen.MAPS ->
                    MapsScreen(viewModel)
            }
        }
    }
}
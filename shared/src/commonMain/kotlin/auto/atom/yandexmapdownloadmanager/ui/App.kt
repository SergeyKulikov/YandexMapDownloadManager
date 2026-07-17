package auto.atom.yandexmapdownloadmanager.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
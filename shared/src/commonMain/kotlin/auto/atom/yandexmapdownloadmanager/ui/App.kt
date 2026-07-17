package auto.atom.yandexmapdownloadmanager.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import auto.atom.yandexmapdownloadmanager.ui.theme.YandexMapManagerTheme

@Composable
fun App(
    viewModel: MainViewModel
) {

    YandexMapManagerTheme {

        Surface(
            modifier = Modifier.fillMaxSize()
        ) {

            MainScreen(viewModel)
        }
    }
}
package auto.atom.yandexmapdownloadmanager.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import auto.atom.yandexmapdownloadmanager.ui.MainViewModel
import auto.atom.yandexmapdownloadmanager.theme.YandexMapManagerTheme

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
package auto.atom.yandexmapdownloadmanager.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import auto.atom.yandexmapdownloadmanager.timer.ui.TimerScreen
import auto.atom.yandexmapdownloadmanager.ui.MainViewModel
import auto.atom.yandexmapdownloadmanager.ui.Screen

/**
 * Контейнер содержимого главного окна.
 *
 * В зависимости от выбранного раздела отображает соответствующую страницу.
 */
@Composable
fun Content(
    viewModel: MainViewModel
) {

    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        when (uiState.screen) {

            Screen.SERVER ->
                ServerScreen(viewModel)

            Screen.MAPS_HANDMADE ->
                HandmadeMapsScreen(viewModel)

            Screen.MAPS_AUTO ->
                AutoMapsScreen(viewModel)

            Screen.TIMERS -> {
                TimerScreen(viewModel)
            }
        }
    }
}
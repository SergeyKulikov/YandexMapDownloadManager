package auto.atom.yandexmapdownloadmanager.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

@Composable
fun MapsScreen(
    viewModel: MainViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isClientConnected) {
        if (!uiState.isClientConnected) {
            viewModel.showServerScreen()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        PrimaryScrollableTabRow(
            selectedTabIndex = when (uiState.screen) {
                Screen.MAPS_HANDMADE -> 0
                Screen.MAPS_AUTO -> 1
                else -> 0
            }
        ) {

            Tab(
                selected = uiState.screen == Screen.MAPS_HANDMADE,
                onClick = {
                    viewModel.showHandmadeMapsScreen()
                },
                text = {
                    Text("Ручное обновление")
                }
            )

            Tab(
                selected = uiState.screen == Screen.MAPS_AUTO,
                onClick = {
                    viewModel.showAutoMapsScreen()
                },
                text = {
                    Text("Автоматическое обновление")
                }
            )
        }

        when (uiState.screen) {

            Screen.MAPS_HANDMADE ->
                HandmadeMapsScreen(viewModel)

            Screen.MAPS_AUTO ->
                AutoMapsScreen(viewModel)

            else -> {}
        }
    }
}
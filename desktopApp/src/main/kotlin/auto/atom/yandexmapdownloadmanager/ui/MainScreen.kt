package auto.atom.yandexmapdownloadmanager.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import auto.atom.yandexmapdownloadmanager.ui.MainViewModel

@Composable
fun MainScreen(
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

        Row(
            modifier = Modifier.weight(1f)
        ) {

            NavigationPanel(
                uiState = uiState,
                viewModel = viewModel
            )

            VerticalDivider()

            Content(viewModel)
        }
    }
}
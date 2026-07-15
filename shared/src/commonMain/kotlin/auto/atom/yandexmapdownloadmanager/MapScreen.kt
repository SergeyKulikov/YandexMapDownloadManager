package auto.atom.yandexmapdownloadmanager

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Экран работы с офлайн-картами.
 */
@Composable
fun MapsScreen(
    viewModel: MainViewModel
) {

    val uiState by viewModel.uiState.collectAsState()
    val regions by viewModel.regions.collectAsState()

    LaunchedEffect(uiState.isClientConnected) {
        if (!uiState.isClientConnected) {
            viewModel.showServerScreen()
        }
    }

    val countries = remember(regions) {
        regions
            .map { it.country }
            .distinct()
            .sorted()
    }

    var selectedTab by remember(countries) {
        mutableIntStateOf(0)
    }

    if (selectedTab >= countries.size) {
        selectedTab = 0
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        if (countries.isNotEmpty()) {

            PrimaryScrollableTabRow(
                selectedTabIndex = selectedTab
            ) {

                countries.forEachIndexed { index, country ->

                    Tab(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                        },
                        text = {
                            Text(country)
                        }
                    )
                }
            }

            val selectedCountry = countries[selectedTab]

            val countryRegions = remember(regions, selectedCountry) {
                regions.filter { it.country == selectedCountry }
            }

            Column(
                modifier = Modifier.padding(24.dp)
            ) {

                Text(
                    text = selectedCountry,
                    style = MaterialTheme.typography.headlineSmall
                )

                Text(
                    text = "Регионов: ${countryRegions.size}",
                    modifier = Modifier.padding(top = 16.dp)
                )

                Text(
                    text = "Список регионов будет добавлен следующим шагом.",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

        } else {

            Column(
                modifier = Modifier.padding(24.dp)
            ) {

                Text("Нет доступных стран")
            }
        }
    }
}
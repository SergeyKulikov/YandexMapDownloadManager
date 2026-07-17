package auto.atom.yandexmapdownloadmanager

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import auto.atom.yandexmapdownloadmanager.protocol.CountryGeoID

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
            .mapNotNull { region ->
                region.parentId?.let { CountryGeoID.fromId(it) }
            }
            .distinctBy { it.id }
            .sortedBy { it.localizedName }
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
                            Text(country.localizedName)
                        }
                    )
                }
            }

            val selectedCountry = countries[selectedTab]

            val countryRegions = remember(regions, selectedCountry) {
                regions.filter {
                    it.parentId == selectedCountry.id
                }
            }

            OfflineRegionTree(
                regions = countryRegions,
                onDownloadClick = { region ->
                    viewModel.downloadRegionById(region.id)
                },
                formatSize = ::formatSize,
                formatDate = ::formatDate
            )
        } else {

            Column(
                modifier = Modifier.padding(24.dp)
            ) {

                Text("Нет доступных стран")
            }
        }
    }
}
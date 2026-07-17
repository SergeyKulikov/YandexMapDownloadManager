package auto.atom.yandexmapdownloadmanager.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import auto.atom.yandexmapdownloadmanager.model.RegionUpdateTask
import auto.atom.yandexmapdownloadmanager.model.UpdateReason

@Composable
fun RegionUpdateItem(
    task: RegionUpdateTask
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {

        Column(
            modifier = Modifier.padding(12.dp)
        ) {

            Text(task.region.name)

            Text(
                when (task.reason) {
                    UpdateReason.NOT_DOWNLOADED ->
                        "Не загружен"

                    UpdateReason.NEW_VERSION_AVAILABLE ->
                        "Доступно обновление"
                }
            )
        }
    }
}
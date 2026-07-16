package auto.atom.yandexmapdownloadmanager

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import auto.atom.yandexmapdownloadmanager.map.OfflineRegion

@Composable
fun RegionDownloadButton(
    region: OfflineRegion,
    onClick: () -> Unit
) {

    val (text, colors) = when (region.state) {

        RegionState.NOT_DOWNLOADED ->
            "Скачать" to ButtonDefaults.buttonColors()

        RegionState.DOWNLOADING ->
            "Отмена" to ButtonDefaults.outlinedButtonColors()

        RegionState.DOWNLOADED ->
            "Удалить" to ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )

        RegionState.PAUSED ->
            "Продолжить" to ButtonDefaults.buttonColors()

        RegionState.ERROR ->
            "Повторить" to ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
    }

    Button(
        onClick = onClick,
        colors = colors
    ) {
        Text(text)
    }
}
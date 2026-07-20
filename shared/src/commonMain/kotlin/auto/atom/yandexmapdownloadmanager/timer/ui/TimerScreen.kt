package auto.atom.yandexmapdownloadmanager.timer.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TimerScreen() {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {

        TimerList(
            modifier = Modifier
                .width(260.dp)
                .fillMaxHeight()
        )

        HorizontalDivider()

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            RegionAssignmentPanel()
        }
    }
}
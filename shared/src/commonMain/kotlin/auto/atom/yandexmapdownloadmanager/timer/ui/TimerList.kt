package auto.atom.yandexmapdownloadmanager.timer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TimerList(
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.padding(16.dp)
    ) {

        Text(
            text = "Периоды обновления",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(16.dp))

        HorizontalDivider()

        Spacer(Modifier.height(12.dp))

        TimerListItem(
            title = "🟣 14 дней",
            selected = true
        )

        TimerListItem(
            title = "🟢 1 месяц",
            selected = false
        )

        TimerListItem(
            title = "🟡 3 месяца",
            selected = false
        )

        TimerListItem(
            title = "⚪ Не назначено",
            selected = false
        )

        Spacer(Modifier.weight(1f))

        HorizontalDivider()

        Spacer(Modifier.height(12.dp))

        Text(
            text = "➕ Добавить период",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
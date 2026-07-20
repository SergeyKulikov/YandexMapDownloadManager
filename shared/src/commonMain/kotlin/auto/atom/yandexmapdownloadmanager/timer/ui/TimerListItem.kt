package auto.atom.yandexmapdownloadmanager.timer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TimerListItem(
    title: String,
    selected: Boolean
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (selected)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surface,
                RoundedCornerShape(12.dp)
            )
            .clickable { }
            .padding(
                horizontal = 14.dp,
                vertical = 12.dp
            )
    ) {

        Text(
            text = title,
            fontWeight =
                if (selected)
                    FontWeight.SemiBold
                else
                    FontWeight.Normal
        )
    }
}
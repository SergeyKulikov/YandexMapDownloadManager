package auto.atom.yandexmapdownloadmanager.timer.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTransferAction
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.DragAndDropTransferable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import auto.atom.yandexmapdownloadmanager.timer.model.RegionTransferData
import java.awt.datatransfer.StringSelection

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RegionTransferItem(
    region: RegionTransferData,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier
            .dragAndDropSource {
                DragAndDropTransferData(
                    transferable = DragAndDropTransferable(
                        StringSelection("${region.id}|${region.name}")
                    ),
                    supportedActions = listOf(
                        DragAndDropTransferAction.Copy
                    )
                )
            }
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = region.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Код региона: ${region.id}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
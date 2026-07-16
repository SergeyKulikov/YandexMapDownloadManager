package auto.atom.yandexmapdownloadmanager

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import atomyandexmapmanager.shared.generated.resources.Res
import atomyandexmapmanager.shared.generated.resources.collapse
import atomyandexmapmanager.shared.generated.resources.expand
import auto.atom.yandexmapdownloadmanager.map.OfflineRegion
import org.jetbrains.compose.resources.painterResource

@Composable
fun RegionTreeIcon(
    region: OfflineRegion,
    expanded: Boolean = true,
    onExpandedChange: (Boolean) -> Unit = {}
) {

    if (region.children.isEmpty()) {
        Spacer(
            modifier = Modifier.width(40.dp)
        )
        return
    }

    IconButton(
        onClick = {
            onExpandedChange(!expanded)
        }
    ) {

        Image(
            painter = painterResource(
                if (expanded) {
                    Res.drawable.collapse
                } else {
                    Res.drawable.expand
                }
            ),
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
    }
}
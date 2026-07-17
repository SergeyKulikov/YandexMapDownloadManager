package auto.atom.yandexmapdownloadmanager.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import atomyandexmapmanager.shared.generated.resources.Res
import atomyandexmapmanager.shared.generated.resources.chevron_right
import atomyandexmapmanager.shared.generated.resources.expand_more
import auto.atom.yandexmapdownloadmanager.model.OfflineRegion
import org.jetbrains.compose.resources.painterResource

@Composable
fun RegionTreeIcon(
    region: OfflineRegion,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
) {
    if (region.children.isEmpty()) {
        Spacer(
            modifier = Modifier.width(32.dp)
        )
        return
    }

    IconButton(
        modifier = Modifier.size(32.dp),
        onClick = {
            onExpandedChange(!expanded)
        }
    ) {
        Image(
            painter = painterResource(
                if (expanded)
                    Res.drawable.expand_more
                else
                    Res.drawable.chevron_right
            ),
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
    }
}
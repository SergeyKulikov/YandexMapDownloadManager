package auto.atom.yandexmapdownloadmanager.timer.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import auto.atom.yandexmapdownloadmanager.model.OfflineRegion

class DragState {
    var region by mutableStateOf<OfflineRegion?>(null)
    var isDragging by mutableStateOf(false)
}
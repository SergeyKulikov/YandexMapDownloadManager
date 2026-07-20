package auto.atom.yandexmapdownloadmanager.timer.model

import kotlinx.serialization.Serializable

@Serializable
data class RegionAssignments(
    val assignments: Map<Int, String?> = emptyMap()
)
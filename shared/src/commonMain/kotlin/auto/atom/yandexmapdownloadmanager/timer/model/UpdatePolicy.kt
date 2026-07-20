package auto.atom.yandexmapdownloadmanager.timer.model

import kotlin.time.Duration

data class UpdatePolicy(
    val id: String,
    val name: String,
    val periodDays: Duration
)
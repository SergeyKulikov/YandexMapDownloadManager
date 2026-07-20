package auto.atom.yandexmapdownloadmanager.timer.model
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class UpdatePolicy(
    val id: String,
    val name: String,
    val periodDays: Duration
)
package auto.atom.yandexmapdownloadmanager

import auto.atom.yandexmapdownloadmanager.model.OfflineRegion
import auto.atom.yandexmapdownloadmanager.ui.OfflineRegionState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ln
import kotlin.math.pow

fun formatSize(bytes: Long): String {
    if (bytes <= 0) return "0 Б"

    val units = arrayOf("Б", "КБ", "МБ", "ГБ", "ТБ")
    val digitGroups = (ln(bytes.toDouble()) / ln(1024.0)).toInt()

    return String.format(
        Locale.getDefault(),
        "%.1f %s",
        bytes / 1024.0.pow(digitGroups.toDouble()),
        units[digitGroups]
    )
}

fun formatDate(time: Long): String {
    return SimpleDateFormat(
        "dd.MM.yyyy HH:mm",
        Locale.getDefault()
    ).format(Date(time))
}

fun OfflineRegionState.localizedName(): String =
    when (this) {
        OfflineRegionState.AVAILABLE -> "Не загружен"
        OfflineRegionState.DOWNLOADING -> "Загружается"
        OfflineRegionState.PAUSED -> "Приостановлен"
        OfflineRegionState.COMPLETED -> "Загружен"
        OfflineRegionState.OUTDATED -> "Устарел"
        OfflineRegionState.NEED_UPDATE -> "Требуется обновление"
        OfflineRegionState.UNSUPPORTED -> "Не поддерживается"
    }

fun List<OfflineRegion>.flatten(): List<OfflineRegion> {

    val result = mutableListOf<OfflineRegion>()

    this.forEach { item ->
        result.add(item.copy(children = mutableListOf()))
        result.addAll(item.children.flatten())
    }

    return result
}

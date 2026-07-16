package auto.atom.yandexmapdownloadmanager

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
        "dd.MM.yyyy",
        Locale.getDefault()
    ).format(Date(time))
}
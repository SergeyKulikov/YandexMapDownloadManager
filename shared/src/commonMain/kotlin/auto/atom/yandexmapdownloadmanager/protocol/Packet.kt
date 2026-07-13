package auto.atom.yandexmapdownloadmanager.protocol

import kotlinx.serialization.Serializable

/**
 * Транспортный контейнер протокола.
 *
 * Любое сообщение между Desktop и Android передается внутри объекта
 * `Packet`.
 *
 * Контейнер содержит:
 *
 * * версию сетевого протокола;
 * * сообщение [Message].
 *
 * Использование отдельного контейнера позволяет изменять протокол в
 * будущем, сохраняя совместимость между различными версиями приложений.
 *
 * @property version Версия протокола обмена данными.
 * @property message Передаваемое сообщение.
 */
@Serializable
data class Packet(
    val version: Int = PROTOCOL_VERSION,
    val message: Message
)

/**
 * Текущая версия сетевого протокола.
 *
 * Используется обеими сторонами соединения для проверки совместимости.
 */
const val PROTOCOL_VERSION = 1
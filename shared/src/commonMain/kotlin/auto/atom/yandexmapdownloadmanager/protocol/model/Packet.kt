package auto.atom.yandexmapdownloadmanager.protocol.model

import auto.atom.yandexmapdownloadmanager.protocol.model.Protocol.PROTOCOL_VERSION
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
 * @property protocolVersion Версия протокола обмена данными.
 * @property message Передаваемое сообщение.
 */
@Serializable
data class Packet(
    val protocolVersion: Int = PROTOCOL_VERSION,
    val message: Message
)

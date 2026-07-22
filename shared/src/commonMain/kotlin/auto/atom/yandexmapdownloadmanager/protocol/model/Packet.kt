package auto.atom.yandexmapdownloadmanager.protocol.model

import auto.atom.yandexmapdownloadmanager.protocol.model.Protocol.PROTOCOL_VERSION
import kotlinx.serialization.Serializable

/**
 * Транспортный контейнер протокола.
 *
 * Любое сообщение между Desktop и Android передается внутри объекта
 * [Packet].
 *
 * Контейнер содержит:
 * - версию сетевого протокола;
 * - передаваемое сообщение [Message].
 *
 * Если сериализованное сообщение не помещается в один сетевой пакет,
 * оно может быть разбито на несколько фрагментов. В этом случае все
 * фрагменты содержат один и тот же [message], разделенный транспортом,
 * а поле [isLastPart] указывает на последний фрагмент.
 *
 * Использование отдельного контейнера позволяет изменять протокол,
 * сохраняя совместимость между различными версиями приложений.
 *
 * @property protocolVersion Версия протокола обмена.
 * @property message Передаваемое сообщение.
 * @property fragmentIndex Порядковый номер фрагмента сообщения (начиная с 0).
 * @property isLastPart Признак того, что данный фрагмент является последним.
 */
@Serializable
data class Packet(
    val protocolVersion: Int = PROTOCOL_VERSION,
    val message: Message,
    val fragmentIndex: Int = 0,
    val isLastPart: Boolean = true
)

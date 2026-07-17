package auto.atom.yandexmapdownloadmanager.protocol.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Базовый класс всех сообщений, передаваемых между Desktop и Android.
 *
 * Любое сообщение передается внутри [Packet].
 *
 * Типичный процесс обмена сообщениями выглядит следующим образом:
 *
 * ```
 * Desktop
 *     |
 *     | Request
 *     |
 * Android
 *     |
 *     | Response
 *     |
 * Desktop
 * ```
 *
 * Для длительных операций Android может отправлять несколько сообщений
 * [Progress], после чего завершить выполнение сообщением [Response].
 *
 * ```
 * Request
 *      ↓
 * Progress(10)
 *      ↓
 * Progress(45)
 *      ↓
 * Progress(90)
 *      ↓
 * Response(OK)
 * ```
 *
 * В настоящий момент поддерживаются следующие типы сообщений:
 *
 * * [Request] — запрос на выполнение команды.
 * * [Response] — результат выполнения запроса.
 * * [Progress] — прогресс выполнения длительной операции.
 *
 * Новые типы сообщений могут быть добавлены без изменения существующего
 * сетевого протокола.
 *
 * @see Packet
 * @see Request
 * @see Response
 * @see Progress
 */
@Serializable
sealed class Message


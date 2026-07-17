package auto.atom.yandexmapdownloadmanager.transport

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.cancel
import io.ktor.utils.io.readFully
import io.ktor.utils.io.readInt
import io.ktor.utils.io.writeFully
import io.ktor.utils.io.writeInt

/**
 * Выполняет передачу сообщений по TCP-соединению.
 *
 * Каждое сообщение передается в виде отдельного фрейма:
 *
 * +-----------------+----------------------+
 * | 4 байта (Int32) | Данные сообщения     |
 * +-----------------+----------------------+
 *
 * Сначала записывается длина сообщения в байтах,
 * затем передаются сами данные.
 *
 * Такой формат позволяет принимающей стороне точно определить,
 * где заканчивается одно сообщение и начинается следующее.
 *
 * Формат фрейма является транспортным уровнем протокола
 * и не зависит от содержимого сообщения.
 * Данные внутри фрейма сериализуются отдельно
 * (например, через JSON-протокол).
 */
internal class FrameIO(
    private val input: ByteReadChannel,
    private val output: ByteWriteChannel
) {

    private companion object {

        /**
         * Максимальный размер одного фрейма.
         *
         * Ограничение защищает от некорректных данных,
         * когда удаленная сторона отправляет поврежденную
         * или ошибочную длину сообщения.
         */
        const val MAX_FRAME_SIZE = 16 * 1024 * 1024
    }

    /**
     * Записывает один фрейм.
     *
     * В TCP нет понятия границ сообщений:
     * один вызов отправки на одной стороне не гарантирует,
     * что на другой стороне он будет получен одним чтением.
     *
     * Поэтому перед данными записывается их размер.
     */
    suspend fun writeFrame(payload: ByteArray) {
        require(payload.isNotEmpty()) {
            "Cannot send empty protocol frame"
        }

        require(payload.size <= MAX_FRAME_SIZE) {
            "Frame too large: ${payload.size}"
        }

        output.writeInt(payload.size)
        output.writeFully(payload)
        output.flush()
    }

    /**
     * Считывает следующий фрейм.
     *
     * Сначала читается размер сообщения,
     * затем указанное количество байт данных.
     *
     * Метод ожидает получение полного фрейма.
     * Если соединение закрыто до получения всех данных,
     * будет выброшено исключение канала ввода.
     */
    suspend fun readFrame(): ByteArray {
        val length = input.readInt()

        require(length in 1..MAX_FRAME_SIZE) {
            "Invalid frame length: $length"
        }

        val payload = ByteArray(length)

        input.readFully(payload)

        return payload
    }

    /**
     * Закрывает каналы ввода/вывода.
     *
     * После закрытия экземпляр FrameIO больше не может
     * использоваться для передачи данных.
     */
    suspend fun close() {
        try {
            output.flush()
        } catch (_: Exception) {
            // ignore
        } finally {
            try {
                output.flushAndClose()
            } catch (_: Exception) {
                // ignore
            }

            input.cancel()
        }
    }
}
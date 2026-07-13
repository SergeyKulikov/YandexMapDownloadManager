package auto.atom.yandexmapdownloadmanager.transport

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.cancel
import io.ktor.utils.io.close
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
 */
internal class FrameIO(
    private val input: ByteReadChannel,
    private val output: ByteWriteChannel
) {

    /**
     * Записывает один фрейм.
     */
    suspend fun writeFrame(payload: ByteArray) {
        require(payload.isNotEmpty()) {
            "Frame payload cannot be empty"
        }

        output.writeInt(payload.size)
        output.writeFully(payload)
        output.flush()
    }

    /**
     * Считывает следующий фрейм.
     */
    suspend fun readFrame(): ByteArray {
        val length = input.readInt()

        require(length >= 0) {
            "Invalid frame length: $length"
        }

        val payload = ByteArray(length)
        if (length > 0) {
            input.readFully(payload)
        }
        return payload
    }

    /**
     * Закрывает каналы ввода/вывода.
     */
    suspend fun close() {
        try {
            output.flushAndClose()
        } catch (e: Exception) {
            // ignore
        }
        input.cancel()
    }
}
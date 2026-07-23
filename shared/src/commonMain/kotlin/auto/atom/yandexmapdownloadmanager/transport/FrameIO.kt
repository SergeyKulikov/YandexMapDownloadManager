package auto.atom.yandexmapdownloadmanager.transport

import auto.atom.yandexmapdownloadmanager.protocol.model.BinaryFileFrame
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.cancel
import io.ktor.utils.io.readByte
import io.ktor.utils.io.readFully
import io.ktor.utils.io.readInt
import io.ktor.utils.io.readLong
import io.ktor.utils.io.writeByte
import io.ktor.utils.io.writeFully
import io.ktor.utils.io.writeInt
import io.ktor.utils.io.writeLong

internal class FrameIO(
    private val input: ByteReadChannel,
    private val output: ByteWriteChannel
) {

    private companion object {

        const val MAX_FRAME_SIZE = 16 * 1024 * 1024

        private const val FRAME_JSON: Byte = 1
        private const val FRAME_FILE: Byte = 2
    }

    suspend fun writeFrame(
        frame: TransportFrame
    ) {
        when (frame) {

            is JsonTransportFrame -> {

                val payload = frame.payload

                require(payload.isNotEmpty())
                require(payload.size <= MAX_FRAME_SIZE)

                output.writeByte(FRAME_JSON)
                output.writeInt(payload.size)
                output.writeFully(payload)
            }

            is BinaryTransportFrame -> {

                val binary = frame.frame

                require(binary.bytes.isNotEmpty())
                require(binary.bytes.size <= MAX_FRAME_SIZE)

                val requestIdBytes =
                    binary.requestId.encodeToByteArray()

                val fileNameBytes =
                    binary.fileName.encodeToByteArray()

                output.writeByte(FRAME_FILE)

                output.writeInt(requestIdBytes.size)
                output.writeFully(requestIdBytes)

                output.writeInt(binary.regionId)

                output.writeInt(fileNameBytes.size)
                output.writeFully(fileNameBytes)

                output.writeLong(binary.offset)

                output.writeInt(binary.bytes.size)
                output.writeFully(binary.bytes)

                output.writeByte(
                    if (binary.lastPart) 1 else 0
                )
            }
        }

        output.flush()
    }

    suspend fun readFrame(): TransportFrame {

        return when (input.readByte()) {

            FRAME_JSON -> {

                val length = input.readInt()

                require(length in 1..MAX_FRAME_SIZE)

                val payload = ByteArray(length)

                input.readFully(payload)

                JsonTransportFrame(payload)
            }

            FRAME_FILE -> {

                val requestIdLength = input.readInt()

                require(requestIdLength in 1..4096)

                val requestIdBytes = ByteArray(requestIdLength)
                input.readFully(requestIdBytes)

                val regionId = input.readInt()

                val fileNameLength = input.readInt()

                require(fileNameLength in 1..4096)

                val fileNameBytes = ByteArray(fileNameLength)
                input.readFully(fileNameBytes)

                val offset = input.readLong()

                val chunkLength = input.readInt()

                require(chunkLength in 1..MAX_FRAME_SIZE)

                val bytes = ByteArray(chunkLength)
                input.readFully(bytes)

                val isLastChunk =
                    input.readByte().toInt() != 0

                BinaryTransportFrame(
                    BinaryFileFrame(
                        requestId = requestIdBytes.decodeToString(),
                        regionId = regionId,
                        fileName = fileNameBytes.decodeToString(),
                        offset = offset,
                        lastPart = isLastChunk,
                        bytes = bytes
                    )
                )
            }

            else ->
                error("Unknown frame type")
        }
    }

    suspend fun close() {
        try {
            output.flush()
        } catch (_: Exception) {
        } finally {
            try {
                output.flushAndClose()
            } catch (_: Exception) {
            }

            input.cancel()
        }
    }
}
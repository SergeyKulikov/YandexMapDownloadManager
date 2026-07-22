package auto.atom.yandexmapdownloadmanager.protocol

import auto.atom.yandexmapdownloadmanager.model.FileTransferState
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionFileChunkPayload
import java.io.File
import java.io.FileOutputStream

class RegionFileReceiver(
    private val root: File
) {

    private var output: FileOutputStream? = null

    fun onChunk(chunk: RegionFileChunkPayload) {
        when (chunk.state) {

            FileTransferState.START -> {
                output?.close()

                val file = File(
                    root,
                    "${chunk.regionId}/${chunk.fileName}"
                )

                file.parentFile.mkdirs()

                output = FileOutputStream(file)
            }

            FileTransferState.CHUNK -> {
                output!!.write(chunk.bytes)
            }

            FileTransferState.END -> {
                output?.flush()
                output?.close()
                output = null
            }
        }
    }
}
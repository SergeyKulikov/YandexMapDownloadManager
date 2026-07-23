package auto.atom.yandexmapdownloadmanager.transport

import auto.atom.yandexmapdownloadmanager.protocol.model.BinaryFileFrame

// FrameIO
sealed interface TransportFrame

data class JsonTransportFrame(
    val payload: ByteArray
) : TransportFrame

data class BinaryTransportFrame(
    val frame: BinaryFileFrame
) : TransportFrame
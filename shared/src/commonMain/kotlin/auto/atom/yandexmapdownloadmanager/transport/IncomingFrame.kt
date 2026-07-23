package auto.atom.yandexmapdownloadmanager.transport

import auto.atom.yandexmapdownloadmanager.protocol.model.BinaryFileFrame
import auto.atom.yandexmapdownloadmanager.protocol.model.Packet

sealed interface IncomingFrame

data class JsonFrame(
    val packet: Packet
) : IncomingFrame

data class BinaryFrame(
    val frame: BinaryFileFrame
) : IncomingFrame
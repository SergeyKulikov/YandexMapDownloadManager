package auto.atom.yandexmapdownloadmanager.timer.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

object DurationSerializer : KSerializer<Duration> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor(
            serialName = "kotlin.time.Duration",
            kind = PrimitiveKind.LONG
        )

    override fun serialize(
        encoder: Encoder,
        value: Duration
    ) {
        encoder.encodeLong(value.inWholeMilliseconds)
    }

    override fun deserialize(decoder: Decoder): Duration {
        return decoder.decodeLong().milliseconds
    }
}
package auto.atom.yandexmapdownloadmanager.transport


import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.serializer

/**
 * Настройки сериализации сетевого протокола.
 *
 * Один и тот же экземпляр используется Desktop и Android,
 * что гарантирует одинаковое поведение сериализации и
 * десериализации сообщений.
 */
val ProtocolJson = Json {

    /**
     * Игнорировать неизвестные поля.
     *
     * Это позволяет новым версиям приложения работать
     * со старыми версиями протокола.
     */
    ignoreUnknownKeys = true

    /**
     * Сериализовать значения по умолчанию.
     */
    encodeDefaults = true

    /**
     * Поле, содержащее тип наследника Message.
     */
    classDiscriminator = "type"
}

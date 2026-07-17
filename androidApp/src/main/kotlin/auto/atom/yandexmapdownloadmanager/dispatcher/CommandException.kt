package auto.atom.yandexmapdownloadmanager.dispatcher

open class CommandException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
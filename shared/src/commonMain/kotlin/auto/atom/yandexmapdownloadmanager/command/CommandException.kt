package auto.atom.yandexmapdownloadmanager.command

open class CommandException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
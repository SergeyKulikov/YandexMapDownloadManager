package auto.atom.yandexmapdownloadmanager.ui

/**
 * Состояние пользовательского интерфейса Android-приложения.
 */
data class AndroidUiState(

    /**
     * Подключено ли приложение к Desktop-серверу.
     */
    val isConnected: Boolean = false,

    /**
     * Текущий статус подключения.
     */
    val status: String = "Не подключено",

    /**
     * Текущая выполняемая операция.
     */
    val operation: String = "Отсутствует",

    /**
     * Прогресс выполнения операции.
     *
     * Значение от 0 до 100.
     * null — прогресс отсутствует.
     */
    val progress: Int? = null,

    val isBusy: Boolean = false
)
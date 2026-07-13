package auto.atom.yandexmapdownloadmanager

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel главного окна приложения.
 *
 * Хранит состояние пользовательского интерфейса и управляет
 * запуском и остановкой TCP-сервера.
 */
class MainViewModel {

    private val _uiState = MutableStateFlow(MainUiState())

    /**
     * Текущее состояние пользовательского интерфейса.
     */
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    /**
     * Запускает сервер.
     *
     * Реализация будет добавлена позже.
     */
    fun startServer() {
        _uiState.value = _uiState.value.copy(
            isServerRunning = true,
            status = "Сервер запущен"
        )
    }

    /**
     * Останавливает сервер.
     *
     * Реализация будет добавлена позже.
     */
    fun stopServer() {
        _uiState.value = _uiState.value.copy(
            isServerRunning = false,
            status = "Сервер остановлен"
        )
    }
}
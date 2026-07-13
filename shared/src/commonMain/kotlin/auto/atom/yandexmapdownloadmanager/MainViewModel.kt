package auto.atom.yandexmapdownloadmanager

import auto.atom.yandexmapdownloadmanager.transport.KtorTcpServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel главного окна приложения.
 *
 * Хранит состояние пользовательского интерфейса и управляет
 * запуском и остановкой TCP-сервера.
 */
class MainViewModel {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val server = KtorTcpServer(
        host = "0.0.0.0",
        port = 5555
    )

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

        scope.launch {

            try {

                server.start()

                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        isServerRunning = true,
                        status = "Ожидание подключения..."
                    )
                }

                val connection = server.waitForConnection()

                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        isClientConnected = true,
                        status = "Клиент подключен"
                    )
                }

            } catch (e: Exception) {

                withContext(Dispatchers.Main) {
                    _uiState.value = _uiState.value.copy(
                        status = e.message ?: "Ошибка"
                    )
                }
            }
        }
    }

    /**
     * Останавливает сервер.
     *
     * Реализация будет добавлена позже.
     */
    fun stopServer() {

        scope.launch {

            server.stop()

            withContext(Dispatchers.Main) {
                _uiState.value = MainUiState()
            }
        }
    }
}
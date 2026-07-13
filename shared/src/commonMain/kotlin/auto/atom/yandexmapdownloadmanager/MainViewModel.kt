package auto.atom.yandexmapdownloadmanager

import auto.atom.yandexmapdownloadmanager.transport.Connection
import auto.atom.yandexmapdownloadmanager.transport.KtorTcpServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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

    private var connection: Connection? = null

    /**
     * Подписка на состояние соединения.
     */
    private var connectionJob: Job? = null

    private val _uiState = MutableStateFlow(MainUiState())

    /**
     * Текущее состояние пользовательского интерфейса.
     */
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    /**
     * Запускает сервер.
     */
    /**
     * Запускает сервер.
     */
    fun startServer() {

        scope.launch {

            try {

                _uiState.value = _uiState.value.copy(
                    isBusy = true,
                    isServerRunning = false,
                    isClientConnected = false,
                    status = "Запуск сервера..."
                )

                server.start()

                _uiState.value = _uiState.value.copy(
                    isBusy = false,
                    isServerRunning = true,
                    status = "Ожидание подключения..."
                )

                connection = server.waitForConnection()

                _uiState.value = _uiState.value.copy(
                    isClientConnected = true,
                    status = "Клиент подключен"
                )

                connectionJob?.cancel()

                connectionJob = launch {

                    connection!!
                        .isConnected
                        .collect { connected ->

                            _uiState.value = _uiState.value.copy(
                                isClientConnected = connected,
                                status = if (connected)
                                    "Клиент подключен"
                                else
                                    "Соединение потеряно"
                            )
                        }
                }

            } catch (e: Exception) {

                connectionJob?.cancel()
                connectionJob = null

                runCatching {
                    connection?.close()
                }

                connection = null

                _uiState.value = _uiState.value.copy(
                    isBusy = false,
                    isServerRunning = false,
                    isClientConnected = false,
                    status = e.message ?: "Ошибка"
                )
            }
        }
    }

    /**
     * Останавливает сервер.
     */
    fun stopServer() {

        scope.launch {

            _uiState.value = _uiState.value.copy(
                isBusy = true,
                status = "Остановка сервера..."
            )

            connectionJob?.cancel()
            connectionJob = null

            runCatching {
                connection?.close()
            }

            connection = null

            server.stop()

            _uiState.value = MainUiState()
        }
    }

    /**
     * Останавливает сервер при закрытии приложения.
     */
    fun shutdown() {
        runBlocking {
            runCatching {
                connection?.close()
            }

            runCatching {
                server.stop()
            }

            connectionJob?.cancel()
            scope.cancel()
        }
    }
}
package auto.atom.yandexmapdownloadmanager

import android.util.Log
import auto.atom.yandexmapdownloadmanager.map.AndroidProtocolHandler
import auto.atom.yandexmapdownloadmanager.map.OfflineRegion
import auto.atom.yandexmapdownloadmanager.protocol.Protocol
import auto.atom.yandexmapdownloadmanager.transport.KtorTcpClient
import auto.atom.yandexmapdownloadmanager.map.OfflineYandexMapsManager
import auto.atom.yandexmapdownloadmanager.map.toOfflineRegionTree
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel Android-приложения.
 *
 * Управляет подключением к Desktop-серверу и
 * предоставляет состояние пользовательского интерфейса.
 */
class AndroidViewModel {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val client = KtorTcpClient()

    private var connectionJob: Job? = null

    private val _uiState = MutableStateFlow(AndroidUiState())
    val uiState: StateFlow<AndroidUiState> = _uiState.asStateFlow()

    private val offlineYandexMapsManager = OfflineYandexMapsManager()
    private val _regions = MutableStateFlow<List<OfflineRegion>>(emptyList())
    val regions: StateFlow<List<OfflineRegion>> = _regions

    /**
     * Подключается к Desktop-серверу.
     */
    suspend fun connect() {

        try {

            _uiState.value = _uiState.value.copy(
                isBusy = true,
                status = "Подключение..."
            )

            client.connect(
                host = "10.0.2.2",
                port = Protocol.PORT
            )

            _uiState.value = _uiState.value.copy(
                isConnected = true,
                isBusy = false,
                status = "Подключено"
            )

            connectionJob?.cancel()

            connectionJob = scope.launch {

                client.currentConnection
                    ?.isConnected
                    ?.collect { connected ->

                        _uiState.value = _uiState.value.copy(
                            isConnected = connected,
                            isBusy = false,
                            status = if (connected)
                                "Подключено"
                            else
                                "Соединение потеряно"
                        )

                        if (connected) {
                            // Старт обработки команд от сервера
                            AndroidProtocolHandler(
                                client.currentConnection!!,
                                offlineYandexMapsManager
                            ).run()
                        }
                    }
            }

        } catch (e: Exception) {
            e.printStackTrace()

            Log.e("TCP", "Connect failed", e)

            _uiState.value = _uiState.value.copy(
                isConnected = false,
                isBusy = false,
                status = e.message ?: "Ошибка подключения"
            )
        }
    }

    /**
     * Отключается от Desktop-серверу.
     */
    suspend fun disconnect() {

        connectionJob?.cancel()
        connectionJob = null

        client.close()

        _uiState.value = _uiState.value.copy(
            isConnected = false,
            isBusy = false,
            status = "Отключено"
        )
    }

    /**
     * Устанавливает текст статуса.
     */
    fun setStatus(status: String) {
        _uiState.value = _uiState.value.copy(
            status = status,
            isBusy = true
        )
    }


    /**
     * Получение списка регионов из менеджера офлайн-карт в своем формате.
     */
    /*
    fun loadRegions() {
        offlineYandexMapsManager.loadRegions { regions ->
            _regions.value = regions.toOfflineRegionTree()

            regions.forEach {
                println("${it.id} -> ${it.name}")
            }
        }
    }
    */


}
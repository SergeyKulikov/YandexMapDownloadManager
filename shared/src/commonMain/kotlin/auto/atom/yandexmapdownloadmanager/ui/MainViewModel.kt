package auto.atom.yandexmapdownloadmanager.ui

import auto.atom.yandexmapdownloadmanager.model.OfflineRegion
import auto.atom.yandexmapdownloadmanager.protocol.DesktopProtocolApi
import auto.atom.yandexmapdownloadmanager.protocol.DesktopProtocolApiImpl
import auto.atom.yandexmapdownloadmanager.protocol.DesktopProtocolSession
import auto.atom.yandexmapdownloadmanager.protocol.model.Protocol
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionStatePayload
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

    private var protocolSession: DesktopProtocolSession? = null

    private var protocolApi: DesktopProtocolApi? = null
    private val server = KtorTcpServer(
        host = "0.0.0.0",
        port = Protocol.PORT
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

                protocolSession = DesktopProtocolSession(connection!!)
                protocolSession!!.start()

                protocolSession!!.setOnRegionStateChangedListener { payload ->
                    updateRegionState(payload)
                }

                protocolApi = DesktopProtocolApiImpl(protocolSession!!)


                getRegionsFromClient()

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

    private suspend fun getRegionsFromClient() {
        _regions.value = requireNotNull(protocolApi).getRegions()

        _uiState.value = _uiState.value.copy(
            screen = Screen.MAPS,
            isClientConnected = true,
            status = "Получено регионов: ${_regions.value.size}"
        )
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

            protocolApi = null

            protocolSession?.stop()
            protocolSession = null

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
            protocolApi = null

            runCatching {
                protocolSession?.stop()
            }

            protocolSession = null

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


    private val _regions = MutableStateFlow<List<OfflineRegion>>(emptyList())
    val regions: StateFlow<List<OfflineRegion>> = _regions.asStateFlow()

    /**
    fun loadRegions() {
        scope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isBusy = true,
                    status = "Получение списка регионов..."
                )

                _regions.value = requireNotNull(protocolApi).getRegions()

                _uiState.value = _uiState.value.copy(
                    isBusy = false,
                    status = "Получено регионов: ${_regions.value.size}"
                )

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    isBusy = false,
                    status = e.message ?: "Ошибка получения списка регионов"
                )
            }
        }

    }
    */

    fun showServerScreen() {
        _uiState.value = _uiState.value.copy(
            screen = Screen.SERVER
        )
    }


    /*
    fun downloadRegionById(regionId: Int) {
        scope.launch {

            try {

                requireNotNull(protocolApi).downloadRegion(regionId) { progress ->

                    _regions.value = _regions.value.map { region ->

                        if (region.id == regionId) {
                            region.copy(
                                downloadProgress = progress
                            )
                        } else {
                            region
                        }
                    }
                }

                getRegionsFromClient()

            } catch (e: Exception) {

                _uiState.value = _uiState.value.copy(
                    status = e.message ?: "Ошибка загрузки региона"
                )
            }
        }
    }
    */


    fun regionAction(region: OfflineRegion) {
        scope.launch {
            try {
                when (region.state) {

                    OfflineRegionState.AVAILABLE -> {
                        requireNotNull(protocolApi).downloadRegion(region.id) { progress ->

                            _regions.value = _regions.value.map {
                                if (it.id == region.id) {
                                    it.copy(downloadProgress = progress)
                                } else {
                                    it
                                }
                            }
                        }
                    }

                    OfflineRegionState.DOWNLOADING -> {
                        requireNotNull(protocolApi)
                            .pauseRegion(region.id)
                    }

                    OfflineRegionState.PAUSED -> {
                        requireNotNull(protocolApi)
                            .resumeRegion(region.id)
                    }

                    OfflineRegionState.COMPLETED -> {
                        requireNotNull(protocolApi)
                            .deleteRegion(region.id)
                    }

                    OfflineRegionState.OUTDATED,
                    OfflineRegionState.NEED_UPDATE -> {
                        requireNotNull(protocolApi)
                            .downloadRegion(region.id)
                    }

                    OfflineRegionState.UNSUPPORTED -> {
                        // Ничего не делаем.
                    }
                }

            } catch (_: Exception) {
            }
        }
    }

    private fun updateRegionState(
        payload: RegionStatePayload
    ) {
        _regions.value = _regions.value.map { region ->
            if (region.id == payload.regionId) {
                region.copy(state = payload.state)
            } else {
                region
            }
        }
    }
}



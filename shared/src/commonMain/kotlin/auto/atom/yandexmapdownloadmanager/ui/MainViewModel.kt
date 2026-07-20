package auto.atom.yandexmapdownloadmanager.ui

import auto.atom.yandexmapdownloadmanager.flatten
import auto.atom.yandexmapdownloadmanager.model.OfflineRegion
import auto.atom.yandexmapdownloadmanager.protocol.DesktopProtocolApi
import auto.atom.yandexmapdownloadmanager.protocol.DesktopProtocolApiImpl
import auto.atom.yandexmapdownloadmanager.protocol.DesktopProtocolSession
import auto.atom.yandexmapdownloadmanager.protocol.model.Protocol
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionProgressPayload
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionStatePayload
import auto.atom.yandexmapdownloadmanager.timer.model.RegionAssignments
import auto.atom.yandexmapdownloadmanager.timer.model.UpdatePolicy
import auto.atom.yandexmapdownloadmanager.timer.repository.JsonRegionAssignmentRepository
import auto.atom.yandexmapdownloadmanager.timer.repository.JsonUpdatePolicyRepository
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okio.FileSystem
import okio.Path.Companion.toPath
import java.util.UUID
import kotlin.time.Duration.Companion.days

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


    private val _policies = MutableStateFlow<List<UpdatePolicy>>(emptyList())
    val policies: StateFlow<List<UpdatePolicy>> = _policies.asStateFlow()

    private val _selectedPolicyId = MutableStateFlow<String?>(null)
    val selectedPolicyId: StateFlow<String?> = _selectedPolicyId.asStateFlow()

    /**
     * Подписка на состояние соединения.
     */
    private var serverJob: Job? = null

    private val _regions = MutableStateFlow<List<OfflineRegion>>(emptyList())
    val regions: StateFlow<List<OfflineRegion>> = _regions.asStateFlow()

    private val _filteredRegions = MutableStateFlow<List<OfflineRegion>>(emptyList())
    val filteredRegions: StateFlow<List<OfflineRegion>> = _filteredRegions.asStateFlow()

    @Volatile
    private var serverRunning = false
    private val server = KtorTcpServer(
        host = "0.0.0.0",
        port = Protocol.PORT
    )

    private var connection: Connection? = null

    private val _uiState = MutableStateFlow(MainUiState(serverPort = Protocol.PORT))

    /**
     * Текущее состояние пользовательского интерфейса.
     */
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()


    private val updatePolicyRepository = JsonUpdatePolicyRepository(
        fileSystem = FileSystem.SYSTEM,
        file = "config/update_policies.json".toPath()
    )

    private val regionAssignmentRepository = JsonRegionAssignmentRepository(
        fileSystem = FileSystem.SYSTEM,
        file = "config/region_assignments.json".toPath()
    )

    private val _regionAssignments = MutableStateFlow(RegionAssignments())
    val regionAssignments = _regionAssignments.asStateFlow()

    init {
        scope.launch {
            loadPolicies()
            loadRegionAssignments()
        }

        scope.launch {
            combine(
                _regions,
                _selectedPolicyId,
                _regionAssignments
            ) { regions, selectedPolicyId, assignments ->

                val flatRegions = regions.flatten()
                val assignmentsMap = assignments.assignments

                val result =
                    if (selectedPolicyId == null) {
                        flatRegions.filter {
                            !assignmentsMap.containsKey(it.id)
                        }
                    } else {
                        flatRegions.filter {
                            assignmentsMap[it.id] == selectedPolicyId
                        }
                    }

                println("selectedPolicyId=$selectedPolicyId")
                println("assignments=${assignmentsMap.size}")
                println("regions=${flatRegions.size}")
                println("result=${result.size}")

                result

            }.collect { filtered ->
                _filteredRegions.value = filtered
            }
        }
    }
    /**
     * Запускает сервер.
     */
    fun startServer() {
        if (serverRunning) {
            return
        }

        serverRunning = true

        serverJob = scope.launch {
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

                while (serverRunning) {

                    connection = server.waitForConnection()

                    protocolSession = DesktopProtocolSession(connection!!)
                    protocolSession!!.start()

                    protocolSession!!.setOnRegionStateChangedListener(::updateRegionState)
                    protocolSession!!.setOnRegionProgressChangedListener(::updateRegionProgress)

                    protocolApi = DesktopProtocolApiImpl(protocolSession!!)

                    getRegionsFromClient()

                    _uiState.value = _uiState.value.copy(
                        isClientConnected = true,
                        status = "Клиент подключен"
                    )

                    while (serverRunning && connection!!.isConnected.value) {
                        kotlinx.coroutines.delay(200)
                    }

                    runCatching {
                        protocolSession?.stop()
                    }

                    protocolSession = null
                    protocolApi = null

                    runCatching {
                        connection?.close()
                    }

                    connection = null

                    if (serverRunning) {
                        _uiState.value = _uiState.value.copy(
                            isClientConnected = false,
                            status = "Ожидание подключения..."
                        )
                    }
                }
            } catch (e: Exception) {
                if (serverRunning) {
                    _uiState.value = _uiState.value.copy(
                        isBusy = false,
                        isServerRunning = false,
                        isClientConnected = false,
                        status = e.message ?: "Ошибка"
                    )
                }
            } finally {
                serverRunning = false

                runCatching {
                    protocolSession?.stop()
                }
                protocolSession = null
                protocolApi = null

                runCatching {
                    connection?.close()
                }
                connection = null

                runCatching {
                    server.stop()
                }

                _uiState.value = MainUiState(
                    serverPort = Protocol.PORT
                )
            }
        }
    }

    private suspend fun getRegionsFromClient() {
        _regions.value = requireNotNull(protocolApi).getRegions()

        _uiState.value = _uiState.value.copy(
            screen = Screen.MAPS_HANDMADE,
            isClientConnected = true,
            status = "Получено регионов: ${_regions.value.size}"
        )
    }

    /**
     * Останавливает сервер.
     */
    fun stopServer() {
        serverRunning = false

        scope.launch {
            _uiState.value = _uiState.value.copy(
                isBusy = true,
                status = "Остановка сервера..."
            )

            serverJob?.cancel()
            serverJob = null

            protocolApi = null

            runCatching {
                protocolSession?.stop()
            }
            protocolSession = null

            runCatching {
                connection?.close()
            }
            connection = null

            runCatching {
                server.stop()
            }

            _uiState.value = MainUiState(
                serverPort = Protocol.PORT
            )
        }
    }

    /**
     * Останавливает сервер при закрытии приложения.
     */
    fun shutdown() {
        serverRunning = false
        runBlocking {
            serverJob?.cancel()
            protocolApi = null

            runCatching {
                protocolSession?.stop()
            }
            protocolSession = null

            runCatching {
                connection?.close()
            }
            connection = null

            runCatching {
                server.stop()
            }

            scope.cancel()
        }
    }

    fun showServerScreen() {
        _uiState.value = _uiState.value.copy(
            screen = Screen.SERVER
        )
    }

    fun showScreen(
        screen: Screen
    ) {
        _uiState.value = _uiState.value.copy(
            screen = screen
        )
    }

    fun regionAction(region: OfflineRegion) {
        scope.launch {
            try {
                when (region.state) {

                    OfflineRegionState.AVAILABLE -> {
                        requireNotNull(protocolApi)
                            .downloadRegion(region.id)
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
        _regions.value = _regions.value.map {
            it.updateState(
                payload.regionId,
                payload.state
            )
        }
    }

    private fun OfflineRegion.updateState(
        regionId: Int,
        state: OfflineRegionState
    ): OfflineRegion {

        val newChildren = children.map { it.updateState(regionId, state) }.toMutableList()

        return if (id == regionId) {
            copy(state = state, children = newChildren)
        } else {
            copy(children = newChildren)
        }
    }

    private fun OfflineRegion.updateProgress(
        regionId: Int,
        progress: Float
    ): OfflineRegion {

        val newChildren = children.map { it.updateProgress(regionId, progress) }.toMutableList()

        return if (id == regionId) {
            copy(downloadProgress = progress, children = newChildren)
        } else {
            copy(children = newChildren)
        }
    }

    private fun updateRegionProgress(
        payload: RegionProgressPayload
    ) {
        println("UI PROGRESS ${payload.regionId} ${payload.progress}")
        _regions.value = _regions.value.map {
            it.updateProgress(
                payload.regionId,
                payload.progress
            )
        }
    }

    fun selectPolicy(id: String?) {
        _selectedPolicyId.value = id
    }

    fun addPolicy(
        name: String,
        days: Int
    ) {
        println("addPolicy($name, $days)")

        scope.launch {
            try {
                val policy = UpdatePolicy(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    periodDays = days.days
                )

                updatePolicyRepository.addPolicy(policy)

                _policies.value = updatePolicyRepository.getPolicies()
                _selectedPolicyId.value = policy.id

                println("Policies: ${_policies.value.size}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deletePolicy(
        id: String
    ) {

        scope.launch {

            updatePolicyRepository.deletePolicy(id)

            _policies.value =
                updatePolicyRepository.getPolicies()

            if (_selectedPolicyId.value == id) {
                _selectedPolicyId.value =
                    _policies.value.firstOrNull()?.id
            }
        }
    }

    fun updatePolicy(
        policy: UpdatePolicy
    ) {

        scope.launch {

            updatePolicyRepository.updatePolicy(policy)

            _policies.value =
                updatePolicyRepository.getPolicies()
        }
    }

    private suspend fun loadPolicies() {

        _policies.value =
            updatePolicyRepository.getPolicies()

        _selectedPolicyId.value =
            _policies.value.firstOrNull()?.id
    }

    private suspend fun loadRegionAssignments() {
        _regionAssignments.value =
            regionAssignmentRepository.getAssignments()
    }

    fun assignRegion(
        regionId: Int,
        policyId: String?
    ) {
        scope.launch {
            regionAssignmentRepository.assign(regionId, policyId)

            _regionAssignments.value =
                regionAssignmentRepository.getAssignments()
        }
    }

    private fun OfflineRegion.filterTree(
        predicate: (OfflineRegion) -> Boolean
    ): OfflineRegion? {

        val filteredChildren = children
            .mapNotNull {
                it.filterTree(predicate)
            }
            .toMutableList()

        return if (predicate(this) || filteredChildren.isNotEmpty()) {
            copy(children = filteredChildren)
        } else {
            null
        }
    }
}

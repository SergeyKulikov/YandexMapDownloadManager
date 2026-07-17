package auto.atom.yandexmapdownloadmanager.protocol

import auto.atom.yandexmapdownloadmanager.protocol.model.HelloRequest
import auto.atom.yandexmapdownloadmanager.protocol.model.HelloResponse
import auto.atom.yandexmapdownloadmanager.protocol.model.Packet
import auto.atom.yandexmapdownloadmanager.protocol.model.Progress
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionProgressNotification
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionProgressPayload
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionStateNotification
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionStatePayload
import auto.atom.yandexmapdownloadmanager.protocol.model.Request
import auto.atom.yandexmapdownloadmanager.protocol.model.Response
import auto.atom.yandexmapdownloadmanager.transport.Connection
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * Сессия обмена сообщениями между Desktop и Android.
 *
 * Работает поверх готового Connection и отвечает за:
 * - отправку Request;
 * - ожидание Response по id;
 * - прием входящих сообщений;
 * - обработку Progress;
 * - сопоставление Response ожидающему запросу.
 */
class DesktopProtocolSession(
    private val connection: Connection
) {

    private val scope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Ожидающие ответы.
     */
    private val pendingRequests =
        ConcurrentHashMap<String, CompletableDeferred<Response>>()

    /**
     * Колбэки прогресса.
     */
    private val progressCallbacks =
        ConcurrentHashMap<String, (Progress) -> Unit>()

    private var onRegionStateChanged: ((RegionStatePayload) -> Unit)? = null

    private var onRegionProgressChanged: ((RegionProgressPayload) -> Unit)? = null

    private var receiveJob: Job? = null

    /**
     * Запускает обработку входящих сообщений.
     */
    fun start() {

        if (receiveJob != null) {
            return
        }

        receiveJob = scope.launch {
            receiveLoop()
        }
    }

    /**
     * Останавливает обработку сообщений.
     */
    suspend fun stop() {

        receiveJob?.cancel()
        receiveJob = null

        pendingRequests.values.forEach {
            it.cancel()
        }

        pendingRequests.clear()
        progressCallbacks.clear()

        scope.cancel()
    }


    fun setOnRegionStateChangedListener(
        listener: (RegionStatePayload) -> Unit
    ) {
        onRegionStateChanged = listener
    }

    fun setOnRegionProgressChangedListener(
        listener: (RegionProgressPayload) -> Unit
    ) {
        onRegionProgressChanged = listener
    }

    /**
     * Отправляет запрос и ожидает ответ.
     */
    suspend fun execute(
        request: Request,
        onProgress: ((Progress) -> Unit)? = null
    ): Response {

        println("EXECUTE ${request.command}")

        val deferred = CompletableDeferred<Response>()

        pendingRequests[request.id] = deferred

        if (onProgress != null) {
            progressCallbacks[request.id] = onProgress
        }

        try {

            connection.send(
                Packet(
                    message = request
                )
            )

            println("REQUEST QUEUED ${request.command}")

            return deferred.await()

        } finally {

            pendingRequests.remove(request.id)
            progressCallbacks.remove(request.id)
        }
    }

    /**
     * Основной цикл обработки входящих сообщений.
     */
    private suspend fun receiveLoop() {

        while (connection.isConnected.value) {

            val packet = connection.receive() ?: break

            when (val message = packet.message) {

                is Response -> {

                    pendingRequests[message.id]
                        ?.complete(message)
                }

                is Progress -> {
                    println("<<< PROGRESS ${message.progress}")
                    handleProgress(message)
                }

                is RegionStateNotification -> {
                    println(
                        "<<< REGION STATE ${message.payload.regionId} ${message.payload.state}"
                    )

                    handleRegionState(message.payload)
                }

                is RegionProgressNotification -> {
                    println(
                        "<<< REGION PROGRESS ${message.payload.regionId} ${message.payload.progress}"
                    )

                    handleRegionProgress(message.payload)
                }
                is Request -> {
                    // Desktop не принимает Request
                }

                is HelloRequest,
                is HelloResponse -> {
                    // Уже обработаны во время handshake()
                }
            }
        }

        connectionClosed()
    }

    /**
     * Обработка Progress.
     */
    private fun handleProgress(
        progress: Progress
    ) {

        progressCallbacks[progress.id]?.invoke(progress)
    }

    private fun handleRegionState(
        payload: RegionStatePayload
    ) {
        onRegionStateChanged?.invoke(payload)
    }

    private fun handleRegionProgress(
        payload: RegionProgressPayload
    ) {
        println("<<< REGION PROGRESS ${payload.regionId} ${payload.progress}")
        onRegionProgressChanged?.invoke(payload)
    }

    /**
     * Соединение закрыто.
     *
     * Завершаем все ожидающие запросы.
     */
    private fun connectionClosed() {

        val exception =
            IllegalStateException("Connection closed")

        pendingRequests.values.forEach {
            it.completeExceptionally(exception)
        }

        pendingRequests.clear()
        progressCallbacks.clear()

        onRegionStateChanged = null
        onRegionProgressChanged = null
    }
}
package auto.atom.yandexmapdownloadmanager.protocol

import auto.atom.yandexmapdownloadmanager.model.FileTransferState
import auto.atom.yandexmapdownloadmanager.protocol.model.HelloRequest
import auto.atom.yandexmapdownloadmanager.protocol.model.HelloResponse
import auto.atom.yandexmapdownloadmanager.protocol.model.Packet
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionFileChunkPayload
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionProgressNotification
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionProgressPayload
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionStateNotification
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionStatePayload
import auto.atom.yandexmapdownloadmanager.protocol.model.Request
import auto.atom.yandexmapdownloadmanager.protocol.model.Response
import auto.atom.yandexmapdownloadmanager.protocol.model.Status
import auto.atom.yandexmapdownloadmanager.transport.Connection
import auto.atom.yandexmapdownloadmanager.transport.ProtocolJson
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.decodeFromJsonElement
import java.io.File
import java.io.FileOutputStream
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


    private var onRegionStateChanged: ((RegionStatePayload) -> Unit)? = null

    private var onRegionProgressChanged: ((RegionProgressPayload) -> Unit)? = null

    private var receiveJob: Job? = null

    private val regionFileReceiver = RegionFileReceiver(
        File("C:/temp/map_cache")
    )

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
        try {
            receiveJob?.cancel()
            receiveJob?.join()
            receiveJob = null

            pendingRequests.values.forEach {
                it.cancel()
            }

            pendingRequests.clear()

            scope.cancel()
        } catch (ex: Exception) {
            // ex.printStackTrace()
        }
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
        request: Request
    ): Response {

        println("EXECUTE ${request.command}")

        val deferred = CompletableDeferred<Response>()

        pendingRequests[request.id] = deferred

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
        }
    }

    /**
     * Основной цикл обработки входящих сообщений.
     */
    private suspend fun receiveLoop() {
        try {
            while (connection.isConnected.value) {
                val packet = connection.receive() ?: break

                when (val message = packet.message) {
                    is Response -> {

                        if (message.payload != null) {

                            val chunk = runCatching {
                                ProtocolJson.decodeFromJsonElement<RegionFileChunkPayload>(
                                    message.payload!!
                                )
                            }.getOrNull()

                            if (chunk != null) {
                                regionFileReceiver.onChunk(chunk)
                                continue
                            }
                        }

                        pendingRequests[message.id]?.complete(message)
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
        } catch (_: CancellationException) {
            // Нормальное завершение
        } finally {
            connectionClosed()
        }
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

        onRegionStateChanged = null
        onRegionProgressChanged = null
    }
}
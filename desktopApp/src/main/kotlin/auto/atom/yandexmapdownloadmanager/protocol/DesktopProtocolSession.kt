package auto.atom.yandexmapdownloadmanager.protocol

import auto.atom.yandexmapdownloadmanager.protocol.model.FileCopyProgressNotification
import auto.atom.yandexmapdownloadmanager.protocol.model.FileCopyProgressPayload
import auto.atom.yandexmapdownloadmanager.protocol.model.HelloRequest
import auto.atom.yandexmapdownloadmanager.protocol.model.HelloResponse
import auto.atom.yandexmapdownloadmanager.protocol.model.Packet
import auto.atom.yandexmapdownloadmanager.protocol.model.FileDataPayload
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionProgressNotification
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionProgressPayload
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionStateNotification
import auto.atom.yandexmapdownloadmanager.protocol.model.RegionStatePayload
import auto.atom.yandexmapdownloadmanager.protocol.model.Request
import auto.atom.yandexmapdownloadmanager.protocol.model.Response
import auto.atom.yandexmapdownloadmanager.protocol.model.Status
import auto.atom.yandexmapdownloadmanager.transport.BinaryFrame
import auto.atom.yandexmapdownloadmanager.transport.Connection
import auto.atom.yandexmapdownloadmanager.transport.JsonFrame
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
    private val connection: Connection,
    private val root: String
) {

    private var currentFileStartedAt = 0L
    private var currentFileName: String? = null
    private val scope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Ожидающие ответы.
     */
    private val pendingRequests = ConcurrentHashMap<String, CompletableDeferred<Response>>()

    /**
     * Колбэки прогресса.
     */


    private var onRegionStateChanged: ((RegionStatePayload) -> Unit)? = null

    private var onRegionProgressChanged: ((RegionProgressPayload) -> Unit)? = null
    private var onFileCopyProgressChanged: ((FileCopyProgressPayload) -> Unit)? = null

    private var receiveJob: Job? = null

    private val regionFileReceiver = RegionFileReceiver(
        File(root)
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
    suspend fun executeOld(
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

            val response = deferred.await()

            if (response.status == Status.ERROR) {
                throw DesktopProtocolException(
                    response.error ?: "Unknown protocol error"
                )
            }

            return response

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

                when (val incoming = connection.receive()) {

                    null -> break

                    is JsonFrame -> {

                        when (val message = incoming.packet.message) {

                            is Response -> {
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

                            is FileCopyProgressNotification -> {
                                println(
                                    "<<< FILE PROGRESS ${message.payload.regionId} ${message.payload.fileName} ${message.payload.progress}"
                                )
                                handleFileCopyProgress(message.payload)
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

                    is BinaryFrame -> {

                        val file = incoming.frame

                        if (currentFileName != file.fileName) {
                            currentFileName = file.fileName
                            currentFileStartedAt = System.nanoTime()

                            println(">>> START ${file.fileName}")
                        }

                        regionFileReceiver.onChunk(
                            FileDataPayload(
                                regionId = file.regionId,
                                fileName = file.fileName,
                                offset = file.offset,
                                bytes = file.bytes
                            ),
                            file.lastPart
                        )

                        if (file.lastPart) {

                            val elapsedMs =
                                (System.nanoTime() - currentFileStartedAt) / 1_000_000.0

                            println(
                                ">>> FINISH ${file.fileName} (${String.format("%.2f", elapsedMs)} ms)"
                            )

                            currentFileName = null
                            currentFileStartedAt = 0L
                        }
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

    private fun handleFileCopyProgress(
        payload: FileCopyProgressPayload
    ) {
        println("<<< REGION PROGRESS ${payload.regionId} ${payload.progress}")
        onFileCopyProgressChanged?.invoke(payload)
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
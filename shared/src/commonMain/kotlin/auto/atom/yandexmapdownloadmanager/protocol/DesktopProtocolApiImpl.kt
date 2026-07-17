package auto.atom.yandexmapdownloadmanager.protocol

import auto.atom.yandexmapdownloadmanager.map.OfflineRegion
import auto.atom.yandexmapdownloadmanager.protocol.map.RegionPayload
import auto.atom.yandexmapdownloadmanager.protocol.map.RegionsPayload
import auto.atom.yandexmapdownloadmanager.transport.ProtocolJson
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import java.util.UUID

/**
 * Реализация высокоуровневого API обмена с Android.
 *
 * Скрывает детали сетевого протокола и предоставляет
 * удобные методы для управления офлайн-картами.
 */
class DesktopProtocolApiImpl(
    private val session: DesktopProtocolSession
) : DesktopProtocolApi {

    /**
     * Получает список доступных офлайн-регионов.
     */
    override suspend fun getRegions(): List<OfflineRegion> {

        val response = session.execute(
            Request(
                id = UUID.randomUUID().toString(),
                command = Command.GET_REGIONS
            )
        )

        check(response.status == Status.OK) {
            "GET_REGIONS failed: ${response.status}"
        }

        val payload = requireNotNull(response.payload) {
            "GET_REGIONS returned empty payload."
        }

        return ProtocolJson.decodeFromJsonElement<RegionsPayload>(
            payload
        ).regions
    }

    /**
     * Начинает загрузку региона.
     */
    override suspend fun downloadRegion(
        regionId: Int,
        onProgress: (Float) -> Unit
    ) {

        val response = session.execute(
            Request(
                id = UUID.randomUUID().toString(),
                command = Command.DOWNLOAD_REGION,
                payload = ProtocolJson.encodeToJsonElement(
                    RegionPayload(regionId)
                )
            )
        ) { progress ->

            onProgress(progress.progress)
        }

        check(response.status == Status.OK) {
            response.error ?: "DOWNLOAD_REGION failed."
        }
    }

    override suspend fun pauseRegion(regionId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun resumeRegion(regionId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun cancelRegion(regionId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRegion(regionId: String) {
        TODO("Not yet implemented")
    }
}
package auto.atom.yandexmapdownloadmanager.timer.repository

import auto.atom.yandexmapdownloadmanager.timer.model.RegionDownloadState
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path
import okio.buffer

class JsonRegionDownloadRepository(
    private val fileSystem: FileSystem,
    private val file: Path,
    private val json: Json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }
) : RegionDownloadRepository {

    override suspend fun getStates(): List<RegionDownloadState> {

        if (!fileSystem.exists(file)) {
            return emptyList()
        }

        val text = fileSystem.source(file)
            .buffer()
            .readUtf8()

        if (text.isBlank()) {
            return emptyList()
        }

        return json.decodeFromString(text)
    }

    override suspend fun getState(
        regionId: Int
    ): RegionDownloadState? =
        getStates().firstOrNull {
            it.regionId == regionId
        }

    override suspend fun updateState(
        state: RegionDownloadState
    ) {

        val map = getStates()
            .associateBy { it.regionId }
            .toMutableMap()

        map[state.regionId] = state

        save(map.values.toList())
    }

    override suspend fun deleteState(
        regionId: Int
    ) {

        val map = getStates()
            .associateBy { it.regionId }
            .toMutableMap()

        map.remove(regionId)

        save(map.values.toList())
    }

    private fun save(
        states: List<RegionDownloadState>
    ) {

        file.parent?.let {
            fileSystem.createDirectories(it)
        }

        fileSystem.sink(file)
            .buffer()
            .use { sink ->
                sink.writeUtf8(
                    json.encodeToString(states)
                )
            }
    }
}
package auto.atom.yandexmapdownloadmanager.timer.repository


import auto.atom.yandexmapdownloadmanager.timer.model.RegionAssignments
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path

class JsonRegionAssignmentRepository(
    private val fileSystem: FileSystem,
    private val file: Path,
    private val json: Json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }
) : RegionAssignmentRepository {

    override suspend fun getPolicy(
        regionId: Int
    ): String? {

        return getAssignments()[regionId]
    }

    override suspend fun assign(
        regionId: Int,
        policyId: String?
    ) {

        val assignments = getAssignments().toMutableMap()

        if (policyId == null) {
            assignments.remove(regionId)
        } else {
            assignments[regionId] = policyId
        }

        save(assignments)
    }

    override suspend fun getAssignments(): Map<Int, String?> {

        if (!fileSystem.exists(file)) {
            return emptyMap()
        }

        val text = fileSystem.read(file) {
            readUtf8()
        }

        if (text.isBlank()) {
            return emptyMap()
        }

        return json.decodeFromString<RegionAssignments>(text)
            .assignments
    }

    private suspend fun save(
        assignments: Map<Int, String?>
    ) {

        file.parent?.let {
            fileSystem.createDirectories(it)
        }

        fileSystem.write(file) {
            writeUtf8(
                json.encodeToString(
                    RegionAssignments(assignments)
                )
            )
        }
    }
}
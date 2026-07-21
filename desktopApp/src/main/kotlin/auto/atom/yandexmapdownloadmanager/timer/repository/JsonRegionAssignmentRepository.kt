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
    ): String? =
        getAssignments().policyFor(regionId)

    override suspend fun assign(
        regionId: Int,
        policyId: String?
    ) {
        save(
            getAssignments().withAssignment(regionId, policyId)
        )
    }

    override suspend fun getAssignments(): RegionAssignments {

        if (!fileSystem.exists(file)) {
            return RegionAssignments()
        }

        val text = fileSystem.read(file) {
            readUtf8()
        }

        if (text.isBlank()) {
            return RegionAssignments()
        }

        return json.decodeFromString(text)
    }

    override suspend fun getRegions(
        policyId: String?
    ): Set<Int> =
        getAssignments().regionsFor(policyId)

    private fun save(
        assignments: RegionAssignments
    ) {

        file.parent?.let(fileSystem::createDirectories)

        fileSystem.write(file) {
            writeUtf8(
                json.encodeToString(assignments)
            )
        }
    }
}
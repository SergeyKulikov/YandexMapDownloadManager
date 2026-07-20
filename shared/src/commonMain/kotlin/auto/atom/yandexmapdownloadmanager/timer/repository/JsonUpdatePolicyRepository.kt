package auto.atom.yandexmapdownloadmanager.timer.repository

import auto.atom.yandexmapdownloadmanager.timer.model.UpdatePolicy
import kotlinx.serialization.json.Json
import okio.FileSystem
import okio.Path
import okio.buffer

class JsonUpdatePolicyRepository(
    private val fileSystem: FileSystem,
    private val file: Path,
    private val json: Json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }
) : UpdatePolicyRepository {

    override suspend fun getPolicies(): List<UpdatePolicy> {

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

    override suspend fun addPolicy(
        policy: UpdatePolicy
    ) {

        val list = getPolicies().toMutableList()

        list += policy

        save(list)
    }

    override suspend fun updatePolicy(
        policy: UpdatePolicy
    ) {

        val list = getPolicies().map {
            if (it.id == policy.id) {
                policy
            } else {
                it
            }
        }

        save(list)
    }

    override suspend fun deletePolicy(
        id: String
    ) {

        val list = getPolicies().filterNot {
            it.id == id
        }

        save(list)
    }

    private fun save(
        policies: List<UpdatePolicy>
    ) {

        file.parent?.let {
            fileSystem.createDirectories(it)
        }

        fileSystem.sink(file)
            .buffer()
            .use { sink ->
                sink.writeUtf8(
                    json.encodeToString(policies)
                )
            }
    }
}
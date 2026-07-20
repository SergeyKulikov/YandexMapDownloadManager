package auto.atom.yandexmapdownloadmanager.timer.repository

import auto.atom.yandexmapdownloadmanager.timer.model.UpdatePolicy

interface UpdatePolicyRepository {

    suspend fun getPolicies(): List<UpdatePolicy>

    suspend fun addPolicy(policy: UpdatePolicy)

    suspend fun updatePolicy(policy: UpdatePolicy)

    suspend fun deletePolicy(id: String)
}
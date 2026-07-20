package auto.atom.yandexmapdownloadmanager.timer.repository

import auto.atom.yandexmapdownloadmanager.timer.model.RegionAssignments

interface RegionAssignmentRepository {

    suspend fun getPolicy(
        regionId: Int
    ): String?

    suspend fun assign(
        regionId: Int,
        policyId: String?
    )

    suspend fun getAssignments(): RegionAssignments

    suspend fun getRegions(
        policyId: String?
    ): Set<Int>
}
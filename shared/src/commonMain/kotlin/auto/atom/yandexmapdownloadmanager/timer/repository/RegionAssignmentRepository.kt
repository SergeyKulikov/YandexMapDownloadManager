package auto.atom.yandexmapdownloadmanager.timer.repository

interface RegionAssignmentRepository {

    suspend fun getPolicy(
        regionId: Int
    ): String?

    suspend fun assign(
        regionId: Int,
        policyId: String?
    )

    suspend fun getAssignments(): Map<Int, String?>
}
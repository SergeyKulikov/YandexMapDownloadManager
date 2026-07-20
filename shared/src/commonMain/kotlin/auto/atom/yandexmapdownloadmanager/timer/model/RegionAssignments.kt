package auto.atom.yandexmapdownloadmanager.timer.model

import kotlinx.serialization.Serializable

@Serializable
data class RegionAssignments(
    val assignments: Map<Int, String?> = emptyMap()
) {

    fun policyFor(
        regionId: Int
    ): String? =
        assignments[regionId]

    fun withAssignment(
        regionId: Int,
        policyId: String?
    ): RegionAssignments =
        copy(
            assignments = assignments.toMutableMap().apply {
                if (policyId == null) {
                    remove(regionId)
                } else {
                    put(regionId, policyId)
                }
            }
        )

    fun regionsFor(
        policyId: String?
    ): Set<Int> =
        assignments
            .filterValues { it == policyId }
            .keys

    fun isAssigned(
        regionId: Int
    ): Boolean =
        assignments.containsKey(regionId)

    fun isAssignedTo(
        regionId: Int,
        policyId: String?
    ): Boolean =
        policyFor(regionId) == policyId
}
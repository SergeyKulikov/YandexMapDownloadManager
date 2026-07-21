package auto.atom.yandexmapdownloadmanager

import auto.atom.yandexmapdownloadmanager.model.OfflineRegion
import auto.atom.yandexmapdownloadmanager.timer.model.RegionAssignments

fun filterRegions(
    regions: List<OfflineRegion>,
    selectedPolicyId: String?,
    assignments: RegionAssignments
): List<OfflineRegion> {

    val flatRegions = regions.flatten()
    val assignmentsMap = assignments.assignments

    return if (selectedPolicyId == null) {
        flatRegions.filter { region ->
            !assignmentsMap.containsKey(region.id)
        }
    } else {
        flatRegions.filter { region ->
            assignmentsMap[region.id] == selectedPolicyId
        }
    }
}
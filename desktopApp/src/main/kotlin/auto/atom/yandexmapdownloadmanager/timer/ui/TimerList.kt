package auto.atom.yandexmapdownloadmanager.timer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import atomyandexmapmanager.shared.generated.resources.Res
import atomyandexmapmanager.shared.generated.resources.add_box
import auto.atom.yandexmapdownloadmanager.filterRegions
import auto.atom.yandexmapdownloadmanager.timer.model.UpdatePolicy
import auto.atom.yandexmapdownloadmanager.ui.MainViewModel
import org.jetbrains.compose.resources.painterResource

@Composable
fun TimerList(
    viewModel: MainViewModel,
    policies: List<UpdatePolicy>,
    selectedPolicyId: String?,
    onSelect: (String?) -> Unit,
    onAdd: (name: String, days: Int) -> Unit,
    onDelete: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    var editingPolicy by remember {
        mutableStateOf<UpdatePolicy?>(null)
    }

    if (showAddDialog) {
        AddPolicyDialog(
            onDismiss = {
                showAddDialog = false
            },
            onConfirm = { name, days ->
                onAdd(name, days)
                showAddDialog = false
            },
            isEdit = false
        )
    }

    editingPolicy?.let { policy ->
        AddPolicyDialog(
            initialName = policy.name,
            initialDays = policy.periodDays.inWholeDays.toInt(),
            onDismiss = {
                editingPolicy = null
            },
            onConfirm = { name, days ->
                viewModel.editPolicy(
                    id = policy.id,
                    name = name,
                    days = days
                )
                editingPolicy = null
            },
            isEdit = true
        )
    }

    Column(
        modifier = modifier.padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "Периоды обновления",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.weight(1f))

            FilledTonalIconButton(
                onClick = {
                    showAddDialog = true
                }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.add_box),
                    contentDescription = "Добавить период",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        HorizontalDivider()

        Spacer(Modifier.height(12.dp))

         val allRegions by viewModel.regions.collectAsState()
        val assignments by viewModel.regionAssignments.collectAsState()

        policies.forEach { policy ->

            val count = filterRegions(allRegions, policy.id, assignments).size

            TimerListItem(
                title = policy.name,
                periodDays = policy.periodDays.inWholeDays.toInt(),
                regionCount = count,
                selected = policy.id == selectedPolicyId,
                onClick = {
                    onSelect(policy.id)
                },
                onEdit = {
                    editingPolicy = policy
                },
                onDelete = {
                    onDelete(policy.id)
                },
                onRegionDropped = { regionId, _ ->
                    viewModel.addRegionToPeriod(
                        policy.id,
                        regionId
                    )
                }
            )

            Spacer(Modifier.height(12.dp))
        }

        val count = filterRegions(
            allRegions,
            null,
            assignments
        ).size

        TimerListItem(
            title = "Не назначено",
            selected = selectedPolicyId == null,
            periodDays = 0,
            regionCount = count,
            onClick = {
                onSelect(null)
            },
            onEdit = null,
            onDelete = null,
            onRegionDropped = { regionId, _ ->
                viewModel.addRegionToPeriod(
                    null,
                    regionId
                )
            }
        )
    }
}

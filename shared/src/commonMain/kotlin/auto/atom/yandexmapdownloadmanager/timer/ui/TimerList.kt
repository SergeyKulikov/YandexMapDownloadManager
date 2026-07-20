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
import androidx.lifecycle.viewmodel.compose.viewModel
import atomyandexmapmanager.shared.generated.resources.Res
import atomyandexmapmanager.shared.generated.resources.add_box
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

    if (showAddDialog) {

        AddPolicyDialog(
            onDismiss = {
                showAddDialog = false
            },
            onConfirm = { name, days ->
                onAdd(name, days)
                showAddDialog = false
            }
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

        policies.forEach { policy ->

            TimerListItem(
                title = policy.name,
                selected = policy.id == selectedPolicyId,
                onClick = {
                    onSelect(policy.id)
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
        }

        TimerListItem(
            title = "Не назначено",
            selected = selectedPolicyId == null,
            onClick = {
                onSelect(null)
            },
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
package auto.atom.yandexmapdownloadmanager.timer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AddPolicyDialog(
    initialName: String = "",
    initialDays: Int = 30,
    isEdit: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit
) {

    var name by remember(initialName) {
        mutableStateOf(initialName)
    }

    var days by remember(initialDays) {
        mutableStateOf(initialDays.toString())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (isEdit)
                    "Редактирование периода"
                else
                    "Новый период"
            )
        },
        text = {
            Column {

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                    },
                    label = {
                        Text("Название")
                    },
                    singleLine = true
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = days,
                    onValueChange = {
                        days = it.filter(Char::isDigit)
                    },
                    label = {
                        Text("Количество дней")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank() && days.toIntOrNull() != null,
                onClick = {
                    onConfirm(
                        name.trim(),
                        days.toInt()
                    )
                }
            ) {
                Text(
                    if (isEdit)
                        "Сохранить"
                    else
                        "Добавить"
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Отмена")
            }
        }
    )
}
package ge.siradze.multiplayergame.menu.presentation.screens.alert

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AlertDialogWithTextField(
    title: String,
    description: String,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    defaultValue: String = "",
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var textFieldValue by remember { mutableStateOf(defaultValue) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(title) },
        text = {
            Column {
                Text(description)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(textFieldValue) }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismissRequest
            ) {
                Text("Cancel")
            }
        }
    )
}
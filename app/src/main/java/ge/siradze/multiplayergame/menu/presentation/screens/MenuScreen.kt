package ge.siradze.multiplayergame.menu.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ge.siradze.multiplayergame.menu.presentation.MenuActivityVM
import ge.siradze.multiplayergame.menu.presentation.screens.alert.AlertDialogWithTextField
import ge.siradze.multiplayergame.ui.theme.MultiplayerGameTheme
import kotlin.random.Random

@Composable
fun MainScreen(
    state: MenuActivityVM.MenuState.Main,
    onEvent: (MenuActivityVM.MenuEvent) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(
        modifier.fillMaxSize(),
    ) {
        var showAlert by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier.align(Alignment.BottomCenter).padding(30.dp)
        ) {
            if(showAlert) {
                AlertDialogWithTextField(
                    title = "Host Game",
                    description = "Enter game name",
                    onDismissRequest = { showAlert = false },
                    defaultValue = Random.nextInt(1000, 9999).toString(),
                    onConfirm = {
                        onEvent(MenuActivityVM.MenuEvent.HostClicked(it))
                        showAlert = false
                    }
                )
            }
            OutlinedTextField(
                value = state.ip,
                shape = RoundedCornerShape(30.dp),
                textStyle = MaterialTheme.typography.bodySmall,
                onValueChange = {
                    onEvent(MenuActivityVM.MenuEvent.IpChanged(it))
                },
            )

        }

        Box(
            modifier = Modifier.align(Alignment.Center)
        ) {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Spacer(modifier = Modifier.height(50.dp))
                OutlinedButton(
                    onClick = {
                        onEvent(MenuActivityVM.MenuEvent.PlayClicked)
                    }
                ) {
                    Text(text = "Play")
                }
                OutlinedButton(
                    onClick = {
                        showAlert = true
                    }
                ) {
                    Text(text = "Host")
                }
                OutlinedButton(
                    onClick = {
                        onEvent(MenuActivityVM.MenuEvent.JoinClicked)
                    }
                ) {
                    Text(text = "Join")
                }


            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MultiplayerGameTheme {
        MainScreen(
            MenuActivityVM.MenuState.Main("localhost")
        )
    }
}
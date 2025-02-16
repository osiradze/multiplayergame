package ge.siradze.mutiplayergame.menu.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ge.siradze.mutiplayergame.menu.presentation.MenuActivityVM
import ge.siradze.mutiplayergame.ui.theme.MutiplayerGameTheme

@Composable
fun MainScreen(
    onEvent: (MenuActivityVM.MenuEvent) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(
        modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            OutlinedTextField(
                value = "192.168.25.211",
                onValueChange = {
                    onEvent(MenuActivityVM.MenuEvent.IpChanged(it))
                },
            )
            Spacer(modifier = Modifier.height(50.dp))
            OutlinedButton(
                onClick = {
                    onEvent(MenuActivityVM.MenuEvent.HostClicked)
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

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    MutiplayerGameTheme {
        MainScreen()
    }
}
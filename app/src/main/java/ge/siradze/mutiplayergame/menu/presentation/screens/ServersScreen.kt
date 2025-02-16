package ge.siradze.mutiplayergame.menu.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ge.siradze.mutiplayergame.menu.domain.model.Server

@Composable
fun ServersScreen(
    list: List<Server>,
    modifier: Modifier = Modifier,
) {

    Box(
        modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            modifier = modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(list.count()) { index ->
                val server = list[index]
                ElevatedCard (
                    onClick = {}
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Name: ${server.name}")
                        Text(text = "port: ${server.port}")
                    }

                }

            }
        }
    }
}

@Preview
@Composable
fun ServersScreenPreview() {
    val list = listOf(
        Server(1, "Server 1", 5000),
        Server(2, "Server 2", 5001),
        Server(3, "Server 3", 5002),
        Server(4, "Server 4", 5003),
    )
    ServersScreen(list = list)
}
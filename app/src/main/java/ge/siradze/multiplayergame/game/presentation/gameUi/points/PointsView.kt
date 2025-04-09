package ge.siradze.multiplayergame.game.presentation.gameUi.points

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ge.siradze.multiplayergame.game.presentation.GameVM

@Composable
fun BoxScope.PointsView(uiState: GameVM.UiState) {
    Text(
        color = Color.White,
        text = uiState.points.toString(),
        fontSize = 20.sp,
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(50.dp)
    )
}
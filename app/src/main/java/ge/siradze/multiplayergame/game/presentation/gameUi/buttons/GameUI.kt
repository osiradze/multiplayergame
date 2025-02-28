package ge.siradze.multiplayergame.game.presentation.gameUi.buttons

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ge.siradze.multiplayergame.game.presentation.gameUi.UIEvents
import java.util.concurrent.CancellationException

@Composable
fun GameUI(
    modifier: Modifier = Modifier,
    onEvent: (UIEvents) -> Unit = {}
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        JoySticks(
            modifier = Modifier.align(Alignment.BottomEnd),
        )
        JoySticks(
            modifier = Modifier.align(Alignment.BottomStart).pointerInput(Unit) {
                detectTapGestures(
                   onTap = {
                       onEvent(UIEvents.onTap)
                   }
                )
            }
        )
    }
}

@Composable
fun JoySticks (
    modifier: Modifier = Modifier,
) {
    Box(
        modifier.padding(60.dp)
    ){
        Box(modifier = Modifier
            .width(70.dp)
            .height(70.dp)
            .border(
                width = 1.dp,
                color = Color.White,
                shape = RoundedCornerShape(35.dp)
            )
        )
    }
}

@Preview
@Composable
fun JoySticksPreview() {
    GameUI()
}


package ge.siradze.multiplayergame.game.presentation.gameUi.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import ge.siradze.multiplayergame.game.presentation.engine.extensions.multiply
import ge.siradze.multiplayergame.game.presentation.engine.extensions.normalize
import ge.siradze.multiplayergame.game.presentation.engine.extensions.vectorLength
import ge.siradze.multiplayergame.game.presentation.engine.extensions.x
import ge.siradze.multiplayergame.game.presentation.engine.extensions.y
import ge.siradze.multiplayergame.game.presentation.gameUi.UIEvents
import kotlin.math.roundToInt

@Composable
fun GameUI(
    modifier: Modifier = Modifier,
    onEvent: (UIEvents) -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 40.dp)
    ) {
        RadioButtonJoyStick (
            modifier = Modifier.align(Alignment.BottomStart),
            onEvent = onEvent
        )
        DragJoySticks(
            modifier = Modifier.align(Alignment.BottomEnd),
            onEvent = onEvent
        )
        /*PressJoySticks(
            modifier = Modifier.align(Alignment.BottomStart),
            onEvent = onEvent
        )*/

    }
}

@Composable
fun DragJoySticks (
    modifier: Modifier = Modifier,
    onEvent: (UIEvents) -> Unit = {}
) {
    val offset = remember { mutableStateOf(Offset(0f, 0f)) }

    val pointerInput: suspend PointerInputScope.() -> Unit = {
        awaitPointerEventScope {
            val centerX = size.width / 2f
            val centerY = size.height / 2f

            while (true) {
                // Wait for down event
                val event = awaitPointerEvent()
                val touch = event.changes.firstOrNull() ?: continue
                val position = touch.position
                val relativePosition = floatArrayOf(position.x - centerX, position.y - centerY)
                with(relativePosition) {
                    if(vectorLength() > 170) {
                        normalize()
                        multiply(170f)
                        offset.value = Offset(x, y)
                    } else {
                        offset.value = Offset(x, y)
                    }
                }
                onEvent(UIEvents.OnMove(relativePosition))

                onEvent(UIEvents.OnDown)
                if (touch.pressed.not()) {
                    offset.value = Offset(0f, 0f)
                    onEvent(UIEvents.OnUp)
                }

                // Consume the event
                event.changes.forEach { change ->
                    change.consume()
                }
            }
        }
    }



    Box(
        modifier.pointerInput(Unit, pointerInput)
    ){
        Box(modifier = Modifier
            .padding(60.dp)
            .width(70.dp)
            .height(70.dp)
            .border(
                width = 1.dp,
                color = Color.White,
                shape = RoundedCornerShape(35.dp)
            )
            .align(Alignment.Center)
        )

        Box(modifier = Modifier
            .offset { IntOffset(offset.value.x.roundToInt(), offset.value.y.roundToInt()) }
            .width(50.dp)
            .height(50.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.5f))
            .align(Alignment.Center)
        )
    }
}

@Composable
fun PressJoySticks (
    modifier: Modifier,
    onEvent: (UIEvents) -> Unit = {}
) {

    val pressedState = remember { mutableStateOf(true) }



    Box(
        modifier.padding(60.dp).pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    // Touch Down event
                    pressedState.value = true
                    onEvent(UIEvents.OnDown)

                    // Wait for release
                    val released = tryAwaitRelease()

                    // Touch Up event
                    if (released) {
                        pressedState.value = false
                        onEvent(UIEvents.OnUp)
                    }
                }
            )
        }
    ){
        Box(modifier = Modifier
            .width(70.dp)
            .height(70.dp)
            .border(
                width = 1.dp,
                color = Color.White,
                shape = RoundedCornerShape(35.dp)
            )
            .align(Alignment.Center)
        )
        val innerCircleAlpha = if(pressedState.value) { 0f } else { 0.5f }
        Box(modifier = Modifier
            .width(50.dp)
            .height(50.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = innerCircleAlpha))
            .align(Alignment.Center)
        )
    }
}

@Composable
fun RadioButtonJoyStick(
    modifier: Modifier = Modifier,
    onEvent: (UIEvents) -> Unit = {}
) {
    val checked = remember { mutableStateOf(true) }

    Switch(
        modifier = modifier.padding(60.dp),
        checked = checked.value,
        onCheckedChange = {
            onEvent(UIEvents.Switch(it))
            checked.value = it
        }
    )
}

@Preview
@Composable
fun JoySticksPreview() {
    GameUI()
}


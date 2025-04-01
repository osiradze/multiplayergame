package ge.siradze.multiplayergame.game.presentation.gameView

import android.view.MotionEvent
import ge.siradze.multiplayergame.game.presentation.engine.GameRender
import ge.siradze.multiplayergame.game.presentation.gameUi.UIEvents

object TouchHelper {

    private var lastX = 0f
    private var lastY = 0f

    fun handleEvent(
        event: MotionEvent,
        renderer: GameRender
    ) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val pointerIndex = event.actionIndex
                val pointerId = event.getPointerId(pointerIndex)
                if(pointerId != 0) {
                    return
                }
                lastX = event.getX(pointerIndex)
                lastY = event.getY(pointerIndex)
                renderer.onUIEvent(UIEvents.OnDown)
            }

            MotionEvent.ACTION_MOVE -> {
                for (i in 0 until event.pointerCount) {
                    val pointerId = event.getPointerId(i)
                    if(pointerId != 0) {
                        continue
                    }
                    val x = event.getX(i)
                    val y = event.getY(i)

                    val dx = x - lastX
                    val dy = y - lastY
                    lastX = x
                    lastY = y
                    //renderer.onUIEvent(UIEvents.OnMove(dx, dy))
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = event.actionIndex
                val pointerId = event.getPointerId(pointerIndex)
                if(pointerId != 0) {
                    return
                }
                renderer.onUIEvent(UIEvents.OnUp)
            }
        }
        return
    }
}
package ge.siradze.multiplayergame.game.presentation.engine

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import ge.siradze.multiplayergame.game.presentation.engine.gameUi.UIEvents

class GameView (private val context: Context) : GLSurfaceView(context) {

    private val renderer: GameRender = GameRender(context)


    init {
        setEGLContextClientVersion(3)

        setRenderer(renderer)

        renderMode = RENDERMODE_CONTINUOUSLY
    }

    fun onUIEvent(event: UIEvents) {
        renderer.onUIEvent(event)
    }


    fun release() {
        renderer.release()
    }

    private var lastX = 0f
    private var lastY = 0f

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                lastY = event.y
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - lastX
                val dy = event.y - lastY

                renderer.onUIEvent(UIEvents.OnMove(dx, dy))

                // Update last position
                lastX = event.x
                lastY = event.y


                return true
            }
        }
        return super.onTouchEvent(event)
    }

}


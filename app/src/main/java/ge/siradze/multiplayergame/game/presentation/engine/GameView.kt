package ge.siradze.multiplayergame.game.presentation.engine

import android.content.Context
import android.opengl.GLES31
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import ge.siradze.multiplayergame.game.presentation.gameUi.UIEvents


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

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                val pointerIndex = event.actionIndex
                val pointerId = event.getPointerId(pointerIndex)
                if(pointerId != 0) {
                    return true
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
                    renderer.onUIEvent(UIEvents.OnMove(dx, dy))
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = event.actionIndex
                val pointerId = event.getPointerId(pointerIndex)
                if(pointerId != 0) {
                    return true
                }
                renderer.onUIEvent(UIEvents.OnUp)
            }
        }
        return true
    }

    fun getFPS(): Int {
        val fps = renderer.fps
        renderer.fps = 0
        return fps
    }

}


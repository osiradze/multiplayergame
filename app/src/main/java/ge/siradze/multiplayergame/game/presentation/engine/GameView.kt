package ge.siradze.multiplayergame.game.presentation.engine

import android.content.Context
import android.opengl.GLSurfaceView
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


}


package ge.siradze.multiplayergame.game.presentation.gameView

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import ge.siradze.multiplayergame.game.presentation.GameState
import ge.siradze.multiplayergame.game.presentation.engine.GameRender
import ge.siradze.multiplayergame.game.presentation.gameUi.UIEvents
import ge.siradze.multiplayergame.game.presentation.vibrator.FeedbackSounds


class GameView (
    context: Context,
    state: GameState,
    feedbackSounds: FeedbackSounds
) : GLSurfaceView(context) {

    private val renderer: GameRender = GameRender(
        context = context,
        state = state,
        feedbackSounds = feedbackSounds
    )


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


   /* override fun onTouchEvent(event: MotionEvent): Boolean {
        TouchHelper.handleEvent(event, renderer)
        return true
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }*/

    fun getFPS(): Int {
        val fps = renderer.fps
        renderer.fps = 0
        return fps
    }

}


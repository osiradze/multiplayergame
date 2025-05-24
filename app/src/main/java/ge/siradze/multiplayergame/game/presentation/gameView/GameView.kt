package ge.siradze.multiplayergame.game.presentation.gameView

import android.content.Context
import android.opengl.GLSurfaceView
import androidx.lifecycle.LifecycleCoroutineScope
import ge.siradze.glcore.EngineGlobals
import ge.siradze.glcore.GameState
import ge.siradze.multiplayergame.game.presentation.engine.GameRender
import ge.siradze.multiplayergame.game.presentation.gameUi.UIEvents
import ge.siradze.multiplayergame.game.presentation.feedback.FeedbackSounds
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class GameView (
    context: Context,
    private val lifecycleScope: LifecycleCoroutineScope,
    state: GameState,
    feedbackSounds: FeedbackSounds,
    uiEffect: (GameRender.UIEffect) -> Unit,
) : GLSurfaceView(context) {

    private val _fpsState: MutableStateFlow<Int> = MutableStateFlow(EngineGlobals.fps)
    val fpsState = _fpsState.asStateFlow()

    private val renderer: GameRender = GameRender(
        context = context,
        state = state,
        feedbackSounds = feedbackSounds,
        uiEffect = uiEffect,
    )

    init {
        setEGLContextClientVersion(3)

        setRenderer(renderer)

        renderMode = RENDERMODE_CONTINUOUSLY
        updateFps()
    }

    private fun updateFps() {
        lifecycleScope.launch {
            while (true) {
                delay(1000)
                _fpsState.emit(EngineGlobals.fps)
                EngineGlobals.fps = 0
            }
        }
    }

    fun onUIEvent(event: UIEvents) {
        renderer.onUIEvent(event)
    }


    fun release() {
        renderer.release()
    }
}


package ge.siradze.multiplayergame.game.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import ge.siradze.multiplayergame.game.presentation.gameView.GameView
import ge.siradze.multiplayergame.game.presentation.gameUi.UIEvents
import ge.siradze.multiplayergame.game.presentation.gameUi.buttons.GameUI
import ge.siradze.multiplayergame.game.presentation.gameUi.points.PointsView
import ge.siradze.multiplayergame.game.presentation.feedback.FeedbackSoundsImpl
import ge.siradze.multiplayergame.ui.theme.MultiplayerGameTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class GameActivity : ComponentActivity() {

    private val viewModel: GameVM by viewModel {
        parametersOf(intent.extras?.getInt(PORT, 0))
    }

    private lateinit var gameView : GameView
    private val feedbackSounds = FeedbackSoundsImpl(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel
        gameView = GameView(
            context = this,
            lifecycleScope = lifecycleScope,
            state = viewModel.state,
            feedbackSounds = feedbackSounds,
            uiEffect = viewModel::onUIEffect
        )
        enableEdgeToEdge()
        enableFullScreen()
        setContent {
            MultiplayerGameTheme {
                val uiState = viewModel.uiState.collectAsState()
                val fpsState = gameView.fpsState.collectAsState()
                Scaffold(contentWindowInsets = WindowInsets(0,0,0,0)) { innerPadding ->
                    Box(Modifier.padding(innerPadding)) {
                        ComposeGLSurfaceView()
                        GameUI { event ->
                            handleUIEvents(event)
                        }
                        FPS(fpsState)
                        PointsView(uiState)
                    }
                }
            }
        }
    }


    @Composable
    fun BoxScope.FPS(fps: State<Int>) {
        Box(modifier = Modifier.align(Alignment.BottomStart).padding(10.dp)) {
            Text(text = fps.value.toString())
        }
    }


    private fun handleUIEvents(event: UIEvents) {
        gameView.onUIEvent(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        gameView.release()
    }

    @Composable
    fun ComposeGLSurfaceView() {
        AndroidView(
            factory = {
                gameView
            }
        )
    }


    private fun enableFullScreen() {
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }


    companion object {

        private const val PORT = "port"

        fun start(context: Context, port: Int? = null) {
            val intent = Intent(context, GameActivity::class.java)
            intent.putExtra(PORT, port)
            context.startActivity(intent)
        }
    }

}


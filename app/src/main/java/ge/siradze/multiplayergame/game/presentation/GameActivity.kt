package ge.siradze.multiplayergame.game.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import ge.siradze.multiplayergame.game.presentation.engine.GameView
import ge.siradze.multiplayergame.game.presentation.gameUi.UIEvents
import ge.siradze.multiplayergame.game.presentation.gameUi.buttons.GameUI
import ge.siradze.multiplayergame.ui.theme.MultiplayerGameTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class GameActivity : ComponentActivity() {

    private val viewModel: GameVM by viewModel {
        parametersOf(intent.extras?.getInt(PORT, 0))
    }

    private lateinit var gameView : GameView

    private var fps = mutableIntStateOf(0)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel
        gameView = GameView(context = this)
        fps()
        enableEdgeToEdge()
        setContent {
            MultiplayerGameTheme {
                Scaffold(contentWindowInsets = WindowInsets(0,0,0,0)) { innerPadding ->
                    Box(Modifier.padding(innerPadding)) {
                        ComposeGLSurfaceView()
                        GameUI { event ->
                            handleUIEvents(event)
                        }
                        Box(modifier = Modifier.align(Alignment.BottomStart).padding(10.dp)) { Text(text = fps.intValue.toString()) }
                    }
                }
            }
        }
    }

    private fun fps() {
        lifecycleScope.launch {
            while (true) {
                delay(1000)
                fps.intValue = gameView.getFPS()
            }
        }
    }


    private fun handleUIEvents(event: UIEvents) {
        gameView.onUIEvent(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        gameView.release()
    }

    companion object {

        private const val PORT = "port"

        fun start(context: Context, port: Int? = null) {
            val intent = Intent(context, GameActivity::class.java)
            intent.putExtra(PORT, port)
            context.startActivity(intent)
        }
    }

    @Composable
    fun ComposeGLSurfaceView() {
        AndroidView(
            factory = {
                gameView
            }
        )
    }

}


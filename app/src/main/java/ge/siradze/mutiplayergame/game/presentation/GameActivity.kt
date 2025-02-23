package ge.siradze.mutiplayergame.game.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ge.siradze.mutiplayergame.ui.theme.MutiplayerGameTheme
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class GameActivity : ComponentActivity() {

    private val viewModel: GameVM by viewModel {
        parametersOf(intent.extras?.getInt(PORT))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val port = intent.extras?.getInt(PORT)

        viewModel

        enableEdgeToEdge()
        setContent {
            MutiplayerGameTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "$port",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    companion object {

        private const val PORT = "port"

        fun start(context: Context, port: Int) {
            val intent = Intent(context, GameActivity::class.java)
            intent.putExtra(PORT, port)
            context.startActivity(intent)
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "We Playing on port $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MutiplayerGameTheme {
        Greeting("Android")
    }
}


package ge.siradze.multiplayergame.menu.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import ge.siradze.multiplayergame.game.presentation.GameActivity
import ge.siradze.multiplayergame.menu.presentation.screens.MainScreen
import ge.siradze.multiplayergame.menu.presentation.screens.ServersScreen
import ge.siradze.multiplayergame.ui.theme.MultiplayerGameTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MenuActivity : ComponentActivity() {

    private val viewModel: MenuActivityVM by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleEffects()
        backPress()
        enableEdgeToEdge()
        setContent {
            MultiplayerGameTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    val state = viewModel.state.collectAsState().value

                    when(state){
                        is MenuActivityVM.MenuState.Main -> {
                            MainScreen(
                                state = state,
                                onEvent = viewModel::event,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        is MenuActivityVM.MenuState.Servers -> {
                            ServersScreen(state.servers)
                        }
                    }
                }
            }
        }

        GameActivity.start(this@MenuActivity, null)
    }

    private fun handleEffects() = lifecycleScope.launch {
        viewModel.effect.collectLatest { effect ->
            when(effect) {
                is MenuActivityVM.MenuEffect.StartGame -> {
                    GameActivity.start(this@MenuActivity, effect.port)
                }

                is MenuActivityVM.MenuEffect.ShowToast -> {
                    Toast.makeText(this@MenuActivity, effect.message, Toast.LENGTH_SHORT).show()
                }
                MenuActivityVM.MenuEffect.Finish -> finish()
            }
        }
    }

    private fun backPress() {
        onBackPressedDispatcher.addCallback {
            viewModel.event(MenuActivityVM.MenuEvent.OnBackPress)
        }
    }

}
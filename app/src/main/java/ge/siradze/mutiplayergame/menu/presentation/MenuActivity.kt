package ge.siradze.mutiplayergame.menu.presentation

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
import ge.siradze.mutiplayergame.game.GameActivity
import ge.siradze.mutiplayergame.menu.presentation.screens.MainScreen
import ge.siradze.mutiplayergame.menu.presentation.screens.ServersScreen
import ge.siradze.mutiplayergame.ui.theme.MutiplayerGameTheme
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
            MutiplayerGameTheme {
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
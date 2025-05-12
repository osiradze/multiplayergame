package ge.siradze.multiplayergame.game.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ge.siradze.core.GameState
import ge.siradze.multiplayergame.core.ResultFace
import ge.siradze.multiplayergame.game.domain.GameRepository
import ge.siradze.multiplayergame.game.presentation.engine.GameRender
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameVM(
    private val port: Int,
    private val gameRepository: GameRepository,
    val state: GameState,
): ViewModel() {

    companion object {
        private const val TAG = "GameVM"
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState = _uiState.asStateFlow()


    init {
        viewModelScope.launch {
            if(port == 0) {
                Log.i(TAG, "init: singlePlayer")
                return@launch
            }
            val result = gameRepository.connect(port)
            when(result) {
                is ResultFace.Success -> {
                    Log.i(TAG, "init: connected")
                    listen()
                }
                is ResultFace.Failure -> {
                    Log.e(TAG, "init: ${result.error}")
                }
            }
        }
    }

    private fun listen() = viewModelScope.launch {
        gameRepository.listen { bytes ->
            Log.i(TAG, "listen: $bytes")
        }
    }

    fun onUIEffect(uiEffect: GameRender.UIEffect) {
        when(uiEffect) {
            GameRender.UIEffect.PointUp -> {
                _uiState.value = _uiState.value.copy(
                    points = _uiState.value.points + 1
                )
            }
        }
    }

    data class UiState (
        val points: Int = 0
    )
}
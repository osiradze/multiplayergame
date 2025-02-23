package ge.siradze.mutiplayergame.game.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ge.siradze.mutiplayergame.core.ResultFace
import ge.siradze.mutiplayergame.game.domain.GameRepository
import kotlinx.coroutines.launch

class GameVM(
    private val port: Int,
    private val gameRepository: GameRepository
): ViewModel() {

    companion object {
        private const val TAG = "GameVM"
    }

    init {
        viewModelScope.launch {
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
}
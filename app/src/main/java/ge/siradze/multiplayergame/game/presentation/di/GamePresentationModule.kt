package ge.siradze.multiplayergame.game.presentation.di

import ge.siradze.multiplayergame.game.presentation.GameState
import ge.siradze.multiplayergame.game.presentation.GameStateImpl
import ge.siradze.multiplayergame.game.presentation.GameVM
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val gamePresentationModule = module {
    viewModel {
        (port: Int) -> GameVM(port, get(), GameStateImpl())
    }
}
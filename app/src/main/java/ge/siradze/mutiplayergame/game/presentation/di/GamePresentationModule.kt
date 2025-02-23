package ge.siradze.mutiplayergame.game.presentation.di

import ge.siradze.mutiplayergame.game.presentation.GameVM
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val gamePresentationModule = module {
    viewModel {
        (port: Int) -> GameVM(port, get())
    }
}
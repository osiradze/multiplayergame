package ge.siradze.multiplayergame.menu.presentation.di

import ge.siradze.multiplayergame.menu.presentation.MenuActivityVM
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val menuPresentationModule = module {
    viewModel {
        MenuActivityVM(get(), get(), get())
    }
}
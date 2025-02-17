package ge.siradze.mutiplayergame.menu.di

import ge.siradze.mutiplayergame.menu.data.network.ServerRepository
import ge.siradze.mutiplayergame.menu.data.network.ServerRepositoryImpl
import ge.siradze.mutiplayergame.menu.domain.usecases.GetServersUseCase
import ge.siradze.mutiplayergame.menu.domain.usecases.GetServersUseCaseImpl
import ge.siradze.mutiplayergame.menu.domain.usecases.HostGameUseCase
import ge.siradze.mutiplayergame.menu.domain.usecases.HostGameUseCaseImpl
import ge.siradze.mutiplayergame.menu.presentation.MenuActivityVM
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val menuDataModule = module {
    single<GetServersUseCase> { GetServersUseCaseImpl(get()) }
    single<HostGameUseCase> { HostGameUseCaseImpl(get()) }
    single<ServerRepository> { ServerRepositoryImpl(get()) }
}

val menuPresentationModule = module {
    viewModel {
        MenuActivityVM(get(), get(), get())
    }
}
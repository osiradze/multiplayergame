package ge.siradze.multiplayergame.menu.data.di

import ge.siradze.multiplayergame.menu.domain.ServerRepository
import ge.siradze.multiplayergame.menu.data.network.ServerRepositoryImpl
import ge.siradze.multiplayergame.menu.domain.usecases.GetServersUseCase
import ge.siradze.multiplayergame.menu.domain.usecases.GetServersUseCaseImpl
import ge.siradze.multiplayergame.menu.domain.usecases.HostGameUseCase
import ge.siradze.multiplayergame.menu.domain.usecases.HostGameUseCaseImpl
import org.koin.dsl.module

val menuDataModule = module {
    single<GetServersUseCase> { GetServersUseCaseImpl(get()) }
    single<HostGameUseCase> { HostGameUseCaseImpl(get()) }
    single<ServerRepository> { ServerRepositoryImpl(get()) }
}
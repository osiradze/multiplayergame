package ge.siradze.mutiplayergame.menu.data.di

import ge.siradze.mutiplayergame.menu.domain.ServerRepository
import ge.siradze.mutiplayergame.menu.data.network.ServerRepositoryImpl
import ge.siradze.mutiplayergame.menu.domain.usecases.GetServersUseCase
import ge.siradze.mutiplayergame.menu.domain.usecases.GetServersUseCaseImpl
import ge.siradze.mutiplayergame.menu.domain.usecases.HostGameUseCase
import ge.siradze.mutiplayergame.menu.domain.usecases.HostGameUseCaseImpl
import org.koin.dsl.module

val menuDataModule = module {
    single<GetServersUseCase> { GetServersUseCaseImpl(get()) }
    single<HostGameUseCase> { HostGameUseCaseImpl(get()) }
    single<ServerRepository> { ServerRepositoryImpl(get()) }
}
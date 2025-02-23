package ge.siradze.mutiplayergame.game.di

import ge.siradze.mutiplayergame.game.data.GameRepositoryImpl
import ge.siradze.mutiplayergame.game.data.socket.ServerSocket
import ge.siradze.mutiplayergame.game.data.socket.ServerSocketImpl
import ge.siradze.mutiplayergame.game.domain.GameRepository
import org.koin.dsl.module

val gameDataModule = module {
    single<ServerSocket>  { ServerSocketImpl(get()) }
    single<GameRepository> { GameRepositoryImpl(get()) }
}

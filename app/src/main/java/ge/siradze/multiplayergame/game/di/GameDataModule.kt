package ge.siradze.multiplayergame.game.di

import ge.siradze.multiplayergame.game.data.GameRepositoryImpl
import ge.siradze.multiplayergame.game.data.socket.ServerSocket
import ge.siradze.multiplayergame.game.data.socket.ServerSocketImpl
import ge.siradze.multiplayergame.game.domain.GameRepository
import org.koin.dsl.module

val gameDataModule = module {
    single<ServerSocket>  { ServerSocketImpl(get()) }
    single<GameRepository> { GameRepositoryImpl(get()) }
}

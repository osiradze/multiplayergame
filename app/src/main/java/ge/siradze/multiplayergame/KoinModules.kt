package ge.siradze.multiplayergame

import ge.siradze.multiplayergame.core.network.networkModule
import ge.siradze.multiplayergame.game.di.gameDataModule
import ge.siradze.multiplayergame.game.presentation.di.gamePresentationModule
import ge.siradze.multiplayergame.menu.data.di.menuDataModule
import ge.siradze.multiplayergame.menu.presentation.di.menuPresentationModule

val appModules = listOf (
    networkModule,

    menuDataModule,
    menuPresentationModule,

    gameDataModule,
    gamePresentationModule
)
package ge.siradze.mutiplayergame

import ge.siradze.mutiplayergame.core.network.networkModule
import ge.siradze.mutiplayergame.game.di.gameDataModule
import ge.siradze.mutiplayergame.game.presentation.di.gamePresentationModule
import ge.siradze.mutiplayergame.menu.data.di.menuDataModule
import ge.siradze.mutiplayergame.menu.presentation.di.menuPresentationModule

val appModules = listOf (
    networkModule,

    menuDataModule,
    menuPresentationModule,

    gameDataModule,
    gamePresentationModule
)
package ge.siradze.mutiplayergame

import ge.siradze.mutiplayergame.core.network.networkModule
import ge.siradze.mutiplayergame.menu.di.menuDataModule
import ge.siradze.mutiplayergame.menu.di.menuPresentationModule

val appModules = listOf (
    networkModule,
    menuDataModule,
    menuPresentationModule
)
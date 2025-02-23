package ge.siradze.multiplayergame.game.presentation.engine.gameUi

sealed class UIEvents {
    data object OnDown: UIEvents()
    data object OnUp: UIEvents()
}
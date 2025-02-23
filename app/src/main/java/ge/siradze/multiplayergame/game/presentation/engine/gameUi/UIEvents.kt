package ge.siradze.multiplayergame.game.presentation.engine.gameUi

sealed class UIEvents {
    data object OnDown: UIEvents()
    data object OnUp: UIEvents()
    data class OnMove(val x: Float, val y: Float): UIEvents()
}
package ge.siradze.multiplayergame.game.presentation.gameUi

sealed class UIEvents {
    data object OnDown: UIEvents()
    data object OnUp: UIEvents()
    data class OnMove(val move: FloatArray): UIEvents()
    data object onTap: UIEvents()
}
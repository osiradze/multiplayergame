package ge.siradze.multiplayergame.game.presentation.gameUi

sealed class UIEvents {
    data object OnDown: UIEvents()
    data object OnUp: UIEvents()
    class OnMove(val move: FloatArray): UIEvents()
    data class Switch(val isOn: Boolean): UIEvents()
}
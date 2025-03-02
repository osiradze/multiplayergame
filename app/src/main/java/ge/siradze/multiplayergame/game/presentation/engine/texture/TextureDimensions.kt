package ge.siradze.multiplayergame.game.presentation.engine.texture

data class TextureDimensions(
    val rows: Int,
    val columns: Int
) {
    val stepX: Float = 1f / columns
    val stepY: Float = 1f / rows
}
package ge.siradze.glcore.texture

data class TextureDimensions(
    val rows: Int,
    val columns: Int,
    val bitmapRes: Int,
) {
    val size = rows * columns
    val stepX: Float = 1f / columns
    val stepY: Float = 1f / rows
}
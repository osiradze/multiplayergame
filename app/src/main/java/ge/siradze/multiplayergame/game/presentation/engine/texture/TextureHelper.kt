package ge.siradze.multiplayergame.game.presentation.engine.texture

object TextureHelper {
    private var loadedTextureNumber: Int = 0
    fun getTextureOffset(newTextureNumber: Int): Int {
        val currentNumber = loadedTextureNumber
        loadedTextureNumber += newTextureNumber
        return currentNumber
    }
}
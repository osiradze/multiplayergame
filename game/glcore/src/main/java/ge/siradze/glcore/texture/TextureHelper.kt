package ge.siradze.glcore.texture


/*
* In OpenGL we are limited to 32 textures per shader, so we need to keep track of loaded textures
* This class is used to keep track of loaded textures and provide offset for each texture
*/
class TextureCounter {
    private var loadedTextureNumber: Int = 0
    fun getTextureOffset(newTextureNumber: Int): Int {
        val currentNumber = loadedTextureNumber
        loadedTextureNumber += newTextureNumber
        return currentNumber
    }
}
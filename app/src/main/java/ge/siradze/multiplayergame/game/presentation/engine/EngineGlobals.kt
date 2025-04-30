package ge.siradze.multiplayergame.game.presentation.engine

/**
 * Every speed value game of should be multiplied by deltaTime,
 * Otherwise, it will be frame rate dependent.
*/

object EngineGlobals {
    var fps: Int = 120
    var deltaTime: Float = 1f / fps
    private var lastFrameTime: Long = System.nanoTime()
    private var currentTime = System.nanoTime()

    fun init() {
        lastFrameTime = System.nanoTime()
    }
    fun update() {
        currentTime = System.nanoTime()
        deltaTime = (currentTime - lastFrameTime) / 1_000_000_000f
        lastFrameTime = currentTime
    }
}
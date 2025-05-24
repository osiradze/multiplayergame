package ge.siradze.glcore

/**
 * Every speed value game of should be multiplied by deltaTime,
 * Otherwise, it will be frame rate dependent.
*/

object EngineGlobals {
    // at start we have wrong numbers so we need to wait for 60 frames
    private var startUpTime = 60

    var fps: Int = 120
    var deltaTime: Float = 0.007790469f
    private var lastFrameTime: Long = 0
    private var currentTime: Long = 0

    fun init() {
        lastFrameTime = System.nanoTime()
    }
    fun update() {
        currentTime = System.nanoTime()
        if(startUpTime < 0) {
            deltaTime = (currentTime - lastFrameTime) / 1_000_000_000f
        } else {
            startUpTime--
        }
        lastFrameTime = currentTime
    }
}
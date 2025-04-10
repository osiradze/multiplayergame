package ge.siradze.multiplayergame.game.presentation.engine

/**
 * Every speed value game of should be multiplied by deltaTime,
 * Otherwise, it will be frame rate dependent.
*/

object EngineGlobals {
    var fps: Int = 120
    var deltaTime: Float = 1f / fps
}
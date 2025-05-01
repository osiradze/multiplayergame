package ge.siradze.multiplayergame.game.presentation.engine.camera

import android.opengl.GLES20.glUniform2f
import ge.siradze.multiplayergame.game.presentation.GameState
import ge.siradze.multiplayergame.game.presentation.engine.EngineGlobals
import ge.siradze.multiplayergame.game.presentation.engine.extensions.x
import ge.siradze.multiplayergame.game.presentation.engine.extensions.y
import ge.siradze.multiplayergame.game.presentation.engine.objects.player.PlayerData

class Camera(state: GameState) {

    private var playerProperties: PlayerData.Properties? = null

    private val position: FloatArray = state.get(Camera::class.qualifiedName) as? FloatArray
        ?: floatArrayOf(0.0f, 0.0f).also {
            state.set(Camera::class.qualifiedName, it)
        }

    private val followSpeed = 2f

    fun followPlayer(playerProps: PlayerData.Properties) {
        playerProperties = playerProps
    }

    fun update() {
        val player = playerProperties ?: return

        position[0] += (player.position.x - position[0]) * followSpeed * EngineGlobals.deltaTime
        position[1] += (player.position.y - position[1]) * followSpeed * EngineGlobals.deltaTime
    }

    fun bindUniform(location: Int) {
        glUniform2f(location, position.x, position.y)
    }
}

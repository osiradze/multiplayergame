package ge.siradze.multiplayergame.game.presentation.engine.camera

import android.opengl.GLES20.glUniform2f
import ge.siradze.multiplayergame.game.presentation.GameState
import ge.siradze.multiplayergame.game.presentation.engine.extensions.x
import ge.siradze.multiplayergame.game.presentation.engine.extensions.y
import ge.siradze.multiplayergame.game.presentation.engine.objects.player.PlayerData

class Camera(
    state: GameState
) {
    private var playerProperties: PlayerData.Properties? = null
    private val position: FloatArray = state.get(Camera::class.qualifiedName) as? FloatArray ?:
    arrayOf(0.0f, 0.0f).toFloatArray().also {
        state.set(Camera::class.qualifiedName, it)
    }
    private val followSpeed = 0.011f

    fun followPlayer(playerProperties: PlayerData.Properties) {
        this.playerProperties = playerProperties
    }


    fun update() {
        playerProperties ?: return
        position[0] += (playerProperties!!.position.x - position[0]) * followSpeed
        position[1] += (playerProperties!!.position.y - position[1]) * followSpeed
    }

    fun bindUniform(location: Int) {
        glUniform2f(location, position.x, position.y)
    }
}
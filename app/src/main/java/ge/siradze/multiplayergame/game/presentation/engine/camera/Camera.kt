package ge.siradze.multiplayergame.game.presentation.engine.camera

import android.opengl.GLES20.glUniform2f
import ge.siradze.multiplayergame.game.presentation.engine.extensions.x
import ge.siradze.multiplayergame.game.presentation.engine.extensions.y
import ge.siradze.multiplayergame.game.presentation.engine.objects.player.PlayerData

object Camera {
    private var playerProperties: PlayerData.Properties? = null
    private val position: FloatArray = arrayOf(0.0f, 0.0f).toFloatArray()
    private const val FOLLOW_SPEED = 0.011f

    fun followPlayer(playerProperties: PlayerData.Properties) {
        this.playerProperties = playerProperties
    }


    fun update() {
        playerProperties ?: return
        position[0] += (playerProperties!!.position.x - position[0]) * FOLLOW_SPEED
        position[1] += (playerProperties!!.position.y - position[1]) * FOLLOW_SPEED
    }

    fun bindUniform(location: Int) {
        glUniform2f(location, position.x, position.y)
    }
}
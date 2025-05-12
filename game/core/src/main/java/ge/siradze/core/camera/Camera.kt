package ge.siradze.core.camera

import android.opengl.GLES20.glUniform2f
import ge.siradze.core.EngineGlobals
import ge.siradze.core.GameState
import ge.siradze.core.extensions.x
import ge.siradze.core.extensions.y

class Camera(state: GameState) {

    private var playerPosition: FloatArray? = null

    private val position: FloatArray = state.get(Camera::class.qualifiedName) as? FloatArray
        ?: floatArrayOf(0.0f, 0.0f).also {
            state.set(Camera::class.qualifiedName, it)
        }

    private val followSpeed = 2f

    fun followPlayer(positionArray: FloatArray) {
        playerPosition = positionArray
    }

    fun update() {
        val player = playerPosition ?: return

        position[0] += (player.x - position[0]) * followSpeed * EngineGlobals.deltaTime
        position[1] += (player.y - position[1]) * followSpeed * EngineGlobals.deltaTime
    }

    fun bindUniform(location: Int) {
        glUniform2f(location, position.x, position.y)
    }
}

package ge.siradze.multiplayergame.game.presentation.engine.objects.player

import ge.siradze.multiplayergame.game.presentation.engine.EngineGlobals
import ge.siradze.multiplayergame.game.presentation.engine.extensions.middlePoint
import ge.siradze.multiplayergame.game.presentation.engine.extensions.normalize
import ge.siradze.multiplayergame.game.presentation.engine.extensions.rotate
import ge.siradze.multiplayergame.game.presentation.engine.extensions.scale
import ge.siradze.multiplayergame.game.presentation.engine.extensions.signedAngleBetweenVectors
import ge.siradze.multiplayergame.game.presentation.engine.extensions.toBuffer
import ge.siradze.multiplayergame.game.presentation.engine.extensions.x
import ge.siradze.multiplayergame.game.presentation.engine.extensions.y
import ge.siradze.multiplayergame.game.presentation.engine.shader.CameraShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.RatioShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderAttribLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderUniformLocation
import ge.siradze.multiplayergame.game.presentation.gameUi.UIEvents
import java.nio.Buffer

class PlayerData {

    class Vertex {
        private val scale = 0.02f
        private val numberOfFloatsPerVertex = 2
        private val data: FloatArray = floatArrayOf(
            0.0f, 0.5f,
            0.5f, -0.5f,
            -0.5f, -0.5f,
        ).apply{
            scale(scale, numberOfFloatsPerVertex)
        }

        val middlePoint = data.middlePoint(numberOfFloatsPerVertex)

        val pointNumber = data.size / numberOfFloatsPerVertex
        val stride = numberOfFloatsPerVertex * Float.SIZE_BYTES
        val bufferSize = data.size * Float.SIZE_BYTES

        fun getBuffer(): Buffer = data.toBuffer()

    }

    class ShaderLocations(
        val vertex : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_position"
        ),
        val ratio: ShaderLocation = RatioShaderLocation(),
        val camera: ShaderLocation = CameraShaderLocation(),

        val middlePoint : ShaderLocation = ShaderUniformLocation(
            name = "u_middlePoint"
        ),
        val position: ShaderLocation = ShaderUniformLocation(
            name = "u_position"
        ),
        val direction: ShaderLocation = ShaderUniformLocation(
            name = "u_direction"
        ),
        val velocity: ShaderLocation = ShaderUniformLocation(
            name = "u_velocity"
        ),
    )

    class Properties(
        val position: FloatArray = floatArrayOf(0.0f, 0.0f),
        var direction: FloatArray = floatArrayOf(0.0f, 1.0f).apply {
            normalize()
        },
        private var targetDirection: FloatArray = floatArrayOf(0.0f, 1.0f).apply {
            normalize()
        },
        private var velocity: Float = 0f,
    ) {
        private val rotateSpeed = 15f
        private var gas = false
        private val gasForce = 0.05f
        private val maxSpeed = 0.9f
        private val deceleration = 0.98f

        fun update() {
            if (gas && velocity < maxSpeed) {
                velocity += gasForce
            } else {
                velocity *= deceleration
            }
            position[0] += direction[0] * velocity * EngineGlobals.deltaTime
            position[1] += direction[1] * velocity * EngineGlobals.deltaTime
            val angle = signedAngleBetweenVectors(targetDirection, direction)
            direction.rotate(-angle * rotateSpeed * EngineGlobals.deltaTime)
        }

        fun addForce(force: FloatArray) {
            direction[0] += (position.x - force.x) * 10f
            direction[1] += (position.y - force.y) * 10f
            direction.normalize()
        }

        fun onUIEvent(event: UIEvents) {
            when(event) {
                UIEvents.OnDown -> {
                    gas = true
                }
                UIEvents.OnUp -> {
                    gas = false
                }

                is UIEvents.OnMove -> {
                    targetDirection[0] = event.move[0]
                    targetDirection[1] = -event.move[1]
                }

                is UIEvents.onTap -> {
                    //maxSpeed += 0.0001f
                }
            }
        }
    }
}
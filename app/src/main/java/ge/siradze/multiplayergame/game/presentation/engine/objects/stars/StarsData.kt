package ge.siradze.multiplayergame.game.presentation.engine.objects.stars

import ge.siradze.multiplayergame.game.presentation.engine.EngineGlobals
import ge.siradze.multiplayergame.game.presentation.engine.extensions.toBuffer
import ge.siradze.multiplayergame.game.presentation.engine.shader.CameraShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.RatioShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderAttribLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderUniformLocation
import java.nio.Buffer
import kotlin.random.Random

class StarsData {

    @Suppress("NOTHING_TO_INLINE")
    class Vertex(
        val numberOfPoints: Int = 1000
    ) {
        // 2 for position, 1 for camera speed and 1 for brightness
        private val numberOfFloatsPerVertex = 4

        private val data: FloatArray = FloatArray(numberOfPoints * numberOfFloatsPerVertex)

        val stride = numberOfFloatsPerVertex * Float.SIZE_BYTES
        val bufferSize = data.size * Float.SIZE_BYTES
        fun getBuffer(): Buffer = data.toBuffer()

        private val cameraSpeeds = floatArrayOf(
            0.2f, 0.4f, 0.6f,
        )

        init {
            generatePoints()
        }



        private fun generatePoints() {
            for (i in 0 until numberOfPoints) {
                // position
                data[px(i)] = (Random.nextFloat() - 0.5f) * 8
                data[py(i)] = (Random.nextFloat() - 0.5f) * 8
                // camera speed
                data[camera(i)] = cameraSpeeds[Random.nextInt(cameraSpeeds.size)]
                // brightness
                data[brightness(i)] = Random.nextFloat() * 0.9f
            }
        }

        private inline fun px(i: Int) = i * numberOfFloatsPerVertex
        private inline fun py(i: Int) = i * numberOfFloatsPerVertex + 1
        private inline fun camera(i: Int) = i * numberOfFloatsPerVertex + 2
        private inline fun brightness(i: Int) = i * numberOfFloatsPerVertex + 3
    }

    class ShaderLocations(
        val vertex : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_position"
        ),
        val cameraSpeed : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_camera_speed"
        ),
        val brightness : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_brightness"
        ),
        val camera : ShaderLocation = CameraShaderLocation(),
        val ratio : RatioShaderLocation = RatioShaderLocation()

    )
}
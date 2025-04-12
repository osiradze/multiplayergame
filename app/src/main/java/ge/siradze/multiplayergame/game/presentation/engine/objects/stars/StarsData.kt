package ge.siradze.multiplayergame.game.presentation.engine.objects.stars

import ge.siradze.multiplayergame.game.presentation.engine.EngineGlobals
import ge.siradze.multiplayergame.game.presentation.engine.extensions.toBuffer
import ge.siradze.multiplayergame.game.presentation.engine.shader.CameraShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderAttribLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderUniformLocation
import java.nio.Buffer
import kotlin.random.Random

class StarsData {

    @Suppress("NOTHING_TO_INLINE")
    class Vertex(
        val numberOfPoints: Int = 6000
    ) {
        // 4 floats per vertex, 2 for position, 2 for velocity
        val numberOfFloatsPerVertex = 5

        private val data: FloatArray = FloatArray(numberOfPoints * numberOfFloatsPerVertex)

        val stride = numberOfFloatsPerVertex * Float.SIZE_BYTES
        val bufferSize = data.size * Float.SIZE_BYTES
        fun getBuffer(): Buffer = data.toBuffer()

        init {
            generatePoints()
        }

        private fun generatePoints() {
            for (i in 0 until numberOfPoints) {
                // position
                data[px(i)] = (Random.nextFloat() - 0.5f) * 4
                data[py(i)] = (Random.nextFloat() - 0.5f) * 4
                // velocity
                data[vx(i)] = -0.02f * EngineGlobals.deltaTime
                data[vy(i)] = -0.02f * EngineGlobals.deltaTime
                // brightness
                data[vy(i) + 1] = Random.nextFloat() * 0.9f


            }
        }

        private inline fun px(i: Int) = i * numberOfFloatsPerVertex
        private inline fun py(i: Int) = i * numberOfFloatsPerVertex + 1
        private inline fun vx(i: Int) = i * numberOfFloatsPerVertex + 2
        private inline fun vy(i: Int) = i * numberOfFloatsPerVertex + 3
    }

    class ShaderLocations(
        val vertex : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_position"
        ),
        val brightness : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_brightness"
        ),
        val floatsPerVertex : ShaderLocation = ShaderUniformLocation(
            name = "floats_per_vertex"
        ),
        val camera : ShaderLocation = CameraShaderLocation()
    )
}
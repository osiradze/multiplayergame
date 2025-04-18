package ge.siradze.multiplayergame.game.presentation.engine.objects.planets

import ge.siradze.multiplayergame.game.presentation.engine.extensions.toBuffer
import ge.siradze.multiplayergame.game.presentation.engine.extensions.x
import ge.siradze.multiplayergame.game.presentation.engine.extensions.y
import ge.siradze.multiplayergame.game.presentation.engine.objects.AttributeData
import ge.siradze.multiplayergame.game.presentation.engine.shader.CameraShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.RatioShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderAttribLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderUniformLocation
import ge.siradze.multiplayergame.game.presentation.engine.texture.TextureDimensions
import java.nio.Buffer
import kotlin.random.Random

class PlanetsData {
    class Vertex(
        val numberOfPlanets: Int,
        private val planetMinSize: Float = 0.5f,
        private val planetMaxSize: Float = 0.5f,
        private val textureDimensions: TextureDimensions
    ): AttributeData() {
        // 2 position + 1 size + 4 texture coordinates + 3 color + 1 collision flag + 1 isDestroyed flag
        override val numberOfFloatsPerVertex = 12
        override val typeSize = Float.SIZE_BYTES
        override val size = numberOfPlanets * numberOfFloatsPerVertex
        private val data: FloatArray = FloatArray(size)

        override fun getBuffer(): Buffer = data.toBuffer()

        init {
            generatePoints()
        }

        private fun generatePoints() {
            val lastPlanetPosition = floatArrayOf(0f,0f)
            for (i in 0 until numberOfPlanets) {

                //position
                data[i * numberOfFloatsPerVertex + 0] = lastPlanetPosition.x + (Random.nextFloat()) * 4f
                data[i * numberOfFloatsPerVertex + 1] = lastPlanetPosition.y + (Random.nextFloat()) * 4f - 2f
                lastPlanetPosition[0] = data[i * numberOfFloatsPerVertex + 0]
                lastPlanetPosition[1] = data[i * numberOfFloatsPerVertex + 1]

                //size
                data[i * numberOfFloatsPerVertex + 2] = Random.nextFloat() * planetMaxSize + planetMinSize

                // texture coordinates
                val randomX = Random.nextInt(until = textureDimensions.columns) + 1
                val randomY = Random.nextInt(until = textureDimensions.rows) + 1

                data[i * numberOfFloatsPerVertex + 3] = textureDimensions.stepX * (randomX - 1)
                data[i * numberOfFloatsPerVertex + 4] = textureDimensions.stepY * (randomY - 1)
                data[i * numberOfFloatsPerVertex + 5] = textureDimensions.stepX
                data[i * numberOfFloatsPerVertex + 6] = textureDimensions.stepY

                // color
                data[i * numberOfFloatsPerVertex + 7] = Random.nextFloat() // 0.5f + 0.5f
                data[i * numberOfFloatsPerVertex + 8] = Random.nextFloat() //* 0.5f + 0.5f
                data[i * numberOfFloatsPerVertex + 9] = Random.nextFloat() //* 0.5f + 0.5f
            }
        }
    }

    // For getting data from GPU about collision
    class CollisionData {
        val data: FloatArray = FloatArray(9)
        val buffer: Buffer = data.toBuffer()
        val bufferSize = data.size * Float.SIZE_BYTES
    }

    class ShaderLocations(
        val vertex : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_position"
        ),
        val size : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_size"
        ),
        val textureCoordinates : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_texture_coordinates"
        ),
        val color : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_color"
        ),

        val screenWidth : ShaderUniformLocation = ShaderUniformLocation(
            name = "u_screen_width"
        ),

        val ratio: ShaderLocation = RatioShaderLocation(),
        var camera: ShaderLocation = CameraShaderLocation(),

        val texture: ShaderLocation = ShaderUniformLocation(
            name = "u_texture"
        ),
        val floatsPerVertex: ShaderLocation = ShaderUniformLocation(
            name = "u_floats_per_vertex"
        ),
        val playerPosition: ShaderLocation = ShaderUniformLocation(
            name = "u_player_position"
        ),
        val collision : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_collision"
        ),
        val isDestroyed : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_isDestroyed"
        ),
        val push: ShaderUniformLocation = ShaderUniformLocation(
            name = "u_push"
        ),
        val drawLine : ShaderUniformLocation = ShaderUniformLocation(
            name = "u_drawLine"
        ),
    )
}
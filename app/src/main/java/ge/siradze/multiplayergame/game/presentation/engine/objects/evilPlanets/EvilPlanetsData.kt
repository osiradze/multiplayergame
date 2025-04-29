package ge.siradze.multiplayergame.game.presentation.engine.objects.evilPlanets

import ge.siradze.multiplayergame.game.presentation.engine.extensions.multiply
import ge.siradze.multiplayergame.game.presentation.engine.extensions.normalize
import ge.siradze.multiplayergame.game.presentation.engine.extensions.rotate
import ge.siradze.multiplayergame.game.presentation.engine.extensions.toBuffer
import ge.siradze.multiplayergame.game.presentation.engine.extensions.x
import ge.siradze.multiplayergame.game.presentation.engine.extensions.y
import ge.siradze.multiplayergame.game.presentation.engine.objects.AttributeData
import ge.siradze.multiplayergame.game.presentation.engine.objects.planets.PlanetsData
import ge.siradze.multiplayergame.game.presentation.engine.shader.CameraShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.RatioShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderAttribLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderUniformLocation
import ge.siradze.multiplayergame.game.presentation.engine.texture.TextureDimensions
import java.nio.Buffer
import kotlin.random.Random

class EvilPlanetsData {

    companion object {
        const val MIN_SIZE = 0.3f
        const val MAX_SIZE = 0.3f

        const val NUMBER_OF_FLOATS_PER_VERTEX = 12
        const val NUMBER_OF_EVIL_PLANET_PER_PLANET = 10
        const val SPAWN_RADIUS = 2f


        const val PX = 0
        const val PY = 1
        const val SIZE = 2
        const val TX = 3
        const val TY = 4
        const val TW = 5
        const val TH = 6

        const val CR = 7
        const val CB = 8
        const val CG = 9

    }

    class Vertex(
        private val minSize: Float = MIN_SIZE,
        private val sizeRange: Float = MAX_SIZE,
        private val textureDimensions: TextureDimensions,
        private val planets: FloatArray,
    ): AttributeData() {
        // 2 position + 1 size + 4 texture coordinates + 3 color + 1 collision flag + 1 isDestroyed flag
        override val numberOfFloatsPerVertex = NUMBER_OF_FLOATS_PER_VERTEX
        override val typeSize = Float.SIZE_BYTES
        val numberOfPlanets = planets.size / PlanetsData.NUMBER_OF_FLOATS_PER_VERTEX * NUMBER_OF_EVIL_PLANET_PER_PLANET
        override val size = numberOfPlanets * numberOfFloatsPerVertex
        private val data: FloatArray = FloatArray(size)

        override fun getBuffer(): Buffer = data.toBuffer()

        private val direction = floatArrayOf(0f, 1f)

        init {
            generatePoints()
        }

        private fun generatePoints() {

            for (i in 0 until numberOfPlanets) {
                val planetX = planets[i / NUMBER_OF_EVIL_PLANET_PER_PLANET * PlanetsData.NUMBER_OF_FLOATS_PER_VERTEX]
                val planetY = planets[i / NUMBER_OF_EVIL_PLANET_PER_PLANET* PlanetsData.NUMBER_OF_FLOATS_PER_VERTEX + 1]

                //position
                direction.rotate(360f / NUMBER_OF_EVIL_PLANET_PER_PLANET)

                direction.normalize()
                direction.multiply(SPAWN_RADIUS)

                data[i * numberOfFloatsPerVertex + PX] = planetX + direction.x
                data[i * numberOfFloatsPerVertex + PY] = planetY + direction.y

                //size
                data[i * numberOfFloatsPerVertex + SIZE] = Random.nextFloat() * sizeRange + minSize

                // texture coordinates
                val randomX = Random.nextInt(until = textureDimensions.columns) + 1
                val randomY = Random.nextInt(until = textureDimensions.rows) + 1

                data[i * numberOfFloatsPerVertex + TX] = textureDimensions.stepX * (randomX - 1)
                data[i * numberOfFloatsPerVertex + TY] = textureDimensions.stepY * (randomY - 1)
                data[i * numberOfFloatsPerVertex + TW] = textureDimensions.stepX
                data[i * numberOfFloatsPerVertex + TH] = textureDimensions.stepY

                // color
                val min = 0.5f
                data[i * numberOfFloatsPerVertex + CR] = Random.nextFloat()  + min
                data[i * numberOfFloatsPerVertex + CB] = Random.nextFloat()  + min
                data[i * numberOfFloatsPerVertex + CG] = Random.nextFloat()  + min
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
            name = "a_position",
            offset = 0
        ),
        val size : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_size",
            offset = 2
        ),
        val textureCoordinates : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_texture_coordinates",
            offset = 3
        ),
        val color : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_color",
            offset = 7
        ),
        val isDestroyed : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_isDestroyed",
            offset = 11
        ),

        // required to convert pixel size to world units
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
        val destructible: ShaderUniformLocation = ShaderUniformLocation(
            name = "u_destructible"
        ),
        val drawLine : ShaderUniformLocation = ShaderUniformLocation(
            name = "u_drawLine"
        ),
    )
}
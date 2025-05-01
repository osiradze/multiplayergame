package ge.siradze.multiplayergame.game.presentation.engine.objects.asteroids

import ge.siradze.multiplayergame.game.presentation.engine.EngineGlobals
import ge.siradze.multiplayergame.game.presentation.engine.extensions.multiply
import ge.siradze.multiplayergame.game.presentation.engine.extensions.normalize
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
import kotlin.random.Random

/**
 * Allocating Memory For MAX asteroids from start.
 * Because GPU handle the data and not CPU, this solution is better than sending new instance of data to GPU on every allocation.
 * When we will want to add new asteroid, we will send the data and index to GPU.
**/


object AsteroidsData {
    /**
     * 2 - position
     * 2 - velocity
     * 1 - size
     * 4 - texture coordinates
     * 3 - color
     * 1 - is alive flag, 0 - no, 1 - yes
     **/
    private const val NUMBER_OF_FLOATS_PER_VERTEX = 13
    private const val PX = 0
    private const val PY = 1
    private const val VX = 2
    private const val VY = 3
    private const val SIZE = 4
    private const val TX = 5
    private const val TY = 6
    private const val TW = 7
    private const val TH = 8
    private const val CR = 9
    private const val CB = 10
    private const val CG = 11
    private const val IS_ALIVE = 12


    const val NUMBER_OF_ASTEROIDS = 500
    private const val MIN_SIZE = 0.1f
    private const val SIZE_RANGE = 0.2f
    private const val SPAWN_DISTANCE = 8f


    class Vertex(
        textureDimensions: TextureDimensions
    ) : AttributeData() {
        override val numberOfFloatsPerVertex = NUMBER_OF_FLOATS_PER_VERTEX
        override val typeSize = Float.SIZE_BYTES
        override val size = NUMBER_OF_ASTEROIDS * numberOfFloatsPerVertex
        val data: FloatArray = FloatArray(size) { 0f }

        init {
            generatePoints(
                this,
                textureDimensions = textureDimensions
            )
        }

        override fun getBuffer() = data.toBuffer()
    }

    // For getting data from GPU about collision
    class CollisionData {
        val data: FloatArray = FloatArray(9)
    }

    class ShaderLocations(
        val vertex : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_position",
            offset = 0
        ),
        val velocity : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_velocity",
            offset = 2

        ),
        val size : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_size",
            offset = 4
        ),
        val textureCoordinates : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_texture_coordinates",
            offset = 5
        ),
        val color : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_color",
            offset = 9
        ),
        val isAlive : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_isAlive",
            offset = 12
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
        val deltaTime : ShaderUniformLocation = ShaderUniformLocation(
            name = "u_delta_time"
        ),
        val readerOffset : ShaderUniformLocation = ShaderUniformLocation(
            name = "u_reader_offset"
        ),
    )


    fun generatePoints(
        vertex: Vertex,
        textureDimensions: TextureDimensions
    ) {
        for (i in 0 until NUMBER_OF_ASTEROIDS) {
            // position
            vertex.data[i * NUMBER_OF_FLOATS_PER_VERTEX + PX] = Random.nextFloat() * SPAWN_DISTANCE - SPAWN_DISTANCE / 2
            vertex.data[i * NUMBER_OF_FLOATS_PER_VERTEX + PY] = Random.nextFloat() * SPAWN_DISTANCE - SPAWN_DISTANCE / 2

            val velocityVector = floatArrayOf(
                Random.nextFloat(),
                Random.nextFloat(),
            )
            velocityVector.normalize()
            val speed = EngineGlobals.deltaTime * (Math.random().toFloat() * 0.5f + 0.5f) * 0.05f
            velocityVector.multiply(speed)

            vertex.data[i * NUMBER_OF_FLOATS_PER_VERTEX + VX] = velocityVector.x
            vertex.data[i * NUMBER_OF_FLOATS_PER_VERTEX + VY] = velocityVector.y
            // size
            vertex.data[i * NUMBER_OF_FLOATS_PER_VERTEX + SIZE] = Math.random().toFloat() * SIZE_RANGE + MIN_SIZE

            // texture coordinates
            val randomX = Random.nextInt(until = textureDimensions.columns) + 1
            val randomY = Random.nextInt(until = textureDimensions.rows) + 1
            vertex.data[i * NUMBER_OF_FLOATS_PER_VERTEX + TX] = textureDimensions.stepX * (randomX - 1)
            vertex.data[i * NUMBER_OF_FLOATS_PER_VERTEX + TY] = textureDimensions.stepY * (randomY - 1)
            vertex.data[i * NUMBER_OF_FLOATS_PER_VERTEX + TW] = textureDimensions.stepX
            vertex.data[i * NUMBER_OF_FLOATS_PER_VERTEX + TH] = textureDimensions.stepY

            // color
            val value = 0.3f + Random.nextFloat()
            vertex.data[i * NUMBER_OF_FLOATS_PER_VERTEX + CR] = value
            vertex.data[i * NUMBER_OF_FLOATS_PER_VERTEX + CB] = value
            vertex.data[i * NUMBER_OF_FLOATS_PER_VERTEX + CG] = value

            // alive flag
            vertex.data[i * NUMBER_OF_FLOATS_PER_VERTEX + IS_ALIVE] = 1f
        }

    }



}
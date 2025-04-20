package ge.siradze.multiplayergame.game.presentation.engine.objects.asteroids

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
import java.nio.Buffer
import kotlin.random.Random

/**
 * Allocating Memory For MAX asteroids from start.
 * Because GPU handle the data and not CPU, this solution is better than sending new instance of data to GPU on every allocation.
 * When we will want to add new asteroid, we will send the data and index to GPU.
**/


class AsteroidsData {
    companion object {
        /**
         * 2 - position
         * 2 - velocity
         * 1 - size
         * 4 - texture coordinates
         * 1 - empty flag, 0 - empty, 1 - alive
         **/
        const val NUMBER_OF_FLOATS_PER_VERTEX = 10

        const val NUMBER_OF_ASTEROIDS = 50
        const val MIN_SIZE = 0.05f
        const val SIZE_RANGE = 0.05f
    }

    class Vertex(
        private val minSize: Float = MIN_SIZE,
        private val sizeRange: Float = SIZE_RANGE,
        private val textureDimensions: TextureDimensions
    ) : AttributeData() {

        override val numberOfFloatsPerVertex = NUMBER_OF_FLOATS_PER_VERTEX
        override val typeSize = Float.SIZE_BYTES
        override val size = NUMBER_OF_ASTEROIDS * numberOfFloatsPerVertex
        private val data: FloatArray = FloatArray(size) { 0f }

        override fun getBuffer() = data.toBuffer()

        fun addAsteroid(
            spawnPosition: FloatArray
        ): FloatArray {
            // new Asteroid data
            val newData = FloatArray(numberOfFloatsPerVertex)
            val vector = floatArrayOf(
                Math.random().toFloat(),
                Math.random().toFloat()
            )

            // velocity
            newData[2] = -vector.x
            newData[3] = -vector.y

            vector.normalize()
            // position
            newData[0] = spawnPosition.x + vector.x
            newData[1] = spawnPosition.y + vector.y


            // size
            newData[4] = Math.random().toFloat() * sizeRange + minSize

            // texture coordinates
            val randomX = Random.nextInt(until = textureDimensions.columns) + 1
            val randomY = Random.nextInt(until = textureDimensions.rows) + 1

            data[4] = textureDimensions.stepX * (randomX - 1)
            data[5] = textureDimensions.stepY * (randomY - 1)
            data[6] = textureDimensions.stepX
            data[7] = textureDimensions.stepY

            return newData
        }

    }

    class CreateAsteroidData {
        // 2 extra data for:
        // 1. do we request adding new asteroid? 1 - yes, 0 - no
        // 2. index to add new asteroid at (or replace)
        val data: FloatArray = FloatArray(NUMBER_OF_FLOATS_PER_VERTEX + 2)
        val buffer: Buffer = data.toBuffer()
        val bufferSize = data.size * Float.SIZE_BYTES
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
        val velocity : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_velocity"
        ),
        val size : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_size"
        ),
        val textureCoordinates : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_texture_coordinates"
        ),
        val isAlive : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_isAlive"
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
    )



}
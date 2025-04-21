package ge.siradze.multiplayergame.game.presentation.engine.objects.asteroids

import android.util.Log
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
import java.nio.Buffer
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
     * 1 - empty flag, 0 - empty, 1 - alive
     **/
    const val NUMBER_OF_FLOATS_PER_VERTEX = 10

    // 2 extra data for:
    // 1. do we request adding new asteroid? 1 - yes, 0 - no
    // 2. index to add new asteroid at (or replace)
    const val NUMBER_OF_FLOAT_IN_CREATE_REQUEST = NUMBER_OF_FLOATS_PER_VERTEX + 2

    const val NUMBER_OF_ASTEROIDS = 150
    private const val MIN_SIZE = 0.1f
    private const val SIZE_RANGE = 0.25f

    const val TAG = "AsteroidsData"

    class Vertex : AttributeData() {
        override val numberOfFloatsPerVertex = NUMBER_OF_FLOATS_PER_VERTEX
        override val typeSize = Float.SIZE_BYTES
        override val size = NUMBER_OF_ASTEROIDS * numberOfFloatsPerVertex
        val data: FloatArray = FloatArray(size) { 0f }

        override fun getBuffer() = data.toBuffer()
    }

    class CreateAsteroidData {
        val data: FloatArray = FloatArray(NUMBER_OF_FLOAT_IN_CREATE_REQUEST)
        val buffer: Buffer get() {
            return data.toBuffer()
        }
        val bufferSize = data.size * Float.SIZE_BYTES

        private var clean = true

        // Cleaning the request data
        fun unload() {
            if(clean) { return }
            data.fill(0f)
            clean = true
        }

        fun load(requestData: FloatArray) {
            if(requestData.size != data.size) {
                Log.e(TAG, "Request data size must be ${data.size} but was ${requestData.size}")
            }
            for (i in data.indices) {
                data[i] = requestData[i]
            }
            clean = false
        }
    }

    // For getting data from GPU about collision
    class CollisionData {
        val data: FloatArray = FloatArray(6)
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



    private var createAsteroidIndex = 0
    fun getAsteroid(
        spawnPosition: FloatArray,
        textureDimensions: TextureDimensions
    ): FloatArray {
        // new Asteroid data
        val newData = FloatArray(NUMBER_OF_FLOAT_IN_CREATE_REQUEST)
        val vector = floatArrayOf(
           Math.random().toFloat() - 0.5f,
            Math.random().toFloat() - 0.5f
        )
        vector.normalize()
        vector.multiply(3f)
        // position
        newData[0] = spawnPosition.x + vector.x
        newData[1] = spawnPosition.y + vector.y

        // velocity
        val speed = EngineGlobals.deltaTime * (Math.random().toFloat() * 0.5f + 0.5f) * 0.1f
        newData[2] = -vector.x * speed
        newData[3] = -vector.y * speed

        // size
        newData[4] = Math.random().toFloat() * SIZE_RANGE + MIN_SIZE

        // texture coordinates
        val randomX = Random.nextInt(until = textureDimensions.columns) + 1
        val randomY = Random.nextInt(until = textureDimensions.rows) + 1
        newData[5] = textureDimensions.stepX * (randomX - 1)
        newData[6] = textureDimensions.stepY * (randomY - 1)
        newData[7] = textureDimensions.stepX
        newData[8] = textureDimensions.stepY

        // alive flag
        newData[9] = 1f
        // request to add new asteroid
        newData[10] = 1f
        // index to add new asteroid at
        newData[11] = createAsteroidIndex.toFloat()

        // increment index
        createAsteroidIndex = (createAsteroidIndex + 1) % NUMBER_OF_ASTEROIDS

        return newData
    }



}
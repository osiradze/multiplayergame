package ge.siradze.multiplayergame.game.presentation.engine.objects.asteroids

import android.util.Log
import ge.siradze.multiplayergame.game.presentation.engine.EngineGlobals
import ge.siradze.multiplayergame.game.presentation.engine.extensions.add
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
     * 1 - is alive flag, 0 - no, 1 - yes
     **/
    private const val NUMBER_OF_FLOATS_PER_VERTEX = 10
    private const val PX = 0
    private const val PY = 1
    private const val VX = 2
    private const val VY = 3
    private const val SIZE = 4
    private const val TX = 5
    private const val TY = 6
    private const val TW = 7
    private const val TH = 8
    private const val IS_ALIVE = 9

    private const val REQUEST = 10
    private const val CREATE_INDEX = 11



    // 2 extra data for:
    // 1. do we request adding new asteroid? 1 - yes, 0 - no
    // 2. index to add new asteroid at (or replace)
    const val NUMBER_OF_FLOAT_IN_CREATE_REQUEST = NUMBER_OF_FLOATS_PER_VERTEX + 2

    const val NUMBER_OF_ASTEROIDS = 400
    private const val MIN_SIZE = 0.1f
    private const val SIZE_RANGE = 0.25f
    const val SPAWN_DISTANCE = 3f

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
        val isAlive : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_isAlive",
            offset = 9
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
        val positionVector = floatArrayOf(
           Math.random().toFloat() - 0.5f,
            Math.random().toFloat() - 0.5f
        )
        positionVector.normalize()
        positionVector.multiply(SPAWN_DISTANCE)
        // position
        newData[PX] = spawnPosition.x + positionVector.x
        newData[PY] = spawnPosition.y + positionVector.y


        val velocityVector = floatArrayOf(
            Math.random().toFloat() - 0.5f,
            Math.random().toFloat() - 0.5f
        )
        velocityVector.add(positionVector)

        // velocity
        val speed = EngineGlobals.deltaTime * (Math.random().toFloat() * 0.5f + 0.5f) * 0.1f
        newData[VX] = -velocityVector.x * speed
        newData[VY] = -velocityVector.y * speed

        // size
        newData[SIZE] = Math.random().toFloat() * SIZE_RANGE + MIN_SIZE

        // texture coordinates
        val randomX = Random.nextInt(until = textureDimensions.columns) + 1
        val randomY = Random.nextInt(until = textureDimensions.rows) + 1
        newData[TX] = textureDimensions.stepX * (randomX - 1)
        newData[TY] = textureDimensions.stepY * (randomY - 1)
        newData[TW] = textureDimensions.stepX
        newData[TH] = textureDimensions.stepY

        // alive flag
        newData[IS_ALIVE] = 1f
        // request to add new asteroid
        newData[REQUEST] = 1f
        // index to add new asteroid at
        newData[CREATE_INDEX] = createAsteroidIndex.toFloat()

        // increment index
        createAsteroidIndex = (createAsteroidIndex + 1) % NUMBER_OF_ASTEROIDS

        return newData
    }



}
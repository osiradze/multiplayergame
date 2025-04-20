package ge.siradze.multiplayergame.game.presentation.engine.objects.asteroids

import ge.siradze.multiplayergame.game.presentation.engine.extensions.normalize
import ge.siradze.multiplayergame.game.presentation.engine.extensions.toBuffer
import ge.siradze.multiplayergame.game.presentation.engine.extensions.x
import ge.siradze.multiplayergame.game.presentation.engine.extensions.y
import ge.siradze.multiplayergame.game.presentation.engine.objects.AttributeData
import ge.siradze.multiplayergame.game.presentation.engine.texture.TextureDimensions
import kotlin.random.Random

/**
 * Allocating Memory For MAX asteroids from start.
 * Because GPU handle the data and not CPU, this solution is better than sending new instance of data to GPU on every allocation.
 * When we will want to add new asteroid, we will send the data and index to GPU.
**/


class AsteroidsData {
    companion object {
        const val NUMBER_OF_ASTEROIDS = 50
        const val MIN_SIZE = 0.05f
        const val SIZE_RANGE = 0.05f
    }

    class Vertex(
        private val minSize: Float = MIN_SIZE,
        private val sizeRange: Float = SIZE_RANGE,
        private val textureDimensions: TextureDimensions
    ) : AttributeData() {
        /**
         * 2 - position
         * 2 - velocity
         * 1 - size
         * 4 - texture coordinates
         * 1 - collision flag
         * 1 - isDestroyed flag
        **/
        override val numberOfFloatsPerVertex = 11
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



}
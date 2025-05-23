package ge.siradze.asteroids.data

import ge.siradze.asteroids.data.Vertex.Companion.CB
import ge.siradze.asteroids.data.Vertex.Companion.CG
import ge.siradze.asteroids.data.Vertex.Companion.CR
import ge.siradze.asteroids.data.Vertex.Companion.IS_ALIVE
import ge.siradze.asteroids.data.Vertex.Companion.NUMBER_OF_FLOATS_PER_VERTEX
import ge.siradze.asteroids.data.Vertex.Companion.PX
import ge.siradze.asteroids.data.Vertex.Companion.PY
import ge.siradze.asteroids.data.Vertex.Companion.SIZE
import ge.siradze.asteroids.data.Vertex.Companion.TH
import ge.siradze.asteroids.data.Vertex.Companion.TW
import ge.siradze.asteroids.data.Vertex.Companion.TX
import ge.siradze.asteroids.data.Vertex.Companion.TY
import ge.siradze.asteroids.data.Vertex.Companion.VX
import ge.siradze.asteroids.data.Vertex.Companion.VY
import ge.siradze.core.extensions.multiply
import ge.siradze.core.extensions.normalize
import ge.siradze.core.extensions.toBuffer
import ge.siradze.core.extensions.x
import ge.siradze.core.extensions.y
import ge.siradze.core.AttributeData
import ge.siradze.core.texture.TextureDimensions
import kotlin.random.Random

internal class VertexProperties(
    val numberOfAsteroids: Int = NUMBER_OF_ASTEROIDS,
    val minSize: Float = MIN_SIZE,
    val sizeRange: Float = SIZE_RANGE,
    val spawnDistance: Float = SPAWN_DISTANCE
) {
    companion object {
        private const val MIN_SIZE = 0.1f
        private const val SIZE_RANGE = 0.2f
        private const val SPAWN_DISTANCE = 8f
        private const val NUMBER_OF_ASTEROIDS = 100
    }
}

internal class Vertex(
    properties: VertexProperties,
    textureDimensions: TextureDimensions
) : AttributeData() {

    companion object {

        /**
         * 2 - position
         * 2 - velocity
         * 1 - size
         * 4 - texture coordinates
         * 3 - color
         * 1 - is alive flag, 0 - no, 1 - yes
         **/

        const val NUMBER_OF_FLOATS_PER_VERTEX = 13
        const val PX = 0
        const val PY = 1
        const val VX = 2
        const val VY = 3
        const val SIZE = 4
        const val TX = 5
        const val TY = 6
        const val TW = 7
        const val TH = 8
        const val CR = 9
        const val CB = 10
        const val CG = 11
        const val IS_ALIVE = 12
    }

    override val numberOfFloatsPerVertex = NUMBER_OF_FLOATS_PER_VERTEX
    override val typeSize = Float.SIZE_BYTES
    override val size = properties.numberOfAsteroids * numberOfFloatsPerVertex
    val data: FloatArray = FloatArray(size)
    val numberOfAsteroids = properties.numberOfAsteroids

    init {
        generatePoints(
            vertex = this,
            properties = properties,
            textureDimensions = textureDimensions
        )
    }

    override fun getBuffer() = data.toBuffer()

}

private fun generatePoints(
    vertex: Vertex,
    properties: VertexProperties,
    textureDimensions: TextureDimensions
) = with(properties) {
    for (i in 0 until properties.numberOfAsteroids) {
        // position
        vertex.data[i * NUMBER_OF_FLOATS_PER_VERTEX + PX] = Random.nextFloat() * spawnDistance - spawnDistance / 2
        vertex.data[i * NUMBER_OF_FLOATS_PER_VERTEX + PY] = Random.nextFloat() * spawnDistance - spawnDistance / 2

        val velocityVector = floatArrayOf(
            Random.nextFloat(),
            Random.nextFloat(),
        )
        velocityVector.normalize()
        val speed = (Random.nextFloat() * 0.5f + 0.5f) * 0.05f
        velocityVector.multiply(speed)

        vertex.data[i * NUMBER_OF_FLOATS_PER_VERTEX + VX] = velocityVector.x
        vertex.data[i * NUMBER_OF_FLOATS_PER_VERTEX + VY] = velocityVector.y
        // size
        vertex.data[i * NUMBER_OF_FLOATS_PER_VERTEX + SIZE] = Random.nextFloat() * sizeRange + minSize

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

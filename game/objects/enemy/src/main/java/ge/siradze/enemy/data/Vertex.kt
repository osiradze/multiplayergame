package ge.siradze.enemy.data

import ge.siradze.core.AttributeData
import ge.siradze.enemy.data.Vertex.Companion.NUMBER_OF_FLOATS_PER_VERTEX
import ge.siradze.enemy.data.Vertex.Companion.TH
import ge.siradze.enemy.data.Vertex.Companion.TW
import ge.siradze.enemy.data.Vertex.Companion.TX
import ge.siradze.enemy.data.Vertex.Companion.TY
import ge.siradze.glcore.extensions.toBuffer
import ge.siradze.glcore.texture.TextureDimensions
import kotlin.random.Random

class VertexProperties (
    val spawnPosition: FloatArray,
    val numberOfEnemies: Int = NUMBER_OF_ENEMIES,
    val minSize: Float = MIN_SIZE,
    val sizeRange: Float = SIZE_RANGE
) {
    companion object {
        private const val MIN_SIZE = 0.1f
        private const val SIZE_RANGE = 0.02f
        private const val NUMBER_OF_ENEMIES = 200
    }
}

class Vertex(
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
        const val IS_ALIVE = 12 // 0 - has not born, 1 - is alive, 2 - has died
    }



    override val numberOfFloatsPerVertex: Int = NUMBER_OF_FLOATS_PER_VERTEX
    override val typeSize: Int = Float.SIZE_BYTES
    override val size: Int = properties.numberOfEnemies * numberOfFloatsPerVertex
    val data: FloatArray = FloatArray(size)

    override fun getBuffer() = data.toBuffer()

    init {
        generatePoints(this, properties, textureDimensions)
    }

}


private fun generatePoints(
    vertex: Vertex,
    properties: VertexProperties,
    textureDimensions: TextureDimensions
) {
    // Generate enemy points based on the properties and texture dimensions
    for (i in 0 until properties.numberOfEnemies) {
        val index = i * vertex.numberOfFloatsPerVertex
        vertex.data[index + Vertex.PX] = properties.spawnPosition[0]
        vertex.data[index + Vertex.PY] = properties.spawnPosition[1]
        vertex.data[index + Vertex.VX] = 0f // Initial velocity X
        vertex.data[index + Vertex.VY] = 0f // Initial velocity Y
        vertex.data[index + Vertex.SIZE] = properties.minSize + Math.random().toFloat() * properties.sizeRange


        // texture coordinates
        val randomX = Random.nextInt(until = textureDimensions.columns) + 1
        val randomY = Random.nextInt(until = textureDimensions.rows) + 1
        vertex.data[i * NUMBER_OF_FLOATS_PER_VERTEX + TX] = textureDimensions.stepX * (randomX - 1)
        vertex.data[i * NUMBER_OF_FLOATS_PER_VERTEX + TY] = textureDimensions.stepY * (randomY - 1)
        vertex.data[i * NUMBER_OF_FLOATS_PER_VERTEX + TW] = textureDimensions.stepX
        vertex.data[i * NUMBER_OF_FLOATS_PER_VERTEX + TH] = textureDimensions.stepY


        vertex.data[index + Vertex.CR] = 1f // Color Red
        vertex.data[index + Vertex.CB] = 1f // Color Blue
        vertex.data[index + Vertex.CG] = 1f // Color Green
        vertex.data[index + Vertex.IS_ALIVE] = 0f // Alive flag
    }
}
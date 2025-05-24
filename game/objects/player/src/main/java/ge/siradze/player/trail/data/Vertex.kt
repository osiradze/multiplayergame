package ge.siradze.player.trail.data

import ge.siradze.glcore.extensions.fillWith
import ge.siradze.glcore.extensions.toBuffer
import ge.siradze.glcore.extensions.x
import ge.siradze.glcore.extensions.y
import java.nio.Buffer

internal class Vertex(
    private val initPosition: FloatArray
) {
    // 3 floats per vertex, 2 for position, 1 for alpha
    val numberOfFloatsPerVertex = 3
    val data: FloatArray = FloatArray(size = 60 * numberOfFloatsPerVertex).also {
        it.fillWith(
            floatArrayOf(initPosition.x, initPosition.y, 0f)
        )
        for (i in it.indices) {
            if(i % numberOfFloatsPerVertex == 0) {
                it[i+2] = i.toFloat() / it.size.toFloat()
            }
        }
    }
    val pointNumber = data.size / numberOfFloatsPerVertex
    val stride = numberOfFloatsPerVertex * Float.SIZE_BYTES
    val bufferSize = data.size * Float.SIZE_BYTES

    fun getBuffer(): Buffer = data.toBuffer()
}
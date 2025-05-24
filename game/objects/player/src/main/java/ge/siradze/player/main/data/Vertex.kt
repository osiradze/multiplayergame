package ge.siradze.player.main.data

import ge.siradze.glcore.extensions.scale
import ge.siradze.glcore.extensions.toBuffer
import java.nio.Buffer

internal class Vertex {
    private val scale = 0.08f
    private val numberOfFloatsPerVertex = 4
    private val data: FloatArray = floatArrayOf(
        // positions (x,y)  // texture coords (s,t)
        // First triangle
        -0.5f, -0.5f,       0.0f, 0.0f,     // bottom left
        0.5f, -0.5f,       1.0f, 0.0f,     // bottom right
        0.5f,  0.5f,       1.0f, 1.0f,     // top right

        // Second triangle
        -0.5f, -0.5f,       0.0f, 0.0f,     // bottom left
        0.5f,  0.5f,       1.0f, 1.0f,     // top right
        -0.5f,  0.5f,       0.0f, 1.0f      // top left
    ).apply{
        scale(scale, numberOfFloatsPerVertex)
    }

    val middlePoint = floatArrayOf(0f, 0f)

    val pointNumber = data.size / numberOfFloatsPerVertex
    val stride = numberOfFloatsPerVertex * Float.SIZE_BYTES
    val bufferSize = data.size * Float.SIZE_BYTES

    fun getBuffer(): Buffer = data.toBuffer()

}
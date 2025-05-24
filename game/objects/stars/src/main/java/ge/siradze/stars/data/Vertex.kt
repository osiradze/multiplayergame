package ge.siradze.stars.data

import ge.siradze.glcore.extensions.toBuffer
import java.nio.Buffer
import kotlin.random.Random

internal class Vertex(
    val numberOfPoints: Int = 1000
) {
    // 2 for position, 1 for camera speed and 1 for brightness
    private val numberOfFloatsPerVertex = 4

    private val data: FloatArray = FloatArray(numberOfPoints * numberOfFloatsPerVertex)

    val stride = numberOfFloatsPerVertex * Float.SIZE_BYTES
    val bufferSize = data.size * Float.SIZE_BYTES
    fun getBuffer(): Buffer = data.toBuffer()

    private val cameraSpeeds = floatArrayOf(
        0.2f, 0.2f, 0.2f, 0.4f, 0.4f, 0.6f,
    )

    init {
        generatePoints()
    }

    private fun generatePoints() {
        for (i in 0 until numberOfPoints) {
            // position
            data[px(i)] = (Random.nextFloat() - 0.5f) * 8
            data[py(i)] = (Random.nextFloat() - 0.5f) * 8
            // camera speed
            data[camera(i)] = cameraSpeeds[Random.nextInt(cameraSpeeds.size)]
            // brightness
            data[brightness(i)] = Random.nextFloat() * 0.9f
        }
    }

    private fun px(i: Int) = i * numberOfFloatsPerVertex
    private fun py(i: Int) = i * numberOfFloatsPerVertex + 1
    private fun camera(i: Int) = i * numberOfFloatsPerVertex + 2
    private fun brightness(i: Int) = i * numberOfFloatsPerVertex + 3
}

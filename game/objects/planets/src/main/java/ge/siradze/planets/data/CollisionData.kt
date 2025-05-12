package ge.siradze.planets.data

import ge.siradze.core.extensions.toBuffer
import java.nio.Buffer

// For getting data from GPU about collision
internal class CollisionData {
    val data: FloatArray = FloatArray(9)
    val buffer: Buffer = data.toBuffer()
    val bufferSize = data.size * Float.SIZE_BYTES
}

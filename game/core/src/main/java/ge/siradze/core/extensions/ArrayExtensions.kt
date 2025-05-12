package ge.siradze.core.extensions

import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

fun FloatArray.toBuffer(): Buffer {
    return ByteBuffer.allocateDirect(size * Float.SIZE_BYTES)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .put(this).rewind()
}
package ge.siradze.core

import java.nio.Buffer


abstract class AttributeData {
    abstract val numberOfFloatsPerVertex: Int
    abstract val typeSize: Int
    abstract val size: Int
    val stride: Int by lazy { numberOfFloatsPerVertex * typeSize }
    val bufferSize: Int by lazy { size * typeSize }

    abstract fun getBuffer(): Buffer

}
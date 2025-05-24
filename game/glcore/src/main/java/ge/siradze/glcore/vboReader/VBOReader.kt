package ge.siradze.glcore.vboReader

import android.opengl.GLES20.GL_DYNAMIC_DRAW
import android.opengl.GLES20.glGenBuffers
import android.opengl.GLES20.glBindBuffer
import android.opengl.GLES20.glBufferData
import android.opengl.GLES31.GL_SHADER_STORAGE_BUFFER
import android.util.Log
import ge.siradze.glcore.extensions.toBuffer
import ge.siradze.glcore.utils.OpenGLUtils


/**
 * Reading data from GPU to CPU in OpenGL is extremely slow.
 * Even reading a single byte can stall the pipeline more than the heaviest frame render.
 *
 * This class reduces performance impact by consolidating all GPU reads into a single buffer read per frame.
 * Each object accesses its own slice of the shared buffer.
 */

interface VBOReader {
    fun allocate(
        key: String,
        numberOfFloats: Int,
    )

    fun getOffset(key: String): Int

    fun getData(key: String, destArray: FloatArray)

    val vbo: IntArray

}


class VBOReaderImpl : VBOReader {
    private var data: FloatArray? = null
    private lateinit var cleanData: FloatArray

    private val allocations: MutableMap<String, KeyData> = mutableMapOf()
    private var offset = 0

    override val vbo: IntArray = IntArray(1)
    private val buffer by lazy { cleanData.toBuffer() }
    private val bufferSize by lazy { cleanData.size * Float.SIZE_BYTES }

    class KeyData (
        val offset: Int,
        val numberOfFloats: Int,
    )

    override fun allocate(
        key: String,
        numberOfFloats: Int,
    ) {
        allocations[key] = KeyData(offset, numberOfFloats)
        offset += numberOfFloats
    }

    fun init() {
        cleanData =FloatArray(allocations.values.sumOf { it.numberOfFloats })
        writeCleanVBO()
    }

    fun clean() {
        writeCleanVBO()
    }

    private fun writeCleanVBO() {
        glGenBuffers(vbo.size, vbo, 0)
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, vbo[0])
        glBufferData(
            GL_SHADER_STORAGE_BUFFER,
            bufferSize,
            buffer,
            GL_DYNAMIC_DRAW
        )
    }

    fun read() {
        data = OpenGLUtils.readSSBO(
            vbo[0],
            cleanData.size,
            Float.SIZE_BYTES
        ) ?: return
    }

    override fun getOffset(key: String): Int {
        val keyData = allocations[key] ?: return -1
        return keyData.offset
    }


    // writes data inside provided float array
    override fun getData(key: String, destArray: FloatArray) {
        val keyData = allocations[key] ?: return
        if (keyData.numberOfFloats != destArray.size) {
            Log.e("TAG", "array: is not same size as allocated")
            return
        }
        data?.let {
            System.arraycopy(it, keyData.offset, destArray, 0, keyData.numberOfFloats)
        }
    }

}

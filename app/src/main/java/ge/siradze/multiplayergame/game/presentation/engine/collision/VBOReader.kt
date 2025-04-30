package ge.siradze.multiplayergame.game.presentation.engine.collision

import android.opengl.GLES20.GL_DYNAMIC_DRAW
import android.opengl.GLES20.glGenBuffers
import android.opengl.GLES20.glBindBuffer
import android.opengl.GLES20.glBufferData
import android.opengl.GLES31.GL_SHADER_STORAGE_BUFFER
import android.util.Log
import ge.siradze.multiplayergame.game.presentation.engine.extensions.toBuffer
import ge.siradze.multiplayergame.game.presentation.engine.utils.OpenGLUtils


/**
 Reading array from GPU to CPU is slowest operation in OpenGL.
 It is slower than Heaviest frame render there can be.
 It does not matter if you are reading even one byte from GPU to CPU it is still as slow.
 And when we are using reading for multiple objects we have heavy hit on performance.

 Idea of this class is to use one read for all objects per frame (or less) .
 **/

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

package ge.siradze.multiplayergame.game.presentation.engine.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import android.opengl.GLES20
import android.opengl.GLES20.GL_ACTIVE_UNIFORMS
import android.opengl.GLES20.GL_ACTIVE_UNIFORM_MAX_LENGTH
import android.opengl.GLES20.GL_TEXTURE0
import android.opengl.GLES20.glGetActiveUniform
import android.opengl.GLES30.GL_COMPILE_STATUS
import android.opengl.GLES30.GL_LINK_STATUS
import android.opengl.GLES30.GL_NEAREST
import android.opengl.GLES30.GL_TEXTURE_2D
import android.opengl.GLES30.GL_TEXTURE_MAG_FILTER
import android.opengl.GLES30.GL_TEXTURE_MIN_FILTER
import android.opengl.GLES30.GL_TRUE
import android.opengl.GLES30.glActiveTexture
import android.opengl.GLES30.glAttachShader
import android.opengl.GLES30.glBindTexture
import android.opengl.GLES30.glCompileShader
import android.opengl.GLES30.glCreateProgram
import android.opengl.GLES30.glCreateShader
import android.opengl.GLES30.glGetProgramInfoLog
import android.opengl.GLES30.glGetProgramiv
import android.opengl.GLES30.glGetShaderInfoLog
import android.opengl.GLES30.glGetShaderiv
import android.opengl.GLES30.glLinkProgram
import android.opengl.GLES30.glShaderSource
import android.opengl.GLES30.glTexParameteri
import android.opengl.GLES30.glUniform1i
import android.opengl.GLUtils
import android.util.Log

object OpenGLUtils {

    fun createShader(type: Int, shaderSource: String, shaderName: String = ""): Int? {
        val shader = glCreateShader(type)
        glShaderSource(shader, shaderSource)
        glCompileShader(shader)
        val compileStatus = IntArray(2)
        glGetShaderiv(shader, GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] != GL_TRUE) {
            val info = glGetShaderInfoLog(shader)
            Log.e("$shaderName createShader", "error: $type $info")
            return null
        }
        return shader
    }


    fun createAndLinkProgram(vararg shaders: Int): Int? {
        val program = glCreateProgram()
        shaders.forEach {
            glAttachShader(program, it)
        }
        glLinkProgram(program)

        val linkStatus = IntArray(1)
        glGetProgramiv(program, GL_LINK_STATUS, linkStatus, 0)
        if (linkStatus[0] != GL_TRUE) {
            val info = glGetProgramInfoLog(program)
            Log.e("createAndLinkProgram", "createProgram error: $info")
            return null
        }
        return program
    }

    /**
     * Loads textures to the GPU
     * @param offset Offset for GL_TEXTURE0 to GL_TEXTURE32 all object should share 32 capacity
     */
    fun loadTexture(
        bitmaps: Bitmap,
        textureId: Int,
        locations: Int,
        offset: Int,
    ) {
        val firstGlTexture = GL_TEXTURE0 + offset
        glActiveTexture(firstGlTexture)
        glBindTexture(GL_TEXTURE_2D, textureId)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, flipBitmapVertically(bitmaps), 0)
        bitmaps.recycle()
        glUniform1i(locations, offset)
    }


    /**
     * Loads textures to the GPU
     * @param offset Offset for GL_TEXTURE0 to GL_TEXTURE32 all object should share 32 capacity
     */
    fun loadTextures(
        bitmaps: Array<Bitmap>,
        textureIds: IntArray,
        locations: Array<Int>,
        offset: Int,
    ) {
        val firstGlTexture = GL_TEXTURE0 + offset
        for (i in bitmaps.indices) {
            glActiveTexture(firstGlTexture + i)
            glBindTexture(GL_TEXTURE_2D, textureIds[i])

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, flipBitmapVertically(bitmaps[i]), 0)
            bitmaps[i].recycle()
            glUniform1i(locations[i], i + offset)
        }

    }

    private fun flipBitmapVertically(bitmap: Bitmap): Bitmap {
        val matrix = Matrix().apply {
            postScale(1f, -1f, bitmap.width / 2f, bitmap.height / 2f)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun printShaderProgramUniforms(program: Int) {
        val uniformCount = IntArray(1)
        glGetProgramiv(program, GL_ACTIVE_UNIFORMS, uniformCount, 0)

        val maxLength = IntArray(1)
        glGetProgramiv(program, GL_ACTIVE_UNIFORM_MAX_LENGTH, maxLength, 0)

        for (i in 0 until uniformCount[0]) {
            val length = IntArray(1)
            val size = IntArray(1)
            val type = IntArray(1)
            val name = ByteArray(maxLength[0])

            glGetActiveUniform(program, i, maxLength[0], length, 0, size, 0, type, 0, name, 0)
            val uniformName = String(name, 0, length[0])

            println("Uniform #$i: Name = $uniformName, Size = ${size[0]}, Type = ${type[0]}")
        }
    }

}
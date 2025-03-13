package ge.siradze.multiplayergame.game.presentation.engine.utils

import android.opengl.GLES20.GL_ACTIVE_UNIFORMS
import android.opengl.GLES20.GL_ACTIVE_UNIFORM_MAX_LENGTH
import android.opengl.GLES20.glBindBuffer
import android.opengl.GLES20.glGetActiveUniform
import android.opengl.GLES30.GL_COMPILE_STATUS
import android.opengl.GLES30.GL_LINK_STATUS
import android.opengl.GLES30.GL_MAP_READ_BIT
import android.opengl.GLES30.GL_TRUE
import android.opengl.GLES30.glAttachShader
import android.opengl.GLES30.glCompileShader
import android.opengl.GLES30.glCreateProgram
import android.opengl.GLES30.glCreateShader
import android.opengl.GLES30.glGetProgramInfoLog
import android.opengl.GLES30.glGetProgramiv
import android.opengl.GLES30.glGetShaderInfoLog
import android.opengl.GLES30.glGetShaderiv
import android.opengl.GLES30.glLinkProgram
import android.opengl.GLES30.glMapBufferRange
import android.opengl.GLES30.glShaderSource
import android.opengl.GLES30.glUnmapBuffer
import android.opengl.GLES31.GL_SHADER_STORAGE_BUFFER
import android.util.Log
import java.nio.ByteOrder

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

    fun readSSBO(bufferId: Int, size: Int, typeSize: Int): FloatArray {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, bufferId)

        // Map the buffer to read it on the CPU
        val mappedBuffer = glMapBufferRange(
            GL_SHADER_STORAGE_BUFFER, 0, size * typeSize, GL_MAP_READ_BIT
        ) as? java.nio.ByteBuffer ?: return floatArrayOf()

        val result = FloatArray(size)
        mappedBuffer.order(ByteOrder.LITTLE_ENDIAN)
        mappedBuffer.asFloatBuffer().get(result) // Copy data into result array


        // Unmap the buffer after reading
        glUnmapBuffer(GL_SHADER_STORAGE_BUFFER)

        return result
    }

}
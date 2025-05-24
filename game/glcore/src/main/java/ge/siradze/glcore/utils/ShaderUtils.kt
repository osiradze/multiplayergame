package ge.siradze.glcore.utils

import android.content.Context
import android.opengl.GLES31.GL_SHADER_STORAGE_BARRIER_BIT
import android.opengl.GLES31.GL_SHADER_STORAGE_BUFFER
import android.opengl.GLES31.glBindBuffer
import android.opengl.GLES31.glBindBufferBase
import android.opengl.GLES31.glDispatchCompute
import android.opengl.GLES31.glMemoryBarrier
import android.opengl.GLES31.glUseProgram
import java.io.BufferedReader
import java.io.InputStreamReader

object ShaderUtils {

    fun readShaderFile(context: Context, resourceId: Int): String {
        val shaderCode = StringBuilder()
        try {
            context.resources.openRawResource(resourceId).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).useLines { lines ->
                    lines.forEach {
                        shaderCode.append(it).append("\n")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return shaderCode.toString()
    }

    inline fun computeShader(
        shaderProgram: Int,
        uniforms: () -> Unit = {},
        vbos: IntArray,
        x: Int = 1,
        y: Int = 1,
        z: Int = 1,
    ) {
        glUseProgram(shaderProgram)
        vbos.forEachIndexed { index, vbo ->
            glBindBuffer(GL_SHADER_STORAGE_BUFFER, vbo)
            glBindBufferBase(GL_SHADER_STORAGE_BUFFER, index, vbo)
        }
        uniforms()

        // Dispatch the compute shader
        glDispatchCompute(x, y, z)
        // Make sure all the computations are done
        glMemoryBarrier(GL_SHADER_STORAGE_BARRIER_BIT)
        glUseProgram(0)
    }
}
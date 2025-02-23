package ge.siradze.mutiplayergame.game.presentation.engine.utils

import android.opengl.GLES30.GL_COMPILE_STATUS
import android.opengl.GLES30.GL_LINK_STATUS
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
import android.opengl.GLES30.glShaderSource
import android.util.Log

object OpenGLUtils {

    fun createShader(type: Int, shaderSource: String): Int? {
        val shader = glCreateShader(type)
        glShaderSource(shader, shaderSource)
        glCompileShader(shader)
        val compileStatus = IntArray(2)
        glGetShaderiv(shader, GL_COMPILE_STATUS, compileStatus, 0)
        if (compileStatus[0] != GL_TRUE) {
            val info = glGetShaderInfoLog(shader)
            Log.e("createShader", "error: $type $info")
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

}
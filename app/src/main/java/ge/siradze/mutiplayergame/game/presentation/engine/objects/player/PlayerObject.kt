package ge.siradze.mutiplayergame.game.presentation.engine.objects.player

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.GL_FLOAT
import android.opengl.GLES20.GL_FRAGMENT_SHADER
import android.opengl.GLES20.GL_LINES
import android.opengl.GLES20.GL_POINTS
import android.opengl.GLES20.GL_VERTEX_SHADER
import android.opengl.GLES20.glGetAttribLocation
import android.opengl.GLES20.glUniform1f
import android.opengl.GLES20.glVertexAttribPointer
import android.opengl.GLES30.GL_STATIC_DRAW
import android.opengl.GLES30.glDeleteBuffers
import android.opengl.GLES30.glDeleteProgram
import android.opengl.GLES30.glDeleteShader
import android.opengl.GLES30.glDeleteVertexArrays
import android.opengl.GLES30.glDisableVertexAttribArray
import android.opengl.GLES30.glEnableVertexAttribArray
import android.opengl.GLES30.glUseProgram
import android.opengl.GLES31.GL_ARRAY_BUFFER
import android.opengl.GLES31.glBindBuffer
import android.opengl.GLES31.glBindVertexArray
import android.opengl.GLES31.glBufferData
import android.opengl.GLES31.glGenBuffers
import android.opengl.GLES31.glGenVertexArrays
import ge.siradze.mutiplayergame.R
import ge.siradze.mutiplayergame.game.presentation.engine.objects.GameObject
import ge.siradze.mutiplayergame.game.presentation.engine.utils.OpenGLUtils
import ge.siradze.mutiplayergame.game.presentation.engine.utils.ShaderUtils
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class PlayerData {
    class Vertex {
        private val data: FloatArray = floatArrayOf(
            0.0f, 0.5f,
            -0.5f, -0.5f,

            0.5f, -0.5f,
            0.0f, 0.5f,

            0.5f, -0.5f,
            -0.5f, -0.5f,
        )
        val pointNumber = data.size
        val stride = 2 * Float.SIZE_BYTES

        val bufferSize = data.size * Float.SIZE_BYTES

        fun getBuffer(): Buffer = ByteBuffer.allocateDirect(bufferSize)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(data).rewind()





    }

    class Locations(
        var vertex: Int = 0,
    )

}



class PlayerObject(
    private val context: Context
): GameObject {

    private val vao: IntArray = IntArray(1)
    private val vbo: IntArray = IntArray(1)

    private val vertex = PlayerData.Vertex()
    private val locations = PlayerData.Locations()

    private var shaders = IntArray(2)
    private var program = 0



    override fun init() {
        initPrograms()


        glGenVertexArrays(1, vao, 0)
        glGenBuffers(1, vbo, 0)
        glBindVertexArray(vao[0])
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0])
        glBufferData(
            GL_ARRAY_BUFFER,
            vertex.bufferSize,
            vertex.getBuffer(),
            GL_STATIC_DRAW
        )

        locations.vertex = glGetAttribLocation(program, "a_position")
        glEnableVertexAttribArray(locations.vertex)
        glVertexAttribPointer(locations.vertex, 2, GL_FLOAT, false, vertex.stride, 0)
    }


    private fun initPrograms(){
        loadProgram()
    }

    private fun loadProgram() {
        shaders[0] = OpenGLUtils.createShader(
            GL_VERTEX_SHADER,
            ShaderUtils.readShaderFile(context, R.raw.player_vertex)
        ) ?: return
        shaders[1] = OpenGLUtils.createShader(
            GL_FRAGMENT_SHADER,
            ShaderUtils.readShaderFile(context, R.raw.player_fragment)
        ) ?: return
        program = OpenGLUtils.createAndLinkProgram(shaders[0], shaders[1]) ?: return
        glDeleteShader(shaders[0])
        glDeleteShader(shaders[1])
    }


    override fun draw() {
        glUseProgram(program)
        glBindVertexArray(vao[0])
        glEnableVertexAttribArray(locations.vertex)

        GLES20.glDrawArrays(
            GL_LINES,
            0,
            vertex.pointNumber,
        )

        glDisableVertexAttribArray(locations.vertex)
        glBindVertexArray(0)
        glUseProgram(0)
    }

    override fun release() {
        glDeleteVertexArrays(vao.size, vao, 0)
        glDeleteBuffers(vbo.size, vbo, 0)
        glDeleteProgram(program)
    }

}
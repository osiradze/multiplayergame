package ge.siradze.multiplayergame.game.presentation.engine.objects.player

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.GL_DYNAMIC_DRAW
import android.opengl.GLES20.GL_FLOAT
import android.opengl.GLES20.GL_FRAGMENT_SHADER
import android.opengl.GLES20.GL_POINTS
import android.opengl.GLES20.GL_VERTEX_SHADER
import android.opengl.GLES20.glDeleteBuffers
import android.opengl.GLES20.glDeleteProgram
import android.opengl.GLES20.glGetAttribLocation
import android.opengl.GLES20.glGetUniformLocation
import android.opengl.GLES20.glUniform1i
import android.opengl.GLES20.glUniform2f
import android.opengl.GLES20.glVertexAttribPointer
import android.opengl.GLES30.glDeleteShader
import android.opengl.GLES30.glDeleteVertexArrays
import android.opengl.GLES30.glDisableVertexAttribArray
import android.opengl.GLES30.glEnableVertexAttribArray
import android.opengl.GLES30.glUniform1f
import android.opengl.GLES30.glUseProgram
import android.opengl.GLES31.GL_ARRAY_BUFFER
import android.opengl.GLES31.GL_COMPUTE_SHADER
import android.opengl.GLES31.GL_SHADER_STORAGE_BUFFER
import android.opengl.GLES31.glBindBuffer
import android.opengl.GLES31.glBindVertexArray
import android.opengl.GLES31.glBufferData
import android.opengl.GLES31.glGenBuffers
import android.opengl.GLES31.glGenVertexArrays
import ge.siradze.multiplayergame.R
import ge.siradze.multiplayergame.game.presentation.engine.extensions.x
import ge.siradze.multiplayergame.game.presentation.engine.extensions.y
import ge.siradze.multiplayergame.game.presentation.engine.objects.GameObject
import ge.siradze.multiplayergame.game.presentation.engine.utils.OpenGLUtils
import ge.siradze.multiplayergame.game.presentation.engine.utils.ShaderUtils
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder


class PlayerTrailData {

    class Vertex {
        val numberOfFloatsPerVertex = 2
        private val data: FloatArray = FloatArray(size = 30 * numberOfFloatsPerVertex) { 0f }
        val pointNumber = data.size / numberOfFloatsPerVertex
        val stride = numberOfFloatsPerVertex * Float.SIZE_BYTES
        val bufferSize = data.size * Float.SIZE_BYTES

        fun getBuffer(): Buffer = ByteBuffer.allocateDirect(bufferSize)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(data).rewind()
    }

    class ShaderLocations(
        var vertex: Int = 0,
        var ratio: Int = 0,


        var index: Int = 0,
        var position: Int = 0,
    )

    class Properties(
        var index: Int
    )

}



class PlayerTrail(
    private val context: Context,
    private val playerProperties: PlayerData.Properties
): GameObject {


    private val vao: IntArray = IntArray(1)
    private val vbo: IntArray = IntArray(1)

    private val vertex = PlayerTrailData.Vertex()
    private val shaderLocations = PlayerTrailData.ShaderLocations()
    private val properties = PlayerTrailData.Properties(0)

    private val shaders = IntArray(3)
    private var program = 0
    private var computeProgram = 0

    override fun init() {
        initProgram()

        glGenVertexArrays(1, vao, 0)
        glBindVertexArray(vao[0])

        glGenBuffers(1, vbo, 0)
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0])
        glBufferData(
            GL_ARRAY_BUFFER,
            vertex.bufferSize,
            vertex.getBuffer(),
            GL_DYNAMIC_DRAW
        )

        shaderLocations.vertex = glGetAttribLocation(program, "a_position")
        glEnableVertexAttribArray(shaderLocations.vertex)
        glVertexAttribPointer(shaderLocations.vertex, 2, GL_FLOAT, false, vertex.stride, 0)
        glDisableVertexAttribArray(shaderLocations.vertex)
        shaderLocations.ratio = glGetUniformLocation(program, "u_ratio")

        shaderLocations.index = glGetUniformLocation(computeProgram, "u_index")
        shaderLocations.position = glGetUniformLocation(computeProgram, "u_position")

        glBindBuffer(GL_ARRAY_BUFFER, 0)

    }

    override fun setRatio(ratio: Float) {
        glUseProgram(program)
        glUniform1f(shaderLocations.ratio, ratio)
    }

    private fun initProgram() {
        shaders[0] = OpenGLUtils.createShader(
            GL_VERTEX_SHADER,
            ShaderUtils.readShaderFile(context, R.raw.player_trail_vertex),
            shaderName = "Trail Vertex"
        ) ?: return
        shaders[1] = OpenGLUtils.createShader(
            GL_FRAGMENT_SHADER,
            ShaderUtils.readShaderFile(context, R.raw.player_trail_fragment),
            shaderName = "Trail Fragment"
        ) ?: return
        shaders[2] = OpenGLUtils.createShader(
            GL_COMPUTE_SHADER,
            ShaderUtils.readShaderFile(context, R.raw.player_trail_compute),
            shaderName = "Trail Compute"
        ) ?: return
        program = OpenGLUtils.createAndLinkProgram(shaders[0], shaders[1]) ?: return
        computeProgram = OpenGLUtils.createAndLinkProgram(shaders[2]) ?: return
        glDeleteShader(shaders[0])
        glDeleteShader(shaders[1])
        glDeleteShader(shaders[2])
    }


    override fun draw() {
        glBindVertexArray(vao[0])
       /* ShaderUtils.computeShader(
            shaderProgram = computeProgram,
            vbo = vbo[0],
            uniforms = {
                glUniform2f(shaderLocations.position, playerProperties.position.x, playerProperties.position.y)
                glUniform1i(shaderLocations.index, properties.index)
            },
            x = vertex.pointNumber,
            y = 1,
            z = 1,
        )*/
        glUseProgram(program)
        glEnableVertexAttribArray(shaderLocations.vertex)
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, vbo[0])

        properties.index = (properties.index + vertex.numberOfFloatsPerVertex) % (vertex.pointNumber * vertex.numberOfFloatsPerVertex)

        GLES20.glDrawArrays(GL_POINTS, 0, vertex.pointNumber)

        glDisableVertexAttribArray(shaderLocations.vertex)
        glBindVertexArray(0)
    }

    override fun release() {
        glDeleteVertexArrays(vao.size, vao, 0)
        glDeleteBuffers(vbo.size, vbo, 0)
        glDeleteProgram(program)
        glDeleteProgram(computeProgram)
    }



}
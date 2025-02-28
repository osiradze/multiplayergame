package ge.siradze.multiplayergame.game.presentation.engine.objects.player

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.GL_DYNAMIC_DRAW
import android.opengl.GLES20.GL_FLOAT
import android.opengl.GLES20.GL_FRAGMENT_SHADER
import android.opengl.GLES20.GL_LINE_STRIP
import android.opengl.GLES20.GL_VERTEX_SHADER
import android.opengl.GLES20.glDeleteBuffers
import android.opengl.GLES20.glDeleteProgram
import android.opengl.GLES20.glGetAttribLocation
import android.opengl.GLES20.glGetUniformLocation
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
import ge.siradze.multiplayergame.game.presentation.engine.camera.Camera
import ge.siradze.multiplayergame.game.presentation.engine.extensions.x
import ge.siradze.multiplayergame.game.presentation.engine.extensions.y
import ge.siradze.multiplayergame.game.presentation.engine.objects.GameObject
import ge.siradze.multiplayergame.game.presentation.engine.shader.CameraShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.RatioShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.Shader
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderAttribLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderUniformLocation
import ge.siradze.multiplayergame.game.presentation.engine.utils.OpenGLUtils
import ge.siradze.multiplayergame.game.presentation.engine.utils.ShaderUtils
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder


class PlayerTrailData {

    class Vertex {
        private val numberOfFloatsPerVertex = 2
        private val data: FloatArray = FloatArray(size = 100 * numberOfFloatsPerVertex) { 0f }
        val pointNumber = data.size / numberOfFloatsPerVertex
        val stride = numberOfFloatsPerVertex * Float.SIZE_BYTES
        val bufferSize = data.size * Float.SIZE_BYTES

        fun getBuffer(): Buffer = ByteBuffer.allocateDirect(bufferSize)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(data).rewind()
    }

    class ShaderLocations(
        val vertex : ShaderLocation = ShaderAttribLocation(
            name = "a_position"
        ),
        val ratio: ShaderLocation = RatioShaderLocation(),
        var camera: ShaderLocation = CameraShaderLocation(),
        var position: ShaderLocation = ShaderUniformLocation(
            name = "u_position"
        )
    )

    class Properties {

        // For addition logic
        fun shouldUpdate(): Boolean {
            return true
        }
    }

}

class PlayerTrail(
    private val context: Context,
    private val playerProperties: PlayerData.Properties
): GameObject {


    private val vao: IntArray = IntArray(1)
    private val vbo: IntArray = IntArray(1)

    private val vertex = PlayerTrailData.Vertex()
    private val shaderLocations = PlayerTrailData.ShaderLocations()
    private val properties = PlayerTrailData.Properties()

    private val shaders = arrayOf(
        Shader(GL_VERTEX_SHADER, R.raw.player_trail_vertex, "Trail Vertex"),
        Shader(GL_FRAGMENT_SHADER, R.raw.player_trail_fragment, "Trail Fragment"),
        Shader(GL_COMPUTE_SHADER, R.raw.player_trail_compute, "Trail Compute"),
    )
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

        shaderLocations.vertex.init(program)
        glEnableVertexAttribArray(shaderLocations.vertex.location)
        glVertexAttribPointer(shaderLocations.vertex.location, 2, GL_FLOAT, false, vertex.stride, 0)
        glDisableVertexAttribArray(shaderLocations.vertex.location)
        shaderLocations.ratio.init(program)
        shaderLocations.camera.init(program)
        shaderLocations.position.init(computeProgram)

        glBindBuffer(GL_ARRAY_BUFFER, 0)

    }

    override fun setRatio(ratio: Float) {
        glUseProgram(program)
        glUniform1f(shaderLocations.ratio.location, ratio)
    }

    private fun initProgram() {
        val vertexShader = shaders[0].create(context) ?: return
        val fragmentShader = shaders[1].create(context) ?: return
        val computeShader = shaders[2].create(context) ?: return
        program = OpenGLUtils.createAndLinkProgram(vertexShader, fragmentShader) ?: return
        computeProgram = OpenGLUtils.createAndLinkProgram(computeShader) ?: return
        glDeleteShader(vertexShader)
        glDeleteShader(fragmentShader)
        glDeleteShader(computeShader)
    }


    override fun draw() {
        glBindVertexArray(vao[0])
        compute()
        drawTrail()
        glBindVertexArray(0)
    }
    private fun drawTrail() {
        glUseProgram(program)
        glEnableVertexAttribArray(shaderLocations.vertex.location)
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, vbo[0])
        Camera.bindUniform(shaderLocations.camera.location)
        GLES20.glDrawArrays(GL_LINE_STRIP, 0, vertex.pointNumber)
        glDisableVertexAttribArray(shaderLocations.vertex.location)
    }

    private fun compute() {
        if(properties.shouldUpdate().not()) {
          return
        }
        ShaderUtils.computeShader(
            shaderProgram = computeProgram,
            vbo = vbo[0],
            uniforms = {
                glUniform2f(shaderLocations.position.location, playerProperties.position.x, playerProperties.position.y)
            },
            x = 1,
            y = 1,
            z = 1,
        )
    }

    override fun release() {
        glDeleteVertexArrays(vao.size, vao, 0)
        glDeleteBuffers(vbo.size, vbo, 0)
        glDeleteProgram(program)
        glDeleteProgram(computeProgram)
    }



}
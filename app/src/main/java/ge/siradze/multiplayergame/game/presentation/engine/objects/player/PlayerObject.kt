package ge.siradze.multiplayergame.game.presentation.engine.objects.player

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.GL_DYNAMIC_DRAW
import android.opengl.GLES20.GL_FLOAT
import android.opengl.GLES20.GL_FRAGMENT_SHADER
import android.opengl.GLES20.GL_LINE_LOOP
import android.opengl.GLES20.GL_POINTS
import android.opengl.GLES20.GL_VERTEX_SHADER
import android.opengl.GLES20.glGetAttribLocation
import android.opengl.GLES20.glGetUniformLocation
import android.opengl.GLES20.glUniform2f
import android.opengl.GLES20.glVertexAttribPointer
import android.opengl.GLES30.GL_STATIC_DRAW
import android.opengl.GLES30.glDeleteBuffers
import android.opengl.GLES30.glDeleteProgram
import android.opengl.GLES30.glDeleteShader
import android.opengl.GLES30.glDeleteVertexArrays
import android.opengl.GLES30.glDisableVertexAttribArray
import android.opengl.GLES30.glEnableVertexAttribArray
import android.opengl.GLES30.glUniform1f
import android.opengl.GLES30.glUseProgram
import android.opengl.GLES31.GL_ARRAY_BUFFER
import android.opengl.GLES31.GL_SHADER_STORAGE_BUFFER
import android.opengl.GLES31.glBindBuffer
import android.opengl.GLES31.glBindVertexArray
import android.opengl.GLES31.glBufferData
import android.opengl.GLES31.glGenBuffers
import android.opengl.GLES31.glGenVertexArrays
import ge.siradze.multiplayergame.R
import ge.siradze.multiplayergame.game.presentation.engine.extensions.add
import ge.siradze.multiplayergame.game.presentation.engine.extensions.middlePoint
import ge.siradze.multiplayergame.game.presentation.engine.extensions.normalize
import ge.siradze.multiplayergame.game.presentation.engine.extensions.rotate
import ge.siradze.multiplayergame.game.presentation.engine.extensions.scale
import ge.siradze.multiplayergame.game.presentation.engine.extensions.times
import ge.siradze.multiplayergame.game.presentation.engine.extensions.x
import ge.siradze.multiplayergame.game.presentation.engine.extensions.y
import ge.siradze.multiplayergame.game.presentation.engine.gameUi.UIEvents
import ge.siradze.multiplayergame.game.presentation.engine.objects.GameObject
import ge.siradze.multiplayergame.game.presentation.engine.utils.OpenGLUtils
import ge.siradze.multiplayergame.game.presentation.engine.utils.ShaderUtils
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class PlayerData {

    class Vertex {
        private val numberOfFloatsPerVertex = 2
        private val data: FloatArray = floatArrayOf(
            0.0f, 0.5f,
            0.5f, -0.5f,
            -0.5f, -0.5f,
        ).scale(0.05f, numberOfFloatsPerVertex)

        val middlePoint = data.middlePoint(numberOfFloatsPerVertex)

        val pointNumber = data.size / numberOfFloatsPerVertex
        val stride = numberOfFloatsPerVertex * Float.SIZE_BYTES
        val bufferSize = data.size * Float.SIZE_BYTES

        fun getBuffer(): Buffer = ByteBuffer.allocateDirect(bufferSize)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(data).rewind()

    }

    class Trail {
        private val numberOfFloatsPerVertex = 2
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
        var trail: Int = 0,

        var ratio: Int = 0,
        var middlePoint : Int = 0,

        var position: Int = 0,
        var direction: Int = 0,
        var velocity: Int = 0,
    )

    class Properties(
        val position: FloatArray = floatArrayOf(0.0f, 0.0f),
        var direction: FloatArray = floatArrayOf(0.0f, 1.0f).apply {
            normalize()
        },
        private var velocity: Float = 0f,
    ) {
        private var gas = false
        private val gasForce = 0.0005f
        private val maxSpeed = 0.01f
        private val deceleration = 0.98f

        fun update() {
            if (gas && velocity < maxSpeed) {
                velocity += gasForce
            } else {
                velocity *= deceleration
            }
            position.add(direction * velocity)
        }

        fun onUIEvent(event: UIEvents) {
            when(event) {
                UIEvents.OnDown -> {
                    gas = true
                }
                UIEvents.OnUp -> {
                    gas = false
                }

                is UIEvents.OnMove -> {
                    direction.rotate(event.x)
                }

                else -> {}
            }
        }

    }
}



class PlayerObject(
    private val context: Context
): GameObject {

    private val vao: IntArray = IntArray(1)
    private val vbo: IntArray = IntArray(2) // 1. For player, 2. For trail.

    private val vertex = PlayerData.Vertex()
    private val trail = PlayerData.Trail()
    private val locations = PlayerData.ShaderLocations()
    private val properties = PlayerData.Properties()

    private var shaders = IntArray(4)
    private var playerProgram = 0
    private var trailProgram = 0


    override fun init() {
        initPrograms()
        glGenVertexArrays(1, vao, 0)
        glBindVertexArray(vao[0])
        initPlayer()
        initTrail()
    }

    private fun initPlayer() {
        glUseProgram(playerProgram)
        glGenBuffers(1, vbo, 0)
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0])
        glBufferData(
            GL_ARRAY_BUFFER,
            vertex.bufferSize,
            vertex.getBuffer(),
            GL_STATIC_DRAW
        )

        locations.vertex = glGetAttribLocation(playerProgram, "a_position")
        glEnableVertexAttribArray(locations.vertex)
        glVertexAttribPointer(locations.vertex, 2, GL_FLOAT, false, vertex.stride, 0)

        // Uniforms
        locations.ratio = glGetUniformLocation(playerProgram, "u_ratio")
        locations.middlePoint = glGetUniformLocation(playerProgram, "u_middlePoint")
        locations.position = glGetUniformLocation(playerProgram, "u_position")
        locations.direction = glGetUniformLocation(playerProgram, "u_direction")
        locations.velocity = glGetUniformLocation(playerProgram, "u_velocity")

        glUniform2f(locations.ratio, vertex.middlePoint.x, vertex.middlePoint.y)
    }

    private fun initTrail() {
        glGenBuffers(1, vbo, 1)
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, vbo[1])
        glBufferData(
            GL_SHADER_STORAGE_BUFFER,
            trail.bufferSize,
            trail.getBuffer(),
            GL_DYNAMIC_DRAW
        )

        locations.trail = glGetAttribLocation(playerProgram, "a_trail")
        glEnableVertexAttribArray(locations.trail)
        glVertexAttribPointer(locations.trail, 2, GL_FLOAT, false, trail.stride, 0)
    }

    override fun setRatio(ratio: Float) {
        glUseProgram(playerProgram)
        glUniform1f(locations.ratio, ratio)
    }


    private fun initPrograms(){
        loadPlayerProgram()
        loadTrailProgram()
    }
    private fun loadPlayerProgram() {
        shaders[0] = OpenGLUtils.createShader(
            GL_VERTEX_SHADER,
            ShaderUtils.readShaderFile(context, R.raw.player_vertex)
        ) ?: return
        shaders[1] = OpenGLUtils.createShader(
            GL_FRAGMENT_SHADER,
            ShaderUtils.readShaderFile(context, R.raw.player_fragment)
        ) ?: return
        playerProgram = OpenGLUtils.createAndLinkProgram(shaders[0], shaders[1]) ?: return
        glDeleteShader(shaders[0])
        glDeleteShader(shaders[1])
    }

    private fun loadTrailProgram() {
        shaders[2] = OpenGLUtils.createShader(
            GL_VERTEX_SHADER,
            ShaderUtils.readShaderFile(context, R.raw.player_trail_vertex)
        ) ?: return
        shaders[3] = OpenGLUtils.createShader(
            GL_FRAGMENT_SHADER,
            ShaderUtils.readShaderFile(context, R.raw.player_trail_fragment)
        ) ?: return
        trailProgram = OpenGLUtils.createAndLinkProgram(shaders[2], shaders[3]) ?: return
        glDeleteShader(shaders[2])
        glDeleteShader(shaders[3])
    }

    override fun draw() {
        glBindVertexArray(vao[0])

        drawPlayer()
        drawTrail()

        glBindVertexArray(0)
        glUseProgram(0)
    }

    private fun drawPlayer() {
        glUseProgram(playerProgram)
        glEnableVertexAttribArray(locations.vertex)

        updateAttributes()

        GLES20.glDrawArrays(GL_LINE_LOOP, 0, vertex.pointNumber,)

        glDisableVertexAttribArray(locations.vertex)
    }

    private fun drawTrail() {
        glUseProgram(trailProgram)
        glEnableVertexAttribArray(locations.trail)

        GLES20.glDrawArrays(GL_POINTS, 0, trail.pointNumber)

        glDisableVertexAttribArray(locations.trail)
    }

    private fun updateAttributes() {
        properties.update()
        glUniform2f(locations.direction, properties.direction.x, properties.direction.y)
        glUniform2f(locations.position, properties.position.x, properties.position.y)
    }

    override fun release() {
        glDeleteVertexArrays(vao.size, vao, 0)
        glDeleteBuffers(vbo.size, vbo, 0)
        glDeleteProgram(playerProgram)
    }

    fun onUIEvent(event: UIEvents) {
        properties.onUIEvent(event)
    }

}
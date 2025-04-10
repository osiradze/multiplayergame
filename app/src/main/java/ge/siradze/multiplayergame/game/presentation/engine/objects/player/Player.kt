package ge.siradze.multiplayergame.game.presentation.engine.objects.player

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.GL_FLOAT
import android.opengl.GLES20.GL_FRAGMENT_SHADER
import android.opengl.GLES20.GL_LINE_LOOP
import android.opengl.GLES20.GL_VERTEX_SHADER
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
import android.opengl.GLES31.glBindBuffer
import android.opengl.GLES31.glBindVertexArray
import android.opengl.GLES31.glBufferData
import android.opengl.GLES31.glGenBuffers
import android.opengl.GLES31.glGenVertexArrays
import ge.siradze.multiplayergame.R
import ge.siradze.multiplayergame.game.presentation.GameState
import ge.siradze.multiplayergame.game.presentation.engine.camera.Camera
import ge.siradze.multiplayergame.game.presentation.engine.extensions.x
import ge.siradze.multiplayergame.game.presentation.engine.extensions.y
import ge.siradze.multiplayergame.game.presentation.engine.objects.GameObject
import ge.siradze.multiplayergame.game.presentation.engine.shader.Shader
import ge.siradze.multiplayergame.game.presentation.engine.utils.OpenGLUtils
import ge.siradze.multiplayergame.game.presentation.gameUi.UIEvents


class PlayerObject(
    state: GameState,
    private val context: Context,
    private val camera: Camera,
): GameObject {

    private val vao: IntArray = IntArray(1)
    private val vbo: IntArray = IntArray(1)

    private val vertex = PlayerData.Vertex()
    private val shaderLocations = PlayerData.ShaderLocations()
    val properties: PlayerData.Properties =
        state.get(PlayerData.Properties::class.qualifiedName) as? PlayerData.Properties
            ?: PlayerData.Properties().also {
            state.set(PlayerData.Properties::class.qualifiedName, it)
        }

    private val shaders = arrayOf(
        Shader(
            type = GL_VERTEX_SHADER,
            source = R.raw.player_vertex,
            name = "Player Vertex"
        ),
        Shader(
            type = GL_FRAGMENT_SHADER,
            source = R.raw.player_fragment,
            name = "Player Fragment"
        )
    )
    private var program = 0

    override fun init() {
        initProgram()

        initData()

        initLocations()
    }

    private fun initData() {
        glGenVertexArrays(1, vao, 0)
        glBindVertexArray(vao[0])

        glGenBuffers(1, vbo, 0)
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0])
        glBufferData(
            GL_ARRAY_BUFFER,
            vertex.bufferSize,
            vertex.getBuffer(),
            GL_STATIC_DRAW
        )
    }

    private fun initLocations() {
        shaderLocations.vertex.init(program)
        glEnableVertexAttribArray(shaderLocations.vertex.location)
        glVertexAttribPointer(shaderLocations.vertex.location, 2, GL_FLOAT, false, vertex.stride, 0)
        glDisableVertexAttribArray(shaderLocations.vertex.location)

        // Uniforms
        shaderLocations.ratio.init(program)
        shaderLocations.camera.init(program)
        shaderLocations.middlePoint.init(program)
        shaderLocations.position.init(program)
        shaderLocations.direction.init(program)
        shaderLocations.velocity.init(program)

        glUniform2f(shaderLocations.ratio.location, vertex.middlePoint.x, vertex.middlePoint.y)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }

    private fun initProgram() {
        val vertexShader = shaders[0].create(context) ?: return
        val fragmentShader = shaders[1].create(context) ?: return

        program = OpenGLUtils.createAndLinkProgram(vertexShader, fragmentShader) ?: return
        glDeleteShader(vertexShader)
        glDeleteShader(fragmentShader)
    }


    override fun draw() {
        glBindVertexArray(vao[0])
        glUseProgram(program)
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0])
        glEnableVertexAttribArray(shaderLocations.vertex.location)

        updateAttributes()
        camera.bindUniform(shaderLocations.camera.location)
        GLES20.glDrawArrays(GL_LINE_LOOP, 0, vertex.pointNumber)

        glDisableVertexAttribArray(shaderLocations.vertex.location)

        glBindVertexArray(0)
    }

    private fun updateAttributes() {
        properties.update()
        glUniform2f(shaderLocations.direction.location, properties.direction.x, properties.direction.y)
        glUniform2f(shaderLocations.position.location, properties.position.x, properties.position.y)
    }

    fun onUIEvent(event: UIEvents) {
        properties.onUIEvent(event)
    }

    override fun setRatio(ratio: Float) {
        glUseProgram(program)
        glUniform1f(shaderLocations.ratio.location, ratio)
    }

    override fun release() {
        glDeleteVertexArrays(vao.size, vao, 0)
        glDeleteBuffers(vbo.size, vbo, 0)
        glDeleteProgram(program)
    }

}
package ge.siradze.multiplayergame.game.presentation.engine.objects.explosion

import android.content.Context
import android.opengl.GLES20.GL_ARRAY_BUFFER
import android.opengl.GLES20.GL_FRAGMENT_SHADER
import android.opengl.GLES20.GL_POINTS
import android.opengl.GLES20.GL_VERTEX_SHADER
import android.opengl.GLES20.glBindBuffer
import android.opengl.GLES20.glBufferData
import android.opengl.GLES20.glDeleteBuffers
import android.opengl.GLES20.glDeleteShader
import android.opengl.GLES20.glDisableVertexAttribArray
import android.opengl.GLES20.glDrawArrays
import android.opengl.GLES20.glEnableVertexAttribArray
import android.opengl.GLES20.glGenBuffers
import android.opengl.GLES20.glUniform1f
import android.opengl.GLES20.glUniform1i
import android.opengl.GLES20.glUniform2f
import android.opengl.GLES20.glUseProgram
import android.opengl.GLES30.glBindVertexArray
import android.opengl.GLES30.glDeleteVertexArrays
import android.opengl.GLES30.glGenVertexArrays
import android.opengl.GLES30.glUniform1ui
import android.opengl.GLES31.GL_COMPUTE_SHADER
import android.opengl.GLES31.GL_DYNAMIC_DRAW
import android.opengl.GLES31.GL_FLOAT
import ge.siradze.multiplayergame.R
import ge.siradze.multiplayergame.game.presentation.engine.EngineGlobals
import ge.siradze.multiplayergame.game.presentation.engine.camera.Camera
import ge.siradze.multiplayergame.game.presentation.engine.extensions.x
import ge.siradze.multiplayergame.game.presentation.engine.extensions.y
import ge.siradze.multiplayergame.game.presentation.engine.objects.GameObject
import ge.siradze.multiplayergame.game.presentation.engine.objects.player.PlayerData
import ge.siradze.multiplayergame.game.presentation.engine.shader.Shader
import ge.siradze.multiplayergame.game.presentation.engine.utils.OpenGLUtils
import ge.siradze.multiplayergame.game.presentation.engine.utils.ShaderUtils


class Explosion(
    private val context: Context,
    private val camera: Camera,
    private val playerProperties: PlayerData.Properties,
    helper: ExplosionHelper,
    planet: FloatArray,
    size: Float,
    position: FloatArray,
    color: FloatArray,
): GameObject {
    private val vao: IntArray = IntArray(1)
    private val vbo: IntArray = IntArray(1)

    private val vertex: ExplosionData.Vertex =
        ExplosionData.Vertex(
            helper = helper,
            tilePosition = planet,
            size = size,
            position = position,
            color = color,
        )

    private val shader = ExplosionData.ShaderLocations()

    private val shaders = arrayOf(
        Shader(
            type = GL_VERTEX_SHADER,
            source = R.raw.explosion_vertex,
            name = "Explosion Vertex"
        ),
        Shader(
            type = GL_FRAGMENT_SHADER,
            source = R.raw.explosion_fragment,
            name = "Explosion Fragment"
        ),
        Shader(
            type = GL_COMPUTE_SHADER,
            source = R.raw.explosion_compute,
            name = "Explosion Compute"
        ),

    )

    private var program: Int = 0
    private var computeProgram: Int = 0

    override fun init() {
        initProgram()
        initData()
        initLocations()
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
    private fun initData() {
        glGenVertexArrays(1, vao, 0)
        glBindVertexArray(vao[0])

        glGenBuffers(1, vbo, 0)
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0])
        glBufferData(GL_ARRAY_BUFFER, vertex.bufferSize, vertex.getBuffer(), GL_DYNAMIC_DRAW)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }
    private fun initLocations() {
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0])

        shader.vertex.apply {
            init(program)
            load(2, GL_FLOAT, false, vertex.stride, 0)
        }
        shader.color.apply {
            init(program)
            load(3, GL_FLOAT, false, vertex.stride, 2 * vertex.typeSize)
        }

        shader.isDead.apply {
            init(program)
            load(1, GL_FLOAT, false, vertex.stride, 7 * vertex.typeSize)
        }

        // Uniforms
        shader.ratio.init(program)
        shader.camera.init(program)
        glUseProgram(program)

        shader.floatsPerVertex.init(computeProgram)
        shader.playerPosition.init(computeProgram)
        shader.deltaTime.init(computeProgram)
        shader.push.init(computeProgram)
    }

    override fun draw() {
        glBindVertexArray(vao[0])

        compute()
        drawExplosion()

        glBindVertexArray(0)
    }

    private fun compute() {
        ShaderUtils.computeShader(
            shaderProgram = computeProgram,
            uniforms = {
                glUniform1ui(shader.floatsPerVertex.location, vertex.numberOfFloatsPerVertex)
                glUniform2f(shader.playerPosition.location, playerProperties.position.x, playerProperties.position.y)
                glUniform1f(shader.deltaTime.location, EngineGlobals.deltaTime)
                glUniform1i(shader.push.location, if (playerProperties.push) 1 else 0)
            },
            vbos = vbo,
            x = vertex.pointNumber,
        )
    }



    private fun drawExplosion() {
        glUseProgram(program)
        glEnableVertexAttribArray(shader.vertex.location)
        glEnableVertexAttribArray(shader.color.location)
        glEnableVertexAttribArray(shader.isDead.location)
        camera.bindUniform(shader.camera.location)

        glDrawArrays(
            GL_POINTS,
            0,
            vertex.numberOfVertex
        )

        glDisableVertexAttribArray(shader.vertex.location)
        glDisableVertexAttribArray(shader.color.location)
        glDisableVertexAttribArray(shader.isDead.location)
        glBindBuffer(GL_ARRAY_BUFFER, 0)

    }

    override fun setRatio(ratio: Float) {
        super.setRatio(ratio)
        glUseProgram(program)
        glUniform1f(shader.ratio.location, ratio)
    }


    override fun release() {
        glDeleteBuffers(vbo.size, vbo, 0)
        glDeleteVertexArrays(vao.size, vao, 0)
        glDeleteShader(program)
    }
}
package ge.siradze.multiplayergame.game.presentation.engine.objects.planets.explosion

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
import android.opengl.GLES20.glUseProgram
import android.opengl.GLES30.glBindVertexArray
import android.opengl.GLES30.glDeleteVertexArrays
import android.opengl.GLES30.glGenVertexArrays
import android.opengl.GLES31.GL_DYNAMIC_DRAW
import android.opengl.GLES31.GL_FLOAT
import ge.siradze.multiplayergame.R
import ge.siradze.multiplayergame.game.presentation.engine.camera.Camera
import ge.siradze.multiplayergame.game.presentation.engine.extensions.toBuffer
import ge.siradze.multiplayergame.game.presentation.engine.objects.AttributeData
import ge.siradze.multiplayergame.game.presentation.engine.objects.GameObject
import ge.siradze.multiplayergame.game.presentation.engine.shader.CameraShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.RatioShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.Shader
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderAttribLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.utils.OpenGLUtils
import java.nio.Buffer

class PlanetExplosionData {
    class Vertex(
        helper: PlanetExplosionHelper,
        x: Int,
        y: Int,
    ): AttributeData() {
        val data = helper.data[x][y]

        override val numberOfFloatsPerVertex: Int = helper.numberOfFloatsPerPoint
        override val typeSize: Int = Float.SIZE_BYTES
        override val size: Int = helper.numberOfFloatsPerPoint * helper.pointNumber
        val numberOfVertex = helper.pointNumber



        override fun getBuffer(): Buffer = data.toBuffer()
    }

    class ShaderLocations (
        val vertex: ShaderAttribLocation = ShaderAttribLocation(name = "a_position"),
        val color: ShaderAttribLocation = ShaderAttribLocation(name = "a_color"),
        val ratio: ShaderLocation = RatioShaderLocation(),
        var camera: ShaderLocation = CameraShaderLocation(),
    )
}

class PlanetExplosion(
    private val context: Context,
    private val camera: Camera,
    helper: PlanetExplosionHelper,
    x: Int,
    y: Int,
): GameObject {
    private val vao: IntArray = IntArray(1)
    private val vbo: IntArray = IntArray(1)

    private val vertex: PlanetExplosionData.Vertex =
        PlanetExplosionData.Vertex(helper, x, y)

    private val shader = PlanetExplosionData.ShaderLocations()

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
    )

    private var program: Int = 0

    override fun init() {
        initProgram()
        initData()
        initLocations()
    }
    private fun initProgram() {
        val vertexShader = shaders[0].create(context) ?: return
        val fragmentShader = shaders[1].create(context) ?: return
        program = OpenGLUtils.createAndLinkProgram(vertexShader, fragmentShader) ?: return
        glDeleteShader(vertexShader)
        glDeleteShader(fragmentShader)
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

        // Uniforms
        shader.ratio.init(program)
        shader.camera.init(program)
    }

    override fun draw() {
        glBindVertexArray(vao[0])

        // compute()
        drawExplosion()

        glBindVertexArray(0)
    }
    private fun drawExplosion() {
        glUseProgram(program)
        glEnableVertexAttribArray(shader.vertex.location)
        glEnableVertexAttribArray(shader.color.location)
        camera.bindUniform(shader.camera.location)

        glDrawArrays(
            GL_POINTS,
            0,
            vertex.numberOfVertex
        )

        glDisableVertexAttribArray(shader.vertex.location)
        glDisableVertexAttribArray(shader.color.location)
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
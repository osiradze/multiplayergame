package ge.siradze.multiplayergame.game.presentation.engine.objects.stars

import android.content.Context
import android.opengl.GLES20.GL_FRAGMENT_SHADER
import android.opengl.GLES20.GL_POINTS
import android.opengl.GLES20.glDeleteBuffers
import android.opengl.GLES20.glDeleteShader
import android.opengl.GLES20.glDisableVertexAttribArray
import android.opengl.GLES20.glDrawArrays
import android.opengl.GLES20.glEnableVertexAttribArray
import android.opengl.GLES20.glUseProgram
import android.opengl.GLES30.GL_VERTEX_SHADER
import android.opengl.GLES30.glDeleteVertexArrays
import android.opengl.GLES30.glUniform1ui
import android.opengl.GLES31.GL_ARRAY_BUFFER
import android.opengl.GLES31.GL_COMPUTE_SHADER
import android.opengl.GLES31.GL_DYNAMIC_DRAW
import android.opengl.GLES31.GL_FLOAT
import android.opengl.GLES31.glBindBuffer
import android.opengl.GLES31.glBindVertexArray
import android.opengl.GLES31.glBufferData
import android.opengl.GLES31.glGenBuffers
import android.opengl.GLES31.glGenVertexArrays
import ge.siradze.multiplayergame.R
import ge.siradze.multiplayergame.game.presentation.engine.camera.Camera
import ge.siradze.multiplayergame.game.presentation.engine.extensions.toBuffer
import ge.siradze.multiplayergame.game.presentation.engine.objects.GameObject
import ge.siradze.multiplayergame.game.presentation.engine.shader.CameraShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.Shader
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderAttribLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderUniformLocation
import ge.siradze.multiplayergame.game.presentation.engine.utils.OpenGLUtils
import ge.siradze.multiplayergame.game.presentation.engine.utils.ShaderUtils
import java.nio.Buffer
import kotlin.random.Random

class StarsData {

    @Suppress("NOTHING_TO_INLINE")
    class Vertex(
        val numberOfPoints: Int = 6000
    ) {
        // 4 floats per vertex, 2 for position, 2 for velocity
        val numberOfFloatsPerVertex = 5

        private val data: FloatArray = FloatArray(numberOfPoints * numberOfFloatsPerVertex)

        val stride = numberOfFloatsPerVertex * Float.SIZE_BYTES
        val bufferSize = data.size * Float.SIZE_BYTES
        fun getBuffer(): Buffer = data.toBuffer()

        init {
            generatePoints()
        }

        private fun generatePoints() {
            for (i in 0 until numberOfPoints) {
                //position
                data[px(i)] = (Random.nextFloat() - 0.5f) * 4
                data[py(i)] = (Random.nextFloat() - 0.5f) * 4
                data[vx(i)] = -0.0006f
                data[vy(i)] = -0.0006f
                data[vy(i) + 1] = Random.nextFloat() * 0.5f
            }
        }

        private inline fun px(i: Int) = i * numberOfFloatsPerVertex
        private inline fun py(i: Int) = i * numberOfFloatsPerVertex + 1
        private inline fun vx(i: Int) = i * numberOfFloatsPerVertex + 2
        private inline fun vy(i: Int) = i * numberOfFloatsPerVertex + 3
    }

    class ShaderLocations(
        val vertex : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_position"
        ),
        val brightness : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_brightness"
        ),
        val floatsPerVertex : ShaderLocation = ShaderUniformLocation(
            name = "floats_per_vertex"
        ),
        val camera : ShaderLocation = CameraShaderLocation()
    )
}

class Stars(
    private val context: Context,
    private val camera: Camera
): GameObject {

    private val vao: IntArray = IntArray(1)
    private val vbo: IntArray = IntArray(1)


    private val vertex = StarsData.Vertex()
    private val shader = StarsData.ShaderLocations()
    private val shaders = arrayOf(
        Shader(
            type = GL_VERTEX_SHADER,
            source = R.raw.stars_vertex,
            name = "Wind Vertex"
        ),
        Shader(
            type = GL_FRAGMENT_SHADER,
            source = R.raw.stars_fragment,
            name = "Wind Fragment"
        ),
        Shader(
            type = GL_COMPUTE_SHADER,
            source = R.raw.stars_compute,
            name = "Wind Compute"
        )
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

        shader.vertex.apply {
            init(program)
            load(2, GL_FLOAT, false, vertex.stride, 0)
        }
        shader.brightness.apply {
            init(program)
            load(1, GL_FLOAT, false, vertex.stride, 4 * Float.SIZE_BYTES)
        }
        shader.camera.init(computeProgram)
        shader.floatsPerVertex.init(computeProgram)

        glBindBuffer(GL_ARRAY_BUFFER, 0)
        glBindVertexArray(0)
    }

    private fun initProgram() {
        val vertexShader = shaders[0].create(context) ?: return
        val fragmentShader = shaders[1].create(context) ?: return
        val computeShader = shaders[2].create(context) ?: return

        program = OpenGLUtils.createAndLinkProgram(vertexShader, fragmentShader) ?: return
        computeProgram = OpenGLUtils.createAndLinkProgram(computeShader) ?: return
        glDeleteShader(vertexShader)
        glDeleteShader(fragmentShader)
    }

    override fun draw() {

        ShaderUtils.computeShader(
            shaderProgram = computeProgram,
            uniforms = {
                glUniform1ui(shader.floatsPerVertex.location, vertex.numberOfFloatsPerVertex)
                camera.bindUniform(shader.camera.location)
            },
            vbos = vbo,
            x = vertex.numberOfFloatsPerVertex,
            y = vertex.numberOfPoints
        )


        glUseProgram(program)
        glBindVertexArray(vao[0])
        glEnableVertexAttribArray(shader.vertex.location)
        glEnableVertexAttribArray(shader.brightness.location)

        glDrawArrays(
            GL_POINTS,
            0,
            vertex.numberOfPoints
        )

        glDisableVertexAttribArray(shader.vertex.location)
        glDisableVertexAttribArray(shader.brightness.location)
        glBindVertexArray(0)
        glUseProgram(0)
    }

    override fun release() {
        glDeleteBuffers(1, vbo, 0)
        glDeleteVertexArrays(1, vao, 0)
        glDeleteShader(program)
    }


}



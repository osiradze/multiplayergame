package ge.siradze.explosion

import android.content.Context
import android.opengl.GLES20.GL_ARRAY_BUFFER
import android.opengl.GLES20.GL_FRAGMENT_SHADER
import android.opengl.GLES20.GL_POINTS
import android.opengl.GLES20.GL_VERTEX_SHADER
import android.opengl.GLES20.glBindBuffer
import android.opengl.GLES20.glBufferData
import android.opengl.GLES20.glDeleteBuffers
import android.opengl.GLES20.glDeleteShader
import android.opengl.GLES20.glDrawArrays
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
import ge.siradze.glcore.EngineGlobals
import ge.siradze.glcore.camera.Camera
import ge.siradze.glcore.extensions.x
import ge.siradze.glcore.extensions.y
import ge.siradze.core.GameObject
import ge.siradze.explosion.data.ShaderLocations
import ge.siradze.explosion.data.Vertex
import ge.siradze.explosion.helper.ExplosionHelper
import ge.siradze.glcore.shader.Shader
import ge.siradze.glcore.utils.OpenGLUtils
import ge.siradze.glcore.utils.ShaderUtils


class Explosion(
    private val context: Context,
    private val camera: Camera,
    private val playerPosition: FloatArray,
    helper: ExplosionHelper,
    planet: FloatArray,
    size: Float,
    position: FloatArray,
    color: FloatArray,
): GameObject {
    private val vao: IntArray = IntArray(1)
    private val vbo: IntArray = IntArray(1)

    private val vertex: Vertex = Vertex(
        helper = helper,
        tilePosition = planet,
        size = size,
        position = position,
        color = color,
    )

    private val shader = ShaderLocations()

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
        shader.init(
            program = program,
            computeProgram = computeProgram,
            stride = vertex.stride,
            type = GL_FLOAT,
        )
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
                glUniform2f(shader.playerPosition.location, playerPosition.x, playerPosition.y)
                glUniform1f(shader.deltaTime.location, EngineGlobals.deltaTime)
                glUniform1i(shader.push.location, 1)
            },
            vbos = vbo,
            x = vertex.pointNumber,
        )
    }

    private fun drawExplosion() {
        glUseProgram(program)

        shader.enableAttributeLocations()
        camera.bindUniform(shader.camera.location)

        glDrawArrays(
            GL_POINTS,
            0,
            vertex.numberOfVertex
        )

        shader.disableAttributeLocations()
        glBindBuffer(GL_ARRAY_BUFFER, 0)

    }

    override fun setRatio(ratio: Float) {
        super.setRatio(ratio)
        glUseProgram(program)
        glUniform1f(shader.ratio.location, ratio)
    }

    override fun onSizeChange(width: Int, height: Int) {
        super.onSizeChange(width, height)
    }


    override fun release() {
        glDeleteBuffers(vbo.size, vbo, 0)
        glDeleteVertexArrays(vao.size, vao, 0)
        glDeleteShader(program)
    }
}
package ge.siradze.player.trail

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.GL_DYNAMIC_DRAW
import android.opengl.GLES20.GL_FRAGMENT_SHADER
import android.opengl.GLES20.GL_LINE_STRIP
import android.opengl.GLES20.GL_VERTEX_SHADER
import android.opengl.GLES20.glDeleteBuffers
import android.opengl.GLES20.glDeleteProgram
import android.opengl.GLES20.glUniform2f
import android.opengl.GLES30.glDeleteShader
import android.opengl.GLES30.glDeleteVertexArrays
import android.opengl.GLES30.glDisableVertexAttribArray
import android.opengl.GLES30.glEnableVertexAttribArray
import android.opengl.GLES30.glUniform1f
import android.opengl.GLES30.glUniform1ui
import android.opengl.GLES30.glUseProgram
import android.opengl.GLES31.GL_ARRAY_BUFFER
import android.opengl.GLES31.GL_COMPUTE_SHADER
import android.opengl.GLES31.GL_SHADER_STORAGE_BUFFER
import android.opengl.GLES31.glBindBuffer
import android.opengl.GLES31.glBindVertexArray
import android.opengl.GLES31.glBufferData
import android.opengl.GLES31.glGenBuffers
import android.opengl.GLES31.glGenVertexArrays
import ge.siradze.glcore.camera.Camera
import ge.siradze.glcore.extensions.x
import ge.siradze.glcore.extensions.y
import ge.siradze.glcore.shader.Shader
import ge.siradze.glcore.utils.OpenGLUtils
import ge.siradze.glcore.utils.ShaderUtils
import ge.siradze.player.R
import ge.siradze.player.main.PlayerProperties
import ge.siradze.player.trail.data.ShaderLocations
import ge.siradze.player.trail.data.Vertex

class PlayerTrail(
    private val context: Context,
    private val playerProperties: PlayerProperties,
    private val camera: Camera
): ge.siradze.core.GameObject {


    private val vao: IntArray = IntArray(1)
    private val vbo: IntArray = IntArray(1)

    private val vertex = Vertex(playerProperties.position)
    private val shader = ShaderLocations()

    private val shaders = arrayOf(
        Shader(
            type = GL_VERTEX_SHADER,
            source = R.raw.player_trail_vertex,
            name = "Trail Vertex"
        ),
        Shader(
            type = GL_FRAGMENT_SHADER,
            source = R.raw.player_trail_fragment,
            name = "Trail Fragment"
        ),
        Shader(
            type = GL_COMPUTE_SHADER,
            source = R.raw.player_trail_compute,
            name = "Trail Compute"
        ),
    )
    private var program = 0
    private var computeProgram = 0

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
        glBufferData(
            GL_ARRAY_BUFFER,
            vertex.bufferSize,
            vertex.getBuffer(),
            GL_DYNAMIC_DRAW
        )

    }

    private fun initLocations() {
        shader.init(
            program = program,
            computeProgram = computeProgram,
            stride = vertex.stride,
        )
        glUseProgram(computeProgram)
        glUniform1ui(
            shader.dataSize.location,
            vertex.data.size
        )
        glBindBuffer(GL_ARRAY_BUFFER, 0)
    }


    override fun draw() {
        glBindVertexArray(vao[0])
        compute()
        drawTrail()
        glBindVertexArray(0)
    }
    private fun drawTrail() {
        glUseProgram(program)
        glEnableVertexAttribArray(shader.vertex.location)
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, vbo[0])
        camera.bindUniform(shader.camera.location)
        GLES20.glDrawArrays(GL_LINE_STRIP, 0, vertex.pointNumber)
        glDisableVertexAttribArray(shader.vertex.location)
    }

    private fun compute() {
        ShaderUtils.computeShader(
            shaderProgram = computeProgram,
            vbos = vbo,
            uniforms = {
                glUniform2f(
                    shader.playerPosition.location,
                    playerProperties.position.x,
                    playerProperties.position.y
                )
                glUniform1ui(
                    shader.floatsPerVertex.location,
                    vertex.numberOfFloatsPerVertex
                )
            },
            // we are dispatching only one worker.
            x = 1,
            y = 1,
            z = 1,
        )
    }

    override fun setRatio(ratio: Float) {
        glUseProgram(program)
        glUniform1f(shader.ratio.location, ratio)
    }

    override fun release() {
        glDeleteVertexArrays(vao.size, vao, 0)
        glDeleteBuffers(vbo.size, vbo, 0)
        glDeleteProgram(program)
        glDeleteProgram(computeProgram)
    }



}
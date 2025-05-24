package ge.siradze.player.trail

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.GL_DYNAMIC_DRAW
import android.opengl.GLES20.GL_FLOAT
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
import ge.siradze.glcore.extensions.fillWith
import ge.siradze.glcore.extensions.toBuffer
import ge.siradze.glcore.extensions.x
import ge.siradze.glcore.extensions.y
import ge.siradze.player.PlayerData
import ge.siradze.glcore.shader.CameraShaderLocation
import ge.siradze.glcore.shader.RatioShaderLocation
import ge.siradze.glcore.shader.Shader
import ge.siradze.glcore.shader.ShaderAttribLocation
import ge.siradze.glcore.shader.ShaderLocation
import ge.siradze.glcore.shader.ShaderUniformLocation
import ge.siradze.glcore.utils.OpenGLUtils
import ge.siradze.glcore.utils.ShaderUtils
import ge.siradze.player.R
import java.nio.Buffer


class PlayerTrailData {

    class Vertex(
        val initPosition: FloatArray
    ) {
        // 3 floats per vertex, 2 for position, 1 for alpha
        val numberOfFloatsPerVertex = 3
        val data: FloatArray = FloatArray(size = 60 * numberOfFloatsPerVertex) { 0f }.also {
            it.fillWith(
                floatArrayOf(initPosition.x, initPosition.y, 0f)
            )
            for (i in it.indices) {
               if(i % numberOfFloatsPerVertex == 0) {
                   it[i+2] = i.toFloat() / it.size.toFloat()
               }
            }
        }
        val pointNumber = data.size / numberOfFloatsPerVertex
        val stride = numberOfFloatsPerVertex * Float.SIZE_BYTES
        val bufferSize = data.size * Float.SIZE_BYTES

        fun getBuffer(): Buffer = data.toBuffer()
    }

    class ShaderLocations(
        val vertex : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_position"
        ),
        val ratio: ShaderLocation = RatioShaderLocation(),
        val camera: ShaderLocation = CameraShaderLocation(),
        val playerPosition: ShaderLocation = ShaderUniformLocation(
            name = "u_player_position"
        ),
        val floatsPerVertex: ShaderLocation = ShaderUniformLocation(
            name = "u_floatsPerVertex"
        ),
        val dataSize: ShaderUniformLocation = ShaderUniformLocation(
            name = "u_dataSize"
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
    private val playerProperties: PlayerData.Properties,
    private val camera: Camera
): ge.siradze.core.GameObject {


    private val vao: IntArray = IntArray(1)
    private val vbo: IntArray = IntArray(1)

    private val vertex = PlayerTrailData.Vertex(playerProperties.position)
    private val shader = PlayerTrailData.ShaderLocations()
    private val properties = PlayerTrailData.Properties()

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
        shader.vertex.apply {
            init(program)
            load(3, GL_FLOAT, false, vertex.stride, 0)
        }

        shader.ratio.init(program)
        shader.camera.init(program)
        shader.playerPosition.init(computeProgram)
        shader.floatsPerVertex.init(computeProgram)
        shader.dataSize.init(computeProgram)
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
        if(properties.shouldUpdate().not()) {
          return
        }
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
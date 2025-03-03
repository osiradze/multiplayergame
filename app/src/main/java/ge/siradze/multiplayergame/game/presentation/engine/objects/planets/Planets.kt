package ge.siradze.multiplayergame.game.presentation.engine.objects.planets

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20.GL_ARRAY_BUFFER
import android.opengl.GLES20.GL_FRAGMENT_SHADER
import android.opengl.GLES20.GL_POINTS
import android.opengl.GLES20.glDeleteBuffers
import android.opengl.GLES20.glDeleteShader
import android.opengl.GLES20.glDisableVertexAttribArray
import android.opengl.GLES20.glDrawArrays
import android.opengl.GLES20.glEnableVertexAttribArray
import android.opengl.GLES20.glGenBuffers
import android.opengl.GLES20.glUniform1f
import android.opengl.GLES20.glUseProgram
import android.opengl.GLES30.GL_VERTEX_SHADER
import android.opengl.GLES30.glBindVertexArray
import android.opengl.GLES30.glDeleteVertexArrays
import android.opengl.GLES30.glGenTextures
import android.opengl.GLES30.glGenVertexArrays
import android.opengl.GLES31.GL_DYNAMIC_DRAW
import android.opengl.GLES31.GL_FLOAT
import android.opengl.GLES31.glBindBuffer
import android.opengl.GLES31.glBufferData
import ge.siradze.multiplayergame.R
import ge.siradze.multiplayergame.game.presentation.engine.camera.Camera
import ge.siradze.multiplayergame.game.presentation.engine.extensions.toBuffer
import ge.siradze.multiplayergame.game.presentation.engine.objects.GameObject
import ge.siradze.multiplayergame.game.presentation.engine.shader.CameraShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.RatioShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.Shader
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderAttribLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderUniformLocation
import ge.siradze.multiplayergame.game.presentation.engine.texture.TextureDimensions
import ge.siradze.multiplayergame.game.presentation.engine.texture.TextureHelper
import ge.siradze.multiplayergame.game.presentation.engine.utils.OpenGLUtils
import java.nio.Buffer
import kotlin.random.Random

class PlanetsData {
    class Vertex(
        val numberOfPlanets: Int = 700
    ) {
        // 2 position + 1 size + 4 texture coordinates + 3 color
        private val numberOfFloatsPerVertex = 10
        private val data: FloatArray = FloatArray(numberOfPlanets * numberOfFloatsPerVertex)
        val stride = numberOfFloatsPerVertex * Float.SIZE_BYTES
        val bufferSize = data.size * Float.SIZE_BYTES

        fun getBuffer(): Buffer = data.toBuffer()

        private val textureDimensions : TextureDimensions = TextureDimensions(4, 5)


        init {
            generatePoints()
        }


        private fun generatePoints() {
            for (i in 0 until numberOfPlanets) {
                //position
                data[i * numberOfFloatsPerVertex + 0] = (Random.nextFloat() - 0.5f) * 20
                data[i * numberOfFloatsPerVertex + 1] = (Random.nextFloat() - 0.5f) * 20

                val randomX = Random.nextInt(until = 5) + 1
                val randomY = Random.nextInt(until = 4) + 1

                data[i * numberOfFloatsPerVertex + 2] = Random.nextFloat() * 300f + 50f

                data[i * numberOfFloatsPerVertex + 3] = textureDimensions.stepX * (randomX - 1)
                data[i * numberOfFloatsPerVertex + 4] = textureDimensions.stepY * (randomY - 1)
                data[i * numberOfFloatsPerVertex + 5] = textureDimensions.stepX
                data[i * numberOfFloatsPerVertex + 6] = textureDimensions.stepY

                data[i * numberOfFloatsPerVertex + 7] = Random.nextFloat() * 0.5f + 0.5f
                data[i * numberOfFloatsPerVertex + 8] = Random.nextFloat() * 0.5f + 0.5f
                data[i * numberOfFloatsPerVertex + 9] = Random.nextFloat() * 0.5f + 0.5f
            }
        }
    }

    class ShaderLocations(
        val vertex : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_position"
        ),
        val size : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_size"
        ),
        val textureCoordinates : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_texture_coordinates"
        ),
        val color : ShaderAttribLocation = ShaderAttribLocation(
            name = "a_color"
        ),

        val ratio: ShaderLocation = RatioShaderLocation(),
        var camera: ShaderLocation = CameraShaderLocation(),

        val texture: ShaderLocation = ShaderUniformLocation(
            name = "u_texture"
        )
    )
}

class Planets(val context: Context): GameObject {

    private val vao: IntArray = IntArray(1)
    private val vbo: IntArray = IntArray(1)

    private val vertex = PlanetsData.Vertex()
    private val shader = PlanetsData.ShaderLocations()
    private val shaders = arrayOf(
        Shader(
            type = GL_VERTEX_SHADER,
            source = R.raw.planets_vertex,
            name = "Planets Vertex"
        ),
        Shader(
            type = GL_FRAGMENT_SHADER,
            source = R.raw.planets_fragment,
            name = "Planets Fragment"
        ),
    )

    private val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.planets)

    private val textures = IntArray(1)

    private var program: Int = 0

    override fun init() {
        initProgram()
        initData()
        initLocations()
        bindTexture()
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

        glBufferData(
            GL_ARRAY_BUFFER,
            vertex.bufferSize,
            vertex.getBuffer(),
            GL_DYNAMIC_DRAW
        )
    }


    private fun initLocations() {
        // attributes
        shader.vertex.apply {
            init(program)
            load(2, GL_FLOAT, false, vertex.stride, 0)
        }

        shader.size.apply {
            init(program)
            load(1, GL_FLOAT, false, vertex.stride, 2 * Float.SIZE_BYTES)
        }

        shader.textureCoordinates.apply {
            init(program)
            load(4, GL_FLOAT, false, vertex.stride, 3 * Float.SIZE_BYTES)
        }

        shader.color.apply {
            init(program)
            load(3, GL_FLOAT, false, vertex.stride, 7 * Float.SIZE_BYTES)
        }

        // Uniforms
        shader.ratio.init(program)
        shader.camera.init(program)
    }

    private fun bindTexture() {
        glGenTextures(1, textures, 0)

        OpenGLUtils.loadTexture(
            bitmap,
            textures[0],
            shader.texture.location,
            TextureHelper.getTextureOffset(1)
        )
    }

    override fun draw() {
        glUseProgram(program)
        glBindVertexArray(vao[0])

        glEnableVertexAttribArray(shader.vertex.location)
        glEnableVertexAttribArray(shader.textureCoordinates.location)
        glEnableVertexAttribArray(shader.size.location)
        glEnableVertexAttribArray(shader.color.location)

        Camera.bindUniform(shader.camera.location)

        glDrawArrays(
            GL_POINTS,
            0,
            vertex.numberOfPlanets
        )

        glDisableVertexAttribArray(shader.vertex.location)
        glDisableVertexAttribArray(shader.textureCoordinates.location)
        glDisableVertexAttribArray(shader.size.location)
        glDisableVertexAttribArray(shader.color.location)

        glBindVertexArray(0)
        glUseProgram(0)
    }

    override fun setRatio(ratio: Float) {
        super.setRatio(ratio)
        glUseProgram(program)
        glUniform1f(shader.ratio.location, ratio)
    }

    override fun release() {
        glDeleteBuffers(1, vbo, 0)
        glDeleteVertexArrays(1, vao, 0)
        glDeleteShader(program)
    }

}
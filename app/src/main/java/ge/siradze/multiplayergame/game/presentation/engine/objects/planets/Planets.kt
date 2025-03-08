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
import android.opengl.GLES20.glUniform2f
import android.opengl.GLES20.glUseProgram
import android.opengl.GLES30.GL_VERTEX_SHADER
import android.opengl.GLES30.glBindVertexArray
import android.opengl.GLES30.glDeleteVertexArrays
import android.opengl.GLES30.glGenTextures
import android.opengl.GLES30.glGenVertexArrays
import android.opengl.GLES30.glUniform1ui
import android.opengl.GLES31.GL_COMPUTE_SHADER
import android.opengl.GLES31.GL_DYNAMIC_DRAW
import android.opengl.GLES31.GL_FLOAT
import android.opengl.GLES31.GL_SHADER_STORAGE_BUFFER
import android.opengl.GLES31.glBindBuffer
import android.opengl.GLES31.glBufferData
import android.util.Log
import ge.siradze.multiplayergame.R
import ge.siradze.multiplayergame.game.presentation.engine.camera.Camera
import ge.siradze.multiplayergame.game.presentation.engine.extensions.toBuffer
import ge.siradze.multiplayergame.game.presentation.engine.extensions.x
import ge.siradze.multiplayergame.game.presentation.engine.extensions.y
import ge.siradze.multiplayergame.game.presentation.engine.objects.AttributeData
import ge.siradze.multiplayergame.game.presentation.engine.objects.GameObject
import ge.siradze.multiplayergame.game.presentation.engine.objects.player.PlayerData
import ge.siradze.multiplayergame.game.presentation.engine.shader.CameraShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.RatioShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.Shader
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderAttribLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderUniformLocation
import ge.siradze.multiplayergame.game.presentation.engine.texture.TextureDimensions
import ge.siradze.multiplayergame.game.presentation.engine.texture.TextureHelper
import ge.siradze.multiplayergame.game.presentation.engine.utils.OpenGLUtils
import ge.siradze.multiplayergame.game.presentation.engine.utils.ShaderUtils
import java.nio.Buffer

class PlanetsData {
    class Vertex(
        val numberOfPlanets: Int = 700
    ): AttributeData() {
        // 2 position + 1 size + 4 texture coordinates + 3 color
        override val numberOfFloatsPerVertex = 10
        override val typeSize = Float.SIZE_BYTES
        override val size = numberOfPlanets * numberOfFloatsPerVertex
        private val data: FloatArray = FloatArray(size)

        override fun getBuffer(): Buffer = data.toBuffer()

        private val textureDimensions : TextureDimensions = TextureDimensions(4, 5)

        init {
            generatePoints(
                data = data,
                numberOfPlanets = numberOfPlanets,
                numberOfFloatsPerVertex = numberOfFloatsPerVertex,
                textureDimensions = textureDimensions
            )
        }
    }

    class CollisionData {
        val data: FloatArray = FloatArray(5)
        val buffer: Buffer = data.toBuffer()
        val bufferSize = data.size * Float.SIZE_BYTES
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

        val screenWidth : ShaderUniformLocation = ShaderUniformLocation(
            name = "u_screen_width"
        ),

        val ratio: ShaderLocation = RatioShaderLocation(),
        var camera: ShaderLocation = CameraShaderLocation(),

        val texture: ShaderLocation = ShaderUniformLocation(
            name = "u_texture"
        ),
        val floatsPerVertex: ShaderLocation = ShaderUniformLocation(
            name = "u_floats_per_vertex"
        ),
        val playerPosition: ShaderLocation = ShaderUniformLocation(
            name = "u_player_position"
        ),
    )
}

class Planets(
    val context: Context,
    private val playerProperties: PlayerData.Properties
): GameObject {

    private val vao: IntArray = IntArray(1)
    private val vbo: IntArray = IntArray(2)

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
        Shader(
            type = GL_COMPUTE_SHADER,
            source = R.raw.planets_compute,
            name = "Planets Compute"
        )
    )

    private val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.planets)
    private val textures = IntArray(1)

    private var program: Int = 0
    private var computeProgram: Int = 0

    private val collisionData = PlanetsData.CollisionData()

    override fun init() {
        initProgram()
        initData()
        initLocations()
        bindTexture()
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

        glGenBuffers(2, vbo, 0)
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0])

        glBufferData(
            GL_ARRAY_BUFFER,
            vertex.bufferSize,
            vertex.getBuffer(),
            GL_DYNAMIC_DRAW
        )

        glBindBuffer(GL_SHADER_STORAGE_BUFFER, vbo[1])
        glBufferData(
            GL_SHADER_STORAGE_BUFFER,
            collisionData.bufferSize,
            collisionData.buffer,
            GL_DYNAMIC_DRAW
        )
    }


    private fun initLocations() {
        // attributes
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0])
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
        shader.screenWidth.init(program)
        shader.ratio.init(program)
        shader.camera.init(program)

        shader.floatsPerVertex.init(computeProgram)
        shader.playerPosition.init(computeProgram)
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
        glBindVertexArray(vao[0])

        compute()
        drawPlanets()

        glBindVertexArray(0)
    }

    private fun drawPlanets() {
        glUseProgram(program)
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
        glUseProgram(0)
    }

    private fun compute() {
        glBindBuffer(GL_SHADER_STORAGE_BUFFER, vbo[1])
        glBufferData(
            GL_SHADER_STORAGE_BUFFER,
            collisionData.bufferSize,
            collisionData.buffer,
            GL_DYNAMIC_DRAW
        )
         ShaderUtils.computeShader(
             shaderProgram = computeProgram,
             uniforms = {
                 glUniform1ui(shader.floatsPerVertex.location, vertex.numberOfFloatsPerVertex)
                 glUniform2f(shader.playerPosition.location, playerProperties.position.x, playerProperties.position.y)
             },
             vbos = vbo,
             x = vertex.numberOfFloatsPerVertex,
             y = vertex.numberOfPlanets,
         )

        val collisionData = OpenGLUtils.readSSBO(
            vbo[1],
            collisionData.data.size,
            Float.SIZE_BYTES
        )
        playerProperties.addForce(collisionData)
        Log.i("TAG", "compute: ${collisionData.contentToString()}")


    }

    override fun setRatio(ratio: Float) {
        super.setRatio(ratio)
        glUseProgram(program)
        glUniform1f(shader.ratio.location, ratio)
    }

    override fun onSizeChange(width: Int, height: Int) {
        glUseProgram(program)
        glUniform1f(shader.screenWidth.location, width.toFloat() / 2f)
    }

    override fun release() {
        glDeleteBuffers(1, vbo, 0)
        glDeleteVertexArrays(1, vao, 0)
        glDeleteShader(program)
    }

}
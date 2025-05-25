package ge.siradze.planets

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20.GL_ARRAY_BUFFER
import android.opengl.GLES20.GL_FRAGMENT_SHADER
import android.opengl.GLES20.GL_LINEAR
import android.opengl.GLES20.GL_LINE_STRIP
import android.opengl.GLES20.GL_POINTS
import android.opengl.GLES20.glActiveTexture
import android.opengl.GLES20.glDeleteBuffers
import android.opengl.GLES20.glDeleteShader
import android.opengl.GLES20.glDeleteTextures
import android.opengl.GLES20.glDisableVertexAttribArray
import android.opengl.GLES20.glDrawArrays
import android.opengl.GLES20.glEnableVertexAttribArray
import android.opengl.GLES20.glGenBuffers
import android.opengl.GLES20.glLineWidth
import android.opengl.GLES20.glUniform1f
import android.opengl.GLES20.glUniform1i
import android.opengl.GLES20.glUniform2f
import android.opengl.GLES20.glUseProgram
import android.opengl.GLES30.GL_TEXTURE_2D
import android.opengl.GLES30.GL_VERTEX_SHADER
import android.opengl.GLES30.glBindTexture
import android.opengl.GLES30.glBindVertexArray
import android.opengl.GLES30.glDeleteVertexArrays
import android.opengl.GLES30.glGenTextures
import android.opengl.GLES30.glGenVertexArrays
import android.opengl.GLES30.glUniform1ui
import android.opengl.GLES31.GL_COMPUTE_SHADER
import android.opengl.GLES31.GL_DYNAMIC_DRAW
import android.opengl.GLES31.GL_FLOAT
import android.opengl.GLES31.glBindBuffer
import android.opengl.GLES31.glBufferData
import ge.siradze.core.GameObject
import ge.siradze.glcore.EngineGlobals
import ge.siradze.glcore.GameState
import ge.siradze.glcore.camera.Camera
import ge.siradze.glcore.extensions.x
import ge.siradze.glcore.extensions.y
import ge.siradze.glcore.shader.Shader
import ge.siradze.glcore.texture.TextureCounter
import ge.siradze.glcore.texture.TextureDimensions
import ge.siradze.glcore.utils.OpenGLUtils
import ge.siradze.glcore.utils.TextureUtils
import ge.siradze.glcore.vboReader.VBOReader
import ge.siradze.planets.data.CollisionData
import ge.siradze.planets.data.ShaderLocations
import ge.siradze.planets.data.Vertex
import ge.siradze.planets.data.VertexProperties
import ge.siradze.glcore.utils.ShaderUtils
import ge.siradze.player.main.PlayerProperties


class Planets(
    name: String,
    state: GameState,
    numberOfPlanets: Int,
    private val context: Context,
    private val playerProperties: PlayerProperties,
    private val camera: Camera,
    private val textureCounter: TextureCounter,
    private val vboReader: VBOReader
): GameObject {

    private val textureDimensions = TextureDimensions(2, 2, R.drawable.planets3)
    private val bitmap = BitmapFactory.decodeResource(context.resources, textureDimensions.bitmapRes)
    private val textures = IntArray(1)
    private var texture: Int = 0

    private val vao: IntArray = IntArray(1)
    private val vbo: IntArray = IntArray(1)

    private val dataSerializeName = Planets::class.qualifiedName + name
    private val vertex: Vertex = state.get(dataSerializeName) as? Vertex ?:
        Vertex(
            properties = VertexProperties(
               numberOfPlanets = numberOfPlanets,
            ),
            textureDimensions = textureDimensions
        ).also {
            state.set(dataSerializeName, it)
        }

    // for evil planets to spawn around
    fun getVertexData(): FloatArray = vertex.data

    private val shader = ShaderLocations()
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

    private var program: Int = 0
    private var computeProgram: Int = 0

    private val collisionData = CollisionData()

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
        glGenVertexArrays(vao.size, vao, 0)
        glBindVertexArray(vao[0])

        glGenBuffers(vbo.size, vbo, 0)
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0])

        glBufferData(
            GL_ARRAY_BUFFER,
            vertex.bufferSize,
            vertex.getBuffer(),
            GL_DYNAMIC_DRAW
        )

        vboReader.allocate(
            key = dataSerializeName,
            numberOfFloats = collisionData.data.size
        )
    }


    private fun initLocations() {
        // attributes
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0])
        shader.init(
            program = program,
            computeProgram = computeProgram,
            stride = vertex.stride,
            type = GL_FLOAT,
        )
    }

    private fun bindTexture() {
        glGenTextures(textures.size, textures, 0)
        glUseProgram(program)

        texture = TextureUtils.loadTexture(
            bitmap,
            textures[0],
            shader.texture.location,
            textureCounter.getTextureOffset(1),
            GL_LINEAR
        )
    }

    override fun draw() {
        glBindVertexArray(vao[0])

        compute()
        drawLines()
        drawPlanets()

        glBindVertexArray(0)
    }

    private fun drawPlanets() {
        glUseProgram(program)
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0])
        glUniform1i(shader.counter.location, EngineGlobals.counter)


        shader.enableAttributeLocations()

        glActiveTexture(texture)
        glBindTexture(GL_TEXTURE_2D, textures[0])

        camera.bindUniform(shader.camera.location)

        glDrawArrays(
            GL_POINTS,
            0,
            vertex.numberOfPlanets
        )

        glActiveTexture(0)
        glBindTexture(GL_TEXTURE_2D, 0)

        shader.disableAttributeLocations()

        glUseProgram(0)
    }

    private fun drawLines() {
        glUseProgram(program)
        glLineWidth(1.0f)
        glEnableVertexAttribArray(shader.vertex.location)
        glEnableVertexAttribArray(shader.isDestroyed.location)
        glUniform1i(shader.drawLine.location, 1)
        glDrawArrays(
            GL_LINE_STRIP,
            0,
            vertex.numberOfPlanets
        )
        glUniform1i(shader.drawLine.location, 0)
        glDisableVertexAttribArray(shader.vertex.location)
        glDisableVertexAttribArray(shader.isDestroyed.location)
    }

    private fun compute() {
         // Running as many work as there is planet, and each work will be working with each planet.
        ShaderUtils.computeShader(
         shaderProgram = computeProgram,
         uniforms = {
             glUniform1ui(shader.floatsPerVertex.location, vertex.numberOfFloatsPerVertex)
             glUniform2f(shader.playerPosition.location, playerProperties.position.x, playerProperties.position.y)
             glUniform1i(shader.destructible.location, if (playerProperties.push) 1 else 0)
             glUniform1ui(shader.readerOffset.location, vboReader.getOffset(dataSerializeName))
         },
         vbos = vbo + vboReader.vbo,
         x = vertex.numberOfPlanets,
        )
        //handleCollisionData()
    }

    private fun handleCollisionData() {
        vboReader.getData(
            key = dataSerializeName,
            destArray = collisionData.data
        )
        // check if collision happened
        if(collisionData.data[0] == 1f){
            playerProperties.addForce(
                floatArrayOf(collisionData.data[1], collisionData.data[2])
            )
        }
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
        glDeleteBuffers(vbo.size, vbo, 0)
        glDeleteVertexArrays(vao.size, vao, 0)
        glDeleteTextures(textures.size, textures, 0)
        glDeleteShader(program)
    }

}
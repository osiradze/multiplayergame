package ge.siradze.enemy

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20.GL_ARRAY_BUFFER
import android.opengl.GLES20.GL_FRAGMENT_SHADER
import android.opengl.GLES20.GL_LINEAR
import android.opengl.GLES20.GL_POINTS
import android.opengl.GLES20.glActiveTexture
import android.opengl.GLES20.glDeleteBuffers
import android.opengl.GLES20.glDeleteShader
import android.opengl.GLES20.glDeleteTextures
import android.opengl.GLES20.glDrawArrays
import android.opengl.GLES20.glGenBuffers
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
import ge.siradze.enemy.data.CollisionData
import ge.siradze.enemy.data.SpawnHandler
import ge.siradze.enemy.data.ShaderLocations
import ge.siradze.enemy.data.Vertex
import ge.siradze.enemy.data.VertexProperties
import ge.siradze.explosion.helper.ExplosionHelper
import ge.siradze.glcore.EngineGlobals
import ge.siradze.glcore.GameState
import ge.siradze.glcore.camera.Camera
import ge.siradze.glcore.extensions.x
import ge.siradze.glcore.extensions.y
import ge.siradze.glcore.shader.Shader
import ge.siradze.glcore.texture.TextureCounter
import ge.siradze.glcore.texture.TextureDimensions
import ge.siradze.glcore.utils.OpenGLUtils
import ge.siradze.glcore.utils.ShaderUtils
import ge.siradze.glcore.utils.TextureUtils
import ge.siradze.glcore.vboReader.VBOReader
import ge.siradze.player.main.PlayerProperties

class Enemy(
    name: String,
    state: GameState,
    private val context: Context,
    private val spawnPosition: FloatArray,
    private val playerProperties: PlayerProperties,
    private val camera: Camera,
    private val textureCounter: TextureCounter,
    private val vboReader: VBOReader
) : GameObject {



    private val textureDimensions = TextureDimensions(4, 4, R.drawable.enemy)
    private val explosionHelper = ExplosionHelper(context, textureDimensions)

    private val vao: IntArray = IntArray(1)
    private val vbo: IntArray = IntArray(1)


    private val properties = VertexProperties(
        spawnPosition = spawnPosition,
    )
    private val dataSerializeName = Enemy::class.qualifiedName + name
    private val vertex: Vertex =
        state.get(dataSerializeName) as? Vertex ?:
        Vertex(
            properties = properties,
            textureDimensions = textureDimensions
        ).also {
            state.set(dataSerializeName, it)
        }

    private val data = SpawnHandler(properties.numberOfEnemies)

    private val shader = ShaderLocations()
    private val shaders = arrayOf(
        Shader(
            type = GL_VERTEX_SHADER,
            source = R.raw.enemy_vertex,
            name = "Asteroid Vertex"
        ),
        Shader(
            type = GL_FRAGMENT_SHADER,
            source = R.raw.enemy_fragment,
            name = "Asteroid Fragment"
        ),
        Shader(
            type = GL_COMPUTE_SHADER,
            source = R.raw.enemy_compute,
            name = "Asteroid Compute"
        )
    )

    private val bitmap = BitmapFactory.decodeResource(context.resources, textureDimensions.bitmapRes)
    private val textures = IntArray(1)
    private var texture: Int = 0

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
        glGenVertexArrays(1, vao, 0)
        glBindVertexArray(vao[0])

        glGenBuffers(vbo.size, vbo, 0)
        glBindBuffer(GL_ARRAY_BUFFER, vbo[0])

        // vertex
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
        glGenTextures(1, textures, 0)
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
        data.update()

        glBindVertexArray(vao[0])

        compute()
        drawEnemy()

        glBindVertexArray(0)
    }


    private fun compute() {
        // Running as many work as there is max number of asteroid, and each work will be working with each planet.
        ShaderUtils.computeShader(
            shaderProgram = computeProgram,
            uniforms = {
                glUniform1ui(shader.floatsPerVertex.location, vertex.numberOfFloatsPerVertex)
                glUniform2f(shader.playerPosition.location, playerProperties.position.x, playerProperties.position.y)
                glUniform1f(shader.deltaTime.location, EngineGlobals.deltaTime)
                glUniform1ui(shader.readerOffset.location, vboReader.getOffset(dataSerializeName))
            },
            vbos = vbo + vboReader.vbo,
            x = data.activeEnemyCount,
        )
    }

    private fun drawEnemy() {

        glUseProgram(program)
        shader.enableAttributeLocations()

        glActiveTexture(texture)
        glBindTexture(GL_TEXTURE_2D, textures[0])

        glUniform1i(shader.counter.location, EngineGlobals.counter)

        camera.bindUniform(shader.camera.location)

        glDrawArrays(
            GL_POINTS,
            0,
            properties.numberOfEnemies
        )

        glActiveTexture(0)
        glBindTexture(GL_TEXTURE_2D, 0)

        shader.disableAttributeLocations()

        glUseProgram(0)
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
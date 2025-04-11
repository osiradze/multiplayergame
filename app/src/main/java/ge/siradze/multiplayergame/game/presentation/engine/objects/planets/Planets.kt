package ge.siradze.multiplayergame.game.presentation.engine.objects.planets

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20.GL_ARRAY_BUFFER
import android.opengl.GLES20.GL_FRAGMENT_SHADER
import android.opengl.GLES20.GL_POINTS
import android.opengl.GLES20.glDeleteBuffers
import android.opengl.GLES20.glDeleteShader
import android.opengl.GLES20.glDeleteTextures
import android.opengl.GLES20.glDisableVertexAttribArray
import android.opengl.GLES20.glDrawArrays
import android.opengl.GLES20.glEnableVertexAttribArray
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
import android.opengl.GLES31.GL_SHADER_STORAGE_BUFFER
import android.opengl.GLES31.glBindBuffer
import android.opengl.GLES31.glBufferData
import ge.siradze.multiplayergame.R
import ge.siradze.multiplayergame.game.presentation.GameState
import ge.siradze.multiplayergame.game.presentation.engine.GameRender
import ge.siradze.multiplayergame.game.presentation.engine.camera.Camera
import ge.siradze.multiplayergame.game.presentation.engine.extensions.x
import ge.siradze.multiplayergame.game.presentation.engine.extensions.y
import ge.siradze.multiplayergame.game.presentation.engine.objects.GameObject
import ge.siradze.multiplayergame.game.presentation.engine.objects.player.PlayerData
import ge.siradze.multiplayergame.game.presentation.engine.shader.Shader
import ge.siradze.multiplayergame.game.presentation.engine.texture.TextureCounter
import ge.siradze.multiplayergame.game.presentation.engine.texture.TextureDimensions
import ge.siradze.multiplayergame.game.presentation.engine.utils.OpenGLUtils
import ge.siradze.multiplayergame.game.presentation.engine.utils.ShaderUtils
import ge.siradze.multiplayergame.game.presentation.engine.utils.TextureUtils


class Planets(
    state: GameState,
    private val numberOfPlanets: Int,
    private val context: Context,
    private val playerProperties: PlayerData.Properties,
    private val camera: Camera,
    private val textureCounter: TextureCounter,
    private val textureDimensions: TextureDimensions,
    private val event: (GameRender.InGameEvents.CreateExplosion) -> Unit
): GameObject {

    private val vao: IntArray = IntArray(1)
    private val vbo: IntArray = IntArray(2)

    private val vertex: PlanetsData.Vertex =
        state.get(PlanetsData.Vertex::class.qualifiedName) as? PlanetsData.Vertex
            ?: PlanetsData.Vertex(numberOfPlanets, textureDimensions = textureDimensions).also { state.set(PlanetsData.Vertex::class.qualifiedName, it) }
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

        shader.collision.apply {
            init(program)
            load(1, GL_FLOAT, false, vertex.stride, 10 * Float.SIZE_BYTES)
        }
        shader.isDestroyed.apply {
            init(program)
            load(1, GL_FLOAT, false, vertex.stride, 11 * Float.SIZE_BYTES)
        }

        // Uniforms
        shader.screenWidth.init(program)
        shader.ratio.init(program)
        shader.camera.init(program)

        shader.floatsPerVertex.init(computeProgram)
        shader.playerPosition.init(computeProgram)
        shader.push.init(computeProgram)
    }

    private fun bindTexture() {
        glGenTextures(1, textures, 0)

        TextureUtils.loadTexture(
            bitmap,
            textures[0],
            shader.texture.location,
            textureCounter.getTextureOffset(1)
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
        glEnableVertexAttribArray(shader.collision.location)
        glEnableVertexAttribArray(shader.isDestroyed.location)
        glBindTexture(GL_TEXTURE_2D, textures[0])


        camera.bindUniform(shader.camera.location)

        glDrawArrays(
            GL_POINTS,
            0,
            vertex.numberOfPlanets
        )

        glBindTexture(GL_TEXTURE_2D, 0)
        glDisableVertexAttribArray(shader.vertex.location)
        glDisableVertexAttribArray(shader.textureCoordinates.location)
        glDisableVertexAttribArray(shader.size.location)
        glDisableVertexAttribArray(shader.color.location)
        glDisableVertexAttribArray(shader.collision.location)
        glDisableVertexAttribArray(shader.isDestroyed.location)
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
         // Running as many work as there is planet, and each work will be working with each planet.
         ShaderUtils.computeShader(
             shaderProgram = computeProgram,
             uniforms = {
                 glUniform1ui(shader.floatsPerVertex.location, vertex.numberOfFloatsPerVertex)
                 glUniform2f(shader.playerPosition.location, playerProperties.position.x, playerProperties.position.y)
                 glUniform1i(shader.push.location, if (playerProperties.push) 1 else 0)
             },
             vbos = vbo,
             x = vertex.numberOfPlanets,
         )

        val collisionData = OpenGLUtils.readSSBO(
            vbo[1],
            collisionData.data.size,
            Float.SIZE_BYTES
        )
        if(collisionData[0] == 1f){
            if(playerProperties.push){
                event(
                    GameRender.InGameEvents.CreateExplosion(
                        position = floatArrayOf(collisionData[1], collisionData[2]),
                        size = collisionData[3],
                        planet = floatArrayOf(collisionData[4],  collisionData[5]),
                        color = floatArrayOf(collisionData[6], collisionData[7], collisionData[8])
                    )
                )
            }  else {
                playerProperties.addForce(
                    floatArrayOf(collisionData[1], collisionData[2])
                )
            }

            //Log.i("TAG", "compute: $collisionData")
            //Log.i("TAG", "compute: ${System.currentTimeMillis() - old}")
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
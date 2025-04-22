package ge.siradze.multiplayergame.game.presentation.engine

import android.content.Context
import android.opengl.GLES20.GL_COLOR_BUFFER_BIT
import android.opengl.GLES20.glClear
import android.opengl.GLES20.glClearColor
import android.opengl.GLSurfaceView
import ge.siradze.multiplayergame.R
import ge.siradze.multiplayergame.game.presentation.GameState
import ge.siradze.multiplayergame.game.presentation.engine.camera.Camera
import ge.siradze.multiplayergame.game.presentation.engine.objects.GameObject
import ge.siradze.multiplayergame.game.presentation.engine.objects.asteroids.Asteroids
import ge.siradze.multiplayergame.game.presentation.engine.objects.planets.Planets
import ge.siradze.multiplayergame.game.presentation.engine.objects.explosion.Explosion
import ge.siradze.multiplayergame.game.presentation.engine.objects.explosion.ExplosionHelper
import ge.siradze.multiplayergame.game.presentation.engine.objects.player.PlayerObject
import ge.siradze.multiplayergame.game.presentation.engine.objects.player.trail.PlayerTrail
import ge.siradze.multiplayergame.game.presentation.engine.objects.stars.Stars
import ge.siradze.multiplayergame.game.presentation.engine.texture.TextureCounter
import ge.siradze.multiplayergame.game.presentation.gameUi.UIEvents
import ge.siradze.multiplayergame.game.presentation.feedback.FeedbackSounds
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GameRender(
    val context: Context,
    val state: GameState,
    val feedbackSounds: FeedbackSounds,
    val uiEffect: (UIEffect) -> Unit,
) : GLSurfaceView.Renderer {

    private var ratio = 1f
    private var lastFrameTime: Long = System.nanoTime()

    private val textureCounter: TextureCounter = TextureCounter()

    val camera: Camera = Camera(
        state
    )

    // create player and set camera to follow it
    val player = PlayerObject(state, context, camera, textureCounter).also {
        camera.followPlayer(it.properties)
    }
    private val playerTrail = PlayerTrail(
        context  = context,
        player.properties,
        camera
    )

    private val planets = Planets(
        name = "Planets",
        state = state,
        numberOfPlanets = NUMBER_OF_PLANETS,
        context = context,
        playerProperties = player.properties,
        camera = camera,
        textureCounter = textureCounter,
        event = explosionCreation
    )

    private val asteroids = Asteroids(
        name = "Asteroids",
        state = state,
        context = context,
        playerProperties = player.properties,
        camera = camera,
        textureCounter = textureCounter,
        event = explosionCreation
    )

    private val stars = Stars(context, camera)

    private val objects: MutableList<GameObject> = mutableListOf(
        stars,
        asteroids,
        planets,
        player,
        playerTrail,
    )
    
    val temporaryObjects: MutableList<Explosion> = mutableListOf()

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        lastFrameTime = System.nanoTime()
        objects.forEach {
            it.init()
        }
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        ratio = width.toFloat() / height.toFloat()
        objects.forEach {
            it.setRatio(ratio)
            it.onSizeChange(width, height)
        }
    }

    fun onUIEvent(event: UIEvents) {
        player.onUIEvent(event)
    }

    override fun onDrawFrame(p0: GL10?) {
        // clear screen
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        glClear(GL_COLOR_BUFFER_BIT)

        val currentTime = System.nanoTime()
        EngineGlobals.deltaTime = (currentTime - lastFrameTime) / 1_000_000_000f
        lastFrameTime = currentTime


        createObject()

        camera.update()
        objects.forEach {
           it.draw()
        }
        EngineGlobals.fps++
    }

    private fun createObject() {
        if(temporaryObjects.isNotEmpty()) {
            val tempObject = temporaryObjects.first()
            tempObject.init()
            tempObject.setRatio(ratio)
            objects.add(1, tempObject)
            temporaryObjects.removeAt(0)
            if(objects.size > MAX_EXPLOSION) {
                objects.findLast { it is Explosion }?.let {
                    objects.remove(it)
                    it.release()
                }
            }

        }
    }

    fun release() {
        objects.forEach {
            it.release()
        }
    }


    sealed class InGameEvents {
        class CreateExplosion(
            val position: FloatArray,
            val size: Float,
            val planet: FloatArray,
            val color: FloatArray,
            val explosionHelper: ExplosionHelper
        ) : InGameEvents()
    }

    sealed class UIEffect {
        data object PointUp : UIEffect()
    }

    companion object {
        const val MAX_EXPLOSION = 70
        const val NUMBER_OF_PLANETS = 50
    }
}
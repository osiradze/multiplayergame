package ge.siradze.multiplayergame.game.presentation.engine

import android.content.Context
import android.opengl.GLES20.GL_COLOR_BUFFER_BIT
import android.opengl.GLES20.glClear
import android.opengl.GLES20.glClearColor
import android.opengl.GLSurfaceView
import ge.siradze.multiplayergame.game.presentation.GameState
import ge.siradze.multiplayergame.game.presentation.engine.camera.Camera
import ge.siradze.multiplayergame.game.presentation.engine.objects.GameObject
import ge.siradze.multiplayergame.game.presentation.engine.objects.explosion.Explosion
import ge.siradze.multiplayergame.game.presentation.engine.objects.explosion.ExplosionHelper
import ge.siradze.multiplayergame.game.presentation.engine.objects.player.Player
import ge.siradze.multiplayergame.game.presentation.engine.scene.ExplosionCreation
import ge.siradze.multiplayergame.game.presentation.engine.scene.SceneObjects
import ge.siradze.multiplayergame.game.presentation.engine.texture.TextureCounter
import ge.siradze.multiplayergame.game.presentation.engine.vboReader.VBOReaderImpl
import ge.siradze.multiplayergame.game.presentation.feedback.FeedbackSounds
import ge.siradze.multiplayergame.game.presentation.gameUi.UIEvents
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class GameRender(
    val context: Context,
    val state: GameState,
    val feedbackSounds: FeedbackSounds,
    val uiEffect: (UIEffect) -> Unit,
) : GLSurfaceView.Renderer {

    private var ratio = 1f

    val camera: Camera = Camera(state)
    private val textureCounter: TextureCounter = TextureCounter()
    private val vboReader: VBOReaderImpl = VBOReaderImpl()

    val player = Player(state, context, camera, textureCounter).also {
        camera.followPlayer(it.properties)
    }

    private val temporaryObjects: MutableList<Explosion> = mutableListOf()
    private val explosionCreation = ExplosionCreation(
        context = context,
        camera = camera,
        player = player,
        temporaryObjects = temporaryObjects,
        feedbackSounds = feedbackSounds,
        uiEffect = uiEffect
    )

    private val sceneObjects = SceneObjects(
        state = state,
        context = context,
        camera = camera,
        textureCounter = textureCounter,
        vboReader = vboReader,
        player = player,
        explosionCreation = explosionCreation,
    )


    private val objects: MutableList<GameObject> = mutableListOf(
        sceneObjects.stars,
        sceneObjects.asteroids,
        sceneObjects.evilPlanets,
        sceneObjects.planets,
        player,
        sceneObjects.playerTrail,
    )

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        EngineGlobals.update()
        objects.forEach {
            it.init()
        }
        vboReader.init()
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

        createObject()

        camera.update()
        objects.forEach {
           it.draw()
        }

        vboReader.read()
        vboReader.clean()

        EngineGlobals.update()
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
        const val MAX_EXPLOSION = 50
        const val NUMBER_OF_PLANETS = 30
    }
}
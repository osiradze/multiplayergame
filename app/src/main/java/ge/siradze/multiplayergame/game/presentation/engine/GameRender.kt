package ge.siradze.multiplayergame.game.presentation.engine

import android.content.Context
import android.opengl.GLES20.GL_COLOR_BUFFER_BIT
import android.opengl.GLES20.glClear
import android.opengl.GLES20.glClearColor
import android.opengl.GLSurfaceView
import ge.siradze.glcore.EngineGlobals
import ge.siradze.core.GameObject
import ge.siradze.glcore.GameState
import ge.siradze.glcore.camera.Camera
import ge.siradze.multiplayergame.game.presentation.engine.scene.helpers.ExplosionCreationHelper
import ge.siradze.multiplayergame.game.presentation.engine.scene.SceneObjects
import ge.siradze.glcore.texture.TextureCounter
import ge.siradze.glcore.vboReader.VBOReaderImpl
import ge.siradze.explosion.Explosion
import ge.siradze.multiplayergame.game.presentation.engine.scene.helpers.EnemySpawnerHelper
import ge.siradze.multiplayergame.game.presentation.feedback.FeedbackSounds
import ge.siradze.multiplayergame.game.presentation.gameUi.UIEvents
import ge.siradze.player.main.Player
import ge.siradze.player.PlayerEvents
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


class GameRender(
    val context: Context,
    val state: GameState,
    val feedbackSounds: FeedbackSounds,
    val uiEffect: (UIEffect) -> Unit,
) : GLSurfaceView.Renderer {

    private var ratio = 1f

    private var screenWidth = 0
    private var screenHeight = 0

    private val camera: Camera = Camera(state)
    private val textureCounter: TextureCounter = TextureCounter()
    private val vboReader: VBOReaderImpl = VBOReaderImpl()

    private val player = Player(state, context, camera, textureCounter).also {
        camera.followPlayer(it.properties.position)
    }

    private val temporaryObjects: MutableList<GameObject> = mutableListOf()

    private val explosionCreationHelper = ExplosionCreationHelper(
        context = context,
        camera = camera,
        player = player,
        temporaryObjects = temporaryObjects,
        feedbackSounds = feedbackSounds,
        uiEffect = uiEffect
    )

    private val enemySpawnerHelper = EnemySpawnerHelper(
        context = context,
        camera = camera,
        player = player,
        temporaryObjects = temporaryObjects,
        state = state,
        textureCounter = textureCounter,
        vboReader = vboReader,
    )

    private val sceneObjects = SceneObjects(
        state = state,
        context = context,
        camera = camera,
        textureCounter = textureCounter,
        vboReader = vboReader,
        player = player,
        explosionCreationHelper = explosionCreationHelper,
        enemySpawnerHelper = enemySpawnerHelper
    )


    private val objects: MutableList<GameObject> = mutableListOf(
        sceneObjects.stars,
        sceneObjects.asteroids,
        sceneObjects.planets,
        sceneObjects.evilPlanets,
        player,
        sceneObjects.playerTrail,
    )

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        EngineGlobals.init()
        objects.forEach {
            it.init()
        }
        vboReader.init()
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        this.screenWidth = width
        this.screenHeight = height
        ratio = width.toFloat() / height.toFloat()
        objects.forEach {
            it.setRatio(ratio)
            it.onSizeChange(width, height)
        }
    }

    fun onUIEvent(event: UIEvents) {
        when(event) {
            UIEvents.OnDown -> player.onUIEvent(PlayerEvents.Accelerate)
            UIEvents.OnUp -> player.onUIEvent(PlayerEvents.Decelerate)
            is UIEvents.OnMove -> player.onUIEvent(PlayerEvents.Rotate(event.move))
            is UIEvents.Switch -> Unit
        }
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
            tempObject.onSizeChange(screenWidth, screenHeight)
            if(tempObject is Explosion) {
                addExplosion(tempObject)
            }else {
                addEnemy(tempObject)
            }

        }
    }

    private fun addExplosion(explosion: Explosion) {
        objects.add(explosion)
        temporaryObjects.removeAt(temporaryObjects.size - 1 )
        if(objects.size > MAX_OBJECTS) {
            objects.find { it is Explosion }?.let {
                objects.remove(it)
                it.release()
            }
        }
    }

    private fun addEnemy(enemy: GameObject) {
        objects.add(enemy)
        temporaryObjects.removeAt(temporaryObjects.size - 1 )
    }

    fun release() {
        objects.forEach {
            it.release()
        }
    }

    sealed class UIEffect {
        data object PointUp : UIEffect()
    }

    companion object {
        const val MAX_OBJECTS = 50
        const val NUMBER_OF_PLANETS = 30
    }
}
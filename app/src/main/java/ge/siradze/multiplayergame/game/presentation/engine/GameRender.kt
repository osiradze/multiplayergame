package ge.siradze.multiplayergame.game.presentation.engine

import android.content.Context
import android.opengl.GLES20.GL_COLOR_BUFFER_BIT
import android.opengl.GLES20.glClear
import android.opengl.GLES20.glClearColor
import android.opengl.GLSurfaceView
import ge.siradze.multiplayergame.game.presentation.GameState
import ge.siradze.multiplayergame.game.presentation.engine.camera.Camera
import ge.siradze.multiplayergame.game.presentation.gameUi.UIEvents
import ge.siradze.multiplayergame.game.presentation.engine.objects.GameObject
import ge.siradze.multiplayergame.game.presentation.engine.objects.planets.Planets
import ge.siradze.multiplayergame.game.presentation.engine.objects.player.PlayerObject
import ge.siradze.multiplayergame.game.presentation.engine.objects.player.PlayerTrail
import ge.siradze.multiplayergame.game.presentation.engine.objects.stars.Stars
import ge.siradze.multiplayergame.game.presentation.engine.texture.TextureCounter
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GameRender(
    context: Context,
    state: GameState
) : GLSurfaceView.Renderer {

    var fps = 0

    private val textureCounter: TextureCounter = TextureCounter()

    private val camera: Camera = Camera(
        state
    )

    // create player and set camera to follow it
    private val player = PlayerObject(state, context, camera).also {
        camera.followPlayer(it.properties)
    }
    private val playerTrail = PlayerTrail(
        context  = context,
        player.properties,
        camera
    )
    private val planets = Planets(
        state = state,
        context = context,
        playerProperties = player.properties,
        camera = camera,
        textureCounter = textureCounter
    )

    private val stars = Stars(context, camera)

    private val objects: MutableList<GameObject> = mutableListOf(
        player,
        playerTrail,
        stars,
        planets
    )

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        objects.forEach {
            it.init()
        }
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        val ratio = width.toFloat() / height.toFloat()
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

        camera.update()
        objects.forEach {
           it.draw()
        }
        fps++
    }

    fun release() {
        objects.forEach {
            it.release()
        }
    }
}
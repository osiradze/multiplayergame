package ge.siradze.multiplayergame.game.presentation.engine

import android.content.Context
import android.opengl.GLSurfaceView
import ge.siradze.multiplayergame.game.presentation.engine.camera.Camera
import ge.siradze.multiplayergame.game.presentation.gameUi.UIEvents
import ge.siradze.multiplayergame.game.presentation.engine.objects.GameObject
import ge.siradze.multiplayergame.game.presentation.engine.objects.player.PlayerObject
import ge.siradze.multiplayergame.game.presentation.engine.objects.player.PlayerTrail
import ge.siradze.multiplayergame.game.presentation.engine.objects.wind.Wind
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GameRender(context: Context) : GLSurfaceView.Renderer {

    // create player and set camera to follow it
    private val player = PlayerObject(context).also {
        Camera.followPlayer(it.properties)
    }
    private val playerTrail = PlayerTrail(
        context  = context,
        player.properties
    )

    private val objects: MutableList<GameObject> = mutableListOf(
        player, playerTrail,
        Wind(context)
    )

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        objects.forEach {
            it.init()
        }
    }

    override fun onSurfaceChanged(p0: GL10?, width: Int, height: Int) {
        objects.forEach {
            it.setRatio(width.toFloat() / height.toFloat())
        }
    }

    fun onUIEvent(event: UIEvents) {
        player.onUIEvent(event)
    }

    override fun onDrawFrame(p0: GL10?) {
        Camera.update()
        objects.forEach {
           it.draw()
        }
    }

    fun release() {
        objects.forEach {
            it.release()
        }
    }
}
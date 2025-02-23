package ge.siradze.multiplayergame.game.presentation.engine

import android.content.Context
import android.opengl.GLSurfaceView
import ge.siradze.multiplayergame.game.presentation.engine.objects.GameObject
import ge.siradze.multiplayergame.game.presentation.engine.objects.player.PlayerObject
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class GameRender(context: Context) : GLSurfaceView.Renderer {

    private val objects: MutableList<GameObject> = mutableListOf(
        PlayerObject(context)
    )

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        objects.forEach {
            it.init()
        }
    }

    override fun onSurfaceChanged(p0: GL10?, p1: Int, p2: Int) {

    }

    override fun onDrawFrame(p0: GL10?) {
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
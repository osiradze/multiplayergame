package ge.siradze.multiplayergame.game.presentation.engine.scene

import android.content.Context
import ge.siradze.core.GameObject
import ge.siradze.enemy.Enemy
import ge.siradze.enemy.event.EnemySpawn
import ge.siradze.explosion.Explosion
import ge.siradze.explosion.event.CreateExplosion
import ge.siradze.glcore.GameState
import ge.siradze.glcore.camera.Camera
import ge.siradze.glcore.texture.TextureCounter
import ge.siradze.glcore.vboReader.VBOReader
import ge.siradze.player.main.Player

class EnemySpawning(
    private val context: Context,
    private val camera: Camera,
    private val player: Player,
    private val temporaryObjects: MutableList<GameObject>,
    private val state: GameState,
    private val textureCounter: TextureCounter,
    private val vboReader: VBOReader,
) : (EnemySpawn) -> Unit {

    override fun invoke(event: EnemySpawn) {
        Thread {
            temporaryObjects.add(
                Enemy(
                    name = "Enemy",
                    state = state,
                    context = context,
                    spawnPosition = event.position,
                    playerProperties = player.properties,
                    camera = camera,
                    textureCounter = textureCounter,
                    vboReader = vboReader,
                )
            )
        }.start()
    }
}
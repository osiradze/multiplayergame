package ge.siradze.multiplayergame.game.presentation.engine.scene.helpers

import android.content.Context
import ge.siradze.core.GameObject
import ge.siradze.enemy.Enemy
import ge.siradze.enemy.event.EnemySpawnEvent
import ge.siradze.glcore.GameState
import ge.siradze.glcore.camera.Camera
import ge.siradze.glcore.texture.TextureCounter
import ge.siradze.glcore.vboReader.VBOReader
import ge.siradze.player.main.Player
import kotlin.random.Random

class EnemySpawnerHelper(
    private val context: Context,
    private val camera: Camera,
    private val player: Player,
    private val temporaryObjects: MutableList<GameObject>,
    private val state: GameState,
    private val textureCounter: TextureCounter,
    private val vboReader: VBOReader,
) : (EnemySpawnEvent) -> Unit {

    override fun invoke(event: EnemySpawnEvent) {
        Thread {
            temporaryObjects.add(
                Enemy(
                    name = "Enemy" + Random.nextInt(),
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
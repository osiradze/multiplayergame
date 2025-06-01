package ge.siradze.multiplayergame.game.presentation.engine.scene.helpers

import android.content.Context
import ge.siradze.core.GameObject
import ge.siradze.multiplayergame.game.presentation.engine.GameRender.UIEffect
import ge.siradze.glcore.camera.Camera
import ge.siradze.explosion.Explosion
import ge.siradze.explosion.event.ExplotionCreationEvent
import ge.siradze.multiplayergame.game.presentation.feedback.FeedbackSounds
import ge.siradze.player.main.Player

class ExplosionCreationHelper(
    private val context: Context,
    private val camera: Camera,
    private val player: Player,
    private val temporaryObjects: MutableList<GameObject>,
    private val feedbackSounds: FeedbackSounds,
    private val uiEffect: (UIEffect) -> Unit,
): (ExplotionCreationEvent) -> Unit {
    override fun invoke(event: ExplotionCreationEvent) {
        Thread {
            temporaryObjects.add(
                Explosion(
                    context = context,
                    camera = camera,
                    helper = event.explosionHelper,
                    playerPosition = player.properties.position,
                    planet = event.planet,
                    size = event.size,
                    position = event.position,
                    color = event.color
                )
            )
        }.start()
        feedbackSounds.vibrate(10)
        uiEffect(UIEffect.PointUp)
    }
}
package ge.siradze.multiplayergame.game.presentation.engine.scene

import android.content.Context
import ge.siradze.multiplayergame.game.presentation.engine.GameRender.InGameEvents
import ge.siradze.multiplayergame.game.presentation.engine.GameRender.UIEffect
import ge.siradze.core.camera.Camera
import ge.siradze.multiplayergame.game.presentation.engine.objects.explosion.Explosion
import ge.siradze.multiplayergame.game.presentation.feedback.FeedbackSounds
import ge.siradze.player.Player

class ExplosionCreation(
    private val context: Context,
    private val camera: Camera,
    private val player: Player,
    private val temporaryObjects: MutableList<Explosion>,
    private val feedbackSounds: FeedbackSounds,
    private val uiEffect: (UIEffect) -> Unit,
): (InGameEvents.CreateExplosion) -> Unit {
    override fun invoke(event: InGameEvents.CreateExplosion) {
        Thread {
            temporaryObjects.add(
                Explosion(
                    context = context,
                    camera = camera,
                    helper = event.explosionHelper,
                    playerProperties = player.properties,
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
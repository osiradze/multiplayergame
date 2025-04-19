package ge.siradze.multiplayergame.game.presentation.engine

import ge.siradze.multiplayergame.game.presentation.engine.GameRender.UIEffect
import ge.siradze.multiplayergame.game.presentation.engine.objects.planets.explosion.PlanetExplosion

val GameRender.explosionCreation get() =  { event: GameRender.InGameEvents.CreateExplosion ->
    // We need to create Planet explosion in another thread,
    // because it take a lot of time to process vertex data
    Thread {
        temporaryObjects.add(
            PlanetExplosion(
                context = context,
                camera = camera,
                helper = planetExplosionHelper,
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
package ge.siradze.multiplayergame.game.presentation.engine.scene

import android.content.Context
import ge.siradze.core.GameState
import ge.siradze.multiplayergame.game.presentation.engine.GameRender
import ge.siradze.multiplayergame.game.presentation.engine.GameRender.Companion.NUMBER_OF_PLANETS
import ge.siradze.core.camera.Camera
import ge.siradze.multiplayergame.game.presentation.engine.objects.asteroids.Asteroids
import ge.siradze.multiplayergame.game.presentation.engine.objects.evilPlanets.EvilPlanets
import ge.siradze.planets.Planets
import ge.siradze.multiplayergame.game.presentation.engine.objects.stars.Stars
import ge.siradze.core.texture.TextureCounter
import ge.siradze.core.vboReader.VBOReaderImpl
import ge.siradze.player.Player
import ge.siradze.player.trail.PlayerTrail

class SceneObjects(
    state: GameState,
    context: Context,
    camera: Camera,
    textureCounter: TextureCounter,
    vboReader: VBOReaderImpl,
    player: Player,
    explosionCreation: (GameRender.InGameEvents.CreateExplosion) -> Unit,
) {

    val playerTrail = PlayerTrail(
        context = context,
        player.properties,
        camera
    )

    val planets = Planets(
        name = "Planets",
        state = state,
        numberOfPlanets = NUMBER_OF_PLANETS,
        context = context,
        playerProperties = player.properties,
        camera = camera,
        textureCounter = textureCounter,
        vboReader = vboReader,
    )

    val evilPlanets = EvilPlanets(
        name = "EvilPlanets",
        state = state,
        context = context,
        playerProperties = player.properties,
        camera = camera,
        textureCounter = textureCounter,
        planetsData = planets.getVertexData(),
        vboReader = vboReader,
    )

    val asteroids = Asteroids(
        name = "Asteroids",
        state = state,
        context = context,
        playerProperties = player.properties,
        camera = camera,
        textureCounter = textureCounter,
        event = explosionCreation,
        vboReader = vboReader,
    )

    val stars = Stars(context, camera)
}
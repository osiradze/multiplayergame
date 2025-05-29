package ge.siradze.multiplayergame.game.presentation.engine.scene

import android.content.Context
import ge.siradze.glcore.GameState
import ge.siradze.multiplayergame.game.presentation.engine.GameRender.Companion.NUMBER_OF_PLANETS
import ge.siradze.glcore.camera.Camera
import ge.siradze.asteroids.Asteroids
import ge.siradze.enemy.Enemy
import ge.siradze.enemy.event.EnemySpawn
import ge.siradze.planets.Planets
import ge.siradze.glcore.texture.TextureCounter
import ge.siradze.glcore.vboReader.VBOReaderImpl
import ge.siradze.evilplanets.EvilPlanets
import ge.siradze.explosion.event.CreateExplosion
import ge.siradze.player.main.Player
import ge.siradze.player.trail.PlayerTrail
import ge.siradze.stars.Stars

class SceneObjects(
    state: GameState,
    context: Context,
    camera: Camera,
    textureCounter: TextureCounter,
    vboReader: VBOReaderImpl,
    player: Player,
    explosionCreation: (CreateExplosion) -> Unit,
    enemySpawn: (EnemySpawn) -> Unit
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
        event = explosionCreation,
        enemySpawn = enemySpawn,
        vboReader = vboReader,
    )

    val enemy = Enemy(
        name = "Enemy",
        state = state,
        context = context,
        spawnPosition = floatArrayOf(0f, 0f, 0f),
        playerProperties = player.properties,
        camera = camera,
        textureCounter = textureCounter,
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
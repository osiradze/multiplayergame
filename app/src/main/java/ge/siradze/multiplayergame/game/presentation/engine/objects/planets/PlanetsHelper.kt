package ge.siradze.multiplayergame.game.presentation.engine.objects.planets

import ge.siradze.multiplayergame.game.presentation.engine.texture.TextureDimensions
import kotlin.random.Random

fun PlanetsData.Vertex.generatePoints(
    data: FloatArray, numberOfPlanets: Int,
    numberOfFloatsPerVertex: Int,
    textureDimensions: TextureDimensions
) {
    for (i in 0 until numberOfPlanets) {
        //position
        data[i * numberOfFloatsPerVertex + 0] = (Random.nextFloat() - 0.5f) * 40
        data[i * numberOfFloatsPerVertex + 1] = (Random.nextFloat() - 0.5f) * 40

        //size
        data[i * numberOfFloatsPerVertex + 2] = Random.nextFloat() * 0.5f + 0.2f

        // texture coordinates
        val randomX = Random.nextInt(until = 5) + 1
        val randomY = Random.nextInt(until = 4) + 1

        data[i * numberOfFloatsPerVertex + 3] = textureDimensions.stepX * (randomX - 1)
        data[i * numberOfFloatsPerVertex + 4] = textureDimensions.stepY * (randomY - 1)
        data[i * numberOfFloatsPerVertex + 5] = textureDimensions.stepX
        data[i * numberOfFloatsPerVertex + 6] = textureDimensions.stepY

        // color
        data[i * numberOfFloatsPerVertex + 7] = Random.nextFloat() * 0.5f + 0.5f
        data[i * numberOfFloatsPerVertex + 8] = Random.nextFloat() * 0.5f + 0.5f
        data[i * numberOfFloatsPerVertex + 9] = Random.nextFloat() * 0.5f + 0.5f
    }
}
package ge.siradze.planets.data

import ge.siradze.core.AttributeData
import ge.siradze.glcore.extensions.toBuffer
import ge.siradze.glcore.extensions.x
import ge.siradze.glcore.extensions.y
import ge.siradze.glcore.texture.TextureDimensions
import ge.siradze.planets.PlanetsData.NUMBER_OF_FLOATS_PER_VERTEX
import ge.siradze.planets.data.Vertex.Companion.CB
import ge.siradze.planets.data.Vertex.Companion.CG
import ge.siradze.planets.data.Vertex.Companion.CR
import ge.siradze.planets.data.Vertex.Companion.PX
import ge.siradze.planets.data.Vertex.Companion.PY
import ge.siradze.planets.data.Vertex.Companion.SIZE
import ge.siradze.planets.data.Vertex.Companion.TH
import ge.siradze.planets.data.Vertex.Companion.TW
import ge.siradze.planets.data.Vertex.Companion.TX
import ge.siradze.planets.data.Vertex.Companion.TY
import java.nio.Buffer
import kotlin.random.Random

internal class VertexProperties(
    val numberOfPlanets: Int,
    val minSize: Float = MIN_SIZE,
    val sizeRange: Float = SIZE_RANGE,
    val distanceBetweenPlanets: Float = DISTANCE_BETWEEN_PLANETS
) {
    companion object {
        const val MIN_SIZE = 1.0f
        const val SIZE_RANGE = 0.7f
        const val DISTANCE_BETWEEN_PLANETS = 5.0f
    }
}


internal class Vertex(
    val properties: VertexProperties,
    textureDimensions: TextureDimensions
): AttributeData() {

    companion object {
        const val PX = 0
        const val PY = 1
        const val SIZE = 2
        const val TX = 3
        const val TY = 4
        const val TW = 5
        const val TH = 6

        const val CR = 7
        const val CB = 8
        const val CG = 9
    }

    override val numberOfFloatsPerVertex = NUMBER_OF_FLOATS_PER_VERTEX
    override val typeSize = Float.SIZE_BYTES
    override val size = properties.numberOfPlanets * numberOfFloatsPerVertex
    val data: FloatArray = FloatArray(size)
    val numberOfPlanets = properties.numberOfPlanets

    override fun getBuffer(): Buffer = data.toBuffer()

    init {
        generatePoints(
            data,
            numberOfFloatsPerVertex,
            properties,
            textureDimensions
        )
    }

}


private fun generatePoints(
    data: FloatArray,
    numberOfFloatsPerVertex: Int,
    properties: VertexProperties,
    textureDimensions: TextureDimensions
) = with(properties) {
    val lastPlanetPosition = floatArrayOf(0f,0f)
    for (i in 0 until numberOfPlanets) {

        //position
        data[i * numberOfFloatsPerVertex + PX] = lastPlanetPosition.x + (Random.nextFloat()) * distanceBetweenPlanets + distanceBetweenPlanets / 2
        data[i * numberOfFloatsPerVertex + PY] = lastPlanetPosition.y + (Random.nextFloat()) * distanceBetweenPlanets + distanceBetweenPlanets / 2
        lastPlanetPosition[PX] = data[i * numberOfFloatsPerVertex + PX]
        lastPlanetPosition[PY] = data[i * numberOfFloatsPerVertex + PY]

        //size
        data[i * numberOfFloatsPerVertex + SIZE] = Random.nextFloat() * sizeRange + minSize

        // texture coordinates
        val randomX = Random.nextInt(until = textureDimensions.columns) + 1
        val randomY = Random.nextInt(until = textureDimensions.rows) + 1

        data[i * numberOfFloatsPerVertex + TX] = textureDimensions.stepX * (randomX - 1)
        data[i * numberOfFloatsPerVertex + TY] = textureDimensions.stepY * (randomY - 1)
        data[i * numberOfFloatsPerVertex + TW] = textureDimensions.stepX
        data[i * numberOfFloatsPerVertex + TH] = textureDimensions.stepY

        // color
        val min = 0.5f
        data[i * numberOfFloatsPerVertex + CR] = Random.nextFloat() + min
        data[i * numberOfFloatsPerVertex + CB] = Random.nextFloat() + min
        data[i * numberOfFloatsPerVertex + CG] = Random.nextFloat() + min
    }
}
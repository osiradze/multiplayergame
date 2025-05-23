package ge.siradze.evilplanets.data

import ge.siradze.core.AttributeData
import ge.siradze.core.extensions.multiply
import ge.siradze.core.extensions.normalize
import ge.siradze.core.extensions.rotate
import ge.siradze.core.extensions.toBuffer
import ge.siradze.core.extensions.x
import ge.siradze.core.extensions.y
import ge.siradze.core.texture.TextureDimensions
import ge.siradze.evilplanets.data.Vertex.Companion.CB
import ge.siradze.evilplanets.data.Vertex.Companion.CG
import ge.siradze.evilplanets.data.Vertex.Companion.CR
import ge.siradze.evilplanets.data.Vertex.Companion.PX
import ge.siradze.evilplanets.data.Vertex.Companion.PY
import ge.siradze.evilplanets.data.Vertex.Companion.SIZE
import ge.siradze.evilplanets.data.Vertex.Companion.SPAWN_RADIUS
import ge.siradze.evilplanets.data.Vertex.Companion.TH
import ge.siradze.evilplanets.data.Vertex.Companion.TW
import ge.siradze.evilplanets.data.Vertex.Companion.TX
import ge.siradze.evilplanets.data.Vertex.Companion.TY
import ge.siradze.planets.PlanetsData
import java.nio.Buffer
import kotlin.random.Random


internal class VertexProperties(
    val numberOfEvilPlanetsPerPlanet: Int = NUMBER_OF_EVIL_PLANET_PER_PLANET,
    val minSize: Float = MIN_SIZE,
    val sizeRange: Float = SIZE_RANGE,
) {
    companion object {
        const val NUMBER_OF_EVIL_PLANET_PER_PLANET = 10
        const val MIN_SIZE = 0.3f
        const val SIZE_RANGE = 0.3f

    }
}



internal class Vertex(
    properties: VertexProperties,
    textureDimensions: TextureDimensions,
    planets: FloatArray,
): AttributeData() {


    companion object {

        // 2 position + 1 size + 4 texture coordinates + 3 color + 1 collision flag + 1 isDestroyed flag
        const val NUMBER_OF_FLOATS_PER_VERTEX = 12
        const val SPAWN_RADIUS = 2f


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
    val numberOfEvilPlanets = planets.size / PlanetsData.NUMBER_OF_FLOATS_PER_VERTEX * properties.numberOfEvilPlanetsPerPlanet
    override val size = numberOfEvilPlanets * numberOfFloatsPerVertex
    private val data: FloatArray = FloatArray(size)

    override fun getBuffer(): Buffer = data.toBuffer()


    init {
        generatePoints(
            data = data,
            properties = properties,
            numberOfFloatsPerVertex = numberOfFloatsPerVertex,
            numberOfEvilPlanets = numberOfEvilPlanets,
            planets = planets,
            textureDimensions = textureDimensions
        )
    }

}



private fun generatePoints(
    data: FloatArray,
    properties: VertexProperties,
    numberOfFloatsPerVertex: Int,
    numberOfEvilPlanets: Int,
    planets: FloatArray,
    textureDimensions: TextureDimensions,
) = with(properties) {

    val direction = floatArrayOf(0f, 1f)

    for (i in 0 until numberOfEvilPlanets) {
        val planetX = planets[i / numberOfEvilPlanetsPerPlanet * PlanetsData.NUMBER_OF_FLOATS_PER_VERTEX]
        val planetY = planets[i / numberOfEvilPlanetsPerPlanet * PlanetsData.NUMBER_OF_FLOATS_PER_VERTEX + 1]

        //position
        direction.rotate(360f / numberOfEvilPlanetsPerPlanet)

        direction.normalize()
        direction.multiply(SPAWN_RADIUS)

        data[i * numberOfFloatsPerVertex + PX] = planetX + direction.x
        data[i * numberOfFloatsPerVertex + PY] = planetY + direction.y

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
        data[i * numberOfFloatsPerVertex + CR] = Random.nextFloat()  + min
        data[i * numberOfFloatsPerVertex + CB] = Random.nextFloat()  + min
        data[i * numberOfFloatsPerVertex + CG] = Random.nextFloat()  + min
    }
}

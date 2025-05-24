package ge.siradze.explosion.data

import ge.siradze.core.AttributeData
import ge.siradze.explosion.helper.ExplosionHelper
import ge.siradze.glcore.extensions.scale
import ge.siradze.glcore.extensions.toBuffer
import ge.siradze.glcore.extensions.transform
import ge.siradze.glcore.extensions.x
import ge.siradze.glcore.extensions.y
import java.nio.Buffer

internal class Vertex(
    helper: ExplosionHelper,
    tilePosition: FloatArray,
    size: Float,
    position: FloatArray,
    color: FloatArray,
): AttributeData() {
    private val availableSize = if(size > 1f) 1f else size
    val pointNumber = (helper.pointNumber * availableSize).toInt()
    val data: FloatArray = helper.data[
        (tilePosition.x * helper.textureDimensions.columns).toInt()
    ][
        (tilePosition.y * helper.textureDimensions.rows).toInt()
    ]
        .copyOfRange(0, pointNumber * helper.numberOfFloatsPerPoint).apply {
            scale(size, helper.numberOfFloatsPerPoint)
            transform(position.x, position.y, helper.numberOfFloatsPerPoint)
            for(i in 0 until pointNumber) {
                // set color
                this[i * helper.numberOfFloatsPerPoint + 2] *= color[0]
                this[i * helper.numberOfFloatsPerPoint + 3] *= color[1]
                this[i * helper.numberOfFloatsPerPoint + 4] *= color[2]
            }
        }

    override val numberOfFloatsPerVertex: Int = helper.numberOfFloatsPerPoint
    override val typeSize: Int = Float.SIZE_BYTES
    override val size: Int = helper.numberOfFloatsPerPoint * pointNumber
    val numberOfVertex = pointNumber

    override fun getBuffer(): Buffer = data.toBuffer()
}
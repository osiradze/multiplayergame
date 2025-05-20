package ge.siradze.explosion

import ge.siradze.core.extensions.scale
import ge.siradze.core.extensions.toBuffer
import ge.siradze.core.extensions.transform
import ge.siradze.core.extensions.x
import ge.siradze.core.extensions.y
import ge.siradze.core.AttributeData
import ge.siradze.core.shader.CameraShaderLocation
import ge.siradze.core.shader.DeltaTimeShaderLocation
import ge.siradze.core.shader.RatioShaderLocation
import ge.siradze.core.shader.ShaderAttribLocation
import ge.siradze.core.shader.ShaderLocation
import ge.siradze.core.shader.ShaderUniformLocation
import java.nio.Buffer

class ExplosionData {
    class Vertex(
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

    class ShaderLocations (
        val vertex: ShaderAttribLocation = ShaderAttribLocation(
            name = "a_position",
            offset = 0,
        ),
        val color: ShaderAttribLocation = ShaderAttribLocation(
            name = "a_color",
            offset = 2,
        ),
        val isDead: ShaderAttribLocation = ShaderAttribLocation(
            name = "a_isDead",
            offset = 7,
        ),

        // Uniforms
        val ratio: ShaderLocation = RatioShaderLocation(),
        var camera: ShaderLocation = CameraShaderLocation(),


        val floatsPerVertex: ShaderLocation = ShaderUniformLocation(
            name = "u_floats_per_vertex"
        ),
        val playerPosition: ShaderLocation = ShaderUniformLocation(
            name = "u_player_position"
        ),

        val deltaTime: DeltaTimeShaderLocation = DeltaTimeShaderLocation(),

        val push: ShaderUniformLocation = ShaderUniformLocation(
            name = "u_push"
        ),
    )
}
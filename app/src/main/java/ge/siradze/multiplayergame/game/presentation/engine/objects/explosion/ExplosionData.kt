package ge.siradze.multiplayergame.game.presentation.engine.objects.explosion

import ge.siradze.multiplayergame.game.presentation.engine.extensions.multiply
import ge.siradze.multiplayergame.game.presentation.engine.extensions.toBuffer
import ge.siradze.multiplayergame.game.presentation.engine.extensions.transform
import ge.siradze.multiplayergame.game.presentation.engine.extensions.x
import ge.siradze.multiplayergame.game.presentation.engine.extensions.y
import ge.siradze.multiplayergame.game.presentation.engine.objects.AttributeData
import ge.siradze.multiplayergame.game.presentation.engine.shader.CameraShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.DeltaTimeShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.RatioShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderAttribLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderLocation
import ge.siradze.multiplayergame.game.presentation.engine.shader.ShaderUniformLocation
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
            multiply(size)
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
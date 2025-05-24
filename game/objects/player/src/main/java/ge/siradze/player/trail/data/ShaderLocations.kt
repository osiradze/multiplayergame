package ge.siradze.player.trail.data

import ge.siradze.glcore.shader.CameraShaderLocation
import ge.siradze.glcore.shader.ObjectShaderLocations
import ge.siradze.glcore.shader.RatioShaderLocation
import ge.siradze.glcore.shader.ShaderAttribLocation
import ge.siradze.glcore.shader.ShaderLocation
import ge.siradze.glcore.shader.ShaderUniformLocation

internal data class ShaderLocations(
    val vertex : ShaderAttribLocation = ShaderAttribLocation(
        name = "a_position",
        offset = 0,
        size = 3,
    ),

    val ratio: ShaderLocation = RatioShaderLocation(),
    val camera: ShaderLocation = CameraShaderLocation(),
    val playerPosition: ShaderLocation = ShaderUniformLocation(
        name = "u_player_position"
    ),
    val floatsPerVertex: ShaderLocation = ShaderUniformLocation(
        name = "u_floatsPerVertex"
    ),
    val dataSize: ShaderUniformLocation = ShaderUniformLocation(
        name = "u_dataSize"
    ),
) : ObjectShaderLocations {
    override val attributeLocations = listOf(vertex)
    override val programUniformLocations: List<ShaderLocation> = listOf(
        ratio,
        camera,
    )
    override val computeUniformLocations: List<ShaderLocation> = listOf(
        playerPosition,
        floatsPerVertex,
        dataSize,
    )
}

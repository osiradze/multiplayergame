package ge.siradze.player.main.data

import ge.siradze.glcore.shader.CameraShaderLocation
import ge.siradze.glcore.shader.ObjectShaderLocations
import ge.siradze.glcore.shader.RatioShaderLocation
import ge.siradze.glcore.shader.ShaderAttribLocation
import ge.siradze.glcore.shader.ShaderLocation
import ge.siradze.glcore.shader.ShaderUniformLocation

internal class ShaderLocations(
    val vertex : ShaderAttribLocation = ShaderAttribLocation(
        name = "a_position",
        offset = 0,
        size = 2,
    ),
    private val textureCoordinates : ShaderAttribLocation = ShaderAttribLocation(
        name = "a_texture_coordinates",
        offset = 2,
        size = 2,
    ),
    val ratio: ShaderLocation = RatioShaderLocation(),
    val camera: ShaderLocation = CameraShaderLocation(),

    val position: ShaderLocation = ShaderUniformLocation(
        name = "u_position"
    ),
    val direction: ShaderLocation = ShaderUniformLocation(
        name = "u_direction"
    ),

    val texture: ShaderLocation = ShaderUniformLocation(
        name = "u_texture"
    ),
) : ObjectShaderLocations {

    override val attributeLocations: List<ShaderAttribLocation> = listOf(
        vertex,
        textureCoordinates,
    )
    override val programUniformLocations: List<ShaderLocation> = listOf(
        ratio,
        camera,
        position,
        direction,
        texture,
    )
    override val computeUniformLocations: List<ShaderLocation> = emptyList()

}
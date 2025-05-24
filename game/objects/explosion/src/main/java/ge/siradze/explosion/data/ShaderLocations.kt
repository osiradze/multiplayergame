package ge.siradze.explosion.data

import ge.siradze.glcore.shader.CameraShaderLocation
import ge.siradze.glcore.shader.DeltaTimeShaderLocation
import ge.siradze.glcore.shader.ObjectShaderLocations
import ge.siradze.glcore.shader.RatioShaderLocation
import ge.siradze.glcore.shader.ShaderAttribLocation
import ge.siradze.glcore.shader.ShaderLocation
import ge.siradze.glcore.shader.ShaderUniformLocation

internal data class ShaderLocations (
    val vertex: ShaderAttribLocation = ShaderAttribLocation(
        name = "a_position",
        size = 2,
        offset = 0,
    ),
    val color: ShaderAttribLocation = ShaderAttribLocation(
        name = "a_color",
        size = 3,
        offset = 2,
    ),
    val isDead: ShaderAttribLocation = ShaderAttribLocation(
        name = "a_isDead",
        size = 1,
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
) : ObjectShaderLocations {

    override val attributeLocations: List<ShaderAttribLocation> = listOf(
        vertex,
        color,
        isDead,
    )

    override val programUniformLocations: List<ShaderLocation> = listOf(
        ratio,
        camera,
    )
    override val computeUniformLocations: List<ShaderLocation> = listOf(
        floatsPerVertex,
        playerPosition,
        deltaTime,
        push
    )

}
package ge.siradze.asteroids.data

import ge.siradze.glcore.shader.CameraShaderLocation
import ge.siradze.glcore.shader.ObjectShaderLocations
import ge.siradze.glcore.shader.RatioShaderLocation
import ge.siradze.glcore.shader.ReaderOffsetShaderLocation
import ge.siradze.glcore.shader.ShaderAttribLocation
import ge.siradze.glcore.shader.ShaderLocation
import ge.siradze.glcore.shader.ShaderUniformLocation

internal data class ShaderLocations(
    val vertex : ShaderAttribLocation = ShaderAttribLocation(
        name = "a_position",
        size = 2,
        offset = 0
    ),
    val velocity : ShaderAttribLocation = ShaderAttribLocation(
        name = "a_velocity",
        size = 2,
        offset = 2

    ),
    val size : ShaderAttribLocation = ShaderAttribLocation(
        name = "a_size",
        size = 1,
        offset = 4
    ),
    val textureCoordinates : ShaderAttribLocation = ShaderAttribLocation(
        name = "a_texture_coordinates",
        size = 4,
        offset = 5
    ),
    val color : ShaderAttribLocation = ShaderAttribLocation(
        name = "a_color",
        size = 3,
        offset = 9
    ),
    val isAlive : ShaderAttribLocation = ShaderAttribLocation(
        name = "a_isAlive",
        size = 1,
        offset = 12
    ),
    // required to convert pixel size to world units
    val screenWidth : ShaderUniformLocation = ShaderUniformLocation(
        name = "u_screen_width"
    ),

    val ratio: ShaderLocation = RatioShaderLocation(),
    var camera: ShaderLocation = CameraShaderLocation(),

    val texture: ShaderLocation = ShaderUniformLocation(
        name = "u_texture"
    ),

    val floatsPerVertex: ShaderLocation = ShaderUniformLocation(
        name = "u_floats_per_vertex"
    ),
    val playerPosition: ShaderLocation = ShaderUniformLocation(
        name = "u_player_position"
    ),
    val destructible: ShaderUniformLocation = ShaderUniformLocation(
        name = "u_destructible"
    ),
    val deltaTime : ShaderUniformLocation = ShaderUniformLocation(
        name = "u_delta_time"
    ),
    val readerOffset : ShaderUniformLocation = ReaderOffsetShaderLocation(),
) : ObjectShaderLocations {
    override val attributeLocations: List<ShaderAttribLocation> = listOf(
        vertex,
        velocity,
        size,
        textureCoordinates,
        color,
        isAlive
    )
    override val programUniformLocations: List<ShaderLocation> = listOf(
        ratio,
        camera,
        screenWidth,
        texture,
    )

    override val computeUniformLocations: List<ShaderLocation> = listOf(
        floatsPerVertex,
        playerPosition,
        destructible,
        deltaTime,
        readerOffset
    )

}
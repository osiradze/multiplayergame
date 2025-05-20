package ge.siradze.planets.data

import ge.siradze.core.shader.ShaderAttribLocation
import ge.siradze.core.shader.ShaderLocation
import ge.siradze.core.shader.ShaderUniformLocation
import ge.siradze.core.shader.RatioShaderLocation
import ge.siradze.core.shader.CameraShaderLocation



internal data class ShaderLocations(
    val vertex : ShaderAttribLocation = ShaderAttribLocation(
        name = "a_position",
        size = 2,
        offset = 0
    ),
    val size : ShaderAttribLocation = ShaderAttribLocation(
        name = "a_size",
        size = 1,
        offset = 2
    ),
    val textureCoordinates : ShaderAttribLocation = ShaderAttribLocation(
        name = "a_texture_coordinates",
        size = 4,
        offset = 3
    ),
    val color : ShaderAttribLocation = ShaderAttribLocation(
        name = "a_color",
        size = 3,
        offset = 7
    ),
    val isDestroyed : ShaderAttribLocation = ShaderAttribLocation(
        name = "a_isDestroyed",
        size = 1,
        offset = 11
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
    val drawLine : ShaderUniformLocation = ShaderUniformLocation(
        name = "u_drawLine"
    ),
    val readerOffset : ShaderUniformLocation = ShaderUniformLocation(
        name = "u_reader_offset"
    ),
) {
    val attributeLocations: List<ShaderAttribLocation> = listOf(
        vertex,
        size,
        textureCoordinates,
        color,
        isDestroyed,
    )
    val programUniformLocations: List<ShaderLocation> = listOf(
        screenWidth,
        ratio,
        camera,
        drawLine,
        texture,
    )
    val computeUniformLocations: List<ShaderLocation> = listOf(
        floatsPerVertex,
        playerPosition,
        destructible,
        readerOffset
    )
}
package ge.siradze.stars.data

import ge.siradze.glcore.shader.CameraShaderLocation
import ge.siradze.glcore.shader.ObjectShaderLocations
import ge.siradze.glcore.shader.RatioShaderLocation
import ge.siradze.glcore.shader.ShaderAttribLocation
import ge.siradze.glcore.shader.ShaderLocation

data class ShaderLocations(
    val vertex : ShaderAttribLocation = ShaderAttribLocation(
        name = "a_position",
        offset = 0,
        size = 2,
    ),
    val cameraSpeed : ShaderAttribLocation = ShaderAttribLocation(
        name = "a_camera_speed",
        offset = 2,
        size = 1,
    ),
    val brightness : ShaderAttribLocation = ShaderAttribLocation(
        name = "a_brightness",
        offset = 3,
        size = 1,
    ),
    val camera : ShaderLocation = CameraShaderLocation(),
    val ratio : RatioShaderLocation = RatioShaderLocation(),

) : ObjectShaderLocations {
    override val attributeLocations = listOf(vertex, cameraSpeed, brightness)
    override val programUniformLocations: List<ShaderLocation> = listOf(
        ratio,
        camera
    )
    override val computeUniformLocations: List<ShaderLocation> = emptyList()
}
package ge.siradze.glcore.shader

import android.content.Context
import androidx.annotation.RawRes
import ge.siradze.glcore.utils.OpenGLUtils
import ge.siradze.glcore.utils.ShaderUtils

class Shader(
    val type: Int,
    @RawRes val source: Int,
    val name: String
) {
    fun create(context: Context): Int? {
        return OpenGLUtils.createShader(type, ShaderUtils.readShaderFile(context, source), name)
    }
}
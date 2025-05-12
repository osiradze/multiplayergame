package ge.siradze.core.shader

import android.content.Context
import androidx.annotation.RawRes
import ge.siradze.core.utils.OpenGLUtils
import ge.siradze.core.utils.ShaderUtils

class Shader(
    val type: Int,
    @RawRes val source: Int,
    val name: String
) {
    fun create(context: Context): Int? {
        return OpenGLUtils.createShader(type, ShaderUtils.readShaderFile(context, source), name)
    }
}
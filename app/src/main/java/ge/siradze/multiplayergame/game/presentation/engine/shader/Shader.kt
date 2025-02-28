package ge.siradze.multiplayergame.game.presentation.engine.shader

import android.content.Context
import androidx.annotation.RawRes
import ge.siradze.multiplayergame.game.presentation.engine.utils.OpenGLUtils
import ge.siradze.multiplayergame.game.presentation.engine.utils.ShaderUtils

class Shader(
    val type: Int,
    @RawRes val source: Int,
    val name: String
) {
    fun create(context: Context): Int? {
        return OpenGLUtils.createShader(type, ShaderUtils.readShaderFile(context, source), name)
    }
}
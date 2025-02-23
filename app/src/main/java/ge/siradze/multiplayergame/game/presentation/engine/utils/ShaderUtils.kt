package ge.siradze.multiplayergame.game.presentation.engine.utils

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader

object ShaderUtils {

    fun readShaderFile(context: Context, resourceId: Int): String {
        val shaderCode = StringBuilder()
        try {
            context.resources.openRawResource(resourceId).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).useLines { lines ->
                    lines.forEach {
                        shaderCode.append(it).append("\n")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return shaderCode.toString()
    }
}
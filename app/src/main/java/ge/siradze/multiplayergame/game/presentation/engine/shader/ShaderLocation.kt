package ge.siradze.multiplayergame.game.presentation.engine.shader

import android.opengl.GLES20.glGetAttribLocation
import android.opengl.GLES20.glGetUniformLocation

abstract class ShaderLocation(
    var location: Int = -1,
    val name: String
){
    abstract fun init(program: Int)
}

open class ShaderUniformLocation(
    location: Int = -1,
    name: String
): ShaderLocation(location, name) {
    override fun init(program: Int){
        location = glGetUniformLocation(program, name)
    }
}

open class ShaderAttribLocation(
    location: Int = -1,
    name: String
): ShaderLocation(location, name) {
    override fun init(program: Int){
        location =  glGetAttribLocation(program, name)
    }
}
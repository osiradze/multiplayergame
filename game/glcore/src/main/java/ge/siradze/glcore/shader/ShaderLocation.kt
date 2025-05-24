package ge.siradze.glcore.shader

import android.opengl.GLES20.glDisableVertexAttribArray
import android.opengl.GLES20.glEnableVertexAttribArray
import android.opengl.GLES20.glGetAttribLocation
import android.opengl.GLES20.glGetUniformLocation
import android.opengl.GLES31.glVertexAttribPointer



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
    name: String,
    val size: Int = 0,
    val offset: Int = 0,
): ShaderLocation(location, name) {
    override fun init(program: Int){
        location = glGetAttribLocation(program, name)
    }

    fun load(
        size: Int,
        type: Int,
        normalized: Boolean,
        stride: Int,
        offset: Int
    ) {
        glEnableVertexAttribArray(location)
        glVertexAttribPointer(location, size, type, normalized, stride, offset)
        glDisableVertexAttribArray(location)
    }
}
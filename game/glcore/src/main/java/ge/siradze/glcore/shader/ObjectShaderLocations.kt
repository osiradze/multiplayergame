package ge.siradze.glcore.shader

import android.opengl.GLES20.glDisableVertexAttribArray
import android.opengl.GLES20.glEnableVertexAttribArray
import android.opengl.GLES31.GL_FLOAT

interface ObjectShaderLocations {
    val attributeLocations: List<ShaderAttribLocation>
    val programUniformLocations: List<ShaderLocation>
    val computeUniformLocations: List<ShaderLocation>

    fun init(
        program: Int,
        computeProgram: Int? = null,
        stride: Int,
        type: Int = GL_FLOAT,
        typeSize : Int = Float.SIZE_BYTES
    ) {
        attributeLocations.forEach {
            with(it) {
                init(program)
                load(size, type, false, stride, offset * typeSize)
            }
        }

        // Program Uniforms
        programUniformLocations.forEach {
            it.init(program)
        }
        computeProgram?.let {
            computeUniformLocations.forEach {
                it.init(computeProgram)
            }
        }
    }

    fun enableAttributeLocations() {
        attributeLocations.forEach {
            glEnableVertexAttribArray(it.location)
        }
    }
    fun disableAttributeLocations() {
        attributeLocations.forEach {
            glDisableVertexAttribArray(it.location)
        }
    }
}
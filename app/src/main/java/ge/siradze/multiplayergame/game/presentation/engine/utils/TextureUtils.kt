package ge.siradze.multiplayergame.game.presentation.engine.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import android.opengl.GLES20
import android.opengl.GLES20.GL_LINEAR
import android.opengl.GLES20.GL_TEXTURE0
import android.opengl.GLES30.GL_NEAREST
import android.opengl.GLES30.GL_TEXTURE_2D
import android.opengl.GLES30.GL_TEXTURE_MAG_FILTER
import android.opengl.GLES30.GL_TEXTURE_MIN_FILTER
import android.opengl.GLES30.glActiveTexture
import android.opengl.GLES30.glBindTexture
import android.opengl.GLES30.glTexParameteri
import android.opengl.GLES30.glUniform1i
import android.opengl.GLUtils

object TextureUtils {

    /**
     * Loads textures to the GPU
     * @param offset Offset for GL_TEXTURE0 to GL_TEXTURE32 all object should share 32 capacity
     */
    fun loadTexture(
        bitmap: Bitmap,
        textureId: Int,
        locations: Int,
        offset: Int,
        filterType : Int = GL_NEAREST
    ): Int {
        val texture = GL_TEXTURE0 + offset
        glActiveTexture(texture)
        glBindTexture(GL_TEXTURE_2D, textureId)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filterType)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filterType)

        try{
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0) }
        catch (e: Exception){
            e.printStackTrace()
        }
        bitmap.recycle()
        glUniform1i(locations, offset)
        return texture
    }

    private fun flipBitmapVertically(bitmap: Bitmap): Bitmap {
        val matrix = Matrix().apply {
            postScale(1f, -1f, bitmap.width / 2f, bitmap.height / 2f)
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

}
package ge.siradze.multiplayergame.game.presentation.engine.objects.explosion

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import ge.siradze.multiplayergame.game.presentation.engine.texture.TextureDimensions
import androidx.core.graphics.get

class ExplosionHelper(
    context: Context,
    val textureDimensions: TextureDimensions
) {
    val pointNumber: Int = 2000

    val numberOfFloatsPerPoint = 8 // 2 - position.  3 - color. 2 - velocity. 1 - isDead
    // adding 1 for counter
    val data: Array<Array<FloatArray>> = Array(textureDimensions.columns) { Array(textureDimensions.rows) { FloatArray((pointNumber * numberOfFloatsPerPoint)) } }

    private val bitmap = BitmapFactory.decodeResource(context.resources, textureDimensions.bitmapRes)
    private val bitmapArray = Array(textureDimensions.columns) { x ->
        Array(textureDimensions.rows) { y ->
            cutBitmap (
                bitmap = bitmap,
                x = x,
                y = y
            )
        }
    }

    init {
        for (i in 0 until textureDimensions.columns) {
            for (j in 0 until textureDimensions.rows) {
                generatePointsFor(x = i, y = j)
            }
        }
    }

    private fun cutBitmap(
        bitmap: Bitmap,
        x: Int,
        y: Int,
    ): Bitmap {
        val width = bitmap.width / textureDimensions.columns
        val height = bitmap.height / textureDimensions.rows
        return Bitmap.createBitmap(bitmap, x * width, y * height, width, height)
    }

    private fun generatePointsFor(
        x: Int,
        y: Int,
    ) {
        // generate random points
        var counter = 0

        val bitmap = bitmapArray[x][y]
        for (i in 0 until pointNumber) {
            // generate random point
            val randomX = (Math.random() * bitmap.width).toInt()
            val randomY = (Math.random() * bitmap.height).toInt()
            // get pixel color
            val pixel = try {
                bitmap[randomX, randomY]
            } catch (e: ArrayIndexOutOfBoundsException) {
                continue
            }
            // check if it's transparent
            val alpha = Color.alpha(pixel) / 255f
            if(alpha < 0.1f) continue

            // get color values
            val red = Color.red(pixel) / 255f
            val green = Color.green(pixel) / 255f
            val blue = Color.blue(pixel) / 255f

            // set color values to data
            val startPosition = counter * numberOfFloatsPerPoint
            // set position values
            data[x][y][startPosition + 0] = randomX.toFloat() / bitmap.width - 0.5f
            data[x][y][startPosition + 1] = -(randomY.toFloat() / bitmap.height - 0.5f)

            // set color values
            data[x][y][startPosition + 2] = red
            data[x][y][startPosition + 3] = green
            data[x][y][startPosition + 4] = blue

            // set velocity values
            data[x][y][startPosition + 5] = 0f
            data[x][y][startPosition + 6] = 0f
            counter += 1
        }
        //data[x][y][counter * numberOfFloatsPerPoint] = counter.toFloat()

    }


}
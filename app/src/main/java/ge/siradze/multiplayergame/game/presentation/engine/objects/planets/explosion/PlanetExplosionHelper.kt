package ge.siradze.multiplayergame.game.presentation.engine.objects.planets.explosion

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import ge.siradze.multiplayergame.R
import ge.siradze.multiplayergame.game.presentation.engine.texture.TextureDimensions
import androidx.core.graphics.get

class PlanetExplosionHelper(
    context: Context,
    private val dims: TextureDimensions = TextureDimensions(4, 4),
) {
    val pointNumber: Int = 1000

    val numberOfFloatsPerPoint = 5 // x, y, r, g, b
    // adding 1 for counter
    val data: Array<Array<FloatArray>> = Array(dims.columns) { Array(dims.rows) { FloatArray((pointNumber * numberOfFloatsPerPoint) + 1) } }

    private val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.planets)
    private val bitmapArray = Array(dims.columns) {  x ->
        Array(dims.rows) { y ->
            cutBitmap (
                bitmap = bitmap,
                x = x,
                y = y
            )
        }
    }

    init {
        for (i in 0 until dims.columns) {
            for (j in 0 until dims.rows) {
                generatePointsFor(x = i, y = j)
            }
        }
        Log.i("TAG", ": ")
    }

    private fun cutBitmap(
        bitmap: Bitmap,
        x: Int,
        y: Int,
    ): Bitmap {
        val width = bitmap.width / dims.columns
        val height = bitmap.height / dims.rows
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
            //if(alpha < 0.1f) continue

            // get color values
            val red = Color.red(pixel) / 255f
            val green = Color.green(pixel) / 255f
            val blue = Color.blue(pixel) / 255f

            // set color values to data
            val startPosition = counter * 5
            data[x][y][startPosition + 0] = randomX.toFloat() / bitmap.width
            data[x][y][startPosition + 1] = randomY.toFloat()  / bitmap.height
            data[x][y][startPosition + 2] = red
            data[x][y][startPosition + 3] = green
            data[x][y][startPosition + 4] = blue
            counter += 1
        }
        data[x][y][counter * 5] = counter.toFloat()

    }


}
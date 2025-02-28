package ge.siradze.multiplayergame.game.presentation.engine.extensions

import kotlin.math.cos
import kotlin.math.sin

fun FloatArray.scale(times: Float, floatsPerVertex: Int): FloatArray {
    return FloatArray(
        init = { i ->
            if(i % floatsPerVertex == 0 || i % floatsPerVertex == 1) {
                get(i) * times
            } else {
                get(i)
            }
        },
        size = size

    )
}

fun FloatArray.transform(x: Float, y: Float, floatsPerVertex: Int): FloatArray {
    return FloatArray(
        init = { i ->
            if(i % floatsPerVertex == 0) {
                get(i) + x
            } else if (i % floatsPerVertex == 1)  {
                get(i) + y
            } else {
                get(i)
            }
        },
        size = size
    )
}

fun FloatArray.middlePoint(floatsPerVertex: Int): FloatArray {

    val vertexNumber = size / floatsPerVertex

    var midX = 0f
    var midY = 0f

    for (i in indices) {
        if (i % floatsPerVertex != 0) {
            midX += get(i)
        }
        if (i % floatsPerVertex != 1) {
            midY += get(i + 1)
        }

    }
    return floatArrayOf(midX / vertexNumber, midY / vertexNumber)
}

operator fun FloatArray.times(amount: Float): FloatArray {
    return FloatArray(this.size) { i -> this[i] * amount }
}

inline fun FloatArray.add(other: FloatArray) {
    for (i in this.indices) {
        set(i, this[i] + other[i])
    }
}

inline fun FloatArray.normalize() {
    val magnitude = kotlin.math.sqrt(this.sumOf { it.toDouble() * it }).toFloat() // Compute length
    require(magnitude > 0) { "Cannot normalize a zero vector" }
    for (i in this.indices) {
        this[i] /= magnitude
    }
}

inline fun FloatArray.rotate(angleDegrees: Float) {
    require(size == 2) { "Only 2D vectors can be rotated" }

    val angleRad = Math.toRadians(angleDegrees.toDouble()).toFloat() // Convert to radians
    val cosA = cos(angleRad)
    val sinA = sin(angleRad)

    val x = this[0]
    val y = this[1]

    this[0] = x * cosA - y * sinA // Apply rotation
    this[1] = x * sinA + y * cosA
}

inline val FloatArray.x: Float get () = get(0)
inline val FloatArray.y: Float get () = get(1)
inline val FloatArray.z: Float get () = get(2)
inline val FloatArray.w: Float get () = get(3)

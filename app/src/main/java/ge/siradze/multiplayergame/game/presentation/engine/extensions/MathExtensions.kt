package ge.siradze.multiplayergame.game.presentation.engine.extensions

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

fun FloatArray.multiply(value: Float) {
    for (i in this.indices) {
        set(i, this[i] * value)
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun FloatArray.add(other: FloatArray) {
    for (i in this.indices) {
        set(i, this[i] + other[i])
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun FloatArray.normalize() {
    val length = sqrt(this[0] * this[0] + this[1] * this[1])
    if (length != 0f) {
        this[0] /= length
        this[1] /= length
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun FloatArray.isZeroVector(): Boolean {
    return all { it == 0f }
}

@Suppress("NOTHING_TO_INLINE")
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

fun FloatArray.fillWith(array: FloatArray) {
    for (i in indices) {
        val index = if(i >= array.size) {
            i % array.size
        } else i
        set(i, array[index])
    }
}

fun FloatArray.vectorLength(): Float {
    return sqrt(x * x + y * y)
}

fun signedAngleBetweenVectors(v1: FloatArray, v2: FloatArray): Float {
    // Validate input arrays
    require(v1.size == 2) { "First vector must be 2D (size 2)" }
    require(v2.size == 2) { "Second vector must be 2D (size 2)" }

    // Method 1: Using cross product for sign and dot product for angle
    val dotProduct = v1[0] * v2[0] + v1[1] * v2[1]

    // 2D cross product (actually the z-component of the 3D cross product)
    val crossProduct = v1[0] * v2[1] - v1[1] * v2[0]

    // Calculate magnitudes of vectors
    val magnitudeV1 = sqrt(v1[0] * v1[0] + v1[1] * v1[1])
    val magnitudeV2 = sqrt(v2[0] * v2[0] + v2[1] * v2[1])

    // Calculate cosine, handling division by zero
    if (magnitudeV1 == 0f || magnitudeV2 == 0f) {
        throw IllegalArgumentException("Cannot calculate angle with zero vector")
    }

    // Calculate cosine of angle
    var cosine = dotProduct / (magnitudeV1 * magnitudeV2)

    // Handle floating point precision issues
    cosine = max(-1f, min(1f, cosine))

    // Calculate angle in radians
    val angleRadians = acos(cosine)

    // Determine sign using cross product
    val signedAngleRadians = if (crossProduct >= 0) angleRadians else -angleRadians

    // Convert to degrees
    return signedAngleRadians * 180f / Math.PI.toFloat()

    // Alternative method using atan2 (more direct approach)
    // return atan2(crossProduct, dotProduct) * 180f / Math.PI.toFloat()
}

inline val FloatArray.x: Float get () = get(0)
inline val FloatArray.y: Float get () = get(1)
inline val FloatArray.z: Float get () = get(2)
inline val FloatArray.w: Float get () = get(3)

package ge.siradze.player

sealed class PlayerEvents {
    data object Accelerate : PlayerEvents()
    data object Decelerate : PlayerEvents()
    class Rotate(val vector: FloatArray) : PlayerEvents()
}
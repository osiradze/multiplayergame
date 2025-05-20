package ge.siradze.explosion.event

import ge.siradze.explosion.ExplosionHelper


class CreateExplosion(
    val position: FloatArray,
    val size: Float,
    val planet: FloatArray,
    val color: FloatArray,
    val explosionHelper: ExplosionHelper
)

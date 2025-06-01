package ge.siradze.explosion.event

import ge.siradze.explosion.helper.ExplosionHelper


class ExplotionCreationEvent(
    val position: FloatArray,
    val size: Float,
    val planet: FloatArray,
    val color: FloatArray,
    val explosionHelper: ExplosionHelper
)

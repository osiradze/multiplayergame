package ge.siradze.player.main

import ge.siradze.glcore.EngineGlobals
import ge.siradze.glcore.extensions.normalize
import ge.siradze.glcore.extensions.rotate
import ge.siradze.glcore.extensions.signedAngleBetweenVectors
import ge.siradze.glcore.extensions.vectorLength
import ge.siradze.glcore.extensions.x
import ge.siradze.glcore.extensions.y
import ge.siradze.player.PlayerEvents
import kotlin.math.pow

class PlayerProperties(
    val position: FloatArray = floatArrayOf(0.0f, 0.0f),
    var direction: FloatArray = floatArrayOf(0.0f, 1.0f).apply {
        normalize()
    },
    private val targetDirection: FloatArray = floatArrayOf(0.0f, 1.0f).apply {
        normalize()
    },
    private var velocity: Float = 0f,
) {
    // pushes or pulls particles
    var push: Boolean = true

    private val rotateSpeed = 10f
    private var gas = false
    private val gasForce = 0.01f
    private val maxSpeed = 1.0f
    private val deceleration = 0.98f

    fun update() {
        if (gas && velocity < maxSpeed) {
            velocity += gasForce
        } else {
            velocity *= deceleration
        }
        val vectorLength = targetDirection.vectorLength().toDouble().pow(0.5).toFloat() * 0.1f

        position[0] += direction[0] * velocity * EngineGlobals.deltaTime * vectorLength
        position[1] += direction[1] * velocity * EngineGlobals.deltaTime * vectorLength
        val angle = signedAngleBetweenVectors(targetDirection, direction)
        direction.rotate(-angle * rotateSpeed * vectorLength * EngineGlobals.deltaTime)
    }

    fun addForce(
        forceDirection: FloatArray,
        power: Float? = null
    ) {
        direction[0] += (position.x - forceDirection.x) * 10f
        direction[1] += (position.y - forceDirection.y) * 10f

        power?.let {
            velocity += it
        }
        direction.normalize()
    }

    fun onUIEvent(event: PlayerEvents) {
        when(event) {
            PlayerEvents.Accelerate -> {
                gas = true
            }
            PlayerEvents.Decelerate -> {
                gas = false
            }

            is PlayerEvents.Rotate -> {
                targetDirection[0] = event.vector[0]
                targetDirection[1] = -event.vector[1]
            }
        }
    }
}
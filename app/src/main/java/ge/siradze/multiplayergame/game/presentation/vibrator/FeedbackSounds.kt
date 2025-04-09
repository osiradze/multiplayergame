package ge.siradze.multiplayergame.game.presentation.vibrator

import android.content.Context
import android.os.Vibrator
import android.os.VibratorManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator.VIBRATION_EFFECT_SUPPORT_YES
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission

interface FeedbackSounds {
    fun vibrate(milliseconds: Long)
    fun vibratePattern(pattern: LongArray, repeat: Int)
    fun cancel()
}

class FeedbackSoundsImpl(context: Context): FeedbackSounds {

    private val vibrator: Vibrator? by lazy {
        getVibrator(context)
    }

    /**
     * Vibrates the device for the specified duration.
     * @param milliseconds Duration of vibration in milliseconds
     */
    @RequiresPermission(android.Manifest.permission.VIBRATE)
    override fun vibrate(milliseconds: Long) {
        val amplitude = getAmplitude()
        vibrator?.let { vibrator ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                vibrator.vibrate(VibrationEffect.createPredefined(amplitude))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(milliseconds)
            }
        }
    }

    private fun getAmplitude(): Int {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
            vibrator?.areAllEffectsSupported(VibrationEffect.EFFECT_TICK) == VIBRATION_EFFECT_SUPPORT_YES
        ) {
            return VibrationEffect.EFFECT_TICK
        } else {
            return VibrationEffect.DEFAULT_AMPLITUDE
        }
    }

    /**
     * Vibrates the device with a pattern.
     * @param pattern Pattern of wait/vibrate timings
     * @param repeat Index at which to repeat or -1 for no repeat
     */
    @RequiresPermission(android.Manifest.permission.VIBRATE)
    override fun vibratePattern(pattern: LongArray, repeat: Int) {
        vibrator?.let { vibrator ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, repeat))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, repeat)
            }
        }
    }

    /**
     * Gets the vibrator service appropriate for the device's API level.
     */
    private fun getVibrator(context: Context): Vibrator? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    /**
     * Cancels any ongoing vibration.
     */
    @RequiresPermission(android.Manifest.permission.VIBRATE)
    override fun cancel() {
        vibrator?.cancel()
    }



}
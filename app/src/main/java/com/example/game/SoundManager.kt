package com.example.game

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

/**
 * High-performance, non-blocking Game Audio & Haptics Engine.
 * Synthesizes crisp, satisfying metallic click, drop, complete, and victory chimes
 * loaded into SoundPool for 0-latency playback without blocking the Compose UI thread.
 */
class SoundManager(context: Context) {

    private val appContext = context.applicationContext
    private var soundPool: SoundPool? = null

    private var soundPickupId: Int = 0
    private var soundDropId: Int = 0
    private var soundBoltCompleteId: Int = 0
    private var soundVictoryId: Int = 0
    private var soundErrorId: Int = 0

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = appContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        appContext.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var soundEnabled: Boolean = true
    var hapticEnabled: Boolean = true

    init {
        initSoundPool()
    }

    private fun initSoundPool() {
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(8)
                .setAudioAttributes(audioAttributes)
                .build()

            // Generate and load sounds in background to ensure zero startup UI lag
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val soundDir = File(appContext.cacheDir, "nut_audio_v2").apply { mkdirs() }

                    val pickupFile = File(soundDir, "nut_pickup.wav")
                    val dropFile = File(soundDir, "nut_drop.wav")
                    val completeFile = File(soundDir, "bolt_complete.wav")
                    val victoryFile = File(soundDir, "victory_chime.wav")
                    val errorFile = File(soundDir, "error_bump.wav")

                    if (!pickupFile.exists() || pickupFile.length() < 100) {
                        writeWav(pickupFile, 44100, generatePickupSamples(44100))
                    }
                    if (!dropFile.exists() || dropFile.length() < 100) {
                        writeWav(dropFile, 44100, generateDropSamples(44100))
                    }
                    if (!completeFile.exists() || completeFile.length() < 100) {
                        writeWav(completeFile, 44100, generateBoltCompleteSamples(44100))
                    }
                    if (!victoryFile.exists() || victoryFile.length() < 100) {
                        writeWav(victoryFile, 44100, generateVictorySamples(44100))
                    }
                    if (!errorFile.exists() || errorFile.length() < 100) {
                        writeWav(errorFile, 44100, generateErrorSamples(44100))
                    }

                    soundPool?.let { pool ->
                        soundPickupId = pool.load(pickupFile.absolutePath, 1)
                        soundDropId = pool.load(dropFile.absolutePath, 1)
                        soundBoltCompleteId = pool.load(completeFile.absolutePath, 1)
                        soundVictoryId = pool.load(victoryFile.absolutePath, 1)
                        soundErrorId = pool.load(errorFile.absolutePath, 1)
                    }
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {
            soundPool = null
        }
    }

    /**
     * Crisp, metallic pop when a nut is tapped/lifted.
     */
    fun playPickup() {
        if (soundEnabled && soundPickupId != 0) {
            val pitch = 0.96f + (Random.nextFloat() * 0.08f)
            soundPool?.play(soundPickupId, 0.9f, 0.9f, 1, 0, pitch)
        }
        triggerTickHaptic()
    }

    /**
     * Solid metallic clink when a nut slides into place on a bolt.
     */
    fun playDrop() {
        if (soundEnabled && soundDropId != 0) {
            val pitch = 0.98f + (Random.nextFloat() * 0.05f)
            soundPool?.play(soundDropId, 1.0f, 1.0f, 1, 0, pitch)
        }
        triggerMediumHaptic()
    }

    /**
     * Sparkling bell chime when all 4 nuts of the same color are matched on a bolt.
     */
    fun playBoltComplete() {
        if (soundEnabled && soundBoltCompleteId != 0) {
            soundPool?.play(soundBoltCompleteId, 1.0f, 1.0f, 2, 0, 1.0f)
        }
        triggerSuccessHaptic()
    }

    /**
     * Triumphant victory fanfare when the entire level is solved.
     */
    fun playVictory() {
        if (soundEnabled && soundVictoryId != 0) {
            soundPool?.play(soundVictoryId, 1.0f, 1.0f, 3, 0, 1.0f)
        }
        triggerVictoryHaptic()
    }

    /**
     * Soft wooden bump when an invalid move is attempted.
     */
    fun playError() {
        if (soundEnabled && soundErrorId != 0) {
            soundPool?.play(soundErrorId, 0.7f, 0.7f, 1, 0, 1.0f)
        }
        triggerErrorHaptic()
    }

    private fun triggerTickHaptic() {
        if (!hapticEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(12, 160))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(12)
            }
        } catch (_: Exception) {}
    }

    private fun triggerMediumHaptic() {
        if (!hapticEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(20, 200))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(20)
            }
        } catch (_: Exception) {}
    }

    private fun triggerSuccessHaptic() {
        if (!hapticEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 25, 40, 45), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(50)
            }
        } catch (_: Exception) {}
    }

    private fun triggerVictoryHaptic() {
        if (!hapticEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 30, 40, 50, 40, 80), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(120)
            }
        } catch (_: Exception) {}
    }

    private fun triggerErrorHaptic() {
        if (!hapticEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(35, 120))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(35)
            }
        } catch (_: Exception) {}
    }

    fun release() {
        try {
            soundPool?.release()
            soundPool = null
        } catch (_: Exception) {}
    }

    companion object {
        /**
         * Synthesizes 55ms crisp metallic pop with overtone and transient click.
         */
        private fun generatePickupSamples(sampleRate: Int): ShortArray {
            val numSamples = (sampleRate * 0.055).toInt()
            val samples = ShortArray(numSamples)
            val twoPi = 2.0 * PI
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val env = exp(-t * 70.0)
                val s1 = sin(twoPi * 840.0 * t)
                val s2 = 0.4 * sin(twoPi * 1680.0 * t)
                val click = if (i < (sampleRate * 0.003)) (Random.nextDouble() - 0.5) * 0.5 else 0.0
                val amp = (s1 + s2 + click) * env * 24000.0
                samples[i] = amp.toInt().coerceIn(-32767, 32767).toShort()
            }
            return samples
        }

        /**
         * Synthesizes 70ms tactile metallic settling clink (dual micro-impact).
         */
        private fun generateDropSamples(sampleRate: Int): ShortArray {
            val numSamples = (sampleRate * 0.070).toInt()
            val samples = ShortArray(numSamples)
            val twoPi = 2.0 * PI
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val freq = 640.0 - (t * 800.0)
                val env1 = exp(-t * 55.0)
                val t2 = t - 0.016
                val env2 = if (t2 > 0) exp(-t2 * 65.0) * 0.6 else 0.0
                val s1 = sin(twoPi * freq * t)
                val s2 = 0.35 * sin(twoPi * (freq * 2.1) * t)
                val amp = ((s1 + s2) * (env1 + env2)) * 23000.0
                samples[i] = amp.toInt().coerceIn(-32767, 32767).toShort()
            }
            return samples
        }

        /**
         * Synthesizes 340ms sparkling bell arpeggio (C6, E6, G6, C7).
         */
        private fun generateBoltCompleteSamples(sampleRate: Int): ShortArray {
            val numSamples = (sampleRate * 0.34).toInt()
            val samples = ShortArray(numSamples)
            val twoPi = 2.0 * PI

            val notes = listOf(
                0.00 to 1046.5, // C6
                0.06 to 1318.5, // E6
                0.12 to 1568.0, // G6
                0.18 to 2093.0  // C7
            )

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                var sum = 0.0
                for ((offsetT, freq) in notes) {
                    if (t >= offsetT) {
                        val dt = t - offsetT
                        val env = exp(-dt * 14.0)
                        val s = sin(twoPi * freq * dt) + 0.3 * sin(twoPi * (freq * 2.0) * dt)
                        sum += s * env
                    }
                }
                val amp = sum * 9000.0
                samples[i] = amp.toInt().coerceIn(-32767, 32767).toShort()
            }
            return samples
        }

        /**
         * Synthesizes 550ms triumphant fanfare chime chord.
         */
        private fun generateVictorySamples(sampleRate: Int): ShortArray {
            val numSamples = (sampleRate * 0.55).toInt()
            val samples = ShortArray(numSamples)
            val twoPi = 2.0 * PI

            val chords = listOf(
                0.00 to 523.25, // C5
                0.08 to 659.25, // E5
                0.16 to 783.99, // G5
                0.24 to 1046.5, // C6
                0.32 to 1318.5  // E6
            )

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                var sum = 0.0
                for ((offsetT, freq) in chords) {
                    if (t >= offsetT) {
                        val dt = t - offsetT
                        val env = exp(-dt * 8.5)
                        val s = sin(twoPi * freq * dt) + 0.4 * sin(twoPi * (freq * 2.0) * dt)
                        sum += s * env
                    }
                }
                val amp = sum * 7500.0
                samples[i] = amp.toInt().coerceIn(-32767, 32767).toShort()
            }
            return samples
        }

        /**
         * Synthesizes 80ms soft wooden bump for invalid action.
         */
        private fun generateErrorSamples(sampleRate: Int): ShortArray {
            val numSamples = (sampleRate * 0.08).toInt()
            val samples = ShortArray(numSamples)
            val twoPi = 2.0 * PI
            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val env = exp(-t * 50.0)
                val s = sin(twoPi * 135.0 * t)
                val amp = s * env * 18000.0
                samples[i] = amp.toInt().coerceIn(-32767, 32767).toShort()
            }
            return samples
        }

        /**
         * Writes a mono 16-bit PCM WAV file with standard 44-byte RIFF header.
         */
        private fun writeWav(file: File, sampleRate: Int, samples: ShortArray) {
            val totalDataLen = samples.size * 2
            val totalAudioLen = totalDataLen + 36
            val channels = 1
            val byteRate = sampleRate * channels * 2

            val header = ByteArray(44)
            header[0] = 'R'.code.toByte()
            header[1] = 'I'.code.toByte()
            header[2] = 'F'.code.toByte()
            header[3] = 'F'.code.toByte()
            header[4] = (totalAudioLen and 0xff).toByte()
            header[5] = ((totalAudioLen shr 8) and 0xff).toByte()
            header[6] = ((totalAudioLen shr 16) and 0xff).toByte()
            header[7] = ((totalAudioLen shr 24) and 0xff).toByte()
            header[8] = 'W'.code.toByte()
            header[9] = 'A'.code.toByte()
            header[10] = 'V'.code.toByte()
            header[11] = 'E'.code.toByte()
            header[12] = 'f'.code.toByte()
            header[13] = 'm'.code.toByte()
            header[14] = 't'.code.toByte()
            header[15] = ' '.code.toByte()
            header[16] = 16
            header[17] = 0
            header[18] = 0
            header[19] = 0
            header[20] = 1
            header[21] = 0
            header[22] = channels.toByte()
            header[23] = 0
            header[24] = (sampleRate and 0xff).toByte()
            header[25] = ((sampleRate shr 8) and 0xff).toByte()
            header[26] = ((sampleRate shr 16) and 0xff).toByte()
            header[27] = ((sampleRate shr 24) and 0xff).toByte()
            header[28] = (byteRate and 0xff).toByte()
            header[29] = ((byteRate shr 8) and 0xff).toByte()
            header[30] = ((byteRate shr 16) and 0xff).toByte()
            header[31] = ((byteRate shr 24) and 0xff).toByte()
            header[32] = 2
            header[33] = 0
            header[34] = 16
            header[35] = 0
            header[36] = 'd'.code.toByte()
            header[37] = 'a'.code.toByte()
            header[38] = 't'.code.toByte()
            header[39] = 'a'.code.toByte()
            header[40] = (totalDataLen and 0xff).toByte()
            header[41] = ((totalDataLen shr 8) and 0xff).toByte()
            header[42] = ((totalDataLen shr 16) and 0xff).toByte()
            header[43] = ((totalDataLen shr 24) and 0xff).toByte()

            FileOutputStream(file).use { out ->
                out.write(header)
                val byteBuffer = ByteBuffer.allocate(samples.size * 2).order(ByteOrder.LITTLE_ENDIAN)
                for (s in samples) {
                    byteBuffer.putShort(s)
                }
                out.write(byteBuffer.array())
            }
        }
    }
}

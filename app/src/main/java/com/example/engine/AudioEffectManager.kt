package com.example.engine

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.data.model.SoundTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.exp
import kotlin.math.sin

/**
 * Procedural audio synthesizer for water pouring sounds, clicks, chimes, and victory fanfare.
 * Generates 5 distinct high-quality soft, calm, relaxing ambient music tracks and matching SFX.
 */
class AudioEffectManager {

    var isSoundEnabled: Boolean = true
    var isMusicEnabled: Boolean = true
    var equippedSoundTheme: SoundTheme = SoundTheme.GENTLE_WATER

    private val scope = CoroutineScope(Dispatchers.Default)
    private var ambientJob: Job? = null
    private var previewJob: Job? = null

    /**
     * Continuous ambient soothing music loop synthesizer for the equipped sound theme.
     */
    fun startAmbientMusic(theme: SoundTheme = equippedSoundTheme) {
        if (!isMusicEnabled || ambientJob?.isActive == true) return
        ambientJob = scope.launch {
            try {
                val sampleRate = 44100
                val durationSec = 6
                val numSamples = sampleRate * durationSec
                val buffer = ShortArray(numSamples)
                val random = java.util.Random(42)

                generateAmbientBuffer(buffer, sampleRate, theme, random)

                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(maxOf(buffer.size * 2, minBufferSize))
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                track.play()

                while (isActive && isMusicEnabled) {
                    track.write(buffer, 0, buffer.size)
                }

                try {
                    track.stop()
                    track.release()
                } catch (_: Exception) {}
            } catch (_: Exception) {}
        }
    }

    /**
     * Instantly restarts background music when equipping a new ambient theme.
     */
    fun restartAmbientMusicForEquippedTheme() {
        stopPreview()
        stopAmbientMusic()
        if (isMusicEnabled) {
            startAmbientMusic(equippedSoundTheme)
        }
    }

    fun stopAmbientMusic() {
        ambientJob?.cancel()
        ambientJob = null
    }

    /**
     * Plays a 3.5 second preview of the selected ambient music track and soft water sound,
     * without equipping it.
     */
    fun previewAmbientTheme(theme: SoundTheme, onComplete: () -> Unit = {}) {
        stopPreview()
        val wasMusicActive = ambientJob?.isActive == true
        stopAmbientMusic()

        previewJob = scope.launch {
            try {
                // Play soft pour SFX for the theme as well
                playPourSound(theme)

                val sampleRate = 44100
                val durationMs = 3800
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)
                val random = java.util.Random(101)

                generateAmbientBuffer(buffer, sampleRate, theme, random)

                // Apply smooth fade in (0..0.1) and fade out (0.85..1.0)
                for (i in 0 until numSamples) {
                    val progress = i.toDouble() / numSamples
                    val env = when {
                        progress < 0.10 -> progress / 0.10
                        progress > 0.85 -> (1.0 - progress) / 0.15
                        else -> 1.0
                    }
                    buffer[i] = (buffer[i] * env).toInt().coerceIn(-32768, 32767).toShort()
                }

                playBufferStatic(buffer, sampleRate)
                delay(3800)

                onComplete()
                if (wasMusicActive && isMusicEnabled) {
                    startAmbientMusic(equippedSoundTheme)
                }
            } catch (_: Exception) {
                onComplete()
            }
        }
    }

    fun stopPreview() {
        previewJob?.cancel()
        previewJob = null
    }

    private fun generateAmbientBuffer(
        buffer: ShortArray,
        sampleRate: Int,
        theme: SoundTheme,
        random: java.util.Random
    ) {
        var filterVal = 0.0
        val numSamples = buffer.size

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate

            val sampleVal = when (theme) {
                SoundTheme.GENTLE_WATER -> {
                    // 1. Eb Major 7 aquatic pad (Eb3, Bb3, Eb4, G4, Bb4)
                    val notes = listOf(155.56, 233.08, 311.13, 392.00, 466.16)
                    var synthSum = 0.0
                    for ((idx, freq) in notes.withIndex()) {
                        val lfo = 0.5 + 0.5 * sin(2.0 * Math.PI * (0.08 + idx * 0.05) * t)
                        synthSum += sin(2.0 * Math.PI * freq * t) * lfo * 2400.0
                    }

                    // 2. Continuous soothing flowing water stream noise
                    val noise = random.nextDouble() * 2.0 - 1.0
                    filterVal = filterVal * 0.82 + noise * 0.18

                    // 3. Random gentle water droplets
                    val drop = if (random.nextDouble() > 0.998) {
                        sin(2.0 * Math.PI * (500.0 + random.nextInt(300)) * t) * 3000.0
                    } else 0.0

                    synthSum + filterVal * 1800.0 + drop
                }

                SoundTheme.NATURE_HAVEN -> {
                    // 1. Forest Pentatonic harmony (D4 293.66, F#4 369.99, A4 440.0, B4 493.88, D5 587.33)
                    val notes = listOf(293.66, 369.99, 440.00, 493.88, 587.33)
                    var synthSum = 0.0
                    for ((idx, freq) in notes.withIndex()) {
                        val lfo = 0.5 + 0.5 * sin(2.0 * Math.PI * (0.06 + idx * 0.04) * t)
                        synthSum += sin(2.0 * Math.PI * freq * t) * lfo * 2200.0
                    }

                    // 2. Soft woodland leaf rustle wind
                    val leafLfo = 0.5 + 0.5 * sin(2.0 * Math.PI * 0.12 * t)
                    val noise = random.nextDouble() * 2.0 - 1.0
                    filterVal = filterVal * 0.88 + noise * 0.12

                    // 3. Gentle woodland bird chime
                    val bird = if (random.nextDouble() > 0.9985) {
                        sin(2.0 * Math.PI * 1400.0 * t) * 2500.0
                    } else 0.0

                    synthSum + filterVal * 1500.0 * leafLfo + bird
                }

                SoundTheme.LIGHT_WIND -> {
                    // 1. Airy harmonic fifths (A3 220.0, E4 329.63, B4 493.88, E5 659.25)
                    val notes = listOf(220.00, 329.63, 493.88, 659.25)
                    var synthSum = 0.0
                    for ((idx, freq) in notes.withIndex()) {
                        val lfo = 0.5 + 0.5 * sin(2.0 * Math.PI * (0.07 + idx * 0.03) * t)
                        synthSum += sin(2.0 * Math.PI * freq * t) * lfo * 2500.0
                    }

                    // 2. Whispering wind filter sweep
                    val windSweep = 0.5 + 0.5 * sin(2.0 * Math.PI * 0.08 * t)
                    val noise = random.nextDouble() * 2.0 - 1.0
                    filterVal = filterVal * (0.75 + 0.15 * windSweep) + noise * 0.15

                    synthSum + filterVal * 2200.0
                }

                SoundTheme.PEACEFUL_PIANO -> {
                    // 1. Relaxing piano-like chord progression (Fmaj7 -> Cmaj7 -> Am7)
                    val chordTime = (t % 3.0) / 3.0
                    val chordIdx = ((t / 3.0).toInt()) % 3
                    val chordFreqs = when (chordIdx) {
                        0 -> listOf(174.61, 220.00, 261.63, 329.63) // Fmaj7
                        1 -> listOf(130.81, 164.81, 196.00, 246.94) // Cmaj7
                        else -> listOf(110.00, 130.81, 164.81, 220.00) // Am7
                    }
                    val decay = exp(-chordTime * 2.5)
                    var synthSum = 0.0
                    for (f in chordFreqs) {
                        synthSum += sin(2.0 * Math.PI * f * t) * decay * 2800.0
                    }

                    // Warm background bass pad
                    val bass = sin(2.0 * Math.PI * 65.41 * t) * 1600.0
                    synthSum + bass
                }

                SoundTheme.ZEN_MEDITATION -> {
                    // 1. 432Hz root note + 216Hz sub-harmonic + 648Hz singing bowl resonance
                    val bowlLfo = 0.5 + 0.5 * sin(2.0 * Math.PI * 0.10 * t)
                    val thetaBeat = sin(2.0 * Math.PI * 4.0 * t) * 0.15 // 4Hz binaural theta feel

                    val f1 = sin(2.0 * Math.PI * 216.00 * t) * 3200.0
                    val f2 = sin(2.0 * Math.PI * 432.00 * t) * (1.0 + thetaBeat) * 2800.0 * bowlLfo
                    val f3 = sin(2.0 * Math.PI * 648.00 * t) * 1800.0 * bowlLfo

                    f1 + f2 + f3
                }
            }

            buffer[i] = sampleVal.toInt().coerceIn(-32768, 32767).toShort()
        }
    }

    /**
     * Plays a soft, realistic liquid pouring sound at medium volume with filling acoustic resonance.
     */
    fun playPourSound(theme: SoundTheme = equippedSoundTheme) {
        if (!isSoundEnabled) return
        scope.launch {
            try {
                val sampleRate = 44100
                val durationMs = 750
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)
                val random = java.util.Random(1337)

                var filterPrev = 0.0

                for (i in 0 until numSamples) {
                    val progress = i.toDouble() / numSamples

                    val env = when {
                        progress < 0.12 -> progress / 0.12
                        progress > 0.82 -> (1.0 - progress) / 0.18
                        else -> 1.0
                    }

                    val sample = when (theme) {
                        SoundTheme.GENTLE_WATER -> {
                            val fillResonanceFreq = 310.0 + progress * 220.0
                            val flowWave = sin(2.0 * Math.PI * fillResonanceFreq * i / sampleRate) * 0.35
                            val subWave = sin(2.0 * Math.PI * (fillResonanceFreq * 0.5) * i / sampleRate) * 0.20
                            val whiteNoise = random.nextDouble() * 2.0 - 1.0
                            filterPrev = filterPrev * 0.72 + whiteNoise * 0.28
                            val bubble = if (random.nextDouble() > 0.94) {
                                sin(2.0 * Math.PI * (650.0 + random.nextInt(350)) * i / sampleRate) * 0.20
                            } else 0.0
                            (filterPrev * 0.30 + flowWave + subWave + bubble) * env * 8500.0
                        }

                        SoundTheme.NATURE_HAVEN -> {
                            val fillFreq = 280.0 + progress * 180.0
                            val flow = sin(2.0 * Math.PI * fillFreq * i / sampleRate) * 0.4
                            val noise = random.nextDouble() * 2.0 - 1.0
                            filterPrev = filterPrev * 0.80 + noise * 0.20
                            val trickle = if (random.nextDouble() > 0.95) sin(2.0 * Math.PI * 550.0 * i / sampleRate) * 0.25 else 0.0
                            (flow + filterPrev * 0.25 + trickle) * env * 8000.0
                        }

                        SoundTheme.LIGHT_WIND -> {
                            val freq = 420.0 + sin(progress * Math.PI) * 120.0
                            val flow = sin(2.0 * Math.PI * freq * i / sampleRate) * 0.4
                            val chime = if (random.nextDouble() > 0.96) sin(2.0 * Math.PI * 880.0 * i / sampleRate) * 0.25 else 0.0
                            (flow + chime) * env * 8000.0
                        }

                        SoundTheme.PEACEFUL_PIANO -> {
                            val freq1 = 261.63 + progress * 130.0
                            val freq2 = 329.63 + progress * 130.0
                            val wave = (sin(2.0 * Math.PI * freq1 * i / sampleRate) + sin(2.0 * Math.PI * freq2 * i / sampleRate)) * 0.35
                            wave * env * 8500.0
                        }

                        SoundTheme.ZEN_MEDITATION -> {
                            val freq = 216.0 + progress * 108.0
                            val flow = sin(2.0 * Math.PI * freq * i / sampleRate) * 0.45
                            val bowlWave = sin(2.0 * Math.PI * 432.0 * i / sampleRate) * 0.25
                            (flow + bowlWave) * env * 8500.0
                        }
                    }

                    buffer[i] = sample.toInt().coerceIn(-32768, 32767).toShort()
                }

                playBufferStatic(buffer, sampleRate)
            } catch (_: Exception) {}
        }
    }

    /**
     * Plays bottle selection tap click.
     */
    fun playTapSound(theme: SoundTheme = equippedSoundTheme) {
        if (!isSoundEnabled) return
        scope.launch {
            try {
                val sampleRate = 44100
                val durationMs = 50
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val progress = i.toDouble() / numSamples
                    val env = (1.0 - progress)

                    val sample = when (theme) {
                        SoundTheme.GENTLE_WATER -> sin(2.0 * Math.PI * (520.0 - progress * 200.0) * i / sampleRate) * env * 6500.0
                        SoundTheme.NATURE_HAVEN -> sin(2.0 * Math.PI * (420.0 - progress * 150.0) * i / sampleRate) * env * 6500.0
                        SoundTheme.LIGHT_WIND -> sin(2.0 * Math.PI * 784.0 * i / sampleRate) * env * 6000.0
                        SoundTheme.PEACEFUL_PIANO -> sin(2.0 * Math.PI * 523.25 * i / sampleRate) * exp(-progress * 6.0) * 7000.0
                        SoundTheme.ZEN_MEDITATION -> sin(2.0 * Math.PI * 432.0 * i / sampleRate) * exp(-progress * 5.0) * 7000.0
                    }

                    buffer[i] = sample.toInt().toShort()
                }

                playBufferStatic(buffer, sampleRate)
            } catch (_: Exception) {}
        }
    }

    /**
     * Plays bottle complete sparkle chime.
     */
    fun playBottleCompleteSound(theme: SoundTheme = equippedSoundTheme) {
        if (!isSoundEnabled) return
        scope.launch {
            try {
                val sampleRate = 44100
                val durationMs = 450
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)

                val freqs = when (theme) {
                    SoundTheme.GENTLE_WATER -> listOf(392.00, 523.25, 659.25, 783.99)
                    SoundTheme.NATURE_HAVEN -> listOf(293.66, 369.99, 440.00, 587.33)
                    SoundTheme.LIGHT_WIND -> listOf(440.00, 554.37, 659.25, 880.00)
                    SoundTheme.PEACEFUL_PIANO -> listOf(261.63, 329.63, 392.00, 523.25)
                    SoundTheme.ZEN_MEDITATION -> listOf(216.00, 288.00, 360.00, 432.00)
                }

                for (i in 0 until numSamples) {
                    val progress = i.toDouble() / numSamples
                    val noteIndex = (progress * freqs.size).toInt().coerceIn(0, freqs.size - 1)
                    val freq = freqs[noteIndex]
                    val amplitude = sin(progress * Math.PI) * 7500.0
                    val sample = sin(2.0 * Math.PI * freq * i / sampleRate) * amplitude
                    buffer[i] = sample.toInt().toShort()
                }

                playBufferStatic(buffer, sampleRate)
            } catch (_: Exception) {}
        }
    }

    /**
     * Plays level clear victory celebration fanfare!
     */
    fun playVictorySound(theme: SoundTheme = equippedSoundTheme) {
        if (!isSoundEnabled) return
        scope.launch {
            try {
                val sampleRate = 44100
                val durationMs = 700
                val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
                val buffer = ShortArray(numSamples)

                val chordFreqs = when (theme) {
                    SoundTheme.GENTLE_WATER -> listOf(392.00, 493.88, 587.33, 783.99)
                    SoundTheme.NATURE_HAVEN -> listOf(293.66, 369.99, 440.00, 587.33)
                    SoundTheme.LIGHT_WIND -> listOf(440.00, 554.37, 659.25, 880.00)
                    SoundTheme.PEACEFUL_PIANO -> listOf(261.63, 329.63, 392.00, 523.25)
                    SoundTheme.ZEN_MEDITATION -> listOf(216.00, 288.00, 360.00, 432.00)
                }

                for (i in 0 until numSamples) {
                    val progress = i.toDouble() / numSamples
                    var combinedSample = 0.0
                    val env = sin(progress * Math.PI)
                    for (f in chordFreqs) {
                        combinedSample += sin(2.0 * Math.PI * f * i / sampleRate) * 1800.0
                    }
                    buffer[i] = (combinedSample * env).toInt().toShort()
                }

                playBufferStatic(buffer, sampleRate)
            } catch (_: Exception) {}
        }
    }

    private fun playBufferStatic(buffer: ShortArray, sampleRate: Int) {
        val minBufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(maxOf(buffer.size * 2, minBufferSize))
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(buffer, 0, buffer.size)
        audioTrack.play()

        val durationMs = (buffer.size.toLong() * 1000L) / sampleRate
        scope.launch {
            delay(durationMs + 200L)
            try {
                if (audioTrack.playState == AudioTrack.PLAYSTATE_PLAYING) {
                    audioTrack.stop()
                }
                audioTrack.release()
            } catch (_: Exception) {}
        }
    }
}


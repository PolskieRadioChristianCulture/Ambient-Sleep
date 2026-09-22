package com.example.audio

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object AudioSynthesizer {
    private const val TAG = "AudioSynthesizer"
    private const val SAMPLE_RATE = 22050 // Low CPU usage, perfect ambient fidelity

    fun getAudioFile(context: Context, fileName: String): File {
        val dir = File(context.filesDir, "ambient_audio")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return File(dir, fileName)
    }

    /**
     * Proactively synthesizes all ambient files if they do not exist.
     * This runs on a separate thread to keep the main thread completely smooth.
     */
    fun checkAndGenerateAll(context: Context) {
        Thread {
            try {
                Log.d(TAG, "Starting audio asset verification and synthesis...")
                val files = listOf(
                    "brown_noise.wav" to { generateBrownNoise() },
                    "rain.wav" to { generateRain() },
                    "fireplace.wav" to { generateFireplace() },
                    "ambient_pad.wav" to { generateAmbientPad() },
                    "binaural_delta.wav" to { generateBinauralDelta() },
                    "binaural_theta.wav" to { generateBinauralTheta() }
                )

                for ((fileName, generator) in files) {
                    val file = getAudioFile(context, fileName)
                    if (!file.exists() || file.length() < 1000) {
                        Log.d(TAG, "Generating $fileName...")
                        val data = generator()
                        writeWavFile(file, data.first, data.second, data.third)
                        Log.d(TAG, "Generated $fileName successfully. Size: ${file.length()} bytes")
                    }
                }
                Log.d(TAG, "All audio tracks checked and ready.")
            } catch (e: Exception) {
                Log.e(TAG, "Error generating synthetic ambient tracks", e)
            }
        }.start()
    }

    private fun writeWavFile(file: File, sampleRate: Int, channels: Int, data: ShortArray) {
        val bytesPerSample = 2
        val pcmDataSize = data.size * bytesPerSample
        val totalSize = 36 + pcmDataSize

        try {
            FileOutputStream(file).use { fos ->
                // RIFF Header
                fos.write("RIFF".toByteArray())
                fos.write(intToByteArray(totalSize))
                fos.write("WAVE".toByteArray())

                // fmt Subchunk
                fos.write("fmt ".toByteArray())
                fos.write(intToByteArray(16)) // subchunk size = 16 for PCM
                fos.write(shortToByteArray(1)) // audio format = 1 for PCM
                fos.write(shortToByteArray(channels.toShort()))
                fos.write(intToByteArray(sampleRate))
                fos.write(intToByteArray(sampleRate * channels * bytesPerSample)) // byte rate
                fos.write(shortToByteArray((channels * bytesPerSample).toShort())) // block align
                fos.write(shortToByteArray((bytesPerSample * 8).toShort())) // bits per sample

                // data Subchunk
                fos.write("data".toByteArray())
                fos.write(intToByteArray(pcmDataSize))

                // PCM bytes in Little Endian
                val byteBuffer = ByteArray(pcmDataSize)
                var index = 0
                for (value in data) {
                    byteBuffer[index++] = (value.toInt() and 0xff).toByte()
                    byteBuffer[index++] = ((value.toInt() shr 8) and 0xff).toByte()
                }
                fos.write(byteBuffer)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed writing WAV file: ${file.name}", e)
        }
    }

    private fun intToByteArray(value: Int): ByteArray {
        return byteArrayOf(
            (value and 0xff).toByte(),
            ((value shr 8) and 0xff).toByte(),
            ((value shr 16) and 0xff).toByte(),
            ((value shr 24) and 0xff).toByte()
        )
    }

    private fun shortToByteArray(value: Short): ByteArray {
        return byteArrayOf(
            (value.toInt() and 0xff).toByte(),
            ((value.toInt() shr 8) and 0xff).toByte()
        )
    }

    // 1. Brown Noise: 3.0s mono loop
    private fun generateBrownNoise(): Triple<Int, Int, ShortArray> {
        val duration = 3.5f
        val numSamples = (SAMPLE_RATE * duration).toInt()
        val data = ShortArray(numSamples)
        val random = java.util.Random()
        var accumulator = 0f

        for (i in 0 until numSamples) {
            val white = (random.nextFloat() * 2f - 1f)
            // Accumulate with a leak to create 1/f^2 Brown Noise
            accumulator = (0.975f * accumulator) + (0.025f * white)
            val scaled = (accumulator * 18000f).coerceIn(-32767f, 32767f)
            data[i] = scaled.toInt().toShort()
        }

        // Apply a small fade-in/fade-out at bounds to ensure clickless loop
        applyLoopFades(data)
        return Triple(SAMPLE_RATE, 1, data)
    }

    // 2. Rain Sound: Constant hiss plus intermittent click-clicks (droplets)
    private fun generateRain(): Triple<Int, Int, ShortArray> {
        val duration = 4.0f
        val numSamples = (SAMPLE_RATE * duration).toInt()
        val data = ShortArray(numSamples)
        val random = java.util.Random()
        var hissAccumulator = 0f

        for (i in 0 until numSamples) {
            val white = (random.nextFloat() * 2f - 1f)
            // Rain has standard soft background hiss (ambient high frequencies with body)
            hissAccumulator = (0.88f * hissAccumulator) + (0.12f * white)
            var current = hissAccumulator * 7000f

            // Crackles / raindrops hitting glass
            if (random.nextFloat() < 0.0035f) {
                current += (random.nextFloat() * 11000f)
            }

            val scaled = current.coerceIn(-32767f, 32767f)
            data[i] = scaled.toInt().toShort()
        }

        applyLoopFades(data)
        return Triple(SAMPLE_RATE, 1, data)
    }

    // 3. Crackling Fireplace: Low rumble wood crackle with loud sharp pops
    private fun generateFireplace(): Triple<Int, Int, ShortArray> {
        val duration = 4.0f
        val numSamples = (SAMPLE_RATE * duration).toInt()
        val data = ShortArray(numSamples)
        val random = java.util.Random()
        var rumbleAccumulator = 0f
        var popTrigger = 0f

        for (i in 0 until numSamples) {
            val white = (random.nextFloat() * 2f - 1f)
            rumbleAccumulator = (0.96f * rumbleAccumulator) + (0.04f * white)
            var current = rumbleAccumulator * 5000f

            // Trigger spontaneous popping
            if (random.nextFloat() < 0.0018f) {
                popTrigger = 1.0f
            }

            if (popTrigger > 0f) {
                // Sharp pop impulse decaying exponentially
                current += (random.nextFloat() * 2f - 1f) * popTrigger * 16000f
                popTrigger *= 0.982f // fast decay for crisp wood popping
            }

            val scaled = current.coerceIn(-32767f, 32767f)
            data[i] = scaled.toInt().toShort()
        }

        applyLoopFades(data)
        return Triple(SAMPLE_RATE, 1, data)
    }

    // 4. Calming Ambient Pad: Warm 4-voice C Major 7 chord synthesized droning
    private fun generateAmbientPad(): Triple<Int, Int, ShortArray> {
        val duration = 6.0f
        val numSamples = (SAMPLE_RATE * duration).toInt()
        val data = ShortArray(numSamples)
        
        // Frequencies for chord nodes: C3 (130.81Hz), G3 (196.0Hz), B3 (246.94Hz), E4 (329.63Hz)
        val freq1 = 130.81
        val freq2 = 196.00
        val freq3 = 246.94
        val freq4 = 329.63
        val twoPi = 2.0 * Math.PI

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE

            // LFO wave modulations for individual soundscapes
            val lfo1 = 0.5 + 0.3 * Math.sin(twoPi * 0.16 * t)
            val lfo2 = 0.4 + 0.3 * Math.cos(twoPi * 0.11 * t)
            val lfo3 = 0.5 + 0.2 * Math.sin(twoPi * 0.07 * t)
            val lfo4 = 0.35 + 0.25 * Math.cos(twoPi * 0.13 * t)

            // Sine wave oscillators
            val osc1 = Math.sin(twoPi * freq1 * t) * lfo1
            val osc2 = Math.sin(twoPi * freq2 * t) * lfo2
            val osc3 = Math.sin(twoPi * freq3 * t) * lfo3
            val osc4 = Math.sin(twoPi * freq4 * t) * lfo4

            val mixed = (osc1 + osc2 + osc3 + osc4) * 0.24
            // Saturation limit using tanh
            val saturated = Math.tanh(mixed)
            val scaled = (saturated * 19000f).coerceIn(-32767.0, 32767.0)
            data[i] = scaled.toInt().toShort()
        }

        applyLoopFades(data)
        return Triple(SAMPLE_RATE, 1, data)
    }

    // 5. Binaural Delta Beat: Left ear sine 100Hz, Right ear sine 103Hz (3Hz beat)
    private fun generateBinauralDelta(): Triple<Int, Int, ShortArray> {
        val duration = 5.0f
        val numSamples = (SAMPLE_RATE * duration).toInt()
        val data = ShortArray(numSamples * 2) // Stereo (Left, Right interleaving)
        val twoPi = 2.0 * Math.PI

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val sampleLeft = (Math.sin(twoPi * 100.0 * t) * 14000).toInt().toShort()
            val sampleRight = (Math.sin(twoPi * 103.0 * t) * 14000).toInt().toShort()

            data[i * 2] = sampleLeft
            data[i * 2 + 1] = sampleRight
        }

        applyLoopFadesStereo(data)
        return Triple(SAMPLE_RATE, 2, data)
    }

    // 6. Binaural Theta Beat: Left ear sine 100Hz, Right ear sine 106Hz (6Hz beat)
    private fun generateBinauralTheta(): Triple<Int, Int, ShortArray> {
        val duration = 5.0f
        val numSamples = (SAMPLE_RATE * duration).toInt()
        val data = ShortArray(numSamples * 2) // Stereo (Left, Right interleaving)
        val twoPi = 2.0 * Math.PI

        for (i in 0 until numSamples) {
            val t = i.toDouble() / SAMPLE_RATE
            val sampleLeft = (Math.sin(twoPi * 100.0 * t) * 14000).toInt().toShort()
            val sampleRight = (Math.sin(twoPi * 106.0 * t) * 14000).toInt().toShort()

            data[i * 2] = sampleLeft
            data[i * 2 + 1] = sampleRight
        }

        applyLoopFadesStereo(data)
        return Triple(SAMPLE_RATE, 2, data)
    }

    private fun applyLoopFades(data: ShortArray) {
        // Fade in first 1000 samples and fade out last 1000 samples to avoid pops when looping
        val length = data.size
        val fadeSamples = 1000
        for (i in 0 until fadeSamples) {
            val factor = i.toFloat() / fadeSamples
            data[i] = (data[i] * factor).toInt().toShort()
            data[length - 1 - i] = (data[length - 1 - i] * factor).toInt().toShort()
        }
    }

    private fun applyLoopFadesStereo(data: ShortArray) {
        val frameCount = data.size / 2
        val fadeFrames = 1000
        for (i in 0 until fadeFrames) {
            val factor = i.toFloat() / fadeFrames
            // Left
            data[i * 2] = (data[i * 2] * factor).toInt().toShort()
            // Right
            data[i * 2 + 1] = (data[i * 2 + 1] * factor).toInt().toShort()

            // Out Left
            val outLeftIdx = ((frameCount - 1 - i) * 2)
            data[outLeftIdx] = (data[outLeftIdx] * factor).toInt().toShort()
            // Out Right
            val outRightIdx = outLeftIdx + 1
            data[outRightIdx] = (data[outRightIdx] * factor).toInt().toShort()
        }
    }
}

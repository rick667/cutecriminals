package com.example.audio

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.min
import kotlin.math.max

/**
 * SoundManager: Centralized Audio Engine for the game.
 * 
 * Features:
 * 1. Hybrid SoundPool + Procedural Synthesis:
 *    - Tries to play custom audio assets from res/raw if provided by the user.
 *    - If a custom file is not present, automatically generates crystal-clear
 *      procedural PCM waveforms in-memory (clicks, metallic unscrew ticks, snaps,
 *      coin chimes, dialogue chirps, victory fanfares) with zero latency.
 * 2. Background Music (BGM) management via MediaPlayer with smooth volume and loop.
 * 3. User Preferences persistence (SFX volume, BGM volume, mute toggles).
 * 4. Safe lifecycle handlers (app pause/resume).
 */
object SoundManager {
    private const val TAG = "SoundManager"
    private const val PREFS_NAME = "game_audio_preferences"
    private const val KEY_SFX_ENABLED = "sfx_enabled"
    private const val KEY_MUSIC_ENABLED = "music_enabled"
    private const val KEY_SFX_VOLUME = "sfx_volume"
    private const val KEY_MUSIC_VOLUME = "music_volume"

    private const val SAMPLE_RATE = 44100

    private var appContext: Context? = null
    private var prefs: SharedPreferences? = null

    private var soundPool: SoundPool? = null
    private val rawSoundIds = ConcurrentHashMap<String, Int>()
    private val proceduralTracks = ConcurrentHashMap<SoundType, ByteArray>()

    private var bgmPlayer: MediaPlayer? = null
    private var currentBgmResId: Int? = null

    var isSfxEnabled: Boolean = true
        private set
    var isMusicEnabled: Boolean = true
        private set
    var sfxVolume: Float = 1.0f
        private set
    var musicVolume: Float = 0.8f
        private set

    private val audioExecutor = Executors.newSingleThreadExecutor()

    enum class SoundType(val rawResName: String) {
        BUTTON_CLICK("sfx_button_click"),
        SCREW_SELECT("sfx_screw_select"),
        SCREW_UNSCREW("sfx_screw_unscrew"),
        SCREW_PLACE("sfx_screw_place"),
        SCREW_FAIL("sfx_screw_fail"),
        PLATE_FALL("sfx_plate_fall"),
        COIN_COLLECT("sfx_coin_collect"),
        DIALOGUE_MARCEL("sfx_dialogue_marcel"),
        DIALOGUE_LUNA("sfx_dialogue_luna"),
        DIALOGUE_DUQUE("sfx_dialogue_duque"),
        VICTORY_FANFARE("sfx_victory"),
        GAME_OVER("sfx_game_over"),
        ALARM_WARNING("sfx_alarm_warning"),
        CHEST_OPEN("sfx_chest_open")
    }

    /**
     * Initialize the audio engine with Application or Activity context.
     * Pre-generates procedural audio buffers so playback is immediate.
     */
    fun init(context: Context) {
        if (appContext != null) return
        val application = context.applicationContext
        appContext = application

        prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs?.let {
            isSfxEnabled = it.getBoolean(KEY_SFX_ENABLED, true)
            isMusicEnabled = it.getBoolean(KEY_MUSIC_ENABLED, true)
            sfxVolume = it.getFloat(KEY_SFX_VOLUME, 1.0f)
            musicVolume = it.getFloat(KEY_MUSIC_VOLUME, 0.8f)
        }

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(8)
            .setAudioAttributes(audioAttributes)
            .build()

        // Pre-synthesize procedural sounds in background to avoid any startup stall
        audioExecutor.execute {
            preSynthesizeProceduralSounds()
            preloadCustomRawSounds(application)
        }
    }

    // --- Volume & Settings Controls ---

    fun setSfxEnabled(enabled: Boolean) {
        isSfxEnabled = enabled
        prefs?.edit()?.putBoolean(KEY_SFX_ENABLED, enabled)?.apply()
    }

    fun setMusicEnabled(enabled: Boolean) {
        isMusicEnabled = enabled
        prefs?.edit()?.putBoolean(KEY_MUSIC_ENABLED, enabled)?.apply()
        if (!enabled) {
            pauseBgm()
        } else {
            resumeBgm()
        }
    }

    fun setSfxVolume(volume: Float) {
        sfxVolume = volume.coerceIn(0f, 1f)
        prefs?.edit()?.putFloat(KEY_SFX_VOLUME, sfxVolume)?.apply()
    }

    fun setMusicVolume(volume: Float) {
        musicVolume = volume.coerceIn(0f, 1f)
        prefs?.edit()?.putFloat(KEY_MUSIC_VOLUME, musicVolume)?.apply()
        bgmPlayer?.let { player ->
            if (player.isPlaying) {
                val effectiveVol = if (isMusicEnabled) musicVolume else 0f
                player.setVolume(effectiveVol, effectiveVol)
            }
        }
    }

    // --- Sound Effects (SFX) Playback ---

    fun playSound(type: SoundType, pitchModifier: Float = 1.0f) {
        if (!isSfxEnabled || sfxVolume <= 0.01f) return

        val pool = soundPool
        val customSoundId = rawSoundIds[type.rawResName]

        // 1. If user supplied a custom sound file in res/raw, play it via SoundPool
        if (pool != null && customSoundId != null && customSoundId != 0) {
            val vol = sfxVolume
            pool.play(customSoundId, vol, vol, 1, 0, pitchModifier.coerceIn(0.5f, 2.0f))
            return
        }

        // 2. Fallback to procedural synthesis buffer
        val pcmData = proceduralTracks[type]
        if (pcmData != null) {
            playPcmStatic(pcmData, sfxVolume, pitchModifier)
        }
    }

    // Convenience triggers
    fun playButtonClick() = playSound(SoundType.BUTTON_CLICK)
    fun playScrewSelect() = playSound(SoundType.SCREW_SELECT)
    fun playScrewUnscrew() = playSound(SoundType.SCREW_UNSCREW)
    fun playScrewPlace() = playSound(SoundType.SCREW_PLACE)
    fun playScrewFail() = playSound(SoundType.SCREW_FAIL)
    fun playPlateFall() = playSound(SoundType.PLATE_FALL)
    fun playCoinCollect() = playSound(SoundType.COIN_COLLECT)
    fun playVictory() = playSound(SoundType.VICTORY_FANFARE)
    fun playGameOver() = playSound(SoundType.GAME_OVER)
    fun playAlarm() = playSound(SoundType.ALARM_WARNING)
    fun playChestOpen() = playSound(SoundType.CHEST_OPEN)

    fun playDialogueBlip(character: String) {
        when (character.lowercase()) {
            "marcel" -> playSound(SoundType.DIALOGUE_MARCEL)
            "luna" -> playSound(SoundType.DIALOGUE_LUNA)
            "duque", "poddle" -> playSound(SoundType.DIALOGUE_DUQUE)
            else -> playSound(SoundType.DIALOGUE_MARCEL)
        }
    }

    // --- Background Music (BGM) Playback ---

    fun playBgm(context: Context, rawResId: Int, isLooping: Boolean = true) {
        if (currentBgmResId == rawResId && bgmPlayer?.isPlaying == true) return
        stopBgm()

        currentBgmResId = rawResId
        try {
            val player = MediaPlayer.create(context.applicationContext, rawResId) ?: return
            bgmPlayer = player
            player.isLooping = isLooping
            val vol = if (isMusicEnabled) musicVolume else 0f
            player.setVolume(vol, vol)
            if (isMusicEnabled) {
                player.start()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing BGM", e)
        }
    }

    fun pauseBgm() {
        try {
            bgmPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error pausing BGM", e)
        }
    }

    fun resumeBgm() {
        if (!isMusicEnabled) return
        try {
            bgmPlayer?.let {
                if (!it.isPlaying) {
                    val vol = musicVolume
                    it.setVolume(vol, vol)
                    it.start()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error resuming BGM", e)
        }
    }

    fun stopBgm() {
        try {
            bgmPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
            bgmPlayer = null
            currentBgmResId = null
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping BGM", e)
        }
    }

    // --- Procedural PCM Waveform Synthesis ---

    private fun preSynthesizeProceduralSounds() {
        // Button Click: Short pop (800Hz down to 400Hz, 25ms)
        proceduralTracks[SoundType.BUTTON_CLICK] = generateChirpPcm(
            startFreq = 900.0, endFreq = 450.0, durationMs = 25, decay = true
        )

        // Screw Select: Metallic high frequency ping (2200Hz + 1100Hz harmonic, 40ms)
        proceduralTracks[SoundType.SCREW_SELECT] = generateHarmonicPcm(
            baseFreq = 1800.0, harmonicRatio = 2.4, durationMs = 40, decayRate = 18.0
        )

        // Screw Unscrew: Dual ratchet tick (80ms total with 2 micro-pulses)
        proceduralTracks[SoundType.SCREW_UNSCREW] = generateRatchetTickPcm()

        // Screw Place: Solid mechanical snap (600Hz down to 180Hz, 50ms)
        proceduralTracks[SoundType.SCREW_PLACE] = generateSnapPcm()

        // Screw Fail: Dull double-thump (160Hz and 120Hz, 80ms)
        proceduralTracks[SoundType.SCREW_FAIL] = generateChirpPcm(
            startFreq = 220.0, endFreq = 110.0, durationMs = 80, decay = true
        )

        // Plate Fall: Resonant chime (660Hz with soft ringing, 150ms)
        proceduralTracks[SoundType.PLATE_FALL] = generateHarmonicPcm(
            baseFreq = 587.33, harmonicRatio = 1.5, durationMs = 150, decayRate = 7.0
        )

        // Coin Collect: Two bright ascending notes (G6 1568Hz -> C7 2093Hz)
        proceduralTracks[SoundType.COIN_COLLECT] = generateArpeggioPcm(
            frequencies = doubleArrayOf(1567.98, 2093.00), noteDurationMs = 55
        )

        // Marcel Dialogue Blip: Warm playful chirp (520Hz)
        proceduralTracks[SoundType.DIALOGUE_MARCEL] = generateChirpPcm(
            startFreq = 480.0, endFreq = 540.0, durationMs = 35, decay = true
        )

        // Luna Dialogue Blip: Bright cheerful chirp (720Hz)
        proceduralTracks[SoundType.DIALOGUE_LUNA] = generateChirpPcm(
            startFreq = 680.0, endFreq = 780.0, durationMs = 35, decay = true
        )

        // Duque Dialogue Blip: Deeper smug chirp (320Hz)
        proceduralTracks[SoundType.DIALOGUE_DUQUE] = generateChirpPcm(
            startFreq = 340.0, endFreq = 290.0, durationMs = 45, decay = true
        )

        // Victory: Joyful 4-note ascending fanfare (C5, E5, G5, C6)
        proceduralTracks[SoundType.VICTORY_FANFARE] = generateArpeggioPcm(
            frequencies = doubleArrayOf(523.25, 659.25, 783.99, 1046.50), noteDurationMs = 120
        )

        // Game Over: Gentle 3-note descending sequence (E4, C4, A3)
        proceduralTracks[SoundType.GAME_OVER] = generateArpeggioPcm(
            frequencies = doubleArrayOf(329.63, 261.63, 220.00), noteDurationMs = 140
        )

        // Alarm Warning: Dual-tone warning pulse (880Hz / 660Hz)
        proceduralTracks[SoundType.ALARM_WARNING] = generateArpeggioPcm(
            frequencies = doubleArrayOf(880.0, 660.0), noteDurationMs = 80
        )

        // Chest Open: Mystery chime + lock click
        proceduralTracks[SoundType.CHEST_OPEN] = generateArpeggioPcm(
            frequencies = doubleArrayOf(440.0, 554.37, 659.25, 880.0), noteDurationMs = 70
        )
    }

    private fun preloadCustomRawSounds(context: Context) {
        val pool = soundPool ?: return
        for (type in SoundType.values()) {
            val resId = context.resources.getIdentifier(type.rawResName, "raw", context.packageName)
            if (resId != 0) {
                try {
                    val soundId = pool.load(context, resId, 1)
                    rawSoundIds[type.rawResName] = soundId
                    Log.d(TAG, "Custom sound loaded for ${type.rawResName} -> soundId: $soundId")
                } catch (e: Exception) {
                    Log.w(TAG, "Could not load custom raw sound ${type.rawResName}", e)
                }
            }
        }
    }

    // --- Audio Synthesis Utilities (16-bit Mono PCM @ 44.1kHz) ---

    private fun generateChirpPcm(
        startFreq: Double,
        endFreq: Double,
        durationMs: Int,
        decay: Boolean
    ): ByteArray {
        val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        val pcm = ByteArray(numSamples * 2)
        var phase = 0.0

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val currentFreq = startFreq + (endFreq - startFreq) * progress
            phase += 2.0 * PI * currentFreq / SAMPLE_RATE

            val envelope = if (decay) {
                // Exponential decay for clean acoustic feel without clipping clicks
                Math.exp(-5.0 * progress)
            } else {
                1.0
            }

            val sample = (sin(phase) * envelope * 0.8 * Short.MAX_VALUE).toInt().toShort()
            pcm[i * 2] = (sample.toInt() and 0x00FF).toByte()
            pcm[i * 2 + 1] = ((sample.toInt() shr 8) and 0x00FF).toByte()
        }
        return pcm
    }

    private fun generateHarmonicPcm(
        baseFreq: Double,
        harmonicRatio: Double,
        durationMs: Int,
        decayRate: Double
    ): ByteArray {
        val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        val pcm = ByteArray(numSamples * 2)
        var phase1 = 0.0
        var phase2 = 0.0

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            phase1 += 2.0 * PI * baseFreq / SAMPLE_RATE
            phase2 += 2.0 * PI * (baseFreq * harmonicRatio) / SAMPLE_RATE

            val envelope = Math.exp(-decayRate * progress)
            val sampleVal = (sin(phase1) * 0.7 + sin(phase2) * 0.3) * envelope * Short.MAX_VALUE
            val sample = sampleVal.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()

            pcm[i * 2] = (sample.toInt() and 0x00FF).toByte()
            pcm[i * 2 + 1] = ((sample.toInt() shr 8) and 0x00FF).toByte()
        }
        return pcm
    }

    private fun generateRatchetTickPcm(): ByteArray {
        // Two subtle metallic ticks spaced 30ms apart
        val durationMs = 70
        val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        val pcm = ByteArray(numSamples * 2)
        val tick1Start = 0
        val tick2Start = (SAMPLE_RATE * 0.035).toInt()

        for (i in 0 until numSamples) {
            var value = 0.0
            if (i >= tick1Start && i < tick2Start) {
                val t = (i - tick1Start).toDouble() / (SAMPLE_RATE * 0.02)
                if (t <= 1.0) {
                    val phase = 2.0 * PI * 1400.0 * (i - tick1Start) / SAMPLE_RATE
                    value += sin(phase) * Math.exp(-12.0 * t)
                }
            }
            if (i >= tick2Start) {
                val t = (i - tick2Start).toDouble() / (SAMPLE_RATE * 0.025)
                if (t <= 1.0) {
                    val phase = 2.0 * PI * 1650.0 * (i - tick2Start) / SAMPLE_RATE
                    value += sin(phase) * Math.exp(-12.0 * t)
                }
            }

            val sample = (value * 0.75 * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            pcm[i * 2] = (sample.toInt() and 0x00FF).toByte()
            pcm[i * 2 + 1] = ((sample.toInt() shr 8) and 0x00FF).toByte()
        }
        return pcm
    }

    private fun generateSnapPcm(): ByteArray {
        // Fast snap: brief burst of noise + low resonant punch
        val durationMs = 45
        val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        val pcm = ByteArray(numSamples * 2)
        var phase = 0.0

        for (i in 0 until numSamples) {
            val progress = i.toDouble() / numSamples
            val freq = 550.0 - 320.0 * progress
            phase += 2.0 * PI * freq / SAMPLE_RATE

            val noise = (Math.random() * 2.0 - 1.0) * (1.0 - progress) * 0.3
            val tone = sin(phase) * 0.7
            val envelope = Math.exp(-10.0 * progress)

            val sampleVal = (tone + noise) * envelope * Short.MAX_VALUE
            val sample = sampleVal.toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()

            pcm[i * 2] = (sample.toInt() and 0x00FF).toByte()
            pcm[i * 2 + 1] = ((sample.toInt() shr 8) and 0x00FF).toByte()
        }
        return pcm
    }

    private fun generateArpeggioPcm(frequencies: DoubleArray, noteDurationMs: Int): ByteArray {
        val totalDurationMs = frequencies.size * noteDurationMs
        val numSamples = (SAMPLE_RATE * (totalDurationMs / 1000.0)).toInt()
        val noteSamples = (SAMPLE_RATE * (noteDurationMs / 1000.0)).toInt()
        val pcm = ByteArray(numSamples * 2)

        var currentNoteIndex = 0
        var noteSampleCounter = 0
        var phase = 0.0

        for (i in 0 until numSamples) {
            if (noteSampleCounter >= noteSamples && currentNoteIndex < frequencies.size - 1) {
                currentNoteIndex++
                noteSampleCounter = 0
            }

            val freq = frequencies[currentNoteIndex]
            phase += 2.0 * PI * freq / SAMPLE_RATE

            val noteProgress = noteSampleCounter.toDouble() / noteSamples
            // Subtle decay per note
            val envelope = Math.exp(-3.5 * noteProgress)

            val sample = (sin(phase) * envelope * 0.75 * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()

            pcm[i * 2] = (sample.toInt() and 0x00FF).toByte()
            pcm[i * 2 + 1] = ((sample.toInt() shr 8) and 0x00FF).toByte()

            noteSampleCounter++
        }
        return pcm
    }

    private fun playPcmStatic(pcmData: ByteArray, volume: Float, pitchModifier: Float) {
        audioExecutor.execute {
            try {
                val effectiveSampleRate = (SAMPLE_RATE * pitchModifier.coerceIn(0.6f, 1.8f)).toInt()
                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(effectiveSampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(pcmData.size)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(pcmData, 0, pcmData.size)
                val safeVol = volume.coerceIn(0f, 1f)
                audioTrack.setVolume(safeVol)
                audioTrack.play()

                // Release AudioTrack after playback finishes
                val durationMs = (pcmData.size.toDouble() / (effectiveSampleRate * 2) * 1000).toLong() + 50
                Thread.sleep(durationMs)
                audioTrack.stop()
                audioTrack.release()
            } catch (e: Exception) {
                Log.w(TAG, "Procedural audio playback error", e)
            }
        }
    }
}

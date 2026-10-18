package com.educa.mathkids.presentation.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.SoundPool
import android.media.ToneGenerator
import android.os.SystemClock
import android.util.Log
import com.educa.mathkids.R
import com.educa.mathkids.domain.model.OperationType
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

/**
 * Gestor de efectos sonoros profesionales y didácticos para MateKids.
 * Integra SoundPool de baja latencia con archivos WAV de alta definición,
 * sintetizador musical con AudioTrack estático para notas y melodías (Do, Re, Mi, Fa, Sol, etc.)
 * y ToneGenerator como respaldo offline de alta disponibilidad.
 */
class SoundManager(private val context: Context) {

    private var soundPool: SoundPool? = null
    private var toneGenerator: ToneGenerator? = null
    var isSoundEnabled: Boolean = true

    // Sintetizador melódico con AudioTrack (MODE_STATIC) de latencia ultra baja
    private val musicalTracks = mutableMapOf<Int, AudioTrack>()
    // Frecuencias exactas en Hz para las notas musicales (C4, D4, E4, F4, G4, A4, B4, C5)
    private val noteFrequencies = doubleArrayOf(
        261.63, // 0: Do
        293.66, // 1: Re
        329.63, // 2: Mi
        349.23, // 3: Fa
        392.00, // 4: Sol
        440.00, // 5: La
        493.88, // 6: Si
        523.25  // 7: Do alto
    )

    // IDs de sonidos precargados en SoundPool
    private var soundIdCorrect: Int = 0
    private var soundIdIncorrect: Int = 0
    private var soundIdGoalComplete: Int = 0
    private var soundIdAddition: Int = 0
    private var soundIdSubtraction: Int = 0
    private var soundIdMultiplication: Int = 0
    private var soundIdDivision: Int = 0
    private var soundIdClick: Int = 0
    private var soundIdHint: Int = 0
    private var soundIdStart: Int = 0
    private var soundIdWelcome: Int = 0
    private var soundIdApplause: Int = 0

    private val loadedSounds = mutableSetOf<Int>()

    private var lastPlayTime: Long = 0L
    private val minDebounceIntervalMs = 70L

    init {
        initSoundPool()
        initToneGenerator()
        initMusicalSynth()
    }

    private fun initMusicalSynth() {
        try {
            val sampleRate = 44100
            val durationSec = 0.40
            val numSamples = (durationSec * sampleRate).toInt()

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val audioFormat = AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(sampleRate)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()

            for (noteIndex in noteFrequencies.indices) {
                val freq = noteFrequencies[noteIndex]
                val pcmData = ShortArray(numSamples)
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    // Envolvente de decaimiento natural estilo campana / marimba musical
                    val decay = exp(-7.2 * t)
                    val sample = (sin(2.0 * PI * freq * t) * 0.72 +
                            sin(4.0 * PI * freq * t) * 0.20 +
                            sin(6.0 * PI * freq * t) * 0.08) * decay
                    pcmData[i] = (sample.coerceIn(-1.0, 1.0) * 32767.0).toInt().toShort()
                }

                val track = AudioTrack.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setAudioFormat(audioFormat)
                    .setBufferSizeInBytes(pcmData.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(pcmData, 0, pcmData.size)
                musicalTracks[noteIndex] = track
            }
        } catch (e: Throwable) {
            Log.w("SoundManager", "Error al inicializar sintetizador melódico AudioTrack", e)
        }
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
                .build().apply {
                    setOnLoadCompleteListener { _, sampleId, status ->
                        if (status == 0) {
                            loadedSounds.add(sampleId)
                        }
                    }
                }

            soundPool?.let { pool ->
                soundIdCorrect = pool.load(context, R.raw.sound_correct, 1)
                soundIdIncorrect = pool.load(context, R.raw.sound_incorrect, 1)
                soundIdGoalComplete = pool.load(context, R.raw.sound_goal_complete, 1)
                soundIdAddition = pool.load(context, R.raw.sound_op_addition, 1)
                soundIdSubtraction = pool.load(context, R.raw.sound_op_subtraction, 1)
                soundIdMultiplication = pool.load(context, R.raw.sound_op_multiplication, 1)
                soundIdDivision = pool.load(context, R.raw.sound_op_division, 1)
                soundIdClick = pool.load(context, R.raw.sound_click, 1)
                soundIdHint = pool.load(context, R.raw.sound_hint, 1)
                soundIdStart = pool.load(context, R.raw.sound_start, 1)
                soundIdWelcome = pool.load(context, R.raw.sound_welcome, 1)
                soundIdApplause = pool.load(context, R.raw.sound_applause, 1)
            }
        } catch (e: Throwable) {
            Log.w("SoundManager", "No se pudo inicializar SoundPool", e)
        }
    }

    private fun initToneGenerator() {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 75)
        } catch (e: Throwable) {
            Log.w("SoundManager", "No se pudo inicializar ToneGenerator local", e)
        }
    }

    private fun shouldPlay(forceOverride: Boolean = false): Boolean {
        if (!isSoundEnabled) return false
        val now = try { SystemClock.elapsedRealtime() } catch (e: Throwable) { System.currentTimeMillis() }
        if (!forceOverride && now - lastPlayTime < minDebounceIntervalMs) {
            return false
        }
        lastPlayTime = now
        return true
    }

    private fun playSoundPoolTrack(soundId: Int, fallbackTone: Int, toneDurationMs: Int) {
        val pool = soundPool
        if (pool != null && loadedSounds.contains(soundId)) {
            val streamId = pool.play(soundId, 1f, 1f, 1, 0, 1f)
            if (streamId != 0) return
        }
        // Fallback inmediato con ToneGenerator
        try {
            toneGenerator?.startTone(fallbackTone, toneDurationMs)
        } catch (e: Throwable) {
            Log.w("SoundManager", "Error en fallback ToneGenerator", e)
        }
    }

    /**
     * 1. Sonido alegre y motivador al acertar un ejercicio.
     * Arpegio ascendente brillante (C5 -> E5 -> G5 -> C6).
     */
    fun playCorrect() {
        if (!shouldPlay(forceOverride = true)) return
        playSoundPoolTrack(soundIdCorrect, ToneGenerator.TONE_PROP_BEEP2, 180)
    }

    /**
     * 2. Sonido suave, dulce y no punitivo al no acertar.
     * Tono descendente de apoyo motivador.
     */
    fun playIncorrect() {
        if (!shouldPlay(forceOverride = true)) return
        playSoundPoolTrack(soundIdIncorrect, ToneGenerator.TONE_PROP_NACK, 220)
    }

    /**
     * 3. Sonido triunfal al cumplir la meta o completar el nivel/sesión.
     * Fanfarria de celebración festiva.
     */
    fun playGoalComplete() {
        if (!shouldPlay(forceOverride = true)) return
        playSoundPoolTrack(soundIdGoalComplete, ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 450)
    }

    /**
     * Alias de playGoalComplete para mantener compatibilidad con pantallas existentes.
     */
    fun playVictory() {
        playGoalComplete()
    }

    /**
     * Sonido festivo al desbloquear una nueva recompensa o medalla.
     */
    fun playReward() {
        playGoalComplete()
    }

    /**
     * 4. Sonido distintivo al seleccionar un botón de operación matemática.
     * Genera un efecto sonoro propio y adaptado según sea Suma, Resta, Multiplicación o División.
     */
    fun playOperationSelect(operation: OperationType) {
        if (!shouldPlay(forceOverride = true)) return
        when (operation) {
            OperationType.ADDITION -> playAddition()
            OperationType.SUBTRACTION -> playSubtraction()
            OperationType.MULTIPLICATION -> playMultiplication()
            OperationType.DIVISION -> playDivision()
        }
    }

    /**
     * Sonido alegre y saltarín al elegir Suma (+).
     */
    fun playAddition() {
        if (!shouldPlay(forceOverride = true)) return
        playSoundPoolTrack(soundIdAddition, ToneGenerator.TONE_PROP_BEEP, 70)
    }

    /**
     * Sonido ágil y nítido de madera al elegir Resta (-).
     */
    fun playSubtraction() {
        if (!shouldPlay(forceOverride = true)) return
        playSoundPoolTrack(soundIdSubtraction, ToneGenerator.TONE_PROP_BEEP, 70)
    }

    /**
     * Sonido mágico y enérgico de destello al elegir Multiplicación (×).
     */
    fun playMultiplication() {
        if (!shouldPlay(forceOverride = true)) return
        playSoundPoolTrack(soundIdMultiplication, ToneGenerator.TONE_PROP_ACK, 90)
    }

    /**
     * Sonido de burbuja cristalina al elegir División (÷).
     */
    fun playDivision() {
        if (!shouldPlay(forceOverride = true)) return
        playSoundPoolTrack(soundIdDivision, ToneGenerator.TONE_PROP_ACK, 90)
    }

    /**
     * Sonido corto y sutil para pulsaciones de botones generales.
     */
    fun playButtonClick() {
        if (!shouldPlay()) return
        playSoundPoolTrack(soundIdClick, ToneGenerator.TONE_PROP_BEEP, 40)
    }

    /**
     * Sonido mágico de campanilla al solicitar o mostrar una pista de Mate.
     */
    fun playHint() {
        if (!shouldPlay(forceOverride = true)) return
        playSoundPoolTrack(soundIdHint, ToneGenerator.TONE_PROP_ACK, 250)
    }

    /**
     * Sonido de despegue alegre al comenzar una aventura o juego.
     */
    fun playGameStart() {
        if (!shouldPlay(forceOverride = true)) return
        playSoundPoolTrack(soundIdStart, ToneGenerator.TONE_PROP_BEEP2, 350)
    }

    /**
     * Sonido celestial de bienvenida al iniciar MateKids.
     * Utiliza SoundPool si ya fue precargado, o MediaPlayer como respaldo inmediato
     * para garantizar que suene en el instante exacto en que se abre la aplicación.
     */
    fun playWelcome() {
        if (!isSoundEnabled) return
        val pool = soundPool
        if (pool != null && loadedSounds.contains(soundIdWelcome)) {
            val streamId = pool.play(soundIdWelcome, 1f, 1f, 1, 0, 1f)
            if (streamId != 0) return
        }
        // Respaldo de alta fidelidad con MediaPlayer para inicio inmediato
        try {
            android.media.MediaPlayer.create(context, R.raw.sound_welcome)?.apply {
                setOnCompletionListener { mp ->
                    try { mp.release() } catch (_: Throwable) {}
                }
                start()
            }
        } catch (e: Throwable) {
            Log.w("SoundManager", "Error al reproducir audio de bienvenida con MediaPlayer", e)
        }
    }

    /**
     * Sonido festivo de aplausos y ovación infantil al completar una racha de respuestas correctas.
     */
    fun playApplause() {
        if (!isSoundEnabled) return
        val pool = soundPool
        if (pool != null && loadedSounds.contains(soundIdApplause)) {
            val streamId = pool.play(soundIdApplause, 1f, 1f, 1, 0, 1f)
            if (streamId != 0) return
        }
        try {
            android.media.MediaPlayer.create(context, R.raw.sound_applause)?.apply {
                setOnCompletionListener { mp ->
                    try { mp.release() } catch (_: Throwable) {}
                }
                start()
            }
        } catch (e: Throwable) {
            Log.w("SoundManager", "Error al reproducir aplausos", e)
        }
    }

    /**
     * Reproduce una nota musical pura (0=Do, 1=Re, 2=Mi, 3=Fa, 4=Sol, etc.)
     * con timbre de campana/marimba y latencia cero para formar melodías infantiles.
     */
    fun playMusicalNote(noteIndex: Int) {
        if (!isSoundEnabled) return
        try {
            val track = musicalTracks[noteIndex]
            if (track != null) {
                track.pause()
                track.reloadStaticData()
                track.play()
                return
            }
        } catch (e: Throwable) {
            Log.w("SoundManager", "Error al reproducir nota melódica con AudioTrack", e)
        }

        // Respaldo inmediato mediante SoundPool variando pitch rate
        try {
            val rates = floatArrayOf(1.0f, 1.122f, 1.260f, 1.335f, 1.50f, 1.68f, 1.89f, 2.0f)
            val rate = rates.getOrElse(noteIndex) { 1.0f }
            val pool = soundPool
            if (pool != null && loadedSounds.contains(soundIdHint)) {
                pool.play(soundIdHint, 1f, 1f, 2, 0, rate)
            } else {
                playButtonClick()
            }
        } catch (e: Throwable) {
            Log.w("SoundManager", "Error al reproducir fallback de nota melódica", e)
        }
    }

    /**
     * Libera de forma segura los recursos del sistema al destruir o pausar.
     */
    fun release() {
        try {
            soundPool?.release()
            toneGenerator?.release()
            musicalTracks.values.forEach { track ->
                try {
                    track.stop()
                    track.release()
                } catch (_: Throwable) {}
            }
        } catch (e: Throwable) {
            Log.w("SoundManager", "Error liberando recursos de audio", e)
        } finally {
            soundPool = null
            toneGenerator = null
            musicalTracks.clear()
            loadedSounds.clear()
        }
    }
}

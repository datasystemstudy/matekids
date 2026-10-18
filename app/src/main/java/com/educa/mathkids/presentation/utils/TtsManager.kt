package com.educa.mathkids.presentation.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import com.educa.mathkids.domain.model.Exercise
import com.educa.mathkids.domain.model.MissingTarget
import com.educa.mathkids.domain.model.OperationType
import java.util.Locale

/**
 * Gestor oficial de síntesis de voz (Android Text-To-Speech) para MateKids
 * y su compañero infantil "Mate".
 *
 * Configurado especialmente con características vocales amigables para niños:
 * - Selección inteligente de voz dulce, cálida y de alta calidad (femenina/infantil offline).
 * - Tono animado y alegre (pitch 1.28f) para transmitir entusiasmo, afecto y cercanía.
 * - Velocidad moderada (speechRate 0.93f) para garantizar una comprensión clara sin aburrir.
 * - Cola de espera automática para que ningún mensaje (como el de bienvenida al iniciar)
 *   se pierda si el motor TTS tarda unos milisegundos en inicializar.
 */
class TtsManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech? = null
    var isInitialized: Boolean = false
        private set
    var isVoiceEnabled: Boolean = true

    // Cola de mensajes pendientes mientras el motor TTS inicializa
    private val pendingUtterances = mutableListOf<String>()

    // Listener para coordinar eventos al terminar de hablar
    var onSpeechFinished: (() -> Unit)? = null

    init {
        try {
            textToSpeech = TextToSpeech(context.applicationContext, this)
        } catch (e: Throwable) {
            Log.w("TtsManager", "No se pudo inicializar TextToSpeech", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val tts = textToSpeech ?: return

            // 1. Configurar idioma español latinoamericano
            // Se priorizan variantes latinoamericanas estándar (México, es-419, EE.UU. latino, Colombia)
            val preferredLatamLocales = listOf(
                Locale.forLanguageTag("es-MX"),  // México (estándar de doblaje y síntesis para Latinoamérica)
                Locale.forLanguageTag("es-419"), // Código UN M.49 para toda América Latina
                Locale.forLanguageTag("es-US"),  // Español latino de EE.UU.
                Locale.forLanguageTag("es-CO"),  // Colombia (dicción neutra)
                Locale.forLanguageTag("es")      // Fallback a español genérico
            )

            var selectedLocale = Locale.forLanguageTag("es-MX")
            var langConfigured = false
            for (loc in preferredLatamLocales) {
                val result = tts.setLanguage(loc)
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    selectedLocale = loc
                    langConfigured = true
                    Log.d("TtsManager", "Idioma configurado con éxito a español latino: $loc (resultado=$result)")
                    break
                }
            }
            if (!langConfigured) {
                tts.setLanguage(Locale.forLanguageTag("es"))
            }

            // 2. Selección inteligente de voz cálida, dulce y amigable para niños con acento latino
            configureChildFriendlyVoice(tts, selectedLocale)

            // 3. Tono y cadencia infantil: alegre, dulce y perfectamente comprensible
            tts.setPitch(1.28f)      // Tono elevado y animado (personaje infantil / mascota amigable)
            tts.setSpeechRate(0.93f)  // Velocidad pausada, clara y paciente para niños de 4 a 10 años

            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {
                    onSpeechFinished?.let { callback ->
                        try {
                            callback()
                        } catch (e: Throwable) {
                            Log.w("TtsManager", "Error en onSpeechFinished", e)
                        }
                    }
                }
                override fun onError(utteranceId: String?) {}
            })

            isInitialized = true
            Log.d("TtsManager", "TTS inicializado con éxito con voz amigable para niños en español latino")

            // 4. Reproducir automáticamente mensajes en cola (ej. saludo de bienvenida)
            val queued = pendingUtterances.toList()
            pendingUtterances.clear()
            for (queuedText in queued) {
                speak(queuedText, flush = false)
            }
        } else {
            Log.w("TtsManager", "Error al inicializar TTS (status=$status)")
            isInitialized = false
        }
    }

    /**
     * Preprocesa cualquier texto antes de ser enviado a TextToSpeech para asegurar
     * que la pronunciación de "MateKids" tenga la acentuación prosódica en "kids" ([Matekíds]),
     * evitando que el motor en español lo interprete como palabra llana ("ma-TE-kids").
     */
    fun processTextForPronunciation(text: String): String = Companion.processTextForPronunciation(text)

    companion object {
        /**
         * Lógica pura de normalización fonética para pronunciación de MateKids.
         * Forzar la sílaba tónica en "kids" (aguda con tilde: Matekíds).
         */
        fun processTextForPronunciation(text: String): String {
            if (text.isBlank()) return text
            return text
                .replace(Regex("(?i)\\bMate[- ]*kíds\\b"), "Matekíds")
                .replace(Regex("(?i)\\bMate[- ]*Kids\\b"), "Matekíds")
        }
    }

    /**
     * Examina las voces disponibles en el dispositivo y escoge la más dulce,
     * cálida y amigable para niños, priorizando fuertemente el acento latinoamericano
     * y voces femeninas de alta calidad sin conexión obligatoria.
     */
    private fun configureChildFriendlyVoice(tts: TextToSpeech, targetLocale: Locale) {
        try {
            val voices = tts.voices
            if (voices.isNullOrEmpty()) return

            val allSpanishVoices = voices.filter { voice ->
                voice.locale.language.equals("es", ignoreCase = true)
            }

            Log.d("TtsManager", "Total de voces en español encontradas: ${allSpanishVoices.size}")
            for (v in allSpanishVoices) {
                Log.d("TtsManager", "Voz disponible: name=${v.name}, locale=${v.locale}, networkReq=${v.isNetworkConnectionRequired}, quality=${v.quality}")
            }

            // Preferir voces sin conexión si están disponibles; si no, usar todas
            val candidateVoices = allSpanishVoices.filter { !it.isNetworkConnectionRequired }.ifEmpty { allSpanishVoices }

            val latamCountryCodes = setOf(
                "MX", "419", "US", "CO", "AR", "CL", "PE", "VE", "EC",
                "GT", "CU", "BO", "DO", "HN", "PY", "SV", "NI", "CR", "PA", "UY"
            )

            if (candidateVoices.isNotEmpty()) {
                val bestVoice = candidateVoices.maxByOrNull { voice ->
                    var score = 0
                    val nameLower = voice.name.lowercase()
                    val country = voice.locale.country.uppercase()
                    val features = voice.features ?: emptySet()

                    // Identificar si es explícitamente voz de España (castellano peninsular)
                    val isSpain = country == "ES" ||
                            nameLower.contains("es-es") ||
                            nameLower.contains("es_es") ||
                            nameLower.contains("-eed") ||
                            nameLower.contains("-eea")

                    // Identificar si es voz de Latinoamérica
                    val isLatam = country in latamCountryCodes ||
                            nameLower.contains("es-419") ||
                            nameLower.contains("es-mx") ||
                            nameLower.contains("es-us") ||
                            nameLower.contains("es-co") ||
                            nameLower.contains("es_419") ||
                            nameLower.contains("es_mx") ||
                            nameLower.contains("es_us") ||
                            (!isSpain && voice.locale.language.equals("es", ignoreCase = true))

                    // 1. Prioridad absoluta al acento latinoamericano (penalizar España drásticamente)
                    if (isLatam) score += 200
                    if (isSpain) score -= 300

                    // 2. Priorizar voces femeninas / dulces (tienen un timbre cálido y maternal/infantil)
                    val isFemale = nameLower.contains("female") ||
                            nameLower.contains("fem") ||
                            nameLower.contains("-sfb") || // Google TTS Spanish LatAm Female
                            nameLower.contains("-sfc") || // Google TTS Spanish LatAm Female alt
                            nameLower.contains("-sfd") || // Google TTS Spanish LatAm Female alt
                            features.any { it.contains("female", ignoreCase = true) }

                    val isMale = (nameLower.contains("male") || features.any { it.contains("male", ignoreCase = true) }) && !isFemale

                    if (isFemale) score += 70
                    if (isMale) score -= 40

                    // Bono adicional para voz femenina y latina
                    if (isLatam && isFemale) score += 50

                    // 3. Priorizar alta calidad de síntesis
                    if (voice.quality == Voice.QUALITY_VERY_HIGH) score += 25
                    else if (voice.quality == Voice.QUALITY_HIGH) score += 15

                    // 4. Baja latencia para interacción rápida
                    if (voice.latency == Voice.LATENCY_VERY_LOW || voice.latency == Voice.LATENCY_LOW) score += 10

                    // 5. Coincidencia con la región objetivo
                    if (country.equals(targetLocale.country, ignoreCase = true)) score += 15

                    score
                }

                bestVoice?.let {
                    tts.voice = it
                    Log.d("TtsManager", "Voz seleccionada: ${it.name} (locale=${it.locale}, calidad=${it.quality})")
                }
            }
        } catch (e: Throwable) {
            Log.w("TtsManager", "No se pudo seleccionar voz personalizada, usando configuración predeterminada", e)
        }
    }

    /**
     * Pronuncia un texto en voz alta si la voz está habilitada.
     * Si el motor TTS aún está cargando, encola el mensaje para emitirlo inmediatamente al iniciar.
     */
    fun speak(text: String, flush: Boolean = true) {
        if (!isVoiceEnabled) return
        val processedText = processTextForPronunciation(text)
        if (!isInitialized) {
            if (flush) {
                pendingUtterances.clear()
            }
            pendingUtterances.add(processedText)
            return
        }
        val tts = textToSpeech ?: return
        val queueMode = if (flush) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
        tts.speak(processedText, queueMode, null, "matekids_${System.currentTimeMillis()}")
    }

    fun isSpeaking(): Boolean = textToSpeech?.isSpeaking == true

    var currentLanguageCode: String = "es"
        private set

    /**
     * Cambia dinámicamente el idioma del motor Text-To-Speech entre español e inglés.
     */
    fun setLanguage(languageCode: String) {
        currentLanguageCode = languageCode
        val tts = textToSpeech ?: return
        if (!isInitialized) return
        try {
            if (languageCode.equals("en", ignoreCase = true)) {
                tts.setLanguage(Locale.US)
                configureEnglishVoice(tts)
                Log.d("TtsManager", "TTS cambiado exitosamente a inglés")
            } else {
                val esMxLocale = Locale.forLanguageTag("es-MX")
                tts.setLanguage(esMxLocale)
                configureChildFriendlyVoice(tts, esMxLocale)
                Log.d("TtsManager", "TTS cambiado exitosamente a español")
            }
        } catch (e: Throwable) {
            Log.w("TtsManager", "Error al cambiar idioma en TTS", e)
        }
    }

    private fun configureEnglishVoice(tts: TextToSpeech) {
        try {
            val voices = tts.voices ?: return
            val englishVoices = voices.filter { it.locale.language.equals("en", ignoreCase = true) }
            val candidateVoices = englishVoices.filter { !it.isNetworkConnectionRequired }.ifEmpty { englishVoices }
            val best = candidateVoices.maxByOrNull { v ->
                var s = 0
                val nl = v.name.lowercase()
                if (nl.contains("female") || nl.contains("fem")) s += 50
                if (v.quality == Voice.QUALITY_VERY_HIGH) s += 20
                if (v.locale.country.equals("US", ignoreCase = true)) s += 20
                s
            }
            best?.let { tts.voice = it }
        } catch (e: Throwable) {
            Log.w("TtsManager", "Error al configurar voz en inglés", e)
        }
    }

    /**
     * Lee la bienvenida oficial de MateKids al arrancar la app.
     */
    fun speakWelcome(
        text: String? = null,
        onFinished: (() -> Unit)? = null
    ) {
        if (onFinished != null) {
            onSpeechFinished = {
                onSpeechFinished = null
                onFinished()
            }
        }
        val welcomeSpeech = text ?: if (currentLanguageCode.equals("en", ignoreCase = true)) {
            "Welcome to Math-Adventures, where you will learn and explore the exciting world of mathematics in a fun and interactive way!"
        } else {
            "Bienvenidos a Mate-Aventuras, donde aprenderás a conocer el emocionante mundo de las matemáticas de una manera didáctica y divertida."
        }
        speak(welcomeSpeech, flush = false)
    }

    /**
     * Lee un número con dicción clara en español o inglés según el idioma activo.
     * Ejemplo: 5 -> "¡Cinco!" o "Five!"
     */
    fun speakNumber(number: Int) {
        val isEn = currentLanguageCode.equals("en", ignoreCase = true)
        val name = if (isEn) {
            when (number) {
                0 -> "Zero"
                1 -> "One"
                2 -> "Two"
                3 -> "Three"
                4 -> "Four"
                5 -> "Five"
                6 -> "Six"
                7 -> "Seven"
                8 -> "Eight"
                9 -> "Nine"
                10 -> "Ten"
                11 -> "Eleven"
                12 -> "Twelve"
                13 -> "Thirteen"
                14 -> "Fourteen"
                15 -> "Fifteen"
                16 -> "Sixteen"
                17 -> "Seventeen"
                18 -> "Eighteen"
                19 -> "Nineteen"
                20 -> "Twenty"
                else -> number.toString()
            }
        } else {
            when (number) {
                0 -> "Cero"
                1 -> "Uno"
                2 -> "Dos"
                3 -> "Tres"
                4 -> "Cuatro"
                5 -> "Cinco"
                6 -> "Seis"
                7 -> "Siete"
                8 -> "Ocho"
                9 -> "Nueve"
                10 -> "Diez"
                11 -> "Once"
                12 -> "Doce"
                13 -> "Trece"
                14 -> "Catorce"
                15 -> "Quince"
                16 -> "Dieciséis"
                17 -> "Diecisiete"
                18 -> "Dieciocho"
                19 -> "Diecinueve"
                20 -> "Veinte"
                else -> number.toString()
            }
        }
        val prefix = if (isEn) "" else "¡"
        val suffix = if (isEn) "!" else "!"
        speak("$prefix$name$suffix")
    }

    /**
     * Lee un ejercicio matemático de forma contextualizada, soportando operaciones
     * directas y ejercicios de completación con término faltante.
     */
    fun speakExercise(exercise: Exercise) {
        val isEn = currentLanguageCode.equals("en", ignoreCase = true)
        val opWord = when (exercise.operationType) {
            OperationType.ADDITION -> if (isEn) "plus" else "más"
            OperationType.SUBTRACTION -> if (isEn) "minus" else "menos"
            OperationType.MULTIPLICATION -> if (isEn) "times" else "por"
            OperationType.DIVISION -> if (isEn) "divided by" else "entre"
        }
        if (isEn) {
            when (exercise.missingTarget) {
                MissingTarget.RESULT -> speak("How much is ${exercise.operand1} $opWord ${exercise.operand2}?")
                MissingTarget.SECOND_OPERAND -> speak("What number is missing? ${exercise.operand1} $opWord what number is equal to ${exercise.operand2}?")
                MissingTarget.FIRST_OPERAND -> speak("What number is missing? What number $opWord ${exercise.operand2} is equal to ${exercise.operand1}?")
            }
            return
        }
        if (exercise.questionText.isNotEmpty()) {
            speak(exercise.questionText)
            return
        }
        when (exercise.missingTarget) {
            MissingTarget.RESULT -> speak("¿Cuánto es ${exercise.operand1} $opWord ${exercise.operand2}?")
            MissingTarget.SECOND_OPERAND -> speak("¿Qué número falta? ${exercise.operand1} $opWord qué número es igual a ${exercise.operand2}?")
            MissingTarget.FIRST_OPERAND -> speak("¿Qué número falta? Qué número $opWord ${exercise.operand2} es igual a ${exercise.operand1}?")
        }
    }

    /**
     * Lee la resolución completa y correcta del ejercicio (estándar o de completación).
     */
    fun speakExerciseResult(exercise: Exercise) {
        val isEn = currentLanguageCode.equals("en", ignoreCase = true)
        val opWord = when (exercise.operationType) {
            OperationType.ADDITION -> if (isEn) "plus" else "más"
            OperationType.SUBTRACTION -> if (isEn) "minus" else "menos"
            OperationType.MULTIPLICATION -> if (isEn) "times" else "por"
            OperationType.DIVISION -> if (isEn) "divided by" else "entre"
        }
        val solved = when (exercise.missingTarget) {
            MissingTarget.RESULT -> {
                if (isEn) "${exercise.operand1} $opWord ${exercise.operand2} is equal to ${exercise.correctAnswer}"
                else "${exercise.operand1} $opWord ${exercise.operand2} es igual a ${exercise.correctAnswer}"
            }
            MissingTarget.SECOND_OPERAND -> {
                if (isEn) "${exercise.operand1} $opWord ${exercise.correctAnswer} is equal to ${exercise.operand2}"
                else "${exercise.operand1} $opWord ${exercise.correctAnswer} es igual a ${exercise.operand2}"
            }
            MissingTarget.FIRST_OPERAND -> {
                if (isEn) "${exercise.correctAnswer} $opWord ${exercise.operand2} is equal to ${exercise.operand1}"
                else "${exercise.correctAnswer} $opWord ${exercise.operand2} es igual a ${exercise.operand1}"
            }
        }
        val praise = if (isEn) "Great job!" else "¡Muy bien!"
        speak("$praise $solved.")
    }

    /**
     * Lee una operación matemática de forma natural y didáctica para niños.
     * Ejemplo: "¿Cuánto es cinco más tres?"
     */
    fun speakEquation(operand1: Int, operationType: OperationType, operand2: Int) {
        val isEn = currentLanguageCode.equals("en", ignoreCase = true)
        val opWord = when (operationType) {
            OperationType.ADDITION -> if (isEn) "plus" else "más"
            OperationType.SUBTRACTION -> if (isEn) "minus" else "menos"
            OperationType.MULTIPLICATION -> if (isEn) "times" else "por"
            OperationType.DIVISION -> if (isEn) "divided by" else "entre"
        }
        if (isEn) {
            speak("How much is $operand1 $opWord $operand2?")
        } else {
            speak("¿Cuánto es $operand1 $opWord $operand2?")
        }
    }

    /**
     * Lee una felicitación tras responder.
     * Ejemplo: "¡Muy bien! Cinco más tres es igual a ocho."
     */
    fun speakEquationResult(operand1: Int, operationType: OperationType, operand2: Int, result: Int) {
        val isEn = currentLanguageCode.equals("en", ignoreCase = true)
        val opWord = when (operationType) {
            OperationType.ADDITION -> if (isEn) "plus" else "más"
            OperationType.SUBTRACTION -> if (isEn) "minus" else "menos"
            OperationType.MULTIPLICATION -> if (isEn) "times" else "por"
            OperationType.DIVISION -> if (isEn) "divided by" else "entre"
        }
        if (isEn) {
            speak("Great job! $operand1 $opWord $operand2 is equal to $result.")
        } else {
            speak("¡Muy bien! $operand1 $opWord $operand2 es igual a $result.")
        }
    }

    /**
     * Lee una pista o explicación educativa guiada por Mate.
     */
    fun speakHint(hintText: String) {
        speak(hintText)
    }

    /**
     * Detiene inmediatamente cualquier lectura en curso.
     */
    fun stop() {
        try {
            textToSpeech?.stop()
        } catch (e: Throwable) {
            Log.w("TtsManager", "Error al detener TTS", e)
        }
    }

    /**
     * Libera de forma segura los recursos nativos del motor TTS.
     */
    fun shutdown() {
        try {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        } catch (e: Throwable) {
            Log.w("TtsManager", "Error al cerrar TTS", e)
        } finally {
            textToSpeech = null
            isInitialized = false
            pendingUtterances.clear()
            onSpeechFinished = null
        }
    }
}

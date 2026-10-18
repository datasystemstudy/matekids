package com.educa.mathkids.domain.engine

import com.educa.mathkids.domain.model.BondElement
import com.educa.mathkids.domain.model.DidacticStrategy
import com.educa.mathkids.domain.model.Difficulty
import com.educa.mathkids.domain.model.EvaluationStage
import com.educa.mathkids.domain.model.Exercise
import com.educa.mathkids.domain.model.MissingTarget
import com.educa.mathkids.domain.model.OperationLevelConfig
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.model.VisualRepresentation
import com.educa.mathkids.domain.model.VisualTheme
import com.educa.mathkids.domain.model.VisualType
import java.util.UUID
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

/**
 * Contrato del generador de ejercicios matemáticos.
 * Lógica pura de Kotlin sin dependencias de Android UI ni Compose.
 */
interface ExerciseGenerator {
    fun generate(
        operation: OperationType,
        difficulty: Difficulty
    ): Exercise

    fun generateByLevel(
        operation: OperationType,
        level: Int
    ): Exercise

    fun generateByLevel(
        operation: OperationType,
        level: Int,
        includeCompletion: Boolean
    ): Exercise

    fun generateCompletionExercise(
        operation: OperationType,
        level: Int
    ): Exercise

    fun generateByStage(
        operation: OperationType,
        level: Int,
        stage: EvaluationStage
    ): Exercise

    fun generateDidacticExercise(
        operation: OperationType,
        strategy: DidacticStrategy,
        difficulty: Difficulty = Difficulty.EASY
    ): Exercise

    fun isValidExercise(exercise: Exercise): Boolean
}

/**
 * Implementación predeterminada del generador de ejercicios con 10 niveles progresivos
 * por cada operación matemática (Fases 2 a 7 del Prompt Maestro).
 */
class DefaultExerciseGenerator(
    private val maxRecentCacheSize: Int = 5
) : ExerciseGenerator {

    private val recentExercises = ArrayDeque<Triple<OperationType, Int, Int>>()

    override fun generate(
        operation: OperationType,
        difficulty: Difficulty
    ): Exercise {
        val config = DifficultyConfig.forDifficulty(difficulty)
        var op1 = 0
        var op2 = 0
        var ans = 0
        var attempts = 0
        do {
            val generated = when (operation) {
                OperationType.ADDITION -> {
                    val a = config.additionRange1.random()
                    val b = config.additionRange2.random()
                    Triple(a, b, a + b)
                }
                OperationType.SUBTRACTION -> {
                    val rawA = config.subtractionRange1.random()
                    val rawB = config.subtractionRange2.random()
                    val a = max(rawA, rawB)
                    val b = min(rawA, rawB)
                    Triple(a, b, a - b)
                }
                OperationType.MULTIPLICATION -> {
                    val a = config.multiplicationRange1.random()
                    val b = config.multiplicationRange2.random()
                    Triple(a, b, a * b)
                }
                OperationType.DIVISION -> {
                    val divisor = max(1, config.divisionDivisorRange.random())
                    val quotient = config.divisionQuotientRange.random()
                    val dividend = divisor * quotient
                    Triple(dividend, divisor, quotient)
                }
            }
            op1 = generated.first
            op2 = generated.second
            ans = generated.third
            val key = Triple(operation, op1, op2)
            val isRecent = recentExercises.contains(key)
            attempts++
        } while (isRecent && attempts < 25)

        val key = Triple(operation, op1, op2)
        if (recentExercises.size >= maxRecentCacheSize) {
            recentExercises.removeFirst()
        }
        recentExercises.addLast(key)

        val options = DistractorGenerator.generateOptions(ans, op1, op2, operation)
        val didactic = buildDidacticDetails(operation, op1, op2, ans, difficulty)
        return Exercise(
            id = UUID.randomUUID().toString(),
            operationType = operation,
            operand1 = op1,
            operand2 = op2,
            correctAnswer = ans,
            options = options,
            difficulty = difficulty,
            level = when (difficulty) {
                Difficulty.EASY -> 1
                Difficulty.BASIC -> 3
                Difficulty.INTERMEDIATE -> 5
                Difficulty.ADVANCED -> 8
            },
            questionText = didactic.questionText,
            visualRepresentation = didactic.visualRepresentation,
            explanation = didactic.explanation,
            displayEquation = "$op1 ${operation.symbol} $op2 = ?",
            didacticStrategy = didactic.strategy,
            missingTarget = MissingTarget.RESULT
        )
    }

    override fun generateByLevel(
        operation: OperationType,
        level: Int
    ): Exercise {
        return generateByLevel(operation, level, includeCompletion = true)
    }

    override fun generateByLevel(
        operation: OperationType,
        level: Int,
        includeCompletion: Boolean
    ): Exercise {
        val safeLevel = level.coerceIn(1, OperationLevelConfig.MAX_LEVELS_PER_OPERATION)
        var attempts = 0
        var exercise: Exercise

        // Probabilidad de ~35% de generar un ejercicio de completación para enriquecer el nivel
        val chooseCompletion = includeCompletion && (Random.nextFloat() < 0.35f)

        do {
            exercise = if (chooseCompletion) {
                generateCompletionExercise(operation, safeLevel)
            } else {
                when (operation) {
                    OperationType.ADDITION -> generateAdditionExercise(safeLevel)
                    OperationType.SUBTRACTION -> generateSubtractionExercise(safeLevel)
                    OperationType.MULTIPLICATION -> generateMultiplicationExercise(safeLevel)
                    OperationType.DIVISION -> generateDivisionExercise(safeLevel)
                }
            }

            val key = Triple(operation, exercise.operand1, exercise.operand2)
            val isRecent = recentExercises.contains(key)
            attempts++
        } while ((isRecent || !isValidExercise(exercise)) && attempts < 25)

        val key = Triple(operation, exercise.operand1, exercise.operand2)
        if (recentExercises.size >= maxRecentCacheSize) {
            recentExercises.removeFirst()
        }
        recentExercises.addLast(key)

        return exercise
    }

    override fun generateCompletionExercise(
        operation: OperationType,
        level: Int
    ): Exercise {
        val safeLevel = level.coerceIn(1, OperationLevelConfig.MAX_LEVELS_PER_OPERATION)
        return when (operation) {
            OperationType.ADDITION -> generateAdditionCompletionExercise(safeLevel)
            OperationType.SUBTRACTION -> generateSubtractionCompletionExercise(safeLevel)
            OperationType.MULTIPLICATION -> generateMultiplicationCompletionExercise(safeLevel)
            OperationType.DIVISION -> generateDivisionCompletionExercise(safeLevel)
        }
    }

    override fun generateByStage(
        operation: OperationType,
        level: Int,
        stage: EvaluationStage
    ): Exercise {
        val safeLevel = level.coerceIn(1, OperationLevelConfig.MAX_LEVELS_PER_OPERATION)
        return when (stage) {
            EvaluationStage.SIMPLE_SELECTION -> {
                // Etapa 1: Selección Simple (resolución directa eligiendo entre opciones)
                val base = generateByLevel(operation, safeLevel, includeCompletion = false)
                base.copy(
                    isKeypadAllowed = false,
                    evaluationStage = stage
                )
            }
            EvaluationStage.COMPLETION_SELECTION -> {
                // Etapa 2: Completación con Selección (completar incógnita eligiendo entre opciones)
                val base = generateCompletionExercise(operation, safeLevel)
                base.copy(
                    isKeypadAllowed = false,
                    evaluationStage = stage
                )
            }
            EvaluationStage.COMPLETION_WRITTEN -> {
                // Etapa 3: Completación Escrita (escribir la cantidad correcta con el teclado)
                val base = generateCompletionExercise(operation, safeLevel)
                base.copy(
                    isKeypadAllowed = true,
                    evaluationStage = stage
                )
            }
        }
    }

    // ==========================================
    // 🟦 1. GENERACIÓN DE SUMA (10 NIVELES)
    // ==========================================
    private fun generateAdditionExercise(level: Int): Exercise {
        val theme = pickRandomTheme()
        val emojiA = theme.randomEmoji()
        val emojiB = if (Random.nextBoolean()) theme.randomEmoji() else emojiA

        var op1 = 1
        var op2 = 1
        var visualType = VisualType.OBJECT_COUNT
        var question = ""
        var explanation = ""
        var isKeypad = false
        var story: String? = null
        var storyIcon = "➕"

        when (level) {
            1 -> { // 1+1, 2+1, 2+2
                op1 = (1..2).random()
                op2 = (1..2).random()
                visualType = VisualType.OBJECT_COUNT
                question = "¿Cuántos objetos hay en total?"
                explanation = "Juntamos $op1 $emojiA y $op2 $emojiB. Contamos: ¡En total son ${op1 + op2}!"
            }
            2 -> { // 3+2, 4+3, 5+2
                op1 = (3..5).random()
                op2 = (1..3).random()
                visualType = VisualType.OBJECT_COUNT
                question = "$op1 + $op2 = ?"
                explanation = "Tenemos $op1 $emojiA y sumamos $op2 $emojiB: ${op1} + ${op2} = ${op1 + op2}."
            }
            3 -> { // Hasta 10
                op1 = (3..6).random()
                op2 = (1..(10 - op1)).random()
                visualType = VisualType.NUMBER_FOCUS
                question = "$op1 + $op2 = ?"
                explanation = "$op1 más $op2 es igual a ${op1 + op2}."
            }
            4 -> { // Hasta 20
                op1 = (6..12).random()
                op2 = (3..8).random()
                visualType = VisualType.NUMBER_FOCUS
                question = "$op1 + $op2 = ?"
                explanation = "Sumando $op1 + $op2 obtenemos ${op1 + op2}."
            }
            5 -> { // Dos cifras sin llevar (24 + 15)
                val tens1 = (1..4).random()
                val units1 = (0..4).random()
                val tens2 = (1..4).random()
                val units2 = (0..(9 - units1)).random()
                op1 = tens1 * 10 + units1
                op2 = tens2 * 10 + units2
                visualType = VisualType.BASE_TEN_BLOCKS
                question = "$op1 + $op2 = ?"
                explanation = "Sumamos unidades ($units1 + $units2 = ${units1 + units2}) y decenas ($tens1 + $tens2 = ${tens1 + tens2}). Total: ${op1 + op2}."
            }
            6 -> { // Dos cifras con llevadas (28 + 15)
                val tens1 = (1..3).random()
                val units1 = (5..9).random()
                val tens2 = (1..3).random()
                val units2 = ((10 - units1)..9).random()
                op1 = tens1 * 10 + units1
                op2 = tens2 * 10 + units2
                visualType = VisualType.BASE_TEN_BLOCKS
                question = "$op1 + $op2 = ?"
                val totalUnits = units1 + units2
                explanation = "Unidades: $units1 + $units2 = $totalUnits (llevamos 1 decena a las ${tens1 + tens2} decenas). Total: ${op1 + op2}."
            }
            7 -> { // Tres cifras
                op1 = (100..450).random()
                op2 = (100..450).random()
                visualType = VisualType.NUMBER_FOCUS
                question = "$op1 + $op2 = ?"
                explanation = "Centenas, decenas y unidades: $op1 + $op2 = ${op1 + op2}."
                isKeypad = true
            }
            8 -> { // Problemas visuales
                op1 = (3..8).random()
                op2 = (2..6).random()
                visualType = VisualType.WORD_PROBLEM
                story = "Lucas tiene $op1 monedas 🪙 y su abuelo le regala $op2 monedas más. ¿Cuántas monedas tiene ahora?"
                storyIcon = "🪙"
                question = "¿Cuántas monedas tiene Lucas?"
                explanation = "Tenía $op1 monedas + $op2 monedas nuevas = ${op1 + op2} monedas en total."
            }
            9 -> { // Problemas matemáticos
                op1 = (12..25).random()
                op2 = (8..15).random()
                visualType = VisualType.WORD_PROBLEM
                story = "En una estación espacial viajan $op1 astronautas 🚀. En la base se acoplan $op2 astronautas más. ¿Cuántos astronautas hay en la misión?"
                storyIcon = "🚀"
                question = "¿Cuántos astronautas viajan en total?"
                explanation = "Había $op1 astronautas y se sumaron $op2: $op1 + $op2 = ${op1 + op2} astronautas."
            }
            10 -> { // Desafío Maestro I
                op1 = (15..45).random()
                op2 = (15..45).random()
                visualType = VisualType.NUMBER_FOCUS
                question = "$op1 + $op2 = ?"
                explanation = "¡Excelente cálculo mental! $op1 + $op2 = ${op1 + op2}."
                isKeypad = Random.nextBoolean()
            }
            in 11..15 -> {
                when (level) {
                    11 -> {
                        op1 = 10
                        op2 = (1..9).random()
                        visualType = VisualType.OBJECT_COUNT
                        question = "$op1 + $op2 = ?"
                        explanation = "Diez más $op2 unidades forman el número ${op1 + op2}."
                    }
                    12 -> {
                        op1 = 10
                        op2 = (2..9).random()
                        visualType = VisualType.OBJECT_COUNT
                        question = "10 monedas de $1 más $op2 monedas = ?"
                        explanation = "Sumamos las monedas: 10 + $op2 = ${op1 + op2}."
                    }
                    13 -> {
                        op1 = (3..9).random()
                        op2 = op1
                        visualType = VisualType.NUMBER_FOCUS
                        question = "¿Cuánto es el doble de $op1? ($op1 + $op1)"
                        explanation = "El doble de $op1 es $op1 + $op1 = ${op1 + op2}."
                    }
                    14 -> {
                        op1 = (4..8).random()
                        op2 = op1 + 1
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 + $op2 = ?"
                        explanation = "Dobles vecinos: $op1 + $op1 = ${op1 * 2}, más 1 = ${op1 + op2}."
                    }
                    else -> {
                        op1 = (5..8).random()
                        op2 = (2..6).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 + $op2 = ?"
                        explanation = "Completando marcos: $op1 + $op2 = ${op1 + op2}."
                    }
                }
            }
            in 16..20 -> {
                when (level) {
                    16 -> {
                        op1 = (4..10).random()
                        op2 = (3..7).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "En la recta numérica: desde $op1 salta +$op2 pasos"
                        explanation = "Dando un salto de $op2 desde $op1 llegamos a ${op1 + op2}."
                    }
                    17 -> {
                        op1 = (7..13).random()
                        op2 = (2..(20 - op1)).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 + $op2 = ?"
                        explanation = "$op1 + $op2 = ${op1 + op2}."
                    }
                    18 -> {
                        val a = (2..5).random()
                        val b = (2..5).random()
                        val c = (1..4).random()
                        op1 = a + b
                        op2 = c
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$a + $b + $c = ?"
                        explanation = "Sumamos: ($a + $b = ${a + b}) + $c = ${op1 + op2}."
                    }
                    19 -> {
                        op1 = (6..12).random()
                        op2 = (4..8).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 + $op2 = ?"
                        explanation = "Balanza de suma: $op1 + $op2 = ${op1 + op2}."
                    }
                    else -> {
                        op1 = (8..12).random()
                        op2 = (6..8).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "Gran Desafío 20: $op1 + $op2 = ?"
                        explanation = "¡Gran logro! $op1 + $op2 = ${op1 + op2}."
                        isKeypad = true
                    }
                }
            }
            in 21..25 -> {
                when (level) {
                    21 -> {
                        op1 = (1..4).random() * 10
                        op2 = (1..3).random() * 10
                        visualType = VisualType.BASE_TEN_BLOCKS
                        question = "$op1 + $op2 = ?"
                        explanation = "Sumamos decenas completas: $op1 + $op2 = ${op1 + op2}."
                    }
                    22 -> {
                        op1 = (2..5).random() * 10
                        op2 = (1..4).random() * 10
                        visualType = VisualType.BASE_TEN_BLOCKS
                        question = "$op1 + $op2 = ?"
                        explanation = "Bloques de decenas: $op1 + $op2 = ${op1 + op2}."
                    }
                    23 -> {
                        val tens = (2..5).random()
                        val units = (1..4).random()
                        op1 = tens * 10 + units
                        op2 = (1..(9 - units)).random()
                        visualType = VisualType.BASE_TEN_BLOCKS
                        question = "$op1 + $op2 = ?"
                        explanation = "Sumamos unidades: $units + $op2 = ${units + op2}. Total: ${op1 + op2}."
                    }
                    24 -> {
                        val tens = (3..6).random()
                        val units = (0..5).random()
                        op1 = tens * 10 + units
                        op2 = (1..(9 - units)).random()
                        visualType = VisualType.BASE_TEN_BLOCKS
                        question = "$op1 + $op2 = ?"
                        explanation = "Decenas y unidades: $op1 + $op2 = ${op1 + op2}."
                    }
                    else -> {
                        op1 = (2..6).random() * 10 + (1..8).random()
                        op2 = listOf(10, 20).random()
                        visualType = VisualType.BASE_TEN_BLOCKS
                        question = "$op1 + $op2 = ?"
                        explanation = "Sumamos decenas rápidas: $op1 + $op2 = ${op1 + op2}."
                    }
                }
            }
            in 26..30 -> {
                when (level) {
                    26 -> {
                        val tens1 = (2..5).random()
                        val u1 = (1..4).random()
                        val tens2 = (1..3).random()
                        val u2 = (1..(9 - u1)).random()
                        op1 = tens1 * 10 + u1
                        op2 = tens2 * 10 + u2
                        visualType = VisualType.BASE_TEN_BLOCKS
                        question = "$op1 + $op2 = ?"
                        explanation = "Unidades ($u1 + $u2) y decenas ($tens1 + $tens2): Total = ${op1 + op2}."
                    }
                    27 -> {
                        val tens1 = (2..4).random()
                        val u1 = (0..4).random()
                        val tens2 = (1..4).random()
                        val u2 = (0..(9 - u1)).random()
                        op1 = tens1 * 10 + u1
                        op2 = tens2 * 10 + u2
                        visualType = VisualType.BASE_TEN_BLOCKS
                        question = "$op1 + $op2 = ?"
                        explanation = "Columna por columna: $op1 + $op2 = ${op1 + op2}."
                    }
                    28 -> {
                        op1 = (15..45).random()
                        op2 = 9
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 + $op2 = ?"
                        explanation = "Sumar 9 es sumar 10 y restar 1: $op1 + 10 - 1 = ${op1 + op2}."
                    }
                    29 -> {
                        val tens1 = (1..4).random()
                        val u1 = (5..8).random()
                        val tens2 = (1..2).random()
                        val u2 = ((10 - u1)..5).random()
                        op1 = tens1 * 10 + u1
                        op2 = tens2 * 10 + u2
                        visualType = VisualType.BASE_TEN_BLOCKS
                        question = "$op1 + $op2 = ?"
                        explanation = "Unidades pasan de 10: $u1 + $u2 = ${u1 + u2}. Llevamos 1 decena. Total: ${op1 + op2}."
                    }
                    else -> {
                        val tens1 = (2..4).random()
                        val u1 = (5..9).random()
                        val tens2 = (1..3).random()
                        val u2 = ((10 - u1)..9).random()
                        op1 = tens1 * 10 + u1
                        op2 = tens2 * 10 + u2
                        visualType = VisualType.BASE_TEN_BLOCKS
                        question = "$op1 + $op2 = ?"
                        explanation = "Agrupamos la decena: $op1 + $op2 = ${op1 + op2}."
                    }
                }
            }
            in 31..35 -> {
                when (level) {
                    31 -> {
                        val tens1 = (3..5).random()
                        val u1 = (6..9).random()
                        val tens2 = (2..4).random()
                        val u2 = ((10 - u1)..9).random()
                        op1 = tens1 * 10 + u1
                        op2 = tens2 * 10 + u2
                        visualType = VisualType.BASE_TEN_BLOCKS
                        question = "$op1 + $op2 = ?"
                        explanation = "Unidades y decenas con llevada: $op1 + $op2 = ${op1 + op2}."
                    }
                    32 -> {
                        op1 = (24..58).random()
                        op2 = (15..38).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 + $op2 = ?"
                        explanation = "Cálculo mental: $op1 + $op2 = ${op1 + op2}."
                    }
                    33 -> {
                        op1 = (5..12).random()
                        op2 = (4..9).random()
                        visualType = VisualType.WORD_PROBLEM
                        story = "Lucas tiene $op1 carritos de carreras 🏎️ y en su cumpleaños le regalan $op2 carritos más. ¿Cuántos carritos tiene en total?"
                        storyIcon = "🏎️"
                        question = "¿Cuántos carritos tiene Lucas en total?"
                        explanation = "$op1 carritos + $op2 nuevos = ${op1 + op2} carritos."
                    }
                    34 -> {
                        op1 = (8..15).random()
                        op2 = (7..14).random()
                        visualType = VisualType.WORD_PROBLEM
                        story = "En la fiesta decoraron con $op1 globos azules 🎈 y agregaron $op2 globos dorados ⭐. ¿Cuántos globos adornan la fiesta?"
                        storyIcon = "🎈"
                        question = "¿Cuántos globos hay en la fiesta?"
                        explanation = "$op1 azules + $op2 dorados = ${op1 + op2} globos en total."
                    }
                    else -> {
                        op1 = (30..70).random()
                        op2 = (100 - op1)
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 + $op2 = ?"
                        explanation = "¡Llegamos a la centena! $op1 + $op2 = 100."
                    }
                }
            }
            in 36..40 -> {
                when (level) {
                    36 -> {
                        op1 = (1..5).random() * 100
                        op2 = (1..(9 - (op1 / 100))).random() * 100
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 + $op2 = ?"
                        explanation = "Centenas completas: $op1 + $op2 = ${op1 + op2}."
                    }
                    37 -> {
                        op1 = (1..3).random() * 100 + (1..4).random() * 10
                        op2 = (1..3).random() * 100 + (1..4).random() * 10
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 + $op2 = ?"
                        explanation = "Centenas y decenas: $op1 + $op2 = ${op1 + op2}."
                    }
                    38 -> {
                        op1 = (120..340).random()
                        op2 = (110..250).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 + $op2 = ?"
                        explanation = "Tres cifras sin llevar: $op1 + $op2 = ${op1 + op2}."
                        isKeypad = true
                    }
                    39 -> {
                        op1 = (145..385).random()
                        op2 = (128..278).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 + $op2 = ?"
                        explanation = "Tres cifras con llevada: $op1 + $op2 = ${op1 + op2}."
                        isKeypad = true
                    }
                    else -> {
                        op1 = (250..450).random()
                        op2 = (200..450).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 + $op2 = ?"
                        explanation = "Cálculo mayor de centenas: $op1 + $op2 = ${op1 + op2}."
                        isKeypad = true
                    }
                }
            }
            in 41..45 -> {
                when (level) {
                    41 -> {
                        op1 = (14..28).random()
                        op2 = (12..25).random()
                        visualType = VisualType.WORD_PROBLEM
                        story = "En el refugio cuidan a $op1 perritos 🐶. Hoy llegaron $op2 gatitos 🐱 rescatados. ¿Cuántos animalitos hay?"
                        storyIcon = "🐶"
                        question = "¿Cuántos animalitos hay en el refugio?"
                        explanation = "$op1 perritos + $op2 gatitos = ${op1 + op2} animalitos."
                    }
                    42 -> {
                        op1 = (16..35).random()
                        op2 = (15..30).random()
                        visualType = VisualType.WORD_PROBLEM
                        story = "Un observatorio detectó $op1 cometas brillantes ☄️ y luego descubrió $op2 asteroides 🌠. ¿Cuántos cuerpos celestes observó?"
                        storyIcon = "☄️"
                        question = "¿Cuántos cuerpos celestes observó en total?"
                        explanation = "$op1 cometas + $op2 asteroides = ${op1 + op2} cuerpos celestes."
                    }
                    43 -> {
                        op1 = (35..75).random()
                        op2 = (25..65).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 + $op2 = ?"
                        explanation = "Agilidad mental: $op1 + $op2 = ${op1 + op2}."
                        isKeypad = true
                    }
                    44 -> {
                        op1 = (40..85).random()
                        op2 = (30..75).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 + $op2 = ?"
                        explanation = "Pirámide numérica: $op1 + $op2 = ${op1 + op2}."
                    }
                    else -> {
                        op1 = (55..95).random()
                        op2 = (45..85).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 + $op2 = ?"
                        explanation = "Enigma de suma: $op1 + $op2 = ${op1 + op2}."
                    }
                }
            }
            else -> {
                when (level) {
                    46 -> {
                        op1 = (60..120).random()
                        op2 = (40..95).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 + $op2 = ?"
                        explanation = "¡Desafío de Campeones! $op1 + $op2 = ${op1 + op2}."
                        isKeypad = true
                    }
                    47 -> {
                        op1 = (25..45).random()
                        op2 = (20..40).random()
                        visualType = VisualType.WORD_PROBLEM
                        story = "En la feria de ciencias, $op1 alumnos presentaron proyectos de robótica 🤖 y $op2 alumnos de astronomía 🔭. ¿Cuántos alumnos expusieron en total?"
                        storyIcon = "🤖"
                        question = "¿Cuántos alumnos expusieron en la feria?"
                        explanation = "$op1 robótica + $op2 astronomía = ${op1 + op2} alumnos en total."
                    }
                    48 -> {
                        op1 = (120..350).random()
                        op2 = (110..290).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 + $op2 = ?"
                        explanation = "Torneo semifinal: $op1 + $op2 = ${op1 + op2}."
                        isKeypad = true
                    }
                    49 -> {
                        op1 = (200..480).random()
                        op2 = (150..390).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 + $op2 = ?"
                        explanation = "Prueba del Gran Sabio: $op1 + $op2 = ${op1 + op2}."
                        isKeypad = true
                    }
                    else -> {
                        op1 = (250..500).random()
                        op2 = (250..500).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "👑 Desafío Final Supremo: $op1 + $op2 = ?"
                        explanation = "¡CORONA DORADA DE SUMA! Cálculo perfecto: $op1 + $op2 = ${op1 + op2}."
                        isKeypad = true
                    }
                }
            }
        }

        val correctAnswer = op1 + op2
        val options = DistractorGenerator.generateOptions(correctAnswer, op1, op2, OperationType.ADDITION)

        val visualRep = VisualRepresentation(
            visualType = visualType,
            theme = theme,
            primaryEmoji = emojiA,
            secondaryEmoji = emojiB,
            countA = op1,
            countB = op2,
            tensA = op1 / 10,
            unitsA = op1 % 10,
            tensB = op2 / 10,
            unitsB = op2 % 10,
            problemStory = story,
            problemIcon = storyIcon
        )

        val didacticStrat = when {
            visualType == VisualType.WORD_PROBLEM -> DidacticStrategy.WORD_PROBLEM
            visualType == VisualType.BASE_TEN_BLOCKS -> DidacticStrategy.BASE_TEN_BLOCKS
            op1 > 20 || op2 > 20 || correctAnswer > 20 -> DidacticStrategy.BASE_TEN_BLOCKS
            visualType == VisualType.OBJECT_COUNT -> DidacticStrategy.CONCRETE_OBJECTS
            correctAnswer <= 10 -> DidacticStrategy.CONCRETE_OBJECTS
            else -> DidacticStrategy.TEN_FRAME
        }

        return Exercise(
            id = UUID.randomUUID().toString(),
            operationType = OperationType.ADDITION,
            operand1 = op1,
            operand2 = op2,
            correctAnswer = correctAnswer,
            options = options,
            difficulty = mapLevelToDifficulty(level),
            level = level,
            questionText = question,
            visualRepresentation = visualRep,
            explanation = explanation,
            isKeypadAllowed = isKeypad,
            didacticStrategy = didacticStrat
        )
    }

    // ==========================================
    // 🟩 2. GENERACIÓN DE RESTA (10 NIVELES)
    // ==========================================
    private fun generateSubtractionExercise(level: Int): Exercise {
        val theme = pickRandomTheme()
        val emoji = theme.randomEmoji()

        var op1 = 5
        var op2 = 2
        var visualType = VisualType.OBJECT_TAKEOUT
        var question = ""
        var explanation = ""
        var isKeypad = false
        var story: String? = null
        var storyIcon = "➖"

        when (level) {
            1 -> { // Restas hasta 5
                op1 = (2..5).random()
                op2 = (1 until op1).random()
                visualType = VisualType.OBJECT_TAKEOUT
                question = "¿Cuántos objetos quedan?"
                explanation = "Teníamos $op1 $emoji y tachamos/quitamos $op2: quedan ${op1 - op2} $emoji."
            }
            2 -> { // Restas hasta 10
                op1 = (5..10).random()
                op2 = (1..4).random()
                visualType = VisualType.OBJECT_TAKEOUT
                question = "$op1 - $op2 = ?"
                explanation = "De $op1 $emoji quitamos $op2: $op1 - $op2 = ${op1 - op2}."
            }
            3 -> { // Restas hasta 20
                op1 = (11..20).random()
                op2 = (3..8).random()
                visualType = VisualType.OBJECT_TAKEOUT
                question = "$op1 - $op2 = ?"
                explanation = "$op1 menos $op2 es igual a ${op1 - op2}."
            }
            4 -> { // Restas con apoyo visual
                op1 = (12..18).random()
                op2 = (4..9).random()
                visualType = VisualType.OBJECT_TAKEOUT
                question = "$op1 - $op2 = ?"
                explanation = "Restando paso a paso: $op1 - $op2 = ${op1 - op2}."
            }
            5 -> { // Dos cifras sin prestar (38 - 17)
                val tens1 = (3..6).random()
                val units1 = (4..9).random()
                val tens2 = (1 until tens1).random()
                val units2 = (0..units1).random()
                op1 = tens1 * 10 + units1
                op2 = tens2 * 10 + units2
                visualType = VisualType.BASE_TEN_BLOCKS
                question = "$op1 - $op2 = ?"
                explanation = "Unidades: $units1 - $units2 = ${units1 - units2}. Decenas: $tens1 - $tens2 = ${tens1 - tens2}. Total: ${op1 - op2}."
            }
            6 -> { // Dos cifras con préstamo (42 - 18)
                val tens1 = (3..6).random()
                val units1 = (0..5).random()
                val tens2 = (1 until tens1).random()
                val units2 = (units1 + 1..9).random()
                op1 = tens1 * 10 + units1
                op2 = tens2 * 10 + units2
                visualType = VisualType.BASE_TEN_BLOCKS
                question = "$op1 - $op2 = ?"
                val borrowedUnits = 10 + units1
                explanation = "Prestamos 1 decena: $borrowedUnits - $units2 = ${borrowedUnits - units2}. Decenas: ${tens1 - 1} - $tens2 = ${tens1 - 1 - tens2}. Total: ${op1 - op2}."
            }
            7 -> { // Tres cifras
                op1 = (250..600).random()
                op2 = (100..(op1 - 50)).random()
                visualType = VisualType.NUMBER_FOCUS
                question = "$op1 - $op2 = ?"
                explanation = "Resta de centenas, decenas y unidades: $op1 - $op2 = ${op1 - op2}."
                isKeypad = true
            }
            8 -> { // Problemas visuales
                op1 = (6..12).random()
                op2 = (2..(op1 - 1)).random()
                visualType = VisualType.WORD_PROBLEM
                story = "Había $op1 pajaritos 🐦 en un árbol. De repente, $op2 salieron volando. ¿Cuántos pajaritos quedaron en el árbol?"
                storyIcon = "🐦"
                question = "¿Cuántos pajaritos quedaron?"
                explanation = "Había $op1 y se fueron $op2: quedan ${op1 - op2} pajaritos."
            }
            9 -> { // Problemas cotidianos
                op1 = (18..35).random()
                op2 = (5..12).random()
                visualType = VisualType.WORD_PROBLEM
                story = "Valentina preparó $op1 deliciosas galletas 🍪. Compartió $op2 galletas con sus compañeros de clase. ¿Cuántas galletas le quedan a Valentina?"
                storyIcon = "🍪"
                question = "¿Cuántas galletas le quedan?"
                explanation = "Preparó $op1 galletas - $op2 que compartió = ${op1 - op2} galletas restantes."
            }
            10 -> { // Desafío Maestro I
                op1 = (30..70).random()
                op2 = (12..(op1 - 5)).random()
                visualType = VisualType.NUMBER_FOCUS
                question = "$op1 - $op2 = ?"
                explanation = "¡Excelente deducción! $op1 - $op2 = ${op1 - op2}."
                isKeypad = Random.nextBoolean()
            }
            in 11..15 -> {
                when (level) {
                    11 -> {
                        val num = (2..9).random()
                        if (Random.nextBoolean()) {
                            op1 = num
                            op2 = 0
                            question = "$op1 - 0 = ?"
                            explanation = "Al restar 0 no quitamos nada: $op1 - 0 = $op1."
                        } else {
                            op1 = num
                            op2 = num
                            question = "$op1 - $op1 = ?"
                            explanation = "Al restar el mismo número nos queda 0: $op1 - $op1 = 0."
                        }
                    }
                    12 -> {
                        op1 = 10
                        op2 = (1..9).random()
                        question = "10 - $op2 = ?"
                        explanation = "Amigos del 10: a 10 le quitamos $op2 y queda ${op1 - op2}."
                    }
                    13 -> {
                        op1 = (12..19).random()
                        op2 = 10
                        question = "$op1 - 10 = ?"
                        explanation = "Quitamos la decena completa: $op1 - 10 = ${op1 - op2}."
                    }
                    14 -> {
                        op1 = (11..18).random()
                        op2 = (3..7).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "En la recta numérica: desde $op1 retrocede -$op2 pasos"
                        explanation = "Dando un salto atrás de $op2 desde $op1 llegamos a ${op1 - op2}."
                    }
                    else -> {
                        val half = (3..9).random()
                        op1 = half * 2
                        op2 = half
                        visualType = VisualType.NUMBER_FOCUS
                        question = "¿Cuánto es la mitad de $op1? ($op1 - $op2)"
                        explanation = "Restando su mitad: $op1 - $op2 = $op2."
                    }
                }
            }
            in 16..20 -> {
                when (level) {
                    16 -> {
                        op1 = (11..19).random()
                        op2 = (3..8).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "¿Cuánto le lleva $op1 a $op2? ($op1 - $op2)"
                        explanation = "La diferencia entre $op1 y $op2 es ${op1 - op2}."
                    }
                    17 -> {
                        op1 = (12..19).random()
                        op2 = (4..9).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 - $op2 = ?"
                        explanation = "Equilibrio en la balanza: $op1 - $op2 = ${op1 - op2}."
                    }
                    18 -> {
                        op1 = (8..15).random()
                        op2 = (3..(op1 - 2)).random()
                        visualType = VisualType.WORD_PROBLEM
                        story = "Había $op1 golondrinas 🐦 en los cables de luz. $op2 volaron al nido. ¿Cuántas quedaron?"
                        storyIcon = "🐦"
                        question = "¿Cuántas golondrinas quedaron?"
                        explanation = "Había $op1 y volaron $op2: quedan ${op1 - op2} golondrinas."
                    }
                    19 -> {
                        op1 = (12..18).random()
                        op2 = (4..8).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 - $op2 = ?"
                        explanation = "Familia inversa: como ${op1 - op2} + $op2 = $op1, entonces $op1 - $op2 = ${op1 - op2}."
                    }
                    else -> {
                        op1 = (15..20).random()
                        op2 = (6..10).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "Gran Desafío 20: $op1 - $op2 = ?"
                        explanation = "¡Meta cumplida! $op1 - $op2 = ${op1 - op2}."
                        isKeypad = true
                    }
                }
            }
            in 21..25 -> {
                when (level) {
                    21 -> {
                        op1 = (3..7).random() * 10
                        op2 = (1..2).random() * 10
                        visualType = VisualType.BASE_TEN_BLOCKS
                        question = "$op1 - $op2 = ?"
                        explanation = "Restamos decenas completas: $op1 - $op2 = ${op1 - op2}."
                    }
                    22 -> {
                        op1 = (6..9).random() * 10
                        op2 = (2..5).random() * 10
                        visualType = VisualType.BASE_TEN_BLOCKS
                        question = "$op1 - $op2 = ?"
                        explanation = "Bloques verdes: $op1 - $op2 = ${op1 - op2}."
                    }
                    23 -> {
                        val tens = (2..6).random()
                        val u1 = (4..9).random()
                        op1 = tens * 10 + u1
                        op2 = (1..(u1 - 1)).random()
                        visualType = VisualType.BASE_TEN_BLOCKS
                        question = "$op1 - $op2 = ?"
                        explanation = "Restamos unidades: $u1 - $op2 = ${u1 - op2}. Decenas quedan intactas. Total: ${op1 - op2}."
                    }
                    24 -> {
                        val tens = (3..7).random()
                        val u1 = (5..9).random()
                        op1 = tens * 10 + u1
                        op2 = (2..(u1 - 1)).random()
                        visualType = VisualType.BASE_TEN_BLOCKS
                        question = "$op1 - $op2 = ?"
                        explanation = "Resta de unidades: $op1 - $op2 = ${op1 - op2}."
                    }
                    else -> {
                        op1 = (4..8).random() * 10 + (2..8).random()
                        op2 = listOf(10, 20, 30).random()
                        visualType = VisualType.BASE_TEN_BLOCKS
                        question = "$op1 - $op2 = ?"
                        explanation = "Restamos decenas rápidas: $op1 - $op2 = ${op1 - op2}."
                    }
                }
            }
            in 26..30 -> {
                when (level) {
                    26 -> {
                        val tens1 = (3..6).random()
                        val u1 = (4..9).random()
                        val tens2 = (1..(tens1 - 1)).random()
                        val u2 = (0..u1).random()
                        op1 = tens1 * 10 + u1
                        op2 = tens2 * 10 + u2
                        visualType = VisualType.BASE_TEN_BLOCKS
                        question = "$op1 - $op2 = ?"
                        explanation = "Unidades ($u1 - $u2) y decenas ($tens1 - $tens2): Total = ${op1 - op2}."
                    }
                    27 -> {
                        val tens1 = (4..7).random()
                        val u1 = (5..9).random()
                        val tens2 = (1..3).random()
                        val u2 = (1..u1).random()
                        op1 = tens1 * 10 + u1
                        op2 = tens2 * 10 + u2
                        visualType = VisualType.BASE_TEN_BLOCKS
                        question = "$op1 - $op2 = ?"
                        explanation = "Columna por columna: $op1 - $op2 = ${op1 - op2}."
                    }
                    28 -> {
                        op1 = (20..55).random()
                        op2 = 9
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 - $op2 = ?"
                        explanation = "Restar 9 es restar 10 y sumar 1: $op1 - 10 + 1 = ${op1 - op2}."
                    }
                    29 -> {
                        val tens1 = (2..5).random()
                        val u1 = (1..4).random()
                        op1 = tens1 * 10 + u1
                        op2 = (u1 + 2..9).random()
                        visualType = VisualType.BASE_TEN_BLOCKS
                        question = "$op1 - $op2 = ?"
                        explanation = "Préstamo de decena: desarmamos 1 decena para restar las unidades. Total = ${op1 - op2}."
                    }
                    else -> {
                        val tens1 = (3..6).random()
                        val u1 = (0..5).random()
                        val tens2 = (1..(tens1 - 1)).random()
                        val u2 = (u1 + 2..9).random()
                        op1 = tens1 * 10 + u1
                        op2 = tens2 * 10 + u2
                        visualType = VisualType.BASE_TEN_BLOCKS
                        question = "$op1 - $op2 = ?"
                        explanation = "Préstamo: $op1 - $op2 = ${op1 - op2}."
                    }
                }
            }
            in 31..35 -> {
                when (level) {
                    31 -> {
                        val tens1 = (4..7).random()
                        val u1 = (1..5).random()
                        val tens2 = (2..(tens1 - 1)).random()
                        val u2 = (u1 + 2..9).random()
                        op1 = tens1 * 10 + u1
                        op2 = tens2 * 10 + u2
                        visualType = VisualType.BASE_TEN_BLOCKS
                        question = "$op1 - $op2 = ?"
                        explanation = "Resta con préstamo: $op1 - $op2 = ${op1 - op2}."
                    }
                    32 -> {
                        op1 = (35..75).random()
                        op2 = (14..(op1 - 10)).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 - $op2 = ?"
                        explanation = "Cálculo mental deductivo: $op1 - $op2 = ${op1 - op2}."
                    }
                    33 -> {
                        op1 = (12..24).random()
                        op2 = (3..(op1 - 4)).random()
                        visualType = VisualType.WORD_PROBLEM
                        story = "En la panadería hornearon $op1 pastelitos 🧁. Vendieron $op2 durante la mañana. ¿Cuántos pastelitos quedan?"
                        storyIcon = "🧁"
                        question = "¿Cuántos pastelitos quedan en la vitrina?"
                        explanation = "Había $op1 pastelitos - $op2 vendidos = ${op1 - op2} restantes."
                    }
                    34 -> {
                        op1 = (15..35).random()
                        op2 = (6..(op1 - 5)).random()
                        visualType = VisualType.WORD_PROBLEM
                        story = "Mateo tenía $op1 monedas en su alcancía 🪙. Compró un autito de $op2 monedas. ¿Cuánto dinero le queda?"
                        storyIcon = "🪙"
                        question = "¿Cuántas monedas le quedan a Mateo?"
                        explanation = "Tenía $op1 - gastó $op2 = le quedan ${op1 - op2} monedas."
                    }
                    else -> {
                        op1 = 100
                        op2 = (1..8).random() * 10 + listOf(0, 5).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 - $op2 = ?"
                        explanation = "100 menos $op2 = ${op1 - op2}."
                    }
                }
            }
            in 36..40 -> {
                when (level) {
                    36 -> {
                        op1 = (3..8).random() * 100
                        op2 = (1..(op1 / 100 - 1)).random() * 100
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 - $op2 = ?"
                        explanation = "Centenas completas: $op1 - $op2 = ${op1 - op2}."
                    }
                    37 -> {
                        op1 = (3..6).random() * 100 + (3..8).random() * 10
                        op2 = (1..2).random() * 100 + (1..3).random() * 10
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 - $op2 = ?"
                        explanation = "Centenas y decenas: $op1 - $op2 = ${op1 - op2}."
                    }
                    38 -> {
                        val c1 = (3..6).random()
                        val d1 = (4..8).random()
                        val u1 = (4..9).random()
                        val c2 = (1..(c1 - 1)).random()
                        val d2 = (1..(d1 - 1)).random()
                        val u2 = (1..(u1 - 1)).random()
                        op1 = c1 * 100 + d1 * 10 + u1
                        op2 = c2 * 100 + d2 * 10 + u2
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 - $op2 = ?"
                        explanation = "Tres cifras sin prestar: $op1 - $op2 = ${op1 - op2}."
                        isKeypad = true
                    }
                    39 -> {
                        op1 = (350..650).random()
                        op2 = (120..(op1 - 100)).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 - $op2 = ?"
                        explanation = "Tres cifras con préstamo: $op1 - $op2 = ${op1 - op2}."
                        isKeypad = true
                    }
                    else -> {
                        op1 = (500..850).random()
                        op2 = (150..(op1 - 150)).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 - $op2 = ?"
                        explanation = "Gran salto de centenas: $op1 - $op2 = ${op1 - op2}."
                        isKeypad = true
                    }
                }
            }
            in 41..45 -> {
                when (level) {
                    41 -> {
                        op1 = (20..45).random()
                        op2 = (6..18).random()
                        visualType = VisualType.WORD_PROBLEM
                        story = "En la granja había $op1 ovejas pastando 🐑. Al atardecer, $op2 regresaron al corral. ¿Cuántas siguen en el campo?"
                        storyIcon = "🐑"
                        question = "¿Cuántas ovejas siguen en el campo?"
                        explanation = "Había $op1 - $op2 en el corral = quedan ${op1 - op2} ovejas."
                    }
                    42 -> {
                        op1 = (25..50).random()
                        op2 = (8..20).random()
                        visualType = VisualType.WORD_PROBLEM
                        story = "En un arrecife nadaban $op1 pececitos de colores 🐠. Un banco de $op2 nadó hacia aguas profundas. ¿Cuántos pececitos quedaron?"
                        storyIcon = "🐠"
                        question = "¿Cuántos pececitos quedaron en el arrecife?"
                        explanation = "$op1 pececitos - $op2 que se fueron = quedan ${op1 - op2} pececitos."
                    }
                    43 -> {
                        op1 = (50..95).random()
                        op2 = (15..(op1 - 15)).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 - $op2 = ?"
                        explanation = "Agilidad mental: $op1 - $op2 = ${op1 - op2}."
                        isKeypad = true
                    }
                    44 -> {
                        op1 = (60..100).random()
                        op2 = (20..(op1 - 15)).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 - $op2 = ?"
                        explanation = "Cascada numérica: $op1 - $op2 = ${op1 - op2}."
                    }
                    else -> {
                        op1 = (70..110).random()
                        op2 = (25..(op1 - 20)).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 - $op2 = ?"
                        explanation = "Enigma de resta: $op1 - $op2 = ${op1 - op2}."
                    }
                }
            }
            else -> {
                when (level) {
                    46 -> {
                        op1 = (80..150).random()
                        op2 = (30..(op1 - 25)).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 - $op2 = ?"
                        explanation = "¡Desafío de Campeones! $op1 - $op2 = ${op1 - op2}."
                        isKeypad = true
                    }
                    47 -> {
                        op1 = (35..65).random()
                        op2 = (12..28).random()
                        visualType = VisualType.WORD_PROBLEM
                        story = "Una biblioteca tenía $op1 libros infantiles en estantes 📚. Los estudiantes prestaron $op2 libros. ¿Cuántos libros quedan?"
                        storyIcon = "📚"
                        question = "¿Cuántos libros quedan en la biblioteca?"
                        explanation = "$op1 libros - $op2 prestados = ${op1 - op2} libros disponibles."
                    }
                    48 -> {
                        op1 = (200..450).random()
                        op2 = (80..(op1 - 60)).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 - $op2 = ?"
                        explanation = "Torneo semifinal: $op1 - $op2 = ${op1 - op2}."
                        isKeypad = true
                    }
                    49 -> {
                        op1 = (300..600).random()
                        op2 = (120..(op1 - 100)).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 - $op2 = ?"
                        explanation = "Prueba del Gran Guardián: $op1 - $op2 = ${op1 - op2}."
                        isKeypad = true
                    }
                    else -> {
                        op1 = (400..750).random()
                        op2 = (150..(op1 - 120)).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "👑 Desafío Final Supremo: $op1 - $op2 = ?"
                        explanation = "¡CORONA DORADA DE RESTA! Deducción perfecta: $op1 - $op2 = ${op1 - op2}."
                        isKeypad = true
                    }
                }
            }
        }

        val correctAnswer = op1 - op2
        val options = DistractorGenerator.generateOptions(correctAnswer, op1, op2, OperationType.SUBTRACTION)

        val visualRep = VisualRepresentation(
            visualType = visualType,
            theme = theme,
            primaryEmoji = emoji,
            totalCount = op1,
            removeCount = op2,
            tensA = op1 / 10,
            unitsA = op1 % 10,
            tensB = op2 / 10,
            unitsB = op2 % 10,
            problemStory = story,
            problemIcon = storyIcon,
            isSubtraction = true
        )

        val didacticStrat = when {
            visualType == VisualType.WORD_PROBLEM -> DidacticStrategy.WORD_PROBLEM
            visualType == VisualType.BASE_TEN_BLOCKS -> DidacticStrategy.BASE_TEN_BLOCKS
            op1 > 20 -> DidacticStrategy.BASE_TEN_BLOCKS
            visualType == VisualType.OBJECT_TAKEOUT -> DidacticStrategy.CONCRETE_OBJECTS
            op1 <= 10 -> DidacticStrategy.CONCRETE_OBJECTS
            else -> DidacticStrategy.NUMBER_LINE
        }

        return Exercise(
            id = UUID.randomUUID().toString(),
            operationType = OperationType.SUBTRACTION,
            operand1 = op1,
            operand2 = op2,
            correctAnswer = correctAnswer,
            options = options,
            difficulty = mapLevelToDifficulty(level),
            level = level,
            questionText = question,
            visualRepresentation = visualRep,
            explanation = explanation,
            isKeypadAllowed = isKeypad,
            didacticStrategy = didacticStrat
        )
    }

    // ==========================================
    // 🟪 3. GENERACIÓN DE MULTIPLICACIÓN (10 NIVELES)
    // ==========================================
    private fun generateMultiplicationExercise(level: Int): Exercise {
        val theme = pickRandomTheme()
        val emoji = theme.randomEmoji()

        var op1 = 2
        var op2 = 2
        var visualType = VisualType.GROUPS_GRID
        var question = ""
        var explanation = ""
        var isKeypad = false
        var story: String? = null
        var storyIcon = "✖️"

        when (level) {
            1 -> { // Grupos de 2
                op1 = (2..5).random()
                op2 = 2
                visualType = VisualType.GROUPS_GRID
                question = "$op1 grupos de 2 $emoji = ?"
                explanation = "$op1 veces 2: sumamos 2 repetidamente $op1 veces y obtenemos ${op1 * op2}."
            }
            2 -> { // Grupos de 3
                op1 = (2..5).random()
                op2 = 3
                visualType = VisualType.GROUPS_GRID
                question = "$op1 grupos de 3 $emoji = ?"
                explanation = "$op1 grupos con 3 objetos cada uno: $op1 × 3 = ${op1 * op2}."
            }
            3 -> { // Grupos de 4 y 5
                op1 = (2..5).random()
                op2 = listOf(4, 5).random()
                visualType = VisualType.GROUPS_GRID
                question = "$op1 × $op2 = ?"
                explanation = "Matriz de $op1 filas con $op2 elementos: $op1 × $op2 = ${op1 * op2}."
            }
            4 -> { // Tablas del 1 al 5
                op1 = (2..9).random()
                op2 = (1..5).random()
                visualType = VisualType.GROUPS_GRID
                question = "$op1 × $op2 = ?"
                explanation = "Tabla del $op2: $op1 veces $op2 es igual a ${op1 * op2}."
            }
            5 -> { // Tablas del 1 al 10
                op1 = (3..9).random()
                op2 = (3..9).random()
                visualType = VisualType.NUMBER_FOCUS
                question = "$op1 × $op2 = ?"
                explanation = "$op1 × $op2 = ${op1 * op2}."
            }
            6 -> { // Dos cifras por una cifra (12 × 3)
                op1 = (11..18).random()
                op2 = (2..5).random()
                visualType = VisualType.NUMBER_FOCUS
                question = "$op1 × $op2 = ?"
                val tens = op1 / 10
                val units = op1 % 10
                explanation = "Multiplicamos ($tens × 10 × $op2) + ($units × $op2) = ${tens * 10 * op2} + ${units * op2} = ${op1 * op2}."
            }
            7 -> { // Multiplicaciones de dos cifras
                op1 = (12..25).random()
                op2 = (3..6).random()
                visualType = VisualType.NUMBER_FOCUS
                question = "$op1 × $op2 = ?"
                explanation = "Cálculo mental: $op1 × $op2 = ${op1 * op2}."
                isKeypad = true
            }
            8 -> { // Problemas visuales
                op1 = (3..6).random()
                op2 = (2..5).random()
                visualType = VisualType.WORD_PROBLEM
                story = "En la juguetería hay $op1 cajas de sorpresas 🎁. Cada caja contiene $op2 autitos de carreras 🏎️. ¿Cuántos autitos hay en total?"
                storyIcon = "🎁"
                question = "¿Cuántos autitos hay en total?"
                explanation = "$op1 cajas × $op2 autitos en cada una = ${op1 * op2} autitos."
            }
            9 -> { // Problemas matemáticos
                op1 = (4..8).random()
                op2 = (6..9).random()
                visualType = VisualType.WORD_PROBLEM
                story = "Un agricultor plantó $op1 filas de árboles frutales 🌳. En cada fila crecen $op2 árboles sanos. ¿Cuántos árboles plantó el agricultor?"
                storyIcon = "🌳"
                question = "¿Cuántos árboles hay en total?"
                explanation = "$op1 filas × $op2 árboles por fila = ${op1 * op2} árboles."
            }
            10 -> { // Desafío final
                op1 = (6..12).random()
                op2 = (6..9).random()
                visualType = VisualType.NUMBER_FOCUS
                question = "$op1 × $op2 = ?"
                explanation = "¡Gran dominio de la multiplicación! $op1 × $op2 = ${op1 * op2}."
                isKeypad = Random.nextBoolean()
            }
            in 11..15 -> {
                when (level) {
                    11 -> { // Multiplicar por 1 y 0
                        op1 = (2..12).random()
                        op2 = listOf(0, 1).random()
                        visualType = VisualType.GROUPS_GRID
                        question = "$op1 × $op2 = ?"
                        explanation = if (op2 == 0) {
                            "Todo número multiplicado por cero es igual a cero: $op1 × 0 = 0."
                        } else {
                            "Multiplicar por 1 mantiene el mismo número: $op1 × 1 = $op1."
                        }
                    }
                    12 -> { // Tabla del 2 Completa
                        op1 = (1..10).random()
                        op2 = 2
                        visualType = VisualType.GROUPS_GRID
                        question = "$op1 × 2 = ?"
                        explanation = "Tabla del 2: $op1 veces 2 es igual a ${op1 * 2}."
                    }
                    13 -> { // Dobles y Duplicación
                        op1 = (3..12).random()
                        op2 = 2
                        visualType = VisualType.GROUPS_GRID
                        question = "¿Cuánto es el doble de $op1? ($op1 × 2)"
                        explanation = "El doble de un número es sumarlo consigo mismo: $op1 + $op1 = ${op1 * 2}."
                    }
                    14 -> { // Tabla del 3 Completa
                        op1 = (1..10).random()
                        op2 = 3
                        visualType = VisualType.GROUPS_GRID
                        question = "$op1 × 3 = ?"
                        explanation = "Tabla del 3: $op1 grupos de 3 son ${op1 * 3}."
                    }
                    else -> { // 15: Saltos en la Recta
                        op1 = (2..8).random()
                        op2 = listOf(2, 3, 5).random()
                        visualType = VisualType.NUMBER_LINE
                        question = "Dando $op1 saltos de $op2 en la recta numérica, ¿a qué número llegas?"
                        explanation = "$op1 saltos de tamaño $op2: $op1 × $op2 = ${op1 * op2}."
                    }
                }
            }
            in 16..20 -> {
                when (level) {
                    16 -> { // Tabla del 4 Completa
                        op1 = (1..10).random()
                        op2 = 4
                        visualType = VisualType.GROUPS_GRID
                        question = "$op1 × 4 = ?"
                        explanation = "Tabla del 4: $op1 veces 4 es igual a ${op1 * 4}."
                    }
                    17 -> { // Tabla del 5 Completa
                        op1 = (1..10).random()
                        op2 = 5
                        visualType = VisualType.GROUPS_GRID
                        question = "$op1 × 5 = ?"
                        explanation = "Tabla del 5: los múltiplos de 5 terminan en 0 o 5 ($op1 × 5 = ${op1 * 5})."
                    }
                    18 -> { // Cuadrículas y Matrices
                        op1 = (2..6).random()
                        op2 = (2..6).random()
                        visualType = VisualType.ARRAY_GRID
                        question = "Cuadrícula con $op1 filas y $op2 columnas: ¿cuántos elementos hay?"
                        explanation = "Matriz rectangular: $op1 filas × $op2 columnas = ${op1 * op2} casillas."
                    }
                    19 -> { // Propiedad Conmutativa
                        val a = (2..7).random()
                        val b = (2..7).random()
                        op1 = a
                        op2 = b
                        visualType = VisualType.GROUPS_GRID
                        question = "Si $b × $a = ${a * b}, ¿cuánto es $a × $b?"
                        explanation = "Propiedad conmutativa: cambiar el orden de los factores no altera el resultado ($a × $b = ${a * b})."
                    }
                    else -> { // 20: Gran Desafío 20
                        op1 = (2..9).random()
                        op2 = (2..5).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "Gran Desafío: $op1 × $op2 = ?"
                        explanation = "¡Excelente dominio de las primeras tablas! $op1 × $op2 = ${op1 * op2}."
                        isKeypad = true
                    }
                }
            }
            in 21..25 -> {
                when (level) {
                    21 -> { // Tabla del 10 Mágica
                        op1 = (2..12).random()
                        op2 = 10
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 × 10 = ?"
                        explanation = "Multiplicar por 10 es tan fácil como agregar un cero a la derecha: $op1 × 10 = ${op1 * 10}."
                    }
                    22 -> { // Tabla del 6 Básica
                        op1 = (1..5).random()
                        op2 = 6
                        visualType = VisualType.GROUPS_GRID
                        question = "$op1 × 6 = ?"
                        explanation = "Tabla del 6: $op1 veces 6 = ${op1 * 6}."
                    }
                    23 -> { // Tabla del 6 Completa
                        op1 = (6..10).random()
                        op2 = 6
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 × 6 = ?"
                        explanation = "Tabla del 6 avanzada: $op1 × 6 = ${op1 * 6}."
                    }
                    24 -> { // Tabla del 7 Básica
                        op1 = (1..5).random()
                        op2 = 7
                        visualType = VisualType.GROUPS_GRID
                        question = "$op1 × 7 = ?"
                        explanation = "Tabla del 7: $op1 semanas de 7 días son ${op1 * 7} días."
                    }
                    else -> { // 25: Tabla del 7 Completa
                        op1 = (6..10).random()
                        op2 = 7
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 × 7 = ?"
                        explanation = "Tabla del 7 avanzada: $op1 × 7 = ${op1 * 7}."
                    }
                }
            }
            in 26..30 -> {
                when (level) {
                    26 -> { // Tabla del 8 Básica
                        op1 = (1..5).random()
                        op2 = 8
                        visualType = VisualType.GROUPS_GRID
                        question = "$op1 × 8 = ?"
                        explanation = "Tabla del 8: $op1 pulpos con 8 tentáculos cada uno = ${op1 * 8} tentáculos."
                    }
                    27 -> { // Tabla del 8 Completa
                        op1 = (6..10).random()
                        op2 = 8
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 × 8 = ?"
                        explanation = "Tabla del 8: es el doble de la tabla del 4 ($op1 × 8 = ${op1 * 8})."
                    }
                    28 -> { // Tabla del 9 y Trucos
                        op1 = (1..5).random()
                        op2 = 9
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 × 9 = ?"
                        explanation = "Truco del 9: $op1 × 9 = ${op1 * 9}."
                    }
                    29 -> { // Tabla del 9 Completa
                        op1 = (6..10).random()
                        op2 = 9
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 × 9 = ?"
                        explanation = "Tabla del 9 avanzada: $op1 × 9 = ${op1 * 9}."
                    }
                    else -> { // 30: Tablas 1 al 10 Mixtas
                        op1 = (2..10).random()
                        op2 = (2..10).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 × $op2 = ?"
                        explanation = "¡Maestría completa en las tablas del 1 al 10! $op1 × $op2 = ${op1 * op2}."
                    }
                }
            }
            in 31..35 -> {
                when (level) {
                    31 -> { // Velocidad de Tablas
                        op1 = (3..9).random()
                        op2 = (3..9).random()
                        visualType = VisualType.NUMBER_FOCUS
                        isKeypad = true
                        question = "$op1 × $op2 = ?"
                        explanation = "¡Cálculo relámpago! $op1 × $op2 = ${op1 * op2}."
                    }
                    32 -> { // Multiplicar Decenas
                        op1 = listOf(20, 30, 40, 50).random()
                        op2 = (2..5).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 × $op2 = ?"
                        explanation = "Multiplicamos ${op1 / 10} × $op2 = ${(op1 / 10) * op2}, y agregamos cero: ${op1 * op2}."
                    }
                    33 -> { // Multiplicar por 100
                        op1 = (2..9).random()
                        op2 = 100
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 × 100 = ?"
                        explanation = "Al multiplicar por 100 agregamos dos ceros: $op1 × 100 = ${op1 * 100}."
                    }
                    34 -> { // Descomposición Sencilla
                        op1 = (11..15).random()
                        op2 = (2..4).random()
                        visualType = VisualType.NUMBER_FOCUS
                        val tens = op1 / 10
                        val units = op1 % 10
                        question = "$op1 × $op2 = ?"
                        explanation = "Descomponiendo: ($tens × 10 × $op2) + ($units × $op2) = ${tens * 10 * op2} + ${units * op2} = ${op1 * op2}."
                    }
                    else -> { // 35: Dos Cifras × Una Cifra
                        op1 = (12..25).random()
                        op2 = (2..4).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 × $op2 = ?"
                        explanation = "Cálculo vertical: $op1 × $op2 = ${op1 * op2}."
                    }
                }
            }
            in 36..40 -> {
                when (level) {
                    36 -> { // Dos Cifras con Llevada
                        op1 = (14..28).random()
                        op2 = (3..5).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 × $op2 = ?"
                        explanation = "Multiplicamos reagrupando decenas: $op1 × $op2 = ${op1 * op2}."
                    }
                    37 -> { // Cálculo Intermedio
                        op1 = (15..35).random()
                        op2 = (2..5).random()
                        visualType = VisualType.NUMBER_FOCUS
                        isKeypad = true
                        question = "$op1 × $op2 = ?"
                        explanation = "Agilidad mental: $op1 × $op2 = ${op1 * op2}."
                    }
                    38 -> { // Filas de Huertos
                        op1 = (3..7).random()
                        op2 = (4..8).random()
                        visualType = VisualType.WORD_PROBLEM
                        story = "En el huerto escolar se plantaron $op1 hileras de lechugas 🥬. Cada hilera tiene $op2 plantines. ¿Cuántos plantines hay?"
                        storyIcon = "🥬"
                        question = "¿Cuántos plantines de lechuga hay en el huerto?"
                        explanation = "$op1 hileras × $op2 plantines = ${op1 * op2} plantines en total."
                    }
                    39 -> { // Tienda de Juguetes
                        op1 = (3..8).random()
                        op2 = (3..6).random()
                        visualType = VisualType.WORD_PROBLEM
                        story = "En la juguetería organizaron $op1 cajas de bloques 🧱. En cada caja vienen $op2 paquetes. ¿Cuántos paquetes hay en total?"
                        storyIcon = "🧱"
                        question = "¿Cuántos paquetes de bloques hay en total?"
                        explanation = "$op1 cajas × $op2 paquetes = ${op1 * op2} paquetes."
                    }
                    else -> { // 40: Área de Cuadriláteros
                        op1 = (4..9).random()
                        op2 = (3..8).random()
                        visualType = VisualType.ARRAY_GRID
                        question = "Un patio rectangular mide $op1 metros de largo por $op2 metros de ancho. ¿Cuál es su área en m²?"
                        explanation = "Área = base × altura: $op1 × $op2 = ${op1 * op2} m²."
                    }
                }
            }
            in 41..45 -> {
                when (level) {
                    41 -> { // Factores Ocultos
                        op1 = (3..9).random()
                        op2 = (2..9).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "Si un factor es $op1 y el producto es ${op1 * op2}, ¿cuál es el otro factor?"
                        explanation = "El factor que falta es $op2, porque $op1 × $op2 = ${op1 * op2}."
                    }
                    42 -> { // Fábricas y Robots
                        op1 = (4..9).random()
                        op2 = (5..10).random()
                        visualType = VisualType.WORD_PROBLEM
                        story = "Una máquina robot 🤖 produce $op2 tornillos por minuto. Si funciona durante $op1 minutos continuos, ¿cuántos tornillos fabrica?"
                        storyIcon = "🤖"
                        question = "¿Cuántos tornillos fabrica el robot?"
                        explanation = "$op1 minutos × $op2 tornillos por minuto = ${op1 * op2} tornillos."
                    }
                    43 -> { // Asientos de Autobús
                        op1 = (5..10).random()
                        op2 = (4..6).random()
                        visualType = VisualType.WORD_PROBLEM
                        story = "Un autobús escolar 🚌 tiene $op1 filas de asientos, y en cada fila pueden sentarse $op2 niños. ¿A cuántos niños puede transportar?"
                        storyIcon = "🚌"
                        question = "¿Cuántos niños caben en el autobús?"
                        explanation = "$op1 filas × $op2 asientos = ${op1 * op2} pasajeros."
                    }
                    44 -> { // Doble y Triple
                        op1 = (10..30).random()
                        op2 = listOf(2, 3).random()
                        val label = if (op2 == 2) "el doble" else "el triple"
                        visualType = VisualType.NUMBER_FOCUS
                        question = "¿Cuánto es $label de $op1?"
                        explanation = "$label de $op1 es $op1 × $op2 = ${op1 * op2}."
                    }
                    else -> { // 45: Enigmas de Multiplicar
                        op1 = (6..12).random()
                        op2 = (6..9).random()
                        visualType = VisualType.NUMBER_FOCUS
                        isKeypad = true
                        question = "Enigma: $op1 × $op2 = ?"
                        explanation = "¡Gran deducción! $op1 × $op2 = ${op1 * op2}."
                    }
                }
            }
            in 46..50 -> {
                when (level) {
                    46 -> { // Desafío de Campeones I
                        op1 = (12..40).random()
                        op2 = (2..6).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "$op1 × $op2 = ?"
                        explanation = "Cálculo de campeón: $op1 × $op2 = ${op1 * op2}."
                    }
                    47 -> { // Desafío de Campeones II
                        op1 = (6..12).random()
                        op2 = (5..9).random()
                        visualType = VisualType.WORD_PROBLEM
                        story = "Para el campamento de ciencia 🏕️ armaron $op1 carpas. En cada carpa dormirán $op2 exploradores con sus mochilas. ¿Cuántos exploradores hay?"
                        storyIcon = "🏕️"
                        question = "¿Cuántos exploradores van al campamento?"
                        explanation = "$op1 carpas × $op2 exploradores = ${op1 * op2} participantes."
                    }
                    48 -> { // Torneo de Maestros
                        op1 = (15..45).random()
                        op2 = (3..6).random()
                        visualType = VisualType.NUMBER_FOCUS
                        isKeypad = true
                        question = "Torneo de Maestros: $op1 × $op2 = ?"
                        explanation = "¡Precisión impecable! $op1 × $op2 = ${op1 * op2}."
                    }
                    49 -> { // Prueba del Gran Fabricante
                        op1 = (20..50).random()
                        op2 = (3..7).random()
                        visualType = VisualType.NUMBER_FOCUS
                        isKeypad = true
                        question = "Gran Fabricante: $op1 × $op2 = ?"
                        explanation = "¡Dominio supremo del producto! $op1 × $op2 = ${op1 * op2}."
                    }
                    else -> { // 50: Maestro Supremo de la Fábrica
                        op1 = (15..50).random()
                        op2 = (4..9).random()
                        visualType = VisualType.NUMBER_FOCUS
                        isKeypad = true
                        question = "¡Desafío Legendario! $op1 × $op2 = ?"
                        explanation = "¡Corona Legendaria de la Fábrica de Grupos! $op1 × $op2 = ${op1 * op2}."
                    }
                }
            }
            else -> {
                op1 = (2..10).random()
                op2 = (2..10).random()
                visualType = VisualType.NUMBER_FOCUS
                question = "$op1 × $op2 = ?"
                explanation = "$op1 × $op2 = ${op1 * op2}."
            }
        }

        val correctAnswer = op1 * op2
        val options = DistractorGenerator.generateOptions(correctAnswer, op1, op2, OperationType.MULTIPLICATION)

        val visualRep = VisualRepresentation(
            visualType = visualType,
            theme = theme,
            primaryEmoji = emoji,
            groupCount = op1,
            itemsPerGroup = op2,
            problemStory = story,
            problemIcon = storyIcon
        )

        val didacticStrat = when {
            visualType == VisualType.WORD_PROBLEM -> DidacticStrategy.WORD_PROBLEM
            op1 > 10 || op2 > 10 -> DidacticStrategy.BASE_TEN_BLOCKS
            op1 <= 2 || op2 <= 2 -> DidacticStrategy.CONCRETE_OBJECTS
            op2 in listOf(3, 5) -> DidacticStrategy.SKIP_COUNTING
            else -> DidacticStrategy.ARRAY_GRID
        }

        return Exercise(
            id = UUID.randomUUID().toString(),
            operationType = OperationType.MULTIPLICATION,
            operand1 = op1,
            operand2 = op2,
            correctAnswer = correctAnswer,
            options = options,
            difficulty = mapLevelToDifficulty(level),
            level = level,
            questionText = question,
            visualRepresentation = visualRep,
            explanation = explanation,
            isKeypadAllowed = isKeypad,
            didacticStrategy = didacticStrat
        )
    }

    // ==========================================
    // 🟧 4. GENERACIÓN DE DIVISIÓN (10 NIVELES)
    // ==========================================
    private fun generateDivisionExercise(level: Int): Exercise {
        val theme = pickRandomTheme()
        val emoji = theme.randomEmoji()

        var divisor = 2
        var quotient = 2
        var visualType = VisualType.SHARING_PARTITION
        var question = ""
        var explanation = ""
        var isKeypad = false
        var story: String? = null
        var storyIcon = "➗"

        when (level) {
            1 -> { // Divisiones exactas muy sencillas (4 ÷ 2, 6 ÷ 2, 6 ÷ 3)
                divisor = listOf(2, 3).random()
                quotient = (2..3).random()
                visualType = VisualType.SHARING_PARTITION
                val dividend = divisor * quotient
                question = "Reparte $dividend $emoji en $divisor canastas iguales"
                explanation = "Distribuimos $dividend $emoji en $divisor recipientes: en cada uno quedan $quotient $emoji."
            }
            2 -> { // Divisiones hasta 10
                divisor = listOf(2, 3, 4).random()
                quotient = (2..(10 / divisor)).random()
                visualType = VisualType.SHARING_PARTITION
                val dividend = divisor * quotient
                question = "$dividend ÷ $divisor = ?"
                explanation = "$dividend repartido entre $divisor partes iguales es $quotient."
            }
            3 -> { // Divisiones hasta 20
                divisor = (2..5).random()
                quotient = (3..(20 / divisor)).random()
                visualType = VisualType.SHARING_PARTITION
                val dividend = divisor * quotient
                question = "$dividend ÷ $divisor = ?"
                explanation = "$dividend ÷ $divisor = $quotient (porque $divisor × $quotient = $dividend)."
            }
            4 -> { // Objetos en recipientes
                divisor = (3..6).random()
                quotient = (3..7).random()
                visualType = VisualType.SHARING_PARTITION
                val dividend = divisor * quotient
                question = "$dividend ÷ $divisor = ?"
                explanation = "Reparto equitativo: $dividend dividido entre $divisor recipientes = $quotient."
            }
            5 -> { // Tablas inversas
                divisor = (3..9).random()
                quotient = (3..9).random()
                visualType = VisualType.SHARING_PARTITION
                val dividend = divisor * quotient
                question = "¿Qué número multiplicado por $divisor da $dividend?"
                explanation = "$divisor × $quotient = $dividend, por lo tanto $dividend ÷ $divisor = $quotient."
            }
            6 -> { // Dos cifras entre una cifra
                divisor = (3..8).random()
                quotient = (5..12).random()
                visualType = VisualType.NUMBER_FOCUS
                val dividend = divisor * quotient
                question = "$dividend ÷ $divisor = ?"
                explanation = "$dividend ÷ $divisor = $quotient."
            }
            7 -> { // Divisiones complejas
                divisor = (4..9).random()
                quotient = (8..15).random()
                visualType = VisualType.NUMBER_FOCUS
                val dividend = divisor * quotient
                question = "$dividend ÷ $divisor = ?"
                explanation = "$dividend ÷ $divisor = $quotient."
                isKeypad = true
            }
            8 -> { // Problemas visuales
                divisor = (2..5).random()
                quotient = (3..6).random()
                val dividend = divisor * quotient
                visualType = VisualType.WORD_PROBLEM
                story = "La profesora tiene $dividend deliciosas manzanas 🍎 y las quiere repartir en partes iguales entre $divisor canastas 🧺. ¿Cuántas manzanas van en cada canasta?"
                storyIcon = "🧺"
                question = "¿Cuántas manzanas van en cada canasta?"
                explanation = "$dividend manzanas repartidas en $divisor canastas = $quotient manzanas por canasta."
            }
            9 -> { // Problemas cotidianos
                divisor = (3..6).random()
                quotient = (4..8).random()
                val dividend = divisor * quotient
                visualType = VisualType.WORD_PROBLEM
                story = "En la clase de robótica hay $dividend piezas de engranaje ⚙️ para repartir en partes iguales entre $divisor equipos. ¿Cuántas piezas recibe cada equipo?"
                storyIcon = "⚙️"
                question = "¿Cuántas piezas recibe cada equipo?"
                explanation = "$dividend piezas ÷ $divisor equipos = $quotient piezas para cada uno."
            }
            10 -> { // Desafío final
                divisor = (4..9).random()
                quotient = (7..14).random()
                val dividend = divisor * quotient
                visualType = VisualType.NUMBER_FOCUS
                question = "$dividend ÷ $divisor = ?"
                explanation = "¡Gran maestría en la división! $dividend ÷ $divisor = $quotient."
                isKeypad = Random.nextBoolean()
            }
            in 11..15 -> {
                when (level) {
                    11 -> { // Repartir entre 2
                        divisor = 2
                        quotient = (2..10).random()
                        visualType = VisualType.SHARING_PARTITION
                        question = "Reparte ${divisor * quotient} $emoji en 2 grupos iguales"
                        explanation = "${divisor * quotient} ÷ 2 = $quotient para cada grupo."
                    }
                    12 -> { // Repartir entre 3
                        divisor = 3
                        quotient = (2..10).random()
                        visualType = VisualType.SHARING_PARTITION
                        question = "Reparte ${divisor * quotient} $emoji en 3 canastas iguales"
                        explanation = "${divisor * quotient} ÷ 3 = $quotient en cada canasta."
                    }
                    13 -> { // Dividir entre 1 y Sí Mismo
                        if (Random.nextBoolean()) {
                            divisor = 1
                            quotient = (2..10).random()
                            question = "$quotient ÷ 1 = ?"
                            explanation = "Cualquier número dividido entre 1 da el mismo número: $quotient ÷ 1 = $quotient."
                        } else {
                            divisor = (2..10).random()
                            quotient = 1
                            question = "$divisor ÷ $divisor = ?"
                            explanation = "Cualquier número dividido entre sí mismo da 1: $divisor ÷ $divisor = 1."
                        }
                        visualType = VisualType.SHARING_PARTITION
                    }
                    14 -> { // Repartir entre 4
                        divisor = 4
                        quotient = (2..10).random()
                        visualType = VisualType.SHARING_PARTITION
                        question = "${divisor * quotient} ÷ 4 = ?"
                        explanation = "Reparto en 4 partes iguales: ${divisor * quotient} ÷ 4 = $quotient."
                    }
                    else -> { // 15: Repartir entre 5
                        divisor = 5
                        quotient = (2..10).random()
                        visualType = VisualType.SHARING_PARTITION
                        question = "${divisor * quotient} ÷ 5 = ?"
                        explanation = "Reparto en 5 partes iguales: ${divisor * quotient} ÷ 5 = $quotient."
                    }
                }
            }
            in 16..20 -> {
                when (level) {
                    16 -> { // Saltos hacia Atrás
                        divisor = (2..5).random()
                        quotient = (2..8).random()
                        visualType = VisualType.NUMBER_LINE
                        question = "Desde ${divisor * quotient}, dando saltos atrás de $divisor en $divisor, ¿cuántos saltos das hasta llegar a 0?"
                        explanation = "Retrocedemos $quotient veces el paso $divisor: ${divisor * quotient} ÷ $divisor = $quotient."
                    }
                    17 -> { // Inversa de la Tabla del 2
                        divisor = 2
                        quotient = (4..12).random()
                        visualType = VisualType.SHARING_PARTITION
                        question = "¿Qué número multiplicado por 2 da ${divisor * quotient}?"
                        explanation = "Como 2 × $quotient = ${divisor * quotient}, entonces ${divisor * quotient} ÷ 2 = $quotient."
                    }
                    18 -> { // Inversa de la Tabla del 3
                        divisor = 3
                        quotient = (3..12).random()
                        visualType = VisualType.SHARING_PARTITION
                        question = "¿Qué número multiplicado por 3 da ${divisor * quotient}?"
                        explanation = "3 × $quotient = ${divisor * quotient}, por ende ${divisor * quotient} ÷ 3 = $quotient."
                    }
                    19 -> { // Inversa de la Tabla del 4
                        divisor = 4
                        quotient = (3..10).random()
                        visualType = VisualType.SHARING_PARTITION
                        question = "${divisor * quotient} ÷ 4 = ?"
                        explanation = "Como 4 × $quotient = ${divisor * quotient}, ${divisor * quotient} ÷ 4 = $quotient."
                    }
                    else -> { // 20: Gran Desafío 20
                        divisor = (2..5).random()
                        quotient = (2..(20 / divisor)).random()
                        visualType = VisualType.NUMBER_FOCUS
                        isKeypad = true
                        question = "Gran Desafío 20: ${divisor * quotient} ÷ $divisor = ?"
                        explanation = "¡Consolidaste las divisiones hasta 20! ${divisor * quotient} ÷ $divisor = $quotient."
                    }
                }
            }
            in 21..25 -> {
                when (level) {
                    21 -> { // Dividir entre 10
                        divisor = 10
                        quotient = (1..10).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "${divisor * quotient} ÷ 10 = ?"
                        explanation = "Dividir entre 10: ¡solo quitamos un cero al final! ${divisor * quotient} ÷ 10 = $quotient."
                    }
                    22 -> { // Repartir entre 6 Básica
                        divisor = 6
                        quotient = (1..5).random()
                        visualType = VisualType.SHARING_PARTITION
                        question = "${divisor * quotient} ÷ 6 = ?"
                        explanation = "${divisor * quotient} repartido entre 6 = $quotient."
                    }
                    23 -> { // Tabla Inversa del 6
                        divisor = 6
                        quotient = (6..10).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "${divisor * quotient} ÷ 6 = ?"
                        explanation = "6 × $quotient = ${divisor * quotient}, por lo tanto ${divisor * quotient} ÷ 6 = $quotient."
                    }
                    24 -> { // Repartir entre 7 Básica
                        divisor = 7
                        quotient = (1..5).random()
                        visualType = VisualType.SHARING_PARTITION
                        question = "${divisor * quotient} ÷ 7 = ?"
                        explanation = "${divisor * quotient} repartido entre 7 días = $quotient por día."
                    }
                    else -> { // 25: Tabla Inversa del 7
                        divisor = 7
                        quotient = (6..10).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "${divisor * quotient} ÷ 7 = ?"
                        explanation = "7 × $quotient = ${divisor * quotient}, luego ${divisor * quotient} ÷ 7 = $quotient."
                    }
                }
            }
            in 26..30 -> {
                when (level) {
                    26 -> { // Repartir entre 8 Básica
                        divisor = 8
                        quotient = (1..5).random()
                        visualType = VisualType.SHARING_PARTITION
                        question = "${divisor * quotient} ÷ 8 = ?"
                        explanation = "${divisor * quotient} repartido en 8 partes = $quotient."
                    }
                    27 -> { // Tabla Inversa del 8
                        divisor = 8
                        quotient = (6..10).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "${divisor * quotient} ÷ 8 = ?"
                        explanation = "8 × $quotient = ${divisor * quotient}, por lo tanto ${divisor * quotient} ÷ 8 = $quotient."
                    }
                    28 -> { // Repartir entre 9 Básica
                        divisor = 9
                        quotient = (1..5).random()
                        visualType = VisualType.SHARING_PARTITION
                        question = "${divisor * quotient} ÷ 9 = ?"
                        explanation = "${divisor * quotient} repartido entre 9 = $quotient."
                    }
                    29 -> { // Tabla Inversa del 9
                        divisor = 9
                        quotient = (6..10).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "${divisor * quotient} ÷ 9 = ?"
                        explanation = "9 × $quotient = ${divisor * quotient}, por eso ${divisor * quotient} ÷ 9 = $quotient."
                    }
                    else -> { // 30: Tablas Inversas Mixtas
                        divisor = (2..10).random()
                        quotient = (2..10).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "${divisor * quotient} ÷ $divisor = ?"
                        explanation = "¡Gran dominio de tablas inversas! ${divisor * quotient} ÷ $divisor = $quotient."
                    }
                }
            }
            in 31..35 -> {
                when (level) {
                    31 -> { // Velocidad de División
                        divisor = (3..9).random()
                        quotient = (3..9).random()
                        visualType = VisualType.NUMBER_FOCUS
                        isKeypad = true
                        question = "${divisor * quotient} ÷ $divisor = ?"
                        explanation = "¡Cálculo mental rápido! ${divisor * quotient} ÷ $divisor = $quotient."
                    }
                    32 -> { // Dividir Decenas Redondas
                        divisor = listOf(2, 3, 4, 5).random()
                        quotient = listOf(10, 20, 30).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "${divisor * quotient} ÷ $divisor = ?"
                        explanation = "Dividimos decenas: ${divisor * quotient} ÷ $divisor = $quotient."
                    }
                    33 -> { // Repartir con Monedas
                        divisor = (2..5).random()
                        quotient = listOf(5, 10, 15, 20).random()
                        visualType = VisualType.SHARING_PARTITION
                        question = "Reparte ${divisor * quotient} monedas 🪙 entre $divisor alcancías iguales"
                        explanation = "${divisor * quotient} monedas ÷ $divisor alcancías = $quotient monedas en cada una."
                    }
                    34 -> { // Mitades de Dos Cifras
                        divisor = 2
                        quotient = (11..25).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "¿Cuánto es la mitad de ${divisor * quotient}? (${divisor * quotient} ÷ 2)"
                        explanation = "La mitad de ${divisor * quotient} es $quotient (${divisor * quotient} ÷ 2 = $quotient)."
                    }
                    else -> { // 35: Dos Cifras ÷ Una Cifra II
                        divisor = (3..6).random()
                        quotient = (10..15).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "${divisor * quotient} ÷ $divisor = ?"
                        explanation = "${divisor * quotient} ÷ $divisor = $quotient."
                    }
                }
            }
            in 36..40 -> {
                when (level) {
                    36 -> { // Reparto en la Cocina
                        divisor = (2..6).random()
                        quotient = (3..8).random()
                        visualType = VisualType.WORD_PROBLEM
                        val dividend = divisor * quotient
                        story = "El chef horneó $dividend deliciosos pastelitos 🧁 y los repartió en partes iguales en $divisor bandejas. ¿Cuántos pastelitos hay por bandeja?"
                        storyIcon = "🧁"
                        question = "¿Cuántos pastelitos hay en cada bandeja?"
                        explanation = "$dividend pastelitos ÷ $divisor bandejas = $quotient pastelitos por bandeja."
                    }
                    37 -> { // Reparto en el Aula
                        divisor = (3..7).random()
                        quotient = (4..9).random()
                        visualType = VisualType.WORD_PROBLEM
                        val dividend = divisor * quotient
                        story = "En el aula hay $dividend lápices de colores ✏️ para repartir de manera justa entre $divisor mesas de trabajo. ¿Cuántos recibe cada mesa?"
                        storyIcon = "✏️"
                        question = "¿Cuántos lápices de colores recibe cada mesa?"
                        explanation = "$dividend lápices ÷ $divisor mesas = $quotient lápices por mesa."
                    }
                    38 -> { // Problemas de Manzanas
                        divisor = (2..6).random()
                        quotient = (4..10).random()
                        visualType = VisualType.WORD_PROBLEM
                        val dividend = divisor * quotient
                        story = "En la cosecha se recolectaron $dividend manzanas frescas 🍎 y se guardaron en $divisor canastas en cantidades idénticas. ¿Cuántas hay en cada una?"
                        storyIcon = "🧺"
                        question = "¿Cuántas manzanas hay en cada canasta?"
                        explanation = "$dividend manzanas ÷ $divisor canastas = $quotient manzanas."
                    }
                    39 -> { // Piezas de Robótica
                        divisor = (3..8).random()
                        quotient = (5..11).random()
                        visualType = VisualType.WORD_PROBLEM
                        val dividend = divisor * quotient
                        story = "En el taller de ciencias hay $dividend sensores ⚙️ para repartir equitativamente entre $divisor proyectos robóticos. ¿Cuántos sensores recibe cada proyecto?"
                        storyIcon = "⚙️"
                        question = "¿Cuántos sensores recibe cada proyecto?"
                        explanation = "$dividend sensores ÷ $divisor proyectos = $quotient sensores."
                    }
                    else -> { // 40: Divisiones hasta 50
                        divisor = (2..7).random()
                        quotient = (4..(50 / divisor)).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "${divisor * quotient} ÷ $divisor = ?"
                        explanation = "Cálculo mental exacto: ${divisor * quotient} ÷ $divisor = $quotient."
                    }
                }
            }
            in 41..45 -> {
                when (level) {
                    41 -> { // Término Desconocido
                        divisor = (3..9).random()
                        quotient = (4..12).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "Si ${divisor * quotient} se reparte en partes iguales y cada uno recibe $quotient, ¿entre cuántos se repartió?"
                        explanation = "Se repartió entre $divisor, porque ${divisor * quotient} ÷ $divisor = $quotient."
                    }
                    42 -> { // Fiesta de Cumpleaños
                        divisor = (3..6).random()
                        quotient = (4..9).random()
                        visualType = VisualType.WORD_PROBLEM
                        val dividend = divisor * quotient
                        story = "En la fiesta de Mateo inflaron $dividend globos festivos 🎈 y los distribuyeron equitativamente en $divisor ramilletes. ¿Cuántos globos tiene cada ramillete?"
                        storyIcon = "🎈"
                        question = "¿Cuántos globos tiene cada ramillete?"
                        explanation = "$dividend globos ÷ $divisor ramilletes = $quotient globos por ramillete."
                    }
                    43 -> { // Equipos Deportivos
                        divisor = (4..8).random()
                        quotient = (5..10).random()
                        visualType = VisualType.WORD_PROBLEM
                        val dividend = divisor * quotient
                        story = "En el torneo deportivo participan $dividend atletas ⚽ divididos en $divisor equipos con el mismo número de jugadores. ¿Cuántos jugadores tiene cada equipo?"
                        storyIcon = "⚽"
                        question = "¿Cuántos jugadores integran cada equipo?"
                        explanation = "$dividend atletas ÷ $divisor equipos = $quotient jugadores por equipo."
                    }
                    44 -> { // Reparto con Bloques
                        divisor = (2..5).random()
                        quotient = (10..20).random()
                        visualType = VisualType.BASE_TEN_BLOCKS
                        question = "${divisor * quotient} ÷ $divisor = ?"
                        explanation = "Reparto con decenas y unidades: ${divisor * quotient} ÷ $divisor = $quotient."
                    }
                    else -> { // 45: Enigmas de División
                        divisor = (4..9).random()
                        quotient = (8..15).random()
                        visualType = VisualType.NUMBER_FOCUS
                        isKeypad = true
                        question = "Enigma: ${divisor * quotient} ÷ $divisor = ?"
                        explanation = "¡Deducción brillante! ${divisor * quotient} ÷ $divisor = $quotient."
                    }
                }
            }
            in 46..50 -> {
                when (level) {
                    46 -> { // Desafío de Campeones I
                        divisor = (3..9).random()
                        quotient = (9..16).random()
                        visualType = VisualType.NUMBER_FOCUS
                        question = "${divisor * quotient} ÷ $divisor = ?"
                        explanation = "Reparto de alta precisión: ${divisor * quotient} ÷ $divisor = $quotient."
                    }
                    47 -> { // Desafío de Campeones II
                        divisor = (4..8).random()
                        quotient = (6..14).random()
                        visualType = VisualType.WORD_PROBLEM
                        val dividend = divisor * quotient
                        story = "En la biblioteca llegaron $dividend nuevos libros de cuentos 📚 para organizar en $divisor estantes en partes idénticas. ¿Cuántos libros van en cada estante?"
                        storyIcon = "📚"
                        question = "¿Cuántos libros van en cada estante?"
                        explanation = "$dividend libros ÷ $divisor estantes = $quotient libros por estante."
                    }
                    48 -> { // Torneo de Maestros
                        divisor = (4..9).random()
                        quotient = (10..20).random()
                        visualType = VisualType.NUMBER_FOCUS
                        isKeypad = true
                        question = "Torneo de Maestros: ${divisor * quotient} ÷ $divisor = ?"
                        explanation = "¡Maestría en reparto exacto! ${divisor * quotient} ÷ $divisor = $quotient."
                    }
                    49 -> { // Prueba del Gran Repartidor
                        divisor = (5..9).random()
                        quotient = (11..22).random()
                        visualType = VisualType.NUMBER_FOCUS
                        isKeypad = true
                        question = "Gran Repartidor: ${divisor * quotient} ÷ $divisor = ?"
                        explanation = "¡Poder supremo de división! ${divisor * quotient} ÷ $divisor = $quotient."
                    }
                    else -> { // 50: Maestro Supremo de la División
                        divisor = (4..10).random()
                        quotient = (12..25).random()
                        visualType = VisualType.NUMBER_FOCUS
                        isKeypad = true
                        question = "¡Desafío Legendario! ${divisor * quotient} ÷ $divisor = ?"
                        explanation = "¡Corona Legendaria del Reparto Mágico! ${divisor * quotient} ÷ $divisor = $quotient."
                    }
                }
            }
            else -> {
                divisor = (2..9).random()
                quotient = (2..9).random()
                question = "${divisor * quotient} ÷ $divisor = ?"
                explanation = "${divisor * quotient} ÷ $divisor = $quotient."
            }
        }

        val dividend = divisor * quotient
        val correctAnswer = quotient
        val options = DistractorGenerator.generateOptions(correctAnswer, dividend, divisor, OperationType.DIVISION)

        val visualRep = VisualRepresentation(
            visualType = visualType,
            theme = theme,
            primaryEmoji = emoji,
            totalToShare = dividend,
            containerCount = divisor,
            itemsPerContainer = quotient,
            problemStory = story,
            problemIcon = storyIcon
        )

        val didacticStrat = when {
            visualType == VisualType.WORD_PROBLEM -> DidacticStrategy.WORD_PROBLEM
            dividend > 20 -> DidacticStrategy.BASE_TEN_BLOCKS
            else -> DidacticStrategy.EQUAL_SHARING
        }

        return Exercise(
            id = UUID.randomUUID().toString(),
            operationType = OperationType.DIVISION,
            operand1 = dividend,
            operand2 = divisor,
            correctAnswer = correctAnswer,
            options = options,
            difficulty = mapLevelToDifficulty(level),
            level = level,
            questionText = question,
            visualRepresentation = visualRep,
            explanation = explanation,
            isKeypadAllowed = isKeypad,
            didacticStrategy = didacticStrat
        )
    }

    override fun generateDidacticExercise(
        operation: OperationType,
        strategy: DidacticStrategy,
        difficulty: Difficulty
    ): Exercise {
        return when (strategy) {
            DidacticStrategy.NUMBER_LINE -> generateNumberLineExercise(operation, difficulty)
            DidacticStrategy.TEN_FRAME -> generateTenFrameExercise(operation, difficulty)
            DidacticStrategy.NUMBER_BONDS -> generateNumberBondsExercise(operation, difficulty)
            DidacticStrategy.MISSING_NUMBER -> generateMissingNumberExercise(operation, difficulty)
            DidacticStrategy.ARRAY_GRID -> generateArrayGridExercise(operation, difficulty)
            DidacticStrategy.EQUAL_SHARING -> generateEqualSharingExercise(operation, difficulty)
            DidacticStrategy.SKIP_COUNTING -> generateSkipCountingExercise(operation, difficulty)
            DidacticStrategy.BALANCE_SCALE -> generateBalanceScaleExercise(operation, difficulty)
            DidacticStrategy.BASE_TEN_BLOCKS -> generateBaseTenExercise(operation, difficulty)
            DidacticStrategy.WORD_PROBLEM -> generateWordProblemDidacticExercise(operation, difficulty)
            DidacticStrategy.CONCRETE_OBJECTS -> generateConcreteObjectsExercise(operation, difficulty)
        }
    }

    // ==========================================
    // ESTRATEGIAS DIDÁCTICAS ESPECÍFICAS
    // ==========================================

    private fun generateNumberLineExercise(operation: OperationType, difficulty: Difficulty): Exercise {
        val (op1, op2, ans, isForward, minLine, maxLine) = when (operation) {
            OperationType.ADDITION -> {
                val a = (1..6).random()
                val b = (1..5).random()
                val sum = a + b
                Tuple6(a, b, sum, true, 0, max(10, sum + 2))
            }
            OperationType.SUBTRACTION -> {
                val a = (4..12).random()
                val b = (1 until a).random()
                val diff = a - b
                Tuple6(a, b, diff, false, 0, max(10, a + 1))
            }
            OperationType.MULTIPLICATION -> {
                val jumps = (2..5).random()
                val step = (2..4).random()
                val prod = jumps * step
                Tuple6(jumps, step, prod, true, 0, max(10, prod + 2))
            }
            OperationType.DIVISION -> {
                val divisor = (2..4).random()
                val quotient = (2..5).random()
                val dividend = divisor * quotient
                Tuple6(dividend, divisor, quotient, false, 0, dividend + 2)
            }
        }

        val options = DistractorGenerator.generateOptions(ans, op1, op2, operation)
        val question = when (operation) {
            OperationType.ADDITION -> "En la recta numérica: salta desde $op1 avanzando +$op2 pasos"
            OperationType.SUBTRACTION -> "En la recta numérica: salta desde $op1 retrocediendo -$op2 pasos"
            OperationType.MULTIPLICATION -> "$op1 saltos repetidos de tamaño $op2 en la recta numérica"
            OperationType.DIVISION -> "¿Cuántos saltos hacia atrás de tamaño $op2 caben desde $op1 hasta 0?"
        }
        val explanation = when (operation) {
            OperationType.ADDITION -> "Partimos en $op1 y damos un salto de $op2 unidades a la derecha: caemos en el $ans."
            OperationType.SUBTRACTION -> "Partimos en $op1 y retrocedemos $op2 unidades a la izquierda: caemos en el $ans."
            OperationType.MULTIPLICATION -> "Damos $op1 saltos de $op2 en $op2 desde el 0: llegamos a $ans."
            OperationType.DIVISION -> "Retrocediendo de $op2 en $op2 desde $op1 hasta 0 se dan exactamente $ans saltos."
        }

        val visual = VisualRepresentation(
            visualType = VisualType.NUMBER_LINE,
            numberLineStart = minLine,
            numberLineEnd = maxLine,
            numberLineInitial = if (operation == OperationType.MULTIPLICATION) 0 else op1,
            numberLineJump = op2,
            numberLineDestination = if (operation == OperationType.DIVISION) 0 else ans,
            numberLineRepeatedJumps = if (operation == OperationType.MULTIPLICATION || operation == OperationType.DIVISION) (if (operation == OperationType.MULTIPLICATION) op1 else ans) else 1,
            isJumpForward = isForward
        )

        return Exercise(
            id = UUID.randomUUID().toString(),
            operationType = operation,
            operand1 = op1,
            operand2 = op2,
            correctAnswer = ans,
            options = options,
            difficulty = difficulty,
            questionText = question,
            visualRepresentation = visual,
            explanation = explanation,
            displayEquation = "$op1 ${operation.symbol} $op2 = ?",
            didacticStrategy = DidacticStrategy.NUMBER_LINE,
            missingTarget = MissingTarget.RESULT
        )
    }

    private fun generateTenFrameExercise(operation: OperationType, difficulty: Difficulty): Exercise {
        val theme = pickRandomTheme()
        val emojiA = theme.randomEmoji()
        val emojiB = theme.randomEmoji()

        return when (operation) {
            OperationType.ADDITION -> {
                val a = (1..9).random()
                val b = if (difficulty == Difficulty.EASY) (1..(10 - a)).random() else (2..9).random()
                val ans = a + b
                val options = DistractorGenerator.generateOptions(ans, a, b, operation)
                val visual = VisualRepresentation(
                    visualType = VisualType.TEN_FRAME,
                    theme = theme,
                    primaryEmoji = emojiA,
                    secondaryEmoji = emojiB,
                    tenFrameCountA = a,
                    tenFrameCountB = b,
                    tenFrameCapacity = if (ans > 10) 20 else 10
                )
                Exercise(
                    id = UUID.randomUUID().toString(),
                    operationType = operation,
                    operand1 = a,
                    operand2 = b,
                    correctAnswer = ans,
                    options = options,
                    difficulty = difficulty,
                    questionText = "Marco de Diez: $a fichas azules + $b fichas naranjas",
                    visualRepresentation = visual,
                    explanation = "Colocamos $a fichas azules y sumamos $b fichas naranjas: en total son $ans fichas.",
                    displayEquation = "$a + $b = ?",
                    didacticStrategy = DidacticStrategy.TEN_FRAME,
                    missingTarget = MissingTarget.RESULT
                )
            }
            OperationType.SUBTRACTION -> {
                val total = (3..10).random()
                val remove = (1 until total).random()
                val ans = total - remove
                val options = DistractorGenerator.generateOptions(ans, total, remove, operation)
                val visual = VisualRepresentation(
                    visualType = VisualType.TEN_FRAME,
                    theme = theme,
                    primaryEmoji = emojiA,
                    tenFrameCountA = total,
                    tenFrameTakeout = remove,
                    tenFrameCapacity = 10
                )
                Exercise(
                    id = UUID.randomUUID().toString(),
                    operationType = operation,
                    operand1 = total,
                    operand2 = remove,
                    correctAnswer = ans,
                    options = options,
                    difficulty = difficulty,
                    questionText = "De $total fichas en el marco quitamos $remove fichas tachadas",
                    visualRepresentation = visual,
                    explanation = "Teníamos $total fichas y quitamos $remove: quedan $ans fichas libres.",
                    displayEquation = "$total - $remove = ?",
                    didacticStrategy = DidacticStrategy.TEN_FRAME,
                    missingTarget = MissingTarget.RESULT
                )
            }
            OperationType.MULTIPLICATION -> {
                val groups = 2
                val items = (2..5).random()
                val ans = groups * items
                val options = DistractorGenerator.generateOptions(ans, groups, items, operation)
                val visual = VisualRepresentation(
                    visualType = VisualType.TEN_FRAME,
                    theme = theme,
                    primaryEmoji = emojiA,
                    tenFrameCountA = items,
                    tenFrameCountB = items,
                    tenFrameCapacity = 10
                )
                Exercise(
                    id = UUID.randomUUID().toString(),
                    operationType = operation,
                    operand1 = groups,
                    operand2 = items,
                    correctAnswer = ans,
                    options = options,
                    difficulty = difficulty,
                    questionText = "2 grupos de $items en el marco de 10",
                    visualRepresentation = visual,
                    explanation = "2 veces $items = $ans fichas en el marco.",
                    displayEquation = "$groups × $items = ?",
                    didacticStrategy = DidacticStrategy.TEN_FRAME,
                    missingTarget = MissingTarget.RESULT
                )
            }
            OperationType.DIVISION -> {
                val total = 10
                val containers = 2
                val ans = 5
                val options = DistractorGenerator.generateOptions(ans, total, containers, operation)
                val visual = VisualRepresentation(
                    visualType = VisualType.TEN_FRAME,
                    theme = theme,
                    primaryEmoji = emojiA,
                    tenFrameCountA = 5,
                    tenFrameCountB = 5,
                    tenFrameCapacity = 10
                )
                Exercise(
                    id = UUID.randomUUID().toString(),
                    operationType = operation,
                    operand1 = total,
                    operand2 = containers,
                    correctAnswer = ans,
                    options = options,
                    difficulty = difficulty,
                    questionText = "Reparte 10 fichas del marco en 2 filas iguales",
                    visualRepresentation = visual,
                    explanation = "10 dividido en 2 filas iguales = 5 fichas por fila.",
                    displayEquation = "$total ÷ $containers = ?",
                    didacticStrategy = DidacticStrategy.TEN_FRAME,
                    missingTarget = MissingTarget.RESULT
                )
            }
        }
    }

    private fun generateNumberBondsExercise(operation: OperationType, difficulty: Difficulty): Exercise {
        return when (operation) {
            OperationType.ADDITION -> {
                val partA = (2..8).random()
                val partB = (2..8).random()
                val whole = partA + partB
                val options = DistractorGenerator.generateOptions(whole, partA, partB, operation)
                val visual = VisualRepresentation(
                    visualType = VisualType.NUMBER_BONDS,
                    bondWhole = whole,
                    bondPartA = partA,
                    bondPartB = partB,
                    missingBondElement = BondElement.WHOLE
                )
                Exercise(
                    id = UUID.randomUUID().toString(),
                    operationType = operation,
                    operand1 = partA,
                    operand2 = partB,
                    correctAnswer = whole,
                    options = options,
                    difficulty = difficulty,
                    questionText = "Enlace Numérico: ¿Cuál es el Todo que unen las partes $partA y $partB?",
                    visualRepresentation = visual,
                    explanation = "Las partes $partA y $partB se unen para formar el Todo: $partA + $partB = $whole.",
                    displayEquation = "$partA + $partB = ?",
                    didacticStrategy = DidacticStrategy.NUMBER_BONDS,
                    missingTarget = MissingTarget.RESULT
                )
            }
            OperationType.SUBTRACTION -> {
                val whole = (5..16).random()
                val partA = (2..(whole - 2)).random()
                val partB = whole - partA
                val options = DistractorGenerator.generateOptions(partB, whole, partA, operation)
                val visual = VisualRepresentation(
                    visualType = VisualType.NUMBER_BONDS,
                    bondWhole = whole,
                    bondPartA = partA,
                    bondPartB = partB,
                    missingBondElement = BondElement.PART_B
                )
                Exercise(
                    id = UUID.randomUUID().toString(),
                    operationType = operation,
                    operand1 = whole,
                    operand2 = partA,
                    correctAnswer = partB,
                    options = options,
                    difficulty = difficulty,
                    questionText = "El Todo es $whole y una parte es $partA: ¿cuál es la otra parte?",
                    visualRepresentation = visual,
                    explanation = "Restamos del Todo la parte conocida: $whole - $partA = $partB.",
                    displayEquation = "$whole - $partA = ?",
                    didacticStrategy = DidacticStrategy.NUMBER_BONDS,
                    missingTarget = MissingTarget.RESULT
                )
            }
            OperationType.MULTIPLICATION -> {
                val groups = (2..4).random()
                val size = (2..5).random()
                val whole = groups * size
                val options = DistractorGenerator.generateOptions(whole, groups, size, operation)
                val visual = VisualRepresentation(
                    visualType = VisualType.NUMBER_BONDS,
                    bondWhole = whole,
                    bondPartA = groups,
                    bondPartB = size,
                    missingBondElement = BondElement.WHOLE
                )
                Exercise(
                    id = UUID.randomUUID().toString(),
                    operationType = operation,
                    operand1 = groups,
                    operand2 = size,
                    correctAnswer = whole,
                    options = options,
                    difficulty = difficulty,
                    questionText = "$groups partes iguales de $size cada una: ¿cuál es el Todo?",
                    visualRepresentation = visual,
                    explanation = "$groups partes de $size forman el Todo: $groups × $size = $whole.",
                    displayEquation = "$groups × $size = ?",
                    didacticStrategy = DidacticStrategy.NUMBER_BONDS,
                    missingTarget = MissingTarget.RESULT
                )
            }
            OperationType.DIVISION -> {
                val divisor = (2..4).random()
                val quotient = (2..5).random()
                val dividend = divisor * quotient
                val options = DistractorGenerator.generateOptions(quotient, dividend, divisor, operation)
                val visual = VisualRepresentation(
                    visualType = VisualType.NUMBER_BONDS,
                    bondWhole = dividend,
                    bondPartA = divisor,
                    bondPartB = quotient,
                    missingBondElement = BondElement.PART_B
                )
                Exercise(
                    id = UUID.randomUUID().toString(),
                    operationType = operation,
                    operand1 = dividend,
                    operand2 = divisor,
                    correctAnswer = quotient,
                    options = options,
                    difficulty = difficulty,
                    questionText = "El Todo es $dividend y se reparte en $divisor partes iguales: ¿cuánto vale cada parte?",
                    visualRepresentation = visual,
                    explanation = "$dividend repartido en $divisor partes iguales es $quotient por parte.",
                    displayEquation = "$dividend ÷ $divisor = ?",
                    didacticStrategy = DidacticStrategy.NUMBER_BONDS,
                    missingTarget = MissingTarget.RESULT
                )
            }
        }
    }

    private fun generateMissingNumberExercise(operation: OperationType, difficulty: Difficulty): Exercise {
        val level = when (difficulty) {
            Difficulty.EASY -> (1..2).random()
            Difficulty.BASIC -> (3..5).random()
            Difficulty.INTERMEDIATE -> (6..8).random()
            Difficulty.ADVANCED -> (9..10).random()
        }
        return generateCompletionExercise(operation, level)
    }

    // =========================================================================
    // 🧩 GENERADORES DE COMPLETACIÓN / INCÓGNITA FALTANTE (10 NIVELES POR OPERACIÓN)
    // =========================================================================

    private fun generateAdditionCompletionExercise(level: Int): Exercise {
        val theme = pickRandomTheme()
        val emoji = theme.randomEmoji()
        val difficulty = mapLevelToDifficulty(level)

        var op1: Int
        var op2: Int
        var ans: Int
        var target: MissingTarget
        var question: String
        var explanation: String
        var isKeypad = false
        var story: String? = null
        var storyIcon = "➕"
        var visualType = VisualType.BALANCE_SCALE
        var tensA = 0
        var unitsA = 0
        var tensB = 0
        var unitsB = 0

        when (level) {
            1 -> { // Sumas 1+1, 2+1, 2+2
                val a = (1..2).random()
                ans = (1..2).random()
                val sum = a + ans
                if (Random.nextBoolean()) {
                    target = MissingTarget.SECOND_OPERAND
                    op1 = a
                    op2 = sum
                    question = "¿Qué número falta para que la suma sea $sum?"
                    explanation = "Teníamos $a y para llegar a $sum faltan $ans: $a + $ans = $sum."
                } else {
                    target = MissingTarget.FIRST_OPERAND
                    op1 = sum
                    op2 = a
                    question = "¿Qué número sumado con $a es igual a $sum?"
                    explanation = "$ans + $a = $sum. El número inicial que faltaba es $ans."
                }
            }
            2 -> { // Sumas 3+2, 4+3, 5+2
                val a = (2..5).random()
                ans = (1..3).random()
                val sum = a + ans
                if (Random.nextBoolean()) {
                    target = MissingTarget.SECOND_OPERAND
                    op1 = a
                    op2 = sum
                    question = "¿Qué número falta para completar $sum?"
                    explanation = "Para completar $sum teniendo $a, sumamos $ans: $a + $ans = $sum."
                } else {
                    target = MissingTarget.FIRST_OPERAND
                    op1 = sum
                    op2 = a
                    question = "¿Qué número sumado a $a da $sum?"
                    explanation = "$ans + $a = $sum. El sumando que faltaba es $ans."
                }
            }
            3 -> { // Hasta 10 (Amigos del 10 y números pequeños)
                val sum = if (Random.nextBoolean()) 10 else (6..9).random()
                val a = (2 until sum).random()
                ans = sum - a
                target = MissingTarget.SECOND_OPERAND
                op1 = a
                op2 = sum
                visualType = if (sum == 10) VisualType.TEN_FRAME else VisualType.BALANCE_SCALE
                question = "¿Qué número falta para sumar $sum?"
                explanation = "El amigo para llegar a $sum desde $a es $ans: $a + $ans = $sum."
            }
            4 -> { // Hasta 20
                val a = (6..13).random()
                ans = (3..7).random()
                val sum = a + ans
                target = MissingTarget.SECOND_OPERAND
                op1 = a
                op2 = sum
                question = "¿Qué número completa la igualdad: $a + ? = $sum?"
                explanation = "Restamos: $sum - $a = $ans. Por lo tanto $a + $ans = $sum."
            }
            5 -> { // Dos cifras sin llevar
                val tens1 = (1..4).random()
                val units1 = (0..4).random()
                val tensAns = (1..4).random()
                val unitsAns = (0..(9 - units1)).random()
                val a = tens1 * 10 + units1
                ans = tensAns * 10 + unitsAns
                val sum = a + ans
                target = MissingTarget.SECOND_OPERAND
                op1 = a
                op2 = sum
                visualType = VisualType.BASE_TEN_BLOCKS
                tensA = tens1
                unitsA = units1
                tensB = tensAns
                unitsB = unitsAns
                question = "¿Qué número de dos cifras completa la suma?"
                explanation = "Unidades: $units1 + $unitsAns = ${units1 + unitsAns}. Decenas: $tens1 + $tensAns = ${tens1 + tensAns}. Falta el $ans."
            }
            6 -> { // Dos cifras con llevadas
                val tens1 = (1..3).random()
                val units1 = (5..9).random()
                val tensAns = (1..3).random()
                val unitsAns = ((10 - units1)..9).random()
                val a = tens1 * 10 + units1
                ans = tensAns * 10 + unitsAns
                val sum = a + ans
                target = MissingTarget.SECOND_OPERAND
                op1 = a
                op2 = sum
                visualType = VisualType.BASE_TEN_BLOCKS
                tensA = tens1
                unitsA = units1
                tensB = tensAns
                unitsB = unitsAns
                question = "¿Qué número completa la suma: $a + ? = $sum?"
                explanation = "Restamos $sum - $a = $ans. Comprobamos: $a + $ans = $sum."
            }
            7 -> { // Tres cifras
                val a = (100..450).random()
                ans = (100..450).random()
                val sum = a + ans
                target = MissingTarget.SECOND_OPERAND
                op1 = a
                op2 = sum
                question = "¿Qué número completa la igualdad: $a + ? = $sum?"
                explanation = "Centenas, decenas y unidades: $sum - $a = $ans. Así: $a + $ans = $sum."
                isKeypad = true
            }
            8 -> { // Problemas visuales
                val a = (3..8).random()
                ans = (2..6).random()
                val sum = a + ans
                target = MissingTarget.SECOND_OPERAND
                op1 = a
                op2 = sum
                visualType = VisualType.WORD_PROBLEM
                story = "Lucas tenía $a monedas 🪙 en su alcancía. Su abuelo le regaló algunas más y ahora tiene $sum en total. ¿Cuántas monedas le regaló su abuelo?"
                storyIcon = "🪙"
                question = "¿Cuántas monedas le regalaron a Lucas?"
                explanation = "Tenía $a y ahora tiene $sum: su abuelo le regaló $ans monedas ($sum - $a = $ans)."
            }
            9 -> { // Problemas matemáticos (MissingTarget.FIRST_OPERAND)
                ans = (10..22).random()
                val b = (6..14).random()
                val sum = ans + b
                target = MissingTarget.FIRST_OPERAND
                op1 = sum
                op2 = b
                visualType = VisualType.WORD_PROBLEM
                story = "Una nave espacial 🚀 transportaba astronautas. Al llegar a la base espacial se unieron $b astronautas más, formando un total de $sum astronautas en la misión. ¿Cuántos astronautas viajaban al inicio?"
                storyIcon = "🚀"
                question = "¿Cuántos astronautas viajaban al inicio de la misión?"
                explanation = "Al inicio viajaban $ans astronautas, porque $ans + $b = $sum."
            }
            10 -> { // Desafío final
                val a = (20..50).random()
                ans = (15..45).random()
                val sum = a + ans
                target = if (Random.nextBoolean()) MissingTarget.SECOND_OPERAND else MissingTarget.FIRST_OPERAND
                if (target == MissingTarget.SECOND_OPERAND) {
                    op1 = a
                    op2 = sum
                    question = "¡Desafío Maestro! ¿Qué número completa: $a + ? = $sum?"
                    explanation = "¡Excelente deducción mental! $a + $ans = $sum."
                } else {
                    op1 = sum
                    op2 = a
                    question = "¡Desafío Maestro! ¿Qué número completa: ? + $a = $sum?"
                    explanation = "¡Genial agilidad mental! $ans + $a = $sum."
                }
                isKeypad = Random.nextBoolean()
            }
            else -> {
                val maxLimit = (level * 6).coerceIn(20, 400)
                val a = (10..(maxLimit / 2)).random()
                ans = (5..(maxLimit / 2)).random()
                val sum = a + ans
                target = if (Random.nextBoolean()) MissingTarget.SECOND_OPERAND else MissingTarget.FIRST_OPERAND
                if (target == MissingTarget.SECOND_OPERAND) {
                    op1 = a
                    op2 = sum
                    question = "¿Qué número falta para que la suma sea $sum?"
                    explanation = "$a + $ans = $sum."
                } else {
                    op1 = sum
                    op2 = a
                    question = "¿Qué número sumado con $a es igual a $sum?"
                    explanation = "$ans + $a = $sum."
                }
                isKeypad = level > 25
            }
        }

        val displayEquation = if (target == MissingTarget.SECOND_OPERAND) {
            "$op1 + ? = $op2"
        } else {
            "? + $op2 = $op1"
        }

        val options = DistractorGenerator.generateOptions(ans, op1, op2, OperationType.ADDITION)

        val visualRep = VisualRepresentation(
            visualType = visualType,
            theme = theme,
            primaryEmoji = emoji,
            countA = if (target == MissingTarget.SECOND_OPERAND) op1 else ans,
            countB = if (target == MissingTarget.SECOND_OPERAND) ans else op2,
            tensA = tensA,
            unitsA = unitsA,
            tensB = tensB,
            unitsB = unitsB,
            problemStory = story,
            problemIcon = storyIcon,
            scaleLeftText = if (target == MissingTarget.SECOND_OPERAND) "$op1 + ?" else "? + $op2",
            scaleRightText = if (target == MissingTarget.SECOND_OPERAND) "$op2" else "$op1",
            scaleIsBalanced = true,
            tenFrameCountA = if (target == MissingTarget.SECOND_OPERAND) op1 else ans,
            tenFrameCapacity = 10
        )

        return Exercise(
            id = UUID.randomUUID().toString(),
            operationType = OperationType.ADDITION,
            operand1 = op1,
            operand2 = op2,
            correctAnswer = ans,
            options = options,
            difficulty = difficulty,
            level = level,
            questionText = question,
            visualRepresentation = visualRep,
            explanation = explanation,
            isKeypadAllowed = isKeypad,
            displayEquation = displayEquation,
            didacticStrategy = DidacticStrategy.MISSING_NUMBER,
            missingTarget = target
        )
    }

    private fun generateSubtractionCompletionExercise(level: Int): Exercise {
        val theme = pickRandomTheme()
        val emoji = theme.randomEmoji()
        val difficulty = mapLevelToDifficulty(level)

        var op1: Int
        var op2: Int
        var ans: Int
        var target: MissingTarget
        var question: String
        var explanation: String
        var isKeypad = false
        var story: String? = null
        var storyIcon = "➖"
        var visualType = VisualType.BALANCE_SCALE
        var tensA = 0
        var unitsA = 0
        var tensB = 0
        var unitsB = 0

        when (level) {
            1 -> { // Restas hasta 5
                val a = (3..5).random()
                ans = (1 until a).random()
                val diff = a - ans
                target = MissingTarget.SECOND_OPERAND
                op1 = a
                op2 = diff
                question = "¿Cuántos quitamos a $a para que queden $diff?"
                explanation = "Teníamos $a y quedaron $diff: quitamos $ans ($a - $diff = $ans)."
            }
            2 -> { // Restas hasta 10
                val a = (6..10).random()
                ans = (1..(a - 2)).random()
                val diff = a - ans
                if (Random.nextBoolean()) {
                    target = MissingTarget.SECOND_OPERAND
                    op1 = a
                    op2 = diff
                    question = "¿Qué número falta restar para obtener $diff?"
                    explanation = "A $a le restamos $ans para obtener $diff: $a - $ans = $diff."
                } else {
                    target = MissingTarget.FIRST_OPERAND
                    op1 = diff
                    op2 = ans
                    ans = a
                    question = "¿A qué número le restamos $op2 para que queden $op1?"
                    explanation = "Sumamos para recuperar el inicio: $op1 + $op2 = $ans. Así: $ans - $op2 = $op1."
                }
            }
            3 -> { // Restas hasta 20
                val a = (12..20).random()
                ans = (3..8).random()
                val diff = a - ans
                target = MissingTarget.SECOND_OPERAND
                op1 = a
                op2 = diff
                question = "¿Qué número completa la resta: $a - ? = $diff?"
                explanation = "Restamos: $a - $diff = $ans. El número que falta es $ans."
            }
            4 -> { // Restas con apoyo visual (12..18)
                val a = (12..18).random()
                ans = (4..9).random()
                val diff = a - ans
                target = MissingTarget.SECOND_OPERAND
                op1 = a
                op2 = diff
                question = "¿Qué número se le quitó a $a para que quedaran $diff?"
                explanation = "Retrocedimos $ans pasos desde $a para llegar a $diff: $a - $ans = $diff."
            }
            5 -> { // Dos cifras sin prestar
                val tens1 = (3..6).random()
                val units1 = (4..9).random()
                val tensAns = (1 until tens1).random()
                val unitsAns = (0..units1).random()
                val a = tens1 * 10 + units1
                ans = tensAns * 10 + unitsAns
                val diff = a - ans
                target = MissingTarget.SECOND_OPERAND
                op1 = a
                op2 = diff
                visualType = VisualType.BASE_TEN_BLOCKS
                tensA = tens1
                unitsA = units1
                tensB = tensAns
                unitsB = unitsAns
                question = "¿Qué número restamos a $a para que el resultado sea $diff?"
                explanation = "Restamos unidades ($units1 - $unitsAns = ${units1 - unitsAns}) y decenas ($tens1 - $tensAns = ${tens1 - tensAns}). El número es $ans."
            }
            6 -> { // Dos cifras con préstamo
                val tens1 = (3..6).random()
                val units1 = (0..5).random()
                val tensAns = (1 until tens1).random()
                val unitsAns = (units1 + 1..9).random()
                val a = tens1 * 10 + units1
                ans = tensAns * 10 + unitsAns
                val diff = a - ans
                target = MissingTarget.SECOND_OPERAND
                op1 = a
                op2 = diff
                visualType = VisualType.BASE_TEN_BLOCKS
                tensA = tens1
                unitsA = units1
                tensB = tensAns
                unitsB = unitsAns
                question = "¿Qué número completa la resta con préstamo: $a - ? = $diff?"
                explanation = "Restamos: $a - $diff = $ans. Verificamos: $a - $ans = $diff."
            }
            7 -> { // Tres cifras
                val a = (250..600).random()
                val diff = (100..(a - 50)).random()
                ans = a - diff
                target = MissingTarget.SECOND_OPERAND
                op1 = a
                op2 = diff
                question = "¿Qué número restamos a $a para llegar a $diff?"
                explanation = "$a - $diff = $ans. Comprobación: $a - $ans = $diff."
                isKeypad = true
            }
            8 -> { // Problemas visuales
                val a = (7..14).random()
                ans = (2..(a - 2)).random()
                val diff = a - ans
                target = MissingTarget.SECOND_OPERAND
                op1 = a
                op2 = diff
                visualType = VisualType.WORD_PROBLEM
                story = "En un árbol había $a pajaritos 🐦. Algunos salieron volando y ahora solo quedan $diff en las ramas. ¿Cuántos pajaritos volaron?"
                storyIcon = "🐦"
                question = "¿Cuántos pajaritos volaron del árbol?"
                explanation = "Había $a y quedaron $diff: volaron $ans pajaritos ($a - $diff = $ans)."
            }
            9 -> { // Problemas cotidianos (MissingTarget.FIRST_OPERAND)
                ans = (18..35).random()
                val b = (5..12).random()
                val diff = ans - b
                target = MissingTarget.FIRST_OPERAND
                op1 = diff
                op2 = b
                visualType = VisualType.WORD_PROBLEM
                story = "Valentina preparó una bandeja de galletas 🍪. Compartió $b galletas con sus compañeros y aún le quedan $diff galletas. ¿Cuántas galletas preparó Valentina al inicio?"
                storyIcon = "🍪"
                question = "¿Cuántas galletas preparó Valentina en total?"
                explanation = "Sumamos las que regaló y las que quedaron: $diff + $b = $ans galletas en total."
            }
            10 -> { // Desafío final
                val a = (45..85).random()
                val diff = (15..(a - 15)).random()
                ans = a - diff
                target = MissingTarget.SECOND_OPERAND
                op1 = a
                op2 = diff
                question = "¡Desafío Maestro! ¿Qué número completa: $a - ? = $diff?"
                explanation = "¡Excelente deducción! $a - $ans = $diff."
                isKeypad = Random.nextBoolean()
            }
            else -> {
                val maxLimit = (level * 6).coerceIn(20, 300)
                target = if (Random.nextBoolean()) MissingTarget.SECOND_OPERAND else MissingTarget.FIRST_OPERAND
                if (target == MissingTarget.SECOND_OPERAND) {
                    val a = (12..maxLimit).random()
                    ans = (2..(a - 5)).random()
                    val diff = a - ans
                    op1 = a
                    op2 = diff
                    question = "¿Qué número falta: $a - ? = $diff?"
                    explanation = "$a - $ans = $diff."
                } else {
                    val b = (2..(maxLimit / 2)).random()
                    val diff = (2..(maxLimit / 2)).random()
                    ans = b + diff
                    op1 = diff
                    op2 = b
                    question = "¿Qué número falta: ? - $b = $diff?"
                    explanation = "$ans - $b = $diff."
                }
                isKeypad = level > 25
            }
        }

        val displayEquation = if (target == MissingTarget.SECOND_OPERAND) {
            "$op1 - ? = $op2"
        } else {
            "? - $op2 = $op1"
        }

        val options = DistractorGenerator.generateOptions(ans, op1, op2, OperationType.SUBTRACTION)

        val visualRep = VisualRepresentation(
            visualType = visualType,
            theme = theme,
            primaryEmoji = emoji,
            totalCount = if (target == MissingTarget.SECOND_OPERAND) op1 else ans,
            removeCount = if (target == MissingTarget.SECOND_OPERAND) ans else op2,
            tensA = tensA,
            unitsA = unitsA,
            tensB = tensB,
            unitsB = unitsB,
            problemStory = story,
            problemIcon = storyIcon,
            scaleLeftText = if (target == MissingTarget.SECOND_OPERAND) "$op1 - ?" else "? - $op2",
            scaleRightText = if (target == MissingTarget.SECOND_OPERAND) "$op2" else "$op1",
            scaleIsBalanced = true,
            isSubtraction = true
        )

        return Exercise(
            id = UUID.randomUUID().toString(),
            operationType = OperationType.SUBTRACTION,
            operand1 = op1,
            operand2 = op2,
            correctAnswer = ans,
            options = options,
            difficulty = difficulty,
            level = level,
            questionText = question,
            visualRepresentation = visualRep,
            explanation = explanation,
            isKeypadAllowed = isKeypad,
            displayEquation = displayEquation,
            didacticStrategy = DidacticStrategy.MISSING_NUMBER,
            missingTarget = target
        )
    }

    private fun generateMultiplicationCompletionExercise(level: Int): Exercise {
        val theme = pickRandomTheme()
        val emoji = theme.randomEmoji()
        val difficulty = mapLevelToDifficulty(level)

        var op1: Int
        var op2: Int
        var ans: Int
        var target: MissingTarget
        var question: String
        var explanation: String
        var isKeypad = false
        var story: String? = null
        var storyIcon = "✖️"
        var visualType = VisualType.BALANCE_SCALE
        var arrayRows = 0
        var arrayCols = 0
        var groupCount = 0
        var itemsPerGroup = 0

        when (level) {
            1 -> { // Grupos de 2
                ans = (2..5).random()
                val b = 2
                val prod = ans * b
                target = MissingTarget.FIRST_OPERAND
                op1 = prod
                op2 = b
                visualType = VisualType.GROUPS_GRID
                groupCount = ans
                itemsPerGroup = b
                question = "¿Cuántos grupos de 2 $emoji forman $prod?"
                explanation = "Necesitamos $ans grupos de 2 para tener $prod: $ans × 2 = $prod."
            }
            2 -> { // Grupos de 3
                ans = (2..5).random()
                val b = 3
                val prod = ans * b
                target = MissingTarget.FIRST_OPERAND
                op1 = prod
                op2 = b
                visualType = VisualType.GROUPS_GRID
                groupCount = ans
                itemsPerGroup = b
                question = "¿Cuántos grupos de 3 $emoji forman $prod?"
                explanation = "$ans grupos con 3 elementos cada uno forman $prod: $ans × 3 = $prod."
            }
            3 -> { // Grupos de 4 y 5
                val a = (2..5).random()
                ans = listOf(4, 5).random()
                val prod = a * ans
                target = MissingTarget.SECOND_OPERAND
                op1 = a
                op2 = prod
                visualType = VisualType.ARRAY_GRID
                arrayRows = a
                arrayCols = ans
                question = "¿Qué factor multiplicado por $a da $prod?"
                explanation = "Dividimos: $prod ÷ $a = $ans. Así: $a × $ans = $prod."
            }
            4 -> { // Tablas del 1 al 5
                val a = (2..5).random()
                ans = (2..9).random()
                val prod = a * ans
                target = MissingTarget.SECOND_OPERAND
                op1 = a
                op2 = prod
                question = "¿Qué número multiplicado por $a da $prod?"
                explanation = "En la tabla del $a: $a × $ans = $prod."
            }
            5 -> { // Tablas del 1 al 10
                val a = (3..9).random()
                ans = (3..9).random()
                val prod = a * ans
                if (Random.nextBoolean()) {
                    target = MissingTarget.SECOND_OPERAND
                    op1 = a
                    op2 = prod
                    question = "¿Qué número completa la igualdad: $a × ? = $prod?"
                    explanation = "$prod ÷ $a = $ans. Por lo tanto $a × $ans = $prod."
                } else {
                    target = MissingTarget.FIRST_OPERAND
                    op1 = prod
                    op2 = a
                    question = "¿Qué número completa la igualdad: ? × $a = $prod?"
                    explanation = "$prod ÷ $a = $ans. Por lo tanto $ans × $a = $prod."
                }
            }
            6 -> { // Dos cifras por una cifra
                val a = (11..18).random()
                ans = (2..5).random()
                val prod = a * ans
                target = MissingTarget.SECOND_OPERAND
                op1 = a
                op2 = prod
                visualType = VisualType.NUMBER_FOCUS
                question = "¿Por qué número multiplicamos $a para obtener $prod?"
                explanation = "$prod ÷ $a = $ans. Verificamos: $a × $ans = $prod."
            }
            7 -> { // Dos cifras intermedias
                val a = (12..25).random()
                ans = (3..6).random()
                val prod = a * ans
                target = MissingTarget.SECOND_OPERAND
                op1 = a
                op2 = prod
                visualType = VisualType.NUMBER_FOCUS
                question = "¿Qué factor completa la igualdad: $a × ? = $prod?"
                explanation = "$prod ÷ $a = $ans."
                isKeypad = true
            }
            8 -> { // Problemas visuales
                val a = (3..6).random()
                ans = (2..5).random()
                val prod = a * ans
                target = MissingTarget.SECOND_OPERAND
                op1 = a
                op2 = prod
                visualType = VisualType.WORD_PROBLEM
                story = "En la tienda de juguetes hay $a cajas de sorpresas 🎁. En total hay $prod autitos 🏎️. Si todas las cajas tienen la misma cantidad, ¿cuántos autitos hay en cada caja?"
                storyIcon = "🎁"
                question = "¿Cuántos autitos contiene cada caja?"
                explanation = "$prod autitos repartidos en $a cajas = $ans autitos en cada una ($a × $ans = $prod)."
            }
            9 -> { // Problemas matemáticos (FIRST_OPERAND)
                ans = (4..8).random()
                val b = (5..9).random()
                val prod = ans * b
                target = MissingTarget.FIRST_OPERAND
                op1 = prod
                op2 = b
                visualType = VisualType.WORD_PROBLEM
                story = "Un agricultor plantó varias filas de árboles frutales 🌳 con $b árboles en cada fila. Si en total plantó $prod árboles, ¿cuántas filas plantó el agricultor?"
                storyIcon = "🌳"
                question = "¿Cuántas filas de árboles plantó el agricultor?"
                explanation = "$prod árboles ÷ $b árboles por fila = $ans filas ($ans × $b = $prod)."
            }
            10 -> { // Desafío final
                val a = (6..12).random()
                ans = (6..9).random()
                val prod = a * ans
                target = MissingTarget.SECOND_OPERAND
                op1 = a
                op2 = prod
                visualType = VisualType.NUMBER_FOCUS
                question = "¡Desafío Maestro! ¿Qué número completa: $a × ? = $prod?"
                explanation = "¡Gran dominio multiplicativo! $a × $ans = $prod."
                isKeypad = Random.nextBoolean()
            }
            else -> {
                val a = if (level <= 20) (2..5).random() else if (level <= 35) (3..9).random() else (4..12).random()
                ans = if (level <= 20) (2..6).random() else if (level <= 35) (3..9).random() else (5..15).random()
                val prod = a * ans
                target = if (Random.nextBoolean()) MissingTarget.SECOND_OPERAND else MissingTarget.FIRST_OPERAND
                if (target == MissingTarget.SECOND_OPERAND) {
                    op1 = a
                    op2 = prod
                    question = "¿Qué número falta: $a × ? = $prod?"
                    explanation = "$a × $ans = $prod."
                } else {
                    op1 = prod
                    op2 = a
                    question = "¿Qué número falta: ? × $a = $prod?"
                    explanation = "$ans × $a = $prod."
                }
                isKeypad = level > 25
            }
        }

        val displayEquation = if (target == MissingTarget.SECOND_OPERAND) {
            "$op1 × ? = $op2"
        } else {
            "? × $op2 = $op1"
        }

        val options = DistractorGenerator.generateOptions(ans, op1, op2, OperationType.MULTIPLICATION)

        val visualRep = VisualRepresentation(
            visualType = visualType,
            theme = theme,
            primaryEmoji = emoji,
            groupCount = groupCount,
            itemsPerGroup = itemsPerGroup,
            arrayRows = arrayRows,
            arrayCols = arrayCols,
            problemStory = story,
            problemIcon = storyIcon,
            scaleLeftText = if (target == MissingTarget.SECOND_OPERAND) "$op1 × ?" else "? × $op2",
            scaleRightText = if (target == MissingTarget.SECOND_OPERAND) "$op2" else "$op1",
            scaleIsBalanced = true
        )

        return Exercise(
            id = UUID.randomUUID().toString(),
            operationType = OperationType.MULTIPLICATION,
            operand1 = op1,
            operand2 = op2,
            correctAnswer = ans,
            options = options,
            difficulty = difficulty,
            level = level,
            questionText = question,
            visualRepresentation = visualRep,
            explanation = explanation,
            isKeypadAllowed = isKeypad,
            displayEquation = displayEquation,
            didacticStrategy = DidacticStrategy.MISSING_NUMBER,
            missingTarget = target
        )
    }

    private fun generateDivisionCompletionExercise(level: Int): Exercise {
        val theme = pickRandomTheme()
        val emoji = theme.randomEmoji()
        val difficulty = mapLevelToDifficulty(level)

        var op1: Int
        var op2: Int
        var ans: Int
        var target: MissingTarget
        var question: String
        var explanation: String
        var isKeypad = false
        var story: String? = null
        var storyIcon = "➗"
        var visualType = VisualType.BALANCE_SCALE
        var containerCount = 0
        var itemsPerContainer = 0
        var totalToShare = 0

        when (level) {
            1 -> { // Divisiones sencillas
                ans = listOf(2, 3).random()
                val quotient = (2..3).random()
                val dividend = ans * quotient
                target = MissingTarget.SECOND_OPERAND
                op1 = dividend
                op2 = quotient
                visualType = VisualType.SHARING_PARTITION
                totalToShare = dividend
                containerCount = ans
                itemsPerContainer = quotient
                question = "Repartimos $dividend objetos y a cada uno le tocan $quotient. ¿Entre cuántos recipientes se repartieron?"
                explanation = "$dividend ÷ $quotient = $ans recipientes ($dividend ÷ $ans = $quotient)."
            }
            2 -> { // Divisiones hasta 10
                ans = listOf(2, 3, 4).random()
                val quotient = (2..(10 / ans)).random()
                val dividend = ans * quotient
                target = MissingTarget.SECOND_OPERAND
                op1 = dividend
                op2 = quotient
                visualType = VisualType.SHARING_PARTITION
                totalToShare = dividend
                containerCount = ans
                itemsPerContainer = quotient
                question = "¿Entre qué número dividimos $dividend para que el resultado sea $quotient?"
                explanation = "$dividend ÷ $quotient = $ans. Comprobación: $dividend ÷ $ans = $quotient."
            }
            3 -> { // Divisiones hasta 20
                ans = (2..5).random()
                val quotient = (2..(20 / ans)).random()
                val dividend = ans * quotient
                target = MissingTarget.SECOND_OPERAND
                op1 = dividend
                op2 = quotient
                question = "¿Qué número completa la división: $dividend ÷ ? = $quotient?"
                explanation = "$dividend ÷ $quotient = $ans. Así: $dividend ÷ $ans = $quotient."
            }
            4 -> { // Objetos en recipientes
                ans = (3..6).random()
                val quotient = (3..6).random()
                val dividend = ans * quotient
                target = MissingTarget.SECOND_OPERAND
                op1 = dividend
                op2 = quotient
                visualType = VisualType.SHARING_PARTITION
                totalToShare = dividend
                containerCount = ans
                itemsPerContainer = quotient
                question = "Si repartimos $dividend elementos y cada grupo tiene $quotient, ¿cuántos grupos hay?"
                explanation = "Reparto equitativo: $dividend ÷ $quotient = $ans grupos."
            }
            5 -> { // Tablas inversas
                ans = (3..9).random()
                val quotient = (3..9).random()
                val dividend = ans * quotient
                if (Random.nextBoolean()) {
                    target = MissingTarget.SECOND_OPERAND
                    op1 = dividend
                    op2 = quotient
                    question = "¿Qué número completa: $dividend ÷ ? = $quotient?"
                    explanation = "Como $quotient × $ans = $dividend, entonces $dividend ÷ $ans = $quotient."
                } else {
                    target = MissingTarget.FIRST_OPERAND
                    op1 = quotient
                    op2 = ans
                    ans = dividend
                    question = "¿Qué número dividido entre $op2 da $op1?"
                    explanation = "Multiplicamos para encontrar el total: $op1 × $op2 = $ans. Así: $ans ÷ $op2 = $op1."
                }
            }
            6 -> { // Dos cifras entre una cifra
                ans = (3..8).random()
                val quotient = (5..12).random()
                val dividend = ans * quotient
                target = MissingTarget.SECOND_OPERAND
                op1 = dividend
                op2 = quotient
                visualType = VisualType.NUMBER_FOCUS
                question = "¿Entre qué número dividimos $dividend para obtener $quotient?"
                explanation = "$dividend ÷ $quotient = $ans."
            }
            7 -> { // Divisiones complejas
                ans = (4..9).random()
                val quotient = (8..15).random()
                val dividend = ans * quotient
                target = MissingTarget.SECOND_OPERAND
                op1 = dividend
                op2 = quotient
                visualType = VisualType.NUMBER_FOCUS
                question = "¿Qué número completa: $dividend ÷ ? = $quotient?"
                explanation = "$dividend ÷ $quotient = $ans."
                isKeypad = true
            }
            8 -> { // Problemas visuales
                ans = (2..5).random()
                val quotient = (3..6).random()
                val dividend = ans * quotient
                target = MissingTarget.SECOND_OPERAND
                op1 = dividend
                op2 = quotient
                visualType = VisualType.WORD_PROBLEM
                story = "La profesora tiene $dividend manzanas 🍎 y las repartió en canastas 🧺 de forma equitativa. En cada canasta puso $quotient manzanas. ¿Cuántas canastas utilizó?"
                storyIcon = "🧺"
                question = "¿Cuántas canastas utilizó la profesora?"
                explanation = "$dividend manzanas ÷ $quotient por canasta = $ans canastas ($dividend ÷ $ans = $quotient)."
            }
            9 -> { // Problemas cotidianos (FIRST_OPERAND)
                val divisor = (3..6).random()
                val quotient = (4..8).random()
                ans = divisor * quotient
                target = MissingTarget.FIRST_OPERAND
                op1 = quotient
                op2 = divisor
                visualType = VisualType.WORD_PROBLEM
                story = "En la clase de robótica se repartieron piezas de engranajes ⚙️ entre $divisor equipos. Cada equipo recibió $quotient piezas. ¿Cuántas piezas de engranajes había en total?"
                storyIcon = "⚙️"
                question = "¿Cuántas piezas de engranajes había en total para repartir?"
                explanation = "$divisor equipos × $quotient piezas cada uno = $ans piezas en total ($ans ÷ $divisor = $quotient)."
            }
            10 -> { // Desafío final
                ans = (4..9).random()
                val quotient = (7..14).random()
                val dividend = ans * quotient
                target = MissingTarget.SECOND_OPERAND
                op1 = dividend
                op2 = quotient
                visualType = VisualType.NUMBER_FOCUS
                question = "¡Desafío Maestro! ¿Qué número completa: $dividend ÷ ? = $quotient?"
                explanation = "¡Brillante maestría en la división! $dividend ÷ $ans = $quotient."
                isKeypad = Random.nextBoolean()
            }
            else -> {
                target = if (Random.nextBoolean()) MissingTarget.SECOND_OPERAND else MissingTarget.FIRST_OPERAND
                if (target == MissingTarget.SECOND_OPERAND) {
                    ans = if (level <= 20) (2..5).random() else if (level <= 35) (3..9).random() else (4..12).random()
                    val quotient = if (level <= 20) (2..6).random() else if (level <= 35) (3..10).random() else (5..15).random()
                    val dividend = ans * quotient
                    op1 = dividend
                    op2 = quotient
                    question = "¿Qué número falta: $dividend ÷ ? = $quotient?"
                    explanation = "$dividend ÷ $ans = $quotient."
                } else {
                    val divisor = if (level <= 20) (2..5).random() else if (level <= 35) (3..9).random() else (4..12).random()
                    val quotient = if (level <= 20) (2..6).random() else if (level <= 35) (3..10).random() else (5..15).random()
                    ans = divisor * quotient
                    op1 = quotient
                    op2 = divisor
                    question = "¿Qué número falta: ? ÷ $divisor = $quotient?"
                    explanation = "$ans ÷ $divisor = $quotient."
                }
                isKeypad = level > 25
            }
        }

        val displayEquation = if (target == MissingTarget.SECOND_OPERAND) {
            "$op1 ÷ ? = $op2"
        } else {
            "? ÷ $op2 = $op1"
        }

        val options = DistractorGenerator.generateOptions(ans, op1, op2, OperationType.DIVISION)

        val visualRep = VisualRepresentation(
            visualType = visualType,
            theme = theme,
            primaryEmoji = emoji,
            totalToShare = totalToShare,
            containerCount = containerCount,
            itemsPerContainer = itemsPerContainer,
            problemStory = story,
            problemIcon = storyIcon,
            scaleLeftText = if (target == MissingTarget.SECOND_OPERAND) "$op1 ÷ ?" else "? ÷ $op2",
            scaleRightText = if (target == MissingTarget.SECOND_OPERAND) "$op2" else "$op1",
            scaleIsBalanced = true
        )

        return Exercise(
            id = UUID.randomUUID().toString(),
            operationType = OperationType.DIVISION,
            operand1 = op1,
            operand2 = op2,
            correctAnswer = ans,
            options = options,
            difficulty = difficulty,
            level = level,
            questionText = question,
            visualRepresentation = visualRep,
            explanation = explanation,
            isKeypadAllowed = isKeypad,
            displayEquation = displayEquation,
            didacticStrategy = DidacticStrategy.MISSING_NUMBER,
            missingTarget = target
        )
    }

    private fun generateArrayGridExercise(operation: OperationType, difficulty: Difficulty): Exercise {
        val theme = pickRandomTheme()

        return when (operation) {
            OperationType.MULTIPLICATION -> {
                val rows = (2..5).random()
                val cols = (2..6).random()
                val total = rows * cols
                val options = DistractorGenerator.generateOptions(total, rows, cols, operation)
                Exercise(
                    id = UUID.randomUUID().toString(),
                    operationType = operation,
                    operand1 = rows,
                    operand2 = cols,
                    correctAnswer = total,
                    options = options,
                    difficulty = difficulty,
                    questionText = "Matriz Rectangular de $rows filas × $cols columnas",
                    visualRepresentation = VisualRepresentation(
                        visualType = VisualType.ARRAY_GRID,
                        theme = theme,
                        arrayRows = rows,
                        arrayCols = cols
                    ),
                    explanation = "Multiplicamos la cuadrícula: $rows filas × $cols columnas = $total elementos en total.",
                    displayEquation = "$rows × $cols = ?",
                    didacticStrategy = DidacticStrategy.ARRAY_GRID,
                    missingTarget = MissingTarget.RESULT
                )
            }
            OperationType.DIVISION -> {
                val rows = (2..5).random()
                val cols = (2..6).random()
                val total = rows * cols
                val options = DistractorGenerator.generateOptions(cols, total, rows, operation)
                Exercise(
                    id = UUID.randomUUID().toString(),
                    operationType = operation,
                    operand1 = total,
                    operand2 = rows,
                    correctAnswer = cols,
                    options = options,
                    difficulty = difficulty,
                    questionText = "Repartimos $total elementos en $rows filas: ¿cuántas columnas forman?",
                    visualRepresentation = VisualRepresentation(
                        visualType = VisualType.ARRAY_GRID,
                        theme = theme,
                        arrayRows = rows,
                        arrayCols = cols
                    ),
                    explanation = "$total elementos repartidos en $rows filas dan $cols columnas ($total ÷ $rows = $cols).",
                    displayEquation = "$total ÷ $rows = ?",
                    didacticStrategy = DidacticStrategy.ARRAY_GRID,
                    missingTarget = MissingTarget.RESULT
                )
            }
            OperationType.ADDITION -> {
                val a = (2..6).random()
                val b = (2..6).random()
                val sum = a + b
                val options = DistractorGenerator.generateOptions(sum, a, b, operation)
                Exercise(
                    id = UUID.randomUUID().toString(),
                    operationType = operation,
                    operand1 = a,
                    operand2 = b,
                    correctAnswer = sum,
                    options = options,
                    difficulty = difficulty,
                    questionText = "Matriz de cuadrícula: $a fichas arriba + $b fichas abajo",
                    visualRepresentation = VisualRepresentation(
                        visualType = VisualType.ARRAY_GRID,
                        theme = theme,
                        arrayRows = 2,
                        arrayCols = max(a, b)
                    ),
                    explanation = "Sumando las dos filas de la matriz: $a + $b = $sum.",
                    displayEquation = "$a + $b = ?",
                    didacticStrategy = DidacticStrategy.ARRAY_GRID,
                    missingTarget = MissingTarget.RESULT
                )
            }
            OperationType.SUBTRACTION -> {
                val a = (6..12).random()
                val b = (2..5).random()
                val diff = a - b
                val options = DistractorGenerator.generateOptions(diff, a, b, operation)
                Exercise(
                    id = UUID.randomUUID().toString(),
                    operationType = operation,
                    operand1 = a,
                    operand2 = b,
                    correctAnswer = diff,
                    options = options,
                    difficulty = difficulty,
                    questionText = "De una matriz de $a fichas quitamos $b fichas",
                    visualRepresentation = VisualRepresentation(
                        visualType = VisualType.ARRAY_GRID,
                        theme = theme,
                        arrayRows = 2,
                        arrayCols = max(2, (a + 1) / 2)
                    ),
                    explanation = "Restando elementos de la matriz: $a - $b = $diff.",
                    displayEquation = "$a - $b = ?",
                    didacticStrategy = DidacticStrategy.ARRAY_GRID,
                    missingTarget = MissingTarget.RESULT
                )
            }
        }
    }

    private fun generateSkipCountingExercise(operation: OperationType, difficulty: Difficulty): Exercise {
        val step = listOf(2, 3, 4, 5, 10).random()
        val series = listOf(step, step * 2, step * 3, step * 4, step * 5)
        val missingIdx = (1..3).random()
        val ans = series[missingIdx]
        val displayList = series.mapIndexed { idx, v -> if (idx == missingIdx) null else v }

        val (op1, op2) = when (operation) {
            OperationType.ADDITION -> Pair(series[missingIdx - 1], step)
            OperationType.SUBTRACTION -> Pair(series[missingIdx] + step, step)
            OperationType.MULTIPLICATION -> Pair(missingIdx + 1, step)
            OperationType.DIVISION -> Pair(ans * 2, 2)
        }

        val options = DistractorGenerator.generateOptions(ans, op1, op2, operation)
        val visual = VisualRepresentation(
            visualType = VisualType.SKIP_COUNTING,
            sequenceItems = displayList,
            sequenceStep = step
        )

        return Exercise(
            id = UUID.randomUUID().toString(),
            operationType = operation,
            operand1 = op1,
            operand2 = op2,
            correctAnswer = ans,
            options = options,
            difficulty = difficulty,
            questionText = "Descubre el número misterioso que completa la serie de $step en $step",
            visualRepresentation = visual,
            explanation = "La serie rítmica avanza sumando $step cada paso: llegamos a $ans.",
            displayEquation = "Serie de $step en $step: [ ? ]",
            didacticStrategy = DidacticStrategy.SKIP_COUNTING,
            missingTarget = MissingTarget.RESULT
        )
    }

    private fun generateBalanceScaleExercise(operation: OperationType, difficulty: Difficulty): Exercise {
        val (op1, op2, ans) = when (operation) {
            OperationType.ADDITION -> {
                val a = (2..9).random()
                val b = (1..9).random()
                Triple(a, b, a + b)
            }
            OperationType.SUBTRACTION -> {
                val a = (5..15).random()
                val b = (1 until a).random()
                Triple(a, b, a - b)
            }
            OperationType.MULTIPLICATION -> {
                val a = (2..6).random()
                val b = (2..5).random()
                Triple(a, b, a * b)
            }
            OperationType.DIVISION -> {
                val divisor = (2..5).random()
                val quotient = (2..5).random()
                Triple(divisor * quotient, divisor, quotient)
            }
        }

        val options = DistractorGenerator.generateOptions(ans, op1, op2, operation)
        val visual = VisualRepresentation(
            visualType = VisualType.BALANCE_SCALE,
            scaleLeftText = "$op1 ${operation.symbol} $op2",
            scaleRightText = "$ans"
        )

        return Exercise(
            id = UUID.randomUUID().toString(),
            operationType = operation,
            operand1 = op1,
            operand2 = op2,
            correctAnswer = ans,
            options = options,
            difficulty = difficulty,
            questionText = "Equilibra la balanza: ¿cuánto debe marcar el platillo derecho?",
            visualRepresentation = visual,
            explanation = "Para que la balanza esté en equilibrio perfecto, ambos lados deben valer $ans.",
            displayEquation = "$op1 ${operation.symbol} $op2 = ?",
            didacticStrategy = DidacticStrategy.BALANCE_SCALE,
            missingTarget = MissingTarget.RESULT
        )
    }

    private fun generateEqualSharingExercise(operation: OperationType, difficulty: Difficulty): Exercise {
        val theme = pickRandomTheme()
        val emoji = theme.randomEmoji()

        return when (operation) {
            OperationType.DIVISION -> {
                val containers = (2..5).random()
                val itemsPer = (2..6).random()
                val total = containers * itemsPer
                val options = DistractorGenerator.generateOptions(itemsPer, total, containers, operation)
                val visual = VisualRepresentation(
                    visualType = VisualType.SHARING_PARTITION,
                    theme = theme,
                    primaryEmoji = emoji,
                    totalToShare = total,
                    containerCount = containers,
                    itemsPerContainer = itemsPer
                )
                Exercise(
                    id = UUID.randomUUID().toString(),
                    operationType = operation,
                    operand1 = total,
                    operand2 = containers,
                    correctAnswer = itemsPer,
                    options = options,
                    difficulty = difficulty,
                    questionText = "Reparte $total objetos en $containers canastas iguales: ¿cuántos van en cada una?",
                    visualRepresentation = visual,
                    explanation = "Reparto equitativo: $total dividido en $containers partes = $itemsPer en cada una.",
                    displayEquation = "$total ÷ $containers = ?",
                    didacticStrategy = DidacticStrategy.EQUAL_SHARING,
                    missingTarget = MissingTarget.RESULT
                )
            }
            OperationType.MULTIPLICATION -> {
                val containers = (2..5).random()
                val itemsPer = (2..6).random()
                val total = containers * itemsPer
                val options = DistractorGenerator.generateOptions(total, containers, itemsPer, operation)
                val visual = VisualRepresentation(
                    visualType = VisualType.SHARING_PARTITION,
                    theme = theme,
                    primaryEmoji = emoji,
                    totalToShare = total,
                    containerCount = containers,
                    itemsPerContainer = itemsPer
                )
                Exercise(
                    id = UUID.randomUUID().toString(),
                    operationType = operation,
                    operand1 = containers,
                    operand2 = itemsPer,
                    correctAnswer = total,
                    options = options,
                    difficulty = difficulty,
                    questionText = "$containers canastas con $itemsPer elementos en cada una: ¿cuántos hay en total?",
                    visualRepresentation = visual,
                    explanation = "Multiplicamos los grupos iguales: $containers × $itemsPer = $total elementos en total.",
                    displayEquation = "$containers × $itemsPer = ?",
                    didacticStrategy = DidacticStrategy.EQUAL_SHARING,
                    missingTarget = MissingTarget.RESULT
                )
            }
            OperationType.ADDITION -> {
                val a = (2..6).random()
                val b = (2..6).random()
                val sum = a + b
                val options = DistractorGenerator.generateOptions(sum, a, b, operation)
                val visual = VisualRepresentation(
                    visualType = VisualType.SHARING_PARTITION,
                    theme = theme,
                    primaryEmoji = emoji,
                    totalToShare = sum,
                    containerCount = 2,
                    itemsPerContainer = a
                )
                Exercise(
                    id = UUID.randomUUID().toString(),
                    operationType = operation,
                    operand1 = a,
                    operand2 = b,
                    correctAnswer = sum,
                    options = options,
                    difficulty = difficulty,
                    questionText = "Juntamos $a objetos de la canasta 1 con $b objetos de la canasta 2",
                    visualRepresentation = visual,
                    explanation = "Sumando el contenido de ambas canastas: $a + $b = $sum.",
                    displayEquation = "$a + $b = ?",
                    didacticStrategy = DidacticStrategy.EQUAL_SHARING,
                    missingTarget = MissingTarget.RESULT
                )
            }
            OperationType.SUBTRACTION -> {
                val total = (6..12).random()
                val remove = (1..(total - 2)).random()
                val rem = total - remove
                val options = DistractorGenerator.generateOptions(rem, total, remove, operation)
                val visual = VisualRepresentation(
                    visualType = VisualType.SHARING_PARTITION,
                    theme = theme,
                    primaryEmoji = emoji,
                    totalToShare = total,
                    containerCount = 1,
                    itemsPerContainer = rem
                )
                Exercise(
                    id = UUID.randomUUID().toString(),
                    operationType = operation,
                    operand1 = total,
                    operand2 = remove,
                    correctAnswer = rem,
                    options = options,
                    difficulty = difficulty,
                    questionText = "De una canasta con $total objetos sacamos $remove: ¿cuántos quedan?",
                    visualRepresentation = visual,
                    explanation = "Restando del total de la canasta: $total - $remove = $rem.",
                    displayEquation = "$total - $remove = ?",
                    didacticStrategy = DidacticStrategy.EQUAL_SHARING,
                    missingTarget = MissingTarget.RESULT
                )
            }
        }
    }

    private fun generateBaseTenExercise(operation: OperationType, difficulty: Difficulty): Exercise {
        val op1: Int
        val op2: Int
        val ans: Int
        val visual: VisualRepresentation

        when (operation) {
            OperationType.ADDITION -> {
                val tens1 = (1..4).random()
                val units1 = (1..8).random()
                val tens2 = (1..3).random()
                val units2 = (1..8).random()
                op1 = tens1 * 10 + units1
                op2 = tens2 * 10 + units2
                ans = op1 + op2
                visual = VisualRepresentation(
                    visualType = VisualType.BASE_TEN_BLOCKS,
                    tensA = tens1,
                    unitsA = units1,
                    tensB = tens2,
                    unitsB = units2
                )
            }
            OperationType.SUBTRACTION -> {
                val tens1 = (2..5).random()
                val units1 = (2..8).random()
                val tens2 = (1 until tens1).random()
                val units2 = (1..units1).random()
                op1 = tens1 * 10 + units1
                op2 = tens2 * 10 + units2
                ans = op1 - op2
                visual = VisualRepresentation(
                    visualType = VisualType.BASE_TEN_BLOCKS,
                    tensA = tens1,
                    unitsA = units1,
                    tensB = tens2,
                    unitsB = units2
                )
            }
            OperationType.MULTIPLICATION -> {
                val factor = (2..4).random()
                val tens = (1..2).random()
                val units = (0..2).random()
                op1 = tens * 10 + units
                op2 = factor
                ans = op1 * op2
                visual = VisualRepresentation(
                    visualType = VisualType.BASE_TEN_BLOCKS,
                    tensA = tens,
                    unitsA = units,
                    tensB = 0,
                    unitsB = factor
                )
            }
            OperationType.DIVISION -> {
                val divisor = (2..4).random()
                val tens = (1..2).random()
                val units = (0..2).random()
                ans = tens * 10 + units
                op1 = divisor * ans
                op2 = divisor
                visual = VisualRepresentation(
                    visualType = VisualType.BASE_TEN_BLOCKS,
                    tensA = op1 / 10,
                    unitsA = op1 % 10,
                    tensB = 0,
                    unitsB = divisor
                )
            }
        }

        val options = DistractorGenerator.generateOptions(ans, op1, op2, operation)

        return Exercise(
            id = UUID.randomUUID().toString(),
            operationType = operation,
            operand1 = op1,
            operand2 = op2,
            correctAnswer = ans,
            options = options,
            difficulty = difficulty,
            questionText = "Bloques de Decenas y Unidades: $op1 ${operation.symbol} $op2 = ?",
            visualRepresentation = visual,
            explanation = "Calculamos con decenas y unidades: resultado = $ans.",
            displayEquation = "$op1 ${operation.symbol} $op2 = ?",
            didacticStrategy = DidacticStrategy.BASE_TEN_BLOCKS,
            missingTarget = MissingTarget.RESULT
        )
    }

    private fun generateWordProblemDidacticExercise(operation: OperationType, difficulty: Difficulty): Exercise {
        val base = when (operation) {
            OperationType.ADDITION -> generateAdditionExercise(8)
            OperationType.SUBTRACTION -> generateSubtractionExercise(8)
            OperationType.MULTIPLICATION -> generateMultiplicationExercise(8)
            OperationType.DIVISION -> generateDivisionExercise(8)
        }
        return base.copy(
            didacticStrategy = DidacticStrategy.WORD_PROBLEM,
            displayEquation = "${base.operand1} ${operation.symbol} ${base.operand2} = ?",
            difficulty = difficulty,
            missingTarget = MissingTarget.RESULT
        )
    }

    private fun generateConcreteObjectsExercise(operation: OperationType, difficulty: Difficulty): Exercise {
        val base = when (operation) {
            OperationType.ADDITION -> generateAdditionExercise(1)
            OperationType.SUBTRACTION -> generateSubtractionExercise(1)
            OperationType.MULTIPLICATION -> generateMultiplicationExercise(1)
            OperationType.DIVISION -> generateDivisionExercise(1)
        }
        return base.copy(
            didacticStrategy = DidacticStrategy.CONCRETE_OBJECTS,
            displayEquation = "${base.operand1} ${operation.symbol} ${base.operand2} = ?",
            difficulty = difficulty,
            missingTarget = MissingTarget.RESULT
        )
    }

    // Helper tuple para 6 valores
    private data class Tuple6<A, B, C, D, E, F>(
        val a: A,
        val b: B,
        val c: C,
        val d: D,
        val e: E,
        val f: F
    )

    private data class DidacticBundle(
        val strategy: DidacticStrategy,
        val visualRepresentation: VisualRepresentation,
        val questionText: String,
        val explanation: String
    )

    private fun buildDidacticDetails(
        operation: OperationType,
        op1: Int,
        op2: Int,
        ans: Int,
        difficulty: Difficulty
    ): DidacticBundle {
        val theme = pickRandomTheme()
        val emojiA = theme.randomEmoji()
        val emojiB = theme.randomEmoji()

        return when (operation) {
            OperationType.ADDITION -> {
                when {
                    ans <= 10 -> {
                        val choices = listOf(
                            DidacticStrategy.NUMBER_LINE,
                            DidacticStrategy.TEN_FRAME,
                            DidacticStrategy.NUMBER_BONDS,
                            DidacticStrategy.BALANCE_SCALE,
                            DidacticStrategy.CONCRETE_OBJECTS
                        )
                        when (choices.random()) {
                            DidacticStrategy.NUMBER_LINE -> DidacticBundle(
                                strategy = DidacticStrategy.NUMBER_LINE,
                                visualRepresentation = VisualRepresentation(
                                    visualType = VisualType.NUMBER_LINE,
                                    numberLineStart = 0,
                                    numberLineEnd = 10,
                                    numberLineInitial = op1,
                                    numberLineJump = op2,
                                    numberLineDestination = ans,
                                    isJumpForward = true
                                ),
                                questionText = "En la recta numérica: salta desde $op1 avanzando +$op2 pasos",
                                explanation = "Partimos en $op1 y damos un salto de $op2 unidades hacia la derecha: llegamos al $ans."
                            )
                            DidacticStrategy.TEN_FRAME -> DidacticBundle(
                                strategy = DidacticStrategy.TEN_FRAME,
                                visualRepresentation = VisualRepresentation(
                                    visualType = VisualType.TEN_FRAME,
                                    theme = theme,
                                    primaryEmoji = emojiA,
                                    secondaryEmoji = emojiB,
                                    tenFrameCountA = op1,
                                    tenFrameCountB = op2,
                                    tenFrameCapacity = 10
                                ),
                                questionText = "Marco de 10: $op1 fichas azules + $op2 fichas naranjas",
                                explanation = "Llenamos $op1 casillas azules y sumamos $op2 naranjas: en total son $ans fichas."
                            )
                            DidacticStrategy.NUMBER_BONDS -> DidacticBundle(
                                strategy = DidacticStrategy.NUMBER_BONDS,
                                visualRepresentation = VisualRepresentation(
                                    visualType = VisualType.NUMBER_BONDS,
                                    bondWhole = ans,
                                    bondPartA = op1,
                                    bondPartB = op2,
                                    missingBondElement = BondElement.WHOLE
                                ),
                                questionText = "Enlace Numérico: ¿Cuál es el Todo que forman $op1 y $op2?",
                                explanation = "Las partes $op1 y $op2 se unen para formar el Todo: $op1 + $op2 = $ans."
                            )
                            DidacticStrategy.BALANCE_SCALE -> DidacticBundle(
                                strategy = DidacticStrategy.BALANCE_SCALE,
                                visualRepresentation = VisualRepresentation(
                                    visualType = VisualType.BALANCE_SCALE,
                                    scaleLeftText = "$op1 + $op2",
                                    scaleRightText = "$ans"
                                ),
                                questionText = "Equilibra la balanza: ¿cuánto debe pesar el platillo derecho?",
                                explanation = "Ambos lados deben ser iguales para estar en equilibrio: $op1 + $op2 = $ans."
                            )
                            else -> DidacticBundle(
                                strategy = DidacticStrategy.CONCRETE_OBJECTS,
                                visualRepresentation = VisualRepresentation(
                                    visualType = VisualType.OBJECT_COUNT,
                                    theme = theme,
                                    primaryEmoji = emojiA,
                                    secondaryEmoji = emojiB,
                                    countA = op1,
                                    countB = op2
                                ),
                                questionText = "$op1 + $op2 = ?",
                                explanation = "Juntamos $op1 $emojiA y $op2 $emojiB: en total son $ans."
                            )
                        }
                    }
                    ans in 11..20 -> {
                        val choices = listOf(DidacticStrategy.TEN_FRAME, DidacticStrategy.NUMBER_LINE, DidacticStrategy.NUMBER_BONDS)
                        when (choices.random()) {
                            DidacticStrategy.TEN_FRAME -> DidacticBundle(
                                strategy = DidacticStrategy.TEN_FRAME,
                                visualRepresentation = VisualRepresentation(
                                    visualType = VisualType.TEN_FRAME,
                                    theme = theme,
                                    primaryEmoji = emojiA,
                                    secondaryEmoji = emojiB,
                                    tenFrameCountA = op1,
                                    tenFrameCountB = op2,
                                    tenFrameCapacity = 20
                                ),
                                questionText = "Doble marco de 10: $op1 + $op2 = ?",
                                explanation = "Completamos una decena de 10 y colocamos ${ans - 10} más: total $ans."
                            )
                            DidacticStrategy.NUMBER_LINE -> DidacticBundle(
                                strategy = DidacticStrategy.NUMBER_LINE,
                                visualRepresentation = VisualRepresentation(
                                    visualType = VisualType.NUMBER_LINE,
                                    numberLineStart = 0,
                                    numberLineEnd = 20,
                                    numberLineInitial = op1,
                                    numberLineJump = op2,
                                    numberLineDestination = ans,
                                    isJumpForward = true
                                ),
                                questionText = "Recta numérica hasta 20: salta de $op1 hacia adelante +$op2",
                                explanation = "Partimos en $op1 y sumamos $op2 saltos: caemos en el $ans."
                            )
                            else -> DidacticBundle(
                                strategy = DidacticStrategy.NUMBER_BONDS,
                                visualRepresentation = VisualRepresentation(
                                    visualType = VisualType.NUMBER_BONDS,
                                    bondWhole = ans,
                                    bondPartA = op1,
                                    bondPartB = op2,
                                    missingBondElement = BondElement.WHOLE
                                ),
                                questionText = "Enlace Numérico: ¿Cuál es el Todo de $op1 y $op2?",
                                explanation = "Uniendo $op1 y $op2 completamos el Todo: $ans."
                            )
                        }
                    }
                    else -> DidacticBundle(
                        strategy = DidacticStrategy.BASE_TEN_BLOCKS,
                        visualRepresentation = VisualRepresentation(
                            visualType = VisualType.BASE_TEN_BLOCKS,
                            tensA = op1 / 10,
                            unitsA = op1 % 10,
                            tensB = op2 / 10,
                            unitsB = op2 % 10
                        ),
                        questionText = "$op1 + $op2 = ?",
                        explanation = "Decenas y unidades: sumamos $op1 + $op2 = $ans."
                    )
                }
            }
            OperationType.SUBTRACTION -> {
                when {
                    op1 <= 10 -> {
                        val choices = listOf(
                            DidacticStrategy.NUMBER_LINE,
                            DidacticStrategy.TEN_FRAME,
                            DidacticStrategy.NUMBER_BONDS,
                            DidacticStrategy.BALANCE_SCALE,
                            DidacticStrategy.CONCRETE_OBJECTS
                        )
                        when (choices.random()) {
                            DidacticStrategy.NUMBER_LINE -> DidacticBundle(
                                strategy = DidacticStrategy.NUMBER_LINE,
                                visualRepresentation = VisualRepresentation(
                                    visualType = VisualType.NUMBER_LINE,
                                    numberLineStart = 0,
                                    numberLineEnd = 10,
                                    numberLineInitial = op1,
                                    numberLineJump = op2,
                                    numberLineDestination = ans,
                                    isJumpForward = false
                                ),
                                questionText = "En la recta numérica: salta desde $op1 retrocediendo -$op2 pasos",
                                explanation = "Desde $op1 retrocedemos $op2 saltos a la izquierda: caemos en $ans."
                            )
                            DidacticStrategy.TEN_FRAME -> DidacticBundle(
                                strategy = DidacticStrategy.TEN_FRAME,
                                visualRepresentation = VisualRepresentation(
                                    visualType = VisualType.TEN_FRAME,
                                    theme = theme,
                                    primaryEmoji = emojiA,
                                    tenFrameCountA = op1,
                                    tenFrameTakeout = op2,
                                    tenFrameCapacity = 10
                                ),
                                questionText = "De $op1 fichas en el marco quitamos $op2 tachadas",
                                explanation = "Teníamos $op1 fichas y tachamos $op2: quedan $ans fichas."
                            )
                            DidacticStrategy.NUMBER_BONDS -> DidacticBundle(
                                strategy = DidacticStrategy.NUMBER_BONDS,
                                visualRepresentation = VisualRepresentation(
                                    visualType = VisualType.NUMBER_BONDS,
                                    bondWhole = op1,
                                    bondPartA = op2,
                                    bondPartB = ans,
                                    missingBondElement = BondElement.PART_B
                                ),
                                questionText = "El Todo es $op1 y una parte es $op2: ¿cuál es la otra parte?",
                                explanation = "Restamos del Todo la parte conocida: $op1 - $op2 = $ans."
                            )
                            DidacticStrategy.BALANCE_SCALE -> DidacticBundle(
                                strategy = DidacticStrategy.BALANCE_SCALE,
                                visualRepresentation = VisualRepresentation(
                                    visualType = VisualType.BALANCE_SCALE,
                                    scaleLeftText = "$op1 - $op2",
                                    scaleRightText = "$ans"
                                ),
                                questionText = "Equilibra la balanza: ¿cuánto debe pesar el platillo derecho?",
                                explanation = "Ambos lados deben ser iguales: $op1 - $op2 = $ans."
                            )
                            else -> DidacticBundle(
                                strategy = DidacticStrategy.CONCRETE_OBJECTS,
                                visualRepresentation = VisualRepresentation(
                                    visualType = VisualType.OBJECT_TAKEOUT,
                                    theme = theme,
                                    primaryEmoji = emojiA,
                                    totalCount = op1,
                                    removeCount = op2
                                ),
                                questionText = "$op1 - $op2 = ?",
                                explanation = "De $op1 $emojiA quitamos $op2: quedan $ans."
                            )
                        }
                    }
                    op1 in 11..20 -> {
                        DidacticBundle(
                            strategy = DidacticStrategy.NUMBER_LINE,
                            visualRepresentation = VisualRepresentation(
                                visualType = VisualType.NUMBER_LINE,
                                numberLineStart = 0,
                                numberLineEnd = 20,
                                numberLineInitial = op1,
                                numberLineJump = op2,
                                numberLineDestination = ans,
                                isJumpForward = false
                            ),
                            questionText = "Recta numérica: salta desde $op1 hacia atrás -$op2 pasos",
                            explanation = "Desde $op1 retrocedemos $op2 unidades: llegamos a $ans."
                        )
                    }
                    else -> DidacticBundle(
                        strategy = DidacticStrategy.BASE_TEN_BLOCKS,
                        visualRepresentation = VisualRepresentation(
                            visualType = VisualType.BASE_TEN_BLOCKS,
                            tensA = op1 / 10,
                            unitsA = op1 % 10,
                            tensB = op2 / 10,
                            unitsB = op2 % 10,
                            isSubtraction = true
                        ),
                        questionText = "$op1 - $op2 = ?",
                        explanation = "Restando decenas y unidades: $op1 - $op2 = $ans."
                    )
                }
            }
            OperationType.MULTIPLICATION -> {
                val choices = listOf(
                    DidacticStrategy.ARRAY_GRID,
                    DidacticStrategy.NUMBER_LINE,
                    DidacticStrategy.NUMBER_BONDS,
                    DidacticStrategy.CONCRETE_OBJECTS
                )
                when (choices.random()) {
                    DidacticStrategy.ARRAY_GRID -> DidacticBundle(
                        strategy = DidacticStrategy.ARRAY_GRID,
                        visualRepresentation = VisualRepresentation(
                            visualType = VisualType.ARRAY_GRID,
                            theme = theme,
                            primaryEmoji = emojiA,
                            arrayRows = op1,
                            arrayCols = op2
                        ),
                        questionText = "Matriz Rectangular de $op1 filas × $op2 columnas",
                        explanation = "Multiplicamos la cuadrícula: $op1 filas con $op2 elementos = $ans en total."
                    )
                    DidacticStrategy.NUMBER_LINE -> DidacticBundle(
                        strategy = DidacticStrategy.NUMBER_LINE,
                        visualRepresentation = VisualRepresentation(
                            visualType = VisualType.NUMBER_LINE,
                            numberLineStart = 0,
                            numberLineEnd = max(10, ans + 2),
                            numberLineInitial = 0,
                            numberLineJump = op2,
                            numberLineDestination = ans,
                            numberLineRepeatedJumps = op1,
                            isJumpForward = true
                        ),
                        questionText = "$op1 saltos repetidos de tamaño $op2 en la recta numérica",
                        explanation = "Damos $op1 saltos de $op2 en $op2 desde 0: llegamos exactamente a $ans."
                    )
                    DidacticStrategy.NUMBER_BONDS -> DidacticBundle(
                        strategy = DidacticStrategy.NUMBER_BONDS,
                        visualRepresentation = VisualRepresentation(
                            visualType = VisualType.NUMBER_BONDS,
                            bondWhole = ans,
                            bondPartA = op1,
                            bondPartB = op2,
                            missingBondElement = BondElement.WHOLE
                        ),
                        questionText = "$op1 grupos iguales de $op2: ¿cuál es el Todo?",
                        explanation = "$op1 veces $op2 forman el Todo: $op1 × $op2 = $ans."
                    )
                    else -> DidacticBundle(
                        strategy = DidacticStrategy.CONCRETE_OBJECTS,
                        visualRepresentation = VisualRepresentation(
                            visualType = VisualType.GROUPS_GRID,
                            theme = theme,
                            primaryEmoji = emojiA,
                            groupCount = op1,
                            itemsPerGroup = op2
                        ),
                        questionText = "$op1 grupos de $op2 $emojiA = ?",
                        explanation = "$op1 grupos con $op2 objetos cada uno: $op1 × $op2 = $ans."
                    )
                }
            }
            OperationType.DIVISION -> {
                val choices = listOf(
                    DidacticStrategy.EQUAL_SHARING,
                    DidacticStrategy.NUMBER_LINE,
                    DidacticStrategy.ARRAY_GRID,
                    DidacticStrategy.BALANCE_SCALE
                )
                when (choices.random()) {
                    DidacticStrategy.EQUAL_SHARING -> DidacticBundle(
                        strategy = DidacticStrategy.EQUAL_SHARING,
                        visualRepresentation = VisualRepresentation(
                            visualType = VisualType.SHARING_PARTITION,
                            theme = theme,
                            primaryEmoji = emojiA,
                            totalToShare = op1,
                            containerCount = op2,
                            itemsPerContainer = ans
                        ),
                        questionText = "Reparte $op1 objetos equitativamente en $op2 canastas",
                        explanation = "Distribuimos $op1 en $op2 partes iguales: tocan $ans en cada canasta."
                    )
                    DidacticStrategy.NUMBER_LINE -> DidacticBundle(
                        strategy = DidacticStrategy.NUMBER_LINE,
                        visualRepresentation = VisualRepresentation(
                            visualType = VisualType.NUMBER_LINE,
                            numberLineStart = 0,
                            numberLineEnd = op1 + 2,
                            numberLineInitial = op1,
                            numberLineJump = op2,
                            numberLineDestination = 0,
                            numberLineRepeatedJumps = ans,
                            isJumpForward = false
                        ),
                        questionText = "¿Cuántos saltos hacia atrás de tamaño $op2 caben desde $op1 hasta 0?",
                        explanation = "Retrocediendo de $op2 en $op2 desde $op1 hasta 0 caben exactamente $ans saltos."
                    )
                    DidacticStrategy.ARRAY_GRID -> DidacticBundle(
                        strategy = DidacticStrategy.ARRAY_GRID,
                        visualRepresentation = VisualRepresentation(
                            visualType = VisualType.ARRAY_GRID,
                            theme = theme,
                            primaryEmoji = emojiA,
                            arrayRows = op2,
                            arrayCols = ans
                        ),
                        questionText = "Reparte $op1 elementos en $op2 filas de la matriz",
                        explanation = "$op1 elementos repartidos en $op2 filas dan $ans columnas."
                    )
                    DidacticStrategy.BALANCE_SCALE -> DidacticBundle(
                        strategy = DidacticStrategy.BALANCE_SCALE,
                        visualRepresentation = VisualRepresentation(
                            visualType = VisualType.BALANCE_SCALE,
                            scaleLeftText = "$op1 ÷ $op2",
                            scaleRightText = "$ans"
                        ),
                        questionText = "Equilibra la balanza: ¿cuánto debe pesar el platillo derecho?",
                        explanation = "$op1 ÷ $op2 = $ans: ambos platillos se equilibran."
                    )
                    else -> DidacticBundle(
                        strategy = DidacticStrategy.EQUAL_SHARING,
                        visualRepresentation = VisualRepresentation(
                            visualType = VisualType.SHARING_PARTITION,
                            theme = theme,
                            primaryEmoji = emojiA,
                            totalToShare = op1,
                            containerCount = op2,
                            itemsPerContainer = ans
                        ),
                        questionText = "$op1 ÷ $op2 = ?",
                        explanation = "$op1 repartido entre $op2 es igual a $ans."
                    )
                }
            }
        }
    }

    override fun isValidExercise(exercise: Exercise): Boolean {
        // 1. Opciones válidas: exactamente 4, todas únicas, contiene la respuesta correcta
        if (exercise.options.size != 4) return false
        if (exercise.options.toSet().size != 4) return false
        if (!exercise.options.contains(exercise.correctAnswer)) return false

        // 2. Operación y cálculo matemático exacto según MissingTarget
        return when (exercise.missingTarget) {
            MissingTarget.RESULT -> {
                when (exercise.operationType) {
                    OperationType.ADDITION -> {
                        exercise.correctAnswer == exercise.operand1 + exercise.operand2
                    }
                    OperationType.SUBTRACTION -> {
                        val correctMath = exercise.correctAnswer == exercise.operand1 - exercise.operand2
                        val nonNegativeCheck = exercise.correctAnswer >= 0
                        correctMath && nonNegativeCheck
                    }
                    OperationType.MULTIPLICATION -> {
                        exercise.correctAnswer == exercise.operand1 * exercise.operand2
                    }
                    OperationType.DIVISION -> {
                        if (exercise.operand2 <= 0) return false
                        val isExact = exercise.operand1 % exercise.operand2 == 0
                        val correctMath = exercise.operand1 / exercise.operand2 == exercise.correctAnswer
                        isExact && correctMath
                    }
                }
            }
            MissingTarget.SECOND_OPERAND -> {
                when (exercise.operationType) {
                    OperationType.ADDITION -> {
                        exercise.operand1 + exercise.correctAnswer == exercise.operand2
                    }
                    OperationType.SUBTRACTION -> {
                        exercise.operand1 - exercise.correctAnswer == exercise.operand2
                    }
                    OperationType.MULTIPLICATION -> {
                        exercise.operand1 * exercise.correctAnswer == exercise.operand2
                    }
                    OperationType.DIVISION -> {
                        if (exercise.correctAnswer <= 0) return false
                        val isExact = exercise.operand1 % exercise.correctAnswer == 0
                        val correctMath = exercise.operand1 / exercise.correctAnswer == exercise.operand2
                        isExact && correctMath
                    }
                }
            }
            MissingTarget.FIRST_OPERAND -> {
                when (exercise.operationType) {
                    OperationType.ADDITION -> {
                        exercise.correctAnswer + exercise.operand2 == exercise.operand1
                    }
                    OperationType.SUBTRACTION -> {
                        exercise.correctAnswer - exercise.operand2 == exercise.operand1
                    }
                    OperationType.MULTIPLICATION -> {
                        exercise.correctAnswer * exercise.operand2 == exercise.operand1
                    }
                    OperationType.DIVISION -> {
                        if (exercise.operand2 <= 0) return false
                        val isExact = exercise.correctAnswer % exercise.operand2 == 0
                        val correctMath = exercise.correctAnswer / exercise.operand2 == exercise.operand1
                        isExact && correctMath
                    }
                }
            }
        }
    }

    private fun mapLevelToDifficulty(level: Int): Difficulty {
        return when (level) {
            in 1..10 -> Difficulty.EASY
            in 11..25 -> Difficulty.BASIC
            in 26..40 -> Difficulty.INTERMEDIATE
            else -> Difficulty.ADVANCED
        }
    }

    private fun pickRandomTheme(): VisualTheme {
        return VisualTheme.entries.random()
    }

    companion object {
        val shared: DefaultExerciseGenerator by lazy { DefaultExerciseGenerator() }
    }
}

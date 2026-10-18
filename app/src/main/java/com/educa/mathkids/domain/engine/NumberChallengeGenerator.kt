package com.educa.mathkids.domain.engine

import com.educa.mathkids.domain.model.NumberCatalog
import com.educa.mathkids.domain.model.NumberChallenge
import com.educa.mathkids.domain.model.NumberGameType
import kotlin.random.Random

/**
 * Generador didáctico de actividades dinámicas para el módulo "Explorando los Números".
 * Garantiza variedad continua sin repetir siempre las mismas preguntas.
 */
object NumberChallengeGenerator {

    private val random = Random(System.currentTimeMillis())

    fun generate(type: NumberGameType, isEnglish: Boolean = false, orderDescending: Boolean? = null): NumberChallenge {
        return when (type) {
            NumberGameType.IDENTIFY -> generateIdentify(isEnglish)
            NumberGameType.COUNT -> generateCount(isEnglish)
            NumberGameType.COMPARE -> generateCompare(isEnglish)
            NumberGameType.COMPLETE -> generateSequence(isEnglish)
            NumberGameType.ORDER -> generateOrder(isEnglish, orderDescending)
            NumberGameType.EXPLORE -> generateIdentify(isEnglish)
        }
    }

    /**
     * 1. Identificar números: "¿Dónde está el número 14?" (del 1 al 20)
     */
    private fun generateIdentify(isEnglish: Boolean): NumberChallenge {
        val target = random.nextInt(1, 21)
        val catalogItem = NumberCatalog.NUMBERS_1_TO_20.first { it.number == target }

        val distractors = (1..20).filter { it != target }.shuffled(random).take(3)
        val options = (distractors + target).shuffled(random)

        val word = catalogItem.getWord(isEnglish)
        return NumberChallenge(
            id = "id_${System.currentTimeMillis()}",
            type = NumberGameType.IDENTIFY,
            question = if (isEnglish) "Where is the number $target?" else "¿Dónde está el número $target?",
            spokenQuestion = if (isEnglish) "Where is the number $word?" else "¿Dónde está el número $word?",
            visualItems = listOf(catalogItem.emojiObject),
            options = options,
            correctAnswer = target,
            explanation = if (isEnglish) "Great job! That is the number $target ($word)." else "¡Muy bien! Ese es el número $target ($word)."
        )
    }

    /**
     * 2. Contar objetos: "¿Cuántas manzanas hay? 🍎🍎🍎🍎" (hasta 15-20)
     */
    private fun generateCount(isEnglish: Boolean): NumberChallenge {
        val count = random.nextInt(1, 16)
        val item = NumberCatalog.NUMBERS_1_TO_20.random(random)
        val visualList = List(count) { item.emojiObject }

        val distractors = (1..20).filter { it != count }.shuffled(random).take(3)
        val options = (distractors + count).shuffled(random)

        val pluralName = item.getObjectNamePlural(isEnglish)
        return NumberChallenge(
            id = "count_${System.currentTimeMillis()}",
            type = NumberGameType.COUNT,
            question = if (isEnglish) "How many objects are here?" else "¿Cuántos objetos hay aquí?",
            spokenQuestion = if (isEnglish) "How many objects do you see on the screen? Let's count them!" else "¿Cuántos objetos ves en la pantalla? ¡Vamos a contarlos!",
            visualItems = visualList,
            options = options,
            correctAnswer = count,
            explanation = if (isEnglish) "Exact! There are $count $pluralName. You counted very well!" else "¡Exacto! Hay $count $pluralName. ¡Contaste muy bien!"
        )
    }

    /**
     * 3. Número Mayor y Menor: "¿Cuál número es MAYOR?" (del 1 al 20)
     */
    private fun generateCompare(isEnglish: Boolean): NumberChallenge {
        val numA = random.nextInt(1, 21)
        var numB = random.nextInt(1, 21)
        while (numB == numA) {
            numB = random.nextInt(1, 21)
        }

        val askGreater = random.nextBoolean()
        val correct = if (askGreater) maxOf(numA, numB) else minOf(numA, numB)
        val word = if (isEnglish) {
            if (askGreater) "GREATER" else "SMALLER"
        } else {
            if (askGreater) "MAYOR" else "MENOR"
        }
        val itemA = NumberCatalog.NUMBERS_1_TO_20.first { it.number == numA }
        val itemB = NumberCatalog.NUMBERS_1_TO_20.first { it.number == numB }

        return NumberChallenge(
            id = "compare_${System.currentTimeMillis()}",
            type = NumberGameType.COMPARE,
            question = if (isEnglish) "Which number is $word?" else "¿Cuál número es $word?",
            spokenQuestion = if (isEnglish) "Which of these two numbers is $word? $numA or $numB?" else "¿Cuál de estos dos números es $word? ¿El $numA o el $numB?",
            visualItems = listOf(itemA.emojiObject, itemB.emojiObject),
            options = listOf(numA, numB).shuffled(random),
            correctAnswer = correct,
            explanation = if (isEnglish) "Awesome! $correct is $word." else "¡Excelente! El $correct es $word."
        )
    }

    /**
     * 4. Completar números: "12, 13, ⬜, 15, 16" (dentro del 1 al 20)
     */
    private fun generateSequence(isEnglish: Boolean): NumberChallenge {
        val start = random.nextInt(1, 17)
        val fullSeq = listOf(start, start + 1, start + 2, start + 3, start + 4)
        val missingIndex = random.nextInt(1, 4) // No dejar el primero ni el último para mejor contexto
        val missingValue = fullSeq[missingIndex]

        val displayed = fullSeq.mapIndexed { idx, v -> if (idx == missingIndex) null else v }

        val distractors = (1..20).filter { it != missingValue }.shuffled(random).take(3)
        val options = (distractors + missingValue).shuffled(random)

        return NumberChallenge(
            id = "seq_${System.currentTimeMillis()}",
            type = NumberGameType.COMPLETE,
            question = if (isEnglish) "Which number is missing in the empty box?" else "¿Qué número falta en la casilla vacía?",
            spokenQuestion = if (isEnglish) "Which number is missing in the row of numbers?" else "¿Qué número falta en la fila de números?",
            displayedSequence = displayed,
            options = options,
            correctAnswer = missingValue,
            explanation = if (isEnglish) "Great! The missing number was $missingValue." else "¡Genial! El número que faltaba era el $missingValue."
        )
    }

    /**
     * 5. Ordenar números:
     * - Menor a mayor (ascendente): "¿Cuál es el número MENOR para comenzar a ordenar?"
     * - Mayor a menor (descendente): "¿Cuál es el número MAYOR para comenzar a ordenar?"
     */
    private fun generateOrder(isEnglish: Boolean, orderDescending: Boolean? = null): NumberChallenge {
        val isDesc = orderDescending ?: random.nextBoolean()
        val nums = (1..20).shuffled(random).take(3).sorted()
        val target = if (isDesc) nums.last() else nums.first()
        val scrambled = nums.shuffled(random)

        val questionText = if (isDesc) {
            if (isEnglish) "Which number is the GREATEST to start ordering?" else "¿Cuál es el número MAYOR para comenzar a ordenar?"
        } else {
            if (isEnglish) "Which number is the SMALLEST to start ordering?" else "¿Cuál es el número MENOR para comenzar a ordenar?"
        }

        val spokenText = if (isDesc) {
            if (isEnglish) "To order from greatest to smallest, which number goes first?" else "Para ordenar de mayor a menor, ¿cuál número va primero?"
        } else {
            if (isEnglish) "To order from smallest to greatest, which number goes first?" else "Para ordenar de menor a mayor, ¿cuál número va primero?"
        }

        val explanationText = if (isDesc) {
            if (isEnglish) "Great job! $target is the greatest number in the list." else "¡Muy bien! $target es el número más grande de la lista."
        } else {
            if (isEnglish) "Great job! $target is the smallest number in the list." else "¡Muy bien! $target es el más pequeño de la lista."
        }

        return NumberChallenge(
            id = "order_${System.currentTimeMillis()}",
            type = NumberGameType.ORDER,
            question = questionText,
            spokenQuestion = spokenText,
            options = scrambled,
            correctAnswer = target,
            explanation = explanationText,
            isDescending = isDesc
        )
    }
}

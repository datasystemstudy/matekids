package com.educa.mathkids.domain.model.problemario

import com.educa.mathkids.domain.model.OperationType

enum class ProblemCategory(
    val title: String,
    val subtitle: String,
    val symbol: String,
    val iconEmoji: String,
    val colorHex: Long,
    val operationType: OperationType
) {
    SUMA(
        title = "SUMA",
        subtitle = "Aprende a juntar cantidades",
        symbol = "+",
        iconEmoji = "🍎",
        colorHex = 0xFF4CAF50, // Verde fresco
        operationType = OperationType.ADDITION
    ),
    RESTA(
        title = "RESTA",
        subtitle = "Aprende a quitar cantidades",
        symbol = "−",
        iconEmoji = "🍪",
        colorHex = 0xFFFF7043, // Naranja cálido
        operationType = OperationType.SUBTRACTION
    ),
    MULTIPLICACION(
        title = "MULTIPLICACIÓN",
        subtitle = "Aprende a formar grupos",
        symbol = "×",
        iconEmoji = "⭐",
        colorHex = 0xFFAB47BC, // Púrpura amigable
        operationType = OperationType.MULTIPLICATION
    ),
    DIVISION(
        title = "DIVISIÓN",
        subtitle = "Aprende a repartir",
        symbol = "÷",
        iconEmoji = "🍬",
        colorHex = 0xFF29B6F6, // Celeste brillante
        operationType = OperationType.DIVISION
    )
}

enum class ProblemDifficulty(
    val displayName: String,
    val description: String,
    val badge: String
) {
    FACIL(
        displayName = "Fácil",
        description = "Números del 1 al 5",
        badge = "🟢"
    ),
    MEDIO(
        displayName = "Medio",
        description = "Números del 1 al 10",
        badge = "🟡"
    ),
    AVANZADO(
        displayName = "Avanzado",
        description = "Retos hasta 20",
        badge = "🟣"
    )
}

/**
 * Modalidad de resolución pedagógica para los problemas del Problemario.
 */
enum class ProblemSolveMode(
    val title: String,
    val instruction: String,
    val iconEmoji: String
) {
    SELECCION(
        title = "Selección",
        instruction = "Elige la respuesta correcta:",
        iconEmoji = "🔘"
    ),
    COMPLETACION(
        title = "Completación",
        instruction = "Toca el número que completa la operación:",
        iconEmoji = "🧩"
    ),
    ESCRITA(
        title = "Completación Escrita",
        instruction = "Escribe el resultado con el teclado:",
        iconEmoji = "✏️"
    )
}

/**
 * Modelo de datos extensible para los problemas matemáticos del Problemario Infantil.
 * Diseñado pedagógicamente para niños de 4 a 6 años.
 */
data class MathProblem(
    val id: String,
    val category: ProblemCategory,
    val difficulty: ProblemDifficulty,
    val title: String,
    val story: String,
    val question: String,
    val num1: Int,
    val num2: Int,
    val correctAnswer: Int,
    val options: List<Int>,
    val itemEmoji: String,
    val itemName: String,
    val equationText: String,
    val pedagogicalExplanation: String,
    val solveMode: ProblemSolveMode = ProblemSolveMode.SELECCION,
    // Atributos específicos para Multiplicación (grupos iguales)
    val groupCount: Int? = null,
    val itemsPerGroup: Int? = null,
    val groupName: String? = null,
    // Atributos específicos para División (repartir)
    val recipientCount: Int? = null,
    val recipientEmojis: List<String> = listOf("👧", "👦"),
    // Campos opcionales para localización en inglés
    val titleEn: String = "",
    val storyEn: String = "",
    val questionEn: String = "",
    val itemNameEn: String = "",
    val groupNameEn: String? = null,
    val pedagogicalExplanationEn: String = ""
) {
    fun getTitle(isEnglish: Boolean): String =
        if (isEnglish) {
            if (titleEn.isNotBlank()) titleEn
            else com.educa.mathkids.data.repository.problem_bank.ProblemTranslationDictionary.getTranslation(id)?.titleEn
                ?: "Problem ${title.filter { it.isDigit() }.ifEmpty { "1" }}"
        } else title

    fun getStory(isEnglish: Boolean): String =
        if (isEnglish) {
            if (storyEn.isNotBlank()) storyEn
            else com.educa.mathkids.data.repository.problem_bank.ProblemTranslationDictionary.getTranslation(id)?.storyEn ?: story
        } else story

    fun getQuestion(isEnglish: Boolean): String =
        if (isEnglish) {
            if (questionEn.isNotBlank()) questionEn
            else com.educa.mathkids.data.repository.problem_bank.ProblemTranslationDictionary.getTranslation(id)?.questionEn ?: question
        } else question

    fun getItemName(isEnglish: Boolean): String =
        if (isEnglish) {
            if (itemNameEn.isNotBlank()) itemNameEn
            else com.educa.mathkids.data.repository.problem_bank.ProblemTranslationDictionary.getTranslation(id)?.itemNameEn ?: itemName
        } else itemName

    fun getGroupName(isEnglish: Boolean): String? =
        if (isEnglish) {
            if (!groupNameEn.isNullOrBlank()) groupNameEn
            else com.educa.mathkids.data.repository.problem_bank.ProblemTranslationDictionary.getTranslation(id)?.groupNameEn ?: groupName
        } else groupName

    fun getPedagogicalExplanation(isEnglish: Boolean): String =
        if (isEnglish) {
            if (pedagogicalExplanationEn.isNotBlank()) pedagogicalExplanationEn
            else com.educa.mathkids.data.repository.problem_bank.ProblemTranslationDictionary.getTranslation(id)?.pedagogicalExplanationEn ?: pedagogicalExplanation
        } else pedagogicalExplanation
}

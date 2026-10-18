package com.educa.mathkids.domain.model

/**
 * Representa las tres etapas pedagógicas de evaluación para cada nivel:
 * 1. Selección Simple: Resolución directa eligiendo entre opciones múltiples.
 * 2. Completación con Selección: Ecuación con incógnita (?) completada eligiendo entre opciones.
 * 3. Completación Escrita: Ecuación con incógnita (?) donde el estudiante escribe la cantidad correcta con el teclado.
 */
enum class EvaluationStage(
    val stageNumber: Int,
    val title: String,
    val shortName: String,
    val description: String,
    val badgeIcon: String
) {
    SIMPLE_SELECTION(
        stageNumber = 1,
        title = "Etapa 1: Selección Simple",
        shortName = "Selección Simple",
        description = "Resuelve y elige la respuesta correcta",
        badgeIcon = "🎯"
    ),
    COMPLETION_SELECTION(
        stageNumber = 2,
        title = "Etapa 2: Completar con Selección",
        shortName = "Completar Opción",
        description = "Descubre el número que falta eligiendo la opción",
        badgeIcon = "🧩"
    ),
    COMPLETION_WRITTEN(
        stageNumber = 3,
        title = "Etapa 3: Escribir la Respuesta",
        shortName = "Completar Escrito",
        description = "Escribe la cantidad correcta con el teclado",
        badgeIcon = "✍️"
    );

    companion object {
        /**
         * Determina la etapa de evaluación correspondiente según el índice del ejercicio
         * dentro del total de la sesión.
         */
        fun fromIndex(index: Int, total: Int): EvaluationStage {
            if (total <= 0) return SIMPLE_SELECTION
            val third = (total / 3).coerceAtLeast(1)
            val twoThirds = (2 * total / 3).coerceAtLeast(third + 1)
            return when {
                index < third -> SIMPLE_SELECTION
                index < twoThirds -> COMPLETION_SELECTION
                else -> COMPLETION_WRITTEN
            }
        }
    }
}

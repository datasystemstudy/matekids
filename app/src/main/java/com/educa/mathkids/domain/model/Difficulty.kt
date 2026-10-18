package com.educa.mathkids.domain.model

enum class Difficulty(
    val levelNumber: Int,
    val title: String,
    val stars: Int
) {
    EASY(1, "Fácil", 1),
    BASIC(2, "Básico", 2),
    INTERMEDIATE(3, "Intermedio", 3),
    ADVANCED(4, "Avanzado", 4)
}

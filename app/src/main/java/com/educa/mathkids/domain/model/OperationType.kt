package com.educa.mathkids.domain.model

enum class OperationType(
    val symbol: String,
    val displayName: String,
    val adventureName: String,
    val colorHex: Long,
    val displayNameEn: String = displayName,
    val adventureNameEn: String = adventureName
) {
    ADDITION("+", "Sumar", "➕ Suma Aventurera", 0xFF1E88E5, "Add", "➕ Addition Adventure"),
    SUBTRACTION("-", "Restar", "➖ Resta Aventurera", 0xFF4CAF50, "Subtract", "➖ Subtraction Adventure"),
    MULTIPLICATION("×", "Multiplicar", "✖️ Fábrica de Grupos", 0xFF9C27B0, "Multiply", "✖️ Group Factory"),
    DIVISION("÷", "Dividir", "➗ Reparto Mágico", 0xFFFF9800, "Divide", "➗ Magical Sharing");

    fun getDisplayName(isEnglish: Boolean): String = if (isEnglish) displayNameEn else displayName
    fun getAdventureName(isEnglish: Boolean): String = if (isEnglish) adventureNameEn else adventureName
}

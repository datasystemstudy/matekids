package com.educa.mathkids.domain.model

/**
 * Estrategias didácticas educativas para el aprendizaje y comprensión profunda
 * de los números y las operaciones aritméticas (Suma, Resta, Multiplicación y División).
 */
enum class DidacticStrategy(
    val title: String,
    val description: String,
    val iconEmoji: String,
    val pedagogicalGoal: String
) {
    CONCRETE_OBJECTS(
        title = "Conteo con Objetos",
        description = "Suma y resta manipulando frutas, juguetes o animales.",
        iconEmoji = "🍎",
        pedagogicalGoal = "Asociación número-cantidad y conteo uno a uno."
    ),
    NUMBER_LINE(
        title = "Recta Numérica",
        description = "Saltos adelante y atrás en la línea graduada.",
        iconEmoji = "📏",
        pedagogicalGoal = "Comprensión de magnitud, orden, avance y retroceso."
    ),
    TEN_FRAME(
        title = "Marco de Diez",
        description = "Organización visual en cuadrículas de 10 casillas (Singapur / ABN).",
        iconEmoji = "🔟",
        pedagogicalGoal = "Subitización (reconocimiento visual instantáneo) y amigos del 10."
    ),
    NUMBER_BONDS(
        title = "Enlaces Numéricos",
        description = "Descomposición Parte-Parte-Todo (Singapore Math).",
        iconEmoji = "🌿",
        pedagogicalGoal = "Comprensión estructural de la composición y descomposición de números."
    ),
    MISSING_NUMBER(
        title = "Número Misterioso",
        description = "Descubre la incógnita faltante que completa la igualdad.",
        iconEmoji = "🕵️",
        pedagogicalGoal = "Pensamiento pre-algebraico y relación inversa entre operaciones."
    ),
    ARRAY_GRID(
        title = "Matriz Rectangular",
        description = "Filas y columnas para visualizar factores y productos.",
        iconEmoji = "🧱",
        pedagogicalGoal = "Propiedad conmutativa y sentido geométrico de la multiplicación."
    ),
    EQUAL_SHARING(
        title = "Reparto Equitativo",
        description = "Distribución justa y equitativa en canastas o recipientes.",
        iconEmoji = "🧺",
        pedagogicalGoal = "Comprensión intuitiva del cociente, partición equitativa y residuo cero."
    ),
    SKIP_COUNTING(
        title = "Conteo a Saltos y Patrones",
        description = "Series rítmicas de 2 en 2, 3 en 3, 5 en 5 o 10 en 10.",
        iconEmoji = "🦘",
        pedagogicalGoal = "Patrones numéricos, ritmo matemático y cimiento de las tablas."
    ),
    BALANCE_SCALE(
        title = "Balanza de Equilibrio",
        description = "Equivalencia numérica: ambos platillos deben pesar igual.",
        iconEmoji = "⚖️",
        pedagogicalGoal = "Comprensión del signo igual '=' como equivalencia y equilibrio."
    ),
    BASE_TEN_BLOCKS(
        title = "Bloques Base 10",
        description = "Barras de decenas (10) y cubos de unidades (1).",
        iconEmoji = "🟦",
        pedagogicalGoal = "Sistema de numeración decimal y valor posicional."
    ),
    WORD_PROBLEM(
        title = "Problemas Cotidianos",
        description = "Historias y retos matemáticos contextualizados en la vida diaria.",
        iconEmoji = "💡",
        pedagogicalGoal = "Razonamiento lógico aplicado a situaciones del entorno real."
    )
}

/**
 * Identifica qué término de la ecuación es la incógnita que el alumno debe resolver.
 */
enum class MissingTarget {
    RESULT,          // A + B = ? (la incógnita es el resultado)
    FIRST_OPERAND,   // ? + B = C (la incógnita es el primer término)
    SECOND_OPERAND   // A + ? = C (la incógnita es el segundo término)
}

/**
 * Identifica qué elemento del enlace numérico (Number Bond) es la incógnita.
 */
enum class BondElement {
    WHOLE,   // El Todo superior
    PART_A,  // La primera parte inferior
    PART_B   // La segunda parte inferior
}

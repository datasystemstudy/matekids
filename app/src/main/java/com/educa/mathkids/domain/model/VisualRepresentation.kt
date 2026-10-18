package com.educa.mathkids.domain.model

/**
 * Tipos de representaciones visuales interactivas y progresivas
 * según las Fases 2 a 8 del proyecto.
 */
enum class VisualType {
    OBJECT_COUNT,       // Suma / Conteo: Objetos del grupo A + Objetos del grupo B
    OBJECT_TAKEOUT,     // Resta: Objetos totales donde se quitan/tachan N elementos
    GROUPS_GRID,        // Multiplicación: N grupos o canastas con M elementos cada una
    SHARING_PARTITION,  // División: Reparto de N objetos entre M recipientes iguales
    BASE_TEN_BLOCKS,    // Decenas (barras) y Unidades (cubos) para dos cifras
    WORD_PROBLEM,       // Problemas cotidianos y razonados con apoyo contextual
    NUMBER_FOCUS,       // Operación numérica con apoyo visual sutil debajo
    NUMBER_LINE,        // Recta numérica interactiva con saltos animados adelante y atrás
    TEN_FRAME,          // Marco de 10 casillas (y doble marco hasta 20) para subitización y decenas
    NUMBER_BONDS,       // Enlaces numéricos Singapur: descomposición Todo (Whole) y Partes (Parts)
    ARRAY_GRID,         // Matriz rectangular de filas × columnas (modelo geométrico del producto)
    SKIP_COUNTING,      // Secuencia de números con saltos rítmicos y tarjetas de incógnita
    BALANCE_SCALE       // Balanza didáctica de equivalencia numérica (ambos platillos iguales)
}

/**
 * Tipos de temática para los objetos visuales
 */
enum class VisualTheme(
    val title: String,
    val items: List<String>
) {
    FRUITS("Frutas", listOf("🍎", "🍌", "🍊", "🍓", "🍉", "🍇")),
    ANIMALS("Animales", listOf("🐶", "🐱", "🐰", "🐸", "🐻", "🐼")),
    STARS("Estrellas", listOf("⭐", "🌟", "✨", "💫")),
    BLOCKS("Bloques", listOf("🟦", "🟩", "🟪", "🟧", "🟨", "🔷")),
    COINS("Monedas y Gemas", listOf("🪙", "💎", "🥇", "💰")),
    GEOMETRIC("Figuras", listOf("🟢", "🔺", "🔷", "⭐", "🔶", "🟣")),
    TECH("Tecnología", listOf("🚀", "🤖", "🛸", "🪐", "⚡", "🛰️"));

    fun randomEmoji(): String = items.random()
}

/**
 * Detalle de la representación visual para alimentar la UI
 */
data class VisualRepresentation(
    val visualType: VisualType,
    val theme: VisualTheme = VisualTheme.FRUITS,
    val primaryEmoji: String = "🍎",
    val secondaryEmoji: String = "🍌",

    // Para OBJECT_COUNT
    val countA: Int = 0,
    val countB: Int = 0,

    // Para OBJECT_TAKEOUT (Resta: total - removeCount)
    val totalCount: Int = 0,
    val removeCount: Int = 0,

    // Para GROUPS_GRID (Multiplicación: groupCount grupos de itemsPerGroup)
    val groupCount: Int = 0,
    val itemsPerGroup: Int = 0,

    // Para SHARING_PARTITION (División: totalToShare repartido en containerCount recipientes)
    val totalToShare: Int = 0,
    val containerCount: Int = 0,
    val itemsPerContainer: Int = 0,

    // Para BASE_TEN_BLOCKS (24 -> tens: 2, units: 4)
    val tensA: Int = 0,
    val unitsA: Int = 0,
    val tensB: Int = 0,
    val unitsB: Int = 0,
    val isSubtraction: Boolean = false,

    // Para WORD_PROBLEM
    val problemStory: String? = null,
    val problemIcon: String = "💡",

    // 📏 Para NUMBER_LINE (Recta Numérica)
    val numberLineStart: Int = 0,
    val numberLineEnd: Int = 10,
    val numberLineInitial: Int = 0,
    val numberLineJump: Int = 0,
    val numberLineDestination: Int = 0,
    val numberLineRepeatedJumps: Int = 1,
    val isJumpForward: Boolean = true,

    // 🔟 Para TEN_FRAME (Marco de Diez)
    val tenFrameCountA: Int = 0,
    val tenFrameCountB: Int = 0,
    val tenFrameTakeout: Int = 0,
    val tenFrameCapacity: Int = 10,

    // 🌿 Para NUMBER_BONDS (Enlaces Numéricos Parte-Parte-Todo)
    val bondWhole: Int = 0,
    val bondPartA: Int = 0,
    val bondPartB: Int = 0,
    val missingBondElement: BondElement? = null,

    // 🧱 Para ARRAY_GRID (Matriz Rectangular)
    val arrayRows: Int = 0,
    val arrayCols: Int = 0,

    // 🦘 Para SKIP_COUNTING (Secuencias a saltos)
    val sequenceItems: List<Int?> = emptyList(),
    val sequenceStep: Int = 0,

    // ⚖️ Para BALANCE_SCALE (Balanza de Equivalencia)
    val scaleLeftText: String = "",
    val scaleRightText: String = "",
    val scaleIsBalanced: Boolean = true
)

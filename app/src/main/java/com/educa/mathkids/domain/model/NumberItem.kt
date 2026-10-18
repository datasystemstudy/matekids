package com.educa.mathkids.domain.model

/**
 * Representación didáctica de un número para el módulo "Explorando los Números".
 */
data class NumberItem(
    val number: Int,
    val wordSpanish: String,
    val emojiObject: String,
    val objectNamePlural: String,
    val colorHex: Long,
    val funFact: String,
    val wordEnglish: String = "",
    val objectNamePluralEnglish: String = "",
    val funFactEnglish: String = ""
) {
    fun getWord(isEnglish: Boolean): String = if (isEnglish && wordEnglish.isNotBlank()) wordEnglish else wordSpanish
    fun getObjectNamePlural(isEnglish: Boolean): String = if (isEnglish && objectNamePluralEnglish.isNotBlank()) objectNamePluralEnglish else objectNamePlural
    fun getFunFact(isEnglish: Boolean): String = if (isEnglish && funFactEnglish.isNotBlank()) funFactEnglish else funFact
}

object NumberCatalog {
    val NUMBERS_1_TO_20 = listOf(
        NumberItem(1, "Uno", "☀️", "soles", 0xFFFF9800, "1 sol brillante en el cielo", "One", "suns", "1 bright sun in the sky"),
        NumberItem(2, "Dos", "👀", "ojitos", 0xFF2196F3, "2 ojos para ver el mundo", "Two", "eyes", "2 eyes to see the world"),
        NumberItem(3, "Tres", "🔺", "triángulos", 0xFF4CAF50, "3 lados de un triángulo", "Three", "triangles", "3 sides on a triangle"),
        NumberItem(4, "Cuatro", "🐱", "gatitos", 0xFFE91E63, "4 patitas tiene un gatito", "Four", "kittens", "4 paws on a kitten"),
        NumberItem(5, "Cinco", "🍎", "manzanas", 0xFFFF5722, "5 dedos en tu mano", "Five", "apples", "5 fingers on your hand"),
        NumberItem(6, "Seis", "🐞", "mariquitas", 0xFF9C27B0, "6 patitas de una mariquita", "Six", "ladybugs", "6 legs on a ladybug"),
        NumberItem(7, "Siete", "🌈", "arcoíris", 0xFF00BCD4, "7 colores del arcoíris", "Seven", "rainbows", "7 colors in the rainbow"),
        NumberItem(8, "Ocho", "🐙", "pulpos", 0xFF3F51B5, "8 tentáculos de un pulpo", "Eight", "octopuses", "8 tentacles on an octopus"),
        NumberItem(9, "Nueve", "🎈", "globos", 0xFFFF4081, "9 globos flotando al cielo", "Nine", "balloons", "9 balloons floating high"),
        NumberItem(10, "Diez", "⭐", "estrellas", 0xFFFFC107, "10 deditos en tus dos manos", "Ten", "stars", "10 fingers on both hands"),
        NumberItem(11, "Once", "⚽", "balones", 0xFF4CAF50, "11 jugadores en un equipo de fútbol", "Eleven", "soccer balls", "11 players on a soccer team"),
        NumberItem(12, "Doce", "🍩", "donas", 0xFF9C27B0, "12 donas en una docena deliciosa", "Twelve", "donuts", "12 donuts in a yummy dozen"),
        NumberItem(13, "Trece", "🍀", "tréboles", 0xFF2E7D32, "13 tréboles de la buena suerte", "Thirteen", "clovers", "13 lucky clovers"),
        NumberItem(14, "Catorce", "🍫", "chocolates", 0xFF795548, "14 días son dos semanas completas", "Fourteen", "chocolates", "14 days make two weeks"),
        NumberItem(15, "Quince", "🎂", "velitas", 0xFFE91E63, "15 minutos es un cuarto de hora", "Fifteen", "candles", "15 minutes is a quarter hour"),
        NumberItem(16, "Dieciséis", "🚀", "cohetes", 0xFF00BCD4, "16 piezas en el ajedrez por bando", "Sixteen", "rockets", "16 chess pieces per side"),
        NumberItem(17, "Diecisiete", "🐬", "delfines", 0xFF009688, "17 saltos alegres en el mar", "Seventeen", "dolphins", "17 happy leaps in the ocean"),
        NumberItem(18, "Dieciocho", "🚗", "ruedas", 0xFFFF5722, "18 ruedas tiene un gran camión", "Eighteen", "wheels", "18 wheels on a big truck"),
        NumberItem(19, "Diecinueve", "🎨", "pinceles", 0xFF673AB7, "19 colores en la paleta del pintor", "Nineteen", "brushes", "19 colors on the painter's palette"),
        NumberItem(20, "Veinte", "💎", "diamantes", 0xFFFFC107, "20 dedos tenemos entre manos y pies", "Twenty", "diamonds", "20 fingers and toes altogether")
    )

    val NUMBERS_1_TO_10 = NUMBERS_1_TO_20.take(10)
}

enum class NumberGameType(val title: String, val iconEmoji: String, val titleEn: String = title) {
    EXPLORE("Explorar", "🔢", "Explore"),
    IDENTIFY("Identificar", "🎯", "Identify"),
    COUNT("Contar", "🍎", "Count"),
    ORDER("Ordenar", "📊", "Order"),
    COMPARE("Mayor y Menor", "⚖️", "Compare"),
    COMPLETE("Completar", "🧩", "Complete");

    fun getTitle(isEnglish: Boolean): String = if (isEnglish) titleEn else title
}

data class NumberChallenge(
    val id: String,
    val type: NumberGameType,
    val question: String,
    val spokenQuestion: String,
    val visualItems: List<String> = emptyList(),
    val displayedSequence: List<Int?> = emptyList(), // Para completar: listOf(1, 2, null, 4)
    val options: List<Int>,
    val correctAnswer: Int,
    val explanation: String,
    val isDescending: Boolean = false
)

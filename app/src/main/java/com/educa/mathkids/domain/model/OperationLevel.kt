package com.educa.mathkids.domain.model

/**
 * Representa el estado y metadata de un nivel específico (1..10)
 * para una de las cuatro operaciones.
 */
data class OperationLevel(
    val operationType: OperationType,
    val levelNumber: Int,
    val title: String,
    val subtitle: String,
    val isUnlocked: Boolean = false,
    val stars: Int = 0,
    val bestScore: Int = 0,
    val exercisesCompleted: Int = 0,
    val iconEmoji: String = "⭐"
) {
    val isCompleted: Boolean get() = stars > 0
}

object OperationLevelConfig {

    const val MAX_LEVELS_PER_OPERATION = 15

    fun getDefaultLevels(operationType: OperationType): List<OperationLevel> {
        return when (operationType) {
            OperationType.ADDITION -> additionLevels
            OperationType.SUBTRACTION -> subtractionLevels
            OperationType.MULTIPLICATION -> multiplicationLevels
            OperationType.DIVISION -> divisionLevels
        }
    }

    private val additionLevels: List<OperationLevel> by lazy {
        val op = OperationType.ADDITION
        listOf(
            OperationLevel(op, 1, "Introducción Visual", "Sumas sencillas con frutas y objetos (1+1, 2+1)", isUnlocked = true, iconEmoji = "🍎"),
            OperationLevel(op, 2, "Visual y Números", "Combina objetos con números (3+2, 4+3)", isUnlocked = false, iconEmoji = "🔢"),
            OperationLevel(op, 3, "Números Pequeños", "Sumas numéricas con apoyo visual hasta 10", isUnlocked = false, iconEmoji = "⭐"),
            OperationLevel(op, 4, "Amigos del 10", "Parejas mágicas que suman 10", isUnlocked = false, iconEmoji = "🤝"),
            OperationLevel(op, 5, "Dos Cifras sin Llevar", "Bloques de decenas y unidades", isUnlocked = false, iconEmoji = "🟦"),
            OperationLevel(op, 6, "Dos Cifras con Llevadas", "Agrupando decenas con llevadas", isUnlocked = false, iconEmoji = "🧩"),
            OperationLevel(op, 7, "Sumas de Tres Cifras", "Operaciones mayores con centenas", isUnlocked = false, iconEmoji = "🚀"),
            OperationLevel(op, 8, "Problemas Visuales", "Historias con objetos cotidianos", isUnlocked = false, iconEmoji = "🎨"),
            OperationLevel(op, 9, "Problemas Matemáticos", "Razonamiento y contexto", isUnlocked = false, iconEmoji = "🧠"),
            OperationLevel(op, 10, "Desafío Maestro I", "Primera prueba de maestría con estrellas", isUnlocked = false, iconEmoji = "👑"),
            OperationLevel(op, 11, "Decena Más Unidades", "10 más un número pequeño (10+3, 10+7)", isUnlocked = false, iconEmoji = "🔟"),
            OperationLevel(op, 12, "Sumar con Monedas", "Monedas de $1 y $10 en la tienda", isUnlocked = false, iconEmoji = "🪙"),
            OperationLevel(op, 13, "Dobles Sencillos", "Sumando números iguales: 3+3, 4+4, 5+5", isUnlocked = false, iconEmoji = "👯"),
            OperationLevel(op, 14, "Dobles Más Uno", "Estrategia de dobles vecinos: 4+5, 6+7", isUnlocked = false, iconEmoji = "⚡"),
            OperationLevel(op, 15, "Gran Desafío Maestro de Suma", "¡Corona legendaria de Suma Aventurera!", isUnlocked = false, iconEmoji = "👑")
        )
    }

    private val subtractionLevels: List<OperationLevel> by lazy {
        val op = OperationType.SUBTRACTION
        listOf(
            OperationLevel(op, 1, "Restas hasta 5", "Aprende a quitar y tachar objetos", isUnlocked = true, iconEmoji = "🍎"),
            OperationLevel(op, 2, "Restas hasta 10", "Conteo inverso con frutas y estrellas", isUnlocked = false, iconEmoji = "⭐"),
            OperationLevel(op, 3, "Restas hasta 20", "Números pequeños con apoyo visual", isUnlocked = false, iconEmoji = "🔢"),
            OperationLevel(op, 4, "Apoyo Visual Guiado", "Tacha y descubre lo que queda", isUnlocked = false, iconEmoji = "✂️"),
            OperationLevel(op, 5, "Dos Cifras sin Prestar", "Restas con bloques de decenas", isUnlocked = false, iconEmoji = "🟩"),
            OperationLevel(op, 6, "Dos Cifras con Préstamo", "Descomponiendo decenas", isUnlocked = false, iconEmoji = "🔄"),
            OperationLevel(op, 7, "Restas de Tres Cifras", "Cálculos mayores con centenas", isUnlocked = false, iconEmoji = "🚀"),
            OperationLevel(op, 8, "Problemas Visuales", "Animales y objetos que se van", isUnlocked = false, iconEmoji = "🌳"),
            OperationLevel(op, 9, "Problemas Cotidianos", "Situaciones de la vida real", isUnlocked = false, iconEmoji = "💡"),
            OperationLevel(op, 10, "Desafío Maestro I", "Primera prueba de maestría con estrellas", isUnlocked = false, iconEmoji = "👑"),
            OperationLevel(op, 11, "Restar Cero y Todo", "5-0=5 y 5-5=0, ¡propiedades mágicas!", isUnlocked = false, iconEmoji = "🪄"),
            OperationLevel(op, 12, "Amigos del 10 Inversos", "Restando desde el 10: 10-3, 10-7", isUnlocked = false, iconEmoji = "🔟"),
            OperationLevel(op, 13, "Quitar una Decena", "17-10, 15-10 de forma instantánea", isUnlocked = false, iconEmoji = "⚡"),
            OperationLevel(op, 14, "Saltos hacia Atrás", "Retrocediendo en la recta numérica", isUnlocked = false, iconEmoji = "🐸"),
            OperationLevel(op, 15, "Gran Desafío Maestro de Resta", "¡Corona legendaria de Resta Aventurera!", isUnlocked = false, iconEmoji = "👑")
        )
    }

    private val multiplicationLevels: List<OperationLevel> by lazy {
        val op = OperationType.MULTIPLICATION
        listOf(
            OperationLevel(op, 1, "Grupos de 2", "Multiplicación como grupos iguales", isUnlocked = true, iconEmoji = "🍒"),
            OperationLevel(op, 2, "Grupos de 3", "Canastas con 3 objetos cada una", isUnlocked = false, iconEmoji = "🧺"),
            OperationLevel(op, 3, "Grupos de 4 y 5", "Matrices de filas y columnas", isUnlocked = false, iconEmoji = "🧱"),
            OperationLevel(op, 4, "Tablas del 1 al 5", "Aprendizaje visual de las primeras tablas", isUnlocked = false, iconEmoji = "📊"),
            OperationLevel(op, 5, "Tablas del 1 al 10", "Dominio completo de tablas", isUnlocked = false, iconEmoji = "🎯"),
            OperationLevel(op, 6, "Dos Cifras × Una Cifra", "Multiplicaciones con números mayores", isUnlocked = false, iconEmoji = "⚡"),
            OperationLevel(op, 7, "Dos Cifras Intermedias", "Cálculo y estimación", isUnlocked = false, iconEmoji = "🚀"),
            OperationLevel(op, 8, "Problemas Visuales", "Cajas, estantes y grupos de juguetes", isUnlocked = false, iconEmoji = "🎁"),
            OperationLevel(op, 9, "Problemas Matemáticos", "Razonamiento multiplicativo", isUnlocked = false, iconEmoji = "🧠"),
            OperationLevel(op, 10, "Desafío Maestro I", "Primera prueba de maestría con estrellas", isUnlocked = false, iconEmoji = "👑"),
            OperationLevel(op, 11, "Multiplicar por 1 y 0", "Reglas mágicas del uno y del cero", isUnlocked = false, iconEmoji = "🪄"),
            OperationLevel(op, 12, "Tabla del 2 Completa", "Pares desde 2×1 hasta 2×10", isUnlocked = false, iconEmoji = "✌️"),
            OperationLevel(op, 13, "Dobles y Duplicación", "El poder de multiplicar por dos", isUnlocked = false, iconEmoji = "⚡"),
            OperationLevel(op, 14, "Tabla del 3 Completa", "Tríos desde 3×1 hasta 3×10", isUnlocked = false, iconEmoji = "📐"),
            OperationLevel(op, 15, "Gran Desafío Maestro de Multiplicar", "¡Corona dorada de Fábrica de Grupos!", isUnlocked = false, iconEmoji = "👑")
        )
    }

    private val divisionLevels: List<OperationLevel> by lazy {
        val op = OperationType.DIVISION
        listOf(
            OperationLevel(op, 1, "Reparto Sencillo", "Reparte en partes iguales entre 2 o 3", isUnlocked = true, iconEmoji = "🎁"),
            OperationLevel(op, 2, "Divisiones hasta 10", "Repartir dulces y manzanas", isUnlocked = false, iconEmoji = "🍬"),
            OperationLevel(op, 3, "Divisiones hasta 20", "Grupos y recipientes iguales", isUnlocked = false, iconEmoji = "🧺"),
            OperationLevel(op, 4, "Objetos en Recipientes", "Reparto animado en canastas", isUnlocked = false, iconEmoji = "📦"),
            OperationLevel(op, 5, "Tablas Inversas", "La división como multiplicación inversa", isUnlocked = false, iconEmoji = "🔄"),
            OperationLevel(op, 6, "Dos Cifras ÷ Una Cifra", "Divisiones exactas mayores", isUnlocked = false, iconEmoji = "⚡"),
            OperationLevel(op, 7, "Divisiones Complejas", "Desafíos numéricos más altos", isUnlocked = false, iconEmoji = "🚀"),
            OperationLevel(op, 8, "Problemas Visuales", "Compartir con amigos equitativamente", isUnlocked = false, iconEmoji = "🤝"),
            OperationLevel(op, 9, "Problemas Cotidianos", "Repartos en la escuela y el hogar", isUnlocked = false, iconEmoji = "💡"),
            OperationLevel(op, 10, "Desafío Maestro I", "Primera prueba de maestría con estrellas", isUnlocked = false, iconEmoji = "👑"),
            OperationLevel(op, 11, "Repartir entre 2", "Mitades exactas de objetos: 4÷2, 8÷2", isUnlocked = false, iconEmoji = "🍎"),
            OperationLevel(op, 12, "Repartir entre 3", "Terceras partes con dulces: 6÷3, 12÷3", isUnlocked = false, iconEmoji = "🍬"),
            OperationLevel(op, 13, "Dividir entre 1 y Sí Mismo", "5÷1=5 y 5÷5=1, ¡propiedades mágicas!", isUnlocked = false, iconEmoji = "🪄"),
            OperationLevel(op, 14, "Repartir entre 4", "Cuartas partes con canastas: 8÷4, 16÷4", isUnlocked = false, iconEmoji = "🧺"),
            OperationLevel(op, 15, "Gran Desafío Maestro de la División", "¡Corona legendaria de Reparto Mágico!", isUnlocked = false, iconEmoji = "👑")
        )
    }
}

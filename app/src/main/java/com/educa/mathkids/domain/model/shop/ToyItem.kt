package com.educa.mathkids.domain.model.shop

enum class ToyCategory(val labelEs: String, val labelEn: String) {
    ALL("Todos", "All"),
    BOYS("Para Niños 🚀", "For Boys 🚀"),
    GIRLS("Para Niñas 🎀", "For Girls 🎀")
}

data class ToyItem(
    val id: String,
    val name: String,
    val emoji: String,
    val price: Int, // 20 a 100 monedas
    val category: ToyCategory,
    val description: String,
    val isPurchased: Boolean = false
)

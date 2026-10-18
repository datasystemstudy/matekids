package com.educa.mathkids.core.shop

import android.content.Context
import android.content.SharedPreferences
import com.educa.mathkids.domain.model.shop.ToyCategory
import com.educa.mathkids.domain.model.shop.ToyItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Gestor oficial de monedas y tienda de regalos de MateKids.
 *
 * Reglas:
 * - Cada acierto en un ejercicio suma 1 moneda (🪙).
 * - Las monedas acumuladas se guardan de forma persistente.
 * - En la tienda se ofrecen 20 juguetes infantiles (para niños y niñas) con precios de 20 a 100 monedas.
 * - Los juguetes canjeados se guardan de forma permanente.
 */
object CoinShopManager {

    private const val PREF_NAME = "mathkids_coin_shop"
    private const val KEY_COINS = "accumulated_coins"
    private const val KEY_PURCHASED_TOYS = "purchased_toy_ids"
    private const val KEY_LAST_DAILY_REWARD_DATE = "last_daily_reward_date"

    // Estado reactivo para Compose
    private val _coinsState = MutableStateFlow(0)
    val coinsState: StateFlow<Int> = _coinsState.asStateFlow()

    private val _purchasedToysState = MutableStateFlow<Set<String>>(emptySet())
    val purchasedToysState: StateFlow<Set<String>> = _purchasedToysState.asStateFlow()

    private val _dailyRewardClaimedTodayState = MutableStateFlow(false)
    val dailyRewardClaimedTodayState: StateFlow<Boolean> = _dailyRewardClaimedTodayState.asStateFlow()

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    private fun getTodayDateString(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }

    /**
     * Inicializa el estado reactivo desde SharedPreferences.
     */
    fun init(context: Context) {
        val prefs = getPrefs(context)
        val coins = prefs.getInt(KEY_COINS, 0)
        val purchased = prefs.getStringSet(KEY_PURCHASED_TOYS, emptySet()) ?: emptySet()
        val lastRewardDate = prefs.getString(KEY_LAST_DAILY_REWARD_DATE, "") ?: ""
        val today = getTodayDateString()

        _coinsState.value = coins
        _purchasedToysState.value = purchased.toSet()
        _dailyRewardClaimedTodayState.value = (lastRewardDate == today)
    }

    /**
     * Verifica si el usuario puede reclamar el regalo diario (solo 1 vez por día calendario).
     */
    fun canClaimDailyReward(context: Context): Boolean {
        val prefs = getPrefs(context)
        val lastRewardDate = prefs.getString(KEY_LAST_DAILY_REWARD_DATE, "") ?: ""
        val today = getTodayDateString()
        val canClaim = lastRewardDate != today
        _dailyRewardClaimedTodayState.value = !canClaim
        return canClaim
    }

    /**
     * Reclama el regalo diario guardando la fecha de hoy para impedir reclamos repetidos el mismo día.
     * Retorna true si se reclamó con éxito, o false si ya se había reclamado hoy.
     */
    fun claimDailyReward(context: Context, amount: Int = 10): Boolean {
        if (!canClaimDailyReward(context)) return false
        val prefs = getPrefs(context)
        val today = getTodayDateString()
        prefs.edit().putString(KEY_LAST_DAILY_REWARD_DATE, today).apply()
        addCoin(context, amount)
        _dailyRewardClaimedTodayState.value = true
        return true
    }

    fun getCoins(context: Context): Int {
        val coins = getPrefs(context).getInt(KEY_COINS, 0)
        _coinsState.value = coins
        return coins
    }

    /**
     * Añade monedas al saldo acumulado (+1 por ejercicio acertado).
     */
    fun addCoin(context: Context, amount: Int = 1) {
        if (amount <= 0) return
        val prefs = getPrefs(context)
        val newCoins = prefs.getInt(KEY_COINS, 0) + amount
        prefs.edit().putInt(KEY_COINS, newCoins).apply()
        _coinsState.value = newCoins
    }

    /**
     * Comprueba si el usuario tiene monedas suficientes para el precio solicitado.
     */
    fun canAfford(context: Context, price: Int): Boolean {
        return getCoins(context) >= price
    }

    fun isToyPurchased(context: Context, toyId: String): Boolean {
        if (_purchasedToysState.value.contains(toyId)) return true
        val purchased = getPrefs(context).getStringSet(KEY_PURCHASED_TOYS, emptySet()) ?: emptySet()
        _purchasedToysState.value = purchased.toSet()
        return purchased.contains(toyId)
    }

    /**
     * Canjea un juguete por el precio en monedas especificado.
     * Retorna true si el canje fue exitoso.
     */
    fun purchaseToy(context: Context, toyId: String, price: Int): Boolean {
        val currentCoins = getCoins(context)
        if (currentCoins < price) return false

        val prefs = getPrefs(context)
        val currentPurchased = (prefs.getStringSet(KEY_PURCHASED_TOYS, emptySet()) ?: emptySet()).toMutableSet()
        currentPurchased.add(toyId)

        val newCoins = currentCoins - price
        prefs.edit()
            .putInt(KEY_COINS, newCoins)
            .putStringSet(KEY_PURCHASED_TOYS, currentPurchased)
            .apply()

        _coinsState.value = newCoins
        _purchasedToysState.value = currentPurchased.toSet()
        return true
    }

    /**
     * Catálogo oficial de los 20 juguetes infantiles con valores de 50 a 300 monedas.
     */
    val TOYS_CATALOG: List<ToyItem> = listOf(
        // Rango 50 - 80 monedas
        ToyItem("toy_pelota", "Pelota Saltarina Arcoíris", "⚽", 50, ToyCategory.BOYS, "Pelota colorida de rebote alto"),
        ToyItem("toy_crayones", "Set de Crayones Mágicos", "🖍️", 50, ToyCategory.ALL, "Estuche de colores para crear dibujos geniales"),
        ToyItem("toy_yoyo", "Yoyo Luminoso con Destellos", "🪀", 60, ToyCategory.BOYS, "Yoyo giratorio que brilla con cada truco"),
        ToyItem("toy_muneca_trapo", "Muñeca de Trapo Dulce", "🪆", 65, ToyCategory.GIRLS, "Muñeca suave con vestido floreado"),
        ToyItem("toy_carrito", "Carro de Carreras Veloz", "🏎️", 75, ToyCategory.BOYS, "Bólido rojo súper rápido para pistas"),
        ToyItem("toy_burbujas", "Máquina de Burbujas Mágicas", "🫧", 80, ToyCategory.ALL, "Genera cientos de pompas flotantes"),

        // Rango 95 - 150 monedas
        ToyItem("toy_cometa", "Cometa Arcoíris Voladora", "🪁", 95, ToyCategory.ALL, "Cometa gigante para volar en días de viento"),
        ToyItem("toy_dino", "Dinosaurio T-Rex Rugiente", "🦖", 110, ToyCategory.BOYS, "El rey de los dinosaurios con gran rugido"),
        ToyItem("toy_unicornio", "Peluche de Unicornio Mágico", "🦄", 110, ToyCategory.GIRLS, "Peluche suave con alas brillantes"),
        ToyItem("toy_rompecabezas", "Gran Rompecabezas 3D", "🧩", 130, ToyCategory.ALL, "Desafío de piezas coloridas para armar"),
        ToyItem("toy_tren", "Tren de Madera Musical", "🚂", 150, ToyCategory.ALL, "Locomotora clásica con vagones magnéticos"),

        // Rango 165 - 225 monedas
        ToyItem("toy_castillo", "Castillo de Princesa de Ensueño", "🏰", 165, ToyCategory.GIRLS, "Castillo de hadas con torres y banderas"),
        ToyItem("toy_robot", "Robot Espacial Interactivo", "🤖", 180, ToyCategory.BOYS, "Robot con luces que camina y baila"),
        ToyItem("toy_pintura", "Set de Pintura y Caballete", "🎨", 200, ToyCategory.ALL, "Acuarelas, pinceles y lienzo para pequeños artistas"),
        ToyItem("toy_tambor", "Tambor Musical Alegre", "🥁", 225, ToyCategory.ALL, "Tambor con baquetas para seguir el compás"),

        // Rango 240 - 300 monedas
        ToyItem("toy_cohete", "Cohete Espacial de Exploración", "🚀", 240, ToyCategory.BOYS, "Nave lunar lista para descubrir las estrellas"),
        ToyItem("toy_oso", "Gran Oso de Peluche Abrazable", "🧸", 260, ToyCategory.GIRLS, "Un tierno amigo gigante para dar abrazos"),
        ToyItem("toy_patineta", "Patineta Turbo con Luces", "🛹", 275, ToyCategory.BOYS, "Patineta deportiva con ruedas que iluminan"),
        ToyItem("toy_guitarra", "Guitarra Musical Rockera", "🎸", 285, ToyCategory.ALL, "Guitarra con melodías para pequeños músicos"),
        ToyItem("toy_dron", "Dron Explorador a Control", "🚁", 300, ToyCategory.BOYS, "El juguete supremo: helicóptero con hélices turbo")
    )

    /**
     * Restablece el saldo de monedas y juguetes canjeados.
     */
    fun reset(context: Context) {
        getPrefs(context).edit().clear().apply()
        _coinsState.value = 0
        _purchasedToysState.value = emptySet()
        _dailyRewardClaimedTodayState.value = false
    }
}

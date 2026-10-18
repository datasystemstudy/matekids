package com.educa.mathkids.core.shop

import android.content.Context
import com.educa.mathkids.domain.model.OperationType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Gestor persistente de cofres de recompensa de MateKids.
 * Garantiza que cada cofre se abra una única vez, entregue sus monedas
 * y permanezca abierto sin posibilidad de reclamar monedas repetidas.
 */
object ChestRewardManager {

    private const val PREF_NAME = "mathkids_chest_rewards"

    private val _claimedChestsState = MutableStateFlow<Set<String>>(emptySet())
    val claimedChestsState: StateFlow<Set<String>> = _claimedChestsState.asStateFlow()

    private fun getKey(operationType: OperationType, chestLevel: Int): String {
        return "${operationType.name}_$chestLevel"
    }

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val allKeys = prefs.all.filter { it.value == true }.keys
        _claimedChestsState.value = allKeys
    }

    fun isChestClaimed(context: Context, operationType: OperationType, chestLevel: Int): Boolean {
        val key = getKey(operationType, chestLevel)
        if (_claimedChestsState.value.contains(key)) return true
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val claimed = prefs.getBoolean(key, false)
        if (claimed) {
            _claimedChestsState.value = _claimedChestsState.value + key
        }
        return claimed
    }

    /**
     * Reclama el cofre otorgando las monedas una sola vez.
     * Retorna true si se reclamó por primera vez, o false si ya estaba abierto.
     */
    fun claimChest(context: Context, operationType: OperationType, chestLevel: Int, amount: Int = 15): Boolean {
        if (isChestClaimed(context, operationType, chestLevel)) {
            return false // Ya fue abierto previamente
        }
        val key = getKey(operationType, chestLevel)
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(key, true).apply()
        _claimedChestsState.value = _claimedChestsState.value + key
        CoinShopManager.addCoin(context, amount)
        return true
    }
}

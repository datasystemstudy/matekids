package com.educa.mathkids.domain.usecase

import com.educa.mathkids.domain.model.Reward
import com.educa.mathkids.domain.model.RewardsConfig

class CheckRewardsUseCase {

    /**
     * Evalúa las condiciones de desbloqueo de recompensas.
     * Retorna únicamente la lista de recompensas que se desbloquean en esta evaluación.
     */
    operator fun invoke(
        existingRewards: List<Reward>,
        totalPoints: Int,
        bestStreak: Int,
        totalSessionsCompleted: Int,
        additionSolved: Int,
        multiplicationSolved: Int,
        subtractionSolved: Int = 0,
        divisionSolved: Int = 0
    ): List<Reward> {
        val unlockedMap = existingRewards.associateBy { it.id }
        val newlyUnlocked = mutableListOf<Reward>()

        for (baseReward in RewardsConfig.INITIAL_REWARDS) {
            val isAlreadyUnlocked = unlockedMap[baseReward.id]?.isUnlocked == true
            if (isAlreadyUnlocked) continue

            val shouldUnlock = when (baseReward.id) {
                RewardsConfig.ID_FIRST_STAR -> totalSessionsCompleted >= 1
                RewardsConfig.ID_FIRE_STREAK -> bestStreak >= 5
                RewardsConfig.ID_STREAK_10 -> bestStreak >= 10
                RewardsConfig.ID_CENTURY -> totalPoints >= 100
                RewardsConfig.ID_ADDITION_MASTER -> additionSolved >= 10
                RewardsConfig.ID_SUBTRACTION_MASTER -> subtractionSolved >= 10
                RewardsConfig.ID_MULTIPLICATION_MASTER -> multiplicationSolved >= 10
                RewardsConfig.ID_DIVISION_MASTER -> divisionSolved >= 10
                RewardsConfig.ID_HALF_THOUSAND -> totalPoints >= 500
                RewardsConfig.ID_ALL_LEVELS -> (additionSolved >= 5 && subtractionSolved >= 5 && multiplicationSolved >= 5 && divisionSolved >= 5)
                else -> false
            }

            if (shouldUnlock) {
                newlyUnlocked.add(
                    baseReward.copy(
                        isUnlocked = true,
                        unlockedAt = System.currentTimeMillis()
                    )
                )
            }
        }

        return newlyUnlocked
    }
}

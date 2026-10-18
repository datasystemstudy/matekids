package com.educa.mathkids.domain.progression

import com.educa.mathkids.domain.model.Reward
import com.educa.mathkids.domain.model.RewardsConfig
import com.educa.mathkids.domain.usecase.CheckRewardsUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RewardsTest {

    private lateinit var checkRewardsUseCase: CheckRewardsUseCase

    @Before
    fun setUp() {
        checkRewardsUseCase = CheckRewardsUseCase()
    }

    @Test
    fun testFirstStar_unlockedAfterFirstSession() {
        val newlyUnlocked = checkRewardsUseCase(
            existingRewards = emptyList(),
            totalPoints = 80,
            bestStreak = 3,
            totalSessionsCompleted = 1,
            additionSolved = 10,
            multiplicationSolved = 0
        )

        assertTrue(newlyUnlocked.any { it.id == RewardsConfig.ID_FIRST_STAR })
    }

    @Test
    fun testFireStreak_unlockedAfterStreakOfFive() {
        val newlyUnlocked = checkRewardsUseCase(
            existingRewards = emptyList(),
            totalPoints = 50,
            bestStreak = 5,
            totalSessionsCompleted = 1,
            additionSolved = 5,
            multiplicationSolved = 0
        )

        assertTrue(newlyUnlocked.any { it.id == RewardsConfig.ID_FIRE_STREAK })
    }

    @Test
    fun testCentury_unlockedAtHundredPoints() {
        val newlyUnlocked = checkRewardsUseCase(
            existingRewards = emptyList(),
            totalPoints = 100,
            bestStreak = 3,
            totalSessionsCompleted = 1,
            additionSolved = 10,
            multiplicationSolved = 0
        )

        assertTrue(newlyUnlocked.any { it.id == RewardsConfig.ID_CENTURY })
    }

    @Test
    fun testOperationsMaster_unlockedAtTenExercises() {
        val newlyUnlocked = checkRewardsUseCase(
            existingRewards = emptyList(),
            totalPoints = 100,
            bestStreak = 4,
            totalSessionsCompleted = 2,
            additionSolved = 10,
            multiplicationSolved = 10
        )

        assertTrue(newlyUnlocked.any { it.id == RewardsConfig.ID_ADDITION_MASTER })
        assertTrue(newlyUnlocked.any { it.id == RewardsConfig.ID_MULTIPLICATION_MASTER })
    }

    @Test
    fun testNoDuplicates_alreadyUnlockedRewardsNotUnlockedAgain() {
        val alreadyUnlocked = listOf(
            Reward(
                id = RewardsConfig.ID_FIRST_STAR,
                title = "Primera estrella",
                description = "",
                icon = "🌟",
                isUnlocked = true
            ),
            Reward(
                id = RewardsConfig.ID_FIRE_STREAK,
                title = "Racha de fuego",
                description = "",
                icon = "🔥",
                isUnlocked = true
            )
        )

        val newlyUnlocked = checkRewardsUseCase(
            existingRewards = alreadyUnlocked,
            totalPoints = 50,
            bestStreak = 5,
            totalSessionsCompleted = 2,
            additionSolved = 5,
            multiplicationSolved = 0
        )

        // No debe desbloquear FIRST_STAR ni FIRE_STREAK de nuevo
        assertTrue(newlyUnlocked.none { it.id == RewardsConfig.ID_FIRST_STAR })
        assertTrue(newlyUnlocked.none { it.id == RewardsConfig.ID_FIRE_STREAK })
    }
}

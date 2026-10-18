package com.educa.mathkids.presentation.screens.home

import com.educa.mathkids.domain.model.GameSession
import com.educa.mathkids.domain.model.OperationLevel
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.model.PlayerProgress
import com.educa.mathkids.domain.model.Reward
import com.educa.mathkids.domain.repository.PlayerProgressRepository
import com.educa.mathkids.domain.repository.SessionProgressResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testHomeViewModel_initializesWithPlayerProfile() = runTest {
        val fakeRepo = object : PlayerProgressRepository {
            override fun getProgress(operationType: OperationType): Flow<PlayerProgress> = flowOf()
            override fun getAllProgress(): Flow<List<PlayerProgress>> = flowOf(emptyList())
            override fun getPlayerProfile(): Flow<PlayerProgress> = flowOf(
                PlayerProgress(
                    totalPoints = 150,
                    currentStreak = 4,
                    bestStreak = 8,
                    level = 3
                )
            )
            override fun getAllRewards(): Flow<List<Reward>> = flowOf(emptyList())
            override fun getLevels(operationType: OperationType): Flow<List<OperationLevel>> = flowOf(emptyList())
            override suspend fun recordLevelResult(
                operationType: OperationType,
                levelNumber: Int,
                stars: Int,
                score: Int,
                exercisesCount: Int
            ): Boolean = false
            override suspend fun saveSession(session: GameSession): SessionProgressResult =
                SessionProgressResult(PlayerProgress(), emptyList())
            override suspend fun addPoints(points: Int) {}
            override suspend fun updateStreak(currentStreak: Int, bestStreak: Int) {}
            override suspend fun unlockReward(rewardId: String) {}
            override suspend fun resetAllProgress() {}
        }

        val viewModel = HomeViewModel(fakeRepo)

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(150, viewModel.uiState.value.totalPoints)
        assertEquals(4, viewModel.uiState.value.currentStreak)
        assertEquals(8, viewModel.uiState.value.bestStreak)
        assertEquals(3, viewModel.uiState.value.level)
    }
}

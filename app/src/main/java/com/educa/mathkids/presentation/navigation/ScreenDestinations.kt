package com.educa.mathkids.presentation.navigation

import com.educa.mathkids.domain.model.DidacticStrategy
import com.educa.mathkids.domain.model.Difficulty
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.model.SessionResult

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Numbers : Screen("numbers?tab={tab}") {
        fun createRoute(tab: String = "EXPLORE"): String = "numbers?tab=$tab"
    }
    object OperationSelect : Screen("operation_select")

    object OperationLevels : Screen("levels/{operationType}") {
        fun createRoute(operationType: OperationType): String =
            "levels/${operationType.name}"
    }

    object DifficultySelect : Screen("difficulty_select/{operationType}") {
        fun createRoute(operationType: OperationType): String =
            "difficulty_select/${operationType.name}"
    }

    object Exercise : Screen("exercise/{operationType}/{level}?strategy={strategy}") {
        fun createRoute(operationType: OperationType, level: Int, strategy: DidacticStrategy? = null): String =
            if (strategy != null) "exercise/${operationType.name}/$level?strategy=${strategy.name}"
            else "exercise/${operationType.name}/$level"

        fun createRoute(operationType: OperationType, difficulty: Difficulty): String =
            "exercise/${operationType.name}/${difficulty.levelNumber}"
    }

    object Practice : Screen("practice")
    object Challenge : Screen("challenge")
    object Achievements : Screen("achievements")

    object Result : Screen("result/{operationType}/{difficulty}/{totalExercises}/{correctCount}/{incorrectCount}/{accuracyPercentage}/{sessionPoints}/{totalPoints}/{maxStreak}/{currentLevel}/{unlockedRewardIds}/{levelNumber}/{nextLevelUnlocked}") {
        fun createRoute(result: SessionResult): String {
            val rewardIds = if (result.unlockedRewards.isEmpty()) "none" else result.unlockedRewards.joinToString(",") { it.id }
            return "result/${result.operationType.name}/${result.difficulty.name}/${result.totalExercises}/${result.correctCount}/${result.incorrectCount}/${result.accuracyPercentage}/${result.sessionPoints}/${result.totalPoints}/${result.maxStreak}/${result.currentLevel}/$rewardIds/${result.levelNumber}/${result.nextLevelUnlocked}"
        }
    }

    object Progress : Screen("progress")
    object Settings : Screen("settings")

    object ProblemarioHub : Screen("problemario_hub")

    object ProblemarioPlay : Screen("problemario_play/{category}?difficulty={difficulty}") {
        fun createRoute(
            category: com.educa.mathkids.domain.model.problemario.ProblemCategory,
            difficulty: com.educa.mathkids.domain.model.problemario.ProblemDifficulty
        ): String = "problemario_play/${category.name}?difficulty=${difficulty.name}"
    }

    object AdventurePlay : Screen("adventure_play/{operationType}/{levelNumber}") {
        fun createRoute(
            operationType: OperationType = OperationType.ADDITION,
            levelNumber: Int = 1
        ): String = "adventure_play/${operationType.name}/$levelNumber"
    }

    object Calculator : Screen("calculator")
    object Shop : Screen("shop")
    object MyToys : Screen("my_toys")

    object ToyGames : Screen("toy_games?initialGame={initialGame}&toyId={toyId}") {
        fun createRoute(initialGame: String = "HUB", toyId: String = ""): String =
            "toy_games?initialGame=$initialGame&toyId=$toyId"
    }
}


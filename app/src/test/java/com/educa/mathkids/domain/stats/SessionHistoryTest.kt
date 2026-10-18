package com.educa.mathkids.domain.stats

import com.educa.mathkids.domain.model.Difficulty
import com.educa.mathkids.domain.model.GameSessionRecord
import com.educa.mathkids.domain.model.OperationType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionHistoryTest {

    @Test
    fun testSessionHistory_sortingAndLimit() {
        val sessions = (1..8).map { i ->
            GameSessionRecord(
                sessionId = "session_$i",
                timestamp = i * 1000L,
                operationType = OperationType.ADDITION,
                difficulty = Difficulty.EASY,
                totalExercises = 10,
                correctCount = 8,
                incorrectCount = 2,
                accuracyPercentage = 80,
                pointsEarned = 80,
                maxStreak = 3
            )
        }

        // Ordenar por timestamp descendente y tomar las últimas 5
        val recentSessions = sessions.sortedByDescending { it.timestamp }.take(5)

        assertEquals(5, recentSessions.size)
        // La primera debe ser la más reciente (session_8 con timestamp 8000L)
        assertEquals("session_8", recentSessions.first().sessionId)
        assertEquals(8000L, recentSessions.first().timestamp)
        // La quinta debe ser session_4
        assertEquals("session_4", recentSessions.last().sessionId)
    }

    @Test
    fun testSessionHistory_noDuplicatesById() {
        val sessionMap = mutableMapOf<String, GameSessionRecord>()

        val session1 = GameSessionRecord(
            sessionId = "session_unique_1",
            timestamp = 1000L,
            operationType = OperationType.ADDITION,
            difficulty = Difficulty.EASY,
            totalExercises = 10,
            correctCount = 10,
            incorrectCount = 0,
            accuracyPercentage = 100,
            pointsEarned = 100,
            maxStreak = 10
        )

        // Insertar por primera vez
        sessionMap[session1.sessionId] = session1
        assertEquals(1, sessionMap.size)

        // Intentar insertar de nuevo con el mismo ID (simulando clave primaria Room)
        sessionMap[session1.sessionId] = session1
        assertEquals(1, sessionMap.size)
    }
}

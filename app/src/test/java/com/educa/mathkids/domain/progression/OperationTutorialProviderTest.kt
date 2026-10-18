package com.educa.mathkids.domain.progression

import com.educa.mathkids.domain.model.OperationTutorialProvider
import com.educa.mathkids.domain.model.OperationType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OperationTutorialProviderTest {

    @Test
    fun testAllOperationsAndLevelsProvideTutorials() {
        for (op in OperationType.values()) {
            for (level in 1..10) {
                val tutorial = OperationTutorialProvider.getTutorial(op, level)
                assertNotNull(tutorial)
                assertEquals(op, tutorial.operationType)
                assertEquals(level, tutorial.levelNumber)
                assertTrue(tutorial.levelTitle.isNotEmpty())
                assertTrue(tutorial.conceptTitle.isNotEmpty())
                assertTrue(tutorial.spokenTutorial.isNotEmpty())
                assertTrue(tutorial.exampleExplanation.isNotEmpty())
                assertTrue(tutorial.exampleEmoji.isNotEmpty())

                // Validar que la ecuación de ejemplo sea matemáticamente correcta
                val calculated = when (op) {
                    OperationType.ADDITION -> tutorial.exampleLeft + tutorial.exampleRight
                    OperationType.SUBTRACTION -> tutorial.exampleLeft - tutorial.exampleRight
                    OperationType.MULTIPLICATION -> tutorial.exampleLeft * tutorial.exampleRight
                    OperationType.DIVISION -> tutorial.exampleLeft / tutorial.exampleRight
                }
                assertEquals(tutorial.exampleResult, calculated)
            }
        }
    }
}

package com.educa.mathkids.domain.engine

import com.educa.mathkids.domain.model.OperationType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DistractorGeneratorTest {

    @Test
    fun testGenerateOptions_alwaysReturnsFourUniqueNonNegativeOptions() {
        val testCases = listOf(
            Triple(12, 7, 5),
            Triple(0, 5, 5),
            Triple(1, 1, 0),
            Triple(56, 7, 8),
            Triple(100, 50, 50)
        )

        testCases.forEach { (correct, op1, op2) ->
            val options = DistractorGenerator.generateOptions(
                correctAnswer = correct,
                operand1 = op1,
                operand2 = op2,
                operationType = OperationType.ADDITION
            )

            assertEquals("Debe generar exactamente 4 opciones", 4, options.size)
            assertEquals("Todas las opciones deben ser únicas", 4, options.toSet().size)
            assertTrue("Debe contener la respuesta correcta", options.contains(correct))
            assertTrue("Ninguna opción debe ser negativa", options.all { it >= 0 })
        }
    }
}

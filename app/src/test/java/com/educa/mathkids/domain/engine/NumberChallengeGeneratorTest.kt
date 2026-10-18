package com.educa.mathkids.domain.engine

import com.educa.mathkids.domain.model.AppSettings
import com.educa.mathkids.domain.model.NumberCatalog
import com.educa.mathkids.domain.model.NumberGameType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NumberChallengeGeneratorTest {

    @Test
    fun `test NumberCatalog contains 20 numbers with complete metadata`() {
        val catalog20 = NumberCatalog.NUMBERS_1_TO_20
        assertEquals(20, catalog20.size)

        catalog20.forEachIndexed { index, item ->
            assertEquals(index + 1, item.number)
            assertTrue(item.wordSpanish.isNotEmpty())
            assertTrue(item.emojiObject.isNotEmpty())
            assertTrue(item.funFact.isNotEmpty())
        }

        val catalog10 = NumberCatalog.NUMBERS_1_TO_10
        assertEquals(10, catalog10.size)
    }

    @Test
    fun `test IDENTIFY challenge generation creates valid question and options up to 20`() {
        val generatedAnswers = mutableSetOf<Int>()
        repeat(50) {
            val challenge = NumberChallengeGenerator.generate(NumberGameType.IDENTIFY)
            assertEquals(NumberGameType.IDENTIFY, challenge.type)
            assertTrue(challenge.correctAnswer in 1..20)
            assertTrue(challenge.options.contains(challenge.correctAnswer))
            assertEquals(4, challenge.options.size)
            assertEquals(4, challenge.options.distinct().size)
            assertTrue(challenge.question.contains(challenge.correctAnswer.toString()))
            generatedAnswers.add(challenge.correctAnswer)
        }
        // Verificar que genera números más allá del 10 (hasta el 20)
        assertTrue(generatedAnswers.any { it > 10 })
    }

    @Test
    fun `test COUNT challenge generation matches visual items count to correct answer`() {
        repeat(20) {
            val challenge = NumberChallengeGenerator.generate(NumberGameType.COUNT)
            assertEquals(NumberGameType.COUNT, challenge.type)
            assertEquals(challenge.correctAnswer, challenge.visualItems.size)
            assertTrue(challenge.options.contains(challenge.correctAnswer))
            assertEquals(4, challenge.options.size)
        }
    }

    @Test
    fun `test COMPARE challenge generation produces valid comparison`() {
        repeat(20) {
            val challenge = NumberChallengeGenerator.generate(NumberGameType.COMPARE)
            assertEquals(NumberGameType.COMPARE, challenge.type)
            assertEquals(2, challenge.options.size)
            assertTrue(challenge.options.contains(challenge.correctAnswer))
            if (challenge.question.contains("MAYOR")) {
                assertEquals(maxOf(challenge.options[0], challenge.options[1]), challenge.correctAnswer)
            } else {
                assertEquals(minOf(challenge.options[0], challenge.options[1]), challenge.correctAnswer)
            }
        }
    }

    @Test
    fun `test COMPLETE sequence challenge contains missing element matching answer`() {
        repeat(20) {
            val challenge = NumberChallengeGenerator.generate(NumberGameType.COMPLETE)
            assertEquals(NumberGameType.COMPLETE, challenge.type)
            assertEquals(5, challenge.displayedSequence.size)
            val nullCount = challenge.displayedSequence.count { it == null }
            assertEquals(1, nullCount)

            val missingIndex = challenge.displayedSequence.indexOf(null)
            val expectedValue = challenge.correctAnswer

            // Reconstruir la secuencia completa insertando la respuesta correcta
            val fullSeq = challenge.displayedSequence.toMutableList()
            fullSeq[missingIndex] = expectedValue

            // Verificar que es estrictamente consecutiva
            for (i in 0 until fullSeq.size - 1) {
                assertEquals(fullSeq[i]!! + 1, fullSeq[i + 1]!!)
            }

            assertTrue(challenge.options.contains(challenge.correctAnswer))
        }
    }

    @Test
    fun `test ORDER challenge supports both ascending and descending ordering`() {
        // Ascendente (Menor a Mayor)
        repeat(20) {
            val challenge = NumberChallengeGenerator.generate(NumberGameType.ORDER, orderDescending = false)
            assertEquals(NumberGameType.ORDER, challenge.type)
            assertFalse(challenge.isDescending)
            assertEquals(challenge.options.minOrNull(), challenge.correctAnswer)
            assertTrue(challenge.question.contains("MENOR"))
        }

        // Descendente (Mayor a Menor)
        repeat(20) {
            val challenge = NumberChallengeGenerator.generate(NumberGameType.ORDER, orderDescending = true)
            assertEquals(NumberGameType.ORDER, challenge.type)
            assertTrue(challenge.isDescending)
            assertEquals(challenge.options.maxOrNull(), challenge.correctAnswer)
            assertTrue(challenge.question.contains("MAYOR"))
        }

        // Generación aleatoria por defecto produce ambos tipos
        val sampleChallenges = (1..50).map { NumberChallengeGenerator.generate(NumberGameType.ORDER) }
        assertTrue(sampleChallenges.any { it.isDescending })
        assertTrue(sampleChallenges.any { !it.isDescending })
    }

    @Test
    fun `test AppSettings includes audio and voice enabled by default`() {
        val settings = AppSettings()
        assertTrue(settings.soundsGeneralEnabled)
        assertTrue(settings.voiceEnabled)
        assertTrue(settings.musicEnabled)
        assertTrue(settings.soundEffectsEnabled)
        assertTrue(settings.animationsEnabled)
        assertTrue(settings.hapticsEnabled)
    }
}

package com.educa.mathkids.core.game

import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.domain.model.adventure.AdventurePhase
import com.educa.mathkids.domain.model.adventure.Vector2D
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AdventureGameEngineTest {

    private lateinit var engine: AdventureGameEngine
    private val emittedSounds = mutableListOf<GameSoundEvent>()

    @Before
    fun setUp() {
        emittedSounds.clear()
        engine = AdventureGameEngine(
            onEmitSound = { emittedSounds.add(it) }
        )
        engine.setScreenDimensions(1080f, 1920f)
    }

    @Test
    fun `startLevel initializes level state and spawns 4 orbs with one correct target`() {
        engine.startLevel(level = 1, operation = OperationType.ADDITION)
        val state = engine.state.value

        assertEquals(1, state.currentLevel)
        assertEquals(3, state.lives)
        assertEquals(0, state.score)
        assertEquals(0, state.combo)
        assertEquals(AdventurePhase.PLAYING, state.phase)
        assertEquals(4, state.orbs.size)

        // Comprobar que exactamente uno de los 4 orbes es el correcto
        val correctOrbs = state.orbs.filter { it.isCorrect }
        assertEquals(1, correctOrbs.size)
        assertEquals(state.question.targetValue, correctOrbs.first().value)
    }

    @Test
    fun `onTick updates physics and orb positions smoothly`() {
        engine.startLevel(level = 1, operation = OperationType.ADDITION)
        val initialOrb = engine.state.value.orbs.first()
        val initialPos = initialOrb.position

        // Simular 5 cuadros a 60 FPS (16.6ms por cuadro)
        repeat(5) {
            engine.onTick(0.016f)
        }

        val updatedOrb = engine.state.value.orbs.first { it.id == initialOrb.id }
        assertNotEquals("La posición del orbe debe actualizarse con el delta time", initialPos, updatedOrb.position)
    }

    @Test
    fun `tapping correct orb increases score, builds combo and emits correct sound`() {
        engine.startLevel(level = 1, operation = OperationType.ADDITION)
        val state = engine.state.value
        val correctOrb = state.orbs.first { it.isCorrect }

        engine.onCanvasTapped(correctOrb.position.x, correctOrb.position.y)

        val updatedState = engine.state.value
        assertEquals("El score debe sumar 100 puntos base", 100, updatedState.score)
        assertEquals("El combo debe subir a 1", 1, updatedState.combo)
        assertEquals(1, updatedState.totalAnsweredCorrectly)
        assertTrue(emittedSounds.contains(GameSoundEvent.CORRECT_ANSWER))
        assertTrue(emittedSounds.contains(GameSoundEvent.ORB_POP))
    }

    @Test
    fun `tapping incorrect orb subtracts life, breaks combo and emits incorrect sound`() {
        engine.startLevel(level = 1, operation = OperationType.ADDITION)
        val state = engine.state.value
        val wrongOrb = state.orbs.first { !it.isCorrect }

        engine.onCanvasTapped(wrongOrb.position.x, wrongOrb.position.y)

        val updatedState = engine.state.value
        assertEquals("Las vidas deben disminuir a 2", 2, updatedState.lives)
        assertEquals("El combo debe reiniciarse a 0", 0, updatedState.combo)
        assertTrue(emittedSounds.contains(GameSoundEvent.INCORRECT_ANSWER))
    }

    @Test
    fun `losing all lives triggers GAME_OVER phase`() {
        engine.startLevel(level = 1, operation = OperationType.ADDITION)

        repeat(3) {
            val wrongOrb = engine.state.value.orbs.firstOrNull { !it.isCorrect }
            assertNotNull(wrongOrb)
            engine.onCanvasTapped(wrongOrb!!.position.x, wrongOrb.position.y)
        }

        val finalState = engine.state.value
        assertEquals(0, finalState.lives)
        assertEquals(AdventurePhase.GAME_OVER, finalState.phase)
        assertTrue(emittedSounds.contains(GameSoundEvent.GAME_OVER))
    }

    @Test
    fun `reaching target correct answers triggers ROUND_WON and level completion`() {
        engine.startLevel(level = 1, operation = OperationType.ADDITION)

        // Acertar 5 veces
        repeat(5) {
            val correctOrb = engine.state.value.orbs.first { it.isCorrect }
            engine.onCanvasTapped(correctOrb.position.x, correctOrb.position.y)
        }

        val state = engine.state.value
        assertEquals(AdventurePhase.ROUND_WON, state.phase)
        assertEquals(3, state.stars)
        assertTrue(emittedSounds.contains(GameSoundEvent.LEVEL_CLEARED))
    }

    @Test
    fun `Vector2D calculations are mathematically sound`() {
        val v1 = Vector2D(3f, 4f)
        val v2 = Vector2D(1f, 1f)

        assertEquals(5f, v1.length(), 0.001f)
        assertEquals(Vector2D(4f, 5f), v1 + v2)
        assertEquals(Vector2D(2f, 3f), v1 - v2)
        assertEquals(Vector2D(6f, 8f), v1 * 2f)

        val norm = v1.normalized()
        assertEquals(1f, norm.length(), 0.001f)
    }

    @Test
    fun `getBalloonCountForLevel returns 4 for levels 1-5, 6 for levels 6-10, and 8 for levels 11-15`() {
        for (lvl in 1..5) {
            assertEquals("Nivel $lvl debe tener 4 globos", 4, AdventureGameEngine.getBalloonCountForLevel(lvl))
        }
        for (lvl in 6..10) {
            assertEquals("Nivel $lvl debe tener 6 globos", 6, AdventureGameEngine.getBalloonCountForLevel(lvl))
        }
        for (lvl in 11..15) {
            assertEquals("Nivel $lvl debe tener 8 globos", 8, AdventureGameEngine.getBalloonCountForLevel(lvl))
        }
    }

    @Test
    fun `startLevel spawns exactly 4 balloons on level 1 and 5`() {
        engine.startLevel(level = 1, operation = OperationType.ADDITION)
        assertEquals(4, engine.state.value.orbs.size)
        assertEquals(1, engine.state.value.orbs.count { it.isCorrect })

        engine.startLevel(level = 5, operation = OperationType.SUBTRACTION)
        assertEquals(4, engine.state.value.orbs.size)
        assertEquals(1, engine.state.value.orbs.count { it.isCorrect })
    }

    @Test
    fun `startLevel spawns exactly 6 balloons from level 6 to 10`() {
        engine.startLevel(level = 6, operation = OperationType.ADDITION)
        assertEquals(6, engine.state.value.orbs.size)
        assertEquals(1, engine.state.value.orbs.count { it.isCorrect })

        engine.startLevel(level = 10, operation = OperationType.MULTIPLICATION)
        assertEquals(6, engine.state.value.orbs.size)
        assertEquals(1, engine.state.value.orbs.count { it.isCorrect })
    }

    @Test
    fun `startLevel spawns exactly 8 balloons from level 11 to 15`() {
        engine.startLevel(level = 11, operation = OperationType.DIVISION)
        assertEquals(8, engine.state.value.orbs.size)
        assertEquals(1, engine.state.value.orbs.count { it.isCorrect })

        engine.startLevel(level = 15, operation = OperationType.ADDITION)
        assertEquals(8, engine.state.value.orbs.size)
        assertEquals(1, engine.state.value.orbs.count { it.isCorrect })
    }
}

package com.educa.mathkids.data

import com.educa.mathkids.data.repository.ProblemarioRepositoryImpl
import com.educa.mathkids.domain.model.problemario.ProblemCategory
import com.educa.mathkids.domain.model.problemario.ProblemDifficulty
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProblemarioDifficultyAndTranslationTest {

    private lateinit var repository: ProblemarioRepositoryImpl

    @Before
    fun setUp() {
        repository = ProblemarioRepositoryImpl()
    }

    @Test
    fun `verificar que cada categoria filtra exactamente 10 problemas por dificultad`() = runBlocking {
        ProblemCategory.entries.forEach { category ->
            ProblemDifficulty.entries.forEach { difficulty ->
                val problems = repository.getProblems(category, difficulty)
                assertEquals(
                    "Categoría $category con dificultad $difficulty debe contener exactamente 10 problemas",
                    10,
                    problems.size
                )
                problems.forEach { problem ->
                    assertEquals(
                        "El problema ${problem.id} debe coincidir con la dificultad requerida",
                        difficulty,
                        problem.difficulty
                    )
                    assertEquals(
                        "El problema ${problem.id} debe pertenecer a la categoría $category",
                        category,
                        problem.category
                    )
                }
            }
        }
    }

    @Test
    fun `verificar que todos los 120 problemas tienen traduccion al ingles completa`() = runBlocking {
        ProblemCategory.entries.forEach { category ->
            ProblemDifficulty.entries.forEach { difficulty ->
                val problems = repository.getProblems(category, difficulty)
                problems.forEach { problem ->
                    val titleEn = problem.getTitle(isEnglish = true)
                    val storyEn = problem.getStory(isEnglish = true)
                    val questionEn = problem.getQuestion(isEnglish = true)
                    val explanationEn = problem.getPedagogicalExplanation(isEnglish = true)
                    val itemEn = problem.getItemName(isEnglish = true)

                    assertNotNull(titleEn)
                    assertFalse("El título en inglés no debe estar vacío (${problem.id})", titleEn.isBlank())

                    assertNotNull(storyEn)
                    assertFalse("La historia en inglés no debe estar vacía (${problem.id})", storyEn.isBlank())

                    assertNotNull(questionEn)
                    assertFalse("La pregunta en inglés no debe estar vacía (${problem.id})", questionEn.isBlank())

                    assertNotNull(explanationEn)
                    assertFalse("La explicación pedagógica en inglés no debe estar vacía (${problem.id})", explanationEn.isBlank())

                    assertNotNull(itemEn)
                    assertFalse("El nombre del elemento en inglés no debe estar vacío (${problem.id})", itemEn.isBlank())
                }
            }
        }
    }
}

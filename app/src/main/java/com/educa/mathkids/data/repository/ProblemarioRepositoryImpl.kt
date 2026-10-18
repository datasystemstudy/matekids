package com.educa.mathkids.data.repository

import com.educa.mathkids.data.repository.problem_bank.divisionProblems
import com.educa.mathkids.data.repository.problem_bank.multiplicacionProblems
import com.educa.mathkids.data.repository.problem_bank.restaProblems
import com.educa.mathkids.data.repository.problem_bank.sumaProblems
import com.educa.mathkids.domain.model.problemario.MathProblem
import com.educa.mathkids.domain.model.problemario.ProblemCategory
import com.educa.mathkids.domain.model.problemario.ProblemDifficulty
import com.educa.mathkids.domain.model.problemario.ProblemSolveMode
import com.educa.mathkids.domain.repository.ProblemarioRepository
import kotlin.random.Random

class ProblemarioRepositoryImpl : ProblemarioRepository {

    override fun getProblems(
        category: ProblemCategory,
        difficulty: ProblemDifficulty
    ): List<MathProblem> {
        val problems = when (category) {
            ProblemCategory.SUMA -> sumaProblems
            ProblemCategory.RESTA -> restaProblems
            ProblemCategory.MULTIPLICACION -> multiplicacionProblems
            ProblemCategory.DIVISION -> divisionProblems
        }
        val filtered = problems.filter { it.difficulty == difficulty }
        return if (filtered.isNotEmpty()) filtered else problems
    }

    override fun generateProblem(
        category: ProblemCategory,
        difficulty: ProblemDifficulty
    ): MathProblem {
        val id = "dyn_${category.name.lowercase()}_${System.currentTimeMillis()}_${Random.nextInt(1000)}"
        val mode = listOf(
            ProblemSolveMode.SELECCION,
            ProblemSolveMode.COMPLETACION,
            ProblemSolveMode.ESCRITA
        ).random()

        return when (category) {
            ProblemCategory.SUMA -> generateAdditionProblem(id, difficulty, mode)
            ProblemCategory.RESTA -> generateSubtractionProblem(id, difficulty, mode)
            ProblemCategory.MULTIPLICACION -> generateMultiplicationProblem(id, difficulty, mode)
            ProblemCategory.DIVISION -> generateDivisionProblem(id, difficulty, mode)
        }
    }

    private fun generateAdditionProblem(id: String, difficulty: ProblemDifficulty, mode: ProblemSolveMode): MathProblem {
        val (num1, num2) = when (difficulty) {
            ProblemDifficulty.FACIL -> Pair(Random.nextInt(1, 4), Random.nextInt(1, 3))
            ProblemDifficulty.MEDIO -> Pair(Random.nextInt(2, 6), Random.nextInt(2, 5))
            ProblemDifficulty.AVANZADO -> Pair(Random.nextInt(5, 10), Random.nextInt(3, 7))
        }
        val sum = num1 + num2
        data class ItemData(val emoji: String, val nameEs: String, val nameEn: String, val storyEs: String, val storyEn: String)
        val items = listOf(
            ItemData("🍎", "manzanas", "apples", "Ana tiene $num1 manzanas en su canasta y recoge $num2 más.", "Ana has $num1 apples in her basket and picks $num2 more."),
            ItemData("🎈", "globos", "balloons", "Tomás infla $num1 globos de fiesta y su amigo infla $num2 más.", "Tomas blows up $num1 party balloons and his friend blows up $num2 more."),
            ItemData("🐶", "perritos", "puppies", "Hay $num1 perritos jugando y llegan $num2 perritos más.", "There are $num1 puppies playing and $num2 more puppies arrive."),
            ItemData("⭐", "estrellas", "stars", "Mate descubrió $num1 estrellas y luego vio $num2 más en el cielo.", "Mate found $num1 stars and then saw $num2 more in the sky."),
            ItemData("🍪", "galletas", "cookies", "Mamá horneó $num1 galletas y luego preparó $num2 galletas más.", "Mom baked $num1 cookies and then prepared $num2 more cookies.")
        ).random()

        val options = createOptions(sum)

        return MathProblem(
            id = id,
            category = ProblemCategory.SUMA,
            difficulty = difficulty,
            title = "Aventura de Suma",
            titleEn = "Addition Adventure",
            story = items.storyEs,
            storyEn = items.storyEn,
            question = when (mode) {
                ProblemSolveMode.SELECCION -> "¿Cuántos ${items.nameEs} hay en total?"
                ProblemSolveMode.COMPLETACION -> "¿Qué número completa la suma de ${items.nameEs}?"
                ProblemSolveMode.ESCRITA -> "¿Cuántos ${items.nameEs} hay en total?"
            },
            questionEn = when (mode) {
                ProblemSolveMode.SELECCION -> "How many ${items.nameEn} are there in total?"
                ProblemSolveMode.COMPLETACION -> "What number completes the addition of ${items.nameEn}?"
                ProblemSolveMode.ESCRITA -> "How many ${items.nameEn} are there in total?"
            },
            num1 = num1,
            num2 = num2,
            correctAnswer = sum,
            options = options,
            itemEmoji = items.emoji,
            itemName = items.nameEs,
            itemNameEn = items.nameEn,
            equationText = "$num1 + $num2 = $sum",
            pedagogicalExplanation = "¡Juntamos $num1 y $num2 y tenemos $sum ${items.nameEs}!",
            pedagogicalExplanationEn = "We put $num1 and $num2 together and have $sum ${items.nameEn}!",
            solveMode = mode
        )
    }

    private fun generateSubtractionProblem(id: String, difficulty: ProblemDifficulty, mode: ProblemSolveMode): MathProblem {
        val (num1, num2) = when (difficulty) {
            ProblemDifficulty.FACIL -> {
                val a = Random.nextInt(2, 6)
                val b = Random.nextInt(1, a)
                Pair(a, b)
            }
            ProblemDifficulty.MEDIO -> {
                val a = Random.nextInt(5, 11)
                val b = Random.nextInt(1, a)
                Pair(a, b)
            }
            ProblemDifficulty.AVANZADO -> {
                val a = Random.nextInt(10, 16)
                val b = Random.nextInt(2, 8)
                Pair(a, b)
            }
        }
        val diff = num1 - num2
        data class ItemData(val emoji: String, val nameEs: String, val nameEn: String, val storyEs: String, val storyEn: String)
        val items = listOf(
            ItemData("🍎", "manzanas", "apples", "En el frutero había $num1 manzanas. Lucas se come $num2.", "There were $num1 apples in the fruit bowl. Lucas eats $num2."),
            ItemData("🍪", "galletas", "cookies", "Sofía tenía $num1 galletas ricas. Se come $num2 en la merienda.", "Sofia had $num1 tasty cookies. She eats $num2 at snack time."),
            ItemData("🎈", "globos", "balloons", "Hay $num1 globos flotando. El viento se lleva $num2 globos.", "There are $num1 floating balloons. The wind takes away $num2 balloons."),
            ItemData("🐰", "conejitos", "bunnies", "Hay $num1 conejitos en el prado. $num2 conejitos se van a su madriguera.", "There are $num1 bunnies in the meadow. $num2 bunnies go into their burrow."),
            ItemData("🐥", "pollitos", "chicks", "Hay $num1 pollitos paseando. $num2 pollitos van con mamá gallina.", "There are $num1 chicks walking. $num2 chicks run to mama hen.")
        ).random()

        val options = createOptions(diff)

        return MathProblem(
            id = id,
            category = ProblemCategory.RESTA,
            difficulty = difficulty,
            title = "Aventura de Resta",
            titleEn = "Subtraction Adventure",
            story = items.storyEs,
            storyEn = items.storyEn,
            question = when (mode) {
                ProblemSolveMode.SELECCION -> "¿Cuántos ${items.nameEs} quedan ahora?"
                ProblemSolveMode.COMPLETACION -> "¿Qué número completa la resta de ${items.nameEs}?"
                ProblemSolveMode.ESCRITA -> "¿Cuántos ${items.nameEs} quedan ahora?"
            },
            questionEn = when (mode) {
                ProblemSolveMode.SELECCION -> "How many ${items.nameEn} are left now?"
                ProblemSolveMode.COMPLETACION -> "What number completes the subtraction of ${items.nameEn}?"
                ProblemSolveMode.ESCRITA -> "How many ${items.nameEn} are left now?"
            },
            num1 = num1,
            num2 = num2,
            correctAnswer = diff,
            options = options,
            itemEmoji = items.emoji,
            itemName = items.nameEs,
            itemNameEn = items.nameEn,
            equationText = "$num1 − $num2 = $diff",
            pedagogicalExplanation = "¡Teníamos $num1, quitamos $num2 y quedan $diff ${items.nameEs}!",
            pedagogicalExplanationEn = "We had $num1, took away $num2, and $diff ${items.nameEn} are left!",
            solveMode = mode
        )
    }

    private fun generateMultiplicationProblem(id: String, difficulty: ProblemDifficulty, mode: ProblemSolveMode): MathProblem {
        val (groups, itemsPerGroup) = when (difficulty) {
            ProblemDifficulty.FACIL -> Pair(Random.nextInt(2, 4), 2)
            ProblemDifficulty.MEDIO -> Pair(Random.nextInt(2, 4), Random.nextInt(2, 4))
            ProblemDifficulty.AVANZADO -> Pair(Random.nextInt(3, 5), Random.nextInt(3, 5))
        }
        val product = groups * itemsPerGroup
        data class ContainerData(val contEs: String, val contEn: String, val emoji: String, val itemEs: String, val itemEn: String)
        val containers = listOf(
            ContainerData("platos", "plates", "🍪", "galletas", "cookies"),
            ContainerData("cajas", "boxes", "🧸", "juguetes", "toys"),
            ContainerData("árboles", "trees", "🍎", "manzanas", "apples"),
            ContainerData("bolsitas", "bags", "🍬", "caramelos", "candies"),
            ContainerData("filas", "rows", "⭐", "estrellas", "stars")
        ).random()

        val options = createOptions(product)

        return MathProblem(
            id = id,
            category = ProblemCategory.MULTIPLICACION,
            difficulty = difficulty,
            title = "Aventura de Grupos",
            titleEn = "Group Adventure",
            story = "Hay $groups ${containers.contEs}. Cada ${containers.contEs.dropLast(1)} tiene $itemsPerGroup ${containers.itemEs}.",
            storyEn = "There are $groups ${containers.contEn}. Each group has $itemsPerGroup ${containers.itemEn}.",
            question = when (mode) {
                ProblemSolveMode.SELECCION -> "¿Cuántos ${containers.itemEs} hay en total?"
                ProblemSolveMode.COMPLETACION -> "¿Qué número completa la multiplicación de ${containers.itemEs}?"
                ProblemSolveMode.ESCRITA -> "¿Cuántos ${containers.itemEs} hay en total?"
            },
            questionEn = when (mode) {
                ProblemSolveMode.SELECCION -> "How many ${containers.itemEn} are there in total?"
                ProblemSolveMode.COMPLETACION -> "What number completes the multiplication of ${containers.itemEn}?"
                ProblemSolveMode.ESCRITA -> "How many ${containers.itemEn} are there in total?"
            },
            num1 = groups,
            num2 = itemsPerGroup,
            correctAnswer = product,
            options = options,
            itemEmoji = containers.emoji,
            itemName = containers.itemEs,
            itemNameEn = containers.itemEn,
            groupCount = groups,
            itemsPerGroup = itemsPerGroup,
            groupName = containers.contEs,
            groupNameEn = containers.contEn,
            equationText = "$groups × $itemsPerGroup = $product",
            pedagogicalExplanation = "¡$groups grupos de $itemsPerGroup ${containers.itemEs} = $product en total!",
            pedagogicalExplanationEn = "$groups groups of $itemsPerGroup ${containers.itemEn} = $product in total!",
            solveMode = mode
        )
    }

    private fun generateDivisionProblem(id: String, difficulty: ProblemDifficulty, mode: ProblemSolveMode): MathProblem {
        val (recipients, eachItem) = when (difficulty) {
            ProblemDifficulty.FACIL -> Pair(2, Random.nextInt(1, 4))
            ProblemDifficulty.MEDIO -> Pair(Random.nextInt(2, 4), Random.nextInt(2, 4))
            ProblemDifficulty.AVANZADO -> Pair(Random.nextInt(2, 5), Random.nextInt(2, 5))
        }
        val total = recipients * eachItem
        data class DivData(val emoji: String, val itemEs: String, val itemEn: String, val recipEs: String, val recipEn: String)
        val items = listOf(
            DivData("🍎", "manzanas", "apples", "amigos", "friends"),
            DivData("🍬", "caramelos", "candies", "niños", "kids"),
            DivData("🍪", "galletas", "cookies", "compañeros", "classmates"),
            DivData("🧸", "juguetes", "toys", "pequeños", "little ones"),
            DivData("🍓", "fresas", "strawberries", "platos", "plates")
        ).random()

        val emojis = if (items.recipEs == "platos") {
            List(recipients) { "🍽️" }
        } else {
            val kidFaces = listOf("👧", "👦", "🧒", "👶")
            List(recipients) { kidFaces[it % kidFaces.size] }
        }

        val options = createOptions(eachItem)

        return MathProblem(
            id = id,
            category = ProblemCategory.DIVISION,
            difficulty = difficulty,
            title = "Aventura de Repartir",
            titleEn = "Sharing Adventure",
            story = "Hay $total ${items.itemEs}. Se reparten en partes iguales entre $recipients ${items.recipEs}.",
            storyEn = "There are $total ${items.itemEn}. They are shared equally among $recipients ${items.recipEn}.",
            question = when (mode) {
                ProblemSolveMode.SELECCION -> "¿Cuántos ${items.itemEs} recibe cada ${items.recipEs.dropLast(1)}?"
                ProblemSolveMode.COMPLETACION -> "¿Qué número completa la repartición de ${items.itemEs}?"
                ProblemSolveMode.ESCRITA -> "¿Cuántos ${items.itemEs} recibe cada ${items.recipEs.dropLast(1)}?"
            },
            questionEn = when (mode) {
                ProblemSolveMode.SELECCION -> "How many ${items.itemEn} does each ${items.recipEn.dropLast(1)} get?"
                ProblemSolveMode.COMPLETACION -> "What number completes the sharing of ${items.itemEn}?"
                ProblemSolveMode.ESCRITA -> "How many ${items.itemEn} does each ${items.recipEn.dropLast(1)} get?"
            },
            num1 = total,
            num2 = recipients,
            correctAnswer = eachItem,
            options = options,
            itemEmoji = items.emoji,
            itemName = items.itemEs,
            itemNameEn = items.itemEn,
            recipientCount = recipients,
            recipientEmojis = emojis,
            equationText = "$total ÷ $recipients = $eachItem",
            pedagogicalExplanation = "¡Repartimos $total ${items.itemEs} entre $recipients: $eachItem para cada uno!",
            pedagogicalExplanationEn = "We shared $total ${items.itemEn} among $recipients: $eachItem for each one!",
            solveMode = mode
        )
    }

    private fun createOptions(correct: Int): List<Int> {
        val options = mutableSetOf(correct)
        var delta = 1
        while (options.size < 3) {
            val candidate1 = correct + delta
            val candidate2 = correct - delta
            if (candidate2 > 0) options.add(candidate2)
            if (options.size < 3 && candidate1 > 0) options.add(candidate1)
            delta++
            if (delta > 20) break
        }
        while (options.size < 3) {
            options.add((options.maxOrNull() ?: 1) + 1)
        }
        return options.shuffled()
    }
}

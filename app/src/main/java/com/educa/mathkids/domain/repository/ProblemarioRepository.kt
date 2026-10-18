package com.educa.mathkids.domain.repository

import com.educa.mathkids.domain.model.problemario.MathProblem
import com.educa.mathkids.domain.model.problemario.ProblemCategory
import com.educa.mathkids.domain.model.problemario.ProblemDifficulty

interface ProblemarioRepository {
    fun getProblems(category: ProblemCategory, difficulty: ProblemDifficulty): List<MathProblem>
    fun generateProblem(category: ProblemCategory, difficulty: ProblemDifficulty): MathProblem
}

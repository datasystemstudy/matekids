package com.educa.mathkids.domain.usecase

import com.educa.mathkids.domain.model.GameSessionRecord
import com.educa.mathkids.domain.repository.ProgressRepository

class SaveGameSessionUseCase(
    private val repository: ProgressRepository
) {
    suspend operator fun invoke(session: GameSessionRecord) {
        repository.saveGameSession(session)
    }
}

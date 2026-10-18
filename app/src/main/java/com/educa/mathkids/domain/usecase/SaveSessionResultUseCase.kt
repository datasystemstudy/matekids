package com.educa.mathkids.domain.usecase

import com.educa.mathkids.domain.model.GameSession
import com.educa.mathkids.domain.repository.PlayerProgressRepository

class SaveSessionResultUseCase(
    private val repository: PlayerProgressRepository
) {
    suspend operator fun invoke(session: GameSession) {
        repository.saveSession(session)
    }
}

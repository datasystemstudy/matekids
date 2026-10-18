package com.educa.mathkids.domain.usecase

import com.educa.mathkids.domain.model.PlayerProgress
import com.educa.mathkids.domain.repository.PlayerProgressRepository
import kotlinx.coroutines.flow.Flow

class GetPlayerProgressUseCase(
    private val repository: PlayerProgressRepository
) {
    operator fun invoke(): Flow<List<PlayerProgress>> {
        return repository.getAllProgress()
    }
}

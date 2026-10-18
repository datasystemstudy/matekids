package com.educa.mathkids.domain.usecase

import com.educa.mathkids.domain.model.GameSessionRecord
import com.educa.mathkids.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow

class GetSessionHistoryUseCase(
    private val repository: ProgressRepository
) {
    operator fun invoke(limit: Int = 5): Flow<List<GameSessionRecord>> {
        return repository.getRecentSessions(limit)
    }
}

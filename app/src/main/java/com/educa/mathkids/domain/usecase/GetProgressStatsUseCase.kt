package com.educa.mathkids.domain.usecase

import com.educa.mathkids.domain.model.ProgressStats
import com.educa.mathkids.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow

class GetProgressStatsUseCase(
    private val repository: ProgressRepository
) {
    operator fun invoke(): Flow<ProgressStats> {
        return repository.getProgressStats()
    }
}

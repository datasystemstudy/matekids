package com.educa.mathkids.domain.usecase

import com.educa.mathkids.domain.model.OperationStats
import com.educa.mathkids.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow

class GetOperationStatsUseCase(
    private val repository: ProgressRepository
) {
    operator fun invoke(): Flow<List<OperationStats>> {
        return repository.getOperationStats()
    }
}

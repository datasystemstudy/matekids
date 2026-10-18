package com.educa.mathkids.domain.usecase

import com.educa.mathkids.domain.model.AppSettings
import com.educa.mathkids.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class GetSettingsUseCase(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<AppSettings> {
        return repository.getSettings()
    }
}

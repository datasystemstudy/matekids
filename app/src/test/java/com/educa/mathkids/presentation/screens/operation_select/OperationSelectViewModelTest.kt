package com.educa.mathkids.presentation.screens.operation_select

import com.educa.mathkids.domain.model.Difficulty
import org.junit.Assert.assertEquals
import org.junit.Test

class OperationSelectViewModelTest {

    @Test
    fun testSelectDifficulty_updatesStateCorrectly() {
        val viewModel = OperationSelectViewModel()

        assertEquals(Difficulty.EASY, viewModel.uiState.value.selectedDifficulty)

        viewModel.selectDifficulty(Difficulty.INTERMEDIATE)
        assertEquals(Difficulty.INTERMEDIATE, viewModel.uiState.value.selectedDifficulty)

        viewModel.selectDifficulty(Difficulty.ADVANCED)
        assertEquals(Difficulty.ADVANCED, viewModel.uiState.value.selectedDifficulty)
    }
}

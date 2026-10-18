package com.educa.mathkids.presentation.screens.difficulty_select

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.domain.model.Difficulty
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.presentation.components.ScreenHeader
import com.educa.mathkids.presentation.theme.BackgroundCream
import com.educa.mathkids.presentation.theme.Dimens
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.theme.TextDark
import com.educa.mathkids.presentation.theme.TextMuted
import com.educa.mathkids.presentation.theme.YellowStar

@Composable
fun DifficultySelectScreen(
    operationType: OperationType,
    onDifficultySelected: (Difficulty) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = com.educa.mathkids.presentation.localization.appStrings()
    val opColor = Color(operationType.colorHex)
    val opDisplayName = when (operationType) {
        OperationType.ADDITION -> strings.additionTitle
        OperationType.SUBTRACTION -> strings.subtractionTitle
        OperationType.MULTIPLICATION -> strings.multiplicationTitle
        OperationType.DIVISION -> strings.divisionTitle
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundCream)
            .padding(Dimens.SpacingLarge)
    ) {
        // Cabecera con botón volver
        ScreenHeader(
            title = strings.chooseLevelTitle,
            onBackClick = onBackClick
        )

        // Banner informativo de la operación elegida
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(opColor.copy(alpha = 0.12f))
                .padding(vertical = 12.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(opColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = operationType.symbol,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Text(
                    text = "${strings.practicingLabel} $opDisplayName",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = opColor,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Lista de dificultades
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(Difficulty.entries, key = { it.name }) { difficulty ->
                DifficultyCard(
                    difficulty = difficulty,
                    accentColor = opColor,
                    onClick = { onDifficultySelected(difficulty) }
                )
            }
        }
    }
}

@Composable
fun DifficultyCard(
    difficulty: Difficulty,
    accentColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "diff_card_scale"
    )

    val shape = RoundedCornerShape(24.dp)

    val strings = com.educa.mathkids.presentation.localization.appStrings()
    val isEnglish = strings is com.educa.mathkids.presentation.localization.EnglishStrings

    val diffTitle = when (difficulty) {
        Difficulty.EASY -> strings.difficultyFacil
        Difficulty.BASIC -> strings.difficultyMedio
        Difficulty.INTERMEDIATE -> if (isEnglish) "Intermediate" else "Intermedio"
        Difficulty.ADVANCED -> strings.difficultyAvanzado
    }

    val description = when (difficulty) {
        Difficulty.EASY -> if (isEnglish) "Small numbers to start with confidence" else "Números pequeños para empezar con confianza"
        Difficulty.BASIC -> if (isEnglish) "Fun calculations up to 25" else "Cálculos divertidos hasta 25"
        Difficulty.INTERMEDIATE -> if (isEnglish) "Quick challenges and multiplication" else "Desafíos rápidos y multiplicaciones"
        Difficulty.ADVANCED -> if (isEnglish) "Expert level for brilliant minds!" else "¡Nivel experto para mentes brillantes!"
    }

    Box(
        modifier = Modifier
            .scale(scale)
            .fillMaxWidth()
            .height(96.dp)
            .shadow(
                elevation = if (isPressed) 1.dp else 4.dp,
                shape = shape,
                spotColor = Color.Black.copy(alpha = 0.08f)
            )
            .clip(shape)
            .background(SurfaceWhite)
            .border(2.dp, accentColor.copy(alpha = 0.25f), shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = diffTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark
                )
                Text(
                    text = description,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Nivel de dificultad (rayos de energía didáctica)
            Row {
                repeat(difficulty.stars) {
                    Text(text = "⚡", fontSize = 18.sp)
                }
            }
        }
    }
}

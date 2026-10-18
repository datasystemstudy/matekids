package com.educa.mathkids.presentation.screens.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.domain.model.DidacticStrategy
import com.educa.mathkids.domain.model.OperationLevelConfig
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.presentation.components.AnimatedGameButton
import com.educa.mathkids.presentation.components.DynamicKidsBackground
import com.educa.mathkids.presentation.components.ScreenHeader
import com.educa.mathkids.presentation.theme.BluePrimary
import com.educa.mathkids.presentation.theme.BluePrimaryDark
import com.educa.mathkids.presentation.theme.Dimens
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.SlateBorder
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SlateMuted
import com.educa.mathkids.presentation.theme.SurfaceWhite

/**
 * Pantalla de configuración del Modo Práctica Libre (Fase 16).
 * Permite seleccionar: Operación, Nivel (1..10), Cantidad de ejercicios,
 * Estrategia Didáctica de aprendizaje y Temporizador.
 */
@Composable
fun PracticeConfigScreen(
    onStartPractice: (OperationType, Int, Int, Boolean, DidacticStrategy?) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onOperationClick: ((OperationType) -> Unit)? = null
) {
    var selectedOperation by remember { mutableStateOf(OperationType.ADDITION) }
    var selectedLevel by remember { mutableIntStateOf(1) }
    var selectedExerciseCount by remember { mutableIntStateOf(10) }
    var selectedStrategy by remember { mutableStateOf<DidacticStrategy?>(null) }
    var isTimerEnabled by remember { mutableStateOf(false) }

    val opColor = Color(selectedOperation.colorHex)

    val availableStrategies = remember(selectedOperation) {
        when (selectedOperation) {
            OperationType.ADDITION, OperationType.SUBTRACTION -> listOf(
                null,
                DidacticStrategy.NUMBER_LINE,
                DidacticStrategy.TEN_FRAME,
                DidacticStrategy.NUMBER_BONDS,
                DidacticStrategy.MISSING_NUMBER,
                DidacticStrategy.BALANCE_SCALE,
                DidacticStrategy.BASE_TEN_BLOCKS,
                DidacticStrategy.CONCRETE_OBJECTS,
                DidacticStrategy.WORD_PROBLEM
            )
            OperationType.MULTIPLICATION -> listOf(
                null,
                DidacticStrategy.ARRAY_GRID,
                DidacticStrategy.SKIP_COUNTING,
                DidacticStrategy.NUMBER_LINE,
                DidacticStrategy.MISSING_NUMBER,
                DidacticStrategy.BALANCE_SCALE,
                DidacticStrategy.CONCRETE_OBJECTS,
                DidacticStrategy.WORD_PROBLEM
            )
            OperationType.DIVISION -> listOf(
                null,
                DidacticStrategy.EQUAL_SHARING,
                DidacticStrategy.ARRAY_GRID,
                DidacticStrategy.NUMBER_LINE,
                DidacticStrategy.MISSING_NUMBER,
                DidacticStrategy.BALANCE_SCALE,
                DidacticStrategy.CONCRETE_OBJECTS,
                DidacticStrategy.WORD_PROBLEM
            )
        }
    }

    DynamicKidsBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.SpacingLarge)
                .verticalScroll(rememberScrollState())
        ) {
            ScreenHeader(
                title = "Modo Práctica Libre",
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Selector de Operación
            Text(
                text = "1. Elige la Operación",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = SlateDark
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OperationType.entries.forEach { op ->
                    val isSelected = op == selectedOperation
                    val color = Color(op.colorHex)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) color else SurfaceWhite)
                            .border(
                                width = if (isSelected) 2.5.dp else 1.5.dp,
                                color = if (isSelected) color else SlateBorder,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                selectedOperation = op
                                if (selectedStrategy !in availableStrategies) {
                                    selectedStrategy = null
                                }
                                onOperationClick?.invoke(op)
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = op.symbol,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = if (isSelected) Color.White else color
                            )
                            Text(
                                text = op.displayName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else SlateDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Selector de Nivel (1 al 10)
            Text(
                text = "2. Nivel de Dificultad (1 al 10)",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = SlateDark
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items((1..OperationLevelConfig.MAX_LEVELS_PER_OPERATION).toList(), key = { it }) { lvl ->
                    val isSelected = lvl == selectedLevel
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) opColor else SurfaceWhite)
                            .border(
                                width = if (isSelected) 2.5.dp else 1.5.dp,
                                color = if (isSelected) opColor else SlateBorder,
                                shape = CircleShape
                            )
                            .clickable { selectedLevel = lvl },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$lvl",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isSelected) Color.White else SlateDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Cantidad de Ejercicios
            Text(
                text = "3. Cantidad de Ejercicios",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = SlateDark
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf(5, 10, 15, 20).forEach { count ->
                    val isSelected = count == selectedExerciseCount
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) opColor else SurfaceWhite)
                            .border(
                                width = if (isSelected) 2.dp else 1.5.dp,
                                color = if (isSelected) opColor else SlateBorder,
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { selectedExerciseCount = count }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$count",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelected) Color.White else SlateDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Estrategia Didáctica de Aprendizaje
            Text(
                text = "4. Estrategia Didáctica de Comprensión",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = SlateDark
            )
            Text(
                text = "Elige una técnica didáctica o combina todas para un aprendizaje variado",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SlateMuted
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(availableStrategies, key = { it?.name ?: "all" }) { strategy ->
                    val isSelected = strategy == selectedStrategy
                    val label = strategy?.let { "${it.iconEmoji} ${it.title}" } ?: "🌈 Todas las Estrategias"

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) opColor else SurfaceWhite)
                            .border(
                                width = if (isSelected) 2.dp else 1.5.dp,
                                color = if (isSelected) opColor else SlateBorder,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedStrategy = strategy }
                            .padding(horizontal = 14.dp, vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            color = if (isSelected) Color.White else SlateDark
                        )
                    }
                }
            }

            // Explicación de la estrategia seleccionada
            selectedStrategy?.let { strategy ->
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(opColor.copy(alpha = 0.08f))
                        .border(1.dp, opColor.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = strategy.iconEmoji, fontSize = 18.sp)
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(
                                text = strategy.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SlateDark
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = strategy.description,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateDark
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "🎯 Objetivo: ${strategy.pedagogicalGoal}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = opColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. Temporizador
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceWhite)
                    .border(1.5.dp, SlateBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "⏱️ Temporizador de Respuesta",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateDark
                        )
                        Text(
                            text = if (isTimerEnabled) "15 segundos por ejercicio" else "Sin límite de tiempo",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateMuted
                        )
                    }

                    Switch(
                        checked = isTimerEnabled,
                        onCheckedChange = { isTimerEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = opColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Botón Comenzar Práctica
            AnimatedGameButton(
                text = "¡COMENZAR PRÁCTICA! 🚀",
                onClick = {
                    onStartPractice(
                        selectedOperation,
                        selectedLevel,
                        selectedExerciseCount,
                        isTimerEnabled,
                        selectedStrategy
                    )
                },
                primaryColor = opColor,
                darkColor = opColor,
                height = Dimens.ButtonHeightPrimary,
                fontSize = 20.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

package com.educa.mathkids.presentation.screens.progress

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.presentation.ads.NativeAdCard
import com.educa.mathkids.presentation.components.AnimatedGameButton
import com.educa.mathkids.presentation.components.KidCard
import com.educa.mathkids.presentation.components.Mascot
import com.educa.mathkids.presentation.components.MascotMood
import com.educa.mathkids.presentation.components.OperationProgressCard
import com.educa.mathkids.presentation.components.ScreenHeader
import com.educa.mathkids.presentation.components.SessionHistoryCard
import com.educa.mathkids.presentation.components.StatCard
import com.educa.mathkids.presentation.theme.AnimationConfig
import com.educa.mathkids.presentation.theme.BackgroundCream
import com.educa.mathkids.presentation.theme.Dimens
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.GreenPrimaryDark
import com.educa.mathkids.presentation.theme.LocalAnimationsEnabled
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.theme.TextDark
import com.educa.mathkids.presentation.theme.TextMuted

/**
 * Pantalla de Progreso y Estadísticas con animaciones suaves de entrada (Fase 9).
 * Muestra el progreso del niño de forma motivadora, estructurada por secciones,
 * con soporte para estados Loading, Empty, Success y Error.
 */
@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel,
    onBackClick: () -> Unit,
    onNavigateToPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundCream)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(Dimens.SpacingLarge)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Cabecera consistente con botón volver
            ScreenHeader(
                title = "Mi Progreso",
                onBackClick = onBackClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            when (val currentState = state) {
                is ProgressUiState.Loading -> {
                    LoadingStateView()
                }
                is ProgressUiState.Empty -> {
                    EmptyStateView(
                        points = currentState.totalPoints,
                        level = currentState.currentLevel,
                        onPlayClick = onNavigateToPlay
                    )
                }
                is ProgressUiState.Error -> {
                    ErrorStateView(
                        message = currentState.message,
                        onRetryClick = { /* Recarga automática por Flow */ }
                    )
                }
                is ProgressUiState.Success -> {
                    SuccessStateView(
                        state = currentState,
                        onFilterSelected = { viewModel.onFilterSelected(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingStateView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = GreenPrimary)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Cargando tu progreso...",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted
            )
        }
    }
}

@Composable
private fun EmptyStateView(
    points: Int,
    level: Int,
    onPlayClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            Mascot(
                size = 140.dp,
                mood = MascotMood.HAPPY
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "🌟 ¡Tu aventura matemática comienza aquí!",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = GreenPrimaryDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Completa tu primera sesión para comenzar a ver tus estadísticas, rachas y logros.",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedGameButton(
                text = "¡JUGAR AHORA! ▶",
                onClick = onPlayClick,
                primaryColor = GreenPrimary,
                darkColor = GreenPrimaryDark,
                height = Dimens.ButtonHeightPrimary,
                fontSize = 24.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ErrorStateView(
    message: String,
    onRetryClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "😕 No pudimos cargar tu progreso.",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Por favor intenta nuevamente en unos momentos.",
                fontSize = 14.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            AnimatedGameButton(
                text = "Intentar nuevamente 🔄",
                onClick = onRetryClick,
                primaryColor = GreenPrimary,
                darkColor = GreenPrimaryDark,
                height = Dimens.ButtonHeightPrimary,
                fontSize = 20.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SuccessStateView(
    state: ProgressUiState.Success,
    onFilterSelected: (OperationType?) -> Unit
) {
    val stats = state.stats
    val animationsEnabled = LocalAnimationsEnabled.current

    AnimatedVisibility(
        visible = true,
        enter = if (animationsEnabled) {
            fadeIn(AnimationConfig.normalTween()) + slideInVertically(
                initialOffsetY = { 30 },
                animationSpec = AnimationConfig.quickSpring()
            )
        } else {
            fadeIn(tween(0))
        }
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Resumen Superior (Puntos, Nivel, Mejor Racha)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        icon = "🎯",
                        value = "${stats.totalPoints}",
                        title = "Puntos",
                        accentColor = Color(0xFFF57F17),
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        icon = "🏆",
                        value = "Nv. ${stats.currentLevel}",
                        title = "Nivel",
                        accentColor = GreenPrimaryDark,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        icon = "🔥",
                        value = "${stats.bestStreak}",
                        title = "Mejor racha",
                        accentColor = Color(0xFFE65100),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // 2. Sección: 📚 Mi aprendizaje
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "📚 Mi aprendizaje",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    KidCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 24.dp
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Sesiones completadas:", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                Text(text = "${stats.totalSessions}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextDark)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Ejercicios realizados:", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                Text(text = "${stats.totalExercises}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextDark)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Respuestas correctas:", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = GreenPrimaryDark)
                                Text(text = "✅ ${stats.correctAnswers}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = GreenPrimaryDark)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Por mejorar:", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                                Text(text = "💪 ${stats.incorrectAnswers}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color(0xFFE65100))
                            }
                        }
                    }
                }
            }

            // 3. Sección: 🎯 Precisión General
            item {
                KidCard(
                    modifier = Modifier.fillMaxWidth(),
                    cornerRadius = 24.dp
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "🎯 Precisión General",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = TextDark
                            )
                            Text(
                                text = if (stats.overallAccuracy >= 70) "¡Excelente puntería matemática! 🌟" else "¡Sigue practicando cada día! 💪",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(if (stats.overallAccuracy >= 70) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${stats.overallAccuracy}%",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (stats.overallAccuracy >= 70) GreenPrimaryDark else Color(0xFFE65100)
                            )
                        }
                    }
                }
            }

            // 4. Sección: Rendimiento por Operación
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Rendimiento por Operación",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Chips de filtro horizontal
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                title = "Todas",
                                isSelected = state.selectedFilter == null,
                                onClick = { onFilterSelected(null) }
                            )
                        }
                        items(OperationType.entries, key = { it.name }) { op ->
                            FilterChip(
                                title = "${op.symbol} ${op.displayName}",
                                isSelected = state.selectedFilter == op,
                                onClick = { onFilterSelected(op) }
                            )
                        }
                    }
                }
            }

            // Lista de tarjetas de operaciones
            items(state.operationStats, key = { it.operationType.name }) { opStat ->
                OperationProgressCard(stats = opStat)
            }

            // 5. Sección: 📅 Actividad Reciente
            if (state.recentSessions.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "📅 Actividad Reciente",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark
                    )
                }

                items(state.recentSessions, key = { it.sessionId }) { sessionRecord ->
                    SessionHistoryCard(record = sessionRecord)
                }
            }

            // Anuncio Nativo integrado en estadísticas de progreso
            item {
                NativeAdCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp))
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun FilterChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) GreenPrimary else SurfaceWhite
    val textColor = if (isSelected) Color.White else TextDark

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

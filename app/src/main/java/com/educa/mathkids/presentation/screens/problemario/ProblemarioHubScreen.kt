package com.educa.mathkids.presentation.screens.problemario

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.domain.model.problemario.ProblemCategory
import com.educa.mathkids.domain.model.problemario.ProblemDifficulty
import com.educa.mathkids.presentation.components.DynamicKidsBackground
import com.educa.mathkids.presentation.components.KidsBottomNavDock
import com.educa.mathkids.presentation.components.KidsNavTab
import com.educa.mathkids.presentation.components.MascotMood
import com.educa.mathkids.presentation.components.MetaCompanion
import com.educa.mathkids.presentation.components.ScreenHeader
import com.educa.mathkids.presentation.theme.Dimens
import com.educa.mathkids.presentation.theme.FredokaFont
import com.educa.mathkids.presentation.theme.OrangeSecondary
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SlateMuted
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.utils.SoundManager
import com.educa.mathkids.presentation.utils.TtsManager

@Composable
fun ProblemarioHubScreen(
    onCategorySelected: (ProblemCategory, ProblemDifficulty) -> Unit,
    onBackClick: () -> Unit,
    ttsManager: TtsManager,
    soundManager: SoundManager?,
    modifier: Modifier = Modifier,
    onInicioClick: () -> Unit = {},
    onJuegosClick: () -> Unit = {},
    onPremiosClick: () -> Unit = {},
    onAjustesClick: () -> Unit = {}
) {
    val strings = com.educa.mathkids.presentation.localization.appStrings()
    var selectedDifficulty by remember { mutableStateOf(ProblemDifficulty.FACIL) }
    var mascotMood by remember { mutableStateOf(MascotMood.HAPPY) }
    val speechMessage = remember(selectedDifficulty, strings) {
        strings.problemarioWelcomeMsg
    }

    DynamicKidsBackground(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Dimens.SpacingLarge, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Cabecera Oficial con Botón de Inicio
                ScreenHeader(
                    title = strings.problemarioTitle,
                    onBackClick = onInicioClick,
                    isHomeButton = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Mascota Mate con bocadillo y TTS
                MetaCompanion(
                    message = speechMessage,
                    mood = mascotMood,
                    mascotSize = 110.dp,
                    ttsManager = ttsManager,
                    onMascotClick = {
                        mascotMood = MascotMood.CELEBRATING
                        soundManager?.playButtonClick()
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Selector de Dificultad (Fácil, Medio, Avanzado)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceWhite.copy(alpha = 0.9f))
                        .border(1.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProblemDifficulty.entries.forEach { diff ->
                        val isSelected = diff == selectedDifficulty
                        val bg = if (isSelected) OrangeSecondary else Color.Transparent
                        val textColor = if (isSelected) Color.White else SlateDark
                        val diffName = when (diff) {
                            ProblemDifficulty.FACIL -> strings.difficultyFacil
                            ProblemDifficulty.MEDIO -> strings.difficultyMedio
                            ProblemDifficulty.AVANZADO -> strings.difficultyAvanzado
                        }

                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(bg)
                                .clickable {
                                    soundManager?.playButtonClick()
                                    selectedDifficulty = diff
                                }
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = diff.badge, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = diffName,
                                fontSize = 13.sp,
                                fontFamily = FredokaFont,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                color = textColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Cuadrícula 2x2 con las 4 tarjetas grandes del Problemario
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(ProblemCategory.entries, key = { it.name }) { category ->
                        ProblemCategoryCard(
                            category = category,
                            onClick = {
                                soundManager?.playButtonClick()
                                onCategorySelected(category, selectedDifficulty)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProblemCategoryCard(
    category: ProblemCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = com.educa.mathkids.presentation.localization.appStrings()
    val categoryColor = Color(category.colorHex)

    Box(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(22.dp), spotColor = categoryColor.copy(alpha = 0.25f))
            .clip(RoundedCornerShape(22.dp))
            .background(SurfaceWhite)
            .border(2.5.dp, categoryColor.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Icono destacado con fondo circular
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(CircleShape)
                    .background(categoryColor.copy(alpha = 0.15f))
                    .border(2.dp, categoryColor.copy(alpha = 0.35f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = category.iconEmoji, fontSize = 34.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Título de la Operación con su símbolo
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = category.symbol,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = categoryColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = strings.getCategoryTitle(category),
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = SlateDark,
                    maxLines = 1,
                    softWrap = false
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Subtítulo pedagógico ("Aprende a juntar cantidades", etc.)
            Text(
                text = "“${strings.getCategorySubtitle(category)}”",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FredokaFont,
                color = SlateMuted,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Badge de 30 ejercicios y modalidades
            Text(
                text = strings.challengesBadge,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FredokaFont,
                color = categoryColor
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Botón visual "¡Jugar!"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(categoryColor)
                    .padding(vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = strings.btnPlayProblem,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = Color.White
                )
            }
        }
    }
}

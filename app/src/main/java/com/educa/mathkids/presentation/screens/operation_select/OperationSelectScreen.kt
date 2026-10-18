package com.educa.mathkids.presentation.screens.operation_select

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.educa.mathkids.domain.model.OperationType
import com.educa.mathkids.presentation.components.DynamicKidsBackground
import com.educa.mathkids.presentation.components.MascotMood
import com.educa.mathkids.presentation.components.MetaCompanion
import com.educa.mathkids.presentation.components.OperationCard
import com.educa.mathkids.presentation.theme.Dimens
import com.educa.mathkids.presentation.theme.FredokaFont
import com.educa.mathkids.presentation.theme.OrangeSecondary
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SlateMuted
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.utils.SoundManager
import com.educa.mathkids.presentation.utils.TtsManager

@Composable
fun OperationSelectScreen(
    onOperationSelected: (OperationType) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onInicioClick: () -> Unit = onBackClick,
    onJuegosClick: () -> Unit = {},
    onPremiosClick: () -> Unit = {},
    onAjustesClick: () -> Unit = {},
    onProblemarioClick: () -> Unit = {},
    ttsManager: TtsManager? = null,
    soundManager: SoundManager? = null
) {
    val strings = com.educa.mathkids.presentation.localization.appStrings()
    val isEnglish = com.educa.mathkids.presentation.localization.LocalAppLanguage.current == com.educa.mathkids.presentation.localization.AppLanguage.INGLES

    var mascotMood by remember { mutableStateOf(MascotMood.HAPPY) }
    val speechMessage = remember(isEnglish, strings) {
        strings.selectOptionToPracticePrompt
    }

    LaunchedEffect(speechMessage) {
        ttsManager?.speak(speechMessage)
    }

    DynamicKidsBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = Dimens.SpacingLarge, vertical = 10.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ============================================================
            // 1. CABECERA SUPERIOR: Botón Inicio (Izquierda) + Título Centrado
            // Reemplaza el botón atrás por el botón de Inicio
            // ============================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Botón de Inicio en la esquina superior izquierda
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(4.dp, CircleShape, spotColor = OrangeSecondary.copy(alpha = 0.3f))
                        .clip(CircleShape)
                        .background(SurfaceWhite)
                        .border(2.dp, OrangeSecondary.copy(alpha = 0.6f), CircleShape)
                        .clickable {
                            soundManager?.playButtonClick()
                            onInicioClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🏠", fontSize = 24.sp)
                }

                // Título en negritas
                Text(
                    text = strings.operationSelectTitle,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = SlateDark,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    textAlign = TextAlign.Center
                )

                // Espacio balanceador para centrar el título
                Spacer(modifier = Modifier.size(48.dp))
            }

            // ============================================================
            // 2. BOTONES DE OPERACIONES (SUMAR, RESTAR, MULTIPLICAR, DIVIDIR)
            // ============================================================
            // Fila 1: Sumar y Restar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OperationCard(
                    title = strings.additionTitle,
                    symbol = OperationType.ADDITION.symbol,
                    color = Color(OperationType.ADDITION.colorHex),
                    operationType = OperationType.ADDITION,
                    onClick = {
                        soundManager?.playButtonClick()
                        onOperationSelected(it)
                    },
                    modifier = Modifier.weight(1f)
                )

                OperationCard(
                    title = strings.subtractionTitle,
                    symbol = OperationType.SUBTRACTION.symbol,
                    color = Color(OperationType.SUBTRACTION.colorHex),
                    operationType = OperationType.SUBTRACTION,
                    onClick = {
                        soundManager?.playButtonClick()
                        onOperationSelected(it)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Fila 2: Multiplicar y Dividir
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                OperationCard(
                    title = strings.multiplicationTitle,
                    symbol = OperationType.MULTIPLICATION.symbol,
                    color = Color(OperationType.MULTIPLICATION.colorHex),
                    operationType = OperationType.MULTIPLICATION,
                    onClick = {
                        soundManager?.playButtonClick()
                        onOperationSelected(it)
                    },
                    modifier = Modifier.weight(1f)
                )

                OperationCard(
                    title = strings.divisionTitle,
                    symbol = OperationType.DIVISION.symbol,
                    color = Color(OperationType.DIVISION.colorHex),
                    operationType = OperationType.DIVISION,
                    onClick = {
                        soundManager?.playButtonClick()
                        onOperationSelected(it)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ============================================================
            // 3. BARRA DEL PROBLEMARIO (Justo debajo de Multiplicar y Dividir)
            // ============================================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFF673AB7).copy(alpha = 0.22f))
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceWhite)
                    .border(2.5.dp, Color(0xFFAB47BC).copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .clickable {
                        soundManager?.playButtonClick()
                        onProblemarioClick()
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📚", fontSize = 30.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = strings.btnProblemario,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FredokaFont,
                                color = SlateDark
                            )
                            Text(
                                text = if (isEnglish) "Illustrated stories to solve" else "Historias ilustradas para resolver",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                fontFamily = FredokaFont,
                                color = SlateMuted
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFAB47BC))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isEnglish) "ENTER ▶" else "ENTRAR ▶",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ============================================================
            // 4. ELEFANTE MATE CON EL MENSAJE PEDAGÓGICO
            // "seleccione la opción que desee practicar"
            // ============================================================
            MetaCompanion(
                message = speechMessage,
                mood = mascotMood,
                mascotSize = 100.dp,
                ttsManager = ttsManager,
                onMascotClick = {
                    mascotMood = MascotMood.CELEBRATING
                    soundManager?.playButtonClick()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

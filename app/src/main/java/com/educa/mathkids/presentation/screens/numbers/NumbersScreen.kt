package com.educa.mathkids.presentation.screens.numbers

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import com.educa.mathkids.R
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.domain.model.NumberGameType
import com.educa.mathkids.domain.model.NumberItem
import com.educa.mathkids.presentation.components.AnimatedGameButton
import com.educa.mathkids.presentation.components.AnswerButton
import com.educa.mathkids.presentation.components.AnswerButtonState
import com.educa.mathkids.presentation.components.ConfettiCelebration
import com.educa.mathkids.presentation.components.DynamicKidsBackground
import com.educa.mathkids.presentation.components.KidCard
import com.educa.mathkids.presentation.components.MetaCompanion
import com.educa.mathkids.presentation.components.ScoreDisplay
import com.educa.mathkids.presentation.components.ScreenHeader
import com.educa.mathkids.presentation.theme.BluePrimary
import com.educa.mathkids.presentation.theme.BluePrimaryDark
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.GreenPrimaryDark
import com.educa.mathkids.presentation.theme.OrangeSecondary
import com.educa.mathkids.presentation.theme.SlateBorder
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SlateMuted
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.utils.TtsManager

@Composable
fun NumbersScreen(
    viewModel: NumbersViewModel,
    ttsManager: TtsManager,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = com.educa.mathkids.presentation.localization.appStrings()
    val isEnglish = com.educa.mathkids.presentation.localization.LocalAppLanguage.current == com.educa.mathkids.presentation.localization.AppLanguage.INGLES
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(isEnglish) {
        ttsManager.setLanguage(if (isEnglish) "en" else "es")
        val welcomeSpeech = if (isEnglish) {
            "Exploring numbers! Touch any number to hear its name!"
        } else {
            "¡Explorando números! ¡Toca cualquier número para escuchar su nombre!"
        }
        ttsManager.speak(welcomeSpeech)
    }

    DynamicKidsBackground(modifier = modifier) {
        ConfettiCelebration(visible = uiState.isCorrect == true)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Cabecera con retroceso y contador de estrellas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ScreenHeader(
                    title = if (isEnglish) "Explore Numbers" else "Explorando Números",
                    onBackClick = onBackClick,
                    modifier = Modifier.weight(1f)
                )

                ScoreDisplay(
                    points = uiState.starsEarned,
                    showLabel = false,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 2. Personaje Guía Mate con bocadillo de diálogo interactivo
            MetaCompanion(
                message = uiState.metaDialog,
                mood = uiState.metaMood,
                mascotSize = 80.dp,
                ttsManager = ttsManager,
                onMascotClick = { viewModel.replayAudio() }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Barra de Selección de Actividades (Chips Horizontales)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NumberGameType.entries.forEach { tab ->
                    val isSelected = tab == uiState.activeTab
                    val chipBg = if (isSelected) BluePrimary else SurfaceWhite
                    val textColor = if (isSelected) Color.White else SlateDark
                    val borderColor = if (isSelected) BluePrimaryDark else SlateBorder

                    Box(
                        modifier = Modifier
                            .shadow(3.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(chipBg)
                            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
                            .clickable { viewModel.selectTab(tab) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = tab.iconEmoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tab.getTitle(isEnglish),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                    }
                }
            }

            // Sub-filtros para el modo ORDENAR (Todos, Menor a Mayor, Mayor a Menor)
            if (uiState.activeTab == NumberGameType.ORDER) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(
                        OrderFilterMode.ALL to (if (isEnglish) "🔀 All" else "🔀 Todos"),
                        OrderFilterMode.ASCENDING to (if (isEnglish) "⬆️ Least to Greatest" else "⬆️ Menor a Mayor"),
                        OrderFilterMode.DESCENDING to (if (isEnglish) "⬇️ Greatest to Least" else "⬇️ Mayor a Menor")
                    ).forEach { (mode, label) ->
                        val isSelected = uiState.orderFilter == mode
                        val btnBg = if (isSelected) OrangeSecondary else SurfaceWhite
                        val btnBorder = if (isSelected) OrangeSecondary else SlateBorder
                        val textColor = if (isSelected) Color.White else SlateDark

                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .shadow(if (isSelected) 3.dp else 1.dp, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .background(btnBg)
                                .border(1.5.dp, btnBorder, RoundedCornerShape(12.dp))
                                .clickable { viewModel.setOrderFilter(mode) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Contenido según Modo (EXPLORAR vs ACTIVIDAD DIDÁCTICA)
            AnimatedContent(
                targetState = uiState.activeTab,
                transitionSpec = {
                    fadeIn().togetherWith(fadeOut())
                },
                label = "numbers_tab_content"
            ) { activeTab ->
                if (activeTab == NumberGameType.EXPLORE) {
                    ExploreNumbersContent(
                        selectedNumber = uiState.selectedNumber,
                        catalog = uiState.numberCatalog,
                        onNumberSelected = { viewModel.selectNumber(it) },
                        ttsManager = ttsManager,
                        isEnglish = isEnglish
                    )
                } else {
                    ChallengeNumbersContent(
                        uiState = uiState,
                        onAnswerSelected = { viewModel.onAnswerSelected(it) },
                        onNextChallenge = { viewModel.onNextChallenge() },
                        ttsManager = ttsManager,
                        isEnglish = isEnglish
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * Modo Explorar: Tocar números 1..10 para ver objetos equivalentes y escuchar audio
 */
@Composable
private fun ExploreNumbersContent(
    selectedNumber: NumberItem,
    catalog: List<NumberItem>,
    onNumberSelected: (NumberItem) -> Unit,
    ttsManager: TtsManager,
    isEnglish: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tira horizontal de números 1..10
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(catalog, key = { it.number }) { item ->
                val isSelected = item.number == selectedNumber.number
                val itemColor = Color(item.colorHex)

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .shadow(if (isSelected) 6.dp else 2.dp, CircleShape)
                        .clip(CircleShape)
                        .background(if (isSelected) itemColor else SurfaceWhite)
                        .border(
                            width = if (isSelected) 3.dp else 1.5.dp,
                            color = if (isSelected) Color.White else itemColor.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                        .clickable { onNumberSelected(item) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item.number.toString(),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isSelected) Color.White else itemColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Tarjeta Principal del Número Seleccionado
        val cardColor = Color(selectedNumber.colorHex)
        KidCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Número gigante y nombre
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = selectedNumber.number.toString(),
                        fontSize = 68.sp,
                        fontWeight = FontWeight.Black,
                        color = cardColor
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = selectedNumber.getWord(isEnglish).uppercase(),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = SlateDark
                        )

                        Text(
                            text = selectedNumber.getFunFact(isEnglish),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateMuted
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Botón para escuchar
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(cardColor.copy(alpha = 0.15f))
                            .border(1.5.dp, cardColor, CircleShape)
                            .clickable { ttsManager.speakNumber(selectedNumber.number) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔊", fontSize = 20.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Objetos equivalentes (Conteo visual 1 a 1)
                Text(
                    text = if (isEnglish) "Count the objects! (${selectedNumber.number}):" else "¡Cuenta los objetos! (${selectedNumber.number}):",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SlateDark
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Filas de emojis interactivos (agrupados de 5 en 5 para facilitar el conteo visual del 1 al 20)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val allEmojis = List(selectedNumber.number) { selectedNumber.emojiObject }
                    val rows = allEmojis.chunked(5)
                    rows.forEachIndexed { rowIndex, rowItems ->
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            rowItems.forEachIndexed { itemIndex, emoji ->
                                val overallIndex = rowIndex * 5 + itemIndex
                                var isPressed by remember(selectedNumber.number, overallIndex) { mutableStateOf(false) }
                                val itemScale by animateFloatAsState(
                                    targetValue = if (isPressed) 1.35f else 1.0f,
                                    animationSpec = spring(),
                                    label = "emoji_scale"
                                )

                                Text(
                                    text = emoji,
                                    fontSize = if (selectedNumber.number > 10) 28.sp else 32.sp,
                                    modifier = Modifier
                                        .padding(horizontal = 4.dp)
                                        .scale(itemScale)
                                        .clickable {
                                            isPressed = !isPressed
                                            ttsManager.speak((overallIndex + 1).toString())
                                        }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isEnglish) "💡 Tap each object to count out loud" else "💡 Toca cada objeto para contar en voz alta",
                    fontSize = 12.sp,
                    color = SlateMuted,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Modo Actividad / Reto Didáctico de Números
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChallengeNumbersContent(
    uiState: NumbersUiState,
    onAnswerSelected: (Int) -> Unit,
    onNextChallenge: () -> Unit,
    ttsManager: TtsManager,
    isEnglish: Boolean
) {
    val challenge = uiState.currentChallenge ?: return

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tarjeta de la Pregunta
        KidCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 22.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Título de la actividad
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = challenge.type.iconEmoji, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = challenge.question,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = BluePrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(OrangeSecondary.copy(alpha = 0.15f))
                            .clickable { ttsManager.speak(challenge.spokenQuestion) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🔊", fontSize = 16.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Modo Específico: MAYOR Y MENOR QUE (Estilo Dribbble con Elefante y Píldoras de Comparación)
                if (challenge.type == NumberGameType.COMPARE && challenge.options.size >= 2) {
                    val optA = challenge.options[0]
                    val optB = challenge.options[1]

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Mascota elefante asomándose alegremente
                        Image(
                            painter = painterResource(id = R.drawable.ic_mascot_elephant_3d),
                            contentDescription = "Mascota Elefante",
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(18.dp))
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Ecuación de comparación visual: [ Píldora A ] [ ? ] [ Píldora B ]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Píldora Izquierda (Rosa)
                            Box(
                                modifier = Modifier
                                    .size(width = 90.dp, height = 98.dp)
                                    .shadow(6.dp, RoundedCornerShape(22.dp), spotColor = Color(0xFFE91E63).copy(alpha = 0.35f))
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(Color(0xFFFCE4EC))
                                    .border(
                                        width = if (uiState.selectedAnswer == optA) 3.5.dp else 2.dp,
                                        color = if (uiState.selectedAnswer == optA) Color(0xFFE91E63) else Color(0xFFF48FB1),
                                        shape = RoundedCornerShape(22.dp)
                                    )
                                    .clickable(enabled = !uiState.isAnswerSubmitted) {
                                        onAnswerSelected(optA)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = optA.toString(),
                                        fontSize = 38.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = com.educa.mathkids.presentation.theme.FredokaFont,
                                        color = Color(0xFFC2185B)
                                    )
                                    if (challenge.visualItems.isNotEmpty()) {
                                        Text(text = challenge.visualItems[0], fontSize = 18.sp)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            // Casilla de operador central: [ ? ] o símbolo
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .shadow(4.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFF9C4))
                                    .border(2.5.dp, Color(0xFFFFB300), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                val symbol = when {
                                    uiState.isAnswerSubmitted && optA > optB -> ">"
                                    uiState.isAnswerSubmitted && optA < optB -> "<"
                                    else -> "?"
                                }
                                Text(
                                    text = symbol,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = com.educa.mathkids.presentation.theme.FredokaFont,
                                    color = Color(0xFFF57F17)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            // Píldora Derecha (Azul Celeste)
                            Box(
                                modifier = Modifier
                                    .size(width = 90.dp, height = 98.dp)
                                    .shadow(6.dp, RoundedCornerShape(22.dp), spotColor = Color(0xFF0288D1).copy(alpha = 0.35f))
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(Color(0xFFE1F5FE))
                                    .border(
                                        width = if (uiState.selectedAnswer == optB) 3.5.dp else 2.dp,
                                        color = if (uiState.selectedAnswer == optB) Color(0xFF0288D1) else Color(0xFF81D4FA),
                                        shape = RoundedCornerShape(22.dp)
                                    )
                                    .clickable(enabled = !uiState.isAnswerSubmitted) {
                                        onAnswerSelected(optB)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = optB.toString(),
                                        fontSize = 38.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = com.educa.mathkids.presentation.theme.FredokaFont,
                                        color = Color(0xFF0277BD)
                                    )
                                    if (challenge.visualItems.size > 1) {
                                        Text(text = challenge.visualItems[1], fontSize = 18.sp)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                } else if (challenge.type == NumberGameType.ORDER) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val isDesc = challenge.isDescending
                        val badgeBg = if (isDesc) Color(0xFFF3E5F5) else Color(0xFFE8F5E9)
                        val badgeBorder = if (isDesc) Color(0xFFAB47BC) else Color(0xFF4CAF50)
                        val badgeText = if (isDesc) {
                            if (isEnglish) "⬇️ From GREATEST to SMALLEST" else "⬇️ De MAYOR a MENOR"
                        } else {
                            if (isEnglish) "⬆️ From SMALLEST to GREATEST" else "⬆️ De MENOR a MAYOR"
                        }

                        // Badge distintivo de la dirección
                        Box(
                            modifier = Modifier
                                .shadow(2.dp, RoundedCornerShape(14.dp))
                                .clip(RoundedCornerShape(14.dp))
                                .background(badgeBg)
                                .border(1.5.dp, badgeBorder, RoundedCornerShape(14.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = badgeText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isDesc) Color(0xFF7B1FA2) else Color(0xFF2E7D32)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Fila de tarjetas para los números a ordenar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            challenge.options.forEachIndexed { idx, num ->
                                if (idx > 0) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isDesc) "◀" else "▶",
                                        fontSize = 15.sp,
                                        color = SlateDark.copy(alpha = 0.45f),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }

                                val isTarget = num == challenge.correctAnswer
                                val isSelected = num == uiState.selectedAnswer
                                val boxBg = when {
                                    uiState.isAnswerSubmitted && isTarget -> Color(0xFFC8E6C9)
                                    uiState.isAnswerSubmitted && isSelected && !isTarget -> Color(0xFFFFCDD2)
                                    isSelected -> Color(0xFFE0F2FE)
                                    else -> Color(0xFFF8FAFC)
                                }
                                val boxBorder = when {
                                    uiState.isAnswerSubmitted && isTarget -> Color(0xFF4CAF50)
                                    uiState.isAnswerSubmitted && isSelected && !isTarget -> Color(0xFFEF5350)
                                    isSelected -> BluePrimary
                                    else -> Color(0xFFCBD5E1)
                                }

                                Box(
                                    modifier = Modifier
                                        .size(width = 68.dp, height = 76.dp)
                                        .shadow(4.dp, RoundedCornerShape(18.dp), spotColor = boxBorder.copy(alpha = 0.3f))
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(boxBg)
                                        .border(2.dp, boxBorder, RoundedCornerShape(18.dp))
                                        .clickable(enabled = !uiState.isAnswerSubmitted) {
                                            onAnswerSelected(num)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = num.toString(),
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = com.educa.mathkids.presentation.theme.FredokaFont,
                                        color = SlateDark
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                } else if (challenge.visualItems.isNotEmpty()) {
                    FlowRow(
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.Center,
                        maxItemsInEachRow = 5,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp)
                    ) {
                        challenge.visualItems.forEachIndexed { index, emoji ->
                            var isPressed by remember(challenge.id, index) { mutableStateOf(false) }
                            val itemScale by animateFloatAsState(
                                targetValue = if (isPressed) 1.35f else 1.0f,
                                animationSpec = spring(),
                                label = "challenge_emoji_scale"
                            )

                            Text(
                                text = emoji,
                                fontSize = if (challenge.visualItems.size > 10) 28.sp else 34.sp,
                                modifier = Modifier
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                    .scale(itemScale)
                                    .clickable {
                                        isPressed = !isPressed
                                        ttsManager.speak((index + 1).toString())
                                    }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Representación de secuencia con casilla vacía (si aplica)
                if (challenge.displayedSequence.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        challenge.displayedSequence.forEach { item ->
                            if (item != null) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFE2E8F0)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item.toString(),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Black,
                                        color = SlateDark
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(OrangeSecondary.copy(alpha = 0.2f))
                                        .border(2.dp, OrangeSecondary, RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "¿ ?",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = OrangeSecondary
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        // Opciones de Respuesta en botones táctiles grandes (Solo para modos que no tienen sus propias tarjetas interactivas como COMPARE u ORDER)
        if (challenge.type != NumberGameType.COMPARE && challenge.type != NumberGameType.ORDER) {
            val optionRows = challenge.options.chunked(2)
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                optionRows.forEach { rowOptions ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        rowOptions.forEach { option ->
                            val buttonState = when {
                                !uiState.isAnswerSubmitted -> AnswerButtonState.DEFAULT
                                option == uiState.selectedAnswer && uiState.isCorrect == true -> AnswerButtonState.SELECTED_CORRECT
                                option == uiState.selectedAnswer && uiState.isCorrect == false -> AnswerButtonState.SELECTED_INCORRECT
                                option == challenge.correctAnswer && uiState.isCorrect == false -> AnswerButtonState.HIGHLIGHT_CORRECT
                                else -> AnswerButtonState.DISABLED
                            }

                            AnswerButton(
                                text = option.toString(),
                                onClick = { onAnswerSelected(option) },
                                state = buttonState,
                                enabled = !uiState.isAnswerSubmitted,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowOptions.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Feedback y Botón Siguiente
        AnimatedVisibility(
            visible = uiState.isAnswerSubmitted,
            enter = fadeIn() + scaleIn(initialScale = 0.9f)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            if (uiState.isCorrect == true) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                        )
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.feedbackMessage,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.isCorrect == true) GreenPrimaryDark else Color(0xFFC62828),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                AnimatedGameButton(
                    text = if (isEnglish) "Next Challenge! ➜" else "¡Siguiente Reto! ➜",
                    onClick = onNextChallenge,
                    primaryColor = GreenPrimary,
                    darkColor = GreenPrimaryDark,
                    height = 52.dp,
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

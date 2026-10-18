package com.educa.mathkids.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.shadow
import com.educa.mathkids.presentation.utils.HapticUtil
import com.educa.mathkids.presentation.utils.SoundManager
import kotlinx.coroutines.delay
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import com.educa.mathkids.domain.model.BondElement
import com.educa.mathkids.domain.model.VisualRepresentation
import com.educa.mathkids.domain.model.VisualType
import com.educa.mathkids.presentation.theme.BluePrimary
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.OrangeSecondary
import com.educa.mathkids.presentation.theme.SlateBorder
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SlateLight
import com.educa.mathkids.presentation.theme.SlateMuted
import com.educa.mathkids.presentation.theme.SurfaceWhite

/**
 * Componente interactivo y dinámico que renderiza la representación visual
 * de la operación matemática según el nivel y tipo pedagógico.
 */
@Composable
fun VisualRepresentationView(
    visual: VisualRepresentation,
    accentColor: Color,
    modifier: Modifier = Modifier,
    withContainer: Boolean = true,
    keypadInput: String = "",
    soundManager: SoundManager? = null
) {
    if (withContainer) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceWhite)
                .border(1.5.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(18.dp))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            VisualContent(visual, accentColor, keypadInput, soundManager)
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            VisualContent(visual, accentColor, keypadInput, soundManager)
        }
    }
}

@Composable
private fun VisualContent(
    visual: VisualRepresentation,
    accentColor: Color,
    keypadInput: String = "",
    soundManager: SoundManager? = null
) {
    when (visual.visualType) {
        VisualType.OBJECT_COUNT -> ObjectCountView(visual, accentColor, soundManager)
        VisualType.OBJECT_TAKEOUT -> ObjectTakeoutView(visual, accentColor, soundManager)
        VisualType.GROUPS_GRID -> GroupsGridView(visual, accentColor)
        VisualType.SHARING_PARTITION -> SharingPartitionView(visual, accentColor)
        VisualType.BASE_TEN_BLOCKS -> BaseTenBlocksView(visual, accentColor)
        VisualType.WORD_PROBLEM -> WordProblemView(visual, accentColor)
        VisualType.NUMBER_FOCUS -> NumberFocusVisualView(visual, accentColor)
        VisualType.NUMBER_LINE -> NumberLineView(visual, accentColor)
        VisualType.TEN_FRAME -> TenFrameView(visual, accentColor)
        VisualType.NUMBER_BONDS -> NumberBondsView(visual, accentColor)
        VisualType.ARRAY_GRID -> ArrayGridView(visual, accentColor)
        VisualType.SKIP_COUNTING -> SkipCountingView(visual, accentColor, keypadInput)
        VisualType.BALANCE_SCALE -> BalanceScaleView(visual, accentColor, keypadInput)
    }
}

// -------------------------------------------------------------
// Componente de Elemento Interactivo con Conteo Táctil para Niños
// -------------------------------------------------------------
@Composable
private fun InteractiveCountingItem(
    emoji: String,
    itemNumber: Int,
    isCounted: Boolean,
    onTap: () -> Unit,
    groupColor: Color,
    modifier: Modifier = Modifier
) {
    var bounceTrigger by remember { mutableIntStateOf(0) }
    val scale by animateFloatAsState(
        targetValue = if (bounceTrigger > 0) 1.28f else 1.0f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = 600f),
        label = "item_bounce"
    )

    LaunchedEffect(bounceTrigger) {
        if (bounceTrigger > 0) {
            delay(160)
            bounceTrigger = 0
        }
    }

    Box(
        modifier = modifier
            .padding(3.dp)
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                bounceTrigger++
                onTap()
            },
        contentAlignment = Alignment.Center
    ) {
        // Contenedor squircle con fondo suave y borde dinámico que da presencia de juguete
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (isCounted) groupColor.copy(alpha = 0.18f)
                    else Color.White.copy(alpha = 0.85f)
                )
                .border(
                    width = if (isCounted) 2.dp else 1.dp,
                    color = if (isCounted) groupColor else groupColor.copy(alpha = 0.22f),
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = 28.sp,
                textAlign = TextAlign.Center
            )
        }

        // Burbuja indicadora con el número de conteo (1, 2, 3...) cuando el niño pulsa
        if (isCounted) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(groupColor)
                    .border(1.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$itemNumber",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }
    }
}

// -------------------------------------------------------------
// Isla de Grupo con Conteo Interactivo
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CountingGroupIsland(
    count: Int,
    emoji: String,
    groupColor: Color,
    modifier: Modifier = Modifier,
    soundManager: SoundManager? = null
) {
    val context = LocalContext.current
    val hapticUtil = remember { HapticUtil(context) }
    var countedIndices by remember(count, emoji) { mutableStateOf(setOf<Int>()) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(groupColor.copy(alpha = 0.08f))
                .border(1.5.dp, groupColor.copy(alpha = 0.28f), RoundedCornerShape(18.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.Center,
                maxItemsInEachRow = if (count > 4) 3 else 3,
                modifier = Modifier.padding(2.dp)
            ) {
                repeat(count.coerceAtMost(10)) { index ->
                    val isCounted = index in countedIndices
                    InteractiveCountingItem(
                        emoji = emoji,
                        itemNumber = index + 1,
                        isCounted = isCounted,
                        onTap = {
                            countedIndices = if (isCounted) {
                                countedIndices - index
                            } else {
                                countedIndices + index
                            }
                            hapticUtil.vibrateClick()
                            soundManager?.playButtonClick()
                        },
                        groupColor = groupColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Badge con el número de elementos del grupo
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceWhite)
                .border(1.2.dp, groupColor.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 2.dp)
        ) {
            Text(
                text = "$count",
                fontSize = 19.sp,
                fontWeight = FontWeight.ExtraBold,
                color = groupColor
            )
        }
    }
}

// -------------------------------------------------------------
// 1. OBJECT_COUNT: Suma / Conteo visual estructurado e interactivo
// -------------------------------------------------------------
@Composable
private fun ObjectCountView(
    visual: VisualRepresentation,
    accentColor: Color,
    soundManager: SoundManager? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Grupo A
        CountingGroupIsland(
            count = visual.countA,
            emoji = visual.primaryEmoji,
            groupColor = accentColor,
            soundManager = soundManager
        )

        // Símbolo Operador "+" con diseño 3D alegre
        Box(
            modifier = Modifier
                .size(38.dp)
                .shadow(3.dp, RoundedCornerShape(12.dp), spotColor = accentColor.copy(alpha = 0.35f))
                .clip(RoundedCornerShape(12.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(accentColor, accentColor.copy(alpha = 0.85f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }

        // Grupo B
        CountingGroupIsland(
            count = visual.countB,
            emoji = visual.secondaryEmoji,
            groupColor = OrangeSecondary,
            soundManager = soundManager
        )
    }
}

// -------------------------------------------------------------
// 2. OBJECT_TAKEOUT: Resta visual (tachar / quitar)
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ObjectTakeoutView(
    visual: VisualRepresentation,
    accentColor: Color,
    soundManager: SoundManager? = null
) {
    val context = LocalContext.current
    val hapticUtil = remember { HapticUtil(context) }
    val remaining = (visual.totalCount - visual.removeCount).coerceAtLeast(0)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Píldora de instrucción clara y lúdica
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(accentColor.copy(alpha = 0.10f))
                .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
            Text(
                text = "De ${visual.totalCount} quitamos ${visual.removeCount} ✂️",
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(accentColor.copy(alpha = 0.08f))
                .border(1.5.dp, accentColor.copy(alpha = 0.28f), RoundedCornerShape(18.dp))
                .padding(horizontal = 10.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.Center,
                maxItemsInEachRow = 5,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Elementos que quedan
                repeat(remaining.coerceAtMost(12)) {
                    InteractiveEmojiItem(
                        emoji = visual.primaryEmoji,
                        fontSize = 32.sp,
                        onTap = {
                            hapticUtil.vibrateClick()
                            soundManager?.playButtonClick()
                        }
                    )
                }
                // Elementos que se quitan (con animación y cruz roja tachada)
                repeat(visual.removeCount.coerceAtMost(10)) {
                    Box(
                        modifier = Modifier.padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        InteractiveEmojiItem(
                            emoji = visual.primaryEmoji,
                            fontSize = 32.sp,
                            modifier = Modifier.alpha(0.35f),
                            onTap = {
                                hapticUtil.vibrateClick()
                                soundManager?.playButtonClick()
                            }
                        )
                        Text(
                            text = "❌",
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. GROUPS_GRID: Multiplicación (grupos iguales)
// -------------------------------------------------------------
@Composable
private fun GroupsGridView(
    visual: VisualRepresentation,
    accentColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "${visual.groupCount} grupos de ${visual.itemsPerGroup}",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(visual.groupCount.coerceAtMost(5)) { groupIndex ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 6.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(accentColor.copy(alpha = 0.08f))
                        .border(1.5.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        repeat(visual.itemsPerGroup.coerceAtMost(5)) {
                            Text(text = visual.primaryEmoji, fontSize = 28.sp)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 4. SHARING_PARTITION: División (reparto en canastas)
// -------------------------------------------------------------
@Composable
private fun SharingPartitionView(
    visual: VisualRepresentation,
    accentColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Reparte ${visual.totalToShare} en ${visual.containerCount} recipientes iguales",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(visual.containerCount.coerceAtMost(5)) { index ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(accentColor.copy(alpha = 0.08f))
                            .border(1.5.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🧺", fontSize = 30.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row {
                                repeat(visual.itemsPerContainer.coerceAtMost(5)) {
                                    Text(text = visual.primaryEmoji, fontSize = 22.sp)
                                }
                            }
                        }
                    }
                    Text(
                        text = "Canasta ${index + 1}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SlateMuted,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. BASE_TEN_BLOCKS: Decenas y Unidades (Fase 2 - Nivel 5)
// -------------------------------------------------------------
@Composable
private fun BaseTenBlocksView(
    visual: VisualRepresentation,
    accentColor: Color
) {
    val isSubtraction = visual.isSubtraction
    val opSymbol = if (isSubtraction) "➖" else "➕"
    val subtitle = if (isSubtraction) "Bloques de Decenas (10) y Unidades (1) - Resta" else "Bloques de Decenas (10) y Unidades (1)"

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = subtitle,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = SlateMuted
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Número A
            NumberBaseTenBlock(tens = visual.tensA, units = visual.unitsA, accentColor = accentColor)

            Text(text = opSymbol, fontSize = 22.sp, color = accentColor)

            // Número B
            NumberBaseTenBlock(tens = visual.tensB, units = visual.unitsB, accentColor = OrangeSecondary)
        }
    }
}

@Composable
private fun NumberBaseTenBlock(tens: Int, units: Int, accentColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            // Decenas (barras más grandes y claras)
            repeat(tens.coerceAtMost(5)) {
                Box(
                    modifier = Modifier
                        .width(18.dp)
                        .height(54.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(accentColor)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            // Unidades (cubos más grandes y claros)
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(units.coerceAtMost(9)) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(accentColor.copy(alpha = 0.85f))
                    )
                }
            }
        }
        Text(
            text = "${tens * 10 + units}",
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            color = SlateDark,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

// -------------------------------------------------------------
// 6. WORD_PROBLEM: Problemas con narración contextual
// -------------------------------------------------------------
@Composable
private fun WordProblemView(
    visual: VisualRepresentation,
    accentColor: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            Text(text = visual.problemIcon, fontSize = 34.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Problema Cotidiano",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )
        }

        if (visual.problemStory != null) {
            Text(
                text = visual.problemStory,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = SlateDark,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }
    }
}

// -------------------------------------------------------------
// 7. NUMBER_FOCUS: Apoyo visual pedagógico (conteo para números pequeños, descomposición para números de 2 y 3 cifras)
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NumberFocusVisualView(
    visual: VisualRepresentation,
    accentColor: Color
) {
    if (visual.countA > 10 || visual.countB > 10) {
        PositionalDecompositionCompactCard(
            numA = visual.countA,
            numB = visual.countB,
            accentColor = accentColor
        )
    } else {
        FlowRow(
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.Center,
            maxItemsInEachRow = 8,
            modifier = Modifier.fillMaxWidth()
        ) {
            val count = (visual.countA + visual.countB).coerceIn(2, 10)
            repeat(count) {
                InteractiveEmojiItem(emoji = visual.primaryEmoji, fontSize = 32.sp)
            }
        }
    }
}

@Composable
private fun PositionalDecompositionCompactCard(
    numA: Int,
    numB: Int,
    accentColor: Color
) {
    val isThreeDigits = numA >= 100 || numB >= 100
    val cA = numA / 100
    val dA = (numA / 10) % 10
    val uA = numA % 10

    val cB = numB / 100
    val dB = (numB / 10) % 10
    val uB = numB % 10

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.2.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .padding(vertical = 8.dp, horizontal = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "🏛️", fontSize = 14.sp)
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "Descomposición por Valor Posicional",
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        NumberDecompositionRow(
            number = numA,
            hundreds = if (isThreeDigits) cA else 0,
            tens = dA,
            units = uA,
            isThreeDigits = isThreeDigits
        )

        Spacer(modifier = Modifier.height(4.dp))

        NumberDecompositionRow(
            number = numB,
            hundreds = if (isThreeDigits) cB else 0,
            tens = dB,
            units = uB,
            isThreeDigits = isThreeDigits
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "💡 Suma primero Unidades 🟠, luego Decenas 🔵 y Centenas 🟢",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = SlateMuted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun NumberDecompositionRow(
    number: Int,
    hundreds: Int,
    tens: Int,
    units: Int,
    isThreeDigits: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "$number =",
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = SlateDark,
            modifier = Modifier.padding(end = 6.dp)
        )

        if (isThreeDigits) {
            // Centena chip (Verde)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFE8F5E9))
                    .border(1.dp, Color(0xFF81C784), RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${hundreds * 100} (${hundreds}C)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2E7D32)
                )
            }
            Text(text = " + ", fontSize = 11.sp, fontWeight = FontWeight.Black, color = SlateMuted)
        }

        // Decena chip (Azul)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFE3F2FD))
                .border(1.dp, Color(0xFF64B5F6), RoundedCornerShape(8.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "${tens * 10} (${tens}D)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1565C0)
            )
        }

        Text(text = " + ", fontSize = 11.sp, fontWeight = FontWeight.Black, color = SlateMuted)

        // Unidad chip (Naranja)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFFF3E0))
                .border(1.dp, Color(0xFFFFB74D), RoundedCornerShape(8.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "$units (${units}U)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE65100)
            )
        }
    }
}

// -------------------------------------------------------------
// Micro-interacción: emoji que responde con rebote al pulsar
// -------------------------------------------------------------
@Composable
private fun InteractiveEmojiItem(
    emoji: String,
    fontSize: TextUnit = 36.sp,
    modifier: Modifier = Modifier,
    onTap: (() -> Unit)? = null
) {
    var bounceTrigger by remember { mutableIntStateOf(0) }
    val scale by animateFloatAsState(
        targetValue = if (bounceTrigger > 0) 1.28f else 1.0f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = 600f),
        label = "emoji_bounce"
    )

    LaunchedEffect(bounceTrigger) {
        if (bounceTrigger > 0) {
            delay(160)
            bounceTrigger = 0
        }
    }

    Text(
        text = emoji,
        fontSize = fontSize,
        modifier = modifier
            .padding(3.dp)
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                bounceTrigger++
                onTap?.invoke()
            }
    )
}

// -------------------------------------------------------------
// 8. NUMBER_LINE: Recta Numérica con saltos adelante y atrás
// -------------------------------------------------------------
@Composable
private fun NumberLineView(
    visual: VisualRepresentation,
    accentColor: Color
) {
    val start = visual.numberLineStart
    val end = visual.numberLineEnd
    val initial = visual.numberLineInitial
    val jump = visual.numberLineJump
    val dest = visual.numberLineDestination
    val isForward = visual.isJumpForward

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Píldora de salto arriba
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(accentColor.copy(alpha = 0.12f))
                .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            val jumpSign = if (isForward) "+" else "-"
            val jumpLabel = if (visual.numberLineRepeatedJumps > 1) {
                "${visual.numberLineRepeatedJumps} saltos de $jump"
            } else {
                "Salto: $jumpSign$jump"
            }
            Text(
                text = "📏 $jumpLabel",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Fila horizontal con la recta numérica
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            (start..end).forEach { num ->
                val isInitial = num == initial
                val isDest = num == dest
                val isHighlighted = isInitial || isDest

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    // Marcador superior si es punto de inicio o destino
                    if (isInitial) {
                        Text(text = "📍", fontSize = 16.sp)
                    } else if (isDest) {
                        Text(text = "🎯", fontSize = 16.sp)
                    } else {
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // Círculo del número
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isDest -> accentColor
                                    isInitial -> OrangeSecondary
                                    else -> SurfaceWhite
                                }
                            )
                            .border(
                                width = if (isHighlighted) 2.5.dp else 1.5.dp,
                                color = if (isHighlighted) Color.White else SlateBorder,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$num",
                            fontSize = 16.sp,
                            fontWeight = if (isHighlighted) FontWeight.Black else FontWeight.Bold,
                            color = if (isHighlighted) Color.White else SlateDark
                        )
                    }

                    // Marca de tick bajo el círculo
                    Box(
                        modifier = Modifier
                            .width(2.5.dp)
                            .height(8.dp)
                            .background(if (isHighlighted) accentColor else SlateBorder)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 9. TEN_FRAME: Marco de Diez y Doble Marco (Singapur / ABN)
// -------------------------------------------------------------
@Composable
private fun TenFrameView(
    visual: VisualRepresentation,
    accentColor: Color
) {
    val countA = visual.tenFrameCountA
    val countB = visual.tenFrameCountB
    val takeout = visual.tenFrameTakeout
    val isDouble = visual.tenFrameCapacity > 10

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (isDouble) "Doble Marco de Diez (hasta 20)" else "Marco de Diez (10 Casillas)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = SlateMuted
        )
        Spacer(modifier = Modifier.height(10.dp))

        if (isDouble) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Primer marco de 10
                SingleTenFrameGrid(
                    startIndex = 1,
                    countA = countA.coerceAtMost(10),
                    countB = if (countA >= 10) 0 else countB.coerceAtMost(10 - countA),
                    takeout = takeout.coerceAtMost(10),
                    accentColor = accentColor,
                    secondaryColor = OrangeSecondary
                )

                // Segundo marco
                val remA = (countA - 10).coerceAtLeast(0)
                val remB = if (countA < 10) (countB - (10 - countA)).coerceAtLeast(0) else countB
                SingleTenFrameGrid(
                    startIndex = 11,
                    countA = remA,
                    countB = remB,
                    takeout = (takeout - 10).coerceAtLeast(0),
                    accentColor = accentColor,
                    secondaryColor = OrangeSecondary
                )
            }
        } else {
            SingleTenFrameGrid(
                startIndex = 1,
                countA = countA.coerceAtMost(10),
                countB = if (countA >= 10) 0 else countB.coerceAtMost(10 - countA),
                takeout = takeout.coerceAtMost(10),
                accentColor = accentColor,
                secondaryColor = OrangeSecondary
            )
        }
    }
}

@Composable
private fun SingleTenFrameGrid(
    startIndex: Int,
    countA: Int,
    countB: Int,
    takeout: Int,
    accentColor: Color,
    secondaryColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFF1F5F9))
            .border(2.dp, SlateBorder, RoundedCornerShape(14.dp))
            .padding(8.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            // Fila 1 (5 casillas)
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                (0 until 5).forEach { slotIndex ->
                    TenFrameCell(slotIndex, countA, countB, takeout, accentColor, secondaryColor)
                }
            }
            // Fila 2 (5 casillas)
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                (5 until 10).forEach { slotIndex ->
                    TenFrameCell(slotIndex, countA, countB, takeout, accentColor, secondaryColor)
                }
            }
        }
    }
}

@Composable
private fun TenFrameCell(
    slotIndex: Int,
    countA: Int,
    countB: Int,
    takeout: Int,
    accentColor: Color,
    secondaryColor: Color
) {
    val isPartA = slotIndex < countA
    val isPartB = slotIndex >= countA && slotIndex < (countA + countB)
    val isTakeout = slotIndex < countA && slotIndex >= (countA - takeout)

    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceWhite)
            .border(1.5.dp, SlateBorder.copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
        contentAlignment = Alignment.Center
    ) {
        when {
            isTakeout -> {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.25f))
                    )
                    Text(text = "❌", fontSize = 18.sp)
                }
            }
            isPartA -> {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(accentColor)
                )
            }
            isPartB -> {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(secondaryColor)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 10. NUMBER_BONDS: Enlaces Numéricos Singapur (Parte-Parte-Todo)
// -------------------------------------------------------------
@Composable
private fun NumberBondsView(
    visual: VisualRepresentation,
    accentColor: Color
) {
    val isWholeMissing = visual.missingBondElement == BondElement.WHOLE
    val isPartAMissing = visual.missingBondElement == BondElement.PART_A
    val isPartBMissing = visual.missingBondElement == BondElement.PART_B

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Enlaces Numéricos (Parte - Parte - Todo)",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = SlateMuted
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Círculo Superior: Todo (Whole)
        BondNode(
            text = if (isWholeMissing) "?" else "${visual.bondWhole}",
            label = "Todo",
            isMissing = isWholeMissing,
            color = accentColor,
            size = 68.dp
        )

        // Líneas de conexión
        Canvas(
            modifier = Modifier
                .width(150.dp)
                .height(32.dp)
        ) {
            val topCenter = Offset(size.width / 2, 0f)
            val leftBottom = Offset(size.width * 0.2f, size.height)
            val rightBottom = Offset(size.width * 0.8f, size.height)

            drawLine(
                color = accentColor.copy(alpha = 0.5f),
                start = topCenter,
                end = leftBottom,
                strokeWidth = 3.5.dp.toPx()
            )
            drawLine(
                color = accentColor.copy(alpha = 0.5f),
                start = topCenter,
                end = rightBottom,
                strokeWidth = 3.5.dp.toPx()
            )
        }

        // Círculos Inferiores: Partes (Parts)
        Row(
            modifier = Modifier.width(190.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BondNode(
                text = if (isPartAMissing) "?" else "${visual.bondPartA}",
                label = "Parte",
                isMissing = isPartAMissing,
                color = OrangeSecondary,
                size = 56.dp
            )
            BondNode(
                text = if (isPartBMissing) "?" else "${visual.bondPartB}",
                label = "Parte",
                isMissing = isPartBMissing,
                color = GreenPrimary,
                size = 56.dp
            )
        }
    }
}

@Composable
private fun BondNode(
    text: String,
    label: String,
    isMissing: Boolean,
    color: Color,
    size: androidx.compose.ui.unit.Dp
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(if (isMissing) color.copy(alpha = 0.12f) else color)
                .border(
                    width = if (isMissing) 2.5.dp else 1.5.dp,
                    color = color,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = if (size > 60.dp) 24.sp else 20.sp,
                fontWeight = FontWeight.Black,
                color = if (isMissing) color else Color.White
            )
        }
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = SlateMuted,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

// -------------------------------------------------------------
// 11. ARRAY_GRID: Matriz Rectangular de Filas × Columnas
// -------------------------------------------------------------
@Composable
private fun ArrayGridView(
    visual: VisualRepresentation,
    accentColor: Color
) {
    val rows = visual.arrayRows.coerceIn(1, 8)
    val cols = visual.arrayCols.coerceIn(1, 8)
    val emoji = visual.primaryEmoji
    val emojiSize = if (cols > 4) 28.sp else 34.sp

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$rows filas × $cols columnas",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor
        )
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(accentColor.copy(alpha = 0.06f))
                .border(1.5.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(rows) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        repeat(cols) {
                            InteractiveEmojiItem(emoji = emoji, fontSize = emojiSize)
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 12. SKIP_COUNTING: Series y Patrones Rítmicos
// -------------------------------------------------------------
@Composable
private fun SkipCountingView(
    visual: VisualRepresentation,
    accentColor: Color,
    keypadInput: String = ""
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Serie Rítmica (+${visual.sequenceStep})",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = SlateMuted
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            visual.sequenceItems.forEachIndexed { index, item ->
                if (item == null) {
                    // Tarjeta misteriosa interactiva
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(OrangeSecondary.copy(alpha = 0.15f))
                            .border(2.5.dp, OrangeSecondary, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (keypadInput.isNotEmpty()) keypadInput else "?",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = OrangeSecondary
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(SurfaceWhite)
                            .border(1.5.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$item",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = SlateDark
                        )
                    }
                }

                if (index < visual.sequenceItems.size - 1) {
                    Text(
                        text = " ➔ ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor.copy(alpha = 0.6f),
                        modifier = Modifier.padding(horizontal = 3.dp)
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 13. BALANCE_SCALE: Balanza de Equivalencia Numérica
// -------------------------------------------------------------
@Composable
private fun BalanceScaleView(
    visual: VisualRepresentation,
    accentColor: Color,
    keypadInput: String = ""
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "⚖️ Balanza de Equivalencia Numérica",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = SlateMuted
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Barra horizontal con los dos platillos ampliados
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val leftText = if (visual.scaleLeftText.contains("?") && keypadInput.isNotEmpty()) {
                visual.scaleLeftText.replace("?", keypadInput)
            } else {
                visual.scaleLeftText.ifEmpty { "A + B" }
            }

            // Platillo Izquierdo
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(accentColor.copy(alpha = 0.1f))
                    .border(2.dp, accentColor, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = leftText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = accentColor
                )
            }

            // Indicador de equilibrio central
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(GreenPrimary)
                    .border(2.5.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "=",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            val rightText = if (visual.scaleRightText.contains("?") && keypadInput.isNotEmpty()) {
                visual.scaleRightText.replace("?", keypadInput)
            } else {
                visual.scaleRightText.ifEmpty { "?" }
            }

            // Platillo Derecho
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(OrangeSecondary.copy(alpha = 0.1f))
                    .border(2.dp, OrangeSecondary, RoundedCornerShape(16.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rightText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = OrangeSecondary
                )
            }
        }
    }
}

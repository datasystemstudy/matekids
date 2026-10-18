package com.educa.mathkids.presentation.screens.calculator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.presentation.components.ConfettiCelebration
import com.educa.mathkids.presentation.components.DynamicKidsBackground
import com.educa.mathkids.presentation.components.MascotMood
import com.educa.mathkids.presentation.components.ScreenHeader
import com.educa.mathkids.presentation.localization.AppLanguage
import com.educa.mathkids.presentation.localization.LocalAppLanguage
import com.educa.mathkids.presentation.theme.BluePrimary
import com.educa.mathkids.presentation.theme.CoralEncouragement
import com.educa.mathkids.presentation.theme.Dimens
import com.educa.mathkids.presentation.theme.FredokaFont
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.OrangeSecondary
import com.educa.mathkids.presentation.theme.PurpleFun
import com.educa.mathkids.presentation.theme.SlateBorder
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SlateMuted
import com.educa.mathkids.presentation.theme.SoftRed
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.utils.HapticUtil
import com.educa.mathkids.presentation.utils.SoundManager
import com.educa.mathkids.presentation.utils.TtsManager
import kotlinx.coroutines.delay

/**
 * Pantalla de Calculadora Interactiva y Didáctica para Niños (MateKids).
 * Permite visualizar números con objetos contables reales (🍎, ⭐, 🚗, etc.),
 * reproduce efectos sonoros, habla los resultados con TTS y celebra con confeti.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KidsCalculatorScreen(
    onBackClick: () -> Unit,
    soundManager: SoundManager? = null,
    ttsManager: TtsManager? = null,
    hapticUtil: HapticUtil? = null,
    modifier: Modifier = Modifier
) {
    val isEnglish = LocalAppLanguage.current == AppLanguage.INGLES

    var firstOperand by remember { mutableStateOf("0") }
    var operator by remember { mutableStateOf<String?>(null) }
    var secondOperand by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf<String?>(null) }
    var showCelebration by remember { mutableStateOf(false) }

    val emojis = listOf("🍎", "⭐", "🚗", "🐱", "🧁", "🎈")
    var selectedEmoji by remember { mutableStateOf(emojis.first()) }

    var isVoiceMuted by remember { mutableStateOf(!(ttsManager?.isVoiceEnabled ?: true)) }

    var mascotMessage by remember {
        mutableStateOf(
            if (isEnglish) "Touch the numbers and let's calculate together!"
            else "¡Toca los números y resolvamos juntos!"
        )
    }

    LaunchedEffect(showCelebration) {
        if (showCelebration) {
            delay(2800)
            showCelebration = false
        }
    }

    fun playClickSound() {
        hapticUtil?.vibrateClick()
        soundManager?.playButtonClick()
    }

    fun toggleVoice() {
        playClickSound()
        isVoiceMuted = !isVoiceMuted
        if (isVoiceMuted) {
            ttsManager?.stop()
            ttsManager?.isVoiceEnabled = false
            mascotMessage = if (isEnglish) "Voice muted 🔇" else "Voz silenciada 🔇"
        } else {
            ttsManager?.isVoiceEnabled = true
            val onMsg = if (isEnglish) "Voice on! 🔊" else "¡Voz activada! 🔊"
            mascotMessage = onMsg
            ttsManager?.speak(onMsg)
        }
    }

    fun onDigit(digit: String) {
        playClickSound()

        if (resultText != null) {
            firstOperand = digit
            operator = null
            secondOperand = ""
            resultText = null
            return
        }

        if (operator == null) {
            if (firstOperand == "0") {
                firstOperand = digit
            } else if (firstOperand.length < 3) {
                firstOperand += digit
            }
        } else {
            if (secondOperand == "0") {
                secondOperand = digit
            } else if (secondOperand.length < 3) {
                secondOperand += digit
            }
        }
    }

    fun onOperator(op: String) {
        playClickSound()
        if (resultText != null) {
            firstOperand = resultText ?: "0"
            secondOperand = ""
            resultText = null
        }
        operator = op
    }

    fun onClear() {
        playClickSound()
        firstOperand = "0"
        operator = null
        secondOperand = ""
        resultText = null
        mascotMessage = if (isEnglish) "Cleared! What do you want to calculate now?" else "¡Limpio! ¿Qué quieres calcular ahora?"
    }

    fun onBackspace() {
        playClickSound()
        if (resultText != null) {
            resultText = null
            return
        }
        if (secondOperand.isNotEmpty()) {
            secondOperand = secondOperand.dropLast(1)
        } else if (operator != null) {
            operator = null
        } else {
            firstOperand = if (firstOperand.length > 1) firstOperand.dropLast(1) else "0"
        }
    }

    fun onCalculate() {
        hapticUtil?.vibrateSuccess()
        val num1 = firstOperand.toIntOrNull() ?: 0
        val num2 = secondOperand.toIntOrNull()

        if (operator == null || num2 == null) {
            resultText = firstOperand
            return
        }

        val op = operator ?: "+"
        var calculated = 0
        var isDivisionByZero = false

        when (op) {
            "+" -> calculated = num1 + num2
            "−" -> calculated = num1 - num2
            "×" -> calculated = num1 * num2
            "÷" -> {
                if (num2 == 0) {
                    isDivisionByZero = true
                } else {
                    calculated = num1 / num2
                }
            }
        }

        if (isDivisionByZero) {
            soundManager?.playIncorrect()
            val errorMsg = if (isEnglish) "Oops! We can't divide by 0!" else "¡Ups! ¡No se puede dividir entre 0!"
            mascotMessage = errorMsg
            if (!isVoiceMuted) {
                ttsManager?.speak(errorMsg)
            }
            return
        }

        resultText = calculated.toString()
        soundManager?.playVictory()
        showCelebration = true

        val speechText = if (isEnglish) {
            val opWord = when (op) {
                "+" -> "plus"
                "−" -> "minus"
                "×" -> "times"
                "÷" -> "divided by"
                else -> "and"
            }
            "$num1 $opWord $num2 equals $calculated!"
        } else {
            val opWord = when (op) {
                "+" -> "más"
                "−" -> "menos"
                "×" -> "por"
                "÷" -> "dividido entre"
                else -> "con"
            }
            "¡$num1 $opWord $num2 es igual a $calculated!"
        }

        mascotMessage = speechText
        if (!isVoiceMuted) {
            ttsManager?.speak(speechText)
        }
    }

    val num1Value = firstOperand.toIntOrNull() ?: 0
    val num2Value = secondOperand.toIntOrNull() ?: 0
    val resultValue = resultText?.toIntOrNull()

    DynamicKidsBackground(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = Dimens.SpacingMedium)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Encabezado con Botón Inicio 3D y Bocina Silenciar/Activar
                ScreenHeader(
                    title = if (isEnglish) "KIDS CALCULATOR" else "CALCULADORA",
                    isHomeButton = true,
                    onBackClick = onBackClick,
                    trailingContent = {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .shadow(
                                    elevation = 3.dp,
                                    shape = RoundedCornerShape(14.dp),
                                    spotColor = if (isVoiceMuted) SoftRed.copy(alpha = 0.4f) else GreenPrimary.copy(alpha = 0.4f)
                                )
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.verticalGradient(
                                        if (isVoiceMuted) listOf(Color(0xFFFFEBEE), Color(0xFFFFCDD2))
                                        else listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9))
                                    )
                                )
                                .border(
                                    width = 2.dp,
                                    color = if (isVoiceMuted) SoftRed else GreenPrimary,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { toggleVoice() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isVoiceMuted) "🔇" else "🔊",
                                fontSize = 22.sp
                            )
                        }
                    },
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )

                // Selector de Objetos Contables Didácticos
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = PurpleFun.copy(alpha = 0.2f))
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceWhite)
                        .border(1.5.dp, Color(0xFFE1BEE7), RoundedCornerShape(16.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEnglish) "Objects:" else "Objetos:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FredokaFont,
                        color = SlateMuted,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        emojis.forEach { emoji ->
                            val isSelected = selectedEmoji == emoji
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Color(0xFFF3E5F5) else Color.Transparent)
                                    .border(
                                        width = if (isSelected) 2.dp else 0.dp,
                                        color = if (isSelected) PurpleFun else Color.Transparent,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        hapticUtil?.vibrateClick()
                                        selectedEmoji = emoji
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 20.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Mensaje Didáctico del Asistente con indicador interactivo de voz
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isVoiceMuted) Color(0xFFFFF3E0) else Color(0xFFE8F5E9))
                        .border(
                            1.5.dp,
                            if (isVoiceMuted) OrangeSecondary.copy(alpha = 0.45f) else GreenPrimary.copy(alpha = 0.45f),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { toggleVoice() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = mascotMessage,
                            fontSize = 12.5.sp,
                            fontFamily = FredokaFont,
                            fontWeight = FontWeight.Bold,
                            color = SlateDark,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isVoiceMuted) "🔇" else "🔊",
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // PANTALLA DIGITAL DE LA CALCULADORA
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = Color(0xFF00BCD4).copy(alpha = 0.3f))
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFFF0FDF4), Color(0xFFDCFCE7))
                            )
                        )
                        .border(2.5.dp, Color(0xFF22C55E), RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        // Expresión en curso
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = firstOperand,
                                fontSize = 32.sp,
                                fontFamily = FredokaFont,
                                fontWeight = FontWeight.Black,
                                color = BluePrimary
                            )
                            if (operator != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(OrangeSecondary)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = operator ?: "",
                                        fontSize = 24.sp,
                                        fontFamily = FredokaFont,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (secondOperand.isEmpty()) "?" else secondOperand,
                                    fontSize = 32.sp,
                                    fontFamily = FredokaFont,
                                    fontWeight = FontWeight.Black,
                                    color = if (secondOperand.isEmpty()) SlateMuted else CoralEncouragement
                                )
                            }
                        }

                        // Resultado
                        if (resultText != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "= ",
                                    fontSize = 34.sp,
                                    fontFamily = FredokaFont,
                                    fontWeight = FontWeight.Black,
                                    color = SlateMuted
                                )
                                Text(
                                    text = resultText ?: "",
                                    fontSize = 44.sp,
                                    fontFamily = FredokaFont,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF15803D)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ÁREA VISUAL DIDÁCTICA DE OBJETOS CONTABLES
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(18.dp), spotColor = Color.Black.copy(alpha = 0.08f))
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceWhite)
                        .border(1.5.dp, SlateBorder, RoundedCornerShape(18.dp))
                        .padding(10.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isEnglish) "Visual Counting Board 🧮" else "Tablero Visual de Conteo 🧮",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = SlateMuted
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        if (resultValue != null && resultValue in 0..30) {
                            // Mostrar resultado con objetos
                            FlowRow(
                                horizontalArrangement = Arrangement.Center,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                repeat(resultValue) {
                                    Text(
                                        text = selectedEmoji,
                                        fontSize = 22.sp,
                                        modifier = Modifier.padding(2.dp)
                                    )
                                }
                            }
                        } else {
                            // Mostrar primer y segundo operando didácticamente
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Objetos grupo 1
                                if (num1Value in 1..15) {
                                    FlowRow(
                                        horizontalArrangement = Arrangement.Center,
                                        modifier = Modifier.weight(1f, fill = false)
                                    ) {
                                        repeat(num1Value) {
                                            Text(text = selectedEmoji, fontSize = 20.sp, modifier = Modifier.padding(1.dp))
                                        }
                                    }
                                } else if (num1Value > 15) {
                                    Text(text = "$selectedEmoji x $num1Value", fontSize = 16.sp, fontWeight = FontWeight.Black)
                                } else {
                                    Text(text = "0 $selectedEmoji", fontSize = 14.sp, color = SlateMuted)
                                }

                                if (operator != null) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = operator ?: "",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = OrangeSecondary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))

                                    // Objetos grupo 2
                                    if (num2Value in 1..15) {
                                        FlowRow(
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.weight(1f, fill = false)
                                        ) {
                                            repeat(num2Value) {
                                                Text(text = selectedEmoji, fontSize = 20.sp, modifier = Modifier.padding(1.dp))
                                            }
                                        }
                                    } else if (num2Value > 15) {
                                        Text(text = "$selectedEmoji x $num2Value", fontSize = 16.sp, fontWeight = FontWeight.Black)
                                    } else {
                                        Text(text = "?", fontSize = 16.sp, color = SlateMuted, fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // TECLADO INFANTIL 3D
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Fila 1: Borrar todo, Borrar uno, Hablar, Dividir
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalculatorKey(
                            text = "C",
                            color = SoftRed,
                            shadowColor = Color(0xFFC62828),
                            onClick = { onClear() },
                            modifier = Modifier.weight(1f)
                        )
                        CalculatorKey(
                            text = "⌫",
                            color = Color(0xFFFF7043),
                            shadowColor = Color(0xFFD84315),
                            onClick = { onBackspace() },
                            modifier = Modifier.weight(1f)
                        )
                        CalculatorKey(
                            text = if (isVoiceMuted) "🔇" else "🔊",
                            color = if (isVoiceMuted) Color(0xFF757575) else Color(0xFF8E24AA),
                            shadowColor = if (isVoiceMuted) Color(0xFF424242) else Color(0xFF4A148C),
                            onClick = { toggleVoice() },
                            modifier = Modifier.weight(1f)
                        )
                        CalculatorKey(
                            text = "÷",
                            color = Color(0xFF00ACC1),
                            shadowColor = Color(0xFF006064),
                            onClick = { onOperator("÷") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Fila 2: 7, 8, 9, Multiplicar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalculatorKey(text = "7", onClick = { onDigit("7") }, modifier = Modifier.weight(1f))
                        CalculatorKey(text = "8", onClick = { onDigit("8") }, modifier = Modifier.weight(1f))
                        CalculatorKey(text = "9", onClick = { onDigit("9") }, modifier = Modifier.weight(1f))
                        CalculatorKey(
                            text = "×",
                            color = Color(0xFFAB47BC),
                            shadowColor = Color(0xFF6A1B9A),
                            onClick = { onOperator("×") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Fila 3: 4, 5, 6, Restar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalculatorKey(text = "4", onClick = { onDigit("4") }, modifier = Modifier.weight(1f))
                        CalculatorKey(text = "5", onClick = { onDigit("5") }, modifier = Modifier.weight(1f))
                        CalculatorKey(text = "6", onClick = { onDigit("6") }, modifier = Modifier.weight(1f))
                        CalculatorKey(
                            text = "−",
                            color = OrangeSecondary,
                            shadowColor = Color(0xFFE65100),
                            onClick = { onOperator("−") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Fila 4: 1, 2, 3, Sumar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalculatorKey(text = "1", onClick = { onDigit("1") }, modifier = Modifier.weight(1f))
                        CalculatorKey(text = "2", onClick = { onDigit("2") }, modifier = Modifier.weight(1f))
                        CalculatorKey(text = "3", onClick = { onDigit("3") }, modifier = Modifier.weight(1f))
                        CalculatorKey(
                            text = "+",
                            color = BluePrimary,
                            shadowColor = Color(0xFF0D47A1),
                            onClick = { onOperator("+") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Fila 5: 0 (ancho) y Botón Grande Calcular (=)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalculatorKey(
                            text = "0",
                            onClick = { onDigit("0") },
                            modifier = Modifier.weight(1.3f)
                        )
                        CalculatorKey(
                            text = if (isEnglish) "CALCULATE =" else "¡CALCULAR! =",
                            color = GreenPrimary,
                            shadowColor = Color(0xFF1B5E20),
                            fontSize = 19.sp,
                            onClick = { onCalculate() },
                            modifier = Modifier.weight(2.7f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Confeti triunfal cuando se calcula un resultado
            ConfettiCelebration(visible = showCelebration)
        }
    }
}

/**
 * Botón táctil 3D con textura infantil para la calculadora.
 */
@Composable
private fun CalculatorKey(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF3F51B5),
    shadowColor: Color = Color(0xFF1A237E),
    fontSize: androidx.compose.ui.unit.TextUnit = 24.sp,
    height: Dp = 54.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
        label = "key_scale"
    )

    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .scale(scale)
            .height(height)
            .shadow(
                elevation = if (isPressed) 2.dp else 5.dp,
                shape = shape,
                spotColor = shadowColor.copy(alpha = 0.5f)
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        color,
                        shadowColor
                    )
                )
            )
            .border(1.5.dp, Color.White.copy(alpha = 0.45f), shape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            fontFamily = FredokaFont,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
    }
}

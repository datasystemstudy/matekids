package com.educa.mathkids.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.presentation.theme.BluePrimary
import com.educa.mathkids.presentation.theme.FredokaFont
import com.educa.mathkids.presentation.theme.OrangeSecondary
import com.educa.mathkids.presentation.theme.SlateBorder
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.utils.TtsManager
import kotlinx.coroutines.delay

/**
 * Frases oficiales y respetuosas de Mate según la metodología pedagógica de MateKids:
 * Nunca castigar ni decir "fallaste" o "eres malo".
 * Fomentar comprensión, resiliencia y alegría por aprender.
 */
object MetaPhrases {
    val GREETINGS = listOf(
        "¡Hola! 👋 ¡Aprender matemáticas es una aventura!",
        "¡Qué alegría verte! ¿Listo para jugar con números?",
        "¡Hola, pequeño genio! Mate te acompaña hoy 🚀"
    )

    val PRAISES = listOf(
        "¡Excelente! 🌟",
        "¡Lo hiciste genial! 🎯",
        "¡Tu cerebro está creciendo! 🧠✨",
        "¡Increíble trabajo! ⭐",
        "¡Eres una estrella matemática! 🚀"
    )

    val PRAISES_EN = listOf(
        "Awesome! 🌟",
        "You did great! 🎯",
        "Your brain is growing! 🧠✨",
        "Incredible job! ⭐",
        "You are a math star! 🚀"
    )

    val ENCOURAGEMENTS = listOf(
        "Casi lo tienes. 💡 ¡Probemos otra vez!",
        "¡No pasa nada! Vamos a descubrirlo juntos.",
        "¡Cada intento te hace más inteligente! 🌱",
        "¡Respira y miremos las pistas! 🔍"
    )

    val ENCOURAGEMENTS_EN = listOf(
        "Almost there. 💡 Let's try again!",
        "Don't worry! Let's discover it together.",
        "Every attempt makes you smarter! 🌱",
        "Take a breath and check the hints! 🔍"
    )

    fun getPraise(isEnglish: Boolean): String =
        if (isEnglish) PRAISES_EN.random() else PRAISES.random()

    fun getEncouragement(isEnglish: Boolean): String =
        if (isEnglish) ENCOURAGEMENTS_EN.random() else ENCOURAGEMENTS.random()

    val NUMBERS_PROMPTS = listOf(
        "Toca los números para escuchar cómo se llaman 🍎",
        "¡Descubre cuántos objetos hay en cada número!",
        "¡Contar es como un juego de magia!"
    )
}

/**
 * Componente unificado del personaje virtual "Mate" junto con su bocadillo de diálogo (Speech Bubble).
 * Permite reproducir la frase con TTS y reaccionar al tacto de los niños.
 */
@Composable
fun MetaCompanion(
    message: String,
    modifier: Modifier = Modifier,
    mood: MascotMood = MascotMood.NORMAL,
    mascotSize: Dp = 100.dp,
    ttsManager: TtsManager? = null,
    autoSpeak: Boolean = false,
    onMascotClick: (() -> Unit)? = null
) {
    var isTapped by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isTapped) 1.12f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "meta_tap_scale"
    )

    LaunchedEffect(message, autoSpeak) {
        if (autoSpeak && message.isNotEmpty()) {
            ttsManager?.speak(message)
        }
    }

    LaunchedEffect(isTapped) {
        if (isTapped) {
            delay(350)
            isTapped = false
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Mascota Mate interactiva
        Box(
            modifier = Modifier
                .scale(scale)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isTapped = true
                    ttsManager?.speak(message)
                    onMascotClick?.invoke()
                }
        ) {
            Mascot(
                size = mascotSize,
                mood = if (isTapped) MascotMood.SURPRISED else mood
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Bocadillo de diálogo de Mate
        AnimatedVisibility(
            visible = message.isNotEmpty(),
            enter = fadeIn() + scaleIn(initialScale = 0.85f)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.08f))
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceWhite)
                    .border(2.dp, BluePrimary.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f, fill = false)) {
                        Text(
                            text = "Mate:",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = BluePrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = message,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FredokaFont,
                            color = SlateDark,
                            lineHeight = 19.sp
                        )
                    }

                    if (ttsManager != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(OrangeSecondary.copy(alpha = 0.15f))
                                .border(1.dp, OrangeSecondary.copy(alpha = 0.4f), CircleShape)
                                .clickable {
                                    ttsManager.speak(message)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🔊", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

package com.educa.mathkids.presentation.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.educa.mathkids.presentation.components.AnimatedGameButton
import com.educa.mathkids.presentation.components.DynamicKidsBackground
import com.educa.mathkids.presentation.components.Mascot
import com.educa.mathkids.presentation.components.MascotMood
import com.educa.mathkids.presentation.localization.AppLanguage
import com.educa.mathkids.presentation.localization.LocalAppLanguage
import com.educa.mathkids.presentation.theme.BluePrimary
import com.educa.mathkids.presentation.theme.FredokaFont
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.OrangeSecondary
import com.educa.mathkids.presentation.theme.OrangeSecondaryDark
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.utils.SoundManager
import com.educa.mathkids.presentation.utils.TtsManager
import kotlinx.coroutines.delay

/**
 * Pantalla de Bienvenida (Splash Screen) interactiva y festiva para niños.
 * Presenta el nombre oficial MATEKIDS, el lema oficial,
 * la mascota Mate celebrando la llegada del niño, animaciones alegres
 * y audio de bienvenida con jingle musical y voz amigable para niños.
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    soundManager: SoundManager,
    ttsManager: TtsManager,
    modifier: Modifier = Modifier
) {
    val isEnglish = LocalAppLanguage.current == AppLanguage.INGLES
    val scaleAnim = remember { Animatable(0.4f) }
    val alphaAnim = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "splash_float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot_floating"
    )

    LaunchedEffect(Unit) {
        // 1. Audio musical de bienvenida estelar inmediato
        soundManager.playWelcome()

        // 2. Saludo con voz cálida y amigable para niños
        val welcomeVoiceMessage = if (isEnglish) {
            "Welcome to Math-Adventures, where you will learn and explore the exciting world of mathematics in a fun and interactive way!"
        } else {
            "Bienvenidos a Mate-Aventuras, donde aprenderás a conocer el emocionante mundo de las matemáticas de una manera didáctica y divertida."
        }
        ttsManager.speakWelcome(welcomeVoiceMessage)

        alphaAnim.animateTo(1f, animationSpec = tween(600))
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(700, easing = FastOutSlowInEasing)
        )
        // Permanece en la pantalla de bienvenida hasta que el usuario pulse ¡EMPEZAR!
    }

    DynamicKidsBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.7f))

            // 1. TÍTULO INFANTIL 3D: MATEKIDS
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .scale(scaleAnim.value)
                    .alpha(alphaAnim.value)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val titleColors = listOf(
                        BluePrimary,
                        OrangeSecondary,
                        GreenPrimary,
                        Color(0xFF9C27B0),
                        Color(0xFF00BCD4),
                        Color(0xFFFF5722),
                        Color(0xFF4CAF50),
                        Color(0xFF3F51B5)
                    )
                    "MATEKIDS".forEachIndexed { index, char ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 2.dp)
                                .shadow(6.dp, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .background(titleColors[index % titleColors.size])
                                .border(2.dp, Color.White, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = char.toString(),
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Subtítulo oficial
                Text(
                    text = if (isEnglish) "“Learning math is an adventure!”" else "“¡Aprender matemáticas es una aventura!”",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = SlateDark,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // 2. PERSONAJE MATE CON ANIMACIÓN DE FLOTACIÓN Y CELEBRACIÓN
            Box(
                modifier = Modifier
                    .offset(y = floatOffset.dp)
                    .scale(scaleAnim.value)
                    .alpha(alphaAnim.value)
                    .clickable {
                        soundManager.playCorrect()
                        ttsManager.speak(
                            if (isEnglish) "I am Mate! Let's have fun and learn together!"
                            else "¡Soy Mate! ¡Vamos a divertirnos y aprender juntos!"
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Mascot(
                    size = 190.dp,
                    mood = MascotMood.CELEBRATING
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Bocadillo de saludo de Mate
            Box(
                modifier = Modifier
                    .shadow(4.dp, RoundedCornerShape(18.dp))
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceWhite)
                    .border(2.dp, OrangeSecondary.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
                    .padding(horizontal = 18.dp, vertical = 8.dp)
                    .alpha(alphaAnim.value)
            ) {
                Text(
                    text = if (isEnglish) "Hello! I'm Mate, your math friend 🌟" else "¡Hola! Soy Mate, tu amigo de aventuras 🌟",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = SlateDark
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 3. BOTÓN RÁPIDO PARA COMENZAR INMEDIATAMENTE (permanece en pantalla hasta pulsar)
            AnimatedGameButton(
                text = if (isEnglish) "🚀 START!" else "🚀 ¡EMPEZAR!",
                onClick = {
                    soundManager.playButtonClick()
                    onSplashFinished()
                },
                primaryColor = OrangeSecondary,
                darkColor = OrangeSecondaryDark,
                height = 60.dp,
                fontSize = 22.sp,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .alpha(alphaAnim.value)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

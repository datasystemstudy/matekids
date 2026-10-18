package com.educa.mathkids.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.educa.mathkids.presentation.theme.BluePrimary
import com.educa.mathkids.presentation.theme.FredokaFont
import com.educa.mathkids.presentation.theme.GreenPrimary
import com.educa.mathkids.presentation.theme.SlateBorder
import com.educa.mathkids.presentation.theme.SlateDark
import com.educa.mathkids.presentation.theme.SoftRed
import com.educa.mathkids.presentation.theme.SurfaceWhite
import com.educa.mathkids.presentation.utils.TtsManager

/**
 * Diálogo de confirmación amigable para salir del juego o de la aplicación.
 */
@Composable
fun KidsExitDialog(
    title: String = "¿Salir de la partida?",
    message: String = "¿Seguro que quieres salir? Puedes seguir jugando cuando quieras.",
    confirmButtonText: String = "Sí, salir",
    dismissButtonText: String = "Seguir jugando",
    ttsManager: TtsManager? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = {
            ttsManager?.stop()
            onDismiss()
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(20.dp, RoundedCornerShape(28.dp))
                .clip(RoundedCornerShape(28.dp))
                .background(SurfaceWhite)
                .border(3.dp, SoftRed.copy(alpha = 0.45f), RoundedCornerShape(28.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Icono amigable de puerta
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFEBEE))
                        .border(2.dp, SoftRed.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🚪", fontSize = 34.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FredokaFont,
                    color = SlateDark,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF8FAFC))
                        .border(1.5.dp, SlateBorder, RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = message,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FredokaFont,
                        color = SlateDark,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }

                if (ttsManager != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(BluePrimary.copy(alpha = 0.12f))
                            .clickable { ttsManager.speak(message) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🔊", fontSize = 15.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = com.educa.mathkids.presentation.localization.appStrings().listenBtn,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = BluePrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Botón para Seguir Jugando
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .shadow(4.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .background(GreenPrimary)
                            .clickable {
                                ttsManager?.stop()
                                onDismiss()
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dismissButtonText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FredokaFont,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Botón para Confirmar Salida
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFFEBEE))
                            .border(1.5.dp, SoftRed.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                            .clickable {
                                ttsManager?.stop()
                                onConfirm()
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = confirmButtonText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FredokaFont,
                            color = SoftRed,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

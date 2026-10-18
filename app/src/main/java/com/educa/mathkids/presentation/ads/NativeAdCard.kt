package com.educa.mathkids.presentation.ads

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.nativead.MediaView
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdEventCallback
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoader
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoaderCallback
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdRequest
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdView

/**
 * Componente nativo de AdMob migrado a Google Mobile Ads Next-Gen SDK para MateKids.
 * Presenta anuncios integrados de manera armoniosa con la estética visual infantil de la aplicación,
 * cumpliendo con todas las directivas de atribución ("Anuncio" / "Ad") y limpieza de recursos.
 * Permite cerrar / quitar el anuncio al tocarlo o pulsar el botón de descarte.
 */
@Composable
fun NativeAdCard(
    modifier: Modifier = Modifier,
    onAdDismissed: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var nativeAdState by remember { mutableStateOf<NativeAd?>(null) }
    var isAdLoaded by remember { mutableStateOf(false) }
    var isDismissed by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val request = NativeAdRequest.Builder(
            AdConfig.getNativeAdUnitId(),
            listOf(NativeAd.NativeAdType.NATIVE)
        ).build()

        NativeAdLoader.load(
            request,
            object : NativeAdLoaderCallback {
                override fun onNativeAdLoaded(ad: NativeAd) {
                    // Configurar callback para que al tocar la publicidad nativa se retire inmediatamente
                    ad.adEventCallback = object : NativeAdEventCallback {
                        override fun onAdClicked() {
                            super.onAdClicked()
                            isDismissed = true
                            onAdDismissed?.invoke()
                        }
                    }
                    nativeAdState = ad
                    isAdLoaded = true
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    isAdLoaded = false
                }
            }
        )

        onDispose {
            nativeAdState = null
        }
    }

    if (isAdLoaded && nativeAdState != null && !isDismissed) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFFF8FAFC))
                .border(1.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(20.dp))
                .padding(12.dp)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxWidth(),
                factory = { ctx ->
                    val nativeAdView = NativeAdView(ctx)

                    // Construcción programática de la vista para evitar dependencias de XML externos
                    val container = android.widget.LinearLayout(ctx).apply {
                        orientation = android.widget.LinearLayout.VERTICAL
                        setPadding(8, 8, 8, 8)
                        setOnClickListener {
                            isDismissed = true
                            onAdDismissed?.invoke()
                        }
                    }

                    // Fila superior: Badge "Anuncio", Icono, Título y Anunciante
                    val topRow = android.widget.LinearLayout(ctx).apply {
                        orientation = android.widget.LinearLayout.HORIZONTAL
                        gravity = android.view.Gravity.CENTER_VERTICAL
                    }

                    val iconView = ImageView(ctx).apply {
                        layoutParams = android.widget.LinearLayout.LayoutParams(96, 96).apply {
                            setMargins(0, 0, 16, 0)
                        }
                    }
                    topRow.addView(iconView)
                    nativeAdView.iconView = iconView

                    val titleCol = android.widget.LinearLayout(ctx).apply {
                        orientation = android.widget.LinearLayout.VERTICAL
                        layoutParams = android.widget.LinearLayout.LayoutParams(0, android.widget.LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
                    }

                    val badgeRow = android.widget.LinearLayout(ctx).apply {
                        orientation = android.widget.LinearLayout.HORIZONTAL
                        gravity = android.view.Gravity.CENTER_VERTICAL
                    }

                    val adBadge = TextView(ctx).apply {
                        text = "ANUNCIO"
                        textSize = 10f
                        setTextColor(android.graphics.Color.WHITE)
                        setTypeface(null, android.graphics.Typeface.BOLD)
                        setPadding(10, 4, 10, 4)
                        val badgeShape = android.graphics.drawable.GradientDrawable().apply {
                            setColor(android.graphics.Color.parseColor("#FB8C00"))
                            cornerRadius = 8f
                        }
                        background = badgeShape
                    }
                    badgeRow.addView(adBadge)

                    val advertiserView = TextView(ctx).apply {
                        textSize = 11f
                        setTextColor(android.graphics.Color.parseColor("#64748B"))
                        setPadding(12, 0, 0, 0)
                    }
                    badgeRow.addView(advertiserView)
                    nativeAdView.advertiserView = advertiserView

                    titleCol.addView(badgeRow)

                    val headlineView = TextView(ctx).apply {
                        textSize = 14f
                        setTypeface(null, android.graphics.Typeface.BOLD)
                        setTextColor(android.graphics.Color.parseColor("#1E293B"))
                        setPadding(0, 6, 0, 0)
                    }
                    titleCol.addView(headlineView)
                    nativeAdView.headlineView = headlineView

                    topRow.addView(titleCol)
                    container.addView(topRow)

                    // Cuerpo del anuncio
                    val bodyView = TextView(ctx).apply {
                        textSize = 12f
                        setTextColor(android.graphics.Color.parseColor("#475569"))
                        setPadding(0, 8, 0, 10)
                    }
                    container.addView(bodyView)
                    nativeAdView.bodyView = bodyView

                    // Botón de llamada a la acción (Call to Action)
                    val callToActionView = Button(ctx).apply {
                        textSize = 13f
                        setTextColor(android.graphics.Color.WHITE)
                        setTypeface(null, android.graphics.Typeface.BOLD)
                        val btnShape = android.graphics.drawable.GradientDrawable().apply {
                            setColor(android.graphics.Color.parseColor("#2E7D32"))
                            cornerRadius = 18f
                        }
                        background = btnShape
                    }
                    container.addView(callToActionView)
                    nativeAdView.callToActionView = callToActionView

                    nativeAdView.addView(container)
                    nativeAdView
                },
                update = { nativeAdView ->
                    val ad = nativeAdState
                    if (ad != null) {
                        (nativeAdView.headlineView as? TextView)?.text = ad.headline ?: ""
                        (nativeAdView.bodyView as? TextView)?.text = ad.body ?: ""
                        (nativeAdView.advertiserView as? TextView)?.text = ad.advertiser ?: ""

                        val icon = ad.icon
                        if (icon != null && icon.drawable != null) {
                            (nativeAdView.iconView as? ImageView)?.setImageDrawable(icon.drawable)
                            nativeAdView.iconView?.visibility = View.VISIBLE
                        } else {
                            nativeAdView.iconView?.visibility = View.GONE
                        }

                        val cta = ad.callToAction
                        if (!cta.isNullOrEmpty()) {
                            (nativeAdView.callToActionView as? Button)?.text = cta
                            nativeAdView.callToActionView?.visibility = View.VISIBLE
                        } else {
                            nativeAdView.callToActionView?.visibility = View.GONE
                        }

                        nativeAdView.registerNativeAd(ad, null)
                    }
                },
                onRelease = { nativeAdView ->
                    nativeAdView.destroy()
                }
            )

            // Botón táctil para quitar la publicidad de la pantalla al tocarlo
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0))
                    .clickable {
                        isDismissed = true
                        onAdDismissed?.invoke()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✕",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

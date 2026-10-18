package com.educa.mathkids.presentation.ads

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.libraries.ads.mobile.sdk.banner.AdSize
import com.google.android.libraries.ads.mobile.sdk.banner.AdView
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError

/**
 * Componente Composable para mostrar un banner con Google Mobile Ads Next-Gen SDK,
 * adaptado a niños y con limpieza de recursos en el ciclo de vida de Compose.
 */
@Composable
fun BannerAdView(
    modifier: Modifier = Modifier,
    adSize: AdSize = AdSize.BANNER
) {
    var adLoaded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.85f))
                .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
            factory = { context ->
                AdView(context).apply {
                    val request = BannerAdRequest.Builder(
                        AdConfig.getBannerAdUnitId(),
                        adSize
                    ).build()
                    loadAd(request, object : AdLoadCallback<BannerAd> {
                        override fun onAdLoaded(ad: BannerAd) {
                            adLoaded = true
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            adLoaded = false
                        }
                    })
                }
            },
            update = { /* Sin recarga innecesaria */ },
            onRelease = { adView ->
                adView.destroy()
            }
        )
    }
}

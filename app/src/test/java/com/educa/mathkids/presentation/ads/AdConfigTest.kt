package com.educa.mathkids.presentation.ads

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pruebas unitarias para validar que los IDs de AdMob coinciden exactamente
 * con los provistos para la aplicación MateKids.
 */
class AdConfigTest {

    @Test
    fun testOfficialAdMobIdsMatchUserConfiguration() {
        assertEquals(
            "El ID de aplicación AdMob debe coincidir con el provisto",
            "ca-app-pub-7824173391983098~7953889435",
            AdConfig.APP_ID
        )

        assertEquals(
            "El ID del banner AdMob debe coincidir con el provisto",
            "ca-app-pub-7824173391983098/7244191424",
            AdConfig.PROD_BANNER_AD_UNIT_ID
        )

        assertEquals(
            "El ID del intersticial AdMob debe coincidir con el provisto",
            "ca-app-pub-7824173391983098/3443798780",
            AdConfig.PROD_INTERSTITIAL_AD_UNIT_ID
        )

        assertEquals(
            "El ID del anuncio bonificado (rewarded) AdMob debe coincidir con el provisto",
            "ca-app-pub-7824173391983098/5630978425",
            AdConfig.PROD_REWARDED_AD_UNIT_ID
        )

        assertEquals(
            "El ID del anuncio nativo AdMob debe coincidir con el provisto",
            "ca-app-pub-7824173391983098/5568322680",
            AdConfig.PROD_NATIVE_AD_UNIT_ID
        )
    }

    @Test
    fun testForceProductionAdsReturnsProductionIds() {
        AdConfig.forceProductionAds = true

        assertEquals(AdConfig.PROD_BANNER_AD_UNIT_ID, AdConfig.getBannerAdUnitId())
        assertEquals(AdConfig.PROD_INTERSTITIAL_AD_UNIT_ID, AdConfig.getInterstitialAdUnitId())
        assertEquals(AdConfig.PROD_REWARDED_AD_UNIT_ID, AdConfig.getRewardedAdUnitId())
        assertEquals(AdConfig.PROD_NATIVE_AD_UNIT_ID, AdConfig.getNativeAdUnitId())

        // Restaurar estado
        AdConfig.forceProductionAds = false
    }
}

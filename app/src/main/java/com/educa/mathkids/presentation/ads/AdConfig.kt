package com.educa.mathkids.presentation.ads

import com.educa.mathkids.BuildConfig

/**
 * Configuración de AdMob para MateKids con los IDs oficiales proporcionados por el usuario.
 */
object AdConfig {
    // ID de la aplicación en AdMob
    const val APP_ID = "ca-app-pub-7824173391983098~7953889435"

    // IDs de producción proporcionados
    const val PROD_BANNER_AD_UNIT_ID = "ca-app-pub-7824173391983098/7244191424"
    const val PROD_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-7824173391983098/3443798780"
    const val PROD_REWARDED_AD_UNIT_ID = "ca-app-pub-7824173391983098/5630978425"
    const val PROD_NATIVE_AD_UNIT_ID = "ca-app-pub-7824173391983098/5568322680"

    // IDs oficiales de prueba de Google AdMob (protección de cuenta en desarrollo)
    const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    const val TEST_NATIVE_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"

    /**
     * Si es true, fuerza el uso de los IDs de producción incluso en modo Debug.
     * En release (producción), siempre se usan los IDs reales proporcionados.
     */
    var forceProductionAds: Boolean = false

    fun getBannerAdUnitId(): String {
        return if (forceProductionAds || !BuildConfig.DEBUG) {
            PROD_BANNER_AD_UNIT_ID
        } else {
            TEST_BANNER_AD_UNIT_ID
        }
    }

    fun getInterstitialAdUnitId(): String {
        return if (forceProductionAds || !BuildConfig.DEBUG) {
            PROD_INTERSTITIAL_AD_UNIT_ID
        } else {
            TEST_INTERSTITIAL_AD_UNIT_ID
        }
    }

    fun getRewardedAdUnitId(): String {
        return if (forceProductionAds || !BuildConfig.DEBUG) {
            PROD_REWARDED_AD_UNIT_ID
        } else {
            TEST_REWARDED_AD_UNIT_ID
        }
    }

    fun getNativeAdUnitId(): String {
        return if (forceProductionAds || !BuildConfig.DEBUG) {
            PROD_NATIVE_AD_UNIT_ID
        } else {
            TEST_NATIVE_AD_UNIT_ID
        }
    }
}

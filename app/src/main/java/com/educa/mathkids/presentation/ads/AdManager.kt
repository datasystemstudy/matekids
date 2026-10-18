package com.educa.mathkids.presentation.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.common.RequestConfiguration
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAdEventCallback
import com.google.android.libraries.ads.mobile.sdk.rewarded.OnUserEarnedRewardListener
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardItem
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardedAd
import com.google.android.libraries.ads.mobile.sdk.rewarded.RewardedAdEventCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Gestor centralizado de anuncios AdMob para MateKids migrado a Google Mobile Ads Next-Gen SDK.
 * Configurado con directivas para niños (COPPA / Familias) con calificación máxima 'G'.
 */
class AdManager(private val context: Context) {

    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoading: Boolean = false
    private var isInterstitialShowing: Boolean = false

    private var rewardedAd: RewardedAd? = null
    private var isRewardedLoading: Boolean = false
    private var isRewardedShowing: Boolean = false

    private val _isRewardedLoaded = MutableStateFlow(false)
    val isRewardedLoaded: StateFlow<Boolean> = _isRewardedLoaded.asStateFlow()

    private val _isInterstitialLoaded = MutableStateFlow(false)
    val isInterstitialLoaded: StateFlow<Boolean> = _isInterstitialLoaded.asStateFlow()

    private var isInitialized: Boolean = false

    init {
        initialize(context)
    }

    /**
     * Inicializa el SDK Next-Gen de AdMob aplicando políticas COPPA para niños (G-rated).
     */
    fun initialize(appContext: Context) {
        if (isInitialized) return

        try {
            val requestConfiguration = RequestConfiguration.Builder()
                .setAgeRestrictedTreatment(com.google.android.libraries.ads.mobile.sdk.common.AgeRestrictedTreatment.CHILD)
                .setMaxAdContentRating(RequestConfiguration.MaxAdContentRating.MAX_AD_CONTENT_RATING_G)
                .build()

            val initConfig = InitializationConfig.Builder(AdConfig.APP_ID)
                .setRequestConfiguration(requestConfiguration)
                .build()

            MobileAds.initialize(appContext, initConfig) { status ->
                Log.d(TAG, "GMA Next-Gen SDK inicializado exitosamente: $status")
                isInitialized = true
                loadInterstitial(appContext)
                loadRewarded(appContext)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error inicializando GMA Next-Gen SDK", e)
        }
    }

    /**
     * Precarga un anuncio Intersticial con Next-Gen SDK.
     */
    fun loadInterstitial(context: Context = this.context) {
        if (interstitialAd != null || isInterstitialLoading) return
        isInterstitialLoading = true

        val adUnitId = AdConfig.getInterstitialAdUnitId()
        val adRequest = AdRequest.Builder(adUnitId).build()

        InterstitialAd.load(
            adRequest,
            object : AdLoadCallback<InterstitialAd> {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isInterstitialLoading = false
                    _isInterstitialLoaded.value = true
                    Log.d(TAG, "Intersticial Next-Gen cargado correctamente ($adUnitId)")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    interstitialAd = null
                    isInterstitialLoading = false
                    _isInterstitialLoaded.value = false
                    Log.w(TAG, "Fallo al cargar intersticial Next-Gen: ${loadAdError.message}")
                }
            }
        )
    }

    /**
     * Ejecuta una acción en el hilo principal (Main Looper) asegurándose de que la Activity
     * esté en estado RESUMED para evitar transiciones de navegación descartadas o caídas
     * cuando se cierran anuncios a pantalla completa (Intersticiales o Recompensados).
     */
    private fun runOnMainThreadWhenResumed(activity: Activity, action: () -> Unit) {
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post {
            if (activity.isFinishing || activity.isDestroyed) {
                Log.w(TAG, "Activity en proceso de finalización o destruida; omitiendo ejecución de acción de anuncio.")
                return@post
            }

            if (activity is LifecycleOwner) {
                val lifecycle = activity.lifecycle
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                    action()
                } else {
                    var executed = false
                    val timeoutRunnable = Runnable {
                        if (!executed) {
                            executed = true
                            Log.w(TAG, "Timeout esperando ON_RESUME de la Activity; ejecutando acción de respaldo.")
                            if (!activity.isFinishing && !activity.isDestroyed) {
                                action()
                            }
                        }
                    }

                    val observer = object : DefaultLifecycleObserver {
                        override fun onResume(owner: LifecycleOwner) {
                            if (!executed) {
                                executed = true
                                mainHandler.removeCallbacks(timeoutRunnable)
                                owner.lifecycle.removeObserver(this)
                                mainHandler.post {
                                    if (!activity.isFinishing && !activity.isDestroyed) {
                                        action()
                                    }
                                }
                            }
                        }

                        override fun onDestroy(owner: LifecycleOwner) {
                            owner.lifecycle.removeObserver(this)
                            mainHandler.removeCallbacks(timeoutRunnable)
                        }
                    }

                    lifecycle.addObserver(observer)
                    // Timeout preventivo de seguridad de 2.5s por si el evento onResume se retrasa
                    mainHandler.postDelayed(timeoutRunnable, 2500L)
                }
            } else {
                action()
            }
        }
    }

    /**
     * Muestra el anuncio Intersticial si está listo y luego ejecuta [onAdDismissed].
     * Si no está cargado o falla, ejecuta [onAdDismissed] inmediatamente y de forma segura.
     */
    fun showInterstitial(
        activity: Activity,
        onAdDismissed: () -> Unit = {}
    ) {
        if (isInterstitialShowing) {
            Log.w(TAG, "Un anuncio intersticial ya se está mostrando actualmente.")
            return
        }

        val currentAd = interstitialAd
        if (currentAd != null) {
            isInterstitialShowing = true
            currentAd.adEventCallback = object : InterstitialAdEventCallback {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Intersticial cerrado por el usuario.")
                    isInterstitialShowing = false
                    interstitialAd = null
                    _isInterstitialLoaded.value = false
                    loadInterstitial(activity.applicationContext)
                    runOnMainThreadWhenResumed(activity, onAdDismissed)
                }

                override fun onAdFailedToShowFullScreenContent(error: FullScreenContentError) {
                    Log.w(TAG, "Fallo al mostrar intersticial: ${error.message}")
                    isInterstitialShowing = false
                    interstitialAd = null
                    _isInterstitialLoaded.value = false
                    loadInterstitial(activity.applicationContext)
                    runOnMainThreadWhenResumed(activity, onAdDismissed)
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Intersticial mostrado en pantalla completa.")
                    interstitialAd = null
                    _isInterstitialLoaded.value = false
                }
            }

            try {
                currentAd.show(activity)
            } catch (e: Throwable) {
                Log.e(TAG, "Excepción al invocar show() en intersticial", e)
                isInterstitialShowing = false
                interstitialAd = null
                _isInterstitialLoaded.value = false
                loadInterstitial(activity.applicationContext)
                runOnMainThreadWhenResumed(activity, onAdDismissed)
            }
        } else {
            Log.d(TAG, "Intersticial no cargado en caché; continuando flujo inmediatamente.")
            loadInterstitial(activity.applicationContext)
            runOnMainThreadWhenResumed(activity, onAdDismissed)
        }
    }

    /**
     * Precarga un anuncio Bonificado (Rewarded) con Next-Gen SDK.
     */
    fun loadRewarded(context: Context = this.context) {
        if (rewardedAd != null || isRewardedLoading) return
        isRewardedLoading = true

        val adUnitId = AdConfig.getRewardedAdUnitId()
        val adRequest = AdRequest.Builder(adUnitId).build()

        RewardedAd.load(
            adRequest,
            object : AdLoadCallback<RewardedAd> {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isRewardedLoading = false
                    _isRewardedLoaded.value = true
                    Log.d(TAG, "Anuncio Bonificado Next-Gen cargado correctamente ($adUnitId)")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    rewardedAd = null
                    isRewardedLoading = false
                    _isRewardedLoaded.value = false
                    Log.w(TAG, "Fallo al cargar anuncio bonificado Next-Gen: ${loadAdError.message}")
                }
            }
        )
    }

    /**
     * Muestra el anuncio Bonificado (Rewarded) con Next-Gen SDK.
     * [onRewardEarned] se invoca con la cantidad ganada cuando el usuario completa la visualización.
     * [onAdDismissed] se invoca cuando el anuncio se cierra.
     */
    fun showRewarded(
        activity: Activity,
        onRewardEarned: (amount: Int) -> Unit,
        onAdDismissed: () -> Unit = {}
    ) {
        if (isRewardedShowing) {
            Log.w(TAG, "Un anuncio bonificado ya se está mostrando actualmente.")
            return
        }

        val currentAd = rewardedAd
        if (currentAd != null) {
            isRewardedShowing = true
            var rewardGiven = false

            currentAd.adEventCallback = object : RewardedAdEventCallback {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Anuncio bonificado cerrado por el usuario.")
                    isRewardedShowing = false
                    rewardedAd = null
                    _isRewardedLoaded.value = false
                    loadRewarded(activity.applicationContext)
                    runOnMainThreadWhenResumed(activity, onAdDismissed)
                }

                override fun onAdFailedToShowFullScreenContent(error: FullScreenContentError) {
                    Log.w(TAG, "Fallo al mostrar anuncio bonificado: ${error.message}")
                    isRewardedShowing = false
                    rewardedAd = null
                    _isRewardedLoaded.value = false
                    loadRewarded(activity.applicationContext)
                    runOnMainThreadWhenResumed(activity) {
                        if (!rewardGiven) {
                            rewardGiven = true
                            onRewardEarned(50)
                        }
                        onAdDismissed()
                    }
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Anuncio bonificado mostrado en pantalla completa.")
                    rewardedAd = null
                    _isRewardedLoaded.value = false
                }
            }

            try {
                currentAd.show(activity, object : OnUserEarnedRewardListener {
                    override fun onUserEarnedReward(rewardItem: RewardItem) {
                        val amount = if (rewardItem.amount > 0) rewardItem.amount else 50
                        Log.d(TAG, "Recompensa Next-Gen otorgada al usuario: $amount")
                        rewardGiven = true
                        runOnMainThreadWhenResumed(activity) {
                            onRewardEarned(amount)
                        }
                    }
                })
            } catch (e: Throwable) {
                Log.e(TAG, "Excepción al invocar show() en anuncio bonificado", e)
                isRewardedShowing = false
                rewardedAd = null
                _isRewardedLoaded.value = false
                loadRewarded(activity.applicationContext)
                runOnMainThreadWhenResumed(activity) {
                    if (!rewardGiven) {
                        rewardGiven = true
                        onRewardEarned(50)
                    }
                    onAdDismissed()
                }
            }
        } else {
            loadRewarded(activity.applicationContext)
            Log.d(TAG, "Anuncio no disponible en caché, otorgando recompensa por fallback")
            runOnMainThreadWhenResumed(activity) {
                onRewardEarned(50)
                onAdDismissed()
            }
        }
    }

    companion object {
        private const val TAG = "MateKidsAdManager"

        @Volatile
        private var INSTANCE: AdManager? = null

        fun getInstance(context: Context): AdManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AdManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}

package com.educa.mathkids.presentation.screens.splash

import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.view.Surface
import android.view.TextureView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.educa.mathkids.R
import com.educa.mathkids.presentation.utils.SoundManager

/**
 * Pantalla de Video Introductorio de MATEKIDS.
 * Reproduce el video animado oficial de forma limpia a pantalla completa,
 * sin controles visuales superpuestos (sin bocina, sin botón de saltar y sin barra de progreso).
 * Al finalizar la reproducción del video, transiciona automáticamente a la pantalla de bienvenida.
 */
@Composable
fun IntroVideoScreen(
    onVideoFinished: () -> Unit,
    soundManager: SoundManager,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasFinished by remember { mutableStateOf(false) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    fun finishVideo() {
        if (!hasFinished) {
            hasFinished = true
            try {
                mediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        player.stop()
                    }
                    player.release()
                }
            } catch (_: Exception) {
            }
            mediaPlayer = null
            onVideoFinished()
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    try {
                        mediaPlayer?.let { player ->
                            if (player.isPlaying) {
                                player.pause()
                            }
                        }
                    } catch (_: Exception) {
                    }
                }
                Lifecycle.Event.ON_RESUME -> {
                    try {
                        mediaPlayer?.let { player ->
                            if (!hasFinished) {
                                player.start()
                            }
                        }
                    } catch (_: Exception) {
                    }
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            try {
                mediaPlayer?.let { player ->
                    if (player.isPlaying) {
                        player.stop()
                    }
                    player.release()
                }
            } catch (_: Exception) {
            }
            mediaPlayer = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                // Toque opcional en pantalla para continuar si el usuario lo desea
                finishVideo()
            },
        contentAlignment = Alignment.Center
    ) {
        // Reproductor de video limpio acelerado por hardware a pantalla completa
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                val textureView = TextureView(ctx)
                textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(
                        surfaceTexture: SurfaceTexture,
                        width: Int,
                        height: Int
                    ) {
                        try {
                            val surface = Surface(surfaceTexture)
                            val afd = ctx.resources.openRawResourceFd(R.raw.video_matekids)
                            val player = MediaPlayer().apply {
                                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                                afd.close()
                                setSurface(surface)
                                setAudioAttributes(
                                    AudioAttributes.Builder()
                                        .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                                        .setUsage(AudioAttributes.USAGE_MEDIA)
                                        .build()
                                )
                                val volume = if (soundManager.isSoundEnabled) 1f else 0f
                                setVolume(volume, volume)

                                setOnVideoSizeChangedListener { mp, _, _ ->
                                    adjustAspectRatio(textureView, mp.videoWidth, mp.videoHeight)
                                }

                                setOnPreparedListener { mp ->
                                    adjustAspectRatio(textureView, mp.videoWidth, mp.videoHeight)
                                    mp.isLooping = false
                                    mp.start()
                                }

                                setOnCompletionListener {
                                    finishVideo()
                                }

                                setOnErrorListener { _, _, _ ->
                                    finishVideo()
                                    true
                                }

                                prepareAsync()
                            }
                            mediaPlayer = player
                        } catch (e: Exception) {
                            e.printStackTrace()
                            finishVideo()
                        }
                    }

                    override fun onSurfaceTextureSizeChanged(
                        surfaceTexture: SurfaceTexture,
                        width: Int,
                        height: Int
                    ) {
                        mediaPlayer?.let { player ->
                            adjustAspectRatio(textureView, player.videoWidth, player.videoHeight)
                        }
                    }

                    override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
                        try {
                            mediaPlayer?.stop()
                            mediaPlayer?.release()
                        } catch (_: Exception) {
                        }
                        mediaPlayer = null
                        surfaceTexture.release()
                        return true
                    }

                    override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}
                }
                textureView
            }
        )
    }
}

/**
 * Ajusta la matriz de transformación del TextureView para conservar la relación de aspecto
 * del video sin distorsión ni deformación en cualquier resolución o pantalla (9:16, 9:20, tablets, etc.).
 */
private fun adjustAspectRatio(
    textureView: TextureView,
    videoWidth: Int,
    videoHeight: Int
) {
    val viewWidth = textureView.width
    val viewHeight = textureView.height
    if (viewWidth == 0 || viewHeight == 0 || videoWidth == 0 || videoHeight == 0) return

    val viewAspect = viewWidth.toDouble() / viewHeight.toDouble()
    val videoAspect = videoWidth.toDouble() / videoHeight.toDouble()

    var scaleX = 1f
    var scaleY = 1f

    if (viewAspect > videoAspect) {
        scaleX = ((viewHeight * videoAspect) / viewWidth).toFloat()
    } else {
        scaleY = ((viewWidth / videoAspect) / viewHeight).toFloat()
    }

    val pivotX = viewWidth / 2f
    val pivotY = viewHeight / 2f

    val matrix = Matrix().apply {
        setScale(scaleX, scaleY, pivotX, pivotY)
    }
    textureView.setTransform(matrix)
}

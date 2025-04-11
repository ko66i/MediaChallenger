package com.example.mediachallenger


import android.widget.Button
import com.airbnb.lottie.LottieAnimationView

class AudioSettingsManager(
    private val playAudio: () -> Unit,
    private val stopAudio: () -> Unit,
    private val pauseAudio: () -> Unit,

    ) {


    fun setupClickListeners(
        btnPlay: Button,
        btnStop: Button,
        btnPause: Button,
        animationView: LottieAnimationView

    ) {

        btnPlay.setOnClickListener {
            playAudio()
            if (!animationView.isAnimating) {
                animationView.resumeAnimation()
            } else {
                animationView.playAnimation()
            }
        }

        btnStop.setOnClickListener {
            stopAudio()
            if (animationView.isAnimating) {
                animationView.pauseAnimation()
            }
        }

        btnPause.setOnClickListener {
            pauseAudio()
            if (animationView.isAnimating) {
                animationView.pauseAnimation()
            }
        }

    }

}

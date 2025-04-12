package com.example.mediachallenger

import android.widget.Button
import com.airbnb.lottie.LottieAnimationView

/**
 * Gerencia as configurações de áudio e botões de controle.
 *
 * @param playAudio Função lambda para iniciar a reprodução de áudio.
 * @param stopAudio Função lambda para parar a reprodução de áudio.
 * @param pauseAudio Função lambda para pausar a reprodução de áudio.
 */
class AudioSettingsManager(
    private val playAudio: () -> Unit,
    private val stopAudio: () -> Unit,
    private val pauseAudio: () -> Unit,
) {

    /**
     * Configura os listeners dos botões de controle de áudio e gerencia a animação correspondente.
     *
     * @param btnPlay Botão para iniciar a reprodução de áudio.
     * @param btnStop Botão para parar a reprodução de áudio.
     * @param btnPause Botão para pausar a reprodução de áudio.
     * @param animationView Componente de animação Lottie vinculado à reprodução de áudio.
     */
    fun setupClickListeners(
        btnPlay: Button,
        btnStop: Button,
        btnPause: Button,
        animationView: LottieAnimationView
    ) {

        // Define o comportamento ao clicar no botão de reprodução
        btnPlay.setOnClickListener {
            playAudio() // Chama a função de reprodução de áudio

            // Se a animação não estiver rodando, retoma a animação
            if (!animationView.isAnimating) {
                animationView.resumeAnimation()
            } else {
                animationView.playAnimation() // Caso contrário, inicia a animação
            }
        }

        // Define o comportamento ao clicar no botão de parada
        btnStop.setOnClickListener {
            stopAudio() // Chama a função para parar o áudio

            // Se a animação estiver rodando, pausa a animação
            if (animationView.isAnimating) {
                animationView.pauseAnimation()
            }
        }

        // Define o comportamento ao clicar no botão de pausa
        btnPause.setOnClickListener {
            pauseAudio() // Chama a função para pausar o áudio

            // Se a animação estiver rodando, pausa a animação
            if (animationView.isAnimating) {
                animationView.pauseAnimation()
            }
        }
    }
}
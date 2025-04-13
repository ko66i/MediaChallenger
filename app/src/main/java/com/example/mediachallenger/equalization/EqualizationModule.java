package com.example.mediachallenger.equalization;

import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.util.Log;

/**
 * Módulo responsável pelo controle do equalizador.
 * Implementa a interface EqualizationInterface para fornecer os métodos de controle.
 */
public class EqualizationModule implements EqualizationInterface {

    private static final String TAG = "EqualizationModule"; // Tag para logs
    private Equalizer equalizer; // Objeto Equalizer para controlar as frequências

    /**
     * Construtor da classe.
     * @param mediaPlayer O objeto MediaPlayer associado ao equalizador.
     */
    public EqualizationModule(MediaPlayer mediaPlayer) {
        initializeEqualizer(mediaPlayer); // Inicializa o equalizador
    }

    /**
     * Inicializa o equalizador com base no MediaPlayer fornecido.
     * @param mediaPlayer O objeto MediaPlayer associado ao equalizador.
     */
    private void initializeEqualizer(MediaPlayer mediaPlayer) {
        try {
            if (mediaPlayer != null) {
                int audioSessionId = mediaPlayer.getAudioSessionId(); // Obtém o ID da sessão de áudio
                equalizer = new Equalizer(0, audioSessionId); // Cria o equalizador
                equalizer.setEnabled(true); // Habilita o equalizador
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Equalizer: " + e.getMessage()); // Log de erro
        }
    }

    /**
     * Define o nível de uma banda de frequência do equalizador.
     * @param band O índice da banda a ser definida.
     * @param level O nível da banda.
     */
    @Override
    public void setBandLevel(short band, short level) {
        try {
            if (equalizer != null) {
                equalizer.setBandLevel(band, level); // Define o nível da banda
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting band level: " + e.getMessage()); // Log de erro
        }
    }

    /**
     * Obtém o nível de uma banda de frequência do equalizador.
     * @param band O índice da banda a ser obtida.
     * @return O nível da banda.
     */
    @Override
    public short getBandLevel(short band) {
        if (equalizer != null) {
            return equalizer.getBandLevel(band); // Obtém o nível da banda
        }
        return 0; // Retorna 0 se o equalizador for nulo
    }

    /**
     * Libera os recursos do equalizador.
     */
    @Override
    public void release() {
        if (equalizer != null) {
            equalizer.release(); // Libera os recursos
            equalizer = null; // Define como nulo para evitar uso posterior
        }
    }
}

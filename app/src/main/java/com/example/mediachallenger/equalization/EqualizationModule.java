package com.example.mediachallenger.equalization;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
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

    private int sampleRate = 44100; // Example sample rate


    // Carregando a native library
    static {
        System.loadLibrary("equalizer");
    }


    /**
     * Native method to apply equalization
     * @param audioData The audio data
     * @param gains The gains for each frequency band
     * @return The number of samples processed
     */
    private native int applyEqualizationNative(short[] audioData, int[] gains);

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

    /**
     * Applies equalization to the audio data using the native JNI method.
     * @param audioData The audio data to equalize.
     * @param gains The gains to apply to each frequency band.
     */
    public void applyEqualization(short[] audioData, int[] gains) {
        if (audioData == null || gains == null) {
            Log.e(TAG, "Audio data or gains are null.");
            return;
        }

        if (audioData.length == 0 || gains.length == 0) {
            Log.w(TAG, "Audio data or gains array is empty.");
            return;
        }

        // It's important to check audioEqualizer for null before calling its methods
        applyEqualizationNative(audioData, gains);
    }

    public short[] getAudioData(MediaPlayer mediaPlayer) {
        // Get the audio session ID
        int audioSessionId = mediaPlayer.getAudioSessionId();

        // Get the minimum buffer size
        int bufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);

        // Create an AudioTrack instance
        AudioTrack audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM
        );

        // Start playing the audioTrack
        audioTrack.play();

        // Create a short array to store the audio data
        short[] audioData = new short[bufferSize / 2];

        return audioData;
    }
}


package com.example.mediachallenger.playback;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Módulo responsável pela reprodução de áudio.
 * Implementa a interface PlaybackInterface para fornecer os métodos de controle de reprodução.
 */
public class PlaybackModule implements PlaybackInterface {

    private static final String TAG = "PlaybackModule"; // Tag para logs
    private MediaPlayer mediaPlayer; // Objeto MediaPlayer para reproduzir o áudio
    private Context context; // Contexto da aplicação
    private int audioResourceId; // ID do recurso de áudio

    /**
     * Construtor da classe.
     * @param context O contexto da aplicação.
     * @param audioResourceId O ID do recurso de áudio a ser reproduzido.
     */
    public PlaybackModule(Context context, int audioResourceId) {
        this.context = context;
        this.audioResourceId = audioResourceId;
        initializeMediaPlayer(); // Inicializa o MediaPlayer
    }

    /**
     * Inicializa o MediaPlayer com o recurso de áudio especificado.
     */
    private void initializeMediaPlayer() {
        try {
            mediaPlayer = MediaPlayer.create(context, audioResourceId); // Cria o MediaPlayer
            mediaPlayer.setLooping(true); // Define para repetir a música
        } catch (Exception e) {
            Log.e(TAG, "Error initializing MediaPlayer: " + e.getMessage()); // Log de erro
        }
    }

    /**
     * Obtém o objeto MediaPlayer.
     * @return O objeto MediaPlayer.
     */
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    /**
     * Inicia a reprodução do áudio.
     * @return true se a reprodução foi iniciada com sucesso, false caso contrário.
     */
    @Override
    public boolean playAudio() {
        try {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start(); // Inicia a reprodução
                return true; // Indica sucesso
            }
        } catch (IllegalStateException e) {
            Log.e(TAG, "Error playing audio: " + e.getMessage()); // Log de erro
        }
        return false; // Indica falha
    }

    /**
     * Pausa a reprodução do áudio.
     * @return true se a reprodução foi pausada com sucesso, false caso contrário.
     */
    @Override
    public boolean pauseAudio() {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause(); // Pausa a reprodução
                return true; // Indica sucesso
            }
        } catch (IllegalStateException e) {
            Log.e(TAG, "Error pausing audio: " + e.getMessage()); // Log de erro
        }
        return false; // Indica falha
    }

    /**
     * Para a reprodução do áudio e reseta a posição para o início.
     * @return true se a reprodução foi parada com sucesso, false caso contrário.
     */
    @Override
    public boolean stopAudio() {
        try {
            if (mediaPlayer != null && (mediaPlayer.isPlaying() || mediaPlayer.getCurrentPosition() > 0)) {
                mediaPlayer.stop(); // Para a reprodução
                mediaPlayer.prepare(); // Prepara o MediaPlayer para ser usado novamente
                mediaPlayer.seekTo(0); // Reseta a posição para o início
                return true; // Indica sucesso
            }
        } catch (Exception e) {
            Log.e(TAG, "Error stopping audio: " + e.getMessage()); // Log de erro
        }
        return false; // Indica falha
    }

    /**
     * Busca uma posição específica no áudio.
     * @param position A posição em milissegundos para buscar.
     * @return true se a busca foi realizada com sucesso, false caso contrário.
     */
    @Override
    public boolean seekAudio(int position) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(position); // Busca a posição
                return true; // Indica sucesso
            }
        } catch (IllegalStateException e) {
            Log.e(TAG, "Error seeking audio: " + e.getMessage()); // Log de erro
        }
        return false; // Indica falha
    }

    /**
     * Obtém a duração total do áudio.
     * @return A duração em milissegundos.
     */
    @Override
    public int getDuration() {
        try {
            if (mediaPlayer != null) {
                return mediaPlayer.getDuration(); // Obtém a duração
            }
        } catch (IllegalStateException e) {
            Log.e(TAG, "Error getting duration: " + e.getMessage()); // Log de erro
        }
        return 0; // Retorna 0 em caso de erro
    }

    /**
     * Obtém a posição atual da reprodução.
     * @return A posição atual em milissegundos.
     */
    @Override
    public int getCurrentPosition() {
        try {
            if (mediaPlayer != null) {
                return mediaPlayer.getCurrentPosition(); // Obtém a posição atual
            }
        } catch (IllegalStateException e) {
            Log.e(TAG, "Error getting current position: " + e.getMessage()); // Log de erro
        }
        return 0; // Retorna 0 em caso de erro
    }

    /**
     * Define o volume da reprodução.
     * @param volume O volume a ser definido (entre 0.0 e 1.0).
     * @return true se o volume foi definido com sucesso, false caso contrário.
     */
    @Override
    public boolean setVolume(float volume) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(volume, volume); // Define o volume para os dois canais
                return true; // Indica sucesso
            }
        } catch (IllegalStateException e) {
            Log.e(TAG, "Error setting volume: " + e.getMessage()); // Log de erro
        }
        return false; // Indica falha
    }

    /**
     * Libera os recursos do MediaPlayer.
     */
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.stop(); // Para a reprodução
            mediaPlayer.release(); // Libera os recursos
            mediaPlayer = null; // Define como nulo para evitar uso posterior
        }
    }
}

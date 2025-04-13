package com.example.mediachallenger.playback;

/**
 * Interface que define os métodos para controlar a reprodução de áudio.
 * Esta interface é implementada pela classe PlaybackModule.
 */
public interface PlaybackInterface {
    /**
     * Inicia a reprodução do áudio.
     * @return true se a reprodução foi iniciada com sucesso, false caso contrário.
     */
    boolean playAudio();

    /**
     * Pausa a reprodução do áudio.
     * @return true se a reprodução foi pausada com sucesso, false caso contrário.
     */
    boolean pauseAudio();

    /**
     * Para a reprodução do áudio e reseta a posição para o início.
     * @return true se a reprodução foi parada com sucesso, false caso contrário.
     */
    boolean stopAudio();

    /**
     * Busca uma posição específica no áudio.
     * @param position A posição em milissegundos para buscar.
     * @return true se a busca foi realizada com sucesso, false caso contrário.
     */
    boolean seekAudio(int position);

    /**
     * Obtém a duração total do áudio.
     * @return A duração em milissegundos.
     */
    int getDuration();

    /**
     * Obtém a posição atual da reprodução.
     * @return A posição atual em milissegundos.
     */
    int getCurrentPosition();

    /**
     * Define o volume da reprodução.
     * @param volume O volume a ser definido (entre 0.0 e 1.0).
     * @return true se o volume foi definido com sucesso, false caso contrário.
     */
    boolean setVolume(float volume);
}

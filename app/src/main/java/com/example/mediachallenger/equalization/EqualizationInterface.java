package com.example.mediachallenger.equalization;

/**
 * Interface que define os métodos para controlar o equalizador.
 * Esta interface é implementada pela classe EqualizationModule.
 */
public interface EqualizationInterface {
    /**
     * Define o nível de uma banda de frequência do equalizador.
     * @param band O índice da banda a ser definida.
     * @param level O nível da banda.
     */
    void setBandLevel(short band, short level);

    /**
     * Obtém o nível de uma banda de frequência do equalizador.
     * @param band O índice da banda a ser obtida.
     * @return O nível da banda.
     */
    short getBandLevel(short band);

    /**
     * Libera os recursos do equalizador.
     */
    void release();
}

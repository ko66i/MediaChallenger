package com.example.mediachallenger

import android.util.Log

/**
 * Gerencia as configurações de áudio através do serviço AIDL.
 *
 * @param aidlServiceManager O manager para a vinculação e comunicação com o serviço AIDL.
 */
class AudioManager(private val aidlServiceManager: AidlServiceManager) {

    // Função para iniciar a reprodução de áudio
    fun playAudio() {
        // Verifica se o serviço AIDL está vinculado
        if (aidlServiceManager.isServiceBound()) {
            try {
                // Tenta chamar o método remoto playAudio()
                val success = aidlServiceManager.getMessageService()?.playAudio() ?: false

                if (success) {
                    Log.d("AudioManager", "Setting play audio: Success") // Log em caso de sucesso
                } else {
                    Log.w("AudioManager", "Setting play audio: Failed (invalid value?)") // Log em caso de falha
                }
            } catch (e: android.os.RemoteException) {
                // Captura e registra exceções de comunicação remota
                Log.e("AudioManager", "RemoteException: ${e.message}")
            }
        } else {
            // Caso o serviço não esteja vinculado, registra um aviso
            Log.w("AudioManager", "Service not bound")
        }
    }

    // Função para pausar a reprodução de áudio
    fun pauseAudio() {
        if (aidlServiceManager.isServiceBound()) {
            try {
                val success = aidlServiceManager.getMessageService()?.pauseAudio() ?: false
                if (success) {
                    Log.d("AudioManager", "Setting pause: Success") // Log em caso de sucesso
                } else {
                    Log.w("AudioManager", "Setting pause: Failed (invalid value?)") // Log em caso de falha
                }
            } catch (e: android.os.RemoteException) {
                Log.e("AudioManager", "RemoteException: ${e.message}") // Erro remoto
            }
        } else {
            Log.w("AudioManager", "Service not bound") // Serviço não vinculado
        }
    }

    // Função para parar a reprodução de áudio
    fun stopAudio() {
        if (aidlServiceManager.isServiceBound()) {
            try {
                val success = aidlServiceManager.getMessageService()?.stopAudio() ?: false
                if (success) {
                    Log.d("AudioManager", "Setting stop audio: Success") // Log em caso de sucesso
                } else {
                    Log.w("AudioManager", "Setting stop audio: Failed (invalid value?)") // Log em caso de falha
                }
            } catch (e: android.os.RemoteException) {
                Log.e("AudioManager", "RemoteException: ${e.message}") // Erro remoto
            }
        } else {
            Log.w("AudioManager", "Service not bound") // Serviço não vinculado
        }
    }
}

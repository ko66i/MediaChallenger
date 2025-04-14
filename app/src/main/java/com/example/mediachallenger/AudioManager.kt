package com.example.mediachallenger

import android.util.Log

/**
 * Gerencia as configurações de áudio através do serviço AIDL.
 *
 * @param aidlServiceManager O gerenciador para a vinculação e comunicação com o serviço AIDL.
 */
class AudioManager(private val aidlServiceManager: AidlServiceManager) {

    /**
     * Função para definir o recurso de áudio
     * @param resourceName nome do recurso
     */
    fun setAudioResource(resourceName: String) {
        if (aidlServiceManager.isServiceBound()) {
            try {
                val success = aidlServiceManager.getMessageService()?.setAudioResource(resourceName) ?: false
                if (success) {
                    Log.d("AudioManager", "Setting audio resource to $resourceName: Success")
                } else {
                    Log.w("AudioManager", "Setting audio resource to $resourceName: Failed")
                }
            } catch (e: android.os.RemoteException) {
                Log.e("AudioManager", "RemoteException: ${e.message}")
            }
        } else {
            Log.w("AudioManager", "Service not bound")
        }
    }

}

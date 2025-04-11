package com.example.mediachallenger

import android.util.Log

/**
 * Gerencia as configurações de áudio através do serviço AIDL.
 *
 * @param aidlServiceManager O manager para a vinculação e comunicação com o serviço AIDL.
 */
class AudioManager(private val aidlServiceManager: AidlServiceManager) {

    fun playAudio() {
        if (aidlServiceManager.isServiceBound()) {
            try {
                val success = aidlServiceManager.getMessageService()?.playAudio() ?: false
                if (success) {
                    Log.d("AudioManager", "Setting play audio: Success")
                } else {
                    Log.w("AudioManager", "Setting play audio: Failed (invalid value?)")
                }
            } catch (e: android.os.RemoteException) {
                Log.e("AudioManager", "RemoteException: ${e.message}")
            }
        } else {
            Log.w("AudioManager", "Service not bound")
        }
    }

    fun pauseAudio() {
        if (aidlServiceManager.isServiceBound()) {
            try {
                val success = aidlServiceManager.getMessageService()?.pauseAudio() ?: false
                if (success) {
                    Log.d("AudioManager", "Setting pause: Success")
                } else {
                    Log.w("AudioManager", "Setting pause: Failed (invalid value?)")
                }
            } catch (e: android.os.RemoteException) {
                Log.e("AudioManager", "RemoteException: ${e.message}")
            }
        } else {
            Log.w("AudioManager", "Service not bound")
        }
    }

    fun stopAudio() {
        if (aidlServiceManager.isServiceBound()) {
            try {
                val success = aidlServiceManager.getMessageService()?.stopAudio() ?: false
                if (success) {
                    Log.d("AudioManager", "Setting stop audio: Success")
                } else {
                    Log.w("AudioManager", "Setting stop audio: Failed (invalid value?)")
                }
            } catch (e: android.os.RemoteException) {
                Log.e("AudioManager", "RemoteException: ${e.message}")
            }
        } else {
            Log.w("AudioManager", "Service not bound")
        }
    }
}

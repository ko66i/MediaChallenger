package com.example.mediachallenger

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.emixerapp.manager.PermissionManager
import com.example.mediachallenger.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val AUDIO_PERMISSION_REQUEST = 100 // Código para a requisição de permissão de áudio
    private lateinit var permissionManager: PermissionManager
    private lateinit var aidlServiceManager: AidlServiceManager
    private lateinit var audioManager: AudioManager
    private lateinit var audioSettingsManager: AudioSettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        aidlServiceManager = AidlServiceManager(this)
        permissionManager = PermissionManager(this, this)

        // Verifica e solicita as permissões de áudio
        checkAudioPermissions()

        // Vincula ao serviço AIDL e configura as definições de áudio
        bindAidlService()

    }

    /**
     * Vincula ao serviço AIDL e configura as definições de áudio
     */
    private fun bindAidlService() {
        aidlServiceManager.bindService(
            onServiceConnected = { service ->
                // Inicializa o AudioManager
                audioManager = AudioManager(aidlServiceManager)
                // Inicializa o AudioSettingsManager quando o serviço é conectado
                audioSettingsManager = AudioSettingsManager(
                    playAudio = {  -> audioManager.playAudio() }, // Callback para tocar audio
                    pauseAudio = {  -> audioManager.stopAudio() }, // Callback para parar audio
                    stopAudio = {  -> audioManager.pauseAudio() }  // Callback para pausar audio
                )

                // Configura os listeners para as SeekBars
                audioSettingsManager.setupClickListeners(
                    binding.btnPlay,
                    binding.btnPause,
                    binding.btnStop,
                    binding.animationView,
                )

            },
            onServiceDisconnected = {
                Log.w("UserPage", "AIDL Service disconnected") // Log de desconexão
            }
        )
    }

    /**
     * Verifica e solicita as permissões de áudio necessárias.
     */
    private fun checkAudioPermissions() {
        permissionManager.checkAudioPermissions(AUDIO_PERMISSION_REQUEST)
    }

}
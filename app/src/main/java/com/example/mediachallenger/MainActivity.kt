package com.example.mediachallenger

import android.os.*
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.emixerapp.manager.PermissionManager
import com.example.mediachallenger.databinding.ActivityMainBinding
import android.os.RemoteException

/**
 * Activity principal da aplicação, responsável por controlar a interface do usuário
 * e interagir com o serviço de áudio através de AIDL.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding // Objeto para acessar os elementos da interface definidos no layout
    private val AUDIO_PERMISSION_REQUEST = 100 // Código para a requisição de permissão de áudio
    private lateinit var permissionManager: PermissionManager // Objeto para gerenciar as permissões do aplicativo
    private lateinit var aidlServiceManager: AidlServiceManager // Objeto para gerenciar a conexão com o serviço AIDL
    private lateinit var audioManager: AudioManager // Objeto para interagir com as funcionalidades de áudio do serviço
    private lateinit var audioSettingsManager: AudioSettingsManager // Objeto para gerenciar as configurações dos botões de controle de áudio

    /**
     * Handler e Runnable para atualizar a SeekBar de progresso da música.
     * Utiliza um Handler para postar a atualização na thread principal, garantindo
     * que a UI seja atualizada de forma segura.
     */
    private val handler = Handler(Looper.getMainLooper())
    private val updateSeekBar: Runnable = object : Runnable {
        override fun run() {
            // Verifica se o serviço AIDL está vinculado
            if (aidlServiceManager.isServiceBound()) {
                try {
                    // Obtém a posição atual da música do serviço
                    val currentPosition = audioManager.getCurrentPosition()
                    // Atualiza a SeekBar com a posição atual
                    binding.seekBar.progress = currentPosition
                } catch (e: RemoteException) {
                    // Loga erros de comunicação remota
                    Log.e("MainActivity", "RemoteException: ${e.message}")
                }
            }
            // Posta esta Runnable novamente após 1 segundo para atualizar a SeekBar continuamente
            handler.postDelayed(this, 1000) // Update every second
        }
    }

    /**
     * Método chamado quando a Activity é criada.
     * Inicializa os objetos, define o layout e configura os listeners.
     *
     * @param savedInstanceState Objeto que contém o estado previamente salvo da Activity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) // Infla o layout da Activity
        setContentView(binding.root) // Define o layout inflado como o layout da Activity

        aidlServiceManager = AidlServiceManager(this) // Inicializa o gerenciador de serviço AIDL
        permissionManager = PermissionManager(this, this) // Inicializa o gerenciador de permissões

        // Encontra as SeekBars no layout
        val seekBar = binding.seekBar
        val volumeSeekBar = binding.volumeSeekBar

        /**
         * Listener para a SeekBar de progresso da música.
         * Notifica o serviço quando o usuário interage com a SeekBar.
         */
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Verifica se a alteração na SeekBar foi feita pelo usuário
                if (fromUser) {
                    audioManager.seekAudio(progress) // Chama o método seekAudio no AudioManager para notificar o serviço
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Opcional: Implementar se precisar fazer algo quando o usuário começa a tocar na SeekBar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Opcional: Implementar se precisar fazer algo quando o usuário para de tocar na SeekBar
            }
        })

        /**
         * Listener para a SeekBar de volume.
         * Notifica o serviço quando o usuário interage com a SeekBar.
         */
        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Verifica se a alteração na SeekBar foi feita pelo usuário
                if (fromUser) {
                    // Calcula o volume com base no progresso da SeekBar (0.0 a 1.0)
                    val volume = progress / 100f
                    audioManager.setVolume(volume) // Chama o método setVolume no AudioManager para notificar o serviço
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Opcional: Implementar se precisar fazer algo quando o usuário começa a tocar na SeekBar
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Opcional: Implementar se precisar fazer algo quando o usuário para de tocar na SeekBar
            }
        })

        // Verifica e solicita as permissões de áudio necessárias
        checkAudioPermissions()

        // Vincula ao serviço AIDL e configura as definições de áudio
        bindAidlService()

    }

    /**
     * Vincula ao serviço AIDL e configura as definições de áudio.
     * Define os callbacks para quando o serviço é conectado e desconectado.
     */
    private fun bindAidlService() {
        aidlServiceManager.bindService(
            onServiceConnected = { service ->
                // Callback chamado quando o serviço é conectado

                // Inicializa o AudioManager
                audioManager = AudioManager(aidlServiceManager)

                // Obtém a duração da música do serviço
                val duration = audioManager.getDuration()
                binding.seekBar.max = duration // Define o valor máximo da SeekBar com a duração da música

                // Define um valor de volume inicial para a SeekBar e para o serviço
                val initialVolume = 50 // Ajustar conforme necessário
                binding.volumeSeekBar.progress = initialVolume
                audioManager.setVolume(initialVolume / 100f) // Define o volume no serviço

                // Inicializa o AudioSettingsManager quando o serviço é conectado
                audioSettingsManager = AudioSettingsManager(
                    playAudio = {  -> audioManager.playAudio() }, // Callback para tocar audio
                    pauseAudio = {  ->
                        audioManager.stopAudio()  // Callback para pausar audio
                        binding.seekBar.progress = 0 // Reset the SeekBar to the beginning
                         },
                    stopAudio = {  ->
                        audioManager.pauseAudio() }, // Callback para parar audio

                )

                // Configura os listeners para os botões de controle de áudio (play, pause, stop)
                audioSettingsManager.setupClickListeners(
                    binding.btnPlay,
                    binding.btnPause,
                    binding.btnStop,
                    binding.animationView,
                )

                // Inicia a atualização da SeekBar de progresso da música
                handler.post(updateSeekBar)
            },
            onServiceDisconnected = {
                // Callback chamado quando o serviço é desconectado
                Log.w("UserPage", "AIDL Service disconnected") // Log de desconexão
                handler.removeCallbacks(updateSeekBar) // Remove o callback para parar de atualizar a SeekBar
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

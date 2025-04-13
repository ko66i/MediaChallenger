package com.example.mediachallenger;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import androidx.lifecycle.LiveData;

import com.example.mediachallenger.equalization.EqualizationModule;
import com.example.mediachallenger.notification.NotificationModule;
import com.example.mediachallenger.playback.PlaybackInterface;
import com.example.mediachallenger.playback.PlaybackModule;

/**
 * Serviço para gerenciar a reprodução de áudio e a equalização.
 */
public class AudioService extends Service implements PlaybackInterface {

    private static final String TAG = "AudioService"; // Tag para logs
    private static final String CHANNEL_ID = "emixer_channel"; // ID do canal de notificação
    private static final int NOTIFICATION_ID = 1; // ID único para a notificação

    public static final String ACTION_FOREGROUND_SERVICE = "com.example.mediachallenger.action.FOREGROUND_SERVICE"; // Define a ação para o serviço de primeiro plano

    private PlaybackModule playbackModule; // Objeto PlaybackModule para controlar a reprodução
    private EqualizationModule equalizationModule; // Objeto EqualizationModule para controlar o equalizador
    private NotificationModule notificationModule; // Objeto NotificationModule para gerenciar as notificações

    private MediaPlayer mediaPlayer; // MediaPlayer para reproduzir áudio
    private Equalizer equalizer; // Equalizador para ajustar o áudio


    // Implementação do Binder para a interface IMessageService
    private final IMessageService.Stub binder = new IMessageService.Stub() {

        @Override
        public boolean playAudio() throws RemoteException {
            Log.e("MSGRECEIVED", "Play audio"); // Log para depuração
            try {
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    // Inicializa o MediaPlayer
                    mediaPlayer.setLooping(true); // Define para repetir a música
                    mediaPlayer.start(); // Inicia a reprodução
                    return true; // Indica sucesso
                }
            } catch (IllegalStateException e) {
                Log.e("AudioError", "Erro ao iniciar o áudio: " + e.getMessage()); // Log de erro
            } catch (Exception e) {
                Log.e("AudioError", "Erro inesperado: " + e.getMessage()); // Log de erro
            }
            return false; // Indica falha
        }

        @Override
        public boolean pauseAudio() throws RemoteException {
            Log.d("MSGRECEIVED", "Pause audio"); // Log para depuração
            try {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.pause(); // Pausa a reprodução
                    return true; // Indica sucesso
                }
            } catch (IllegalStateException e) {
                Log.e("AudioError", "Erro ao pausar o áudio: " + e.getMessage()); // Log de erro
            } catch (Exception e) {
                Log.e("AudioError", "Erro inesperado: " + e.getMessage()); // Log de erro
            }
            return false; // Indica falha
        }

        @Override
        public boolean stopAudio() throws RemoteException {
            Log.d("MSGRECEIVED", "Stop audio"); // Log para depuração
            try {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    mediaPlayer.stop(); // Para a reprodução
                    mediaPlayer.prepare(); // Resets the MediaPlayer to a prepared state
                    mediaPlayer.seekTo(0); // volta pro inicio da faixa
                    return true; // Indica sucesso
                }
            } catch (IllegalStateException e) {
                Log.e("AudioError", "Erro ao parar o áudio: " + e.getMessage()); // Log de erro
            } catch (Exception e) {
                Log.e("AudioError", "Erro inesperado: " + e.getMessage()); // Log de erro
            }
            return false; // Indica falha
        }

        @Override
        public boolean seekAudio(int position) throws RemoteException {
            Log.d("MSGRECEIVED", "Seek audio to position: " + position); // Log para depuração
            try {
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(position); // Busca a posição
                    return true; // Indica sucesso
                } else {
                    Log.w("MessageService", "MediaPlayer is null, cannot seek"); // Log de aviso
                    return false; // Indica falha
                }
            } catch (IllegalStateException e) {
                Log.e("AudioError", "Erro ao buscar o áudio: " + e.getMessage()); // Log de erro
                return false; // Indica falha
            }
        }

        @Override
        public int getDuration() throws RemoteException {
            try {
                if (mediaPlayer != null) {
                    return mediaPlayer.getDuration(); // Obtém a duração
                } else {
                    Log.w("AudioService", "MediaPlayer is null, cannot get duration"); // Log de aviso
                    return 0; // Retorna 0 se o MediaPlayer for nulo
                }
            } catch (IllegalStateException e) {
                Log.e("AudioError", "Erro ao obter a duração: " + e.getMessage()); // Log de erro
                return 0; // Retorna 0 em caso de erro
            }
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            try {
                if (mediaPlayer != null) {
                    return mediaPlayer.getCurrentPosition(); // Obtém a posição atual
                } else {
                    Log.w("AudioService", "MediaPlayer is null, cannot get current position"); // Log de aviso
                    return 0; // Retorna 0 se o MediaPlayer for nulo
                }
            } catch (IllegalStateException e) {
                Log.e("AudioError", "Erro ao obter a posição atual: " + e.getMessage()); // Log de erro
                return 0; // Retorna 0 em caso de erro
            }
        }

        @Override
        public boolean setVolume(float volume) throws RemoteException {
            try {
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(volume, volume); // Define o volume para os canais esquerdo e direito
                    return true; // Indica sucesso
                } else {
                    Log.w("MessageService", "MediaPlayer is null, cannot set volume"); // Log de aviso
                    return false; // Indica falha
                }
            } catch (IllegalStateException e) {
                Log.e("AudioError", "Erro ao definir o volume: " + e.getMessage()); // Log de erro
                return false; // Indica falha
            }
        }

        /**
         * Método para definir qual recurso de áudio será reproduzido.
         *
         * @param resourceName Nome do recurso de áudio a ser reproduzido.
         * @return true se o recurso foi definido com sucesso, false caso contrário.
         * @throws RemoteException Exceção lançada em caso de erro de comunicação remota.
         */
        @Override
        public boolean setAudioResource(String resourceName) throws RemoteException {
            Log.d("MSGRECEIVED", "Set audio resource: " + resourceName); // Log for debugging
            try {
                int audioResId;
                if ("Música da lhama".equals(resourceName)) {
                    audioResId = R.raw.test_audio; // Replace with the correct resource ID
                } else if ("Música do esqueleto".equals(resourceName)) {
                    audioResId = R.raw.skull_music; // Replace with the correct resource ID
                } else {
                    audioResId = R.raw.test_audio; // Default resource
                }

                // Stop and release the previous MediaPlayer
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    mediaPlayer.release();
                    mediaPlayer = null;
                }

                // Create a new MediaPlayer with the selected resource
                mediaPlayer = MediaPlayer.create(AudioService.this, audioResId);

                // Prepare the MediaPlayer
                if (mediaPlayer != null) {
                    mediaPlayer.setLooping(true); // Set looping
                }

                return true; // Indicate success

            } catch (Exception e) {
                Log.e("AudioError", "Error setting audio resource: " + e.getMessage()); // Log error
                return false; // Indicate failure
            }
        }

    };

    /**
     * Método chamado quando o serviço é criado.
     * Inicializa o MediaPlayer e o Equalizer.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() chamado"); // Log para depuração

        // Obtenha a música selecionada do singleton
        LiveData<String> selectedMusicLiveData = SelectedMusicSingleton.INSTANCE.getSelectedMusic();
        String selectedMusic = selectedMusicLiveData.getValue();

        // Determine qual recurso de áudio usar com base na música selecionada
        int audioResId;
        if ("Música da lhama".equals(selectedMusic)) {
            audioResId = R.raw.test_audio; // Substitua pelo ID de recurso correto
        } else if ("Música do esqueleto".equals(selectedMusic)) {
            audioResId = R.raw.skull_music; // Substitua pelo ID de recurso correto
        } else {
            audioResId = R.raw.test_audio; // Recurso padrão
        }

        mediaPlayer = MediaPlayer.create(this, audioResId); // Audio para teste

        // Inicializa o Equalizer
        if (mediaPlayer != null) {
            int audioSessionId = mediaPlayer.getAudioSessionId();
            equalizer = new Equalizer(0, audioSessionId);
            equalizer.setEnabled(true);
        }


        // Inicializa os módulos.
        playbackModule = new PlaybackModule(this, R.raw.test_audio); // Inicializa o PlaybackModule
        equalizationModule = new EqualizationModule(playbackModule.getMediaPlayer()); // Inicializa o EqualizationModule
        notificationModule = new NotificationModule(this, CHANNEL_ID, NOTIFICATION_ID); // Inicializa o NotificationModule
        notificationModule.createNotificationChannel(); // Cria o canal de notificação

    }

    /**
     * Método chamado quando um cliente se conecta ao serviço.
     * @param intent Intent utilizada para conectar ao serviço.
     * @return O Binder para comunicação com o serviço.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() chamado, intent: " + intent); // Log para depuração
        return binder; // Retorna o Binder
    }

    /**
     * Método chamado quando o serviço é iniciado.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = notificationModule.createNotification("Emixer App", "Ajustando áudio em segundo plano"); // Cria a notificação
        startForeground(NOTIFICATION_ID, notification); // Inicia o serviço em primeiro plano
        return START_STICKY; // Retorna START_STICKY
    }

    /**
     * Método chamado quando o serviço é destruído.
     * Libera os recursos dos módulos.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() chamado"); // Log para depuração

        playbackModule.release(); // Libera os recursos do PlaybackModule
        equalizationModule.release(); // Libera os recursos do EqualizationModule

        // Libera os recursos do MediaPlayer e Equalizer
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (equalizer != null) {
            equalizer.release();
            equalizer = null;
        }
    }

    // Implementação dos métodos da interface PlaybackInterface (delegação para PlaybackModule)
    @Override
    public boolean playAudio() {
        return playbackModule.playAudio(); // Delega para o PlaybackModule
    }

    @Override
    public boolean pauseAudio() {
        return playbackModule.pauseAudio(); // Delega para o PlaybackModule
    }

    @Override
    public boolean stopAudio() {
        return playbackModule.stopAudio(); // Delega para o PlaybackModule
    }

    @Override
    public boolean seekAudio(int position) {
        return playbackModule.seekAudio(position); // Delega para o PlaybackModule
    }

    @Override
    public int getDuration() {
        return playbackModule.getDuration(); // Delega para o PlaybackModule
    }

    @Override
    public int getCurrentPosition() {
        return playbackModule.getCurrentPosition(); // Delega para o PlaybackModule
    }

    @Override
    public boolean setVolume(float volume) {
        return playbackModule.setVolume(volume); // Delega para o PlaybackModule
    }
}

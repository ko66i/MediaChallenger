package com.example.mediachallenger;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;

/**
 * Serviço para gerenciar a reprodução de áudio e a equalização.
 */
public class AudioService extends Service {

    private static final String TAG = "MessageService"; // Tag para logs
    private static final String CHANNEL_ID = "emixer_channel"; // ID do canal de notificação
    private static final int NOTIFICATION_ID = 1; // ID único para a notificação
    public static final String ACTION_FOREGROUND_SERVICE = "com.example.emixerapp.action.FOREGROUND_SERVICE"; // Ação para iniciar o serviço em primeiro plano

    private MediaPlayer mediaPlayer; // MediaPlayer para reproduzir áudio
    private Equalizer equalizer; // Equalizador para ajustar o áudio

    // Implementação do Binder para a interface IMessageService
    private final IMessageService.Stub binder = new IMessageService.Stub() {

        /**
         * Método para iniciar a reprodução de áudio.
         *
         * @return true se a reprodução foi iniciada com sucesso, false caso contrário.
         * @throws RemoteException Exceção lançada em caso de erro de comunicação remota.
         */
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

        /**
         * Método para pausar a reprodução de áudio.
         *
         * @return true se a reprodução foi pausada com sucesso, false caso contrário.
         * @throws RemoteException Exceção lançada em caso de erro de comunicação remota.
         */
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

        /**
         * Método para parar a reprodução de áudio.
         *
         * @return true se a reprodução foi parada com sucesso, false caso contrário.
         * @throws RemoteException Exceção lançada em caso de erro de comunicação remota.
         */
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

        /**
         * Método para buscar uma posição específica na faixa de áudio.
         *
         * @param position Posição em milissegundos para buscar.
         * @return true se a busca foi realizada com sucesso, false caso contrário.
         * @throws RemoteException Exceção lançada em caso de erro de comunicação remota.
         */
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

        /**
         * Método para obter a duração total da faixa de áudio.
         *
         * @return A duração total em milissegundos.
         * @throws RemoteException Exceção lançada em caso de erro de comunicação remota.
         */
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

        /**
         * Método para obter a posição atual da reprodução.
         *
         * @return A posição atual em milissegundos.
         * @throws RemoteException Exceção lançada em caso de erro de comunicação remota.
         */
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

        /**
         * Método para definir o volume da reprodução.
         *
         * @param volume Valor do volume (entre 0.0 e 1.0).
         * @return true se o volume foi definido com sucesso, false caso contrário.
         * @throws RemoteException Exceção lançada em caso de erro de comunicação remota.
         */
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
        createNotificationChannel(); // Cria o canal de notificação

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

    }

    /**
     * Método chamado quando um cliente se conecta ao serviço.
     *
     * @param intent Intent utilizada para conectar ao serviço.
     * @return O Binder para comunicação com o serviço.
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() chamado, intent: " + intent); // Log para depuração
        // Retorna o Binder para que os clientes possam interagir com o serviço
        return binder;
    }

    /**
     * Método chamado quando o serviço é iniciado.
     *
     * @param intent Intent utilizada para iniciar o serviço.
     * @param flags Flags adicionais.
     * @param startId ID de início do serviço.
     * @return Valor que indica como o sistema deve se comportar se o serviço for finalizado.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Cria uma notificação
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.circle_users_adapter) // Substitua pelo seu ícone
                .setContentTitle("Emixer App")
                .setContentText("Ajustando áudio em segundo plano")
                .setPriority(NotificationCompat.PRIORITY_LOW);

        Notification notification = builder.build(); // Constrói a notificação
        Log.d(TAG, "Notificação criada"); // Log para depuração

        // Inicia o serviço de primeiro plano
        startForeground(NOTIFICATION_ID, notification);

        // START_STICKY é um tipo de Service que executa tarefas em segundo plano. Esses
        // Services precisam continuar rodando em segundo plano mesmo que o usuário mude para
        // outro aplicativo. Ao usar START_STICKY, nós garantimos que o serviço de áudio continue
        // em execução mesmo que ele seja encerrado ou parado.
        return START_STICKY;
    }

    /**
     * Método chamado quando o serviço é destruído.
     * Libera os recursos do MediaPlayer e Equalizer.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() chamado"); // Log para depuração

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

    /**
     * Cria o canal de notificação (necessário para Android 8.0+).
     */
    private void createNotificationChannel() {
        // Cria um canal de notificação (necessário para Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Emixer Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
                Log.d(TAG, "Canal de notificação criado, ID: " + CHANNEL_ID); // Log para depuração
            } else {
                Log.e(TAG, "NotificationManager não disponível"); // Log de erro
            }
        }
    }


}

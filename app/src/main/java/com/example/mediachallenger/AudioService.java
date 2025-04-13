package com.example.mediachallenger;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

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

    // Implementação do Binder para a interface IMessageService
    private final IMessageService.Stub binder = new IMessageService.Stub() {

        @Override
        public boolean playAudio() throws RemoteException {
            return playbackModule.playAudio(); // Delega para o PlaybackModule
        }

        @Override
        public boolean pauseAudio() throws RemoteException {
            return playbackModule.pauseAudio(); // Delega para o PlaybackModule
        }

        @Override
        public boolean stopAudio() throws RemoteException {
            return playbackModule.stopAudio(); // Delega para o PlaybackModule
        }

        @Override
        public boolean seekAudio(int position) throws RemoteException {
            return playbackModule.seekAudio(position); // Delega para o PlaybackModule
        }

        @Override
        public int getDuration() throws RemoteException {
            return playbackModule.getDuration(); // Delega para o PlaybackModule
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return playbackModule.getCurrentPosition(); // Delega para o PlaybackModule
        }

        @Override
        public boolean setVolume(float volume) throws RemoteException {
            return playbackModule.setVolume(volume); // Delega para o PlaybackModule
        }
    };

    /**
     * Método chamado quando o serviço é criado.
     * Inicializa os módulos.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() chamado"); // Log para depuração

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

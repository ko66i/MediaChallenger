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

import androidx.core.app.NotificationCompat;

/**
 * Serviço para gerenciar a reprodução de áudio e a equalização.
 */
public class MessageService extends Service {

    private static final String TAG = "MessageService";
    private static final String CHANNEL_ID = "emixer_channel";
    private static final int NOTIFICATION_ID = 1; // ID único para a notificação
    public static final String ACTION_FOREGROUND_SERVICE = "com.example.emixerapp.action.FOREGROUND_SERVICE"; // Ação para iniciar o serviço em primeiro plano

    private MediaPlayer mediaPlayer; // MediaPlayer para reproduzir áudio
    private Equalizer equalizer; // Equalizador para ajustar o áudio

    // Implementação do Binder para a interface IMessageService
    private final IMessageService.Stub binder = new IMessageService.Stub() {

        @Override
        public boolean playAudio() throws RemoteException {
            Log.e("MSGRECEIVED", "Play audio");
            if (!mediaPlayer.isPlaying()) {
                // Inicializa o MediaPlayer
                mediaPlayer.setLooping(true); // Define para repetir a música
                mediaPlayer.start(); // Inicia a reprodução
                return true;
            }
            return false;
        }

        @Override
        public boolean pauseAudio() throws RemoteException {
            Log.e("MSGRECEIVED", "Pause audio");
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                return true;
            }
            return false;
        }

        @Override
        public boolean stopAudio() throws RemoteException {
            Log.e("MSGRECEIVED", "Stop audio");
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer = MediaPlayer.create(MessageService.this, R.raw.test_audio);
                return true;
            }
            return false;
        }

    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() chamado");
        createNotificationChannel(); // Cria o canal de notificação

        mediaPlayer = MediaPlayer.create(this, R.raw.test_audio); // Audio para teste

        // Inicializa o Equalizer
        if (mediaPlayer != null) {
            int audioSessionId = mediaPlayer.getAudioSessionId();
            equalizer = new Equalizer(0, audioSessionId);
            equalizer.setEnabled(true);
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() chamado, intent: " + intent);
        // Retorna o Binder para que os clientes possam interagir com o serviço
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() chamado, intent: " + intent + ", flags: " + flags + ", startId: " + startId);

        // Cria uma notificação
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.circle_users_adapter) // Substitua pelo seu ícone
                .setContentTitle("Emixer App")
                .setContentText("Ajustando áudio em segundo plano")
                .setPriority(NotificationCompat.PRIORITY_LOW);

        Notification notification = builder.build();
        Log.d(TAG, "Notificação criada");

        // Inicia o serviço de primeiro plano
        startForeground(NOTIFICATION_ID, notification);

        return START_STICKY; // Garante que o serviço seja reiniciado se for interrompido
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() chamado");

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
                Log.d(TAG, "Canal de notificação criado, ID: " + CHANNEL_ID);
            } else {
                Log.e(TAG, "NotificationManager não disponível");
            }
        }
    }


}

package com.example.mediachallenger.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.mediachallenger.R;

/**
 * Módulo responsável por gerenciar as notificações.
 * Implementa a interface NotificationInterface para fornecer os métodos de criação e gerenciamento de notificações.
 */
public class NotificationModule implements NotificationInterface {

    private Context context; // Contexto da aplicação
    private String channelId; // ID do canal de notificação
    private int notificationId; // ID da notificação


    /**
     * Construtor da classe.
     *
     * @param context        O contexto da aplicação.
     * @param channelId      O ID do canal de notificação.
     * @param notificationId O ID da notificação.
     */
    public NotificationModule(Context context, String channelId, int notificationId) {
        this.context = context;
        this.channelId = channelId;
        this.notificationId = notificationId;
    }

    /**
     * Cria o canal de notificação (necessário para Android 8.0+).
     */
    @Override
    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Emixer Service Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = context.getSystemService(NotificationManager.class); // Obtém o NotificationManager
            if (manager != null) {
                manager.createNotificationChannel(channel); // Cria o canal
            }
        }
    }

    // Criação do builder para Notificações - Serve para desacomplar a criaçao de notification para facilitar os Unit Tests
    public Notification createNotification(String title, String text) {
        return buildCompatNotification(title, text);
    }

    /**
     * Cria uma notificação com o título e texto especificados.
     *
     * @param title O título da notificação.
     * @param text  O texto da notificação.
     * @return A notificação criada.
     */
    protected Notification buildCompatNotification(String title, String text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.circle_users_adapter)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        return builder.build();
    }

    /**
     * Exibe a notificação.
     *
     * @param notification A notificação a ser exibida.
     */
    public void showNotification(Notification notification) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(notificationId, notification);
        }
    }
}
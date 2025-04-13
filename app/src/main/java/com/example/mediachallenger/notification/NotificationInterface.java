package com.example.mediachallenger.notification;

import android.app.Notification;

/**
 * Interface que define os métodos para gerenciar as notificações.
 * Esta interface é implementada pela classe NotificationModule.
 */
public interface NotificationInterface {
    /**
     * Cria o canal de notificação (necessário para Android 8.0+).
     */
    void createNotificationChannel();

    /**
     * Cria uma notificação com o título e texto especificados.
     * @param title O título da notificação.
     * @param text O texto da notificação.
     * @return A notificação criada.
     */
    Notification createNotification(String title, String text);
}

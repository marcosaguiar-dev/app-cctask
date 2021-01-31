package com.myapplications.cctask_app.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.myapplications.cctask_app.Activity.TarefaAvisoActivity;
import com.myapplications.cctask_app.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    //chamado quando o usuário recebe uma notificação
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if(remoteMessage.getNotification() != null){
            String titulo = remoteMessage.getNotification().getTitle();
            String corpo = remoteMessage.getNotification().getBody();

            enviarNoficacao(titulo, corpo);
        }
    }

    private void enviarNoficacao(String titulo, String corpo){

        //configurações para notificação
        String canal = getString(R.string.default_notification_channel_id);
        Uri uriSom = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent = new Intent(this, TarefaAvisoActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0, new Intent[]{intent}, PendingIntent.FLAG_ONE_SHOT);


        //criar notificacao
        NotificationCompat.Builder notificacao = new NotificationCompat.Builder(this, canal)
                .setContentTitle(titulo)
                .setContentText(corpo)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setSound(uriSom)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        //recuperar notificationManager
        NotificationManager notificationManager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Verifica versão o android a partir o Oreo para configurar canal de notificação
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(canal, "canal", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        //enviar notifacao
        notificationManager.notify(0, notificacao.build());
    }

    //durante a comunicação um token é gerado
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }
}

package com.muhammadzain.mzkwallpaper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        showNotifation(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
    }

    private void showNotifation(  String title,String message) {

        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,"MyNotifitation")
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true)
                .setContentText(message);
        NotificationManagerCompat manegar=NotificationManagerCompat.from(this);
        manegar.notify(999,builder.build());
    }
}

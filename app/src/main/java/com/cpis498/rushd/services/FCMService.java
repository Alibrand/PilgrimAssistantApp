package com.cpis498.rushd.services;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.cpis498.rushd.LoginActivity;
import com.cpis498.rushd.PilgrimInfoActivity;
import com.cpis498.rushd.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class FCMService extends FirebaseMessagingService {
    String TAG="fcm";
    private static final String CHANNEL_ID = "channel_02";

    private FirebaseHelper helper;


    public FCMService() {


    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + message.getFrom());
        Log.d(TAG, "Type: " + message.getMessageType());
        Log.d(TAG, "Data: " + message.getData().toString());
        Map<String,String > data=message.getData();
        helper=new FirebaseHelper();
        //check if the organizer owns the campaign
        if(!helper.getLoggedUserId().equals(data.get("organizer_id")))
            return;
        String pilgrim_id=data.get("pilgrim_id");
        Intent intent= new Intent(this, PilgrimInfoActivity.class)
        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("pilgrim_id",pilgrim_id);

        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
               intent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(message.getNotification().getTitle())
                .setContentText(message.getNotification().getBody())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(true)
                .setContentIntent(activityPendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(message.getNotification().getBody())
                .setWhen(System.currentTimeMillis())
                .setChannelId(CHANNEL_ID)
                .setAutoCancel(true);

        NotificationManager notificationManager =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "SOS";
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{500,1000,500,1000});

            // Set the Notification Channel for the Notification Manager.
            notificationManager.createNotificationChannel(mChannel);
        }


// notificationId is a unique int for each notification that you must define
        notificationManager.notify(4, builder.build());

        // Check if message contains a data payload.
        if (message.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + message.getData());



        }

        // Check if message contains a notification payload.
        if (message.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + message.getNotification().getBody());
        }

    }
}
package io.takemeaway.takemeaway.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import io.takemeaway.takemeaway.application.TakeMeAway;

public class FirebaseMessageService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String message = remoteMessage.getNotification().getBody();
        Log.d("FCM", message);
    }
}

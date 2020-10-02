package io.takemeaway.takemeaway.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import io.takemeaway.takemeaway.application.TakeMeAway;

public class FirebaseIDService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String refreshedToken) {
        super.onNewToken(refreshedToken);
        Log.e("NEW_TOKEN", refreshedToken);

        TakeMeAway.getBackend().setSharedPreferences("deviceToken", refreshedToken);
        TakeMeAway.getBackend().setSharedPreferences("deviceTokenSaved", "no");
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
    }
}

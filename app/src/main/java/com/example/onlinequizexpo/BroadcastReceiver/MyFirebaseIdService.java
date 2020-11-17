package com.example.onlinequizexpo.BroadcastReceiver;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;

public class MyFirebaseIdService extends FirebaseMessagingService {



    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
//        s = FirebaseInstanceId.getInstance().getToken();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                   if (!task.isSuccessful()) {
                       Log.d("EQUA", "getInstanceId Failed", task.getException());
                       return;
                   }

                   String token = task.getResult().getToken();

                   String msg = getString(Integer.parseInt("This is the token"), token);
                    }
                });

    sendRegistrationToServer(s);
    }

    private void sendRegistrationToServer(String s) {
        Log.d("TOKEN", s);
    }
}

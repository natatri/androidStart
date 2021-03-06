/**
 * Copyright Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.firebase.codelab.friendlychat;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.android.gms.maps.OnMapReadyCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

import java.math.BigInteger;
import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class MyFirebaseMessagingService extends FirebaseMessagingService {
    LocationController locationController;
    private static final String TAG = "MyFMService";

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.
        Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());
        Log.d(TAG, "FCM Notification Message: " +
                remoteMessage.getNotification());
        Log.d(TAG, "FCM Data Message: " + remoteMessage.getData());


        if (remoteMessage.getData().get("message").equals("yesIWantToMeet")) {
//            Intent intent = new Intent(this,MainActivity.class);
//            intent.putExtra("")
         //   EventBus.getDefault().post(new StartMapEvent(Double.parseDouble(remoteMessage.getNotification().getTitle()),Double.parseDouble(remoteMessage.getNotification().getBody())));

            sendApprovedNotification("You've been approved, would you like to chat now?", remoteMessage);
        }

        if (remoteMessage.getData().get("message").equals("locationNotification")) {
//            DialogUtils.createDialog(this, "Do u want to meet?", new Interfaces.basicListener() {
//                @Override
//                public void onSuccess() {
//                    double lat = locationController.getLat();
//                    double lng = locationController.getLng();
//
//                    NotificationController notificationController = new NotificationController(MyFirebaseMessagingService.this);
//                    String nexus6p = "dSTD1uvHd60:APA91bHLdwLcFtmt-Ee6wEiaXSGpk7flxrD5UwNklH9uxBljYWli9X0bW1pRUOiE6fbCGD40yDqoj-xdgNsRVL-p7xvBQo0z9AF-BEDtguuhNhDMnP8-MsbNV1MqdzPQBVO9tNn4M37O";
//                    //String nexusS ="dWswpCvgpyc:APA91bHdmJzphQgHeT1VvePeIhagqmltsjZ1yhQ_7FpIp-mL79fqzL8X87EiYOX7D7o7XddZ2VLe4Uo_QV8EQwe1yoOcyxYeYxYS8UjPLQm7S7KLyYYB81FobI5TunpAJCh6W1K-DEbw";
//                    ArrayList<String> regIds = new ArrayList<String>();
//                    regIds.add(nexus6p);
//                    JSONArray regArray = new JSONArray(regIds);
//
//                    notificationController.sendMessage(regArray,lat+"",lng +"",null,"locationNotification");
//                }
//
//                @Override
//                public void onError() {
//
//                }
//            });
            String friendId = remoteMessage.getNotification().getBody().substring(remoteMessage.getNotification().getBody().indexOf(":")  + 1);
            FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("usersNew").child(mFirebaseAuth.getCurrentUser().getUid()).child("pending");
          // DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference().child("usersNew").child(getIntent().getStringExtra("senderIdToGetBackToo")).child("matches");
            myRef.keepSynced(true);
            myRef.child(friendId).child("userId").setValue(friendId);
            myRef.child(friendId).child("redDot").setValue("redDot");


            Intent intent = new Intent(MyFirebaseMessagingService.this,MeetingRequestNotificationActivity.class);
            intent.putExtra("tokenToGetBackTo",remoteMessage.getNotification().getTitle().substring(remoteMessage.getNotification().getTitle().indexOf(":")+1,remoteMessage.getNotification().getTitle().length()));
            intent.putExtra("latToGetBackTo",remoteMessage.getNotification().getTitle().substring(0,remoteMessage.getNotification().getTitle().indexOf(":") - 1));
            intent.putExtra("lngToGetBackTo",remoteMessage.getNotification().getBody().substring(0, remoteMessage.getNotification().getBody().indexOf(":") - 1));
            intent.putExtra("senderIdToGetBackToo",friendId);
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        //Calling method to generate notification
        sendNotification(remoteMessage.getNotification().getBody());
    }


    private void sendApprovedNotification(String messageBody,RemoteMessage remoteMessage) {
        Intent intent = new Intent(getApplicationContext(),MeetingRequestNotificationActivity.class);
        intent.putExtra("latToGetBackTo",Double.parseDouble(remoteMessage.getNotification().getTitle()));
        intent.putExtra("lngToGetBackTo",Double.parseDouble(remoteMessage.getNotification().getBody().substring(0,remoteMessage.getNotification().getBody().indexOf(":") - 1)));
        intent.putExtra("senderIdToGetBackToo",remoteMessage.getNotification().getBody().substring(remoteMessage.getNotification().getBody().indexOf(":") + 1));
        intent.putExtra("intentType","youHaveBeenApproved");
        intent.putExtra("mainMessage",messageBody);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Hunt me")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
        startActivity(intent);
    }


    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Firebase Push Notification")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
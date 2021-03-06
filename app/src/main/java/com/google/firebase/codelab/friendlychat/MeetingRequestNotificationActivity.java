package com.google.firebase.codelab.friendlychat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MeetingRequestNotificationActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    LocationController locationController;
    String messageString;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_request_notification);
        setTitle("");
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        mFirebaseAuth = FirebaseAuth.getInstance();
        locationController = new LocationController(this);

        messageString = getIntent().getStringExtra("mainMessage");
        if(messageString == null || messageString.isEmpty()){
            messageString = "Do u want to meet?";
        }

        DialogUtils.createDialog(this, messageString, new Interfaces.basicListener() {
            @Override
            public void onSuccess() {

                if(getIntent().getStringExtra("intentType") != null && getIntent().getStringExtra("intentType").equals("youHaveBeenApproved")){
                    Intent intent = new Intent(MeetingRequestNotificationActivity.this,MainActivity.class);
                    intent.putExtra("latToGetBackTo",getIntent().getDoubleExtra("latToGetBackTo",0));
                    intent.putExtra("lngToGetBackTo",getIntent().getDoubleExtra("lngToGetBackTo",0));
                    intent.putExtra("senderIdToGetBackToo", getIntent().getStringExtra("senderIdToGetBackToo"));
                    intent.putExtra("intentType","cameFormMeetingActivity");
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                }else {

                    double lat = locationController.getLat();
                    double lng = locationController.getLng();

                    NotificationController notificationController = new NotificationController(MeetingRequestNotificationActivity.this);
                    //String nexus6p = "dSTD1uvHd60:APA91bHLdwLcFtmt-Ee6wEiaXSGpk7flxrD5UwNklH9uxBljYWli9X0bW1pRUOiE6fbCGD40yDqoj-xdgNsRVL-p7xvBQo0z9AF-BEDtguuhNhDMnP8-MsbNV1MqdzPQBVO9tNn4M37O";
                    //String nexusS ="dWswpCvgpyc:APA91bHdmJzphQgHeT1VvePeIhagqmltsjZ1yhQ_7FpIp-mL79fqzL8X87EiYOX7D7o7XddZ2VLe4Uo_QV8EQwe1yoOcyxYeYxYS8UjPLQm7S7KLyYYB81FobI5TunpAJCh6W1K-DEbw";
                    ArrayList<String> regIds = new ArrayList<String>();
                    regIds.add(getIntent().getStringExtra("tokenToGetBackTo"));
                    JSONArray regArray = new JSONArray(regIds);
//kkkkkkkkkkkkkkkkk
                    notificationController.sendMessage(regArray, lat + "", lng + ":" + mFirebaseAuth.getCurrentUser().getUid(), null, "yesIWantToMeet");
                    //EventBus.getDefault().post(new StartMapEvent(Double.parseDouble(getIntent().getStringExtra("latToGetBackTo")),Double.parseDouble(getIntent().getStringExtra("lngToGetBackTo"))));
                    Intent intent = new Intent(MeetingRequestNotificationActivity.this, MainActivity.class);
                    intent.putExtra("intentType", "cameFormMeetingActivity");
                    intent.putExtra("latToGetBackTo", Double.parseDouble(getIntent().getStringExtra("latToGetBackTo")));
                    intent.putExtra("lngToGetBackTo", Double.parseDouble(getIntent().getStringExtra("lngToGetBackTo")));
                    intent.putExtra("senderIdToGetBackToo", getIntent().getStringExtra("senderIdToGetBackToo"));

                    DatabaseReference myRefPending = FirebaseDatabase.getInstance().getReference().child("usersNew").child(mFirebaseAuth.getCurrentUser().getUid()).child("pending");
                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference().child("usersNew").child(mFirebaseAuth.getCurrentUser().getUid()).child("matches");
                    DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference().child("usersNew").child(getIntent().getStringExtra("senderIdToGetBackToo")).child("matches");
                    myRef.keepSynced(true);
                    friendRef.keepSynced(true);
                    myRefPending.keepSynced(true);
                    myRefPending.child(getIntent().getStringExtra("senderIdToGetBackToo")).removeValue();
                    myRef.child(getIntent().getStringExtra("senderIdToGetBackToo")).child("userId").setValue(getIntent().getStringExtra("senderIdToGetBackToo"));
                    myRef.child(getIntent().getStringExtra("senderIdToGetBackToo")).child("redDot").setValue("redDot");
                    friendRef.child(mFirebaseAuth.getCurrentUser().getUid()).child("userId").setValue(mFirebaseAuth.getCurrentUser().getUid());
                    friendRef.child(mFirebaseAuth.getCurrentUser().getUid()).child("redDot").setValue("redDot");
                   // myRef.child(getIntent().getStringExtra("senderIdToGetBackToo")).child("redDot").setValue("redDot");

                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onError() {
                finish();
            }
        });






//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

}

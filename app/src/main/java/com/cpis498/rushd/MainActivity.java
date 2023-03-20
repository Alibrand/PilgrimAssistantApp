package com.cpis498.rushd;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cpis498.rushd.services.FirebaseHelper;
import com.cpis498.rushd.services.SendSOS;
import com.cpis498.rushd.services.StartLocationTracking;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {
    AppCompatButton button_start,button_signout,button_contact_my_organizer,button_prayers,
    button_call_authority;
    FirebaseHelper helper;
    StartLocationTracking tracking;
    String campaign_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize variables
        button_start=findViewById(R.id.button_start_journy);
        button_signout=findViewById(R.id.button_signout);
        button_contact_my_organizer=findViewById(R.id.button_contact_my_organizer);
        button_call_authority=findViewById(R.id.button_contact_organizer);
        helper=new FirebaseHelper(this);
        tracking=new StartLocationTracking(this);
        button_prayers=findViewById(R.id.button_prayers);

        Intent intent=getIntent();
        campaign_id=intent.getStringExtra("campaign_id");


        button_prayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,PrayersActivity.class);
                startActivity(intent);
            }
        });

        button_call_authority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,ContactAuthorityActivity.class);
                startActivity(intent);
            }
        });







        button_contact_my_organizer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(campaign_id.isEmpty())
                {
                    Toast.makeText(MainActivity.this, "أنت لا تنتمي إلى الحملة بعد" ,Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent1=new Intent(MainActivity.this,ContactMyCampaignActivity.class);
                intent1.putExtra("campaign_id",campaign_id);
                startActivity(intent1);
            }
        });



        //set on click listener for signout
        button_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper.logout();

            }
        });

        //set on click listener
        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,JournyTypeActivity.class);
                startActivity(intent);
            }
        });



    }


}
package com.cpis498.rushd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class ContactAuthorityActivity extends AppCompatActivity {

    AppCompatButton button_call_ambulance,button_call_police,
    button_call_emergency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_authority);

        button_call_ambulance=findViewById(R.id.button_call_ambulance);
        button_call_police=findViewById(R.id.button_call_police);
        button_call_emergency=findViewById(R.id.button_emergency);

        button_call_ambulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = "997";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });

        button_call_police.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = "999";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });

        button_call_emergency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = "112";
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });

    }
}
package com.cpis498.rushd;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class JournyTypeActivity extends AppCompatActivity {

    AppCompatButton start_hajj,start_umrah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journy_type);

        start_hajj=findViewById(R.id.button_hajj);
        start_umrah=findViewById(R.id.button_umrah);

        start_hajj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent=new Intent(JournyTypeActivity.this,HajjDaysActivity.class);
               startActivity(intent);

            }
        });

        start_umrah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(JournyTypeActivity.this,UmrahFirstActivity.class);
                startActivity(intent);


            }
        });
    }
}
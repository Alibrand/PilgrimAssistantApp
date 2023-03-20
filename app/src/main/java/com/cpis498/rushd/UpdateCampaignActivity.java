package com.cpis498.rushd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateCampaignActivity extends AppCompatActivity {

    EditText text_new_name;
    AppCompatButton button_save;
    String campaign_id="";
    FirebaseFirestore firebaseFirestore;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_campaign);
        button_save=findViewById(R.id.button_save);
        text_new_name=findViewById(R.id.etxt_campaign_name);
        firebaseFirestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);

        Intent intent=getIntent();
        campaign_id=intent.getStringExtra("campaign_id");







        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String new_name=text_new_name.getText().toString();
                if(new_name.isEmpty())
                {
                    Toast.makeText(UpdateCampaignActivity.this, "الاسم فارغ" ,Toast.LENGTH_LONG).show();
                    return;
                }

                progressDialog.setCancelable(false);
                progressDialog.setMessage("جاري حفظ التغيرات");

                progressDialog.show();

                firebaseFirestore.collection("campaigns")
                        .document(campaign_id)
                        .update("name",new_name)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(UpdateCampaignActivity.this, "تم حفظ التغييرات بنجاح" ,Toast.LENGTH_LONG).show();
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                         Toast.makeText(UpdateCampaignActivity.this, "حدث خطأ" ,Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });


            }
        });


    }
}
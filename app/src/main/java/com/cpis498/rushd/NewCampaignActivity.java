package com.cpis498.rushd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cpis498.rushd.models.Campaign;
import com.cpis498.rushd.services.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewCampaignActivity extends AppCompatActivity {

    AppCompatButton button_create;
    EditText text_campaing_name;
    FirebaseHelper helper;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_campaign);

        button_create=findViewById(R.id.button_create);
        text_campaing_name=findViewById(R.id.etxt_campaign_name);
        helper=new FirebaseHelper(this);
        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);


        button_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String campaing_name=text_campaing_name.getText().toString();

                if(campaing_name.isEmpty())
                {
                    Toast.makeText(NewCampaignActivity.this,"حقل الاسم فارغ " ,Toast.LENGTH_LONG).show();
                    return;
                }


                String organizer_id=helper.getLoggedUserId();

                Campaign campaign=new Campaign();
                campaign.setName(campaing_name);
                campaign.setOrganizer_id(organizer_id);

                progressDialog.setCancelable(false);
                progressDialog.setMessage("يتم إنشاء الحملة");
                progressDialog.show();

                firestore.collection("campaigns")
                        .add(campaign)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Intent intent=new Intent(NewCampaignActivity.this,OrganizerActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                Toast.makeText(NewCampaignActivity.this,"تم انشاء الحملة بنجاح" ,Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NewCampaignActivity.this,"حدث خطأ" ,Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });




            }
        });
    }
}
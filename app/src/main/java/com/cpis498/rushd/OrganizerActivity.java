package com.cpis498.rushd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cpis498.rushd.models.Pilgrim;
import com.cpis498.rushd.services.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class OrganizerActivity extends AppCompatActivity {
    AppCompatButton button_call_authority,button_signout,button_new_campaign,button_manage_campaign,
    button_pilgrims_list,button_locate_pilgrims;
    FirebaseHelper helper;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    String campaign_id="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);
        //initialize variables
        button_signout=findViewById(R.id.button_signout);
        button_new_campaign=findViewById(R.id.button_new_campaign);
        button_manage_campaign=findViewById(R.id.button_manage_campaign);
        button_pilgrims_list=findViewById(R.id.button_track_campaign);
        button_locate_pilgrims=findViewById(R.id.button_locate_pilgrims);
        button_call_authority=findViewById(R.id.button_contact_organizer);
        helper=new FirebaseHelper(this);
        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);


        checkCampain();


        button_call_authority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(OrganizerActivity.this,ContactAuthorityActivity.class);
                startActivity(intent);
            }
        });


        button_locate_pilgrims.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(OrganizerActivity.this,TrackingPilgrimsActivity.class);
                intent.putExtra("campaign_id",campaign_id);
                startActivity(intent);
            }
        });


        button_pilgrims_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(campaign_id=="")
                    return;

                Intent intent=new Intent(OrganizerActivity.this,CampaignPilgrimsActivity.class);
                intent.putExtra("campaign_id",campaign_id);
                startActivity(intent);
            }
        });

        button_manage_campaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(campaign_id=="")
                    return;

                Intent intent=new Intent(OrganizerActivity.this,ManageCampaignActivity.class);
                intent.putExtra("campaign_id",campaign_id);
                startActivity(intent);
            }
        });

        button_new_campaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(OrganizerActivity.this,NewCampaignActivity.class);
                startActivity(intent);
            }
        });


        //set on click listener for signout
        button_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helper.logout();

            }
        });
    }

    private void checkCampain() {
        progressDialog.setCancelable(false);
        progressDialog.setMessage("التحقق من معلومات الحملة");
        progressDialog.show();

        String user_id=helper.getLoggedUserId();
        firestore.collection("campaigns")
                .whereEqualTo("organizer_id",user_id)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.size()>0)
                {
                    DocumentSnapshot doc=queryDocumentSnapshots.getDocuments().get(0);
                    campaign_id=doc.getId();
                    button_new_campaign.setVisibility(View.GONE);
                    button_manage_campaign.setVisibility(View.VISIBLE);
                }
                progressDialog.dismiss();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OrganizerActivity.this,"فشل تحديد معلومات الحملة" ,Toast.LENGTH_LONG ).show();
                progressDialog.dismiss();
            }
        });

    }
}
package com.cpis498.rushd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cpis498.rushd.models.Campaign;
import com.cpis498.rushd.models.Pilgrim;
import com.cpis498.rushd.services.SendSOS;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ContactMyCampaignActivity extends AppCompatActivity {

    AppCompatButton button_call,button_im_lost;
    String campaign_id="";
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;
    Pilgrim organizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_my_campaign);
        button_call=findViewById(R.id.button_call);
        button_im_lost=findViewById(R.id.button_im_lost);
        firestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);

        Intent intent=getIntent();
        campaign_id=intent.getStringExtra("campaign_id");


        getOrganizerInfo();

        button_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = organizer.getPhone();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                startActivity(intent);
            }
        });

        button_im_lost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(ContactMyCampaignActivity.this);
                builder.setMessage("سيتم إرسال نداء إلى منظم الحملة");
                builder.setPositiveButton("موافق", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SendSOS sendSOS=new SendSOS(ContactMyCampaignActivity.this);
                        sendSOS.send(organizer.getUid());
                    }
                });
                builder.setNegativeButton("تراجع", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();

            }

        });






    }

    private void getOrganizerInfo() {
        progressDialog.setCancelable(false);
        progressDialog.setMessage("تحميل بيانات المنظم");
        progressDialog.show();

        firestore.collection("campaigns")
                .document(campaign_id)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Campaign campaign=new Campaign(documentSnapshot.getData());
                firestore.collection("user_info")
                        .whereEqualTo("uid",campaign.getOrganizer_id())
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        DocumentSnapshot doc=queryDocumentSnapshots.getDocuments().get(0);
                        organizer=new Pilgrim(doc.getData());
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ContactMyCampaignActivity.this,"حدث خطأ" +e.getMessage(),Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                        finish();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ContactMyCampaignActivity.this,"حدث خطأ" +e.getMessage(),Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                finish();
            }
        });
    }
}
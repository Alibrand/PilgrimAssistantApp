package com.cpis498.rushd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cpis498.rushd.services.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ManageCampaignActivity extends AppCompatActivity {

    String campaign_id="";
    AppCompatButton button_add_pilgrim,button_update_campaign,
    button_delete_campaign;
    FirebaseHelper helper;
    FirebaseFirestore firestore;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_campaign);

        button_add_pilgrim=findViewById(R.id.button_add_pilgrims);
        button_update_campaign=findViewById(R.id.button_update_campaign);
        button_delete_campaign=findViewById(R.id.button_delete_campaign);
        progressDialog=new ProgressDialog(this);

        helper=new FirebaseHelper(this);
        firestore=FirebaseFirestore.getInstance();

        Intent intent=getIntent();
        campaign_id=intent.getStringExtra("campaign_id");


        button_delete_campaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ManageCampaignActivity.this);
                alert.setTitle("تنبيه");
                alert.setMessage("سيتم حذف كامل بيانات الحملة..متأكد من الحذف؟");
                alert.setPositiveButton("نعم", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        deleteCampaign();


                        dialog.dismiss();
                    }
                });

                alert.setNegativeButton("إلغاء", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alert.show();


            }
        });


        button_add_pilgrim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(ManageCampaignActivity.this,AddNewPilgrimsActivity.class);
                intent1.putExtra("campaign_id",campaign_id);
                startActivity(intent1);
            }
        });


        button_update_campaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1=new Intent(ManageCampaignActivity.this,UpdateCampaignActivity.class);
                intent1.putExtra("campaign_id",campaign_id);
                startActivity(intent1);
            }
        });

    }

    private void deleteCampaign() {
        progressDialog.setCancelable(false);
        progressDialog.setMessage("يتم حذف الحملة");
        progressDialog.show();
        String organizer_id=helper.getLoggedUserId();
        firestore.collection("campaigns")
                .document(campaign_id).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        updatePilgrimsCampaigns();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ManageCampaignActivity.this,"حدث خطأ أثناء حذف الحملة" ,Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        });


    }

    private void updatePilgrimsCampaigns() {
        progressDialog.setMessage("يتم تحديث بيانات الأفراد");
        firestore.collection("user_info")
                .whereEqualTo("campaign",campaign_id)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot doc: queryDocumentSnapshots.getDocuments())
                {
                    doc.getReference().update("campaign","");





                }
                Intent intent=new Intent(ManageCampaignActivity.this,OrganizerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ManageCampaignActivity.this,"حدث خطأ أثناء تحديث بيانات الأفراد" ,Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }
}
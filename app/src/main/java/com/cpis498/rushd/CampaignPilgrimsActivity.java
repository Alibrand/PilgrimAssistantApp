package com.cpis498.rushd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.cpis498.rushd.adapters.CampaignListAdapter;
import com.cpis498.rushd.models.Campaign;
import com.cpis498.rushd.models.Pilgrim;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CampaignPilgrimsActivity extends AppCompatActivity {

    RecyclerView campaign_list;
    FirebaseFirestore firestore;
    String campaign_id;
    ProgressDialog progressDialog;
    TextView text_campaign_name,text_count;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_pilgrims);
        firestore=FirebaseFirestore.getInstance();
        campaign_list=findViewById(R.id.campaign_list);
        text_campaign_name=findViewById(R.id.campaign_name);
        text_count=findViewById(R.id.list_count);
        progressDialog=new ProgressDialog(this);

        Intent intent=getIntent();
        campaign_id=intent.getStringExtra("campaign_id");

        loadCampaignInfo();




    }

    public void loadCampaignInfo(){
        List<Pilgrim> pilgrimList=new ArrayList<>();
        progressDialog.setCancelable(false);
        progressDialog.setMessage("الرجاء الانتظار قليلاً");
        progressDialog.show();

        firestore.collection("campaigns")
                .document(campaign_id)
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Campaign campaign=new Campaign(documentSnapshot.getData());
                text_campaign_name.setText(campaign.getName());
                loadCampaignList();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CampaignPilgrimsActivity.this,"حدث خطأ" ,Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }

    private void loadCampaignList() {
        List<Pilgrim> pilgrimList=new ArrayList<>();


        firestore.collection("user_info")
                .whereEqualTo("campaign",campaign_id)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for(DocumentSnapshot doc : queryDocumentSnapshots.getDocuments())
                {
                    Pilgrim pilgrim=new Pilgrim(doc.getData());
                    pilgrim.setDoc_id(doc.getId());
                    pilgrimList.add(pilgrim);

                }
                CampaignListAdapter adapter=new CampaignListAdapter(CampaignPilgrimsActivity.this,pilgrimList);

                campaign_list.setAdapter(adapter);
                text_count.setText(String.valueOf(pilgrimList.size()) );
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CampaignPilgrimsActivity.this,"حدث خطأ" ,Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }
}
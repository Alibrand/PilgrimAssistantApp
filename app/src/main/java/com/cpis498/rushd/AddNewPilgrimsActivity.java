package com.cpis498.rushd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cpis498.rushd.models.Pilgrim;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class AddNewPilgrimsActivity extends AppCompatActivity {
    AppCompatButton button_search,button_add;
    TextView text_name,text_nid,text_phone,text_status;
    EditText text_search_nid;
    LinearLayout search_card;
    String campaign_id="";
    String pilgrim_id="";
    ProgressDialog progressDialog;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_pilgrims);

        Intent intent=getIntent();
        campaign_id=intent.getStringExtra("campaign_id");


        button_add=findViewById(R.id.button_add);
        button_search=findViewById(R.id.button_search);
        text_name=findViewById(R.id.text_name);
        text_nid=findViewById(R.id.text_nid);
        text_search_nid=findViewById(R.id.search_nid);
        text_phone=findViewById(R.id.text_phone);
        text_status=findViewById(R.id.text_status);
        search_card=findViewById(R.id.search_card);
        progressDialog=new ProgressDialog(this);
        firestore=FirebaseFirestore.getInstance();

        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nid=text_search_nid.getText().toString();

                if(nid.isEmpty())
                    return;

                progressDialog.setCancelable(false);
                progressDialog.setMessage("جاري البحث");
                progressDialog.show();

                firestore.collection("user_info")
                        .whereEqualTo("nid",nid)
                        .whereEqualTo("account_type","حاج")
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(queryDocumentSnapshots.size()>0)
                        {
                            DocumentSnapshot doc=queryDocumentSnapshots.getDocuments().get(0);
                            pilgrim_id=doc.getId();
                            Pilgrim pilgrim=new Pilgrim(doc.getData());
                            search_card.setVisibility(View.VISIBLE);
                            text_name.setText("الاسم:" +pilgrim.getName());
                            text_nid.setText("الرقم المعرف :"+pilgrim.getNid());
                            text_phone.setText("الجوال: "+pilgrim.getPhone());
                            if(pilgrim.getCampaign().isEmpty())
                            {
                                text_status.setText("لا ينتمي الى أي حملة ");
                                button_add.setVisibility(View.VISIBLE);
                            }
                            else{
                                if(pilgrim.getCampaign().equals(campaign_id))
                                text_status.setText("ينتمي إلى حملتك");
                                else
                                text_status.setText("ينتمي إلى إحدى الحملات");
                                button_add.setVisibility(View.GONE);
                            }

                        }
                        else {
                            search_card.setVisibility(View.GONE);
                            Toast.makeText(AddNewPilgrimsActivity.this, "لا توجد نتائج مطابقة" ,Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddNewPilgrimsActivity.this, "حدث خطأ" ,Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                });




            }
        });


        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setCancelable(false);
                progressDialog.setMessage("الرجاء الانتظار قليلاً");
                progressDialog.show();

                firestore.collection("user_info")
                        .document(pilgrim_id)
                        .update("campaign",campaign_id)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(AddNewPilgrimsActivity.this, "تم الإضافة الى الحملة بنجاح" ,Toast.LENGTH_LONG).show();
                            search_card.setVisibility(View.GONE);
                                progressDialog.dismiss();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddNewPilgrimsActivity.this, "حدث خطأ" ,Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    }
                });
            }
        });







    }
}
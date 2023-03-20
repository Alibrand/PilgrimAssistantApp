package com.cpis498.rushd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cpis498.rushd.adapters.NusukListAdapter;
import com.cpis498.rushd.models.Pilgrim;
import com.cpis498.rushd.services.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HajjLastDayActivity extends AppCompatActivity {

    AppCompatButton button_prayers;
    RecyclerView list_nusuk;
    String current_phase="";
    List<String> phases=new ArrayList<>();
    FirebaseHelper helper;
    FirebaseFirestore firestore;
    ProgressDialog dialog;
    Pilgrim pilgrim;
    int thisDay=5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hajj_last_day);
        phases.add("رمي الجرات الثلاث");
        phases.add("المبيت في منى");


        list_nusuk=findViewById(R.id.list_nusuk);
        helper=new FirebaseHelper(this);
        firestore=FirebaseFirestore.getInstance();
        dialog=new ProgressDialog(HajjLastDayActivity.this);

        button_prayers=findViewById(R.id.button_prayers);
        button_prayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HajjLastDayActivity.this,PrayersActivity.class);
                startActivity(intent);
            }
        });



        loadCurrentPhase();







    }

    public void loadCurrentPhase() {

        dialog.setCancelable(false);
        dialog.setMessage("يتم تحميل المعلومات");
        dialog.show();


        String uid=helper.getLoggedUserId();

        firestore.collection("user_info")
                .whereEqualTo("uid",uid)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentSnapshot doc=queryDocumentSnapshots.getDocuments().get(0);
                pilgrim=new Pilgrim(doc.getData());


                NusukListAdapter adapter=new NusukListAdapter(HajjLastDayActivity.this,phases, pilgrim.getCurrent_phase(),doc.getId(),pilgrim.getDays_check(),thisDay);
                list_nusuk.setAdapter(adapter);
                dialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HajjLastDayActivity.this, "حدث خطأ", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }


}
package com.cpis498.rushd;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

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
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import java.util.List;

public class HajjFirstDayActivity extends AppCompatActivity {

    AppCompatButton button_tawaf,button_next;
    FirebaseHelper helper;
    FirebaseFirestore firestore;
    ProgressDialog dialog;
    Pilgrim pilgrim;
    int day=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hajj_first_day);



        button_tawaf=findViewById(R.id.button_tawaf);
        button_next=findViewById(R.id.button_next);
        helper=new FirebaseHelper(this);

        firestore=FirebaseFirestore.getInstance();
        dialog=new ProgressDialog(this);

        checkIfComplete();

        button_tawaf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(HajjFirstDayActivity.this,TawafActivity.class);
                    startActivity(intent);
                    }
                });

        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HajjFirstDayActivity.this,HajjSecondDayActivity.class);

                startActivity(intent);
                finish();
            }
        });


    }

    public void checkIfComplete() {

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
                List<Boolean> days=pilgrim.getDays_check();
                if(days.get(day)==true)
                button_next.setVisibility(View.VISIBLE);
 else
                    button_next.setVisibility(View.INVISIBLE);
                dialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HajjFirstDayActivity.this, "حدث خطأ", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onResume() {
        checkIfComplete();
        super.onResume();
    }
}
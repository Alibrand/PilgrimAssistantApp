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

public class HajjThirdDayActivity extends AppCompatActivity {

    AppCompatButton button_next,button_prayers;
    RecyclerView list_nusuk;
    String current_phase="";
    List<String> phases=new ArrayList<>();
    FirebaseHelper helper;
    FirebaseFirestore firestore;
    ProgressDialog dialog;
    Pilgrim pilgrim;
    int thisDay=2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hajj_third_day);
        phases.add("صلاة الفجر في مزدلفة");
        phases.add("التوجه إلى منى");
        phases.add("رمي العقبة الكبرى ( 7 حصيّات )");
        phases.add("التحلل (الحلق أو التقصير )");
        phases.add("التوجه إلى مكة");
        phases.add("القيام بطواف الإفاضة");
        phases.add("التحلل الأكبر");
        phases.add("السعي بين الصفا والمروة");
        phases.add("المبيت في منى");

        button_next=findViewById(R.id.button_next);
        list_nusuk=findViewById(R.id.list_nusuk);
        helper=new FirebaseHelper(this);
        firestore=FirebaseFirestore.getInstance();
        dialog=new ProgressDialog(HajjThirdDayActivity.this);
        button_prayers=findViewById(R.id.button_prayers);
        button_prayers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HajjThirdDayActivity.this,PrayersActivity.class);
                startActivity(intent);
            }
        });

        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(HajjThirdDayActivity.this,HajjFourthDayActivity.class);
                startActivity(intent);
                finish();
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

                if(pilgrim.getDays_check().get(thisDay)==true)
                    button_next.setVisibility(View.VISIBLE);
                else
                    button_next.setVisibility(View.INVISIBLE);


                NusukListAdapter adapter=new NusukListAdapter(HajjThirdDayActivity.this,phases, pilgrim.getCurrent_phase(),doc.getId(),pilgrim.getDays_check(),thisDay);
                list_nusuk.setAdapter(adapter);
                dialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HajjThirdDayActivity.this, "حدث خطأ", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }


}
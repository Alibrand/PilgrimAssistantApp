package com.cpis498.rushd;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.cpis498.rushd.models.Pilgrim;
import com.cpis498.rushd.services.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HajjDaysActivity extends AppCompatActivity {

    AppCompatButton button_first_day,
            button_second_day, button_third_day,
            button_fourth_day, button_fifth_day,
            button_sixth_day,button_reset;
    FirebaseFirestore firestore;
    FirebaseHelper helper;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hajj_days);
        button_first_day = findViewById(R.id.button_first_day);
        button_second_day = findViewById(R.id.button_second_day);
        button_third_day = findViewById(R.id.button_third_day);
        button_fourth_day = findViewById(R.id.button_fourth_day);
        button_fifth_day = findViewById(R.id.button_fifth_day);
        button_sixth_day = findViewById(R.id.button_sixth_day);
        button_reset=findViewById(R.id.button_restart);
        progressDialog = new ProgressDialog(this);


        helper = new FirebaseHelper(this);
        firestore = FirebaseFirestore.getInstance();








        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(HajjDaysActivity.this);
                alert.setTitle("تنبيه");
                alert.setMessage("سيتم إعادتك إلى اليوم الأول...متأكد؟");
                alert.setPositiveButton("نعم", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        resetHajj();



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


        loadDaysStatus();


    }

    public void resetHajj() {
        progressDialog.setCancelable(false);
        progressDialog.setMessage("الرجاء الانتظار قليلاً");
        progressDialog.show();
        String uid = helper.getLoggedUserId();
        Log.d("rushdInfo", "satrt reeeeeeesettt");

        firestore.collection("user_info")
                .whereEqualTo("uid", uid)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                 List<Boolean> days_check= Arrays.asList(false,false,false,false,false,false);
                Log.d("rushdInfo", "get user");
                Map<String,Object> newData=new HashMap<>();
                newData.put("current_day","0");
                newData.put("current_phase","");
                newData.put("days_check",days_check);

                Log.d("rushdInfo", "data  ready");
                String docid=doc.getId();
                Log.d("rushdInfo", "doc id= "+docid);
                firestore.collection("user_info")
                        .document(docid)
                        .update(newData).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("rushdInfo", "reeeeeeesettt success");
                        loadDaysStatus();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(HajjDaysActivity.this, "حدث خطأ", Toast.LENGTH_LONG).show();

                        progressDialog.dismiss();

                    }
                });;



            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(HajjDaysActivity.this, "حدث خطأ", Toast.LENGTH_LONG).show();

                progressDialog.dismiss();

            }
        });
    }


    public void loadDaysStatus() {
        Log.d("rushdInfo", "get user info status");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("يتم تحميل المعلومات");
        progressDialog.show();
        String uid = helper.getLoggedUserId();

        firestore.collection("user_info")
                .whereEqualTo("uid", uid)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                Pilgrim pilgrim = new Pilgrim(doc.getData());
                List<Boolean> daysStatus = pilgrim.getDays_check();
                int day = Integer.parseInt(pilgrim.getCurrent_day());


                if (daysStatus.get(0) == false)
                    if (day == 0) {
                        button_first_day.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_hourglass_empty_24, 0, 0, 0);
                        button_first_day.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(HajjDaysActivity.this, HajjFirstDayActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                    else {
                        button_first_day.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        button_first_day.setOnClickListener(null);
                    }
                    else{
                    button_first_day.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_done_all_24, 0, 0, 0);

                    button_first_day.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(HajjDaysActivity.this, HajjFirstDayActivity.class);
                            startActivity(intent);
                        }
                    });
                }

                if (daysStatus.get(1) == false)
                    if (day == 1) {
                        button_second_day.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_hourglass_empty_24, 0, 0, 0);
                        button_second_day.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(HajjDaysActivity.this, HajjSecondDayActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                    else {
                        button_second_day.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        button_second_day.setOnClickListener(null);
                    }
                else{
                    button_second_day.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_done_all_24, 0, 0, 0);

                    button_second_day.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(HajjDaysActivity.this, HajjSecondDayActivity.class);
                            startActivity(intent);
                        }
                    });
                }

                if (daysStatus.get(2) == false)
                    if (day == 2) {
                        button_third_day.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_hourglass_empty_24, 0, 0, 0);
                        button_third_day.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(HajjDaysActivity.this, HajjThirdDayActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                    else {
                        button_third_day.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        button_third_day.setOnClickListener(null);
                    }
                else{
                    button_third_day.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_done_all_24, 0, 0, 0);

                    button_third_day.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(HajjDaysActivity.this, HajjThirdDayActivity.class);
                            startActivity(intent);
                        }
                    });
                }
                if (daysStatus.get(3) == false)
                    if (day == 3) {
                        button_fourth_day.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_hourglass_empty_24, 0, 0, 0);
                        button_fourth_day.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(HajjDaysActivity.this, HajjFourthDayActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                    else {
                        button_fourth_day.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        button_fourth_day.setOnClickListener(null);
                    }
                else{
                    button_fourth_day.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_done_all_24, 0, 0, 0);

                    button_fourth_day.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(HajjDaysActivity.this, HajjFourthDayActivity.class);
                            startActivity(intent);
                        }
                    });
                }
                if (daysStatus.get(4) == false)
                    if (day == 4) {
                        button_fifth_day.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_hourglass_empty_24, 0, 0, 0);
                        button_fifth_day.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(HajjDaysActivity.this, HajjFifthDayActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                    else {
                        button_fifth_day.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        button_fifth_day.setOnClickListener(null);
                    }
                else{
                    button_fifth_day.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_done_all_24, 0, 0, 0);

                    button_fifth_day.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(HajjDaysActivity.this, HajjFifthDayActivity.class);
                            startActivity(intent);
                        }
                    });
                }
                if (daysStatus.get(5) == false)
                    if (day>= 5) {
                        button_sixth_day.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_hourglass_empty_24, 0, 0, 0);
                        button_sixth_day.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(HajjDaysActivity.this, HajjLastDayActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                    else {
                        button_sixth_day.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                        button_sixth_day.setOnClickListener(null);
                    }
                else{
                    button_sixth_day.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_done_all_24, 0, 0, 0);

                    button_sixth_day.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(HajjDaysActivity.this, HajjLastDayActivity.class);
                            startActivity(intent);
                        }
                    });
                }

                progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(HajjDaysActivity.this, "حدث خطأ", Toast.LENGTH_LONG).show();

                progressDialog.dismiss();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
loadDaysStatus();

    }
}
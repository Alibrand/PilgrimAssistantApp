package com.cpis498.rushd;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.cpis498.rushd.models.Pilgrim;
import com.cpis498.rushd.services.FirebaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TawafActivity extends AppCompatActivity {

    AppCompatButton button_start_tawaf, button_restart, button_return;
    ImageButton button_increase_lap, button_decrease_lap;
    LinearLayoutCompat ly_controls;
    TextView text_current_lap, text_remainig_laps, text_complete;
    FirebaseHelper helper;
    FirebaseFirestore firestore;
    ProgressDialog dialog;
    int current_lap = 1, remainig_laps = 6, MAX_LAPS = 7;
    DocumentSnapshot userSnapshot;
    ListenerRegistration snapListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tawaf);
        button_start_tawaf = findViewById(R.id.button_start_tawaf);
        button_restart = findViewById(R.id.button_restart);
        ly_controls = findViewById(R.id.ly_laps_controls);
        text_current_lap = findViewById(R.id.text_current_lap);
        text_remainig_laps = findViewById(R.id.text_remaining_laps);
        button_increase_lap = findViewById(R.id.button_increase_laps);
        button_decrease_lap = findViewById(R.id.button_decrease_laps);
        button_return = findViewById(R.id.button_return);
        text_complete = findViewById(R.id.text_complete);
        button_return.setVisibility(View.GONE);
        text_complete.setVisibility(View.GONE);

        firestore = FirebaseFirestore.getInstance();
        helper = new FirebaseHelper(this);
        dialog = new ProgressDialog(TawafActivity.this);

        ly_controls.setVisibility(View.GONE);

        checkTawafStatus();


        button_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        button_increase_lap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                current_lap++;

                updateCurrentLap();
            }
        });

        button_decrease_lap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (current_lap == 1) {
                    return;
                }
                current_lap--;

                updateCurrentLap();
            }
        });


        button_start_tawaf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setCancelable(false);
                dialog.setMessage("الرجاء الانتظار قليلا");
                dialog.show();


                Map<String, Object> newData = new HashMap<String, Object>();
                newData.put("current_phase", "طواف القدوم");
                newData.put("current_lap", "1");
                newData.put("current_day", "0");

                firestore.collection("user_info")
                        .document(userSnapshot.getId())
                        .update(newData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                ly_controls.setVisibility(View.VISIBLE);
                                button_start_tawaf.setVisibility(View.GONE);

                                current_lap = 1;


                                updateStats();
                                dialog.dismiss();

                                snapListener = firestore.collection("user_info")
                                        .document(userSnapshot.getId())
                                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                                Pilgrim changes = new Pilgrim(value.getData());
                                                if (changes.getCurrent_lap().equals("انتهى من طواف القدوم"))
                                                    return;
                                                if (current_lap > MAX_LAPS) {
                                                    ly_controls.setVisibility(View.GONE);
                                                    text_complete.setVisibility(View.VISIBLE);
                                                    button_return.setVisibility(View.VISIBLE);
                                                    resetCurrentPhase();
                                                    return;
                                                }
                                                current_lap = Integer.parseInt(changes.getCurrent_lap());
                                                updateStats();


                                            }
                                        });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TawafActivity.this, "حدث خطأ", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });


            }
        });

        button_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ly_controls.setVisibility(View.GONE);
                button_start_tawaf.setVisibility(View.VISIBLE);
            }
        });


    }

    private void resetCurrentPhase() {
//        dialog.setCancelable(false);
//        dialog.setMessage("الرجاء الانتظار قليلا");
//        dialog.show();

        Pilgrim pilgrim = new Pilgrim(userSnapshot.getData());
        List<Boolean> days = pilgrim.getDays_check();

        days.set(0, true);

        Map<String, Object> newData = new HashMap<String, Object>();
        newData.put("current_phase", "انتهى من طواف القدوم");
        newData.put("current_lap", "0");
        newData.put("current_day", "1");
        newData.put("days_check", days);

        Log.d("rushInfo", "updated");


        firestore.collection("user_info")
                .document(userSnapshot.getId())
                .update(newData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        snapListener.remove();
//                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TawafActivity.this, "حدث خطأ", Toast.LENGTH_LONG).show();
//                dialog.dismiss();
            }
        });
    }


    private void updateCurrentLap() {
        dialog.setCancelable(false);
        dialog.setMessage("الرجاء الانتظار قليلا");
        dialog.show();

        firestore.collection("user_info")
                .document(userSnapshot.getId())
                .update("current_lap", String.valueOf(current_lap))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        updateStats();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TawafActivity.this, "حدث خطأ", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }

    private void checkTawafStatus() {
        dialog.setCancelable(false);
        dialog.setMessage("الرجاء الانتظار قليلا");
        dialog.show();

        String uid = helper.getLoggedUserId();


        firestore.collection("user_info")
                .whereEqualTo("uid", uid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() > 0) {


                            userSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            Pilgrim pilgrim = new Pilgrim(userSnapshot.getData());
                            Log.d("TawafInfo", pilgrim.getCurrent_phase());

                            if (pilgrim.getCurrent_phase().equals("طواف القدوم")) {
                                current_lap = Integer.parseInt(pilgrim.getCurrent_lap());
                                ly_controls.setVisibility(View.VISIBLE);
                                button_start_tawaf.setVisibility(View.GONE);


                                updateStats();

                                snapListener = firestore.collection("user_info")
                                        .document(userSnapshot.getId())
                                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                                Pilgrim changes = new Pilgrim(value.getData());
                                                Log.d("TawafInfo", changes.getName());
                                                current_lap = Integer.parseInt(changes.getCurrent_lap());
                                                if (current_lap > MAX_LAPS) {
                                                    ly_controls.setVisibility(View.GONE);
                                                    text_complete.setVisibility(View.VISIBLE);
                                                    button_return.setVisibility(View.VISIBLE);
                                                    resetCurrentPhase();
                                                    return;
                                                }
                                                updateStats();


                                            }
                                        });
                            }


                        }
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TawafActivity.this, "حدث خطأ", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
    }

    private void updateStats() {
        remainig_laps = MAX_LAPS - current_lap;
        text_current_lap.setText(String.valueOf(current_lap));
        text_remainig_laps.setText(String.valueOf(remainig_laps));
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        if(snapListener!=null)
        snapListener.remove();
        super.onDestroy();
    }
}
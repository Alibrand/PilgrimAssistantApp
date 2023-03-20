package com.cpis498.rushd;

import static com.cpis498.rushd.R.id.hajj_phase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cpis498.rushd.databinding.ActivityTrackingPilgrimsBinding;
import com.cpis498.rushd.models.Pilgrim;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class PilgrimInfoActivity  extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String pilgrim_id="";
    ProgressDialog progressDialog;
    FirebaseFirestore firestore;
    String pilgrim_doc_id="";
    private ActivityTrackingPilgrimsBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    Marker mMarker;
    Pilgrim pilgrim;
    TextView button_call,button_locate,button_delete,pilgrim_name,hajj_phase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilgrim_info);

        Intent intent=getIntent();
        pilgrim_id=intent.getStringExtra("pilgrim_id");
        Log.d("fcm","pilgrim is " +pilgrim_id);

        button_call=findViewById(R.id.button_call);
        button_locate=findViewById(R.id.button_locate);
        button_delete=findViewById(R.id.button_delete);
        pilgrim_name=findViewById(R.id.pilgrim_name);
        hajj_phase=findViewById(R.id.hajj_phase);


        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("جاري تحميل المعلومات");
        progressDialog.show();

        firestore=FirebaseFirestore.getInstance();

        firestore.collection("user_info")
                .whereEqualTo("uid",pilgrim_id)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                DocumentSnapshot doc=queryDocumentSnapshots.getDocuments().get(0);
                pilgrim_doc_id=doc.getId();
                pilgrim=new Pilgrim(doc.getData());

                pilgrim_name.setText(pilgrim.getName());

                LatLng myLocation = new LatLng(pilgrim.getLocation().getLatitude(),pilgrim.getLocation().getLongitude());


                float zoom = 17.572168f;
                CameraPosition position = new CameraPosition(myLocation, zoom, 0, 0);
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));

                progressDialog.dismiss();
                button_call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", pilgrim.getPhone(), null));
                        startActivity(intent);
                    }
                });

                button_locate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LatLng myLocation = new LatLng(pilgrim.getLocation().getLatitude(),pilgrim.getLocation().getLongitude());


                        float zoom = 17.572168f;
                        CameraPosition position = new CameraPosition(myLocation, zoom, 0, 0);
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));
                    }
                });

                button_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder dialog=new AlertDialog.Builder(PilgrimInfoActivity.this);
                        dialog.setMessage("متأكد من حذف العنصر؟");
                        dialog.setPositiveButton("نعم", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                progressDialog.setCancelable(false);
                                progressDialog.setMessage("الرجاء الانتظار قليلاً");
                                progressDialog.show();
                                firestore.collection("user_info")
                                        .document(pilgrim_doc_id)
                                        .update("campaign","","current_phase","")
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                progressDialog.dismiss();

                                               finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(PilgrimInfoActivity.this,"حدث خطأ" ,Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }
                                });
                            }
                        });
                        dialog.setNegativeButton("لا", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                            }
                        });
                        dialog.show();
                    }
                });

                firestore.collection("user_info")
                        .document(pilgrim_doc_id)
                        .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {


                                 pilgrim=new Pilgrim(value.getData());
                                MarkerOptions markerOptions=new MarkerOptions();
                                hajj_phase.setText(pilgrim.getCurrent_phase());
                                LatLng location=new LatLng(pilgrim.getLocation().getLatitude(),
                                        pilgrim.getLocation().getLongitude());
                                if(mMarker==null)
                                {markerOptions.position(location);
                                    markerOptions.title(pilgrim.getName());
                                    markerOptions.visible(true);
                                    mMarker= mMap.addMarker(markerOptions);


                                }
                                else{
                                    mMarker.setPosition(location);
                                }

                            }

                        });


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(PilgrimInfoActivity.this,   "حدث خطأ" , Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });

// Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));



    }
}
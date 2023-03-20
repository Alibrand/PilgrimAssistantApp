package com.cpis498.rushd;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.cpis498.rushd.models.Pilgrim;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.cpis498.rushd.databinding.ActivityTrackingPilgrimsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TrackingPilgrimsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityTrackingPilgrimsBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseFirestore firestore;
    private String campaign_id;
    private List<Marker> markers;
    private AppCompatButton button_center,button_my_location;
    private LatLng myLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTrackingPilgrimsBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Intent intent=getIntent();
        campaign_id=intent.getStringExtra("campaign_id");
        firestore=FirebaseFirestore.getInstance();
        markers=new ArrayList<>();
        button_center=findViewById(R.id.button_locate_center);
        button_my_location=findViewById(R.id.button_locate_me);

        button_center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LatLng center= findCenterPosition();

                LatLngBounds.Builder group=new LatLngBounds.Builder();
                for(Marker m:markers)
                {
                    group.include(m.getPosition());
                }
                LatLngBounds bounds=group.build();


                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,200));

            }
        });

        button_my_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                                     LatLngBounds bounds=LatLngBounds.builder()
                                             .include(myLocation).build();
                                      mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,100));


            }
        });












        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    private LatLng findCenterPosition(){
        double sumLat=0.0,sumLong=0.0;
        for (Marker marker:
             markers) {
        sumLat+=marker.getPosition().latitude;
            sumLong+=marker.getPosition().longitude;
 }
        return new LatLng(sumLat/markers.size(),sumLong/markers.size());
    }

    private  Marker findMarker(String tag){
        for (Marker marker:
             markers) {
           if(marker.getTag().equals(tag))
               return  marker;
        }

        return  null;

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
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {

                             myLocation = new LatLng(location.getLatitude(),location.getLongitude());
                            float zoom = 17.572168f;
                            CameraPosition position = new CameraPosition(myLocation, zoom, 0, 0);
                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(position));


                        }
                    }
                });
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        firestore.collection("user_info")
                .whereEqualTo("campaign",campaign_id)

                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if(value!=null)
                        for(DocumentSnapshot doc:value.getDocuments())
                        {

                            Pilgrim pilgrim=new Pilgrim(doc.getData());
                            MarkerOptions markerOptions=new MarkerOptions();
                            LatLng location=new LatLng(pilgrim.getLocation().getLatitude(),
                                    pilgrim.getLocation().getLongitude());
                            //check if marker is already added to list
                            Marker mMarker=findMarker(doc.getId());
                            if(mMarker==null)
                            {markerOptions.position(location);
                            markerOptions.title(pilgrim.getName());
                            markerOptions.visible(true);
                            Marker marker= mMap.addMarker(markerOptions);

                            marker.setTag(doc.getId());
                            markers.add(marker);
                            }
                            else{
                                mMarker.setPosition(location);
                            }

                        }






                    }
                });

    }


}
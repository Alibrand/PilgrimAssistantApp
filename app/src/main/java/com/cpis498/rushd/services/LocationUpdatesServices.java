package com.cpis498.rushd.services;


import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cpis498.rushd.LoginActivity;
import com.cpis498.rushd.R;
import com.cpis498.rushd.models.Pilgrim;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


//import androidx.annotation.NonNull;
//import androidx.core.app.NotificationCompat;
//import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * A bound and started service that is promoted to a foreground service when location updates have
 * been requested and all clients unbind.
 * <p>
 * For apps running in the background on "O" devices, location is computed only once every 10
 * minutes and delivered batched every 30 minutes. This restriction applies even to apps
 * targeting "N" or lower which are run on "O" devices.
 * <p>
 * This sample show how to use a long-running service for location updates. When an activity is
 * bound to this service, frequent location updates are permitted. When the activity is removed
 * from the foreground, the service promotes itself to a foreground service, and location updates
 * continue. When the activity comes back to the foreground, the foreground service stops, and the
 * notification assocaited with that service is removed.
 */
public class LocationUpdatesServices extends Service {

    private static final String PACKAGE_NAME =
            "com.google.android.gms.location.rushd.locationupdates";
    static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";
    static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";
    private static final String TAG = "resPOINT";
    /**
     * The name of the channel for notifications.
     */
    private static final String CHANNEL_ID = "channel_01";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
            ".started_from_notification";
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    /**
     * The identifier for the notification displayed for the foreground service.
     */
    private static final int NOTIFICATION_ID = 12345678;
    private final IBinder mBinder = new LocalBinder();
    List<LatLng> lapPoints = new ArrayList<>();
    List<LatLng> kabbaCorners = new ArrayList<LatLng>();
    LatLng kabbaCenteriod = new LatLng(21.42250204306374, 39.826177287000874);
    Double latitude, longitude;
    /**
     * Realtime location save in firestore or firebase
     */

    FirebaseFirestore firebaseFirestore;
    DocumentReference documentReference;
    FirebaseAuth firebaseAuth;
    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground service notification only if the former takes
     * place.
     */
    private boolean mChangingConfiguration = false;
    private NotificationManager mNotificationManager;
    /**
     * Contains parameters used by {@link com.google.android.gms.location.FusedLocationProviderApi}.
     */
    private LocationRequest mLocationRequest;
    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;
    /**
     * Callback for changes in location.
     */
    private LocationCallback mLocationCallback;
    private Handler mServiceHandler;
    /**
     * The current location.
     */
    private Location mLocation;

    @SuppressWarnings("deprecation")
    public LocationUpdatesServices() {
        kabbaCorners = new ArrayList<LatLng>();
        kabbaCorners.add(new LatLng(21.4225660680771, 39.82620314356593));
        kabbaCorners.add(new LatLng(21.422482421882815, 39.82626081106021));
        kabbaCorners.add(new LatLng(21.422438101864923, 39.82618637975945));
        kabbaCorners.add(new LatLng(21.422519875408874, 39.82612871226575));

    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate() {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };

        createLocationRequest();
        getLastLocation();

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }


    @SuppressWarnings("deprecation")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service started");
        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,
                false);

        // We got here because the user decided to remove location updates from the notification.
        if (startedFromNotification) {
            removeLocationUpdates();
            stopSelf();
        }
        // Tells the system to not try to recreate the service after it has been killed.
        return START_NOT_STICKY;
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }


    @SuppressWarnings("deprecation")
    @Override
    public IBinder onBind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        Log.i(TAG, "in onBind()");
        stopForeground(true);
        mChangingConfiguration = false;

        // Register Firestore when service will restart
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        return mBinder;
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onRebind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
        Log.i(TAG, "in onRebind()");
        stopForeground(true);
        mChangingConfiguration = false;

        // Register Firestore when service will restart
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        super.onRebind(intent);
    }


    @SuppressWarnings("deprecation")
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Last client unbound from service");

        // Called when the last client (MainActivity in case of this sample) unbinds from this
        // service. If this method is called due to a configuration change in MainActivity, we
        // do nothing. Otherwise, we make this service a foreground service.
        if (!mChangingConfiguration && Utils.requestingLocationUpdates(this)) {
            Log.d(TAG, "Starting foreground service");
            /*
            // TODO(developer). If targeting O, use the following code.
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
                mNotificationManager.startServiceInForeground(new Intent(this,
                        LocationUpdatesService.class), NOTIFICATION_ID, getNotification());
            } else {
                startForeground(NOTIFICATION_ID, getNotification());
            }
             */

            startForeground(NOTIFICATION_ID, getNotification());


        }
        return true; // Ensures onRebind() is called when a client re-binds.
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacksAndMessages(null);
    }

    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void requestLocationUpdates() {
        Log.i(TAG, "Requesting location updates");
        Utils.setRequestingLocationUpdates(this, true);
        startService(new Intent(getApplicationContext(), LocationUpdatesServices.class));
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            Utils.setRequestingLocationUpdates(this, false);
            Log.d(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }
    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void removeLocationUpdates() {
        Log.i(TAG, "Removing location updates");
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            Utils.setRequestingLocationUpdates(this, false);
            stopSelf();
        } catch (SecurityException unlikely) {
            Utils.setRequestingLocationUpdates(this, true);
            Log.d(TAG, "Lost location permission. Could not remove updates. " + unlikely);
        }
    }

    /**
     * Returns the {@link NotificationCompat} used as part of the foreground service.
     */
    private Notification getNotification() {
        Intent intent = new Intent(this, LocationUpdatesServices.class);

        CharSequence text = Utils.getLocationText(mLocation);

        // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);

        // The PendingIntent that leads to a call to onStartCommand() in this service.
        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        // The PendingIntent to launch activity.
        PendingIntent activityPendingIntent;

        activityPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, LoginActivity.class), 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .addAction(R.drawable.ic_baseline_launch_24, "افتح التطبيق",
                        activityPendingIntent)

                .setContentText(text)
                .setContentTitle(Utils.getLocationTitle(this))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(text)

                .setWhen(System.currentTimeMillis());

        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }


        return builder.build();
    }

    private void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                            } else {
                                Log.w(TAG, "Failed to get location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.d(TAG, "Lost location permission." + unlikely);
        }
    }

    private void onNewLocation(Location location) {
        Log.d(TAG, "New location: " + location);

        mLocation = location;

        // Notify anyone listening for broadcasts about the new location.
        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra(EXTRA_LOCATION, location);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        // Getting location when notification was call.
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        SavetoServer();
        // Update notification content if running as a foreground service.
        if (serviceIsRunningInForeground(this)) {
            //  mNotificationManager.notify(NOTIFICATION_ID, getNotification());

            // Getting location when notification was call.
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            // Here using to call Save to serverMethod
            SavetoServer();

        }
    }

    /**
     * Sets the location request parameters.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Returns true if this is a foreground service.
     *
     * @param context The {@link Context}.
     */
    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Save a value in realtime to firestore when user in background
     * For foreground you have to call same method to activity
     */

    private void SavetoServer() {
        //Toast.makeText(this, "Save to server", Toast.LENGTH_SHORT).show();
        Log.d("resMM", "Send to server");
        Log.d("resML", String.valueOf(latitude));
        Log.d("resMLL", String.valueOf(longitude));

        Map<String, String> driverMap = new HashMap<>();

        GeoPoint point = new GeoPoint(latitude, longitude);
        FirebaseUser user = null;
        user = firebaseAuth.getCurrentUser();
        if (user == null) return;
        String uid = user.getUid();


        firebaseFirestore
                .collection("user_info")
                .whereEqualTo("uid", uid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                documentReference = queryDocumentSnapshots.getDocuments().get(0).getReference();
                DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                Pilgrim pilgrim = new Pilgrim(doc.getData());
                if (pilgrim.getCurrent_phase().equals("طواف القدوم")) {
                    updateTawafLap(documentReference, pilgrim.getCurrent_lap());
                }
                documentReference.update("location", point)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error updating document", e);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error connecting to firebase", e);
            }
        });


    }

    private void updateTawafLap(DocumentReference documentReference, String laps) {
        LatLng newPoint = new LatLng(latitude, longitude);
        LatLng firstPoint = null, lastPoint = null;
        if (lapPoints.size() > 1) {
            firstPoint = lapPoints.get(0);
            lastPoint = lapPoints.get(lapPoints.size() - 1);

            if (SphericalUtil.computeDistanceBetween(newPoint, lastPoint) < 2)
                return;

        }

        lapPoints.add(newPoint);
        Log.d("TawafInfo", lapPoints.toString());

        List<LatLng> polyPoints = new ArrayList<>();
        polyPoints.addAll(lapPoints);


        boolean isLap = true;
        for (LatLng corner :
                kabbaCorners) {

            boolean isContained = PolyUtil.containsLocation(corner, polyPoints, true);
            Log.d("TawafInfo", "corner:" + corner.toString() + "-isInside=" + isContained);
            isLap = isLap & isContained;

        }
        Log.d("TawafInfo", "islap=" + isLap);

        if (!isLap)
            return;


        //compare the heading
        double headingWithCenter = SphericalUtil.computeHeading(kabbaCenteriod, newPoint);
        double headingWithfirst = SphericalUtil.computeHeading(kabbaCenteriod, firstPoint);
        double diff = Math.abs(headingWithCenter - headingWithfirst);
        //if the last point is too far from first point
        if (diff > 5)
            return;


        lapPoints = new ArrayList<>();
        lapPoints.add(polyPoints.get(0));

        int lap = Integer.parseInt(laps);
        Log.d("TawafInfo", "clap=" + lap);

        documentReference.update("current_lap", String.valueOf(lap + 1))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Lap successfully updated!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error updating document", e);
            }
        });


    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        LocationUpdatesServices getService() {
            return LocationUpdatesServices.this;
        }
    }


}
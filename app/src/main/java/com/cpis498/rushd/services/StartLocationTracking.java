package com.cpis498.rushd.services;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cpis498.rushd.R;
import com.google.android.material.snackbar.Snackbar;

public class StartLocationTracking {
    private static final String TAG = "resPMain";

    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private ServiceReceiver myReceiver;

    // A reference to the service used to get location updates.
    private LocationUpdatesServices mService = null;

    // Tracks the bound state of the service.
    public static boolean mBound = false;

    //context
    private Context mContext;

    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesServices.LocalBinder binder = (LocationUpdatesServices.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };



    public StartLocationTracking(){}


    public StartLocationTracking(Context context){
        this.mContext=context;
        myReceiver = new ServiceReceiver(mContext);


        // Check that the user hasn't revoked permissions by going to Settings.
        if (Utils.requestingLocationUpdates(mContext)) {
            if (!checkPermissions()) {
                requestPermissions();
            }
        }

        mContext.bindService(new Intent(mContext, LocationUpdatesServices.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);

    }

    private boolean checkPermissions() {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(((Activity) mContext),
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public  void startUpdates(){

        mService.requestLocationUpdates();
    }

    public  void stopUpdates(){
        mBound=false;
        mService.removeLocationUpdates();
    }

    public void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(((Activity) mContext),
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
//            Snackbar.make(
//                    ((Activity) mContext).findViewById(R.id.button_signin),
//                   " R.string.permission_rationale",
//                    Snackbar.LENGTH_INDEFINITE)
//                    .setAction("Ok", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            // Request permission
//                            ActivityCompat.requestPermissions(((Activity) mContext),
//                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                    REQUEST_PERMISSIONS_REQUEST_CODE);
//                        }
//                    })
//                    .show();
            // Request permission
                            ActivityCompat.requestPermissions(((Activity) mContext),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
              } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(((Activity) mContext),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    public void unBindService(){
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            if(mService!=null)
           mContext.unbindService(mServiceConnection);
            mBound = false;
        }
    }

    public void onActivityPause(){
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            mContext.unbindService(mServiceConnection);
            mBound = false;
        }
    }

    public  void onActivityResume(){
        LocalBroadcastManager.getInstance(mContext).registerReceiver(myReceiver,
                new IntentFilter(LocationUpdatesServices.ACTION_BROADCAST));
    }

}

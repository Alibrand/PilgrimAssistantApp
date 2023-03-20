package com.cpis498.rushd.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.widget.Toast;

public class ServiceReceiver extends BroadcastReceiver {
    Context mContext;
    @Override
    public void onReceive(Context context, Intent intent) {
        Location location = intent.getParcelableExtra(LocationUpdatesServices.EXTRA_LOCATION);

    }

    public ServiceReceiver(Context context) {
        this.mContext=context;
    }
}


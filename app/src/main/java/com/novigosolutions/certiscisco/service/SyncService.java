package com.novigosolutions.certiscisco.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.novigosolutions.certiscisco.webservices.SyncCaller;

import java.util.Date;

/**
 * Created by dhanrajk on 22-06-17.
 */

public class SyncService extends Service {
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.e("sync service: " , new Date().toString());
        SyncCaller.instance().Sync(null,getApplicationContext());
        return Service.START_NOT_STICKY;
    }
}

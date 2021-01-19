package com.novigosolutions.certiscisco.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.novigosolutions.certiscisco.webservices.ATMOfflineListUpdateCaller;

import java.util.Date;

/**
 * Created by dhanrajk on 22-06-17.
 */

public class OfflineUpdateService extends Service {
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, final int startId) {
        Log.e("offline service: " , new Date().toString());
        ATMOfflineListUpdateCaller.instance().UpdateATMList(null,getApplicationContext());
        return Service.START_NOT_STICKY;
    }
}

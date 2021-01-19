package com.novigosolutions.certiscisco.recivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.novigosolutions.certiscisco.service.SyncService;
import com.novigosolutions.certiscisco.utils.Preferences;

import java.util.Date;

/**
 * Created by dhanrajk on 22-06-17.
 */

public class IntervalChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {
        int EXEC_INTERVAL=Preferences.getInt("INTERVAL",ctx);
        Log.e("sync start: ", new Date().toString());
        Log.e("sync interval: ",":"+EXEC_INTERVAL);
        if(EXEC_INTERVAL>0) {
            try {

                AlarmManager alarmManager = (AlarmManager) ctx
                        .getSystemService(Context.ALARM_SERVICE);
                Intent i = new Intent(ctx, SyncService.class);
                PendingIntent intentExecuted = PendingIntent.getService(ctx, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
                alarmManager.cancel(intentExecuted);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+EXEC_INTERVAL*1000,EXEC_INTERVAL*1000, intentExecuted);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }
}

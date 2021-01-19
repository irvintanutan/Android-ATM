package com.novigosolutions.certiscisco.recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.novigosolutions.certiscisco.utils.Preferences;

/**
 * Created by dhanrajk on 22-06-17.
 */

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context ctx, Intent intent) {
        Log.e("boot completed","yes");
        Preferences.saveBoolean("LoggedIn", false, ctx);
    }
}

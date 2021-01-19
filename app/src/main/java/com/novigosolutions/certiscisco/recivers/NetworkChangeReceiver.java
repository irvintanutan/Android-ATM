package com.novigosolutions.certiscisco.recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.novigosolutions.certiscisco.interfaces.NetworkChangekListener;
import com.novigosolutions.certiscisco.models.Job;
import com.novigosolutions.certiscisco.service.OfflineUpdateService;
import com.novigosolutions.certiscisco.utils.NetworkUtil;


public class NetworkChangeReceiver extends BroadcastReceiver {
    public static NetworkChangekListener changekListener;

    @Override
    public void onReceive(final Context context, final Intent intent) {
        Log.e("Network changed",":"+NetworkUtil.getConnectivityStatusString(context));
        if (changekListener != null)
            changekListener.onNetworkChanged();
        if(NetworkUtil.getConnectivityStatusString(context)&&Job.isOfflineExist())
        {
            Intent eventService = new Intent(context, OfflineUpdateService.class);
            context.startService(eventService);
            Log.e("updating offline",":");
        }
    }
}

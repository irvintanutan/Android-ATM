package com.novigosolutions.certiscisco.applications;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.novigosolutions.certiscisco.models.Cartridge;
import com.novigosolutions.certiscisco.models.ClearHistoryRequests;
import com.novigosolutions.certiscisco.models.CoinEnvelopes;
import com.novigosolutions.certiscisco.models.EditRequests;
import com.novigosolutions.certiscisco.models.Job;
import com.novigosolutions.certiscisco.models.OtherScan;
import com.novigosolutions.certiscisco.models.Seal;
import com.novigosolutions.certiscisco.models.TestCash;
import com.novigosolutions.certiscisco.utils.Preferences;

/**
 * Created by dhanrajk on 23-06-17.
 */

public class CertisCISCO extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Configuration dbConfiguration = new Configuration.Builder(this).setDatabaseName("certiscisco.db").setDatabaseVersion(3).addModelClasses(Job.class, Cartridge.class, Seal.class, OtherScan.class, TestCash.class, CoinEnvelopes.class, EditRequests.class, ClearHistoryRequests.class).create();
        ActiveAndroid.initialize(dbConfiguration);
        if(TextUtils.isEmpty(Preferences.getString("API_URL",getApplicationContext()))){
            Preferences.saveString("API_URL", "http://10.8.8.134", getApplicationContext());
        }
    }

    @Override
    public void onTerminate() {

        super.onTerminate();
        ActiveAndroid.dispose();
    }

    private static boolean activityVisible = false;

    public static boolean isActivityVisible() {
        return activityVisible;
    }

    public static void activityResumed() {
        activityVisible = true;
    }

    public static void activityPaused() {
        activityVisible = false;
    }

    private static Context currentactvity = null;

    public static Context getCurrentactvity() {
        return currentactvity;
    }

    public static void setCurrentactvity(Context pcurrentactvity) {
        currentactvity = pcurrentactvity;
    }

    public static CertisCISCO instance() {
        return new CertisCISCO();
    }
}

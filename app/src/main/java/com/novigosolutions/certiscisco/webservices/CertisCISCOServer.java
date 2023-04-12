package com.novigosolutions.certiscisco.webservices;

import android.content.Context;

import com.novigosolutions.certiscisco.applications.CertisCISCO;
import com.novigosolutions.certiscisco.utils.Preferences;

import java.util.Random;

public class CertisCISCOServer {

    static Random rand = new Random();
    static String[] IPS = new String[]{"http://10.8.8.134/", "http://10.8.8.165/"};



    public static String getIP(Context context) {
        return Preferences.getString("API_URL",context)+"/";
    }

    public static String getPATH(Context context) {
        return getIP(context) + "api/DeviceApi/";
    }

    public static String getIP() {
        return Preferences.getString("API_URL", CertisCISCO.getCurrentactvity())+"/";
    }

    public static String getPATH() {
        return getIP() + "api/DeviceApi/";
    }
}

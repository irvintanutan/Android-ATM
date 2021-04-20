package com.novigosolutions.certiscisco.webservices;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

import com.novigosolutions.certiscisco.utils.Preferences;

import java.util.Random;

public class CertisCISCOServer {

    static Random rand = new Random();
    static String[] IPS = new String[]{"http://10.8.8.134/", "http://10.8.8.165/"};

//    public static String getIP() {
//        return IPS[rand.nextInt(2)];
//    }
//
//    public static String getPATH() {
//        return IPS[rand.nextInt(2)] + "api/DeviceApi/";
//    }


    public static String getIP(Context context) {
       // return "http://certisciscoatmsystem.novigotest.com/";
//        return "http://certisatmapi.azurewebsites.net/";
        return Preferences.getString("API_URL",context)+"/";
    }

    public static String getPATH(Context context) {
        return getIP(context) + "api/DeviceApi/";
        //return "https://pcs-atmuatapi.certis-cslops-uat.com/" + "api/DeviceApi/";
        //return "http://192.168.1.2:81/" + "api/DeviceApi/";
        //return "http://api.bestoptions.com.ph/" + "api/DeviceApi/";
    }
}

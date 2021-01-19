package com.novigosolutions.certiscisco.utils;

/**
 * Created by dhanrajk on 05-12-17.
 */

import android.content.Context;
import android.os.SystemClock;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class CommonMethods {
    private static DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static DateFormat dateTimeFormat2 = new SimpleDateFormat("dd-MMM-yyyy h:mm:ss a");
    private static DateFormat dateTimeFormat3 = new SimpleDateFormat("yyyy-MMM-dd HH:mm");
    private static DateFormat dateFormat = new SimpleDateFormat("d MMM yyyy");
    private static DateFormat timeFormat = new SimpleDateFormat("K:mm a");

//    public static String getCurrentTime(Context context) {
//        try {
//            Long sinceloggedIn = SystemClock.elapsedRealtime() - Preferences.getLong("sinceLoggedIn", context);
//            return timeFormat.format(dateTimeFormat2.parse(Preferences.getString("LoggedOn", context)).getTime() + sinceloggedIn).toUpperCase();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
//
//    public static String getCurrentDate(Context context) {
//        try {
//            Long sinceloggedIn = SystemClock.elapsedRealtime() - Preferences.getLong("sinceLoggedIn", context);
//            return dateFormat.format(dateTimeFormat2.parse(Preferences.getString("LoggedOn", context)).getTime() + sinceloggedIn);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }

    public static String getCurrentDateTime(Context context) {
        try {
            Long sinceloggedIn = SystemClock.elapsedRealtime() - Preferences.getLong("sinceLoggedIn", context);
            return dateTimeFormat2.format(dateTimeFormat2.parse(Preferences.getString("LoggedOn", context)).getTime() + sinceloggedIn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String getCurrentDateTimeInFormat(Context context) {
        try {
            Long sinceloggedIn = SystemClock.elapsedRealtime() - Preferences.getLong("sinceLoggedIn", context);
            return dateTimeFormat.format(dateTimeFormat2.parse(Preferences.getString("LoggedOn", context)).getTime() + sinceloggedIn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    public static String getCurrentDateTimeInFormat3(Context context) {
        try {
            Long sinceloggedIn = SystemClock.elapsedRealtime() - Preferences.getLong("sinceLoggedIn", context);
            return dateTimeFormat3.format(dateTimeFormat2.parse(Preferences.getString("LoggedOn", context)).getTime() + sinceloggedIn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}

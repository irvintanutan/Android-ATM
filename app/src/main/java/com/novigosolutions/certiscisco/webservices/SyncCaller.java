package com.novigosolutions.certiscisco.webservices;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.novigosolutions.certiscisco.models.Job;
import com.novigosolutions.certiscisco.models.UserLogs;
import com.novigosolutions.certiscisco.utils.Preferences;
import com.novigosolutions.certiscisco.utils.SyncDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by dhanrajk on 23-08-17.
 */

public class SyncCaller {
    Context context;
    String result_body = "";

    public void SyncAuditLogs(final ApiCallback callback, final Context activity) {
        List<UserLogs> userLogs = UserLogs.getUserLogs();
        Log.e("USER LOGS SIZE", Integer.toString(userLogs.size()));
        JsonArray jsonArray = new JsonArray();
        for (UserLogs userLog : userLogs) {
            Log.e("USER LOGS ", body(userLog).toString());
            jsonArray.add(body(userLog));
        }

        Log.e("JsonArray", jsonArray.toString());

        if (!userLogs.isEmpty()) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.connectTimeout(30, TimeUnit.SECONDS);
            httpClient.readTimeout(30, TimeUnit.SECONDS);
            httpClient.writeTimeout(30, TimeUnit.SECONDS);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(CertisCISCOServer.getPATH(activity))
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(UnsafeOkHttpClient.getUnsafeOkHttpClient(httpClient))
                    .build();
            CertisCISCOServices service = retrofit.create(CertisCISCOServices.class);
            Call<ResponseBody> call = service.UserActivityLog(Preferences.getString("AuthToken",
                    activity), Preferences.getInt("UserId", activity), jsonArray);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        int result_code = response.code();
                        if (result_code == 200) {
                            try {
                                result_body = response.body().string();
                                JSONObject obj = new JSONObject(result_body);
                                String result = obj.getString("Result");
                                if (result.equals("Success")) {
                                    UserLogs.remove();
                                    Log.e("AuditLogService", ":" + result_body);
                                }
                                if (callback != null) callback.onResult(result_code, result);
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (callback != null)
                                    callback.onResult(result_code, "Data Error");
                            }

                        } else {
                            if (callback != null)
                                callback.onResult(result_code, "Network Error");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (callback != null) callback.onResult(0, "Data Error");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                    if (callback != null) callback.onResult(0, "Network Error");
                }
            });
        }
    }

    private JsonObject body(UserLogs userLogs) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("entity", userLogs.Entity);
        jsonObject.addProperty("userAction", userLogs.UserAction);
        jsonObject.addProperty("remarks", userLogs.Remarks);
        jsonObject.addProperty("userId", userLogs.UserId);
        jsonObject.addProperty("source", "Apk");
        jsonObject.addProperty("apkdatetime", userLogs.DateTime);
        return jsonObject;
    }

    public void Sync(final ApiCallback callback, final Context activity) {
        Log.e("sync started", "yes");
        context = activity;
        if (!Preferences.getBoolean("LoggedIn", context)) return;
        JsonObject json = new JsonObject();
        try {

            json.addProperty("TeamId", Preferences.getString("teamId", activity));
            json.addProperty("LoginDate", Preferences.getString("LoginDate", activity));
            List<Job> jobList = Job.getAll();
            JsonArray jobarray = new JsonArray();
            for (int i = 0; i < jobList.size(); i++) {
                JsonObject cart = new JsonObject();
                cart.addProperty("OrderId", jobList.get(i).ATMOrderId);
                cart.addProperty("Version", jobList.get(i).Version);
                cart.addProperty("Status", "");
                jobarray.add(cart);
            }
            json.add("Orders", jobarray);
            Log.e("UserId", String.valueOf(Preferences.getInt("UserId", activity)));
            Log.e("AuthToken", Preferences.getString("AuthToken", activity));
            Log.e("synch body", String.valueOf(json));

        } catch (Exception e) {
            e.printStackTrace();
        }

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(30, TimeUnit.SECONDS);
        httpClient.readTimeout(30, TimeUnit.SECONDS);
        httpClient.writeTimeout(30, TimeUnit.SECONDS);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CertisCISCOServer.getPATH(activity))
                .addConverterFactory(GsonConverterFactory.create())
                .client(UnsafeOkHttpClient.getUnsafeOkHttpClient(httpClient))
                .build();
        CertisCISCOServices service = retrofit.create(CertisCISCOServices.class);
        Log.e("GETATMLISTJSON", json.toString());
        Call<ResponseBody> call = service.Sync(Preferences.getString("AuthToken", activity), Preferences.getInt("UserId", activity), json);
        Log.e("TOKENS", Preferences.getString("AuthToken", activity) + " " + Preferences.getInt("UserId", activity));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    int result_code = response.code();
                    Log.e("synch result_code", ":" + result_code);
                    if (result_code == 200) {
                        try {
                            result_body = response.body().string();
                            Log.e("synch result_body", ":" + result_body);
                            JSONObject obj = new JSONObject(result_body);
                            String result = obj.getString("Result");
                            if (result.equals("Success")) {
                                Boolean ischangeindata = false;
                                JSONObject jsonObject = obj.getJSONObject("Data");
                                JSONArray jsonArray = jsonObject.getJSONArray("Orders");
                                Log.e("ERROR IRVIN", jsonArray.toString());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject orderJSONObject = jsonArray.getJSONObject(i);
                                    if (orderJSONObject.getString("Status").equals("Updated") || orderJSONObject.getString("Status").equals("Cancelled") || orderJSONObject.getString("Status").equals("New")) {
                                        ischangeindata = true;
                                        break;
                                    }
                                }
                                if (ischangeindata) {
                                    SyncDatabase.instance().sync(false, jsonArray, context);
                                    if (callback == null) {
                                        Intent intent = new Intent("syncreciverevent");
                                        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
                                    }

                                }

                            }
                            if (callback != null) callback.onResult(result_code, result);
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (callback != null)
                                callback.onResult(result_code, "Data Error");
                        }

                    } else {
                        if (callback != null)
                            callback.onResult(result_code, "Network Error");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null) callback.onResult(0, "Data Error");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                if (callback != null) callback.onResult(0, "Network Error");
            }
        });

    }

    public static SyncCaller instance() {
        return new SyncCaller();
    }
}

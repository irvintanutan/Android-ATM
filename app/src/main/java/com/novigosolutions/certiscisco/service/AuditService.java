package com.novigosolutions.certiscisco.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.gson.JsonObject;
import com.novigosolutions.certiscisco.models.UserLogs;
import com.novigosolutions.certiscisco.webservices.CertisCISCOServer;
import com.novigosolutions.certiscisco.webservices.CertisCISCOServices;
import com.novigosolutions.certiscisco.webservices.UnsafeOkHttpClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by irvin
 */

public class AuditService extends IntentService {

    public AuditService() {
        super("Audit Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        uploadLogs();
    }

    private void uploadLogs() {
        UserLogs.removeUserLogs();
        List<UserLogs> userLogs = UserLogs.getUserLogs();
        Log.e("USER LOGS SIZE", Integer.toString(userLogs.size()));
        for (UserLogs userLog : userLogs) {
            Log.e("USER LOGS ", body(userLog).toString());
            Call<ResponseBody> call = getService().UserActivityLog(userLog.Entity, userLog.UserAction, userLog.Remarks,
                    Integer.parseInt(userLog.UserId), "APK", userLog.DateTime);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.code() == 200 || response.code() == 204) {
                            UserLogs.updateUserLogs(userLog.DateTime);
                            Log.e("Success", "Audit Logs " + userLog.toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("Failed", "Audit Logs " + userLog.toString());
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


    private CertisCISCOServices getService() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(200, TimeUnit.SECONDS);
        httpClient.readTimeout(200, TimeUnit.SECONDS);
        httpClient.writeTimeout(200, TimeUnit.SECONDS);
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        Retrofit retrofit = new Retrofit.Builder()
                //.client(client)
                .baseUrl(CertisCISCOServer.getPATH())
                .addConverterFactory(GsonConverterFactory.create())
                .client(UnsafeOkHttpClient.getUnsafeOkHttpClient(httpClient))
                .build();
        return retrofit.create(CertisCISCOServices.class);
    }
}
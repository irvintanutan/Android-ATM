package com.novigosolutions.certiscisco.webservices;

import android.content.Context;
import android.util.Log;

import com.novigosolutions.certiscisco.utils.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by dhanrajk on 21-07-17.
 */

public class ATMListCaller {
    public void getATMList(Context context,final ApiCallback callback, String token, int UserId, int teamId, String logindate) {
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(30, TimeUnit.SECONDS);
        httpClient.readTimeout(30, TimeUnit.SECONDS);
        httpClient.writeTimeout(30, TimeUnit.SECONDS);
//        httpClient.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CertisCISCOServer.getPATH(context))
                .client(UnsafeOkHttpClient.getUnsafeOkHttpClient(httpClient))
                .build();
        CertisCISCOServices service = retrofit.create(CertisCISCOServices.class);
        Log.e("PAYLOAD" , token + " " + UserId + " " + teamId + " " + logindate);
        Call<ResponseBody> call = service.GetList(token, UserId, teamId,logindate);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    int result_code = response.code();
                    Constants.requestBody = response.body().string();
                    Log.e("result_code", ":" + result_code);
                    Log.e("result_messege", ":" + response.message());
                    Log.e("result_error", ":" + response.errorBody());
                    Log.e("result_body" , Constants.requestBody);
                    callback.onResult(result_code, response.body().string());

                } catch (Exception e) {
                    e.printStackTrace();
                    callback.onResult(0, "Data Error");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                callback.onResult(0, "Network Error");
            }
        });

    }

    public static ATMListCaller instance() {
        return new ATMListCaller();
    }
}

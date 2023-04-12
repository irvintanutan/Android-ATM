package com.novigosolutions.certiscisco.webservices;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface CertisCISCOServices {

    @GET("api")
    Call<ResponseBody> ping();

    @GET("GetAndroidAppVersion")
    Call<ResponseBody> chekUpdate();

    @Headers("Content-Type: application/json")
    @POST("ATMLogin")
    Call<ResponseBody> signin(@Body JsonObject object);

    @POST("GetATMScheduledList")
    Call<ResponseBody> GetList(@Header("AuthToken") String AuthToken, @Header("UserId") int UserId, @Query("teamId") int teamId, @Query("LoginDate") String LoginDate);
 
    @Headers("Content-Type: application/json")
    @POST("SubmitReplenishment")
    Call<ResponseBody> SubmitReplenishment(@Header("AuthToken") String AuthToken, @Header("UserId") int UserId, @Body JsonObject jsonObject);

    @Headers("Content-Type: application/json")
    @POST("SubmitReplenishmentBulk")
    Call<ResponseBody> SubmitOfflineReplenishment(@Header("AuthToken") String AuthToken, @Header("UserId") int UserId, @Body JsonArray jsonObject);

    @Headers("Content-Type: application/json")
    @POST("ATMOrderCheckVersions")
    Call<ResponseBody> Sync(@Header("AuthToken") String AuthToken, @Header("UserId") int UserId, @Body JsonObject object);

    @POST("certishub/sendUpdates")
    Call<ResponseBody> sendUpdates(@Query("Page") String Page, @Query("machine") String machine);

    @POST("RequestForEdit")
    Call<ResponseBody> requestForEdit(@Header("AuthToken") String AuthToken, @Header("UserId") int UserId, @Query("module") String module, @Query("requestType") String requestType, @Query("requestedBy") String requestedBy, @Query("requestedOn") String requestedOn);

    @POST("CheckRequestStatusBulk")
    Call<ResponseBody> requestStatus(@Header("AuthToken") String AuthToken, @Header("UserId") int UserId, @Body String[] RequestIds);

//    @GET("Content/ATM.apk")
//    @Streaming
//    Call<ResponseBody> downloadFile();

    @GET("download/File/ATM_App.apk")
    @Streaming
    Call<ResponseBody> downloadFile();

    @Headers("Content-Type: application/json")
    @POST("UserActivityLog/LogUserActivity")
    Call<ResponseBody> UserActivityLog(@Query("entity") String entity, @Query("userAction") String userAction,
                                       @Query("remarks") String remark,
                                       @Query("userId") int userId, @Query("source") String source,
                                       @Query("apkdatetime") String apkdatetime);
}

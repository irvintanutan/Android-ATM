package com.novigosolutions.certiscisco.webservices;

import android.content.Context;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.novigosolutions.certiscisco.models.Job;
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

        //        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(30, TimeUnit.SECONDS);
        httpClient.readTimeout(30, TimeUnit.SECONDS);
        httpClient.writeTimeout(30, TimeUnit.SECONDS);
//        httpClient.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CertisCISCOServer.getPATH(activity))
                .addConverterFactory(GsonConverterFactory.create())
                .client(UnsafeOkHttpClient.getUnsafeOkHttpClient(httpClient))
                .build();
        CertisCISCOServices service = retrofit.create(CertisCISCOServices.class);
        Call<ResponseBody> call = service.Sync(Preferences.getString("AuthToken", activity), Preferences.getInt("UserId", activity), json);
        Log.e("TOKENS" ,Preferences.getString("AuthToken", activity) + " " + Preferences.getInt("UserId", activity));
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
                                Log.e("ERROR IRVIN" , jsonArray.toString());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject orderJSONObject = jsonArray.getJSONObject(i);
                                    Log.e("ORDER INFO" , orderJSONObject.getJSONObject("OrderInfo").toString());
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

//    private void alert() {
//        if (context != null) {
//            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
//            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    Intent i = new Intent(context, HomeActivity.class);
//                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    context.startActivity(i);
//                }
//            });
//            AlertDialog alertDialog = alertDialogBuilder.create();
//            alertDialog.setCancelable(false);
//            alertDialog.setTitle("Job Updated");
//            alertDialog.setMessage("This job is updated from the server");
//            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//            alertDialog.show();
//        }
//    }

//    public void synclocaldata() {
//        try {
//            Boolean isjobonprogress = false;
//            JSONObject obj = new JSONObject(result_body);
//            JSONObject jsonObject = obj.getJSONObject("Data");
//            JSONArray jsonArray = jsonObject.getJSONArray("Orders");
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject orderJSONObject = jsonArray.getJSONObject(i);
//                if (orderJSONObject.getString("Status").equals("Updated") || orderJSONObject.getString("Status").equals("New")) {
//                    Boolean addemptyunloading = false;
//                    Job.removeSingle(orderJSONObject.getInt("OrderId"));
//                    JSONObject jobsJSONObject = orderJSONObject.getJSONObject("OrderInfo");
//                    int ATMOrderId = jobsJSONObject.getInt("ATMOrderId");
//                    Job job = Job.getSingle(ATMOrderId);
//                    if (job == null)
//                        job = new Job();
//                    job.ATMOrderId = ATMOrderId;
//                    job.ATMMasterId = jobsJSONObject.getInt("ATMMasterId");
//                    job.OrderType = jobsJSONObject.getString("OrderType");
//                    job.OrderMode = jobsJSONObject.getString("OrderMode");
//                    job.ATMCode = jobsJSONObject.getString("ATMCode");
//                    job.ATMTypeCode = jobsJSONObject.getString("ATMTypeCode");
//                    job.Bank = jobsJSONObject.getString("Bank");
//                    job.ATMType = jobsJSONObject.getString("ATMType");
//                    job.Version = jobsJSONObject.getString("Version");
//                    job.OperationMode = jobsJSONObject.getString("OperationMode");
//                    job.DeploymentNo = jobsJSONObject.getString("DeploymentNo");
//                    job.Status = jobsJSONObject.getString("Status");
//                    job.Location = jobsJSONObject.getString("Location");
//                    job.Zone = jobsJSONObject.getString("Zone");
//                    job.save();
//
//                    try {
//                        JSONArray unloadingcartridges = jobsJSONObject.getJSONArray("UnloadingCart");
//                        if (jobsJSONObject.getString("OperationMode").equals("LOAD&UNLOAD") && (unloadingcartridges == null || unloadingcartridges.length() == 0)) {
//                            addemptyunloading = true;
//                            Job.clearHistory(ATMOrderId, "UNLOAD");
//                        }
//                        for (int j = 0; j < unloadingcartridges.length(); j++) {
//                            JSONObject cartridgeJsonObject = unloadingcartridges.getJSONObject(j);
//                            int CartId = cartridgeJsonObject.getInt("CartId");
//                            Cartridge cartridge = Cartridge.getSingle(ATMOrderId, "UNLOAD", CartId);
//                            if (cartridge == null)
//                                cartridge = new Cartridge();
//                            cartridge.CartId = CartId;
//                            cartridge.ATMOrderId = ATMOrderId;
//                            cartridge.CartType = "UNLOAD";
//                            cartridge.SerialNo = cartridgeJsonObject.getString("SerialNo").toUpperCase();
//                            cartridge.Deno = cartridgeJsonObject.getString("Deno");
//                            cartridge.CartNo = cartridgeJsonObject.getString("CartNo");
//                            cartridge.save();
//                            JSONArray seals = cartridgeJsonObject.getJSONArray("SealNo");
//                            for (int k = 0; k < seals.length(); k++) {
//                                Seal seal = Seal.getSingle(ATMOrderId, CartId, "UNLOAD", seals.getString(k));
//                                if (seal == null)
//                                    seal = new Seal();
//                                seal.CartId = CartId;
//                                seal.ATMOrderId = ATMOrderId;
//                                seal.CartType = "UNLOAD";
//                                seal.SealNo = seals.getString(k).toUpperCase();
//                                seal.isScanned = 0;
//                                seal.save();
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        JSONArray loadingcartridges = jobsJSONObject.getJSONArray("LoadingCart");
//                        for (int j = 0; j < loadingcartridges.length(); j++) {
//                            JSONObject cartridgeJsonObject = loadingcartridges.getJSONObject(j);
//                            int CartId = cartridgeJsonObject.getInt("CartId");
//                            Cartridge cartridge = Cartridge.getSingle(ATMOrderId, "LOAD", CartId);
//                            if (cartridge == null)
//                                cartridge = new Cartridge();
//                            cartridge.CartId = CartId;
//                            cartridge.ATMOrderId = ATMOrderId;
//                            cartridge.CartType = "LOAD";
//                            cartridge.SerialNo = cartridgeJsonObject.getString("SerialNo").toUpperCase();
//                            cartridge.Deno = cartridgeJsonObject.getString("Deno");
//                            cartridge.CartNo = cartridgeJsonObject.getString("CartNo");
//                            cartridge.isScanCompleted = 0;
//                            cartridge.isScanned = 0;
//                            cartridge.save();
//                            JSONArray seals = cartridgeJsonObject.getJSONArray("SealNo");
//                            for (int k = 0; k < seals.length(); k++) {
//                                Seal seal = Seal.getSingle(ATMOrderId, CartId, "LOAD", seals.getString(k));
//                                if (seal == null)
//                                    seal = new Seal();
//
//                                seal.CartId = CartId;
//                                seal.ATMOrderId = ATMOrderId;
//                                seal.CartType = "LOAD";
//                                seal.SealNo = seals.getString(k).toUpperCase();
//                                seal.isScanned = 0;
//                                seal.save();
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else if (orderJSONObject.getString("Status").equals("Cancelled")) {
//                    Job.removeSingle(orderJSONObject.getInt("OrderId"));
//                }
//                if (orderJSONObject.getInt("OrderId") == Preferences.getInt("PROGRESSJOBID", context)) {
//                    isjobonprogress = true;
//                }
//            }
//            if (isjobonprogress) {
//                alert();
//            } else {
//                if (!isAppIsInBackground(context)) {
//                    Toast.makeText(context, "Job Synced", Toast.LENGTH_SHORT).show();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

//    private boolean isAppIsInBackground(Context context) {
//        boolean isInBackground = true;
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
//            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
//            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
//                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
//                    for (String activeProcess : processInfo.pkgList) {
//                        if (activeProcess.equals(context.getPackageName())) {
//                            isInBackground = false;
//                        }
//                    }
//                }
//            }
//        } else {
//            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
//            if (!taskInfo.isEmpty()) {
//                ComponentName componentInfo = taskInfo.get(0).topActivity;
//                if (componentInfo.getPackageName().equals(context.getPackageName())) {
//                    isInBackground = false;
//                    if (CertisCISCO.getCurrentactvity() != null && CertisCISCO.getCurrentactvity() instanceof HomeActivity) {
//                        ((HomeActivity) CertisCISCO.getCurrentactvity()).refresh();
//                    }
//                }
//            }
//        }
//
//        return isInBackground;
//    }
}

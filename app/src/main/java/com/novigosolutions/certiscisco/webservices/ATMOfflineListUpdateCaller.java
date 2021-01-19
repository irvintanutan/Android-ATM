package com.novigosolutions.certiscisco.webservices;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.models.Cartridge;
import com.novigosolutions.certiscisco.models.Job;
import com.novigosolutions.certiscisco.models.OtherScan;
import com.novigosolutions.certiscisco.models.Seal;
import com.novigosolutions.certiscisco.models.TestCash;
import com.novigosolutions.certiscisco.utils.Preferences;

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
 * Created by dhanrajk on 21-07-17.
 */

public class ATMOfflineListUpdateCaller {
    public void UpdateATMList(final OfflineCallback callback, final Context activity) {
        JsonArray jsonArray = new JsonArray();
        List<Job> offlineList = Job.getOfflinelist();
        for (int k = 0; k < offlineList.size(); k++) {

            try {
                JsonObject json = new JsonObject();
                Job job = offlineList.get(k);
                int ATMOrderId = job.ATMOrderId;
                json.addProperty("ATMOrderId", ATMOrderId);
                json.addProperty("ATMMasterId", job.ATMMasterId);
                json.addProperty("ATMTypeCode", job.ATMTypeCode);
                json.addProperty("ATMCode", job.ATMCode);
                json.addProperty("Location", job.Location);
                json.addProperty("Zone", job.Zone);
                json.addProperty("Status", job.Status);
                if(job.OperationMode.equals("LOAD&UNLOAD"))
                {
                    json.addProperty("OperationMode","UNLOAD&LOAD");
                }
                else
                {
                    json.addProperty("OperationMode", job.OperationMode);
                }
                json.addProperty("DeploymentNo", job.DeploymentNo);
                json.addProperty("StartDate", job.StartDate);
                json.addProperty("EndDate", job.EndDate);
                json.addProperty("ElapsedDeliveryTime", job.Duration);
                json.addProperty("UserId", Preferences.getInt("UserId", activity));
                List<Cartridge> loadingcartridgeList = Cartridge.get(ATMOrderId, activity.getString(R.string.load));
                JsonArray loadingCart = new JsonArray();
                for (int i = 0; i < loadingcartridgeList.size(); i++) {
                    JsonObject cart = new JsonObject();
                    cart.addProperty("CartId", loadingcartridgeList.get(i).CartId);
                    cart.addProperty("ATMOrderId", loadingcartridgeList.get(i).ATMOrderId);
                    cart.addProperty("CartType", loadingcartridgeList.get(i).CartType);
                    cart.addProperty("SerialNo", loadingcartridgeList.get(i).SerialNo);
                    cart.addProperty("CartNo", loadingcartridgeList.get(i).CartNo);
                    List<Seal> sealList = Seal.get(loadingcartridgeList.get(i).ATMOrderId, loadingcartridgeList.get(i).CartId, loadingcartridgeList.get(i).CartType);
                    JsonArray sealarray = new JsonArray();
                    for (int j = 0; j < sealList.size(); j++) {
                        sealarray.add(sealList.get(j).SealNo);
                    }
                    cart.add("SealNo", sealarray);
                    loadingCart.add(cart);
                }
                json.add("LoadingCart", loadingCart);
                List<Cartridge> unloadingcartridgeList = Cartridge.get(ATMOrderId, activity.getString(R.string.unload));
                JsonArray unloadingCart = new JsonArray();
                for (int i = 0; i < unloadingcartridgeList.size(); i++) {
                    JsonObject cart = new JsonObject();
                    cart.addProperty("CartId", unloadingcartridgeList.get(i).CartId);
                    cart.addProperty("ATMOrderId", unloadingcartridgeList.get(i).ATMOrderId);
                    cart.addProperty("CartType", unloadingcartridgeList.get(i).CartType);
                    cart.addProperty("SerialNo", unloadingcartridgeList.get(i).SerialNo);
                    cart.addProperty("CartNo", unloadingcartridgeList.get(i).CartNo);
                    List<Seal> sealList = Seal.get(unloadingcartridgeList.get(i).ATMOrderId, unloadingcartridgeList.get(i).CartId, unloadingcartridgeList.get(i).CartType);
                    JsonArray sealarray = new JsonArray();
                    for (int j = 0; j < sealList.size(); j++) {
                        sealarray.add(sealList.get(j).SealNo);
                    }
                    cart.add("SealNo", sealarray);
                    unloadingCart.add(cart);
                }
                json.add("UnloadingCart", unloadingCart);
                JsonArray unloadingEnvelops = new JsonArray();
                List<OtherScan> OtherScanList = OtherScan.get(ATMOrderId);
                for (int i = 0; i < OtherScanList.size(); i++) {
                    JsonObject otherscan = new JsonObject();
                    otherscan.addProperty("ItemType", OtherScanList.get(i).ScanType);
                    otherscan.addProperty("Barcode", OtherScanList.get(i).ScanValue);
                    otherscan.addProperty("Remarks", "");
                    unloadingEnvelops.add(otherscan);
                }
                json.add("UnloadingEnvelops", unloadingEnvelops);

                JsonArray loadingEnvelops = new JsonArray();
                List<TestCash> TestCashList = TestCash.get(ATMOrderId);
                for (int i = 0; i < TestCashList.size(); i++) {
                    JsonObject testcash = new JsonObject();
                    testcash.addProperty("ItemType", TestCashList.get(i).ScanType);
                    testcash.addProperty("Barcode", TestCashList.get(i).ScanValue);
                    testcash.addProperty("Remarks", "");
                    loadingEnvelops.add(testcash);
                }
                json.add("LoadingEnvelops", loadingEnvelops);
                Log.e("updatedjson", json.toString());
                jsonArray.add(json);
            } catch (Exception e) {
                e.printStackTrace();
            }

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
        Call<ResponseBody> call = service.SubmitOfflineReplenishment(Preferences.getString("AuthToken", activity), Preferences.getInt("UserId", activity), jsonArray);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    int result_code = response.code();
                    String result_body = response.body().string();
                    Log.e("upresult_code", ":" + result_code);
                    Log.e("upresult_messege", ":" + response.message());
//                    Log.e("upresult_body", ":" + response.body().string());
//                    Log.e("upresult_error", ":" + response.errorBody());
                    if(result_code==200)
                    {
                        Boolean isupdated=false;
                        JSONArray resultArray=new JSONArray(result_body);
                        for (int i=0;i<resultArray.length();i++) {
                            JSONObject obj = resultArray.getJSONObject(i);
                            String result = obj.getString("Result");
                            if(result.equals("Success"))
                            {
                                Job.setOffLineUpdated(obj.getInt("JobId"));
                                isupdated=true;
                            }
                        }
                        if (isupdated) {
                            if (callback != null) {
                                callback.onOfflineUpdated(result_code, "Updated");
                            }
                            else
                            {
                                Intent intent = new Intent("offlinereciverevent");
                                LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
                            }
                        }
                        else
                        {
                            if (callback != null)
                                callback.onOfflineUpdated(result_code, "Success");
                        }
                    }
                    else {
                        if (callback != null)
                            callback.onOfflineUpdated(result_code, "Network Error");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if(callback!=null)callback.onOfflineUpdated(0, "Data Error");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                if(callback!=null)callback.onOfflineUpdated(0, "Network Error");
            }
        });

    }

    public static ATMOfflineListUpdateCaller instance() {
        return new ATMOfflineListUpdateCaller();
    }
}

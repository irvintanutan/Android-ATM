package com.novigosolutions.certiscisco.webservices;

import android.app.Activity;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.activities.BaseActivity;
import com.novigosolutions.certiscisco.models.Cartridge;
import com.novigosolutions.certiscisco.models.Denomination;
import com.novigosolutions.certiscisco.models.FLMSLMAdditionalDetails;
import com.novigosolutions.certiscisco.models.FLMSLMScan;
import com.novigosolutions.certiscisco.models.Job;
import com.novigosolutions.certiscisco.models.OtherScan;
import com.novigosolutions.certiscisco.models.Seal;
import com.novigosolutions.certiscisco.models.TestCash;
import com.novigosolutions.certiscisco.utils.Preferences;

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

public class ATMListUpdateCaller {
    public void UpdateATMList(final ApiCallback callback, final Activity activity, int ATMOrderId) {
        JsonObject json = new JsonObject();
        try {
            ((BaseActivity) activity).showProgressDialog("Updating...");
            Job job = Job.getSingle(ATMOrderId);
            json.addProperty("ATMOrderId", job.ATMOrderId);
            json.addProperty("ATMMasterId", job.ATMMasterId);
            json.addProperty("ATMTypeCode", job.ATMTypeCode);
            json.addProperty("ATMCode", job.ATMCode);
            json.addProperty("Location", job.Location);
            json.addProperty("Zone", job.Zone);
            json.addProperty("Status", job.Status);
            json.addProperty("OperationMode", job.OperationMode);
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

            JsonArray FlmSlmEnvelop = new JsonArray();
            List<FLMSLMScan> FLMSLMEnvelope = FLMSLMScan.get(ATMOrderId);
            for (int i = 0; i < FLMSLMEnvelope.size(); i++) {
                JsonObject envelope = new JsonObject();
                envelope.addProperty("ItemType", FLMSLMEnvelope.get(i).ScanType);
                envelope.addProperty("Barcode", FLMSLMEnvelope.get(i).ScanValue);
                envelope.addProperty("Remarks", "");
                FlmSlmEnvelop.add(envelope);
            }
            String operationEnvelope = job.OperationMode.equals("FLM") ? "FLMEnvelops" : "SLMEnvelops";
            if (operationEnvelope.equals("FLMEnvelops")) {
                json.add(operationEnvelope, FlmSlmEnvelop);
                json.add("SLM", new JsonObject());
            } else {
                json.add(operationEnvelope, FlmSlmEnvelop);
                json.add("FLM", new JsonObject());
            }



            Log.e("updatedjson", json.toString());

            JsonObject flmDetails = new JsonObject();
            JsonObject denomination = new JsonObject();
            JsonObject summary = new JsonObject();

            Denomination denom = Denomination.getSingle(ATMOrderId);
            denomination.addProperty("_1000" , denom.text1000);
            denomination.addProperty("_100" , denom.text100);
            denomination.addProperty("_10" , denom.text10);
            denomination.addProperty("_1" , denom.text1);
            denomination.addProperty("_2" , denom.text2);
            denomination.addProperty("_5" , denom.text5);
            denomination.addProperty("_50" , denom.text50);
            denomination.addProperty("_05" , denom.text0_50);
            denomination.addProperty("_02" , denom.text0_20);
            denomination.addProperty("_01" , denom.text0_10);
            denomination.addProperty("_005" , denom.text0_05);
            denomination.addProperty("HighReject" , denom.HighReject);
            denomination.addProperty("NoCashFound" , denom.NoCashFound);

            flmDetails.add("Denominations" , denomination);

            FLMSLMAdditionalDetails details = FLMSLMAdditionalDetails.getSingle(ATMOrderId);
            summary.addProperty("Type" , details.FaultType);
            summary.addProperty("FaultFound" , details.FaultFound);
            summary.addProperty("Resolution" , details.Resolution);
            summary.addProperty("StaffName" , details.StaffName);
            summary.addProperty("FLMTeamArrivalTime" , details.TeamArrivalTime);
            summary.addProperty("EngineerArrivalTime" , details.EngineerArrivalTime);
            summary.addProperty("AdditionalRemarks" , details.AdditionalRemarks);
            summary.addProperty("SLMRequired" , details.SLMRequired);
            flmDetails.add("Summary" , summary);

            json.add("FLMSLMDetails" , flmDetails);
            Log.e("updatedjson", json.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(30, TimeUnit.SECONDS);
        httpClient.readTimeout(30, TimeUnit.SECONDS);
        httpClient.writeTimeout(30, TimeUnit.SECONDS);

        Log.e("JSON" , json.toString());

//        httpClient.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CertisCISCOServer.getPATH(activity))
                .client(UnsafeOkHttpClient.getUnsafeOkHttpClient(httpClient))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        CertisCISCOServices service = retrofit.create(CertisCISCOServices.class);
        Call<ResponseBody> call = service.SubmitReplenishment(Preferences.getString("AuthToken", activity), Preferences.getInt("UserId", activity), json);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    ((BaseActivity) activity).hideProgressDialog();
                    int result_code = response.code();
                    Log.e("upresult_code", ":" + result_code);
                    Log.e("upresult_messege", ":" + response.message());
                    Log.e("upresult_error", ":" + response.errorBody());
                    if (result_code == 200) {
                        callback.onResult(result_code, response.body().string());
                    } else if (result_code == 409) {
                        ((BaseActivity) activity).authalert(activity);
                    }
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

    public static ATMListUpdateCaller instance() {
        return new ATMListUpdateCaller();
    }
}

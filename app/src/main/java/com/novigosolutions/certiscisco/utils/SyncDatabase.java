package com.novigosolutions.certiscisco.utils;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.WindowManager;
import android.widget.Toast;

import com.novigosolutions.certiscisco.activities.HomeActivity;
import com.novigosolutions.certiscisco.applications.CertisCISCO;
import com.novigosolutions.certiscisco.models.Cartridge;
import com.novigosolutions.certiscisco.models.CoinEnvelopes;
import com.novigosolutions.certiscisco.models.Job;
import com.novigosolutions.certiscisco.models.Seal;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by dhanrajk on 21-07-17.
 */

public class SyncDatabase {
    public void sync(Boolean islogindata, JSONArray jobs, Context context) {

        Boolean isjobonprogress = false;
        for (int i = 0; i < jobs.length(); i++) {
            try {
                JSONObject jobsJSONObject = null;
                if (islogindata) {
                    jobsJSONObject = jobs.getJSONObject(i);
                } else {
                    JSONObject orderJSONObject = jobs.getJSONObject(i);
                    if (orderJSONObject.getString("Status").equals("Updated")) {
                        Job.removeSingle(orderJSONObject.getInt("OrderId"));
                        jobsJSONObject = orderJSONObject.getJSONObject("OrderInfo");
                        if (orderJSONObject.getInt("OrderId") == Preferences.getInt("PROGRESSJOBID", context)) {
                            isjobonprogress = true;
                        }
                    } else if (orderJSONObject.getString("Status").equals("New")) {
                        Job.removeSingle(orderJSONObject.getInt("OrderId"));
                        jobsJSONObject = orderJSONObject.getJSONObject("OrderInfo");
                    } else if (orderJSONObject.getString("Status").equals("Cancelled")) {
                        Job.removeSingle(orderJSONObject.getInt("OrderId"));
                    }
                }
                if (jobsJSONObject != null) {
                    Boolean addemptyunloading = false;
                    Job job = new Job();
                    int ATMOrderId = jobsJSONObject.getInt("ATMOrderId");
                    job.ATMOrderId = ATMOrderId;
                    job.ATMMasterId = jobsJSONObject.getInt("ATMMasterId");
                    job.OrderType = jobsJSONObject.getString("OrderType");
                    job.OrderMode = jobsJSONObject.getString("OrderMode");
                    job.ATMCode = jobsJSONObject.getString("ATMCode");
                    job.ATMTypeCode = jobsJSONObject.getString("ATMTypeCode");
                    job.Bank = jobsJSONObject.getString("Bank");
                    job.ATMType = jobsJSONObject.getString("ATMType");
                    job.Version = jobsJSONObject.getString("Version");
                    job.OperationMode = jobsJSONObject.getString("OperationMode");
                    job.DeploymentNo = jobsJSONObject.getString("DeploymentNo");
                    job.Status = jobsJSONObject.getString("Status");
                    job.Location = jobsJSONObject.getString("Location");
                    job.Zone = jobsJSONObject.getString("Zone");
                    job.save();
                    try {
                        JSONArray unloadingcartridges = jobsJSONObject.getJSONArray("UnloadingCart");
                        if (jobsJSONObject.getString("OperationMode").equals("UNLOAD&LOAD") && (unloadingcartridges == null || unloadingcartridges.length() == 0)) {
                            addemptyunloading = true;
                            Job.clearHistory(ATMOrderId, "UNLOAD");
                        }
                        Boolean isUnloadingEmpty=true;
                        for (int j = 0; j < unloadingcartridges.length(); j++) {
                            JSONObject cartridgeJsonObject = unloadingcartridges.getJSONObject(j);
                            int CartId = cartridgeJsonObject.getInt("CartId");
                            Cartridge cartridge = Cartridge.getSingle(ATMOrderId, "UNLOAD", CartId);
                            if (cartridge == null)
                                cartridge = new Cartridge();
                            cartridge.CartId = CartId;
                            cartridge.ATMOrderId = ATMOrderId;
                            cartridge.CartType = "UNLOAD";
                            cartridge.SerialNo = cartridgeJsonObject.getString("SerialNo").toUpperCase();
                            cartridge.Deno = cartridgeJsonObject.getString("Deno");
                            cartridge.CartNo = cartridgeJsonObject.getString("CartNo");
                            cartridge.save();
                            if(!cartridgeJsonObject.getString("SerialNo").isEmpty())isUnloadingEmpty=false;
                            JSONArray seals = cartridgeJsonObject.getJSONArray("SealNo");
                            for (int k = 0; k < seals.length(); k++) {
                                Seal seal=null;
                                if (!seals.getString(k).isEmpty())
                                    seal = Seal.getSingle(ATMOrderId, CartId, "UNLOAD", seals.getString(k));
                                if (seal == null)
                                    seal = new Seal();
                                seal.CartId = CartId;
                                seal.ATMOrderId = ATMOrderId;
                                seal.CartType = "UNLOAD";
                                seal.SealNo = seals.getString(k).toUpperCase();
                                seal.isScanned = 0;
                                seal.save();
                                if(!seals.getString(k).isEmpty())isUnloadingEmpty=false;
                            }
                        }
                        if(isUnloadingEmpty)Job.clearHistory(ATMOrderId, "UNLOAD");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONArray loadingcartridges = jobsJSONObject.getJSONArray("LoadingCart");
                        for (int j = 0; j < loadingcartridges.length(); j++) {
                            JSONObject cartridgeJsonObject = loadingcartridges.getJSONObject(j);
                            int CartId = cartridgeJsonObject.getInt("CartId");
                            Cartridge cartridge = Cartridge.getSingle(ATMOrderId, "LOAD", CartId);
                            if (cartridge == null)
                                cartridge = new Cartridge();
                            cartridge.CartId = CartId;
                            cartridge.ATMOrderId = ATMOrderId;
                            cartridge.CartType = "LOAD";
                            cartridge.SerialNo = cartridgeJsonObject.getString("SerialNo").toUpperCase();
                            cartridge.Deno = cartridgeJsonObject.getString("Deno");
                            cartridge.DuffleSeal = cartridgeJsonObject.getString("DuffleSeal");
                            cartridge.CartNo = cartridgeJsonObject.getString("CartNo");
                            cartridge.isScanCompleted = 0;
                            cartridge.isScanned = 0;
                            cartridge.save();
                            if (addemptyunloading) {
                                Cartridge subcartridge = new Cartridge();
                                subcartridge.CartId = CartId;
                                subcartridge.ATMOrderId = ATMOrderId;
                                subcartridge.CartType = "UNLOAD";
                                subcartridge.SerialNo = "";
                                subcartridge.CartNo = cartridgeJsonObject.getString("CartNo");
                                subcartridge.save();
                            }
                            JSONArray seals = cartridgeJsonObject.getJSONArray("SealNo");
                            for (int k = 0; k < seals.length(); k++) {
                                Seal seal = Seal.getSingle(ATMOrderId, CartId, "LOAD", seals.getString(k));
                                if (seal == null)
                                    seal = new Seal();
                                seal.CartId = CartId;
                                seal.ATMOrderId = ATMOrderId;
                                seal.CartType = "LOAD";
                                seal.SealNo = seals.getString(k).toUpperCase();
                                seal.isScanned = 0;
                                seal.save();
                                if (addemptyunloading) {
                                    Seal emptyseal = new Seal();
                                    emptyseal.CartId = CartId;
                                    emptyseal.ATMOrderId = ATMOrderId;
                                    emptyseal.CartType = "UNLOAD";
                                    emptyseal.SealNo = "";
                                    emptyseal.isScanned = 0;
                                    emptyseal.save();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (!jobsJSONObject.getString("OperationMode").equals("UNLOAD")) {
                            JSONArray coinEnvelopes = jobsJSONObject.getJSONArray("CoinEnvelopes");
                            for (int j = 0; j < coinEnvelopes.length(); j++) {
                                if (!coinEnvelopes.getString(j).isEmpty()) {
                                    CoinEnvelopes envelopes = new CoinEnvelopes();
                                    envelopes.ATMOrderId = ATMOrderId;
                                    envelopes.CoinEnvelope = coinEnvelopes.getString(j);
                                    envelopes.save();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!islogindata) {
            if (isjobonprogress) {
                alert(context);
            } else {
                if (!isAppIsInBackground(context)) {
                    Toast.makeText(context, "Job Synced", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void alert(final Context context) {
        if (context != null) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(context, HomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(i);
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.setTitle("Job Updated");
            alertDialog.setMessage("This job is updated from the server");
            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            alertDialog.show();
        }
    }

    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            if (!taskInfo.isEmpty()) {
                ComponentName componentInfo = taskInfo.get(0).topActivity;
                if (componentInfo.getPackageName().equals(context.getPackageName())) {
                    isInBackground = false;
                    if (CertisCISCO.getCurrentactvity() != null && CertisCISCO.getCurrentactvity() instanceof HomeActivity) {
                        ((HomeActivity) CertisCISCO.getCurrentactvity()).refresh();
                    }
                }
            }
        }

        return isInBackground;
    }

    public static SyncDatabase instance() {
        return new SyncDatabase();
    }
}

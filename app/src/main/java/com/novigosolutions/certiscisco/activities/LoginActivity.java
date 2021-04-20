package com.novigosolutions.certiscisco.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.interfaces.NetworkChangekListener;
import com.novigosolutions.certiscisco.models.Cartridge;
import com.novigosolutions.certiscisco.models.ClearHistoryRequests;
import com.novigosolutions.certiscisco.models.CoinEnvelopes;
import com.novigosolutions.certiscisco.models.EditRequests;
import com.novigosolutions.certiscisco.models.Job;
import com.novigosolutions.certiscisco.models.OtherScan;
import com.novigosolutions.certiscisco.models.Seal;
import com.novigosolutions.certiscisco.models.TestCash;
import com.novigosolutions.certiscisco.recivers.NetworkChangeReceiver;
import com.novigosolutions.certiscisco.utils.Constants;
import com.novigosolutions.certiscisco.utils.NetworkUtil;
import com.novigosolutions.certiscisco.utils.Preferences;
import com.novigosolutions.certiscisco.utils.SyncDatabase;
import com.novigosolutions.certiscisco.webservices.ATMListCaller;
import com.novigosolutions.certiscisco.webservices.ApiCallback;
import com.novigosolutions.certiscisco.webservices.CertisCISCOServer;
import com.novigosolutions.certiscisco.webservices.CertisCISCOServices;
import com.novigosolutions.certiscisco.webservices.UnsafeOkHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * LoginActivity.java - class that loads first.
 *
 * @author dhanrajk
 * @version 1.0
 * @compmany novigosolutions
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, ApiCallback, NetworkChangekListener {
    EditText edtteamid, edtpassword;
    Button btn_login, btn_clear;
    TextView deviceid;
    TextInputLayout mtxtinUserid, mtxtinPassword;
    ImageView imgnetwork;
    Spinner mspindate;
    CoordinatorLayout clv;
    AlertDialog dialog;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setuptoolbar();
        initializeviews();
        setactions();
        // dump();
    }

    private void setuptoolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText("LOGIN");
        imgnetwork = (ImageView) toolbar.findViewById(R.id.imgnetwork);
    }

    private void initializeviews() {
        edtteamid = (EditText) findViewById(R.id.edtTeamid);
        edtpassword = (EditText) findViewById(R.id.edtPassword);
        mtxtinUserid = (TextInputLayout) findViewById(R.id.txtinputuserid);
        mtxtinPassword = (TextInputLayout) findViewById(R.id.txtinputpassword);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        deviceid = (TextView) findViewById(R.id.deviceid);
        mspindate = (Spinner) findViewById(R.id.spndate);
        clv = (CoordinatorLayout) findViewById(R.id.cl);
    }

    private void setactions() {
        btn_login.setOnClickListener(this);
        btn_clear.setOnClickListener(this);

        edtteamid.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable edt) {
                if (edtteamid.getText().length() > 0) {
                    mtxtinUserid.setError(null);
                    mtxtinUserid.setErrorEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

            }
        });
        edtpassword.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable edt) {
                if (edtpassword.getText().length() > 0) {
                    mtxtinPassword.setError(null);
                    mtxtinPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

            }
        });

        Calendar calendar = Calendar.getInstance();
        String myFormat = "dd-MMM-yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        List<String> dates = new ArrayList<String>();
        calendar.add(Calendar.DATE, -1);
        dates.add(sdf.format(calendar.getTime()));
        calendar.add(Calendar.DATE, +1);
        dates.add(sdf.format(calendar.getTime()));
        calendar.add(Calendar.DATE, +1);
        dates.add(sdf.format(calendar.getTime()));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dates);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mspindate.setAdapter(dataAdapter);
        mspindate.setSelection(1);
        setupUI(clv, LoginActivity.this);
    }

    private void setDeviceid() {
        String device = Preferences.getString("DeviceID", LoginActivity.this);
        if (device.length() > 0) {
            deviceid.setText("Device ID : " + device);
        }
    }

    @Override
    public void onClick(View v) {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        int id = v.getId();
        switch (id) {
            case R.id.btn_login:
                if(!TextUtils.isEmpty(Preferences.getString("API_URL",LoginActivity.this)))
                    validate();
                else
                    Toast.makeText(this, "Please set API URL", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_clear:

                edtteamid.setText("");
                edtpassword.setText("");
                mtxtinUserid.setError(null);
                mtxtinUserid.setErrorEnabled(false);
                mtxtinPassword.setError(null);
                mtxtinPassword.setErrorEnabled(false);
                break;
        }

    }

    private void validate() {
        Boolean failflag = false;
        String teamid, password;
        teamid = edtteamid.getText().toString();
        password = edtpassword.getText().toString();
        if (teamid.isEmpty()) {
            mtxtinUserid.setError("User ID cannot be empty");
            failflag = true;
        }
        if (password.isEmpty()) {
            mtxtinPassword.setError("Password cannot be empty");
            failflag = true;
        }
        if (!failflag) {
            if (NetworkUtil.getConnectivityStatusString(LoginActivity.this)) {
                JsonObject json = new JsonObject();
                json.addProperty("DeviceId", Preferences.getString("DeviceID", LoginActivity.this));
                json.addProperty("UserCode", teamid);
                json.addProperty("Password", password);
                json.addProperty("LoginDate", mspindate.getSelectedItem().toString());
                login(json);
            } else {
                raiseInternetSnakbar();
            }
             // dumpDummyData();
        }
    }

    private void login(JsonObject jsonObject) {
        showProgressDialog("Loading...");
        //        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.connectTimeout(30, TimeUnit.SECONDS);
        httpClient.readTimeout(30, TimeUnit.SECONDS);
        httpClient.writeTimeout(30, TimeUnit.SECONDS);
//        httpClient.addInterceptor(logging);

        Log.e("asdsd" , (CertisCISCOServer.getPATH(LoginActivity.this) + "\n" + jsonObject.toString()));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CertisCISCOServer.getPATH(LoginActivity.this))
                .addConverterFactory(GsonConverterFactory.create())
                .client(UnsafeOkHttpClient.getUnsafeOkHttpClient(httpClient))
                .build();
        CertisCISCOServices service = retrofit.create(CertisCISCOServices.class);
        Call<ResponseBody> call = service.signin(jsonObject);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    int result_code = response.code();
                    if (result_code == 200) {
                        String result_body = response.body().string();
                        JSONObject obj = new JSONObject(result_body);
                        String result = obj.getString("Result");
                        String messege = obj.getString("Message");
                        if (result.equals("Success")) {
                            JSONObject jp = obj.getJSONObject("Data");
                            if (jp.getString("UserRole").equals("Admin")) {
                                hideProgressDialog();
                                raiseSnakbar(messege);
                                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                            } else if (jp.getString("UserRole").equals("Supervisor")) {
                                if (Preferences.getString("DeviceID", LoginActivity.this).equals("")) {
                                    hideProgressDialog();
                                    raiseSnakbar("Device ID not set");
                                } else {
                                    Preferences.saveString("LoggedOn", jp.getString("LoggedOn"), LoginActivity.this);
                                    Preferences.saveInt("UserId", Integer.parseInt(jp.getString("LoggedInUser")), LoginActivity.this);
                                    Preferences.saveString("UserName", jp.getString("UserName"), LoginActivity.this);
                                    Preferences.saveInt("INTERVAL", Integer.parseInt(jp.getString("Interval")), LoginActivity.this);
                                    Preferences.saveString("AuthToken", jp.getString("Token"), LoginActivity.this);
                                    Preferences.saveString("teamId", jp.getString("ATMTeamId"), LoginActivity.this);
                                    Preferences.saveLong("sinceLoggedIn", SystemClock.elapsedRealtime(), LoginActivity.this);
                                    Preferences.saveString("LoginDate", mspindate.getSelectedItem().toString(), LoginActivity.this);
                                    Preferences.saveLong("TESTREQUESTID", 0, LoginActivity.this);
                                    Preferences.saveString("TESTREQUESTSTATUS", "", LoginActivity.this);
                                    Preferences.saveLong("JUNKREQUESTID", 0, LoginActivity.this);
                                    Preferences.saveString("JUNKREQUESTSTATUS", "", LoginActivity.this);
                                    getList(jp.getString("Token"), jp.getInt("LoggedInUser"), jp.getInt("ATMTeamId"));
                                    raiseSnakbar(messege);
                                }
                            } else {
                                hideProgressDialog();
                                raiseSnakbar("Invalid User Role");
                            }
                        } else {
                            hideProgressDialog();
                            raiseSnakbar(messege);
                        }

                    } else {
                        hideProgressDialog();
                        raiseSnakbar(getResources().getString(R.string.networkerror));
                    }
                } catch (Exception e) {
                    hideProgressDialog();
                    raiseSnakbar(getResources().getString(R.string.datakerror));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideProgressDialog();
                t.printStackTrace();
                raiseSnakbar(getResources().getString(R.string.networkerror));
            }
        });
    }

    private void getList(String token, int userid, int teamid) {
        if (NetworkUtil.getConnectivityStatusString(LoginActivity.this)) {
            ATMListCaller.instance().getATMList(LoginActivity.this,this, token, userid, teamid, mspindate.getSelectedItem().toString());
        } else {
            raiseInternetSnakbar();
        }
    }

    boolean doubleBackToExitPressedOnce = false;


    @Override
        public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getResources().getString(R.string.pressback), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private void savejob(JSONArray jobs) {
        try {
            Job.remove();
            Cartridge.remove();
            Seal.remove();
            OtherScan.remove();
            TestCash.remove();
            EditRequests.remove();
            CoinEnvelopes.remove();
            ClearHistoryRequests.remove();
            SyncDatabase.instance().sync(true, jobs, LoginActivity.this);
            Intent intent = new Intent("com.novigosolutions.certiscisco.intervalchanged");
            sendBroadcast(intent);
            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void dumpDummyData() {
//        String result_body = "{\"Result\":\"Success\",\"Message\":\"Success\",\"Data\":[{\"ATMOrderId\":436,\"ATMMasterId\":773,\"OrderType\":\"SCHEDULED\",\"OrderMode\":\"SCHEDULED\",\"ATMTypeCode\":\"ATT001\",\"ATMCode\":\"ALL10001\",\"Location\":\"Aloor\",\"PriorityName\":\"P0\",\"AccessableTime\":\"08:30 - 17:30\",\"Zone\":\"W   \",\"Status\":\"READY TO DELIVER\",\"OperationMode\":\"LOAD\",\"DeploymentNo\":3,\"UserId\":0,\"StartDate\":null,\"EndDate\":null,\"ElapsedDeliveryTime\":0,\"Bank\":\"HSBC\",\"ATMType\":\"400K\",\"Version\":\"41AB7A43\",\"LoadingCart\":[{\"CartId\":197,\"CartNo\":\"CART1\",\"SerialNo\":\"CWT1306004\",\"Deno\":\"W-50\",\"DuffleSeal\":\"\",\"SealNo\":[\"BA17101405\"]}],\"UnloadingCart\":[],\"UnloadingEnvelops\":null,\"LoadingEnvelops\":null,\"CoinEnvelopes\":[\"\"]},{\"ATMOrderId\":437,\"ATMMasterId\":774,\"OrderType\":\"SCHEDULED\",\"OrderMode\":\"SCHEDULED\",\"ATMTypeCode\":\"ATT002\",\"ATMCode\":\"ALL10002\",\"Location\":\"Aloor\",\"PriorityName\":\"P1\",\"AccessableTime\":\"08:30 - 17:30\",\"Zone\":\"W   \",\"Status\":\"READY TO DELIVER\",\"OperationMode\":\"LOAD\",\"DeploymentNo\":3,\"UserId\":0,\"StartDate\":null,\"EndDate\":null,\"ElapsedDeliveryTime\":0,\"Bank\":\"HSBC\",\"ATMType\":\"400K\",\"Version\":\"D74F2286\",\"LoadingCart\":[{\"CartId\":197,\"CartNo\":\"CART1\",\"SerialNo\":\"CERTISUN001\",\"Deno\":\"W-50\",\"DuffleSeal\":\"\",\"SealNo\":[\"BA17101406\"]},{\"CartId\":198,\"CartNo\":\"CART2\",\"SerialNo\":\"CERTISUN002\",\"Deno\":\"W-50\",\"DuffleSeal\":\"\",\"SealNo\":[\"BA17101407\"]},{\"CartId\":199,\"CartNo\":\"CART3\",\"SerialNo\":\"CERTISUN003\",\"Deno\":\"W-50\",\"DuffleSeal\":\"\",\"SealNo\":[\"BA17101408\"]},{\"CartId\":200,\"CartNo\":\"CART4\",\"SerialNo\":\"CERTISUN004\",\"Deno\":\"W-50\",\"DuffleSeal\":\"\",\"SealNo\":[\"BA17101409\"]},{\"CartId\":201,\"CartNo\":\"CART5\",\"SerialNo\":\"CERTISUN005\",\"Deno\":\"W-10\",\"DuffleSeal\":\"\",\"SealNo\":[\"BA17101410\"]},{\"CartId\":202,\"CartNo\":\"CART6\",\"SerialNo\":\"CERTISUN006\",\"Deno\":\"W-5\",\"DuffleSeal\":\"\",\"SealNo\":[\"BA17101411\"]},{\"CartId\":203,\"CartNo\":\"CART7\",\"SerialNo\":\"CERTISUN007\",\"Deno\":\"W-2\",\"DuffleSeal\":\"\",\"SealNo\":[\"BA17101412\"]},{\"CartId\":204,\"CartNo\":\"CART8\",\"SerialNo\":\"CERTISUN008\",\"Deno\":\"W-1000\",\"DuffleSeal\":\"\",\"SealNo\":[\"BA17101413\"]},{\"CartId\":205,\"CartNo\":\"CART9\",\"SerialNo\":\"CERTISUN009\",\"Deno\":\"W-50\",\"DuffleSeal\":\"\",\"SealNo\":[\"BA17101414\"]}],\"UnloadingCart\":[],\"UnloadingEnvelops\":null,\"LoadingEnvelops\":null,\"CoinEnvelopes\":[\"BA18011111035\",\"BA18011111036\"]},{\"ATMOrderId\":438,\"ATMMasterId\":775,\"OrderType\":\"SCHEDULED\",\"OrderMode\":\"SCHEDULED\",\"ATMTypeCode\":\"ATT003\",\"ATMCode\":\"ALL10003\",\"Location\":\"Aloor\",\"PriorityName\":\"P2\",\"AccessableTime\":\"08:30 - 17:30\",\"Zone\":\"W   \",\"Status\":\"READY TO DELIVER\",\"OperationMode\":\"UNLOAD\",\"DeploymentNo\":3,\"UserId\":0,\"StartDate\":null,\"EndDate\":null,\"ElapsedDeliveryTime\":0,\"Bank\":\"HSBC\",\"ATMType\":\"400K\",\"Version\":\"283D0748\",\"LoadingCart\":[],\"UnloadingCart\":[{\"CartId\":197,\"CartNo\":\"CART1\",\"SerialNo\":\"BA1711220\",\"Deno\":\"W-100\",\"DuffleSeal\":null,\"SealNo\":[\"BA1711444\"]}],\"UnloadingEnvelops\":null,\"LoadingEnvelops\":null,\"CoinEnvelopes\":[\"BA18011111041\",\"BA18011111042\",\"BA18011111043\"]}],\"RecordCount\":3,\"JobId\":0}";
//        try {
//            JSONObject obj = new JSONObject(result_body);
//            String result = obj.getString("Result");
//            String messege = obj.getString("Message");
//            if (result.equals("Success")) {
//                JSONArray jsonArray = obj.getJSONArray("Data");
//                savejob(jsonArray);
//                Preferences.saveBoolean("LoggedIn", true, LoginActivity.this);
//                Intent i = new Intent(LoginActivity.this, HomeActivity.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(i);
//            } else {
//                raiseSnakbar(messege);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            raiseSnakbar(getResources().getString(R.string.datakerror));
//        }
//    }

    @Override
    public void onResult(int result_code, String result_body) {
        hideProgressDialog();
        if (result_code == 200) {
            try {
                JSONObject obj = new JSONObject(Constants.requestBody);
                String result = obj.getString("Result");
                String messege = obj.getString("Message");
                if (result.equals("Success")) {
                    JSONArray jsonArray = obj.getJSONArray("Data");
                    savejob(jsonArray);
                    Preferences.saveBoolean("LoggedIn", true, LoginActivity.this);
                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                } else {
                    raiseSnakbar(messege);
                }
            } catch (Exception e) {
                e.printStackTrace();
                raiseSnakbar(getResources().getString(R.string.datakerror));
            }

        } else if (result_code == 409) {
            authalert(LoginActivity.this);
        } else {
            raiseSnakbar(getResources().getString(R.string.networkerror));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDeviceid();
        NetworkChangeReceiver.changekListener = this;
        onNetworkChanged();
    }

    @Override
    public void onNetworkChanged() {

        if (NetworkUtil.getConnectivityStatusString(LoginActivity.this))
            imgnetwork.setVisibility(View.VISIBLE);
        else
            imgnetwork.setVisibility(View.GONE);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            View v = LayoutInflater.from(LoginActivity.this).inflate(R.layout.settings_view,null);
            final EditText et = v.findViewById(R.id.etUrl);
            if(!TextUtils.isEmpty(Preferences.getString("API_URL",LoginActivity.this))){
                et.setText(Preferences.getString("API_URL",LoginActivity.this));
            }
            dialog = new AlertDialog.Builder(LoginActivity.this)
                    .setTitle("Set API URL")
                    .setView(v)
                    .setPositiveButton("Confirm", null)
                    .setNeutralButton("Clear", null)
                    .setNegativeButton("Cancel", null)
                    .show();

            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAndSetIP(et.getText().toString());
                }
            });
            Button neutralButton = dialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            neutralButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    et.setText("");
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkAndSetIP(final String ip) {
        showProgressDialog("Loading...");
        try {
            //        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            //        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.connectTimeout(30, TimeUnit.SECONDS);
            httpClient.readTimeout(30, TimeUnit.SECONDS);
            httpClient.writeTimeout(30, TimeUnit.SECONDS);
            //        httpClient.addInterceptor(logging);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ip+"/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(UnsafeOkHttpClient.getUnsafeOkHttpClient(httpClient))
                    .build();
            CertisCISCOServices service = retrofit.create(CertisCISCOServices.class);
            Call<ResponseBody> call = service.ping();
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    hideProgressDialog();
                    int result_code = response.code();
                    if (result_code == 200) {
                        Preferences.saveString("API_URL", ip, LoginActivity.this);
                        Toast.makeText(LoginActivity.this, "IP set", Toast.LENGTH_SHORT).show();
                        if(dialog!=null && dialog.isShowing()){
                            dialog.dismiss();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Could not connect. Please verify the URL", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    hideProgressDialog();
                    Toast.makeText(LoginActivity.this, "Error: Could not set IP", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            hideProgressDialog();
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}
package com.novigosolutions.certiscisco.utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.activities.ProcessJobActivity;
import com.novigosolutions.certiscisco.models.Cartridge;
import com.novigosolutions.certiscisco.models.CoinEnvelopes;
import com.novigosolutions.certiscisco.models.EditRequests;
import com.novigosolutions.certiscisco.models.Job;
import com.novigosolutions.certiscisco.models.Seal;
import com.novigosolutions.certiscisco.webservices.CertisCISCOServer;
import com.novigosolutions.certiscisco.webservices.CertisCISCOServices;
import com.novigosolutions.certiscisco.webservices.UnsafeOkHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.novigosolutions.certiscisco.utils.Constants.coinenvelopeid;


public class RequestEditDialog extends Dialog implements View.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher {

    public Activity activity;
    public Button btnaction, btncancel;
    private int ATMOrderId;
    private String fragmenttype;
    private int CartId;
    Spinner spncart;
    List<Cartridge> cartridgeList;
    OnMyDialogResult mDialogResult;
    CheckBox checkserial;//, checkseal1, checkseal2;
    LinearLayout llpending;
    EditText edtSerial;//, edtseal1, edtseal2;
    Boolean isHistoryCleared;
    Button btncheck;
    LinearLayout llcartidges, llcoinenvelopes, llseals;
    RelativeLayout llrel;
    public RequestEditDialog(Activity activity, int ATMOrderId, String fragmenttype) {
        super(activity);
        // TODO Auto-generated constructor stub
        this.activity = activity;
        this.ATMOrderId = ATMOrderId;
        this.fragmenttype = fragmenttype;
        isHistoryCleared = Job.isHistoryCleared(ATMOrderId) && fragmenttype.equals("UNLOAD");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.request_edit);
        btnaction = (Button) findViewById(R.id.btn_action);
        btncancel = (Button) findViewById(R.id.btn_cancel);
        edtSerial = (EditText) findViewById(R.id.edtserial);
//        edtseal1 = (EditText) findViewById(R.id.edtseal1);
//        edtseal2 = (EditText) findViewById(R.id.edtseal2);
        spncart = (Spinner) findViewById(R.id.spncart);
        btncheck = (Button) findViewById(R.id.btncheck);
        checkserial = (CheckBox) findViewById(R.id.checkserial);
//        checkseal1 = (CheckBox) findViewById(R.id.checkseal1);
//        checkseal2 = (CheckBox) findViewById(R.id.checkseal2);
        llrel = (RelativeLayout) findViewById(R.id.lrel);
        llpending = (LinearLayout) findViewById(R.id.llpending);
        llcartidges = (LinearLayout) findViewById(R.id.llcartidges);
        llcoinenvelopes = (LinearLayout) findViewById(R.id.llcoinenvelopes);
        llseals = (LinearLayout) findViewById(R.id.llseals);
        spncart.setOnItemSelectedListener(this);
        btnaction.setOnClickListener(this);
        btncancel.setOnClickListener(this);
        List<String> cartlist = new ArrayList<String>();
        cartridgeList = Cartridge.getUnScanned(ATMOrderId, fragmenttype);
        for (int i = 0; i < cartridgeList.size(); i++) {
            cartlist.add(cartridgeList.get(i).CartNo);
        }
        if (fragmenttype.equals("LOAD") && CoinEnvelopes.get(ATMOrderId).size() > 0) {
            cartlist.add("Coin Envelope(s)");
        }
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, cartlist);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spncart.setAdapter(dataAdapter);

        edtSerial.addTextChangedListener(this);
//        edtseal1.addTextChangedListener(this);
//        edtseal2.addTextChangedListener(this);
        btncheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRequestStatus();
            }
        });
        ((ProcessJobActivity) activity).setupUI(llrel,activity);
        checkRequestStatus();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_action:
                Boolean failFlag = false;
                JSONArray selectedcoinjsonArray = new JSONArray();
                if (spncart.getSelectedItem().equals("Coin Envelope(s)")) {
                    for (int i = 0; i < llcoinenvelopes.getChildCount(); i++) {
                        LinearLayout linearLayout = (LinearLayout) llcoinenvelopes.getChildAt(i);
                        CheckBox checkBox = (CheckBox) linearLayout.findViewById(R.id.checkcoin);
                        EditText editText = (EditText) linearLayout.findViewById(R.id.edtcoin);
                        if (editText.getVisibility() == View.VISIBLE) {
                            if (editText.getText().toString().isEmpty()) {
                                failFlag = true;
                                editText.setError("Cannot be empty");
                            } else if (!editText.getText().toString().equalsIgnoreCase(checkBox.getText().toString())) {
                                failFlag = true;
                                editText.setError("Invalid bar code");
                            }
                        }
                        if (checkBox.isChecked()) {
                            selectedcoinjsonArray.put(checkBox.getText().toString());
                        }
                    }
                    if (selectedcoinjsonArray.length() == 0) {
                        failFlag = true;
                        Toast.makeText(activity, "Select atleast one checkbox", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Boolean atleastSelected = false;
                    if (checkserial.isChecked()) atleastSelected = true;
                    for (int i = 0; i < llseals.getChildCount(); i++) {
                        LinearLayout linearLayout = (LinearLayout) llseals.getChildAt(i);
                        CheckBox checkBox = (CheckBox) linearLayout.findViewById(R.id.checkcoin);
                        if (checkBox.isChecked()) {
                            atleastSelected = true;
                        }
                    }
                    if (!atleastSelected) {
                        failFlag = true;
                        Toast.makeText(activity, "Select atleast one checkbox", Toast.LENGTH_SHORT).show();
                    }
                    if (edtSerial.getVisibility() == View.VISIBLE) {
                        if (checkserial.getText().toString().equals("")) {
                            String prifix = "";
                            if (edtSerial.getText().toString().length() > 1) {
                                prifix = edtSerial.getText().toString().substring(0, 2);
                            }
                            if (edtSerial.getText().toString().equals("")) {
                                failFlag = true;
                                edtSerial.setError("Cannot be empty");
                            }
                            else if (prifix.equalsIgnoreCase("WA")|| prifix.equalsIgnoreCase("BA")) {
                                failFlag = true;
                                edtSerial.setError("Do not prefix WA/BA");
                            }
                        } else if (!edtSerial.getText().toString().equalsIgnoreCase(checkserial.getText().toString())) {
                            failFlag = true;
                            edtSerial.setError("Invalid bar code");
                        }
                    }
                    for (int i = 0; i < llseals.getChildCount(); i++) {
                        LinearLayout linearLayout = (LinearLayout) llseals.getChildAt(i);
                        CheckBox checkBox = (CheckBox) linearLayout.findViewById(R.id.checkcoin);
                        EditText editText = (EditText) linearLayout.findViewById(R.id.edtcoin);
                        if (editText.getVisibility() == View.VISIBLE) {
                            if (checkBox.getText().toString().equals("")) {
                                String prifix = "";
                                if (editText.getText().toString().length() > 1) {
                                    prifix = editText.getText().toString().substring(0, 2);
                                }
                                if (editText.getText().toString().equals("")) {
                                    failFlag = true;
                                    editText.setError("Cannot be empty");
                                }
                                else if (!(prifix.equalsIgnoreCase("WA")|| prifix.equalsIgnoreCase("BA"))) {
                                    failFlag = true;
                                    editText.setError("Do prefix WA/BA");
                                }
                            } else if (!editText.getText().toString().equalsIgnoreCase(checkBox.getText().toString())) {
                                failFlag = true;
                                editText.setError("Invalid bar code");
                            }
                        }
                    }
                }

                if (!failFlag && mDialogResult != null) {
                    JSONObject resultjsonObject = new JSONObject();
                    try {

                        if (spncart.getSelectedItem().equals("Coin Envelope(s)")) {
                            CartId = coinenvelopeid;
                            resultjsonObject.put("isCoin", true);
                            resultjsonObject.put("serial", "");
                            resultjsonObject.put("seal1", "");
                            resultjsonObject.put("seal2", "");
                            resultjsonObject.put("isSerial", false);
                            resultjsonObject.put("isSeal1", false);
                            resultjsonObject.put("isSeal2", false);
                        } else {
                            resultjsonObject.put("isCoin", false);
                            resultjsonObject.put("serial", checkserial.getText().toString().isEmpty() ? edtSerial.getText().toString().toUpperCase() : checkserial.getText().toString());
                            resultjsonObject.put("isSerial", checkserial.isChecked());
                            JSONArray resultjsonArray = new JSONArray();
                            for (int i = 0; i < llseals.getChildCount(); i++) {
                                JSONObject sealjsonObject = new JSONObject();
                                LinearLayout linearLayout = (LinearLayout) llseals.getChildAt(i);
                                CheckBox checkBox = (CheckBox) linearLayout.findViewById(R.id.checkcoin);
                                EditText editText = (EditText) linearLayout.findViewById(R.id.edtcoin);
                                sealjsonObject.put("seal", checkBox.getText().toString().isEmpty() ? editText.getText().toString().toUpperCase() : checkBox.getText().toString());
                                sealjsonObject.put("isSeal", checkBox.isChecked());
                                resultjsonArray.put(sealjsonObject);
                            }
                            resultjsonObject.put("sealarray", resultjsonArray);
                        }
                        resultjsonObject.put("cartId", CartId);
                        resultjsonObject.put("selectedcoin", selectedcoinjsonArray);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("resultjsonObject", ":" + resultjsonObject);
                    mDialogResult.finish(resultjsonObject);
                    dismiss();
                }
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            default:
                break;
        }

    }

    private void refresh(int position) {
        llcartidges.setVisibility(View.GONE);
        llcoinenvelopes.setVisibility(View.GONE);
        //llseals.setVisibility(View.GONE);
        llpending.setVisibility(View.GONE);

        if (spncart.getSelectedItem().equals("Coin Envelope(s)")) {
            llcoinenvelopes.setVisibility(View.VISIBLE);
            List<CoinEnvelopes> coinEnvelopsList = CoinEnvelopes.get(ATMOrderId);
            llcoinenvelopes.removeAllViews();
            for (int i = 0; i < coinEnvelopsList.size(); i++) {
                View view = LayoutInflater.from(activity).inflate(R.layout.edit_coin_enve, null);
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkcoin);
                EditText editText = (EditText) view.findViewById(R.id.edtcoin);
                checkBox.setText(coinEnvelopsList.get(i).CoinEnvelope);
                String status = EditRequests.getStatus(ATMOrderId, fragmenttype, coinenvelopeid);

                if (status.equals("CONFIRMED")) {
                    btnaction.setText("DONE");
                    if (coinEnvelopsList.get(i).isEditRequested == 1) {
                        checkBox.setChecked(true);
                        editText.setVisibility(View.VISIBLE);
                    }
                } else if (status.equals("PENDING")) {
                    btnaction.setText("OK");
                    if (coinEnvelopsList.get(i).isEditRequested == 1) {
                        checkBox.setChecked(true);
                    }
                    llpending.setVisibility(View.VISIBLE);
                } else {
                    btnaction.setText("REQUEST");
                    checkBox.setEnabled(true);
                }
                llcoinenvelopes.addView(view);
            }
        } else {
            //llseals.setVisibility(View.VISIBLE);
            llseals.removeAllViews();
            llcartidges.setVisibility(View.VISIBLE);
            CartId = cartridgeList.get(position).CartId;
            String status = EditRequests.getStatus(ATMOrderId, fragmenttype, CartId);
            Log.e("status", status);

            checkserial.setEnabled(false);
            checkserial.setChecked(false);
            edtSerial.setVisibility(View.GONE);
            checkserial.setText(cartridgeList.get(position).SerialNo);
            List<Seal> seallist = Seal.get(ATMOrderId, CartId, fragmenttype);
            for (int i = 0; i < seallist.size(); i++) {
                View view = LayoutInflater.from(activity).inflate(R.layout.edit_coin_enve, null);
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkcoin);
                EditText editText = (EditText) view.findViewById(R.id.edtcoin);
                checkBox.setText(seallist.get(i).SealNo);
                llseals.addView(view);
            }

            if (status.equals("CONFIRMED")) {
                btnaction.setText("DONE");
                if (cartridgeList.get(position).isEditRequested == 1) {
                    edtSerial.setVisibility(View.VISIBLE);
                    checkserial.setChecked(true);
                }
                for (int i = 0; i < seallist.size(); i++) {
                    if (seallist.get(i).isEditRequested == 1) {
                        LinearLayout linearLayout = (LinearLayout) llseals.getChildAt(i);
                        CheckBox checkBox = (CheckBox) linearLayout.findViewById(R.id.checkcoin);
                        EditText editText = (EditText) linearLayout.findViewById(R.id.edtcoin);
                        editText.setVisibility(View.VISIBLE);
                        checkBox.setChecked(true);
                    }
                }
            } else if (status.equals("PENDING")) {
                btnaction.setText("OK");
                llpending.setVisibility(View.VISIBLE);
                if (cartridgeList.get(position).isEditRequested == 1) {
                    checkserial.setChecked(true);
                }
                for (int i = 0; i < seallist.size(); i++) {
                    if (seallist.get(i).isEditRequested == 1) {
                        LinearLayout linearLayout = (LinearLayout) llseals.getChildAt(i);
                        CheckBox checkBox = (CheckBox) linearLayout.findViewById(R.id.checkcoin);
                        checkBox.setChecked(true);
                    }
                }

            } else {
                btnaction.setText("REQUEST");
                if (cartridgeList.get(position).isScanned == 0) {
                    checkserial.setEnabled(true);
                }
                for (int i = 0; i < seallist.size(); i++) {
                    if (seallist.get(i).isScanned == 0) {
                        LinearLayout linearLayout = (LinearLayout) llseals.getChildAt(i);
                        CheckBox checkBox = (CheckBox) linearLayout.findViewById(R.id.checkcoin);
                        checkBox.setEnabled(true);
                    }
                }
            }
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        refresh(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void setDialogResult(OnMyDialogResult dialogResult) {
        mDialogResult = dialogResult;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString().length() > 0) {
//            if (s == edtSerial.getEditableText()) {
//                edtSerial.setError(null);
//            } else if (s == edtseal1.getEditableText()) {
//                edtseal1.setError(null);
//            } else if (s == edtseal2.getEditableText()) {
//                edtseal2.setError(null);
//            }
        }
    }

    public interface OnMyDialogResult {
        void finish(JSONObject jsonObject);
    }

    private void checkRequestStatus() {
        if (NetworkUtil.getConnectivityStatusString(activity)) {
            ((ProcessJobActivity) activity).showProgressDialog("Loading...");
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.connectTimeout(30, TimeUnit.SECONDS);
            httpClient.readTimeout(30, TimeUnit.SECONDS);
            httpClient.writeTimeout(30, TimeUnit.SECONDS);
//        httpClient.addInterceptor(logging);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(CertisCISCOServer.getPATH(getContext()))
                    .client(UnsafeOkHttpClient.getUnsafeOkHttpClient(httpClient))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            CertisCISCOServices service = retrofit.create(CertisCISCOServices.class);
            Call<ResponseBody> call = service.requestStatus(Preferences.getString("AuthToken", activity), Preferences.getInt("UserId", activity), EditRequests.getRequetedIDs(ATMOrderId, fragmenttype));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        ((ProcessJobActivity) activity).hideProgressDialog();
                        int result_code = response.code();
                        Log.e("status_result_code", ":" + result_code);
                        Log.e("status_result_messege", ":" + response.message());
                        Log.e("status_result_error", ":" + response.errorBody());
                        //result_body = result_body.replaceAll("\\W", "");
                        if (result_code == 200) {
                            String result_body = response.body().string();
                            Log.e("status_resultbody", ":" + result_body);
                            JSONArray jsonArray = new JSONArray(result_body);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jp = jsonArray.getJSONObject(i);
                                Long requestedid = Long.parseLong(jp.getString("value"));
                                String status = jp.getString("Status");
                                if (status.equals("CONFIRMED")) {
                                    EditRequests.updateSingle(requestedid, status);
                                } else if (status.equals("INVALID") || status.equals("REJECTED")) {
                                    EditRequests.removeSingle(requestedid);
                                }
                            }
                            refresh(spncart.getSelectedItemPosition());
                        } else if (result_code == 409) {
                            ((ProcessJobActivity) activity).authalert(activity);
                        } else {
                            //((ProcessJobActivity) getActivity()).raiseSnakbar(cl, getResources().getString(R.string.networkerror));
                            Toast.makeText(activity, activity.getResources().getString(R.string.networkerror), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
//                    raiseSnakbar(cl, getResources().getString(R.string.networkerror));
                        Toast.makeText(activity, activity.getResources().getString(R.string.networkerror), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    ((ProcessJobActivity) activity).hideProgressDialog();
                    t.printStackTrace();
                    //raiseSnakbar(cl, getResources().getString(R.string.networkerror));
                    Toast.makeText(activity, activity.getResources().getString(R.string.networkerror), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
package com.novigosolutions.certiscisco.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.activities.BarCodeScanActivity;
import com.novigosolutions.certiscisco.activities.ProcessJobActivity;
import com.novigosolutions.certiscisco.adapters.CartridgeListAdapter;
import com.novigosolutions.certiscisco.interfaces.FragmentInterface;
import com.novigosolutions.certiscisco.interfaces.IOnScannerData;
import com.novigosolutions.certiscisco.models.Cartridge;
import com.novigosolutions.certiscisco.models.ClearHistoryRequests;
import com.novigosolutions.certiscisco.models.CoinEnvelopes;
import com.novigosolutions.certiscisco.models.EditRequests;
import com.novigosolutions.certiscisco.models.Job;
import com.novigosolutions.certiscisco.models.Seal;
import com.novigosolutions.certiscisco.objects.ManualEntryResult;
import com.novigosolutions.certiscisco.utils.CommonMethods;
import com.novigosolutions.certiscisco.utils.NetworkUtil;
import com.novigosolutions.certiscisco.utils.Preferences;
import com.novigosolutions.certiscisco.utils.RequestEditDialog;
import com.novigosolutions.certiscisco.webservices.CertisCISCOServer;
import com.novigosolutions.certiscisco.webservices.CertisCISCOServices;
import com.novigosolutions.certiscisco.webservices.UnsafeOkHttpClient;

import org.json.JSONArray;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class LoadUnloadingFragment extends Fragment implements View.OnClickListener, IOnScannerData, FragmentInterface {
    private RecyclerView recyclerView;
    private CartridgeListAdapter mAdapter;
    LinearLayout llscan, lldata, llnodata;
    Button prev, next;
    String fragmenttype;
    int orderno = 0;
    int cartid = 0;
    Boolean isSerialScan = true;
    ImageView imgClear, imghistoryclear;
    List<Cartridge> cartridgeList;
    TextView txtnumscanned, txtnumincomplete, txtnumpending, txtnumcoin;
    LinearLayout llcoincount;
    Boolean isHistoryCleared = false;
    ImageView img_manual_entry;
    Boolean isManualentered = false;

    LinearLayout llcoinen, llcoinhead;

    public LoadUnloadingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_load_unloading, container, false);
        initializeviews(rootView);
        setactions();
        initializedata();

        return rootView;
    }

    private void initializeviews(View rootView) {
        llscan = (LinearLayout) rootView.findViewById(R.id.scanll);
        imghistoryclear = (ImageView) rootView.findViewById(R.id.img_clear_history);
        imgClear = (ImageView) rootView.findViewById(R.id.imgclear);
        lldata = (LinearLayout) rootView.findViewById(R.id.lldata);
        llnodata = (LinearLayout) rootView.findViewById(R.id.llnodata);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        txtnumscanned = (TextView) rootView.findViewById(R.id.txtnumscanned);
        txtnumincomplete = (TextView) rootView.findViewById(R.id.txtnumincomplete);
        txtnumpending = (TextView) rootView.findViewById(R.id.txtnumpending);
        txtnumcoin = (TextView) rootView.findViewById(R.id.txtnumcoin);
        llcoincount = (LinearLayout) rootView.findViewById(R.id.llcoincount);
        prev = (Button) rootView.findViewById(R.id.btn_prev);
        next = (Button) rootView.findViewById(R.id.btn_next);

        img_manual_entry = (ImageView) rootView.findViewById(R.id.img_manual_entry);

        llcoinen = (LinearLayout) rootView.findViewById(R.id.llcoinenvelopes);
        llcoinhead = (LinearLayout) rootView.findViewById(R.id.llcoinhead);

    }

    private void setactions() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        llscan.setOnClickListener(this);
        imgClear.setOnClickListener(this);
        prev.setOnClickListener(this);
        next.setOnClickListener(this);
        img_manual_entry.setOnClickListener(this);
        imghistoryclear.setOnClickListener(this);
        imgClear.setEnabled(false);
    }

    private void initializedata() {
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            orderno = extras.getInt("orderno");
            Log.e("orderno", ":" + orderno);
        }
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            fragmenttype = bundle.getString("FragmentType", getString(R.string.loading));
            Log.e("FragmentType", fragmenttype);
        }

        if (fragmenttype.equals(getString(R.string.loading))) {
            cartridgeList = Cartridge.get(orderno, getString(R.string.load));
            imghistoryclear.setVisibility(View.GONE);
            refreshcoin();
        } else {
            cartridgeList = Cartridge.get(orderno, getString(R.string.unload));
            if (Job.isHistoryCleared(orderno)) {
                isHistoryCleared = true;
                imghistoryclear.setVisibility(View.GONE);
            }
        }
        if (cartridgeList.size() > 0) {
            lldata.setVisibility(View.VISIBLE);
            llnodata.setVisibility(View.GONE);
            refresh();

        } else {
            llnodata.setVisibility(View.VISIBLE);
            lldata.setVisibility(View.GONE);
        }
        txtnumpending.setText(String.valueOf(cartridgeList.size()));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.scanll:
                try {
                    ((BarCodeScanActivity)getContext()).scansoft();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.imgclear:
                alert(1);
                break;
            case R.id.btn_prev:
                ((ProcessJobActivity) getActivity()).setpage(-1);
                break;
            case R.id.btn_next:
                boolean isAllScanned = false;
                if (fragmenttype.equals(getString(R.string.unload))) {
                    if (Cartridge.isAllCartScanned(orderno, fragmenttype)) {
                        isAllScanned = true;
                    }
                } else {
                    if (Cartridge.isAllCartScanned(orderno, fragmenttype) && CoinEnvelopes.isAllCoinScanned(orderno)) {
                        isAllScanned = true;
                    }
                }
                if (isAllScanned) {

                        ((ProcessJobActivity) getActivity()).setpage(1);

                } else {
                    Toast.makeText(getActivity(), "All should be scanned", Toast.LENGTH_SHORT).show();
                }
                break;


            case R.id.img_manual_entry:
                boolean isallScanned = false;
                if (fragmenttype.equals(getString(R.string.unload))) {
                    if (Cartridge.isAllCartScanned(orderno, fragmenttype)) {
                        isallScanned = true;
                    }
                } else {
                    if (Cartridge.isAllCartScanned(orderno, fragmenttype) && CoinEnvelopes.isAllCoinScanned(orderno)) {
                        isallScanned = true;
                    }
                }
                if (isallScanned) {
                    Toast.makeText(getActivity(), "All Scanned", Toast.LENGTH_SHORT).show();
                } else {
                    RequestEditDialog cdd = new RequestEditDialog(getActivity(), orderno, fragmenttype);
                    cdd.setDialogResult(new RequestEditDialog.OnMyDialogResult() {
                        @Override
                        // public void finish(int cartid, Boolean tick1, Boolean tick2, Boolean tick3, String serial, String seal1, String seal2) {
                        public void finish(JSONObject jsonObject) {
                            try {
                                int cartid = jsonObject.getInt("cartId");
                                String status = EditRequests.getStatus(orderno, fragmenttype, cartid);
                                if (status.equals("CONFIRMED")) {
                                    updatemanualentry(jsonObject);
                                } else if (status.equals("NOREQUEST")) {
                                    requestforEdit(jsonObject);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    cdd.show();
                }
                break;
            case R.id.img_clear_history:
                String status = ClearHistoryRequests.getStatus(orderno);
                if (status.equals("CONFIRMED")) {
                    alert(2);
                } else if (status.equals("PENDING")) {
                    if (NetworkUtil.getConnectivityStatusString(getActivity())) {
                        checkRequestStatus();
                    } else {
                        ((ProcessJobActivity) getActivity()).raiseInternetSnakbar();
                    }

                } else {
                    if (NetworkUtil.getConnectivityStatusString(getActivity())) {
                        requestForClearHistory();
                    } else {
                        ((ProcessJobActivity) getActivity()).raiseInternetSnakbar();
                    }
                }
                break;
        }
    }

    @Override
    public void onDataScanned(String data) {
        Log.e("data scanned "," yes");
        Boolean isInvalid = false;
        String message = "This bar code is invalid";
        if (data != null && !data.equals("")) {
            if (isHistoryCleared && Job.isExist(data)) {
                isInvalid = true;
                message = "Duplicate value\n("+Job.isExistwithmessage(data)+")";
            } else if (Cartridge.isAllCartScanned(orderno, fragmenttype)) {
                if (CoinEnvelopes.updateScan(orderno, data)) {
                    refreshcoin();
                } else {
                    isInvalid = true;
                }
            } else {
                if (isSerialScan) {
                        cartid = Cartridge.updateScanAndGetCartridgeId(orderno, data, fragmenttype, isHistoryCleared);
                        if (cartid == -1) {
                            isInvalid = true;
                            message = "Invalid serial no";
                        } else {
                            refresh();
                            isSerialScan = false;
                            txtnumincomplete.setText("1");
                            imgClear.setEnabled(true);
                        }

                } else {
                        if (Seal.isSealExistAndUpdate(orderno, cartid, fragmenttype, data, isHistoryCleared, -5)) {
                            if (Seal.isSealScanComplete(orderno, cartid, fragmenttype)) {
                                isSerialScan = true;
                                txtnumscanned.setText(String.valueOf(Integer.parseInt(txtnumscanned.getText().toString()) + 1));
                                txtnumincomplete.setText("0");
                                txtnumpending.setText(String.valueOf(Integer.parseInt(txtnumpending.getText().toString()) - 1));
                            }
                            refresh();
                        } else {
                            isInvalid = true;
                            message = "Invalid seal no";
                        }
                }
            }
        } else {
            isInvalid = true;
            message = "This bar code is empty";
        }
        if (isInvalid) {
            ((ProcessJobActivity) getActivity()).invalidbarcodealert(message);
        }
        Log.e("data enterd", ":" + isManualentered);
    }

    private void updatemanualentry(JSONObject jsonObject) {
        Log.e("json object ", ":" + jsonObject.toString());
        Boolean isInvalid = false;
        String message = "";
        try {
            if (jsonObject.getBoolean("isCoin")) {
                if (Cartridge.isAllCartScanned(orderno, fragmenttype)) {
                    JSONArray jsonArray = jsonObject.getJSONArray("selectedcoin");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        if (CoinEnvelopes.updateScan(orderno, jsonArray.get(i).toString())) {

                        } else {
                            isInvalid = true;
                            if (message.isEmpty()) message = "Invalid barcode";
                        }
                    }
                    refreshcoin();
                } else {
                    Toast.makeText(getActivity(), "Scan cartridge first", Toast.LENGTH_SHORT).show();
                }
            } else {
                List<ManualEntryResult> manualEntryResults = new ArrayList<ManualEntryResult>();
                Cartridge cartridge = Cartridge.getSingle(orderno, fragmenttype, jsonObject.getInt("cartId"));
                if (cartridge.isEditRequested == 1 && cartridge.isScanned == 0) {
                    ManualEntryResult manualEntryResult = new ManualEntryResult(jsonObject.getString("serial"), true, 0);
                    manualEntryResults.add(manualEntryResult);
                }
                JSONArray sealarray = jsonObject.getJSONArray("sealarray");
                for (int i = 0; i < sealarray.length(); i++) {
                    if (Seal.canEdit(orderno, jsonObject.getInt("cartId"), fragmenttype, i) && !Seal.isScanned(orderno, jsonObject.getInt("cartId"), fragmenttype, i)) {
                        JSONObject sealObject = sealarray.getJSONObject(i);
                        ManualEntryResult manualEntryResult = new ManualEntryResult(sealObject.getString("seal"), false, i);
                        manualEntryResults.add(manualEntryResult);
                    }
                }
                for (int i = 0; i < manualEntryResults.size(); i++) {
                    String data = manualEntryResults.get(i).getData();
                    boolean isSerial = manualEntryResults.get(i).isserial();
                    if (data != null && !data.equals("")) {
                        if (isHistoryCleared && Job.isExist(data)) {
                            isInvalid = true;
                            if (message.isEmpty()) message = "Duplicate value\n("+Job.isExistwithmessage(data)+")";
                        } else {
                            if (isSerialScan) {
                                String prifix = "";
                                if (data.length() > 1) {
                                    prifix = data.substring(0, 2);
                                }
                                Log.e("pref", prifix);
                                if (isSerial) {
                                    cartid = jsonObject.getInt("cartId");
                                    Boolean succes = Cartridge.updateScan(orderno,fragmenttype,cartid,data, isHistoryCleared);
                                    if (succes) {
                                        isSerialScan = false;
                                        txtnumincomplete.setText("1");
                                        imgClear.setEnabled(true);
                                    } else {
                                        cartid=0;
                                        isInvalid = true;
                                        if (message.isEmpty()) message = "Invalid serial no";
                                    }
                                } else {
                                    isInvalid = true;
                                    if (message.isEmpty()) message = "Scan serial first";
                                }
                            } else {
                                String prifix = "";
                                if (data.length() > 1) {
                                    prifix = data.substring(0, 2);
                                }
                                Log.e("pref", prifix);
                                if (!isSerial) {
                                    if (cartid != jsonObject.getInt("cartId")) {
                                        isInvalid = true;
                                        if (message.isEmpty())
                                            message = "Scan seal of scanned serial";
                                    } else {
                                        if (Seal.isSealExistAndUpdate(orderno, cartid, fragmenttype, data, isHistoryCleared, manualEntryResults.get(i).getPos())) {
                                            if (Seal.isSealScanComplete(orderno, cartid, fragmenttype)) {
                                                isSerialScan = true;
                                                txtnumscanned.setText(String.valueOf(Integer.parseInt(txtnumscanned.getText().toString()) + 1));
                                                txtnumincomplete.setText("0");
                                                txtnumpending.setText(String.valueOf(Integer.parseInt(txtnumpending.getText().toString()) - 1));
                                            }
                                            refresh();
                                            isInvalid = false;
                                        } else {
                                            isInvalid = true;
                                            if (message.isEmpty()) message = "Invalid seal";
                                        }
                                    }
                                } else {
                                    isInvalid = true;
                                    if (message.isEmpty()) message = "Invalid seal";
                                }
                            }
                        }
                    } else {
                        isInvalid = true;
                        if (message.isEmpty()) message = "This bar code is empty";
                    }
                }
                refresh();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isInvalid) {
            ((ProcessJobActivity) getActivity()).invalidbarcodealert(message);
        }
    }

    private void refresh() {
        if (mAdapter == null) {
            mAdapter = new CartridgeListAdapter(cartridgeList, getActivity(), orderno, fragmenttype);
            recyclerView.setAdapter(mAdapter);
        } else {
            List<Cartridge> tempcartridgeList;
            if (fragmenttype.equals(getString(R.string.loading))) {
                tempcartridgeList = Cartridge.get(orderno, getString(R.string.load));
            } else {
                tempcartridgeList = Cartridge.get(orderno, getString(R.string.unload));
            }
            cartridgeList.clear();
            cartridgeList.addAll(tempcartridgeList);
            mAdapter.notifyDataSetChanged();
        }
    }

    String colorWhite = "#FFFFFF", colorGreen = "#43A047", colorOrange = "#EF6C00";

    private void refreshcoin() {
        List<CoinEnvelopes> coinEnvelopsList = CoinEnvelopes.get(orderno);
        if (fragmenttype.equals("LOAD") && coinEnvelopsList.size() > 0) {
            llcoinhead.setVisibility(View.VISIBLE);
            llcoincount.setVisibility(View.VISIBLE);
            llcoinen.removeAllViews();
            for (int i = 0; i < coinEnvelopsList.size(); i++) {
                TextView textView = new TextView(getActivity());
                textView.setPadding(10, 5, 10, 5);
                textView.setText(coinEnvelopsList.get(i).CoinEnvelope);
                if (coinEnvelopsList.get(i).isScanned == 1) {
                    textView.setBackgroundColor(Color.parseColor(colorGreen));
                    textView.setTextColor(Color.parseColor(colorWhite));
                } else {
                    textView.setTextColor(Color.parseColor(colorOrange));
                }
                llcoinen.addView(textView);
            }
        }
        txtnumcoin.setText(CoinEnvelopes.getScannedCout(orderno));
    }

    private void alert(final int type) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        if (type == 1) {
            alertDialog.setTitle(getResources().getString(R.string.rescan_title));
            alertDialog.setMessage(getResources().getString(R.string.rescan_message));
        } else if (type == 2) {
            alertDialog.setTitle("Clear history?");
            alertDialog.setMessage("Current unloading cartridges will be deleted");
        } else if (type == 3) {
            alertDialog.setTitle("Duplicate");
            alertDialog.setMessage("Duplicate value");
        }

        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    if (type == 1) {
                        clearScan();
                    } else if (type == 2) {
                        Job.clearHistory(orderno, getString(R.string.unload));
                        refresh();
                        isHistoryCleared = true;
                        imghistoryclear.setVisibility(View.GONE);
                        clearScan();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        if (type == 1 || type == 2 || type==4) {
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        }
        alertDialog.show();
    }

    private void clearScan() {
        if (fragmenttype.equals(getString(R.string.loading))) {
            Cartridge.cancelScan(orderno, getString(R.string.load));
            CoinEnvelopes.cancelScan(orderno);
        } else {
            Cartridge.cancelScan(orderno, getString(R.string.unload));
        }
        if (isHistoryCleared) {
            Job.clearHistory(orderno, getString(R.string.unload));
        }
        txtnumpending.setText(String.valueOf(cartridgeList.size()));
        txtnumscanned.setText("0");
        txtnumincomplete.setText("0");
        isSerialScan = true;
        imgClear.setEnabled(false);
        refresh();
        refreshcoin();
    }

    private void requestforEdit(final JSONObject jsonObject) {
        try {
            final int cartid = jsonObject.getInt("cartId");
            final Boolean tick = jsonObject.getBoolean("isSerial");
            String serial = jsonObject.getString("serial");
            ((ProcessJobActivity) getActivity()).showProgressDialog("Loading...");
            String requestType;
            String requeststr = "";
            String ftype = "";
            if (fragmenttype.equals(getString(R.string.loading))) ftype = "LOAD";
            else ftype = "UNLOAD";
            Log.e("iscoin", ":" + jsonObject.getBoolean("isCoin"));
            if (jsonObject.getBoolean("isCoin")) {
                JSONArray jsonArray = jsonObject.getJSONArray("selectedcoin");
                for (int i = 0; i < jsonArray.length(); i++) {
                    requeststr = stringwithcoma(requeststr, jsonArray.get(i).toString());
                }
                requestType = "MANUAL ENTRY PERMISSION FOR COIN ENVELOPES (" + requeststr + ") TO " + ftype + " ATM-" + Job.getSingle(orderno).ATMCode + ".";
            } else {
                if (tick) {
                    if (serial.isEmpty())
                        requeststr = stringwithcoma(requeststr, "SERIAL");
                    else
                        requeststr = stringwithcoma(requeststr, "SERIAL (" + serial + ")");
                }
                String sealstr = "";
                JSONArray sealarray=jsonObject.getJSONArray("sealarray");
                Boolean isSealChecked=false;
                for(int i=0;i<sealarray.length();i++)
                {
                    JSONObject sealObject=sealarray.getJSONObject(i);
                    if (sealObject.getBoolean("isSeal")) {
                        if(!sealObject.getString("seal").isEmpty()) {
                            sealstr = stringwithcoma(sealstr, sealObject.getString("seal"));
                        }
                        isSealChecked=true;
                    }
                }
                if (!sealstr.isEmpty()) {
                    requeststr = stringwithcoma(requeststr, "SEAL (" + sealstr + ")");
                } else if (isSealChecked) {
                    requeststr = stringwithcoma(requeststr, "SEAL");
                }
                requestType = "MANUAL ENTRY PERMISSION FOR " + requeststr + " OF CARTRIDGE (" + Cartridge.getByCartID(cartid).CartNo + ") TO " + ftype + " ATM-" + Job.getSingle(orderno).ATMCode + ".";

            }
            Log.e("req type", requestType);
            String requestedOn = CommonMethods.getCurrentDateTimeInFormat3(getActivity());
            Log.e("UserId", String.valueOf(Preferences.getInt("UserId", getActivity())));
            //        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.connectTimeout(1000, TimeUnit.SECONDS);
            httpClient.readTimeout(1000, TimeUnit.SECONDS);
            httpClient.writeTimeout(1000, TimeUnit.SECONDS);
//        httpClient.addInterceptor(logging);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(CertisCISCOServer.getPATH(getActivity()))
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(UnsafeOkHttpClient.getUnsafeOkHttpClient(httpClient))
                    .build();
            CertisCISCOServices service = retrofit.create(CertisCISCOServices.class);
            Log.e("RequestForEdit" , requestedOn);
            Call<ResponseBody> call = service.requestForEdit(Preferences.getString("AuthToken", getActivity()), Preferences.getInt("UserId", getActivity()), "ATM", requestType, String.valueOf(Preferences.getInt("UserId", getActivity())), requestedOn);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        ((ProcessJobActivity) getActivity()).hideProgressDialog();
                        int result_code = response.code();
                        Log.e("request_result_code", ":" + result_code);
                        String result_body = response.body().string();
                        result_body = result_body.replaceAll("\\W", "");

                        Log.e("request_result_messege", ":" + response.message());
                        Log.e("request_result_error", ":" + response.errorBody());
                        Log.e("request_resultbody", ":" + result_body);
                        if (result_code == 200) {
                            if (EditRequests.getSingle(orderno, fragmenttype, cartid) == null) {
                                EditRequests editRequests = new EditRequests();
                                editRequests.ATMOrderId = orderno;
                                editRequests.requestId = Long.parseLong(result_body);
                                editRequests.fragmenttype = fragmenttype;
                                editRequests.CartId = cartid;
                                editRequests.status = "PENDING";
                                editRequests.save();
                                if (jsonObject.getBoolean("isCoin")) {
                                    JSONArray jsonArray = jsonObject.getJSONArray("selectedcoin");
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        CoinEnvelopes.updateeditstatus(orderno, jsonArray.get(i).toString());
                                    }
                                } else {
                                    if (tick)
                                        Cartridge.updateeditstatus(orderno, fragmenttype, cartid);
                                    JSONArray sealarray=jsonObject.getJSONArray("sealarray");
                                    for(int i=0;i<sealarray.length();i++)
                                    {
                                        JSONObject sealObject=sealarray.getJSONObject(i);
                                        if (sealObject.getBoolean("isSeal")) {
                                            Seal.updateeditstatus(orderno, fragmenttype, cartid, i);
                                        }
                                    }
                                }
                            }
                            Toast.makeText(getActivity(), "Requested", Toast.LENGTH_SHORT).show();
                        } else if (result_code == 409) {
                            ((ProcessJobActivity) getActivity()).authalert(getActivity());
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.networkerror), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.networkerror), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    ((ProcessJobActivity) getActivity()).hideProgressDialog();
                    t.printStackTrace();
                    //raiseSnakbar(cl, getResources().getString(R.string.networkerror));
                    Toast.makeText(getActivity(), getResources().getString(R.string.networkerror), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestForClearHistory() {
        try {
            ((ProcessJobActivity) getActivity()).showProgressDialog("Loading...");
               String requestType = "CLEAR HISTORY PERMISSION FOR ATM-" + Job.getSingle(orderno).ATMCode + ".";
            String requestedOn = CommonMethods.getCurrentDateTimeInFormat3(getActivity());
            //        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.connectTimeout(1000, TimeUnit.SECONDS);
            httpClient.readTimeout(1000, TimeUnit.SECONDS);
            httpClient.writeTimeout(1000, TimeUnit.SECONDS);
//        httpClient.addInterceptor(logging);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(CertisCISCOServer.getPATH(getActivity()))
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(UnsafeOkHttpClient.getUnsafeOkHttpClient(httpClient))
                    .build();
            CertisCISCOServices service = retrofit.create(CertisCISCOServices.class);
            Call<ResponseBody> call = service.requestForEdit(Preferences.getString("AuthToken", getActivity()), Preferences.getInt("UserId", getActivity()), "ATM", requestType, String.valueOf(Preferences.getInt("UserId", getActivity())), requestedOn);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        ((ProcessJobActivity) getActivity()).hideProgressDialog();
                        int result_code = response.code();
                        Log.e("request_result_code", ":" + result_code);
                        String result_body = response.body().string();
                        result_body = result_body.replaceAll("\\W", "");

                        Log.e("request_result_messege", ":" + response.message());
                        Log.e("request_result_error", ":" + response.errorBody());
                        Log.e("request_resultbody", ":" + result_body);
                        if (result_code == 200) {
                            if (ClearHistoryRequests.getSingle(orderno) == null) {
                                ClearHistoryRequests clearHistoryRequests = new ClearHistoryRequests();
                                clearHistoryRequests.ATMOrderId = orderno;
                                clearHistoryRequests.requestId = Long.parseLong(result_body);
                                clearHistoryRequests.status = "PENDING";
                                clearHistoryRequests.save();
                            }
                            Toast.makeText(getActivity(), "Requested", Toast.LENGTH_SHORT).show();
                        } else if (result_code == 409) {
                            ((ProcessJobActivity) getActivity()).authalert(getActivity());
                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.networkerror), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.networkerror), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    ((ProcessJobActivity) getActivity()).hideProgressDialog();
                    t.printStackTrace();
                    //raiseSnakbar(cl, getResources().getString(R.string.networkerror));
                    Toast.makeText(getActivity(), getResources().getString(R.string.networkerror), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void checkRequestStatus() {
        if (NetworkUtil.getConnectivityStatusString(getActivity())) {
            ((ProcessJobActivity) getActivity()).showProgressDialog("Loading...");
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.connectTimeout(1000, TimeUnit.SECONDS);
            httpClient.readTimeout(1000, TimeUnit.SECONDS);
            httpClient.writeTimeout(1000, TimeUnit.SECONDS);
//        httpClient.addInterceptor(logging);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(CertisCISCOServer.getPATH(getActivity()))
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(UnsafeOkHttpClient.getUnsafeOkHttpClient(httpClient))
                    .build();
            CertisCISCOServices service = retrofit.create(CertisCISCOServices.class);
            Call<ResponseBody> call = service.requestStatus(Preferences.getString("AuthToken", getActivity()), Preferences.getInt("UserId", getActivity()), ClearHistoryRequests.getRequetedIDs(orderno));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        ((ProcessJobActivity) getActivity()).hideProgressDialog();
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
                                    ClearHistoryRequests.updateSingle(requestedid, status);
                                    alert(2);
                                } else if (status.equals("INVALID") || status.equals("REJECTED")) {
                                    ClearHistoryRequests.removeSingle(requestedid);
                                    Toast.makeText(getActivity(), "Request is "+status.toLowerCase(), Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(getActivity(), "Request is "+status.toLowerCase(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else if (result_code == 409) {
                            ((ProcessJobActivity) getActivity()).authalert(getActivity());
                        } else {
                            //((ProcessJobActivity) getActivity()).raiseSnakbar(cl, getResources().getString(R.string.networkerror));
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.networkerror), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
//                    raiseSnakbar(cl, getResources().getString(R.string.networkerror));
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.networkerror), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    ((ProcessJobActivity) getActivity()).hideProgressDialog();
                    t.printStackTrace();
                    //raiseSnakbar(cl, getResources().getString(R.string.networkerror));
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.networkerror), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void fragmentBecameVisible() {
        ((BarCodeScanActivity)getContext()).registerScannerEvent(this);
    }

    private String stringwithcoma(String string1, String string2) {
        if (string1.equals("")) {
            return string2;
        } else {
            return string1 + " , " + string2;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        ((BarCodeScanActivity)getContext()).registerScannerEvent(this);
    }
}

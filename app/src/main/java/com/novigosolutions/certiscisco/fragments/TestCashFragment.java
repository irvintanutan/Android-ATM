package com.novigosolutions.certiscisco.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.activities.BarCodeScanActivity;
import com.novigosolutions.certiscisco.activities.ProcessJobActivity;
import com.novigosolutions.certiscisco.adapters.TestCashChipListAdapter;
import com.novigosolutions.certiscisco.interfaces.FragmentInterface;
import com.novigosolutions.certiscisco.interfaces.IOnScannerData;
import com.novigosolutions.certiscisco.interfaces.RecyclerViewClickListenerLong;
import com.novigosolutions.certiscisco.models.EditRequests;
import com.novigosolutions.certiscisco.models.Job;
import com.novigosolutions.certiscisco.models.TestCash;
import com.novigosolutions.certiscisco.service.UserLogService;
import com.novigosolutions.certiscisco.utils.CommonMethods;
import com.novigosolutions.certiscisco.utils.Constants;
import com.novigosolutions.certiscisco.utils.CustomDialogClass;
import com.novigosolutions.certiscisco.utils.NetworkUtil;
import com.novigosolutions.certiscisco.utils.Preferences;
import com.novigosolutions.certiscisco.utils.UserLog;
import com.novigosolutions.certiscisco.webservices.ApiCallback;
import com.novigosolutions.certiscisco.webservices.CertisCISCOServer;
import com.novigosolutions.certiscisco.webservices.CertisCISCOServices;
import com.novigosolutions.certiscisco.webservices.SendUpdateCaller;
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

import static com.novigosolutions.certiscisco.R.id.recyclerview;

public class TestCashFragment extends Fragment implements IOnScannerData, View.OnClickListener, ApiCallback, RecyclerViewClickListenerLong, FragmentInterface {
    LinearLayout lldata, llnodata;//, llscannedlist;
    ImageView imgclear;
    Button prev, next;
    Button btn_test_cash, btn_junk_cash;
    String scantype = "", scantypename = "";
    int orderno = 0;
    LinearLayout llmessage;
    RecyclerView recyclerView;
    TestCashChipListAdapter mAdapter;
    List<TestCash> list = new ArrayList<>();
    ImageView imgManualentry;

    public TestCashFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_test_cash, container, false);
        initializeviews(rootView);
        setactions();
        return rootView;

    }

    private void initializeviews(View rootView) {
        btn_test_cash = (Button) rootView.findViewById(R.id.btn_test_cash);
        btn_junk_cash = (Button) rootView.findViewById(R.id.btn_junk_cash);
        lldata = (LinearLayout) rootView.findViewById(R.id.lldata);
        llnodata = (LinearLayout) rootView.findViewById(R.id.llnodata);

        recyclerView = (RecyclerView) rootView.findViewById(recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //llscannedlist = (LinearLayout) rootView.findViewById(R.id.llscannedlist);
        imgclear = (ImageView) rootView.findViewById(R.id.imgclear);
        prev = (Button) rootView.findViewById(R.id.btn_prev);
        next = (Button) rootView.findViewById(R.id.btn_next);
        llmessage = (LinearLayout) rootView.findViewById(R.id.llmessage);
        imgManualentry = (ImageView) rootView.findViewById(R.id.img_manual_entry);
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            orderno = extras.getInt("orderno");
        }
        try {
            scantype = "TESTCASH";
            scantypename = "Test Cash";
            //((ProcessJobActivity) getActivity()).setScanDataPass(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
        refresh();

    }

    private void setactions() {
        btn_test_cash.setOnClickListener(this);
        btn_junk_cash.setOnClickListener(this);
        imgclear.setOnClickListener(this);
        imgManualentry.setOnClickListener(this);
        prev.setOnClickListener(this);
        next.setOnClickListener(this);
    }

    @Override
    public void onDataScanned(String data) {
        if (data != null && !data.equals("")) {
            if (Job.isExist(data)) {
                alert(2);
            } else {
                addData(data);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {

            case R.id.btn_test_cash:
                scantype = "TESTCASH";
                scantypename = "Test Cash";
                try {
                    ((BarCodeScanActivity)getContext()).scansoft();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case R.id.btn_junk_cash:
                scantype = "JUNKCASH";
                scantypename = "Jammed Cash";
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
                CustomDialogClass cdd = new CustomDialogClass(getActivity(), this, orderno, false);
                cdd.show();
//                Job.updateStatus(orderno);
//                getActivity().finish();
                break;

            case R.id.img_manual_entry:
                String status = EditRequests.getStatus(orderno, scantype);
                if (status.equals("CONFIRMED")) {
                    dialoguemanualinput();
                } else if (status.equals("NOREQUEST")) {
                    alert(3);
                } else {
                    checkRequestStatus();
                }
                break;
        }
        if (scantype.equals("TESTCASH")) {
            btn_test_cash.setBackgroundResource(R.drawable.button_green);
            btn_junk_cash.setBackgroundResource(R.drawable.button_shape);
        } else if (scantype.equals("JUNKCASH")) {
            btn_test_cash.setBackgroundResource(R.drawable.button_shape);
            btn_junk_cash.setBackgroundResource(R.drawable.button_green);
        }
    }

    private void addData(String data) {
        ///LinearLayout linearLayout = new LinearLayout(getActivity());
        TestCash testCash = new TestCash();
        testCash.ATMOrderId = orderno;
        testCash.ScanType = scantype;
        testCash.ScanTypeName = scantypename;
        testCash.ScanValue = data;
        testCash.save();
        refresh();
        UserLogService.save(UserLog.TEST_CASH.toString(), String.format("ATMOrderId : %s , ScanType : %s , " +
                        "ScanTypeName : %s , ScanValue : %s", Job.getATMCode(orderno), scantype, scantypename, data),
                "ENVELOPE DATA", null, getActivity());
    }

    private void refresh() {
        List<TestCash> templist = TestCash.get(orderno);
        list.clear();
        list.addAll(templist);
        if (list.size() > 0) {
            sethasdata();
            if (mAdapter == null) {
                mAdapter = new TestCashChipListAdapter(list, this);
                recyclerView.setAdapter(mAdapter);
            } else {
                mAdapter.notifyDataSetChanged();
            }
        } else {
            sethasnodata();

        }
        refreshmessage();
    }

    private void alert(final int type) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        String btnText = "Yes";
        if (type == 1) {
            alertDialog.setTitle(getResources().getString(R.string.rescan_title));
            alertDialog.setMessage(getResources().getString(R.string.rescan_message));
        } else if (type == 2) {
            alertDialog.setTitle("Duplicate");
            alertDialog.setMessage("Duplicate value");
            btnText = "Ok";
        } else if (type == 3) {
            alertDialog.setTitle("Confirm");
            if (scantype.equals("TESTCASH")) {
                alertDialog.setMessage("Are you sure you want to request for edit test cash?");
            } else if (scantype.equals("JUNKCASH")) {
                alertDialog.setMessage("Are you sure you want to request for edit jammed cash?");
            }
        }
        alertDialog.setPositiveButton(btnText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (type == 1) {
                    TestCash.cancelScan(orderno);
                    refresh();
                } else if (type == 3) {
                    requestforEdit();
                }
            }
        });
        if (type == 1 || type == 3) {
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        }
        alertDialog.show();
    }

    @Override
    public void onResult(int result, String resultdata) {
        if (result == 200) {
            try {
                Log.e("resultdata", Constants.requestBody);
                JSONObject obj = new JSONObject(Constants.requestBody);
                String strresult = obj.getString("Result");
                String messege = obj.getString("Message");
                if (strresult.equals("Success")) {
                    Job.updateStatus(orderno);
                    getActivity().finish();
                    SendUpdateCaller.instance().sendUpdate(getActivity());
                    UserLogService.save(UserLog.SYNC.toString(), String.format("ATMOrderId : %s", Job.getATMCode(orderno)),
                            "SUCCESS", null, getContext());
                } else {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        } else if (result == 409) {
            ((ProcessJobActivity) getActivity()).authalert(getActivity());
        }
    }

    private void refreshmessage() {
        llmessage.removeAllViews();
        int ctestcash = TestCash.getCount(orderno, "TESTCASH").size();
        if (ctestcash > 0) {
            TextView textView = new TextView(getActivity());
            textView.setText(ctestcash + " test cash scanned");
            llmessage.addView(textView);
        }
        int cjunkcash = TestCash.getCount(orderno, "JUNKCASH").size();
        if (cjunkcash > 0) {
            TextView textView = new TextView(getActivity());
            textView.setText(cjunkcash + " Jammed cash scanned");
            llmessage.addView(textView);
        }
    }

    private void sethasdata() {
        llnodata.setVisibility(View.GONE);
        lldata.setVisibility(View.VISIBLE);
        llmessage.setVisibility(View.VISIBLE);
    }

    private void sethasnodata() {
        llnodata.setVisibility(View.VISIBLE);
        lldata.setVisibility(View.GONE);
        llmessage.setVisibility(View.GONE);
    }

    @Override
    public void recyclerViewListClicked(Long id) {
        TestCash.cancelSingleScan(id);
        refresh();
    }

    @Override
    public void onResume() {
        super.onResume();
//        ((BarCodeScanActivity)getContext()).registerScannerEvent(this);
    }

    private void requestforEdit() {
        try {
            String requestType = "";
            if (scantype.equals("TESTCASH")) {
                requestType = "MANUAL ENTRY PERMISSION FOR TEST CASH TO LOAD ATM-" + Job.getSingle(orderno).ATMCode + ".";
            } else if (scantype.equals("JUNKCASH")) {
                requestType = "MANUAL ENTRY PERMISSION FOR JAMMED CASH TO LOAD ATM-" + Job.getSingle(orderno).ATMCode + ".";
            }
            String requestedOn = CommonMethods.getCurrentDateTimeInFormat3(getActivity());
            //        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.connectTimeout(30, TimeUnit.SECONDS);
            httpClient.readTimeout(30, TimeUnit.SECONDS);
            httpClient.writeTimeout(30, TimeUnit.SECONDS);
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
                            if (EditRequests.getSingle(orderno, scantype) == null) {
                                EditRequests editRequests = new EditRequests();
                                editRequests.ATMOrderId = orderno;
                                editRequests.requestId = Long.parseLong(result_body);
                                editRequests.fragmenttype = scantype;
                                editRequests.CartId = -7;
                                editRequests.status = "PENDING";
                                editRequests.save();
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
            httpClient.connectTimeout(30, TimeUnit.SECONDS);
            httpClient.readTimeout(30, TimeUnit.SECONDS);
            httpClient.writeTimeout(30, TimeUnit.SECONDS);
//        httpClient.addInterceptor(logging);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(CertisCISCOServer.getPATH(getActivity()))
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(UnsafeOkHttpClient.getUnsafeOkHttpClient(httpClient))
                    .build();
            CertisCISCOServices service = retrofit.create(CertisCISCOServices.class);
            Call<ResponseBody> call = service.requestStatus(Preferences.getString("AuthToken", getActivity()), Preferences.getInt("UserId", getActivity()), EditRequests.getRequetedIDs(orderno, scantype));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        ((ProcessJobActivity) getActivity()).hideProgressDialog();
                        int result_code = response.code();
                        Log.e("status_result_code", ":" + result_code);
                        Log.e("status_result_messege", ":" + response.message());
                        Log.e("status_result_error", ":" + response.errorBody());
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
                            String status = EditRequests.getStatus(orderno, scantype);
                            if (status.equals("CONFIRMED")) {
                                dialoguemanualinput();
                            } else
                                Toast.makeText(getActivity(), "Request is pending", Toast.LENGTH_SHORT).show();
                        } else if (result_code == 409) {
                            ((ProcessJobActivity) getActivity()).authalert(getActivity());
                        } else {
                            //((ProcessJobActivity) getActivity()).raiseSnakbar(cl, getResources().getString(R.string.networkerror));
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.networkerror), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
//                    raiseSnakbar(cl, getResources().getString(R.string.networkerror));
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.datakerror), Toast.LENGTH_SHORT).show();
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

    private void dialoguemanualinput() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        final View dialogView = inflater.inflate(R.layout.misc_input_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.misc_input);
        edt.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
                if (edt.getText().length() > 0) {
                    edt.setError(null);
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
        if (scantype.equals("TESTCASH")) {
            dialogBuilder.setTitle("Enter test cash");
        } else if (scantype.equals("JUNKCASH")) {
            dialogBuilder.setTitle("Enter jammed cash");
        }
        ///dialogBuilder.setMessage("Enter misc input");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        final AlertDialog b = dialogBuilder.create();
        b.show();
        b.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textmiscinput = edt.getText().toString().toUpperCase();
                if (textmiscinput.length() > 0) {
                    onDataScanned(textmiscinput);
                    b.cancel();
                } else edt.setError("Cannot be empty.");
            }
        });
    }

    @Override
    public void fragmentBecameVisible() {
        ((BarCodeScanActivity)getContext()).registerScannerEvent(this);
    }
}

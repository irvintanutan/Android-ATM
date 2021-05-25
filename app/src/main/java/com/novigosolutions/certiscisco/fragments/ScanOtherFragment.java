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

import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.activities.BarCodeScanActivity;
import com.novigosolutions.certiscisco.activities.ProcessJobActivity;
import com.novigosolutions.certiscisco.adapters.ScanOtherChipListAdapter;
import com.novigosolutions.certiscisco.interfaces.FragmentInterface;
import com.novigosolutions.certiscisco.interfaces.IOnScannerData;
import com.novigosolutions.certiscisco.interfaces.RecyclerViewClickListenerLong;
import com.novigosolutions.certiscisco.models.Job;
import com.novigosolutions.certiscisco.models.OtherScan;
import com.novigosolutions.certiscisco.utils.Constants;
import com.novigosolutions.certiscisco.utils.CustomDialogClass;
import com.novigosolutions.certiscisco.webservices.ApiCallback;
import com.novigosolutions.certiscisco.webservices.SendUpdateCaller;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.novigosolutions.certiscisco.R.id.recyclerview;

public class  ScanOtherFragment extends Fragment implements IOnScannerData, View.OnClickListener, RecyclerViewClickListenerLong, FragmentInterface, ApiCallback {
    // TODO: Rename parameter arguments, choose names that match
    LinearLayout lldata, llnodata, llmessage;//, llscannedlist;
    Button btn_test_cash, btn_passbook, btn_test_rjr, btn_retain_card, btn_misc_scan, btn_misc_input;
    ImageView imgclear;
    Button prev, next;
    Button prevchoosedbutton;
    int orderno = 0;
    //int ctestcash = 0, cpassbook = 0, ctestrjr = 0, cretaincard = 0, cmiscscan = 0, cmiscinput = 0;
    String scantype = "", scantypename = "", operationMode;
    private RecyclerView recyclerView;
    private ScanOtherChipListAdapter mAdapter;
    List<OtherScan> list = new ArrayList<>();

    public ScanOtherFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scan_other, container, false);
        initializeviews(rootView);
        setactions();
        return rootView;
    }

    private void initializeviews(View rootView) {
        btn_test_cash = (Button) rootView.findViewById(R.id.btn_test_cash);
        btn_passbook = (Button) rootView.findViewById(R.id.btn_passbook);
        btn_test_rjr = (Button) rootView.findViewById(R.id.btn_test_rjr);
        btn_retain_card = (Button) rootView.findViewById(R.id.btn_retain_card);
        btn_misc_scan = (Button) rootView.findViewById(R.id.btn_misc_scan);
        btn_misc_input = (Button) rootView.findViewById(R.id.btn_misc_input);
        lldata = (LinearLayout) rootView.findViewById(R.id.lldata);
        llnodata = (LinearLayout) rootView.findViewById(R.id.llnodata);
        llmessage = (LinearLayout) rootView.findViewById(R.id.llmessage);
        recyclerView = (RecyclerView) rootView.findViewById(recyclerview);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        prevchoosedbutton = btn_test_cash;
        scantype = "TESTCASH";
        scantypename = "Test Cash";
        //llscannedlist = (LinearLayout) rootView.findViewById(R.id.llscannedlist);
        imgclear = (ImageView) rootView.findViewById(R.id.imgclear);
        prev = (Button) rootView.findViewById(R.id.btn_prev);
        next = (Button) rootView.findViewById(R.id.btn_next);
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            orderno = extras.getInt("orderno");
        }
        operationMode = Job.getOperationMode(orderno);
        if (operationMode.equals(getString(R.string.unload))) {
            next.setText(getResources().getString(R.string.stringComplerte));
        }
        try {
            //((ProcessJobActivity) getActivity()).setScanDataPass(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
        refresh();
    }

    private void setactions() {
        btn_test_cash.setOnClickListener(this);
        btn_passbook.setOnClickListener(this);
        btn_test_rjr.setOnClickListener(this);
        btn_retain_card.setOnClickListener(this);
        btn_misc_scan.setOnClickListener(this);
        btn_misc_input.setOnClickListener(this);
        imgclear.setOnClickListener(this);
        prev.setOnClickListener(this);
        next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Button currentButton = null;
        Boolean checkCurrentButton = false;
        switch (id) {
            case R.id.btn_test_cash:
                scantype = "TESTCASH";
                scantypename = "Test Cash";
                currentButton = btn_test_cash;
                try {
                    ((BarCodeScanActivity)getContext()).scansoft();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                checkCurrentButton = true;
                break;
            case R.id.btn_passbook:
                scantype = "PASSBOOK";
                scantypename = "Passbook";
                currentButton = btn_passbook;
                try {
                    ((BarCodeScanActivity)getContext()).scansoft();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                checkCurrentButton = true;
                break;
            case R.id.btn_test_rjr:
                scantype = "RJR";
                scantypename = "RJR";
                currentButton = btn_test_rjr;
                try {
                    ((BarCodeScanActivity)getContext()).scansoft();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                checkCurrentButton = true;
                break;
            case R.id.btn_retain_card:
                scantype = "RETAIN";
                scantypename = "Retain Card";
                currentButton = btn_retain_card;
                try {
                    ((BarCodeScanActivity)getContext()).scansoft();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                checkCurrentButton = true;
                break;
            case R.id.btn_misc_scan:
                scantype = "MISCSCAN";
                scantypename = "Misc Scan";
                currentButton = btn_misc_scan;
                try {
                    ((BarCodeScanActivity)getContext()).scansoft();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                checkCurrentButton = true;
                break;
            case R.id.btn_misc_input:
                scantype = "MISCINPUT";
                scantypename = "Misc Input";
                currentButton = btn_misc_input;
//                try {
//                    ((ProcessJobActivity) getActivity()).scansoft(this);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                dialoguemiscinput();
                checkCurrentButton = true;
                break;
            case R.id.imgclear:
                alert(1, null);
                break;
            case R.id.btn_prev:
                ((ProcessJobActivity) getActivity()).setpage(-1);
                break;
            case R.id.btn_next:
                if (operationMode.equals(getString(R.string.unload))) {
                    CustomDialogClass cdd = new CustomDialogClass(getActivity(), this, orderno, false);
                    cdd.show();
                } else {
                    ((ProcessJobActivity) getActivity()).setpage(1);
                }
                break;
        }
        if (checkCurrentButton && currentButton != prevchoosedbutton) {
            if (prevchoosedbutton != null) {
                prevchoosedbutton.setBackgroundResource(R.drawable.button_shape);
            }
            if (currentButton != null) {
                currentButton.setBackgroundResource(R.drawable.button_green);
                prevchoosedbutton = currentButton;
            }
        }


    }

    @Override
    public void onDataScanned(String data) {
        if (data != null && !data.equals("")) {
            if (Job.isExist(data)) {
                alert(3, null);
            } else {
                addData(data);
            }
        }
    }

    private void alert(final int type, final Long id) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        if (type == 1) {
            alertDialog.setTitle(getResources().getString(R.string.rescan_title));
            alertDialog.setMessage(getResources().getString(R.string.rescan_message));
        } else if (type == 2) {
            alertDialog.setTitle("Delete?");
            alertDialog.setMessage("Are you sure you want to delete?");
        } else if (type == 3) {
            alertDialog.setTitle("Duplicate");
            alertDialog.setMessage("Duplicate value");
        }
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (type == 1) {
                    OtherScan.cancelScan(orderno);
                    list.clear();
                    refresh();
                } else if (type == 2) {
                    OtherScan.cancelSingleScan(id);
                    refresh();
                }
            }
        });
        if (type == 1 || type == 2) {
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        }
        alertDialog.show();
    }

    private void addData(String data) {
        ///LinearLayout linearLayout = new LinearLayout(getActivity());
        OtherScan otherScan = new OtherScan();
        otherScan.ATMOrderId = orderno;
        otherScan.ScanType = scantype;
        otherScan.ScanTypeName = scantypename;
        otherScan.ScanValue = data;
        otherScan.save();
        refresh();

    }

    private void refresh() {
        List<OtherScan> templist = OtherScan.get(orderno);
        list.clear();
        list.addAll(templist);
        if (list.size() > 0) {
            sethasdata();
            if (mAdapter == null) {
                mAdapter = new ScanOtherChipListAdapter(list, this);
                recyclerView.setAdapter(mAdapter);
            } else {

                mAdapter.notifyDataSetChanged();
            }

        } else {

            sethasnodata();
        }
        refreshmessage();
    }

    private void refreshmessage() {
        llmessage.removeAllViews();
        int ctestcash = OtherScan.getCount(orderno, "TESTCASH").size();
        if (ctestcash > 0) {
            TextView textView = new TextView(getActivity());
            textView.setText(ctestcash + " Test cash scanned");
            llmessage.addView(textView);
        }
        int cpassbook = OtherScan.getCount(orderno, "PASSBOOK").size();
        if (cpassbook > 0) {
            TextView textView = new TextView(getActivity());
            textView.setText(cpassbook + " Passbook scanned");
            llmessage.addView(textView);
        }
        int ctestrjr = OtherScan.getCount(orderno, "RJR").size();
        if (ctestrjr > 0) {
            TextView textView = new TextView(getActivity());
            textView.setText(ctestrjr + " RJR scanned");
            llmessage.addView(textView);
        }
        int cretaincard = OtherScan.getCount(orderno, "RETAIN").size();
        if (cretaincard > 0) {
            TextView textView = new TextView(getActivity());
            textView.setText(cretaincard + " Retain card scanned");
            llmessage.addView(textView);
        }
        int cmiscscan = OtherScan.getCount(orderno, "MISCSCAN").size();
        if (cmiscscan > 0) {
            TextView textView = new TextView(getActivity());
            textView.setText(cmiscscan + " Misc scan scanned");
            llmessage.addView(textView);
        }
        int cmiscinput = OtherScan.getCount(orderno, "MISCINPUT").size();
        if (cmiscinput > 0) {
            TextView textView = new TextView(getActivity());
            textView.setText(cmiscinput + " Misc input");
            llmessage.addView(textView);
        }

    }

    private void sethasdata() {
        llnodata.setVisibility(View.GONE);
        lldata.setVisibility(View.VISIBLE);
    }

    private void sethasnodata() {
        llnodata.setVisibility(View.VISIBLE);
        lldata.setVisibility(View.GONE);
    }

    @Override
    public void recyclerViewListClicked(Long id) {
        alert(2, id);
    }

    private void dialoguemiscinput() {
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
        dialogBuilder.setTitle("Enter misc input");
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
                } else {
                    //raiseSnakbar(cl, messege);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (result == 409) {
            ((ProcessJobActivity) getActivity()).authalert(getActivity());
        }
    }
    @Override
    public void fragmentBecameVisible() {
        ((BarCodeScanActivity)getContext()).registerScannerEvent(this);
    }
    @Override
    public void onResume() {
        super.onResume();
//        ((BarCodeScanActivity)getContext()).registerScannerEvent(this);
    }
}

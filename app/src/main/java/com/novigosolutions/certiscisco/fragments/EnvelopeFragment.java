package com.novigosolutions.certiscisco.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.text.Editable;
import android.text.TextWatcher;
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
import com.novigosolutions.certiscisco.webservices.ApiCallback;
import com.novigosolutions.certiscisco.webservices.SendUpdateCaller;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class EnvelopeFragment extends Fragment implements IOnScannerData, View.OnClickListener, ApiCallback, RecyclerViewClickListenerLong, FragmentInterface {

    @BindView(R.id.btn_retained_card)
    Button retainedCard;
    @BindView(R.id.btn_jammed_cash)
    Button jammedCash;
    @BindView(R.id.btn_passbook)
    Button passbook;
    @BindView(R.id.lldata)
    LinearLayout llData;
    @BindView(R.id.llnodata)
    LinearLayout llNoData;
    @BindView(R.id.llmessage)
    LinearLayout llMessage;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.imgclear)
    ImageView imgClear;

    Button prevChooseButton;
    int orderNo = 0;
    String scanType = "", scanTypeName = "";
    private ScanOtherChipListAdapter mAdapter;
    List<OtherScan> list = new ArrayList<>();


    public EnvelopeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_envelope, container, false);
        ButterKnife.bind(this, rootView);

        initializeViews(rootView);
        setActions();

        return rootView;
    }

    @OnClick(R.id.btn_next)
    void next() {
        ((ProcessJobActivity) getActivity()).setpage(1);
    }

    @OnClick(R.id.cancel_action)
    void cancel() {
        ((ProcessJobActivity) getActivity()).alert(1, "Confirm", "Confirm Exit Job?");
    }


    private void initializeViews(View rootView) {

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        prevChooseButton = retainedCard;
        scanType = "RETAIN";
        scanTypeName = "Retained Card";
        imgClear = (ImageView) rootView.findViewById(R.id.imgclear);
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            orderNo = extras.getInt("orderno");
        }

        refresh();
    }

    private void setActions() {
        retainedCard.setOnClickListener(this);
        passbook.setOnClickListener(this);
        jammedCash.setOnClickListener(this);
        imgClear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Button currentButton = null;
        Boolean checkCurrentButton = false;
        switch (id) {
            case R.id.btn_passbook:
                scanType = "PASSBOOK";
                scanTypeName = "Passbook";
                currentButton = passbook;
                try {
                    ((BarCodeScanActivity) getContext()).scansoft();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                checkCurrentButton = true;
                break;
            case R.id.btn_jammed_cash:
                scanType = "JAMMED";
                scanTypeName = "Jammed Cash";
                currentButton = jammedCash;
                try {
                    ((BarCodeScanActivity) getContext()).scansoft();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                checkCurrentButton = true;
                break;
            case R.id.btn_retained_card:
                scanType = "RETAIN";
                scanTypeName = "Retained Card";
                currentButton = retainedCard;
                try {
                    ((BarCodeScanActivity) getContext()).scansoft();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                checkCurrentButton = true;
                break;
            case R.id.imgclear:
                alert(1, null);
                break;
        }
        if (checkCurrentButton && currentButton != prevChooseButton) {
            if (prevChooseButton != null) {
                prevChooseButton.setBackgroundResource(R.drawable.button_shape);
            }
            if (currentButton != null) {
                currentButton.setBackgroundResource(R.drawable.button_green);
                prevChooseButton = currentButton;
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
                    OtherScan.cancelScan(orderNo);
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
        otherScan.ATMOrderId = orderNo;
        otherScan.ScanType = scanType;
        otherScan.ScanTypeName = scanTypeName;
        otherScan.ScanValue = data;
        otherScan.save();
        refresh();

    }

    private void refresh() {
        List<OtherScan> templist = OtherScan.get(orderNo);
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
        llMessage.removeAllViews();
        int jammedCash = OtherScan.getCount(orderNo, "JAMMED").size();
        if (jammedCash > 0) {
            TextView textView = new TextView(getActivity());
            textView.setText(jammedCash + " Jammed Cash scanned");
            llMessage.addView(textView);
        }
        int passbook = OtherScan.getCount(orderNo, "PASSBOOK").size();
        if (passbook > 0) {
            TextView textView = new TextView(getActivity());
            textView.setText(passbook + " Passbook scanned");
            llMessage.addView(textView);
        }
        int retainedCard = OtherScan.getCount(orderNo, "RETAIN").size();
        if (retainedCard > 0) {
            TextView textView = new TextView(getActivity());
            textView.setText(retainedCard + " Retained card scanned");
            llMessage.addView(textView);
        }
    }

    private void sethasdata() {
        llNoData.setVisibility(View.GONE);
        llData.setVisibility(View.VISIBLE);
    }

    private void sethasnodata() {
        llNoData.setVisibility(View.VISIBLE);
        llData.setVisibility(View.GONE);
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
                JSONObject obj = new JSONObject(resultdata);
                String strresult = obj.getString("Result");
                String messege = obj.getString("Message");
                if (strresult.equals("Success")) {
                    Job.updateStatus(orderNo);
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
        ((BarCodeScanActivity) getContext()).registerScannerEvent(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
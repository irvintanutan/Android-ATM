package com.novigosolutions.certiscisco.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.novigosolutions.certiscisco.R;
import com.novigosolutions.certiscisco.activities.ProcessJobActivity;
import com.novigosolutions.certiscisco.interfaces.FragmentInterface;
import com.novigosolutions.certiscisco.models.Denomination;
import com.novigosolutions.certiscisco.models.Job;
import com.novigosolutions.certiscisco.utils.Constants;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static com.novigosolutions.certiscisco.utils.Constants.denomination;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class DenominationFragment extends Fragment implements FragmentInterface {

    @BindView(R.id.text1000)
    EditText text1000;

    @BindView(R.id.text100)
    EditText text100;

    @BindView(R.id.text50)
    EditText text50;

    @BindView(R.id.text10)
    EditText text10;

    @BindView(R.id.text5)
    EditText text5;

    @BindView(R.id.text2)
    EditText text2;

    @BindView(R.id.text1)
    EditText text1;

    @BindView(R.id.text0_50)
    EditText text0_50;

    @BindView(R.id.text0_20)
    EditText text0_20;

    @BindView(R.id.text0_10)
    EditText text0_10;

    @BindView(R.id.text0_05)
    EditText text0_05;

    @BindView(R.id.textTotal)
    EditText textTotal;

    @BindView(R.id.highReject)
    CheckBox highReject;

    @BindView(R.id.noCashFound)
    CheckBox noCashFound;


    int orderNo;
    Double total = 0.00;

    public DenominationFragment() {
        // Required empty public constructor
        super(R.layout.fragment_denomination);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_denomination, container, false);
        ButterKnife.bind(this, rootView);

        initialize();

        return rootView;
    }

    @OnClick(R.id.highReject)
    void setHighReject() {
        if (highReject.isChecked()) {
            clear();
            noCashFound.setChecked(false);
        }
    }

    @OnClick(R.id.noCashFound)
    void setNoCashFound() {
        if (noCashFound.isChecked()) {
            clear();
            highReject.setChecked(false);
        }
    }

    void initialize() {
        Bundle extras = getActivity().getIntent().getExtras();
        if (extras != null) {
            orderNo = extras.getInt("orderno");
            Log.e("orderno", ":" + orderNo);
        }

        text1000.addTextChangedListener(new GenericTextWatcher(text1000));
        text100.addTextChangedListener(new GenericTextWatcher(text100));
        text50.addTextChangedListener(new GenericTextWatcher(text50));
        text10.addTextChangedListener(new GenericTextWatcher(text10));
        text5.addTextChangedListener(new GenericTextWatcher(text5));
        text2.addTextChangedListener(new GenericTextWatcher(text2));
        text1.addTextChangedListener(new GenericTextWatcher(text1));
        text0_50.addTextChangedListener(new GenericTextWatcher(text0_50));
        text0_20.addTextChangedListener(new GenericTextWatcher(text0_20));
        text0_10.addTextChangedListener(new GenericTextWatcher(text0_10));
        text0_05.addTextChangedListener(new GenericTextWatcher(text0_05));
    }

    private void saveDenomination() {
        denomination.ATMOrderId = orderNo;
        denomination.HighReject = highReject.isChecked();
        denomination.NoCashFound = noCashFound.isChecked();
        denomination.OperationMode = Job.getOperationMode(orderNo);
        denomination.text0_05 = text0_05.getText().toString();
        denomination.text0_10 = text0_10.getText().toString();
        denomination.text0_20 = text0_20.getText().toString();
        denomination.text0_50 = text0_50.getText().toString();
        denomination.text1 = text1.getText().toString();
        denomination.text2 = text2.getText().toString();
        denomination.text5 = text5.getText().toString();
        denomination.text10 = text10.getText().toString();
        denomination.text50 = text50.getText().toString();
        denomination.text100 = text100.getText().toString();
        denomination.text1000 = text1000.getText().toString();
        denomination.textTotal = total.toString();
    }

    void clear() {
        text1000.setText("0");
        text100.setText("0");
        text50.setText("0");
        text10.setText("0");
        text5.setText("0");
        text2.setText("0");
        text1.setText("0");
        text0_50.setText("0");
        text0_20.setText("0");
        text0_10.setText("0");
        text0_05.setText("0");
    }


    @OnClick(R.id.btn_next)
    void next() {
        if (total == 0 && (!highReject.isChecked() && !noCashFound.isChecked()))
            ((ProcessJobActivity) getActivity()).alert("Total must be greater than 0");
        else {
            saveDenomination();
            ((ProcessJobActivity) getActivity()).setpage(1);
        }
    }

    @OnClick(R.id.cancel_action)
    void cancel() {
        ((ProcessJobActivity) getActivity()).alert(1, "Confirm", "Confirm Exit Job?");
    }

    @Override
    public void fragmentBecameVisible() {

    }

    private void calculate() {
        NumberFormat formatter = new DecimalFormat("0.00");
        total = 0.00;

        if (text1000.getText().toString() == null)
            text1000.setText("0");
        if (text100.getText().toString() == null)
            text100.setText("0");
        if (text50.getText().toString() == null)
            text50.setText("0");
        if (text10.getText().toString() == null)
            text10.setText("0");
        if (text5.getText().toString() == null)
            text5.setText("0");
        if (text2.getText().toString() == null)
            text2.setText("0");
        if (text1.getText().toString() == null)
            text1.setText("0");
        if (text0_50.getText().toString() == null)
            text0_50.setText("0");
        if (text0_20.getText().toString() == null)
            text0_20.setText("0");
        if (text0_10.getText().toString() == null)
            text0_10.setText("0");
        if (text0_05.getText().toString() == null)
            text0_05.setText("0");

        total += Integer.parseInt(text1000.getText().toString()) * 1000;
        total += Integer.parseInt(text100.getText().toString()) * 100;
        total += Integer.parseInt(text50.getText().toString()) * 50;
        total += Integer.parseInt(text10.getText().toString()) * 10;
        total += Integer.parseInt(text5.getText().toString()) * 5;
        total += Integer.parseInt(text2.getText().toString()) * 2;
        total += Integer.parseInt(text1.getText().toString()) * 1;
        total += Integer.parseInt(text0_50.getText().toString()) * 0.5;
        total += Integer.parseInt(text0_20.getText().toString()) * 0.2;
        total += Integer.parseInt(text0_10.getText().toString()) * 0.1;
        total += Integer.parseInt(text0_05.getText().toString()) * 0.05;


        if (total > 0) {
            highReject.setChecked(false);
            noCashFound.setChecked(false);
        }

        textTotal.setText("$" + formatter.format(total));

    }

    private class GenericTextWatcher implements TextWatcher {

        private View view;

        private GenericTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            try {
                calculate();
            } catch (Exception e) {
                Log.e("ERROR", e.toString());
            }
        }
    }
}
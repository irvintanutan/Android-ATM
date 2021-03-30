package com.novigosolutions.certiscisco.models;

import android.widget.EditText;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.novigosolutions.certiscisco.R;

import java.util.List;

import butterknife.BindView;

/**
 * Created by dhanrajk on 23-06-17.
 */
@Table(name = "Denomination")
public class Denomination extends Model {

    @Column(name = "ATMOrderId")
    public int ATMOrderId;

    @Column(name = "OperationMode")
    public String OperationMode;

    @Column(name = "text1000")
    public String text1000;

    @Column(name = "text100")
    public String text100;

    @Column(name = "text50")
    public String text50;

    @Column(name = "text10")
    public String text10;

    @Column(name = "text5")
    public String text5;

    @Column(name = "text2")
    public String text2;

    @Column(name = "text1")
    public String text1;

    @Column(name = "text0_50")
    public String text0_50;

    @Column(name = "text0_20")
    public String text0_20;

    @Column(name = "text0_10")
    public String text0_10;

    @Column(name = "text0_05")
    public String text0_05;

    @Column(name = "textTotal")
    public String textTotal;

    @Column(name = "HighReject")
    public boolean HighReject;

    @Column(name = "NoCashFound")
    public boolean NoCashFound;

    public static Denomination getSingle(int ATMOrderId) {
        return new Select().from(Job.class)
                .orderBy("ATMOrderId ASC")
                .where("ATMOrderId=?", ATMOrderId)
                .executeSingle();
    }

    public static List<Denomination> get(int ATMOrderId) {
        return new Select()
                .from(Denomination.class)
                .where("ATMOrderId=?", ATMOrderId)
                .execute();
    }

    public static List<Denomination> get(int ATMOrderId, String operationMode) {
        return new Select()
                .from(Denomination.class)
                .where("ATMOrderId=? AND OperationMode=?", ATMOrderId, operationMode)
                .execute();
    }

    public static void clear(int ATMOrderId) {
        new Delete().from(Denomination.class)
                .where("ATMOrderId=?", ATMOrderId)
                .execute();
    }
}

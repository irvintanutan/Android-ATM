package com.novigosolutions.certiscisco.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.List;

/**
 * Created by dhanrajk on 23-06-17.
 */
@Table(name = "CoinEnvelopes")
public class CoinEnvelopes extends Model {

    @Column(name = "ATMOrderId")
    public int ATMOrderId;

    @Column(name = "CoinEnvelope")
    public String CoinEnvelope;

    @Column(name = "isScanned")
    public int isScanned;

    @Column(name = "isEditRequested")
    public int isEditRequested;

    @Column(name = "status")
    public String status;

    public static List<CoinEnvelopes> get(int ATMOrderId) {
        return new Select().from(CoinEnvelopes.class).where("ATMOrderId=?", ATMOrderId).execute();
    }

    public static void updateeditstatus(int ATMOrderId, String CoinEnvelope) {
        new Update(CoinEnvelopes.class)
                .set("isEditRequested=?", 1)
                .where("ATMOrderId=? AND CoinEnvelope=?", ATMOrderId, CoinEnvelope)
                .execute();
    }

    public static boolean updateScan(int ATMOrderId, String CoinEnvelope) {
        CoinEnvelopes envelopes = new Select().from(CoinEnvelopes.class)
                .where("ATMOrderId=? AND CoinEnvelope=? AND isScanned=?", ATMOrderId, CoinEnvelope, 0)
                .executeSingle();
        if (envelopes != null) {
            new Update(CoinEnvelopes.class)
                    .set("isScanned=?", 1)
                    .where("id=?", envelopes.getId())
                    .execute();
            return true;
        } else {
            return false;
        }
    }
    public static String getScannedCout(int ATMOrderId) {
        return String.valueOf(new Select().from(CoinEnvelopes.class).where("ATMOrderId=? AND isScanned=?", ATMOrderId,1).execute().size());
    }
    public static Boolean isAllCoinScanned(int ATMOrderId) {
        CoinEnvelopes coinEnvelopes = new Select().from(CoinEnvelopes.class)
                .where("ATMOrderId=? AND isScanned=?", ATMOrderId, 0)
                .executeSingle();
        return coinEnvelopes == null;
    }

    public static void cancelScan(int ATMOrderId) {
        new Update(CoinEnvelopes.class)
                .set("isScanned=?", 0)
                .where("ATMOrderId=?", ATMOrderId)
                .execute();
    }
    public static List<CoinEnvelopes> getCount(int ATMOrderId) {
        return new Select()
                .from(CoinEnvelopes.class)
                .where("ATMOrderId=?",ATMOrderId)
                .execute();
    }
    public static String getEnvList(int ATMOrderId) {
        String str = "";
        List<CoinEnvelopes> coinEnvelopes = new Select()
                .from(CoinEnvelopes.class)
                .where("ATMOrderId=?", ATMOrderId)
                .execute();
        for (int i = 0; i < coinEnvelopes.size(); i++) {
            if (str.equals("")) {
                str += "(" + coinEnvelopes.get(i).CoinEnvelope;
            } else {
                str += "," + coinEnvelopes.get(i).CoinEnvelope;
            }
        }
        str += ")";
        return str;
    }
    public static void remove() {
        new Delete().from(CoinEnvelopes.class)
                .execute();
    }


}

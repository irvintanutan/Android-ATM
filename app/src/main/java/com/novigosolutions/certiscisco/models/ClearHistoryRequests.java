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
@Table(name = "ClearHistoryRequests")
public class ClearHistoryRequests extends Model {

    @Column(name = "ATMOrderId")
    public int ATMOrderId;

    @Column(name = "requestId")
    public long requestId;

    @Column(name = "status")
    public String status; //Yes,No,Pending



    public static String getStatus(int ATMOrderId) {
        String status;
        ClearHistoryRequests editRequests = new Select().from(ClearHistoryRequests.class)
                .where("ATMOrderId=?", ATMOrderId).executeSingle();
        if (editRequests == null)
            status = "NOREQUEST";
        else status = editRequests.status;
        return status;
    }
    public static ClearHistoryRequests getSingle(int ATMOrderId) {
        return new Select().from(ClearHistoryRequests.class)
                .where("ATMOrderId=?", ATMOrderId).executeSingle();
    }

    public static void updateSingle(long reqid, String status) {
        new Update(ClearHistoryRequests.class)
                .set("status=?", status)
                .where("requestId=?", reqid)
                .execute();
    }

    public static void removeSingle(long reqid) {
        ClearHistoryRequests editRequests = new Select().from(ClearHistoryRequests.class)
                .where("requestId=?", reqid).executeSingle();
        editRequests.delete();

    }

    public static String[] getRequetedIDs(int ATMOrderId) {
        List<ClearHistoryRequests> editRequests = new Select().from(ClearHistoryRequests.class)
                .where("ATMOrderId=?", ATMOrderId).execute();
        String ids[] = new String[editRequests.size()];
        for (int i = 0; i < editRequests.size(); i++) {
            ids[i] = String.valueOf(editRequests.get(i).requestId);
        }
        return ids;
    }

    public static void remove() {
        new Delete().from(ClearHistoryRequests.class)
                .execute();
    }
}

package com.novigosolutions.certiscisco.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.novigosolutions.certiscisco.utils.Constatnts;

import java.util.List;

/**
 * Created by dhanrajk on 23-06-17.
 */
@Table(name = "EditRequests")
public class EditRequests extends Model {

    @Column(name = "ATMOrderId")
    public int ATMOrderId;

    @Column(name = "requestId")
    public long requestId;

    @Column(name = "fragmenttype")
    public String fragmenttype; //LOAD,UNLOAD


    @Column(name = "CartId")
    public int CartId;

    @Column(name = "status")
    public String status; //Yes,No,Pending

    public static List<EditRequests> get(int ATMOrderId) {
        return new Select().from(EditRequests.class)
                .where("ATMOrderId=?", ATMOrderId).execute();
    }
    public static EditRequests getSingle(int ATMOrderId, String fragmenttype) {
        return new Select().from(EditRequests.class)
                .where("ATMOrderId=? AND fragmenttype=? AND CartId=?", ATMOrderId, fragmenttype).executeSingle();
    }

    public static EditRequests getSingle(int ATMOrderId, String fragmenttype, int CartId) {
        return new Select().from(EditRequests.class)
                .where("ATMOrderId=? AND fragmenttype=? AND CartId=?", ATMOrderId, fragmenttype, CartId).executeSingle();
    }

    public static String getStatus(int ATMOrderId, String fragmenttype, int CartId) {
        String status;
        EditRequests editRequests = new Select().from(EditRequests.class)
                .where("ATMOrderId=? AND fragmenttype=? AND CartId=?", ATMOrderId, fragmenttype, CartId).executeSingle();
        if (editRequests == null)
            status = "NOREQUEST";
        else status = editRequests.status;
        return status;
    }
    public static String getStatus(int ATMOrderId, String fragmenttype) {
        String status;
        EditRequests editRequests = new Select().from(EditRequests.class)
                .where("ATMOrderId=? AND fragmenttype=?", ATMOrderId, fragmenttype).executeSingle();
        if (editRequests == null)
            status = "NOREQUEST";
        else status = editRequests.status;
        return status;
    }
    public static String[] getRequetedIDs(int ATMOrderId, String fragmenttype) {
        List<EditRequests> editRequests = new Select().from(EditRequests.class)
                .where("ATMOrderId=? AND fragmenttype=?", ATMOrderId, fragmenttype).execute();
        String ids[] = new String[editRequests.size()];
        for (int i = 0; i < editRequests.size(); i++) {
            ids[i] = String.valueOf(editRequests.get(i).requestId);
        }
        return ids;
    }

    public static void updateSingle(long reqid, String status) {
        new Update(EditRequests.class)
                .set("status=?", status)
                .where("requestId=?", reqid)
                .execute();
    }

    public static void removeSingle(long reqid) {
        EditRequests editRequests = new Select().from(EditRequests.class)
                .where("requestId=?", reqid).executeSingle();
        if(editRequests.CartId== Constatnts.coinenvelopeid)
        {
            new Update(CoinEnvelopes.class)
                    .set("isEditRequested=?", 0)
                    .where("ATMOrderId=?", editRequests.ATMOrderId)
                    .execute();
        }
        else
        {
            new Update(Cartridge.class)
                    .set("isEditRequested=?", 0)
                    .where("ATMOrderId=? AND CartType=? AND CartId=?", editRequests.ATMOrderId, editRequests.fragmenttype, editRequests.CartId)
                    .execute();
        }
        editRequests.delete();

    }

    public static void remove() {
        new Delete().from(EditRequests.class)
                .execute();
    }
}

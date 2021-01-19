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
@Table(name = "seal")
public class Seal extends Model {

//    @Column(name = "id")
//    public int id;

    @Column(name = "CartId")
    public int CartId;

    @Column(name = "ATMOrderId")
    public int ATMOrderId;

    @Column(name = "CartType")
    public String CartType;

    @Column(name = "SealNo")
    public String SealNo;

    @Column(name = "isScanned")
    public int isScanned;

    @Column(name = "isEditRequested")
    public int isEditRequested;

    public static List<Seal> get(int ATMOrderId, int CartId, String CartType) {
        return new Select().from(Seal.class).where("ATMOrderId=? AND CartId=? AND CartType=?", ATMOrderId, CartId, CartType).execute();
    }

    public static List<Seal> get(int ATMOrderId) {
        return new Select().from(Seal.class).where("ATMOrderId=?", ATMOrderId).execute();
    }

    public static Seal getSingle(int ATMOrderId, int CartId, String CartType, String SealNo) {
        return new Select().from(Seal.class).where("ATMOrderId=? AND CartId=? AND CartType=? AND SealNo=?", ATMOrderId, CartId, CartType, SealNo).executeSingle();
    }

    public static Boolean canEdit(int ATMOrderId, int CartId, String CartType, int num) {
        List<Seal> seals = get(ATMOrderId, CartId, CartType);
        try {
            return (seals.get(num).isEditRequested == 1);
        } catch (Exception e) {
            return false;
        }
    }
    public static Boolean isScanned(int ATMOrderId, int CartId, String CartType, int num) {
        List<Seal> seals = get(ATMOrderId, CartId, CartType);
        try {
            return (seals.get(num).isScanned == 1);
        } catch (Exception e) {
            return false;
        }
    }
    public static Boolean isSealExistAndUpdate(int ATMOrderId, int CartId, String CartType, String SealNo, Boolean isHistoryCleared, int pos) {
        Seal seal = null;
        if (isHistoryCleared) {
            if (pos == -5) {
                seal = new Select()
                        .from(Seal.class)
                        .where("ATMOrderId=? AND CartId=? AND CartType=? AND isScanned=?", ATMOrderId, CartId, CartType, 0)
                        .executeSingle();
            } else {
                List<Seal> seals = new Select()
                        .from(Seal.class)
                        .where("ATMOrderId=? AND CartId=? AND CartType=?", ATMOrderId, CartId, CartType)
                        .execute();
                seal = seals.get(pos);
            }

        } else {
            seal = new Select()
                    .from(Seal.class)
                    .where("ATMOrderId=? AND CartId=? AND CartType=? AND SealNo=? AND isScanned=?", ATMOrderId, CartId, CartType, SealNo, 0)
                    .executeSingle();
        }
        if (seal != null) {
            if (isHistoryCleared) {
                new Update(Seal.class)
                        .set("isScanned=?,SealNo=?", 1, SealNo)
                        .where("id=?", seal.getId())
                        .execute();

                return true;
            } else {
                new Update(Seal.class)
                        .set("isScanned=?", 1)
                        .where("id=?", seal.getId())
                        .execute();

                return true;
            }
        } else
            return false;
    }

    public static void updateeditstatus(int ATMOrderId, String CartType, int CartId, int num) {
        List<Seal> seals = get(ATMOrderId, CartId, CartType);
        try {
            new Update(Seal.class)
                    .set("isEditRequested=?", 1)
                    .where("id=?", seals.get(num).getId())
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Boolean isSealScanComplete(int ATMOrderId, int CartId, String CartType) {
        Seal seal = new Select()
                .from(Seal.class)
                .where("ATMOrderId=? AND CartId=? AND CartType=? AND isScanned=?", ATMOrderId, CartId, CartType, 0)
                .executeSingle();
        if (seal == null) {
            new Update(Cartridge.class)
                    .set("isScanCompleted=?", 1)
                    .where("ATMOrderId=? AND CartId=? AND CartType=?", ATMOrderId, CartId, CartType)
                    .execute();
            return true;
        } else {
            return (false);
        }
    }

    public static void remove() {
        new Delete().from(Seal.class)
                .execute();
    }
}

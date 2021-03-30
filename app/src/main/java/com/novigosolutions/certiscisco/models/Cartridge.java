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
@Table(name = "cartridge")
public class Cartridge extends Model {

    @Column(name = "CartId")
    public int CartId;

    @Column(name = "ATMOrderId")
    public int ATMOrderId;

    @Column(name = "CartType")
    public String CartType;

    @Column(name = "SerialNo")
    public String SerialNo;

    @Column(name = "Deno")
    public String Deno;

    @Column(name = "DuffleSeal")
    public String DuffleSeal;

    @Column(name = "CartNo")
    public String CartNo;

    @Column(name = "isScanCompleted")
    public int isScanCompleted;

    @Column(name = "isScanned")
    public int isScanned;

    @Column(name = "isEditRequested")
    public int isEditRequested;

    //    public static List<Cartridge> getAll(int ATMOrderId) {
//        return new Select().from(Cartridge.class).where("ATMOrderId=?", ATMOrderId).execute();
//    }
    public static Cartridge getByCartID(int CartId) {
        return new Select().from(Cartridge.class).where("CartId=?", CartId).executeSingle();
    }

    public static List<Cartridge> get(int ATMOrderId, String CartType) {
        return new Select().from(Cartridge.class).where("ATMOrderId=? AND CartType=?", ATMOrderId, CartType).execute();
    }
    public static List<Cartridge> getUnScanned(int ATMOrderId, String CartType) {
        return new Select().from(Cartridge.class).where("ATMOrderId=? AND CartType=? AND isScanCompleted=?", ATMOrderId, CartType, 0).execute();
    }
    public static Cartridge getSingle(int ATMOrderId, String CartType, int CartId) {
        return new Select().from(Cartridge.class).where("ATMOrderId=? AND CartType=? AND CartId=?", ATMOrderId, CartType, CartId).executeSingle();
    }

    public static List<Cartridge> get(int ATMOrderId) {
        return new Select().from(Cartridge.class).where("ATMOrderId=?", ATMOrderId).execute();
    }

    public static Boolean isAllCartScanned(int ATMOrderId, String CartType) {
        Cartridge cartridge = new Select().from(Cartridge.class)
                .where("ATMOrderId=? AND CartType=? AND isScanCompleted=?", ATMOrderId, CartType, 0)
                .executeSingle();

        return cartridge == null;
    }


//    public static int updateScanAndGetCartridgeId(int ATMOrderId, String SerialNo, String CartType, Boolean isHistoryCleared) {
////        Cartridge cartridge = new Select().from(Cartridge.class)
////                .where("ATMOrderId=? AND CartType=? AND SerialNo=? AND isScanned=?", ATMOrderId, CartType, SerialNo, 0)
////                .executeSingle();
//        Cartridge cartridge = new Select().from(Cartridge.class)
//                .where("ATMOrderId=? AND CartType=? AND isScanned=?", ATMOrderId, CartType, 0)
//                .executeSingle();
//        if (cartridge != null) {
//            if (isHistoryCleared) {
//                new Update(Cartridge.class)
//                        .set("isScanned=?,SerialNo=?", 1, SerialNo)
//                        .where("id=?", cartridge.getId())
//                        .execute();
//                return cartridge.CartId;
//            } else {
//                if (cartridge.SerialNo.equals(SerialNo)) {
//                    new Update(Cartridge.class)
//                            .set("isScanned=?", 1)
//                            .where("id=?", cartridge.getId())
//                            .execute();
//                    return cartridge.CartId;
//                } else {
//                    Cartridge subcartridge = new Select().from(Cartridge.class)
//                            .where("ATMOrderId=? AND CartType=? AND isScanned=? AND SerialNo=?", ATMOrderId, CartType, 0, SerialNo)
//                            .executeSingle();
//                    if (subcartridge == null)
//                        return 0;
//                    else return -1;
//                }
//            }
//        } else
//            return 0;
//    }

    public static int updateScanAndGetCartridgeId(int ATMOrderId, String SerialNo, String CartType, Boolean isHistoryCleared) {
        if (isHistoryCleared) {
            Cartridge cartridge = new Select().from(Cartridge.class)
                    .where("ATMOrderId=? AND CartType=? AND isScanned=?", ATMOrderId, CartType, 0)
                    .executeSingle();
            if (cartridge != null) {
                new Update(Cartridge.class)
                        .set("isScanned=?,SerialNo=?", 1, SerialNo)
                        .where("id=?", cartridge.getId())
                        .execute();
                return cartridge.CartId;
            } else {
                return -1;
            }
        } else {
            Cartridge cartridge = new Select().from(Cartridge.class)
                    .where("ATMOrderId=? AND CartType=? AND SerialNo=? AND isScanned=?", ATMOrderId, CartType, SerialNo, 0)
                    .executeSingle();
            if (cartridge != null) {
                new Update(Cartridge.class)
                        .set("isScanned=?", 1)
                        .where("id=?", cartridge.getId())
                        .execute();
                return cartridge.CartId;
            } else {
                return -1;
            }

        }
    }
    public static Boolean updateScan(int ATMOrderId, String CartType,int CartId,String SerialNo ,Boolean isHistoryCleared) {

        Cartridge cartridge = new Select().from(Cartridge.class)
                .where("ATMOrderId=? AND CartType=? AND isScanned=?  AND CartId=?", ATMOrderId, CartType, 0,CartId)
                .executeSingle();
        if (cartridge != null) {
            if (isHistoryCleared) {
                new Update(Cartridge.class)
                        .set("isScanned=?,SerialNo=?", 1,SerialNo)
                        .where("id=?", cartridge.getId())
                        .execute();
            } else {
                new Update(Cartridge.class)
                        .set("isScanned=?", 1)
                        .where("id=?", cartridge.getId())
                        .execute();
            }
            return true;
        }
        else
        {
            return false;
        }
    }
    public static int updateManualentrydata(int ATMOrderId, int cartId, String SerialNo, String CartType, Boolean isHistoryCleared) {
//        Cartridge cartridge = new Select().from(Cartridge.class)
//                .where("ATMOrderId=? AND CartType=? AND SerialNo=? AND isScanned=?", ATMOrderId, CartType, SerialNo, 0)
//                .executeSingle();
        Cartridge cartridge = new Select().from(Cartridge.class)
                .where("ATMOrderId=? AND CartType=? AND isScanned=?", ATMOrderId, CartType, 0)
                .executeSingle();
        if (cartridge != null) {
            if (cartId != cartridge.CartId) {
                return -1;
            }
            if (isHistoryCleared) {
                new Update(Cartridge.class)
                        .set("isScanned=?,SerialNo=?", 1, SerialNo)
                        .where("id=?", cartridge.getId())
                        .execute();
                return cartridge.CartId;
            } else {
                if (cartridge.SerialNo.equals(SerialNo)) {
                    new Update(Cartridge.class)
                            .set("isScanned=?", 1)
                            .where("id=?", cartridge.getId())
                            .execute();
                    return cartridge.CartId;
                } else {
                    Cartridge subcartridge = new Select().from(Cartridge.class)
                            .where("ATMOrderId=? AND CartType=? AND isScanned=? AND SerialNo=?", ATMOrderId, CartType, 0, SerialNo)
                            .executeSingle();
                    if (subcartridge == null)
                        return 0;
                    else return -1;
                }
            }
        } else
            return 0;
    }

    public static void updateeditstatus(int ATMOrderId, String CartType, int CartId) {
        new Update(Cartridge.class)
                .set("isEditRequested=?", 1)
                .where("ATMOrderId=? AND CartType=? AND CartId=?", ATMOrderId, CartType, CartId)
                .execute();
    }

    public static void cancelScan(int ATMOrderId, String CartType) {
        new Update(Cartridge.class)
                .set("isScanned=?,isScanCompleted=?", 0, 0)
                .where("ATMOrderId=? AND CartType=?", ATMOrderId, CartType)
                .execute();
        new Update(Seal.class)
                .set("isScanned=?", 0)
                .where("ATMOrderId =? AND CartType=?", ATMOrderId, CartType)
                .execute();
    }

    public static void cancelAllScan(int ATMOrderId) {
        new Update(Cartridge.class)
                .set("isScanned=?,isScanCompleted=?", 0, 0)
                .where("ATMOrderId=?", ATMOrderId)
                .execute();
        new Update(Seal.class)
                .set("isScanned=?", 0)
                .where("ATMOrderId=?", ATMOrderId)
                .execute();
        new Update(CoinEnvelopes.class)
                .set("isScanned=?", 0)
                .where("ATMOrderId=?", ATMOrderId)
                .execute();

        new Delete().from(OtherScan.class)
                .where("ATMOrderId=?", ATMOrderId)
                .execute();

        new Delete().from(FLMSLMScan.class)
                .where("ATMOrderId=?", ATMOrderId)
                .execute();

        new Delete().from(TestCash.class)
                .where("ATMOrderId=?", ATMOrderId)
                .execute();


    }

    public static void remove() {
        new Delete().from(Cartridge.class)
                .execute();
    }


}

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
@Table(name = "job")
public class Job extends Model {

    @Column(name = "ATMOrderId")
    public int ATMOrderId;

    @Column(name = "ATMMasterId")
    public int ATMMasterId;

    @Column(name = "OrderType")
    public String OrderType;

    @Column(name = "OrderMode")
    public String OrderMode;

    @Column(name = "ATMCode")
    public String ATMCode;

    @Column(name = "ATMTypeCode")
    public String ATMTypeCode;

    @Column(name = "Bank")
    public String Bank;

    @Column(name = "ATMType")
    public String ATMType;

    @Column(name = "Version")
    public String Version;

    @Column(name = "DeploymentNo")
    public String DeploymentNo;

    @Column(name = "OperationMode")
    public String OperationMode;

    @Column(name = "Status")
    public String Status;

    @Column(name = "Location")
    public String Location;

    @Column(name = "Zone")
    public String Zone;

    @Column(name = "StartDate")
    public String StartDate;

    @Column(name = "EndDate")
    public String EndDate;

    @Column(name = "Duration")
    public int Duration;

    @Column(name = "isOfflineSaved")
    public int isOfflineSaved;

    @Column(name = "isHistoryCleared")
    public int isHistoryCleared;

    public static Job getSingle(int ATMOrderId) {
        return new Select().from(Job.class)
                .orderBy("ATMOrderId ASC")
                .where("ATMOrderId=?", ATMOrderId)
                .executeSingle();
    }

    public static List<Job> getAll() {
        return new Select().from(Job.class)
                .orderBy("ATMOrderId ASC")
                .execute();
    }

    public static List<Job> getbyOrderMode(String OrderMode) {
        return new Select().from(Job.class)
                .where("OrderMode =?", OrderMode)
                .orderBy("ATMOrderId ASC")
                .execute();
    }

    public static List<Job> getByStatus(String OrderMode, String Status) {
        return new Select().from(Job.class)
                .orderBy("ATMOrderId ASC")
                //.where("OrderMode =? AND Status=?", OrderMode, Status)
                .execute();
    }

    public static String getOperationMode(int ATMOrderId) {
        Job job = new Select().from(Job.class)
                .where("ATMOrderId=?", ATMOrderId)
                .executeSingle();
        return job.OperationMode;
    }

    public static String getATMCode(int ATMOrderId) {
        Job job = new Select().from(Job.class)
                .where("ATMOrderId=?", ATMOrderId)
                .executeSingle();
        return job.ATMCode;
    }

    public static Boolean isJobExist(int ATMOrderId) {
        Job job = new Select().from(Job.class)
                .where("ATMOrderId=?", ATMOrderId)
                .executeSingle();
        return job != null;
    }

    public static void updateStartDate(int ATMOrderId, String date) {
        new Update(Job.class)
                .set("StartDate=?", date)
                .where("ATMOrderId=?", ATMOrderId)
                .execute();
    }

    public static void updateStatus(int ATMOrderId) {
        new Update(Job.class)
                .set("Status=?", "DELIVERED")
                .where("ATMOrderId=?", ATMOrderId)
                .execute();
    }

    public static void saveasOffline(int ATMOrderId) {
        new Update(Job.class)
                .set("isOfflineSaved=?", 1)
                .where("ATMOrderId=?", ATMOrderId)
                .execute();
    }

    public static void setOffLineUpdated(int ATMOrderId) {
        new Update(Job.class)
                .set("isOfflineSaved=?", 0)
                .where("ATMOrderId=?", ATMOrderId)
                .execute();
    }

    public static List<Job> getOfflinelist() {
        return new Select().from(Job.class)
                .where("isOfflineSaved =?", 1)
                .orderBy("ATMOrderId ASC")
                .execute();
    }

    public static void updateEndDate(int ATMOrderId, String date, int duration) {
        new Update(Job.class)
                .set("EndDate=?,Duration=?", date, duration)
                .where("ATMOrderId=?", ATMOrderId)
                .execute();
    }

    public static Boolean isOfflineExist() {
        Job job = new Select().from(Job.class)
                .where("isOfflineSaved=?", 1)
                .executeSingle();

        return job != null;
    }

    public static Boolean isHistoryCleared(int ATMOrderId) {
        Job job = new Select().from(Job.class)
                .where("ATMOrderId=? AND isHistoryCleared=?", ATMOrderId, 1)
                .executeSingle();
        if(job==null)
        {
            return  false;
        }
        else
        {
            if(job.OperationMode.equals("LOAD"))
            {
                return false;
            }
            else
            {
                return true;
            }
        }
    }


    public static void clearHistory(int ATMOrderId, String CartType) {
        new Update(Job.class)
                .set("isHistoryCleared=?", 1)
                .where("ATMOrderId=?", ATMOrderId)
                .execute();
        new Update(Cartridge.class)
                .set("SerialNo=?", "")
                .where("ATMOrderId=? AND CartType=?", ATMOrderId, CartType)
                .execute();
        new Update(Seal.class)
                .set("SealNo=?", "")
                .where("ATMOrderId =? AND CartType=?", ATMOrderId, CartType)
                .execute();
    }

    public static Boolean isExist(String ScanValue) {
        OtherScan otherScan = new Select().from(OtherScan.class)
                .where("ScanValue=?", ScanValue)
                .executeSingle();
        TestCash testCash = new Select().from(TestCash.class)
                .where("ScanValue=?", ScanValue)
                .executeSingle();
        Cartridge cartridge = new Select().from(Cartridge.class)
                .where("SerialNo=?", ScanValue)
                .executeSingle();
        Seal seal = new Select().from(Seal.class)
                .where("SealNo=?", ScanValue)
                .executeSingle();
        CoinEnvelopes envelopes = new Select().from(CoinEnvelopes.class)
                .where("CoinEnvelope=?", ScanValue)
                .executeSingle();
        return (otherScan != null) || (testCash != null) || (cartridge != null) || (seal != null) || (envelopes != null);
    }

    //Testing purpose
    public static String isExistwithmessage(String ScanValue) {
        Cartridge cartridge = new Select().from(Cartridge.class)
                .where("SerialNo=?", ScanValue)
                .executeSingle();
        if(cartridge!=null) return ScanValue+" exists in ATM "+Job.getATMCode(cartridge.ATMOrderId)+" as cart serial no with cart id "+cartridge.CartId;

        Seal seal = new Select().from(Seal.class)
                .where("SealNo=?", ScanValue)
                .executeSingle();
        if(seal!=null) return ScanValue+" exists in ATM "+Job.getATMCode(seal.ATMOrderId)+" as cart seal no with cart id "+seal.CartId;

        OtherScan otherScan = new Select().from(OtherScan.class)
                .where("ScanValue=?", ScanValue)
                .executeSingle();
        if(otherScan!=null) return ScanValue+" exists in ATM "+Job.getATMCode(otherScan.ATMOrderId)+" as "+otherScan.ScanTypeName;

        TestCash testCash = new Select().from(TestCash.class)
                .where("ScanValue=?", ScanValue)
                .executeSingle();
        if(testCash!=null) return ScanValue+" exists in ATM "+Job.getATMCode(testCash.ATMOrderId)+" as "+testCash.ScanTypeName;

        CoinEnvelopes envelopes = new Select().from(CoinEnvelopes.class)
                .where("CoinEnvelope=?", ScanValue)
                .executeSingle();
        if(envelopes!=null) return ScanValue+" exists in ATM "+Job.getATMCode(envelopes.ATMOrderId)+" as coin envelope";
        return "";
    }
    public static void removeSingle(int ATMOrderId) {
        Job job = getSingle(ATMOrderId);
        if (job != null) job.delete();

        List<Cartridge> cartridgeList = Cartridge.get(ATMOrderId);
        for (int i = 0; i < cartridgeList.size(); i++) {
            cartridgeList.get(i).delete();
        }

        List<Seal> sealList = Seal.get(ATMOrderId);
        for (int i = 0; i < sealList.size(); i++) {
            sealList.get(i).delete();
        }

        List<OtherScan> otherscanList = OtherScan.get(ATMOrderId);
        for (int i = 0; i < otherscanList.size(); i++) {
            otherscanList.get(i).delete();
        }

        List<TestCash> testscanList = TestCash.get(ATMOrderId);
        for (int i = 0; i < testscanList.size(); i++) {
            testscanList.get(i).delete();
        }

        List<EditRequests> editRequestses = EditRequests.get(ATMOrderId);
        for (int i = 0; i < editRequestses.size(); i++) {
            editRequestses.get(i).delete();
        }

        List<CoinEnvelopes> coinEnvelopes = CoinEnvelopes.get(ATMOrderId);
        for (int i = 0; i < coinEnvelopes.size(); i++) {
            coinEnvelopes.get(i).delete();
        }

//        new Delete().from(Job.class)
//                .where("ATMOrderId=?", ATMOrderId)
//                .execute();
//
//        new Delete().from(Cartridge.class)
//                .where("ATMOrderId=?", ATMOrderId)
//                .execute();
//        new Delete().from(Seal.class)
//                .where("ATMOrderId=?", ATMOrderId)
//                .execute();
//        new Delete().from(OtherScan.class)
//                .where("ATMOrderId=?", ATMOrderId)
//                .execute();
//        new Delete().from(TestCash.class)
//                .where("ATMOrderId=?", ATMOrderId)
//                .execute();
    }

    public static void remove() {
        new Delete().from(Job.class)
                .execute();
    }


}

package com.novigosolutions.certiscisco.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

/**
 * Created by dhanrajk on 23-06-17.
 */
@Table(name = "FLMSLMAdditionalDetails")
public class FLMSLMAdditionalDetails extends Model {

    @Column(name = "ATMOrderId")
    public int ATMOrderId;

    @Column(name = "OperationMode")
    public String OperationMode;

    @Column(name = "FaultType")
    public String FaultType;

    @Column(name = "FaultFound")
    public String FaultFound;

    @Column(name = "Resolution")
    public String Resolution;

    @Column(name = "StaffName")
    public String StaffName;

    @Column(name = "TeamArrivalTime")
    public String TeamArrivalTime;

    @Column(name = "EngineerArrivalTime")
    public String EngineerArrivalTime;

    @Column(name = "AdditionalRemarks")
    public String AdditionalRemarks;

    @Column(name = "SLMRequired")
    public String SLMRequired;

    public static FLMSLMAdditionalDetails getSingle(int ATMOrderId) {
        return new Select().from(FLMSLMAdditionalDetails.class)
                .orderBy("ATMOrderId ASC")
                .where("ATMOrderId=?", ATMOrderId)
                .executeSingle();
    }

    public static List<FLMSLMAdditionalDetails> get(int ATMOrderId) {
        return new Select()
                .from(FLMSLMAdditionalDetails.class)
                .where("ATMOrderId=?", ATMOrderId)
                .execute();
    }

    public static List<FLMSLMAdditionalDetails> get(int ATMOrderId, String operationMode) {
        return new Select()
                .from(FLMSLMAdditionalDetails.class)
                .where("ATMOrderId=? AND OperationMode=?", ATMOrderId, operationMode)
                .execute();
    }

    public static void clear(int ATMOrderId) {
        new Delete().from(FLMSLMAdditionalDetails.class)
                .where("ATMOrderId=?", ATMOrderId)
                .execute();
    }
}

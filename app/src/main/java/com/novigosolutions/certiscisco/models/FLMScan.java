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
@Table(name = "otherscan")
public class FLMScan extends Model {

    @Column(name = "ATMOrderId")
    public int ATMOrderId;

    @Column(name = "ScanType")
    public String ScanType;

    @Column(name = "ScanTypeName")
    public String ScanTypeName;

    @Column(name = "ScanValue")
    public String ScanValue;

    public static List<FLMScan> get(int ATMOrderId) {
        return new Select()
                .from(FLMScan.class)
                .where("ATMOrderId=?", ATMOrderId)
                .execute();
    }

    public static List<FLMScan> getCount(int ATMOrderId, String ScanType) {
        return new Select()
                .from(FLMScan.class)
                .where("ATMOrderId=? AND ScanType=?",ATMOrderId, ScanType)
                .execute();
    }
    public static String getByType(int ATMOrderId,String ScanType) {
        String str="";
        List<FLMScan> otherScen= new Select()
                .from(FLMScan.class)
                .where("ATMOrderId=? AND ScanType=?",ATMOrderId, ScanType)
                .execute();
        for (int i=0;i<otherScen.size();i++)
        {
            if(str.equals(""))
            {
                str+="("+otherScen.get(i).ScanValue;
            }
            else
            {
                str+=","+otherScen.get(i).ScanValue;
            }
        }
        str+=")";
        return str;
    }
    public static void cancelSingleScan(Long id) {
        new Delete().from(FLMScan.class)
                .where("id=?", id)
                .execute();
    }
    public static void cancelScan(int ATMOrderId) {
        new Delete().from(FLMScan.class)
                .where("ATMOrderId=?", ATMOrderId)
                .execute();
    }
    public static void remove() {
        new Delete().from(FLMScan.class)
                .execute();
    }
}

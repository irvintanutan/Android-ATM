package com.novigosolutions.certiscisco.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;

import java.util.List;

@Table(name = "userlogs")
public class UserLogs extends Model {

    @Column(name = "Entity")
    public String Entity;

    @Column(name = "UserAction")
    public String UserAction;

    @Column(name = "Remarks")
    public String Remarks;

    @Column(name = "DateTime")
    public String DateTime;

    @Column(name = "Status")
    public boolean Status;

    @Column(name = "UserId")
    public String UserId;

    public static List<UserLogs> getUserLogs() {
        return new Select().from(UserLogs.class)
                .where("Status=?", 0)
                .execute();
    }

    public static List<UserLogs> getUserLogsDuplicate(String entity, String userAction, String remarks) {
        return new Select().from(UserLogs.class)
                .where("Status=? AND Entity=? AND UserAction=? AND Remarks=?", 0, entity, userAction, remarks)
                .execute();
    }

    public static void removeUserLogs() {
        new Delete().from(UserLogs.class)
                .where("Status=?", 1)
                .execute();
    }

    public static void updateUserLogs(String dateTime) {
        new Update(UserLogs.class)
                .set("Status=?", 1)
                .where("DateTime=?", dateTime)
                .execute();
    }

    public static void remove() {
        new Delete().from(UserLogs.class)
                .execute();
    }

}

package com.novigosolutions.certiscisco.service;

import android.content.Context;

import com.novigosolutions.certiscisco.models.UserLogs;
import com.novigosolutions.certiscisco.utils.Preferences;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

public class UserLogService {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void save(String entity, String remarks, String userAction, Context context) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String dateTime = sdf.format(timestamp);
        String userId = Integer.toString(Preferences.getInt("UserId", context));
        List<UserLogs> list = UserLogs.getUserLogsDuplicate(entity, userAction, remarks);
        if (list.isEmpty()) {
            UserLogs userLogs = new UserLogs();
            userLogs.Entity = entity;
            userLogs.UserAction = userAction;
            userLogs.Remarks = remarks;
            userLogs.Status = false;
            userLogs.DateTime = dateTime;
            userLogs.UserId = userId;
            userLogs.save();
        }
    }


}

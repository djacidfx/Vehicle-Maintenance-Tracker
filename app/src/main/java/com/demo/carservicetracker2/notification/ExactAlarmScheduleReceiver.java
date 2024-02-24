package com.demo.carservicetracker2.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class ExactAlarmScheduleReceiver extends BroadcastReceiver {
    @Override 
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.app.action.SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED")) {
            AlarmUtil.setAllAlarm(context);
        }
    }
}

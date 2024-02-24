package com.demo.carservicetracker2.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BootReceiver extends BroadcastReceiver {
    @Override 
    public void onReceive(Context context, Intent intent) {
        String action;
        if (intent == null || (action = intent.getAction()) == null || action.isEmpty()) {
            return;
        }
        char c = 65535;
        switch (action.hashCode()) {
            case 505380757:
                if (action.equals("android.intent.action.TIME_SET")) {
                    c = 1;
                    break;
                }
                break;
            case 798292259:
                if (action.equals("android.intent.action.BOOT_COMPLETED")) {
                    c = 0;
                    break;
                }
                break;
            case 1041332296:
                if (action.equals("android.intent.action.DATE_CHANGED")) {
                    c = 3;
                    break;
                }
                break;
            case 1737074039:
                if (action.equals("android.intent.action.MY_PACKAGE_REPLACED")) {
                    c = 2;
                    break;
                }
                break;
        }
        if (c == 0) {
            setAlarm(context);
        } else if (c != 2) {
        } else {
            setAlarm(context);
        }
    }

    private void setAlarm(Context context) {
        AlarmUtil.remind24(context);
        AlarmUtil.remind3hour(context);
        AlarmUtil.setAlarmNotification(context);
    }
}

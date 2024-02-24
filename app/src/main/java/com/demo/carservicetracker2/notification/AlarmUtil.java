package com.demo.carservicetracker2.notification;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.demo.carservicetracker2.model.NotificationModel;
import com.google.android.gms.auth.api.credentials.CredentialsApi;
import com.demo.carservicetracker2.database.AppDatabase;
import com.demo.carservicetracker2.utils.AppConstants;
import java.util.Calendar;
import java.util.List;
public class AlarmUtil {
    public static void setAlarmNotification(Context context) {
        cancelAllAlarm(context);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        List<NotificationModel> todaysNotifications = AppDatabase.getAppDatabase(context).reminderDao().getTodaysNotifications(AppConstants.GetOnlyDayBeforeDateInMillis(System.currentTimeMillis()));
        Log.e("AlarmUtil", "setAlarmNotification: " + AppConstants.simpleDateFormat.format(AppConstants.GetOnlyDayBeforeDateInMillis(System.currentTimeMillis())) + " || " + AppConstants.simpleDateFormat.format(Long.valueOf(AppConstants.GetOnlyDateInMillis(System.currentTimeMillis()))));
        int i = CredentialsApi.CREDENTIAL_PICKER_REQUEST_CODE;
        for (NotificationModel notificationModel : todaysNotifications) {
            setAlarmForTime(alarmManager, i, context, notificationModel.getAlertDateTime(), notificationModel.getNotificationId());
            i++;
        }
    }

    public static void setAlarmForTime(AlarmManager alarmManager, int alarmRequestCode, Context context, long alarmTime, String notificationObjId) {
        PendingIntent broadcast;
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(AppConstants.REQUEST_CODE_ALARM_NAME, AppConstants.REQUEST_CODE_SET_ALARM);
        intent.putExtra(AppConstants.NOTIFICATION_OBJECT_ID, notificationObjId);
        if (Build.VERSION.SDK_INT >= 23) {
            broadcast = PendingIntent.getBroadcast(context, alarmRequestCode, intent, 201326592);
        } else {
            broadcast = PendingIntent.getBroadcast(context, alarmRequestCode, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(alarmTime);
        Log.e("AlarmUtil", "setAlarmNotification: " + AppConstants.SIMPLE_DATE_FORMAT_DATE_TIME.format(calendar.getTimeInMillis()) + " context: " + context.getClass().getSimpleName());
        if (System.currentTimeMillis() <= calendar.getTimeInMillis()) {
            if (Build.VERSION.SDK_INT < 19) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
            } else if (Build.VERSION.SDK_INT < 23) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
            } else if (Build.VERSION.SDK_INT < 31) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
            } else if (alarmManager.canScheduleExactAlarms()) {
                Log.e("AlarmUtil 31", "setAlarmNotification: " + AppConstants.SIMPLE_DATE_FORMAT_DATE_TIME.format(calendar.getTimeInMillis()) + " context: " + context.getClass().getSimpleName());
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
            }
        }
    }

    public static void remind3hour(Context context) {
        PendingIntent broadcast;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(AppConstants.REQUEST_CODE_ALARM_NAME, AppConstants.REQUEST_CODE_REMIND_3);
        if (Build.VERSION.SDK_INT >= 23) {
            broadcast = PendingIntent.getBroadcast(context, AppConstants.REQUEST_CODE_REMIND_3, intent, 201326592);
        } else {
            broadcast = PendingIntent.getBroadcast(context, AppConstants.REQUEST_CODE_REMIND_3, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, 6);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        Log.e("AlarmUtil", "Remind 3: " + AppConstants.SIMPLE_DATE_FORMAT_DATE_TIME.format(calendar.getTimeInMillis()) + " context: " + context.getClass().getSimpleName());
        if (System.currentTimeMillis() <= calendar.getTimeInMillis()) {
            if (Build.VERSION.SDK_INT < 23) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
            } else if (Build.VERSION.SDK_INT < 31) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
            } else if (alarmManager.canScheduleExactAlarms()) {
                Log.e("AlarmUtil 31", "Remind 3: " + AppConstants.SIMPLE_DATE_FORMAT_DATE_TIME.format(calendar.getTimeInMillis()) + " context: " + context.getClass().getSimpleName());
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
            }
        }
    }

    public static void remind24(Context context) {
        PendingIntent broadcast;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(AppConstants.REQUEST_CODE_ALARM_NAME, AppConstants.REQUEST_CODE_REMIND_24);
        if (Build.VERSION.SDK_INT >= 23) {
            broadcast = PendingIntent.getBroadcast(context, AppConstants.REQUEST_CODE_REMIND_24, intent, 201326592);
        } else {
            broadcast = PendingIntent.getBroadcast(context, AppConstants.REQUEST_CODE_REMIND_24, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, 24);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        Log.e("AlarmUtil", "Remind 24: " + AppConstants.SIMPLE_DATE_FORMAT_DATE_TIME.format(calendar.getTimeInMillis()) + " context: " + context.getClass().getSimpleName());
        if (Build.VERSION.SDK_INT < 23) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
        } else if (Build.VERSION.SDK_INT < 31) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
        } else if (alarmManager.canScheduleExactAlarms()) {
            Log.e("AlarmUtil 31", "Remind 24: " + AppConstants.SIMPLE_DATE_FORMAT_DATE_TIME.format(calendar.getTimeInMillis()) + " context: " + context.getClass().getSimpleName());
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
        }
    }

    public static void setAllAlarm(Context context) {
        remind3hour(context);
        remind24(context);
        setAlarmNotification(context);
    }

    public static void cancelAllAlarm(Context context) {
        PendingIntent broadcast;
        if (context == null) {
            return;
        }
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            for (int i = CredentialsApi.CREDENTIAL_PICKER_REQUEST_CODE; i < 2100; i++) {
                Intent intent = new Intent(context, AlarmReceiver.class);
                if (Build.VERSION.SDK_INT >= 23) {
                    broadcast = PendingIntent.getBroadcast(context, i, intent, 201326592);
                } else {
                    broadcast = PendingIntent.getBroadcast(context, i, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
                }
                alarmManager.cancel(broadcast);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

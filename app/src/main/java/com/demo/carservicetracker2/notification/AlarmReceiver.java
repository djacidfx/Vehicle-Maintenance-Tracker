package com.demo.carservicetracker2.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.activities.SplashActivity;
import com.demo.carservicetracker2.database.AppDatabase;
import com.demo.carservicetracker2.database.models.ReminderModel;
import com.demo.carservicetracker2.utils.AppConstants;
import com.demo.carservicetracker2.utils.AppPref;
import java.util.Date;


public class AlarmReceiver extends BroadcastReceiver {
    public static final String NOTIFICATION_CHANNEL_ID = "";
    public static final String NOTIFICATION_CHANNEL_NAME = "My Car Daily Reminder";
    Notification.Builder nBuilder;
    int requestCode = 0;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.requestCode = intent.getIntExtra(AppConstants.REQUEST_CODE_ALARM_NAME, 0);
        Log.e("AlarmUtil request code", "" + this.requestCode);
        if (this.requestCode == 1111 && AppPref.IsShowNotification()) {
            setNotification(context, intent);
        }
        int i = this.requestCode;
        if (i == 1103) {
            AlarmUtil.remind24(context);
        } else if (i == 1124) {
            AlarmUtil.remind3hour(context);
            AlarmUtil.setAlarmNotification(context);
        }
    }

    private void setNotification(Context context, Intent intent) {
        String str;
        PendingIntent activity;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        AppDatabase appDatabase = AppDatabase.getAppDatabase(context);
        ReminderModel GetReminderByDate = appDatabase.reminderDao().GetReminderByDate(intent.getStringExtra(AppConstants.NOTIFICATION_OBJECT_ID));
        String str2 = "";
        if (GetReminderByDate != null) {
            str2 = GetReminderByDate.getRemainderText();
            str = "Tomorrow is the deadline for the service. Don't overlook your vehicle upkeep!";
        } else {
            str = "";
        }
        Intent intent2 = new Intent(context, SplashActivity.class);
        intent2.putExtra(AppConstants.REQUEST_CODE_ALARM_NAME_ID, AppConstants.REQUEST_CODE_ALARM_ID);
        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (Build.VERSION.SDK_INT >= 23) {
            activity = PendingIntent.getActivity(context, 0, intent2, 201326592);
        } else {
            activity = PendingIntent.getActivity(context, 0, intent2,  PendingIntent.FLAG_UPDATE_CURRENT);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel notificationChannel = new NotificationChannel(context.getPackageName(), NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setLightColor(-16776961);
            notificationChannel.setLockscreenVisibility(1);
            notificationManager.createNotificationChannel(notificationChannel);
            this.nBuilder = new Notification.Builder(context, context.getPackageName())
                    .setSmallIcon(R.drawable.notification)
                    .setContentTitle(str2).setContentText(str)
                    .setStyle(new Notification.BigTextStyle().bigText(str))
                    .setAutoCancel(true).setShowWhen(true)
                    .setContentIntent(activity)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon200));
        } else {
            this.nBuilder = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.notification)
                    .setContentTitle(str2)
                    .setContentText(str)
                    .setStyle(new Notification.BigTextStyle().bigText(str))
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setDefaults(2).setAutoCancel(true)
                    .setShowWhen(true)
                    .setContentIntent(activity)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.icon200));
        }
        notificationManager.notify((int) ((new Date().getTime() / 1000) % 2147483647L), this.nBuilder.build());
    }
}

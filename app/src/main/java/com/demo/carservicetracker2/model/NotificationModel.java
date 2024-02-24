package com.demo.carservicetracker2.model;


public class NotificationModel {
    long alertDateTime;
    String notificationId;

    public NotificationModel(long alertDateTime, String notificationId) {
        this.alertDateTime = alertDateTime;
        this.notificationId = notificationId;
    }

    public long getAlertDateTime() {
        return this.alertDateTime;
    }

    public void setAlertDateTime(long alertDateTime) {
        this.alertDateTime = alertDateTime;
    }

    public String getNotificationId() {
        return this.notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }
}

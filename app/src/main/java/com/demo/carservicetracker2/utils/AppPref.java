package com.demo.carservicetracker2.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class AppPref {
    static final String CURRENCY = "CURRENCY";
    static final String CURRENCYNAME = "CURRENCYNAME";
    static final String CUSTOM_TYPE = "CUSTOM_TYPE";
    static final String DISTANCE_UNIT = "DISTANCE_UNIT";
    static final String IS_DBVERSION_ADDED = "IS_DBVERSION_ADDED";
    static final String IS_FUEL_SHOW = "IS_FUEL_SHOW";
    static final String IS_RATEUS = "IS_RATEUS_NEW";
    static final String IS_RATE_US_ACTION = "IS_RATE_US_ACTION";
    static final String IS_SHOW_NOTIFICATION = "IS_SHOW_NOTIFICATION";
    static final String IS_TERMS_ACCEPT = "IS_TERMS_ACCEPT";
    public static String MyPref = "carPref";
    static final String NEVER_SHOW_RATTING_DIALOG = "isNeverShowRatting";
    static final String PRO_VERSION = "PRO_VERSION";
    static final String REPORT_DURATION = "REPORT_DURATION";
    static final String SELECTED_CAR = "SELECTED_CAR";
    static final String VOLUME_UNIT = "VOLUME_UNIT";

    public static boolean IsTermsAccept() {
        return App.getContext().getSharedPreferences(MyPref, 0).getBoolean(IS_TERMS_ACCEPT, false);
    }

    public static void setIsTermsAccept(boolean isFirstLaunch) {
        SharedPreferences.Editor edit = App.getContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(IS_TERMS_ACCEPT, isFirstLaunch);
        edit.commit();
    }

    public static boolean isDbVersionAdded() {
        return App.getContext().getSharedPreferences(MyPref, 0).getBoolean(IS_DBVERSION_ADDED, false);
    }

    public static void setIsDbversionAdded(boolean isaddfree) {
        SharedPreferences.Editor edit = App.getContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(IS_DBVERSION_ADDED, isaddfree);
        edit.commit();
    }

    public static String getDistanceUnit() {
        return App.getContext().getSharedPreferences(MyPref, 0).getString(DISTANCE_UNIT, AppConstants.KILOMETER);
    }

    public static void setDistanceUnit(String distanceUnit) {
        SharedPreferences.Editor edit = App.getContext().getSharedPreferences(MyPref, 0).edit();
        edit.putString(DISTANCE_UNIT, distanceUnit);
        edit.commit();
    }

    public static String getVolumeUnit() {
        return App.getContext().getSharedPreferences(MyPref, 0).getString(VOLUME_UNIT, AppConstants.LITER);
    }

    public static void setVolumeUnit(String volumeUnit) {
        SharedPreferences.Editor edit = App.getContext().getSharedPreferences(MyPref, 0).edit();
        edit.putString(VOLUME_UNIT, volumeUnit);
        edit.commit();
    }

    public static String getCurrency() {
        return App.getContext().getSharedPreferences(MyPref, 0).getString(CURRENCY, "$");
    }

    public static void setCurrency(String currency) {
        SharedPreferences.Editor edit = App.getContext().getSharedPreferences(MyPref, 0).edit();
        edit.putString(CURRENCY, currency);
        edit.commit();
    }

    public static String getCurrencyName() {
        return App.getContext().getSharedPreferences(MyPref, 0).getString(CURRENCYNAME, "US Dollar");
    }

    public static void setCurrencyName(String currency) {
        SharedPreferences.Editor edit = App.getContext().getSharedPreferences(MyPref, 0).edit();
        edit.putString(CURRENCYNAME, currency);
        edit.commit();
    }

    public static boolean IsShowFuel() {
        return App.getContext().getSharedPreferences(MyPref, 0).getBoolean(IS_FUEL_SHOW, true);
    }

    public static void setIsShowFuel(boolean isShowFuel) {
        SharedPreferences.Editor edit = App.getContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(IS_FUEL_SHOW, isShowFuel);
        edit.commit();
    }

    public static void setNeverShowRatting(boolean isNeverShow) {
        SharedPreferences.Editor edit = App.getContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(NEVER_SHOW_RATTING_DIALOG, isNeverShow);
        edit.commit();
    }

    public static boolean isNeverShowRatting() {
        return App.getContext().getSharedPreferences(MyPref, 0).getBoolean(NEVER_SHOW_RATTING_DIALOG, false);
    }

    public static boolean IsRateUsAction(Context context) {
        return context.getApplicationContext().getSharedPreferences(MyPref, 0).getBoolean(IS_RATE_US_ACTION, false);
    }

    public static void setRateUsAction(Context context, boolean rateUSAction) {
        SharedPreferences.Editor edit = context.getApplicationContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(IS_RATE_US_ACTION, rateUSAction);
        edit.commit();
    }

    public static boolean IsRateUs() {
        return App.getContext().getSharedPreferences(MyPref, 0).getBoolean(IS_RATEUS, false);
    }

    public static void setRateUs(boolean IsRateUs) {
        SharedPreferences.Editor edit = App.getContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(IS_RATEUS, IsRateUs);
        edit.commit();
    }

    public static String getReportDuration() {
        return App.getContext().getSharedPreferences(MyPref, 0).getString(REPORT_DURATION, AppConstants.CURRENT_YEAR);
    }

    public static void setReportDuration(String duration) {
        SharedPreferences.Editor edit = App.getContext().getSharedPreferences(MyPref, 0).edit();
        edit.putString(REPORT_DURATION, duration);
        edit.commit();
    }

    public static String getCustomType() {
        return App.getContext().getSharedPreferences(MyPref, 0).getString(CUSTOM_TYPE, AppConstants.MONTH);
    }

    public static void setCustomType(String type) {
        SharedPreferences.Editor edit = App.getContext().getSharedPreferences(MyPref, 0).edit();
        edit.putString(CUSTOM_TYPE, type);
        edit.commit();
    }

    public static String getSelectedCarId() {
        return App.getContext().getSharedPreferences(MyPref, 0).getString(SELECTED_CAR, AppConstants.DEFAULT_CAR_ID);
    }

    public static void setSelectedCarId(String car) {
        SharedPreferences.Editor edit = App.getContext().getSharedPreferences(MyPref, 0).edit();
        edit.putString(SELECTED_CAR, car);
        edit.commit();
    }

    public static boolean IsShowNotification() {
        return App.getContext().getSharedPreferences(MyPref, 0).getBoolean(IS_SHOW_NOTIFICATION, true);
    }

    public static void setIsShowNotification(boolean isShow) {
        SharedPreferences.Editor edit = App.getContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(IS_SHOW_NOTIFICATION, isShow);
        edit.commit();
    }

    public static boolean IsProVersion() {

        return true;
    }

    public static void setIsProVersion(Context context, boolean isProversion) {
        SharedPreferences.Editor edit = App.getContext().getSharedPreferences(MyPref, 0).edit();
        edit.putBoolean(PRO_VERSION, isProversion);
        edit.commit();
    }
}

package com.demo.carservicetracker2.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.demo.carservicetracker2.R;
import com.demo.carservicetracker2.model.FuelNameModel;
import com.demo.carservicetracker2.model.ServiceDocumentModel;
import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import net.lingala.zip4j.util.InternalZipConstants;


public class AppConstants {
    public static final String APP_DB_NAME = "CarTracker.db";
    public static final int APP_DB_VERSION = 1;
    public static final String APP_EMAIL_SUBJECT = "Your Suggestion - Vehicle Maintenance Tracker";
    public static final int CLICK_TYPE_BTN = 2;
    public static final int CLICK_TYPE_ROW = 1;
    public static final String CURRENT_MONTHS = "CurrentMonth";
    public static final String CURRENT_YEAR = "CurrentYear";
    public static final String CUSTOM = "Custom";
    public static final String DB_BACKUP_DIRECTORY = "CarBackup";
    public static final String DB_BACKUP_FILE_NAME_PRE = "Backup";
    public static final String DEFAULT_CAR_ID = "1";
    public static final String DEFAULT_NOTIFICATION_DETAIL_TEXT = "Manage your income expense entries, or day to day transaction";
    public static final int DOCUMENT = 2;
    public static final String GALLON = "Gallon";
    public static final String KILOMETER = "Kilometer";
    public static final String LAST_MONTHS = "LastMonth";
    public static final String LAST_SIX_MONTHS = "LastSixMonths";
    public static final String LAST_THREE_MONTHS = "LastThreeMonths";
    public static final String LAST_YEAR = "LastYear";
    public static final String LITER = "Liter";
    public static final String MILE = "Mile";
    public static final String MONTH = "Month";
    public static final String NOTIFICATION_OBJECT_ID = "NOTIFICATION_OBJECT_ID";
    public static final int REQUEST_CAMERA_PERMISSION = 1;
    public static final int REQUEST_CODE_ALARM_ID = 11104;
    public static final String REQUEST_CODE_ALARM_NAME = "REQUEST_CODE_ALARM_NAME";
    public static final String REQUEST_CODE_ALARM_NAME_ID = "REQUEST_CODE_ALARM_NAME_ID";
    public static final int REQUEST_CODE_REMIND_24 = 1124;
    public static final int REQUEST_CODE_REMIND_3 = 1103;
    public static final int REQUEST_CODE_SET_ALARM = 1111;

    public static String TERMS_OF_SERVICE_URL = "https://google.com";
    public static String DISCLOSURE_DIALOG_DESC = "We would like to inform you regarding the 'Consent to Collection and Use Of Data'\n\nAllow to access camera to capture photo.\n\nWe store your data on your device only, we don’t store them on our server.";


    public static int REQUEST_CODE_SIGN_IN = 1005;
    public static String ID_TOKAN = "ID_TOKAN";
    public static final String PRIVATE_SHARED_PREFERENCE = "PRIVATE_SHARED_PREFERENCE";

    public static final int SERVICE = 1;
    public static final String YEAR = "Year";
    public static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    public static SimpleDateFormat MonthFormat = new SimpleDateFormat("MMM, yyyy");
    public static SimpleDateFormat YearFormat = new SimpleDateFormat("yyyy");
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT_DATE_TIME = new SimpleDateFormat("dd, MMMM, yyyy,EEE - HH:mm:ss.SSS a");
    public static String PRIVACY_POLICY_URL = "https://google.com";

    public static String GetMonthName(int month) {
        return month == 0 ? "Jan" : month == 1 ? "Feb" : month == 2 ? "Mar" : month == 3 ? "Apr" : month == 4 ? "May" : month == 5 ? "Jun" : month == 6 ? "Jul" : month == 7 ? "Aug" : month == 8 ? "Sep" : month == 9 ? "Oct" : month == 10 ? "Nov" : "Dec";
    }

    public static String GetProgressColor(int progress) {
        return (progress < 0 || progress > 50) ? (progress < 51 || progress > 75) ? (progress < 76 || progress > 100) ? "#0c3469" : "#ff657e" : "#ffc600" : "#37e882";
    }

    public static String getUniqueId() {
        return UUID.randomUUID().toString();
    }

    public static long GetOnlyDateInMillis(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTimeInMillis();
    }

    public static long GetOnlyDayBeforeDateInMillis(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.add(5, 1);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTimeInMillis();
    }

    public static long GetNextYearDateInMillis(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        calendar.add(1, 1);
        return calendar.getTimeInMillis();
    }

    public static String getRootPath(Context context) {
        return context.getDatabasePath(APP_DB_NAME).getParent();
    }

    public static String getMediaDir(Context context) {
        File file = new File(getRootPath(context), context.getResources().getString(R.string.app_name));
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getPath();
    }

    public static String getTempDirectory(Context context) {
        File file = new File(context.getFilesDir(), "temp");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }

    public static void deleteTempFile(Context context) {
        try {
            deleteFolder(new File(getTempDirectory(context)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteFolder(File file) {
        File[] listFiles;
        for (File file2 : file.listFiles()) {
            if (file2.isDirectory()) {
                deleteFolder(file2);
            } else {
                file2.delete();
            }
        }
        file.delete();
    }

    public static String getLocalFileDir() {
        File file = new File(Environment.getExternalStorageDirectory() + InternalZipConstants.ZIP_FILE_SEPARATOR + DB_BACKUP_DIRECTORY);
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getAbsolutePath();
    }

    public static String getLocalZipFilePath() {
        return getLocalFileDir() + File.separator + getBackupName();
    }

    public static String getBackupName() {
        return "Backup_" + getFileNameCurrentDateTime() + ".zip";
    }

    public static String getFileNameCurrentDateTime() {
        return new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(new Date(Calendar.getInstance().getTimeInMillis()));
    }

    public static String getDatabasePath(Context context) {
        return new File(context.getDatabasePath(APP_DB_NAME).getParent()).getAbsolutePath();
    }

    public static String getRemoteZipFilePath(Context context) {
        return getTempDirectory(context) + File.separator + getBackupName();
    }

    public static void deleteImage(Context context, String filename) {
        File file = new File(getRootPath(context), filename);
        if (file.exists()) {
            file.delete();
        }
    }

    public static long GetDaysDiff(long startDate, long endDate) {
        return ((((GetOnlyDateInMillis(endDate) - GetOnlyDateInMillis(startDate)) / 1000) / 60) / 60) / 24;
    }

    public static String getNumberFormat(Double amount) {
        return new DecimalFormat("##,##,##,##,##,##,##0.0").format(amount);
    }

    public static String GetDistanceUnit() {
        return AppPref.getDistanceUnit().equals(MILE) ? " mi" : " km";
    }

    public static String GetFullDistanceUnit() {
        return AppPref.getDistanceUnit().equals(MILE) ? MILE : KILOMETER;
    }

    public static String GetFullVolumeUnit() {
        return AppPref.getVolumeUnit().equals(LITER) ? LITER : GALLON;
    }

    public static String GetVolumeUnit() {
        return AppPref.getVolumeUnit().equals(LITER) ? "L" : "gal";
    }

    public static String GetCurrency() {
        return AppPref.getCurrency();
    }

    public static Date GetDate(long date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(GetOnlyDateInMillis(date));
        return calendar.getTime();
    }

    public static int GetDrawable(String IconName) {
        return IconName.equalsIgnoreCase("TireChange") ? R.drawable.tire_change : IconName.equalsIgnoreCase("OilWithFilter") ? R.drawable.oli_with_filter : IconName.equalsIgnoreCase("BrakePads") ? R.drawable.brake_pads : IconName.equalsIgnoreCase("BrakeFluid") ? R.drawable.brake_fluid : IconName.equalsIgnoreCase("SparkPlugs") ? R.drawable.spark_plugs : IconName.equalsIgnoreCase("Wipers") ? R.drawable.wiper : IconName.equalsIgnoreCase("PeriodicalMaintenance") ? R.drawable.periodical_maintenance : IconName.equalsIgnoreCase("EngineAirFilter") ? R.drawable.engine_air_filter : IconName.equalsIgnoreCase("AirConditioningFilter") ? R.drawable.air_conditioning_filter : IconName.equalsIgnoreCase("BrakeDiscsAndPads") ? R.drawable.brake_discs_and_pads : IconName.equalsIgnoreCase("OtherService") ? R.drawable.other_service : IconName.equalsIgnoreCase("Engine") ? R.drawable.engine : IconName.equalsIgnoreCase("Wheels") ? R.drawable.wheels : IconName.equalsIgnoreCase("Suspension") ? R.drawable.suspension : IconName.equalsIgnoreCase("GearBox") ? R.drawable.gearbox : IconName.equalsIgnoreCase("Fluid") ? R.drawable.fluid : IconName.equalsIgnoreCase("CarWash") ? R.drawable.car_wash : IconName.equalsIgnoreCase("TirePepair") ? R.drawable.tire_pepair : IconName.equalsIgnoreCase("Battery") ? R.drawable.battery : IconName.equalsIgnoreCase("BodyRepair") ? R.drawable.body_repair : IconName.equalsIgnoreCase("CarTowing") ? R.drawable.car_towing : IconName.equalsIgnoreCase("MileageActualization") ? R.drawable.mileage_actualization : IconName.equalsIgnoreCase("Fine") ? R.drawable.fine : IconName.equalsIgnoreCase("Insurance") ? R.drawable.insurance : IconName.equalsIgnoreCase("CarCheckList") ? R.drawable.car_check_list : IconName.equalsIgnoreCase("Tax") ? R.drawable.tax : IconName.equalsIgnoreCase("ComplexInsurance") ? R.drawable.complex_insurance : IconName.equalsIgnoreCase("CarRegister") ? R.drawable.car_register : IconName.equalsIgnoreCase("OtherDocument") ? R.drawable.other_document : R.drawable.other_service;
    }

    public static List<ServiceDocumentModel> GetAllStaticService() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ServiceDocumentModel("Service_2", "Tire change", "TireChange", 1, true));
        arrayList.add(new ServiceDocumentModel("Service_3", "Oil with filter", "OilWithFilter", 1, true));
        arrayList.add(new ServiceDocumentModel("Service_6", "Brake pads", "BrakePads", 1, true));
        arrayList.add(new ServiceDocumentModel("Service_8", "Brake fluid", "BrakeFluid", 1, true));
        arrayList.add(new ServiceDocumentModel("Service_9", "Spark plugs", "SparkPlugs", 1, true));
        arrayList.add(new ServiceDocumentModel("Service_10", "Wipers", "Wipers", 1, true));
        arrayList.add(new ServiceDocumentModel("Service_1", "Periodical maintenance", "PeriodicalMaintenance", 1, true));
        arrayList.add(new ServiceDocumentModel("Service_4", "Engine air filter", "EngineAirFilter", 1, true));
        arrayList.add(new ServiceDocumentModel("Service_5", "Air conditioning filter", "AirConditioningFilter", 1, true));
        arrayList.add(new ServiceDocumentModel("Service_7", "Brake discs and pads", "BrakeDiscsAndPads", 1, true));
        arrayList.add(new ServiceDocumentModel("Service_12", "Engine", "Engine", 1, true));
        arrayList.add(new ServiceDocumentModel("Service_11", "Wheels", "Wheels", 1, true));
        arrayList.add(new ServiceDocumentModel("Service_13", "Suspension", "Suspension", 1, true));
        arrayList.add(new ServiceDocumentModel("Service_14", "Gearbox", "GearBox", 1, true));
        arrayList.add(new ServiceDocumentModel("Service_15", "Fluid", "Fluid", 1, true));
        arrayList.add(new ServiceDocumentModel("Service_16", "Car wash", "CarWash", 1, false));
        arrayList.add(new ServiceDocumentModel("Service_17", "Tire repair", "TirePepair", 1, false));
        arrayList.add(new ServiceDocumentModel("Service_18", "Battery", "Battery", 1, true));
        arrayList.add(new ServiceDocumentModel("Service_19", "Body repair", "BodyRepair", 1, true));
        arrayList.add(new ServiceDocumentModel("Service_20", "Car towing", "CarTowing", 1, false));
        arrayList.add(new ServiceDocumentModel("Service_21", "Mileage actualization", "MileageActualization", 1, false));
        arrayList.add(new ServiceDocumentModel("Service_22", "Other Service", "OtherService", 1, true));
        return arrayList;
    }

    public static List<ServiceDocumentModel> GetAllStaticDocument() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ServiceDocumentModel("Document_1", "Fine", "Fine", 2, false));
        arrayList.add(new ServiceDocumentModel("Document_2", "Insurance", "Insurance", 2, true));
        arrayList.add(new ServiceDocumentModel("Document_3", "Car check list", "CarCheckList", 2, true));
        arrayList.add(new ServiceDocumentModel("Document_4", "Tax", "Tax", 2, true));
        arrayList.add(new ServiceDocumentModel("Document_5", "Complex insurance", "ComplexInsurance", 2, true));
        arrayList.add(new ServiceDocumentModel("Document_6", "Car register", "CarRegister", 2, false));
        arrayList.add(new ServiceDocumentModel("Document_7", "Other document", "OtherDocument", 2, true));
        return arrayList;
    }

    public static List<FuelNameModel> GetAllFuelName() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new FuelNameModel("Regular"));
        arrayList.add(new FuelNameModel("Plus"));
        arrayList.add(new FuelNameModel("Premium"));
        arrayList.add(new FuelNameModel("Super"));
        arrayList.add(new FuelNameModel("Diesel"));
        arrayList.add(new FuelNameModel("Natural Gas"));
        return arrayList;
    }

    public static void shareApp(Context context) {
        try {
            Intent intent = new Intent("android.intent.action.SEND");
            intent.setType("text/plain");
            intent.putExtra("android.intent.extra.TEXT", "Vehicle Maintenance Tracker\n- Car tracker, who record, track and set reminders for their services, fuel logs\n- Track vehicle services like Tire change, Oil with filter, Brake pads, Wipers, Engine air filter, Suspension, gearbox and air conditioning filter etc\n- Can add multiple vehicles and services no limits\n- Visualize car report data by simply applying filters by years and specific months\n\nhttps://play.google.com/store/apps/details?id=" + context.getPackageName());
            context.startActivity(Intent.createChooser(intent, "Share via"));
        } catch (Exception e) {
            Log.d("TAG", "shareApp: " + e.toString());
        }
    }
    public static void showRattingDialog(final Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            context.startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }


    public static void openUrl(Context context, String url) {
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
        intent.addFlags(1208483840);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(context, "No suitable app found", Toast.LENGTH_SHORT).show();
        }
    }
}

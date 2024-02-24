package com.demo.carservicetracker2.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.demo.carservicetracker2.database.dao.CarDao;
import com.demo.carservicetracker2.database.dao.DbVersionDAO;
import com.demo.carservicetracker2.database.dao.DocumentDao;
import com.demo.carservicetracker2.database.dao.FuelDao;
import com.demo.carservicetracker2.database.dao.ReminderDao;
import com.demo.carservicetracker2.database.dao.ServiceDao;
import com.demo.carservicetracker2.database.models.CarModel;
import com.demo.carservicetracker2.database.models.DbVersionModel;
import com.demo.carservicetracker2.database.models.DocumentModel;
import com.demo.carservicetracker2.database.models.FuelModel;
import com.demo.carservicetracker2.database.models.ImageModel;
import com.demo.carservicetracker2.database.models.ReminderModel;
import com.demo.carservicetracker2.database.models.ServiceModel;
import com.demo.carservicetracker2.database.dao.ImageDao;
import com.demo.carservicetracker2.utils.AppConstants;
import com.demo.carservicetracker2.utils.AppPref;

@Database(entities = {CarModel.class, DbVersionModel.class, DocumentModel.class, FuelModel.class, ImageModel.class, ReminderModel.class, ServiceModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase appDatabase;
    public abstract CarDao carDao();

    public abstract DbVersionDAO dbVersionDAO();

    public abstract DocumentDao documentDao();

    public abstract FuelDao fuelDao();

    public abstract ImageDao imageDao();

    public abstract ReminderDao reminderDao();

    public abstract ServiceDao serviceDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (appDatabase == null) {
            appDatabase = buildDatabaseInstance(context);
            if (!AppPref.isDbVersionAdded()) {
                addDbVersion();
                AppPref.setIsDbversionAdded(true);
            }
        }
        return appDatabase;
    }

    private static void addDbVersion() {
        appDatabase.dbVersionDAO().deleteAll();
        appDatabase.dbVersionDAO().insert(new DbVersionModel(1));
    }

    private static AppDatabase buildDatabaseInstance(Context context) {
        return (AppDatabase) Room.databaseBuilder(context, AppDatabase.class, AppConstants.APP_DB_NAME).allowMainThreadQueries().build();
    }
}

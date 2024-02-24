package com.demo.carservicetracker2.database.dao;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.demo.carservicetracker2.database.models.ReminderModel;
import com.demo.carservicetracker2.model.NotificationModel;

import java.util.List;
@Dao
public interface ReminderDao {
    @Query("DELETE FROM REMINDERMODEL WHERE carId = :carId")
    void DeleteAllReminderByCarId(String carId);

    @Delete
    void DeleteReminder(ReminderModel reminderModel);

    @Query("SELECT * FROM ReminderModel WHERE carId = :carId")
    List<ReminderModel> GetAllReminderByCarId(String carId);


    @Query("SELECT * FROM ReminderModel WHERE ReminderId = :ReminderId")
    ReminderModel GetReminderByDate(String ReminderId);

    @Insert
    void InsertReminder(ReminderModel reminderModel);

    @Update
    void UpdateReminder(ReminderModel reminderModel);

    @Query("Select date alertDateTime,reminderId notificationId From ReminderModel where date(Date/1000,'unixepoch','localtime') = date(:dayBeforeDate/1000,'unixepoch','localtime')")
    List<NotificationModel> getTodaysNotifications(long dayBeforeDate);
}

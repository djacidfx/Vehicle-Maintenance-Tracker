package com.demo.carservicetracker2.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.demo.carservicetracker2.database.models.ServiceModel;

import java.util.List;

@Dao
public interface ServiceDao {

    @Query("DELETE FROM ServiceModel WHERE carId = :carId")
    void DeleteAllServiceByCarId(String carId);

    @Delete
    void DeleteService(ServiceModel serviceModel);

    @Query("Select * From ServiceModel Where CarId=:carId ORDER BY Date DESC")
    List<ServiceModel> GetAllServiceByCarId(String carId);

    @Insert
    void InsertService(ServiceModel serviceModel);

    @Update
    void UpdateService(ServiceModel serviceModel);
}

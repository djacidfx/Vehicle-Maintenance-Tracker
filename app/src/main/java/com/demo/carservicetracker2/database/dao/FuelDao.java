package com.demo.carservicetracker2.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.demo.carservicetracker2.database.models.FuelModel;

import java.util.List;

@Dao
public interface FuelDao {

    @Query("DELETE FROM FuelModel WHERE fuelId = :fuelId")
    void DeleteAllFuelByCarId(String fuelId);


    @Delete
    void DeleteFuel(FuelModel fuelModel);


    @Query("SELECT * FROM FuelModel WHERE carId = :carId")
    List<FuelModel> GetAllFuelByCarId(String carId);


    @Insert
    void InsertFuel(FuelModel fuelModel);

    @Update
    void UpdateFuel(FuelModel fuelModel);
}

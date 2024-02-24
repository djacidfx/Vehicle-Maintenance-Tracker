package com.demo.carservicetracker2.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.demo.carservicetracker2.model.ReportDataModel;
import com.demo.carservicetracker2.database.models.CarModel;

import java.util.List;


@Dao
public interface CarDao {
    @Delete
    void DeleteCar(CarModel carModel);
    @Query("SELECT * FROM CarModel")
    List<CarModel> GetAllCar();

    @Query("SELECT * FROM (\nselect sum(ifnull(s.PartsCosts+s.LabourCosts,0)) as TotalServiceCost from ServiceModel " +
            "as s\nwhere CarId=:CarId and  s.Date  BETWEEN :startDate  AND :endDate)\nLEFT JOIN (\nselect sum(ifnull(d.TotalCoast,0)) " +
            "as TotalDocumentCost from DocumentModel as d\nwhere CarId=:CarId and  d.Date  BETWEEN :startDate  AND :endDate)\nLEFT JOIN (\nselect sum(ifnull(f.TotalCost,0)) " +
            "as TotalFuelCost from FuelModel as f\nwhere CarId=:CarId  and  f.Date  BETWEEN :startDate  AND :endDate\n)")
    ReportDataModel GetCarDataBetweenTwoDates(long startDate, long endDate, String CarId);

    @Insert
    void InsertCar(CarModel carModel);

    @Update
    void UpdateCar(CarModel carModel);
}

package com.demo.carservicetracker2.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.demo.carservicetracker2.database.models.ImageModel;

import java.util.List;

@Dao
public interface ImageDao {
    @Delete
    void DeleteImage(ImageModel imageModel);

    @Query("SELECT * FROM ImageModel WHERE referId = :referId")
    List<ImageModel> GetAllImageOfCar(String referId);

    @Insert
    void InsertImage(ImageModel imageModel);
}

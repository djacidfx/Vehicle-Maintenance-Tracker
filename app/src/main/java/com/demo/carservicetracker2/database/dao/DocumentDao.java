package com.demo.carservicetracker2.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.demo.carservicetracker2.database.models.DocumentModel;

import java.util.List;

@Dao
public interface DocumentDao {
    @Query("DELETE FROM DocumentModel WHERE CarId = :carId")
    void DeleteAllDocumentByCarId(String carId);

    @Delete
    void DeleteDocument(DocumentModel documentModel);

    @Query("SELECT * FROM DocumentModel WHERE carId = :carId")
    List<DocumentModel> GetAllDocumentByCarId(String carId);

    @Insert
    void InsertDocument(DocumentModel documentModel);

    @Update
    void UpdateDocument(DocumentModel documentModel);
}
